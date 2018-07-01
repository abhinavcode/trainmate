package com.abc.trainmate.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.trainmate.R;
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

import static com.abc.trainmate.Constants.constants.BIO;
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

public class MyProfile extends Fragment {
    public MyProfile() {
        // Required empty public constructor
    }
    SharedPreferences myprefs;
    Spinner fbSpinner, whatsappSpinner,callSpinner;
    ArrayAdapter<CharSequence> adapter;
    CardView proceed;
    TextInputEditText bio;
    String privacyWhatsapp="Hidden",privacyCall="Hidden",privacyFB="Hidden";
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_my_profile, container, false);
        myprefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        bio=v.findViewById(R.id.bio);
        progressBar=v.findViewById(R.id.progressBar);
        bio.setText(myprefs.getString(BIO,BIO));
        TextView whatsapp;
        whatsapp=v.findViewById(R.id.whatsapp);

        fbSpinner=v.findViewById(R.id.spinnerFB);
        whatsappSpinner=v.findViewById(R.id.spinnerWhatsapp);
        callSpinner=v.findViewById(R.id.spinnerCall);
        whatsapp.setText(myprefs.getString(WHATSAPP_CONTACT,WHATSAPP_CONTACT));
        adapter= ArrayAdapter.createFromResource(getActivity(),
                R.array.privacy, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fbSpinner.setAdapter(adapter);
        whatsappSpinner.setAdapter(adapter);
        callSpinner.setAdapter(adapter);

        if(!myprefs.getString(VISIBLE_WHATSAPP,VISIBLE_WHATSAPP).equals("Hidden")){
            whatsappSpinner.setSelection(1);
            privacyWhatsapp="Visible";
        }
        if(!myprefs.getString(VISIBLE_FB,VISIBLE_FB).equals("Hidden")){
            fbSpinner.setSelection(1);
            privacyFB="Visible";
        }
        proceed=v.findViewById(R.id.card_view_button);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
        spinnerListeners();
        return v;
    }
    public void next(){

        if(bio.getText().toString().isEmpty()){
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
    public void serverPush(){
        progressBar.setVisibility(View.VISIBLE);
        proceed.setOnClickListener(null);
        RequestQueue queue= Volley.newRequestQueue(getActivity());;
        String url= URL_REGISTER;
        JSONObject object=new JSONObject();
//        Map<String, String> params = new HashMap<String, String>();
        try {
            object.put("email", myprefs.getString(EMAIL,EMAIL));
            object.put("name", myprefs.getString(NAME,NAME));
            object.put("whatsappNumber",(myprefs.getString(WHATSAPP_CONTACT,WHATSAPP_CONTACT)));
            object.put("picture",myprefs.getString(PROFILE_PIC,PROFILE_PIC));
            object.put("FBid",myprefs.getString(ID,ID));
            object.put("bio",bio.getText().toString().trim());
            object.put("whatsappPrivacy",privacyWhatsapp);
            object.put("FBPrivacy",privacyFB);
            object.put("gender",myprefs.getString(GENDER,GENDER));
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
//                        startActivity(new Intent(Credentials.this,Landing.class));
//                        finish();

                    } else  {
                        Toast.makeText(getActivity(), "Error Occured! Try Again.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),"Network Unreachable!",Toast.LENGTH_SHORT).show();
            }
        });
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(strReq);
    }
}
