package com.abc.trainmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.abc.trainmate.Constants.constants.LANDING;
import static com.abc.trainmate.Constants.constants.REGISTERED;

public class Splash extends AppCompatActivity {
    SharedPreferences myprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.abc.trainmate",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(),
                        Base64.DEFAULT));
//                Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                myprefs= PreferenceManager.getDefaultSharedPreferences(Splash.this);
                if(!myprefs.getBoolean(REGISTERED,false))

                {
                    startActivity(new Intent(Splash.this, CreateProfile.class));
                }
                else if(myprefs.getBoolean(LANDING,false))

                {
                    startActivity(new Intent(Splash.this, Landing.class));
                }
                else

                {
                    startActivity(new Intent(Splash.this, Credentials.class));
                }

                finish();

            }
        }.start();
    }

}
