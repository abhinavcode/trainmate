package com.abc.trainmate.utils;

/**
 * Created by mayank on 3/2/17.
 */

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

import static com.abc.trainmate.Constants.constants.FCM_TOKEN;
import static com.abc.trainmate.Constants.constants.LANDING;

/**
 * Created by Belal on 5/27/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    private RequestQueue queue;

    SharedPreferences myprefs;


    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        myprefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = myprefs.edit();
        editor.putString(FCM_TOKEN, refreshedToken);
        editor.commit();


//        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
        queue = Volley.newRequestQueue(this);


        Map<String, String> params = new HashMap<String, String>();
//        params.put("email", myprefs.getString(EMAIL, ""));
        params.put("notificationToken", token);
        Log.d("PARAMS TOKEN", params.toString());
        if(myprefs.getBoolean(LANDING,false)){
//            params.put("fcmT",myprefs.getString(TECHNEX_ID, "technexId"));
        }
//        String url = TOKEN_URL;

//        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
//                url, new JSONObject(params), new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject resp) {
//                Log.d(TAG, resp.toString());
//
//
//                try {
//                    Log.d(TAG, "Token parser executed!");
//
//                    int status = resp.getInt("status");
//
//                    if (status == 1) {
//                        SharedPreferences.Editor editor = myprefs.edit();
//                        editor.putBoolean(IS_FCM_TOKEN_SENT, true);
//                        editor.commit();
//                    }
//
//                    Log.d(TAG, "Token parser executed properly!");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.d(TAG, "Token parser failed!");
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
////                Toast.makeText(getActivity(),"Network Unreachable!",Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                Log.d("HEADERS TOKEN", headers.toString());
//                return headers;
//            }
//        };
//        strReq.setRetryPolicy(new DefaultRetryPolicy(
//                30000,
//                10,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(strReq);

    }

}