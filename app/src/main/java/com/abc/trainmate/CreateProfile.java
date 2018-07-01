package com.abc.trainmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.abc.trainmate.Constants.constants.GENDER;
import static com.abc.trainmate.Constants.constants.ID;
import static com.abc.trainmate.Constants.constants.NAME;
import static com.abc.trainmate.Constants.constants.PROFILE_PIC;
import static com.abc.trainmate.Constants.constants.REGISTERED;

public class CreateProfile extends AppCompatActivity {
    LoginButton loginButton;
    private static final String EMAIL = "email";
    CallbackManager callbackManager;
    SharedPreferences myprefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_create_profile);
        myprefs= PreferenceManager.getDefaultSharedPreferences(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                LoginManager.getInstance().logInWithReadPermissions(CreateProfile.this, Arrays.asList("public_profile"));
            }
        });
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("callback","yo");
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                    Log.d("FB",response.toString()+"\n"+object.toString());
                                    SharedPreferences.Editor editor=myprefs.edit();
                                try {
                                    editor.putBoolean(REGISTERED,true);
//                                    editor.putString(FB_ID,object.getString("link-id"))
                                    editor.putString(NAME,object.getString("name"));
                                    editor.putString(EMAIL,object.getString("email"));
                                    editor.putString(GENDER,object.getString("gender"));
                                    editor.putString(ID,object.getString("id"));
                                    editor.putString(PROFILE_PIC,object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    editor.commit();
                                    Intent intent=new Intent(CreateProfile.this,Credentials.class);
                                    startActivity(intent);
                                    finish();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                FirstNameSocial = object.optString("first_name");
//                                LastNameSocial = object.optString("last_name");
//                                GenderSocial = object.optString("gender");
//                                EmailSocial = object.optString("email", "");
//                                id = object.optString("id");
//
//
//                                if (!EmailSocial.equals("")) {
//                                    login_type = Config.Login_Type_facebook;
//                                    callAPI(EmailSocial, id, "");
//
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Permision Denied", Toast.LENGTH_LONG)
//                                            .show();
//                                }

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,birthday,gender,link,picture.width(400).height(400)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
