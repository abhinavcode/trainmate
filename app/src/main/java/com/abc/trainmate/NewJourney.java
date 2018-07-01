package com.abc.trainmate;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.abc.trainmate.Constants.constants.ID;
import static com.abc.trainmate.Constants.constants.NO_JOURNEY_YET;
import static com.abc.trainmate.Constants.constants.START_DATE;
import static com.abc.trainmate.Constants.constants.TRAIN_NUMBER;
import static com.abc.trainmate.Constants.constants.URL_JOURNEY;

public class NewJourney extends AppCompatActivity {
    AppCompatAutoCompleteTextView train;
    Spinner seatPrivacy,coachPrivacy;
    EditText startDate,seatNumber,coachName;
    String privacySeat="Hidden",privacyCoach="Hidden";
    Button done;
    ProgressBar progressBar;
    final Calendar myCalendar = Calendar.getInstance();

    SharedPreferences myprefs;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journey);
        myprefs= PreferenceManager.getDefaultSharedPreferences(this);
        progressBar=findViewById(R.id.progressBar);
        seatNumber=findViewById(R.id.seatNumber);
        coachName=findViewById(R.id.coachName);
        done=findViewById(R.id.done);
        seatPrivacy=findViewById(R.id.spinnerSeat);
        coachPrivacy=findViewById(R.id.spinnerCoach);
        train=findViewById(R.id.trainName);
        startDate=findViewById(R.id.trainStart);

        final DatePickerDialog.OnDateSetListener date= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NewJourney.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        adapter= ArrayAdapter.createFromResource(this,
                R.array.privacy, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seatPrivacy.setAdapter(adapter);
        coachPrivacy.setAdapter(adapter);
        seatPrivacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    privacySeat="Hidden";
                }
                else{
                    privacySeat="Visible";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        coachPrivacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    privacyCoach="Hidden";
                }
                else{
                    privacyCoach="Visible";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i=new Intent();
//                i.putExtra("")
                if(startDate.getText().toString().isEmpty()){
                    startDate.setError("Enter start Date");
                }
                else if(train.getText().toString().isEmpty()){
                   train.setError("Enter Train Number");
                }
                else if(train.getText().toString().length()!=5){
                    train.setError("Enter correct Train Number");
                }
                else if(seatNumber.getText().toString().isEmpty()){
                    seatNumber.setError("Enter Seat Number");
                }
                else if(coachName.getText().toString().isEmpty()){
                    coachName.setError("Enter Coach");
                }
                else {
                    sendDetails();
                }
            }
        });
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        startDate.setText(sdf.format(myCalendar.getTime()));
    }
    @SuppressLint("LongLogTag")
    public void sendDetails(){
        progressBar.setVisibility(VISIBLE);

        RequestQueue queue= Volley.newRequestQueue(getBaseContext());;
        String url= URL_JOURNEY;
        JSONObject object=new JSONObject();

//facebook
//traincode
//startdate
//coachno
//seat
//cbool
//sbool
        try {
            object.put("facebook",myprefs.getString(ID,ID));
            object.put("traincode",train.getText().toString());
            object.put("startdate",startDate.getText().toString());
            object.put("coachno",coachName.getText().toString());
            object.put("seat",seatNumber.getText().toString());
            object.put("cbool",privacyCoach);
            object.put("sbool",privacySeat);
//fcmtoken
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.d("PARAMS LOGIN", params.toString());
        final String TAG="NETWORK RESPONSE NEW JOURNEY";
        Log.d(TAG+"",object.toString());
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                url,object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject resp) {
                Log.d(TAG, resp.toString());


                try {
                    Log.d(TAG, "Login parser executed!");

                    int status = resp.getInt("status");
                    //save and and to my server
                    if (status==1||status==2) {

                        SharedPreferences.Editor editor = myprefs.edit();
                        editor.putString(TRAIN_NUMBER, train.getText().toString());
                        editor.putString(START_DATE, startDate.getText().toString());
                        editor.putBoolean(NO_JOURNEY_YET,false);
                        editor.commit();
                        setResult(1);
                        finish();

//                        progress.dismiss();
//                        cardView.setVisibility(View.VISIBLE);

                    } else  {
                        Toast.makeText(NewJourney.this, "Error Occured! Try Again.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"can't sent");
                        progressBar.setVisibility(GONE);
//                        cardView.setVisibility(View.VISIBLE);
//                        progress.dismiss();
//                        cardView.setVisibility(View.VISIBLE);
//
//                        Toast.makeText(getActivity(), "Cannot Update", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
//                    cardView.setVisibility(View.VISIBLE);
//
//                    progress.dismiss();
                    e.printStackTrace();
                    progressBar.setVisibility(GONE);
                    Log.d(TAG, "Login parser failed!");
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("Error: "+ error.getMessage());
                    progressBar.setVisibility(GONE);
//                Toast.makeText(Credentials.this, "Error1 Occured! Try Again.", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"can't sent");
//                cardView.setVisibility(View.VISIBLE);
//                progress.dismiss();
                Toast.makeText(NewJourney.this,"Network Unreachable!",Toast.LENGTH_SHORT).show();
            }
        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                Log.d("HEADERS LOGIN", headers.toString());
//                return headers;
//            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(strReq);
    }
}