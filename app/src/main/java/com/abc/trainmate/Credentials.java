package com.abc.trainmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.abc.trainmate.Constants.constants.BIO;
import static com.abc.trainmate.Constants.constants.CALL_CONTACT;
import static com.abc.trainmate.Constants.constants.EMAIL;
import static com.abc.trainmate.Constants.constants.FCM_TOKEN;
import static com.abc.trainmate.Constants.constants.GENDER;
import static com.abc.trainmate.Constants.constants.ID;
import static com.abc.trainmate.Constants.constants.LANDING;
import static com.abc.trainmate.Constants.constants.NAME;
import static com.abc.trainmate.Constants.constants.PROFILE_PIC;
import static com.abc.trainmate.Constants.constants.URL_REGISTER;
import static com.abc.trainmate.Constants.constants.VISIBLE_FB;
import static com.abc.trainmate.Constants.constants.VISIBLE_WHATSAPP;
import static com.abc.trainmate.Constants.constants.WHATSAPP_CONTACT;

public class Credentials extends AppCompatActivity {
    SharedPreferences myprefs;
    TextInputEditText name,email,gender,bio;
    Spinner fbSpinner, whatsappSpinner,callSpinner;
    LinearLayout llfb,llwhatsapp,llcall;
    TextView fbStatus, whatsappStatus, callStatus;
    CircleImageView circleImageView;
    CardView proceed;
    ProgressBar progressBar;
    ArrayAdapter<CharSequence> adapter;
    String privacyWhatsapp="Hidden",privacyCall="Hidden",privacyFB="Hidden";
    public static int WHATSAPP=1;
    public static int CALL=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        initialize();
        setIntialData();
        setListenersSocial();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           next();
            }
        });
        spinnerListeners();

    }
    public void next(){
        if(whatsappStatus.getText().toString().equals("Click to add")){
            whatsappStatus.setError("Enter call details");
        }
        else if(bio.getText().toString().isEmpty()){
            bio.setError("Enter Bio to proceed");
        }
        else{
            serverPush();

        }
    }
    public void spinnerListeners(){
        callSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    privacyCall="Hidden";
                }
                else{
                    privacyCall="Visible";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fbSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    privacyFB="Hidden";
                }
                else{
                    privacyFB="Visible";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        whatsappSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    privacyWhatsapp="Hidden";
                }
                else{
                    privacyWhatsapp="Visible";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setListenersSocial(){
        llfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Credentials.this, "Facebook already connected!", Toast.LENGTH_SHORT).show();
            }
        });
        llwhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myprefs.getString(WHATSAPP_CONTACT,WHATSAPP_CONTACT).equals(WHATSAPP_CONTACT)) {
                    Intent i = new Intent(Credentials.this, LoginActivity.class);
                    startActivityForResult(i, WHATSAPP);
                }
                else {
                    Toast.makeText(Credentials.this, "Whatsapp Contact Already Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        llcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myprefs.getString(CALL_CONTACT,CALL_CONTACT).equals(CALL_CONTACT)) {
                    Intent i = new Intent(Credentials.this, LoginActivity.class);
                    startActivityForResult(i, CALL);
                }
                else{
                    Toast.makeText(Credentials.this, "Call Contact ALready Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void setIntialData(){
        if(!myprefs.getString(WHATSAPP_CONTACT,WHATSAPP_CONTACT).equals(WHATSAPP_CONTACT)){
            whatsappStatus.setText(myprefs.getString(WHATSAPP_CONTACT,WHATSAPP_CONTACT));
        }
        name.setText(myprefs.getString(NAME,NAME));
        name.setKeyListener(null);
        gender.setText(myprefs.getString(GENDER,GENDER));
        gender.setKeyListener(null);
        email.setText(myprefs.getString(EMAIL,EMAIL));
        email.setKeyListener(null);
    }
    public void initialize(){
        progressBar=findViewById(R.id.progressBar);
        proceed=findViewById(R.id.card_view_button);
        myprefs= PreferenceManager.getDefaultSharedPreferences(this);
        circleImageView=findViewById(R.id.image);
        Glide.with(this).load(myprefs.getString(PROFILE_PIC,PROFILE_PIC)).diskCacheStrategy(DiskCacheStrategy.ALL).into(circleImageView);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        gender=findViewById(R.id.gender);
        bio=findViewById(R.id.bio);
        fbSpinner=findViewById(R.id.spinnerFB);
        whatsappSpinner=findViewById(R.id.spinnerWhatsapp);
        callSpinner=findViewById(R.id.spinnerCall);
        llfb=findViewById(R.id.llFacebook);
        llcall=findViewById(R.id.llCall);
        llwhatsapp=findViewById(R.id.llWhatsapp);
        fbStatus=findViewById(R.id.fb);
        whatsappStatus=findViewById(R.id.whatsapp);
        callStatus=findViewById(R.id.call);
        name.setSelected(false);
        email.setSelected(false);
        gender.setSelected(false);
        bio.setSelected(true);
        adapter= ArrayAdapter.createFromResource(this,
                R.array.privacy, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fbSpinner.setAdapter(adapter);
        whatsappSpinner.setAdapter(adapter);
        callSpinner.setAdapter(adapter);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==WHATSAPP)
        {
            if(resultCode==1){
                String s=data.getStringExtra("NUMBER");
                if(s.equals("")){
                    s=myprefs.getString(CALL_CONTACT,CALL_CONTACT);
                }

                whatsappStatus.setText(s);
                SharedPreferences.Editor editor=myprefs.edit();
                editor.putString(WHATSAPP_CONTACT,s);
                editor.commit();
            }
        }
        if(requestCode==CALL){
            if(resultCode==1){
                String s=data.getStringExtra("NUMBER");
                if(s.equals("")){
                    s=myprefs.getString(WHATSAPP_CONTACT,WHATSAPP_CONTACT);
                }
                callStatus.setText(s);
                SharedPreferences.Editor editor=myprefs.edit();
                editor.putString(CALL_CONTACT,s);
                editor.commit();
            }
        }
    }
    public void serverPush(){
        progressBar.setVisibility(View.VISIBLE);
        proceed.setOnClickListener(null);
        RequestQueue queue= Volley.newRequestQueue(getBaseContext());;
        String url= URL_REGISTER;
        JSONObject object=new JSONObject();
//        Map<String, String> params = new HashMap<String, String>();
        try {
            object.put("email", email.getText().toString().trim());
            object.put("name", name.getText().toString().trim());
            object.put("whatsappNumber",(whatsappStatus.getText().toString()));
            object.put("picture",myprefs.getString(PROFILE_PIC,PROFILE_PIC));
            object.put("FBid",myprefs.getString(ID,ID));
            object.put("bio",bio.getText().toString().trim());
            object.put("whatsappPrivacy",privacyWhatsapp);
            object.put("FBPrivacy",privacyFB);
            object.put("gender",gender.getText().toString());
            object.put("fcmtoken",myprefs.getString(FCM_TOKEN,FCM_TOKEN));
        } catch (JSONException e) {
            Log.d("CREDENTIAL ","JSON error");
            e.printStackTrace();
        }

        final String TAG="NETWORK RESPONSE LOGIN";
        Log.d(TAG,object.toString());
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                url,object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject resp) {
                Log.d(TAG, resp.toString());
                progressBar.setVisibility(View.GONE);
                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        next();
                    }
                });
                try {
                    Log.d(TAG, "Login parser executed!");

                    int status = resp.getInt("status");
                    //save and and to my server
                    if (status==1) {
                        SharedPreferences.Editor editor=myprefs.edit();
                        editor.putBoolean(LANDING,true);
                        editor.putString(BIO,bio.getText().toString());
                        editor.putString(VISIBLE_WHATSAPP,privacyWhatsapp);
                        editor.putString(VISIBLE_FB,privacyFB);
                        editor.commit();
                        startActivity(new Intent(Credentials.this,Landing.class));
                        finish();

                    } else  {
                        Toast.makeText(Credentials.this, "Error Occured! Try Again.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"can't sent");
                   }
                } catch (JSONException e) {
                  progressBar.setVisibility(View.GONE);
                    proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            next();
                        }
                    });
                    e.printStackTrace();
                    Log.d(TAG, "Login parser failed!");
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("Error: "+ error.getMessage());
                progressBar.setVisibility(View.GONE);
                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        next();
                    }
                });
//                Toast.makeText(Credentials.this, "Error1 Occured! Try Again.", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"can't sent");
//                cardView.setVisibility(View.VISIBLE);
//                progress.dismiss();
                Toast.makeText(Credentials.this,"Network Unreachable!",Toast.LENGTH_SHORT).show();
            }
        });
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(strReq);
    }
}
