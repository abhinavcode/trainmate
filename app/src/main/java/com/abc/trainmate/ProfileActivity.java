package com.abc.trainmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.abc.trainmate.Constants.constants.START_DATE;
import static com.abc.trainmate.Constants.constants.TRAIN_NUMBER;
import static com.abc.trainmate.Constants.constants.URL_PERSON;

public class ProfileActivity extends AppCompatActivity {
    String fbid="";
    CircleImageView circleImageView;
    TextView trainname,startdate,coach,seat,gender,bio,whatsapp,fb,name,email;
    SharedPreferences myprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = (9 * getWindowManager().getDefaultDisplay().getHeight()) / 10;
        params.width = (9 * getWindowManager().getDefaultDisplay().getWidth()) / 10;
        this.getWindow().setAttributes(params);
        setContentView(R.layout.activity_profile);
        Intent i=getIntent();
        fbid=i.getStringExtra("fbid");
        circleImageView=findViewById(R.id.profile_image);
        trainname=findViewById(R.id.TravellingTrain);
        startdate=findViewById(R.id.startDate);
        name=findViewById(R.id.PersonName);
        coach=findViewById(R.id.coach);
        seat=findViewById(R.id.seat);
        gender=findViewById(R.id.sex);
        bio=findViewById(R.id.bio);
        whatsapp=findViewById(R.id.whatsapp);
        fb=findViewById(R.id.fb);
        email=findViewById(R.id.email);
        myprefs= PreferenceManager.getDefaultSharedPreferences(this);

        setData();
    }
    public void setData(){
        final String TAG="NETWORK PROFILE";
        JSONObject object=new JSONObject();
        try {
            object.put("fbid",fbid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG,object.toString());
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                URL_PERSON,object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG ,response.toString());
                try {
                    if(!response.getString("facebookbool").equals("Hidden")) {
                        fb.setText("https://www.facebook.com/app_scoped_user_id/" + response.getString("fbid"));
                    }
                    else{
                        fb.setText("ID hidden");
                    }
                    trainname.setText("Train Name: "+myprefs.getString(TRAIN_NUMBER,TRAIN_NUMBER));
                    startdate.setText("Start Date: "+myprefs.getString(START_DATE,START_DATE));
                    bio.setText(response.getString("bio"));
                    name.setText(response.getString("name"));
                    gender.setText(response.getString("sex"));
                    if(response.getString("cbool").equals("Hidden")){
                        coach.setText("Coach/Seat: Hidden/");
                    }
                    else {
                        coach.setText("Coach/Seat: " + response.getString("coachno") + "/");
                    }
                    if(response.getString("sbool").equals("Hidden")){
                        seat.setText("Hidden");
                    }
                    else{
                        seat.setText(response.getString("seat"));
                    }
                    email.setText(response.getString("email"));
                    if(response.getString("whatsappbool").equals("Hidden")){
                        whatsapp.setText("Contact Hidden");
                    }
                    else {
                        whatsapp.setText(response.getString("whatsapp"));
                    }
                    Glide.with(getBaseContext()).load(response.getString("pic")).into(circleImageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this,"No Internet connection",Toast.LENGTH_LONG).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
}
