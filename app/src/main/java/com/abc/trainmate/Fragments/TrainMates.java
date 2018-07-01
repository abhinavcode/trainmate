package com.abc.trainmate.Fragments;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.trainmate.JSONParse.JSONParseTrainMates;
import com.abc.trainmate.Models.DataTrainMates;
import com.abc.trainmate.NewJourney;
import com.abc.trainmate.R;
import com.abc.trainmate.adapter.AdapterTrainMates;
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
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.abc.trainmate.Constants.constants.ID;
import static com.abc.trainmate.Constants.constants.NO_JOURNEY_YET;
import static com.abc.trainmate.Constants.constants.START_DATE;
import static com.abc.trainmate.Constants.constants.TRAIN_NUMBER;
import static com.abc.trainmate.Constants.constants.URL_JOURNEY;
import static com.abc.trainmate.Constants.constants.URL_PROFILE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainMates extends Fragment {
    RecyclerView recyclerView;
    RelativeLayout rlnewJourney;
    private CardView cardViewNewJourney,cardViewNoJourney;
    public static int NEWJOURNEY=1;
    RecyclerView.Adapter mAdapter;
    List<DataTrainMates> mDataset;
    AppCompatAutoCompleteTextView train;
    Spinner seatPrivacy,coachPrivacy;
    EditText startDate,seatNumber,coachName;
    String privacySeat="Hidden",privacyCoach="Hidden";
    Button done;
    ProgressBar progressBar;
    final Calendar myCalendar = Calendar.getInstance();
    int rl=0;
    SharedPreferences myprefs;
    ArrayAdapter<CharSequence> adapter;
    public TrainMates() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_train_mates, container, false);
        myprefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        recyclerView=v.findViewById(R.id.recycler_view_trainmates);
        cardViewNewJourney=v.findViewById(R.id.card_view_new_journey);
        recyclerView=v.findViewById(R.id.recycler_view_trainmates);
        cardViewNoJourney=v.findViewById(R.id.card_view_no_journey);
        GridLayoutManager gridlayoutManager = new GridLayoutManager(getActivity().getBaseContext(),1);
        recyclerView.setLayoutManager(gridlayoutManager);
        rlnewJourney=v.findViewById(R.id.rlnewjourney);
        newjour(v);
        cardViewNewJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rl==1){
                    rlnewJourney.setVisibility(GONE);
                    rl=0;
                }
                else{
                    rlnewJourney.setVisibility(VISIBLE);

                    rl=1;
                }
            }
        });
        Boolean b=myprefs.getBoolean(NO_JOURNEY_YET,true);
//        setListener();
        if(!b){
            Log.d("VISIBILITY TRAIN","gone");
            cardViewNoJourney.setVisibility(View.GONE);
            setData();

            //pass
        }
        else{

            Log.d("VISIBILITY TRAIN","visible");

        }
        return v;
    }
    public void setListener(){
        cardViewNewJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(getActivity(), cardViewNewJourney, "newJourney");
                Intent intent=new Intent(getActivity(), NewJourney.class);
                Bundle bundle=new Bundle();
                bundle.putAll(options.toBundle());
                intent.putExtras(bundle);
                startActivityForResult(intent,NEWJOURNEY);
            }
        });
    }
    public void newjour(View v){
        progressBar=v.findViewById(R.id.progressBar);
        seatNumber=v.findViewById(R.id.seatNumber);
        coachName=v.findViewById(R.id.coachName);
        done=v.findViewById(R.id.done);
        seatPrivacy=v.findViewById(R.id.spinnerSeat);
        coachPrivacy=v.findViewById(R.id.spinnerCoach);
        train=v.findViewById(R.id.trainName);
        startDate=v.findViewById(R.id.trainStart);

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
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        adapter= ArrayAdapter.createFromResource(getActivity(),
                R.array.privacy, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seatPrivacy.setAdapter(adapter);
        coachPrivacy.setAdapter(adapter);
        seatPrivacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
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
                if(i==0){
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

    public void setData(){
        final String TAG="NETWORK TRAIN";
        RequestQueue queue= Volley.newRequestQueue(getContext());;

        JSONObject object=new JSONObject();
        try {
            object.put("fbid",myprefs.getString(ID,ID));
        } catch (JSONException e) {
            Log.d(TAG,"JSON error");
            e.printStackTrace();
        }
        Log.d(TAG,object.toString());
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                URL_PROFILE,object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG + " response",response.toString());

                        int status = 0;
                        try {
                            status = response.getInt(("status"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status == 1) {
                            JSONParseTrainMates pj = new JSONParseTrainMates();
                            try {
                                Log.d(TAG,"status 1");
                                pj.parseJSONmates(response.getJSONArray(("data")));
                                cardViewNoJourney.setVisibility(GONE);
                                mDataset = pj.getData();
                                Log.d("data",mDataset.toString());
                                mAdapter = new AdapterTrainMates(mDataset, getActivity());
                                recyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else if(status==2){
                            cardViewNoJourney.setVisibility(View.VISIBLE);
                            TextView t=getActivity().findViewById(R.id.noJourney);
                            t.setText("There is no user travelling in your train.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,error.toString()+error.getMessage());
                        Toast.makeText(getActivity(),"No Internet connection",Toast.LENGTH_LONG).show();

                    }
                });

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(strReq);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==NEWJOURNEY)
        {
            if(resultCode==1){
                if(mDataset!=null) {
                    mDataset.clear();
                    mAdapter.notifyDataSetChanged();
                }
                setData();
            }
        }
    }
    @SuppressLint("LongLogTag")
    public void sendDetails(){
        cardViewNoJourney.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);

        RequestQueue queue= Volley.newRequestQueue(getActivity());;
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
                        setData();
                        progressBar.setVisibility(GONE);

//                        progress.dismiss();
//                        cardView.setVisibility(View.VISIBLE);

                    } else  {
                        Toast.makeText(getActivity(), "Error Occured! Try Again.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),"Network Unreachable!",Toast.LENGTH_SHORT).show();
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
