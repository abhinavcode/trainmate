package com.abc.trainmate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

/**
 * Created by admin on 2/4/2018.
 */

public class Alarm extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Alarm set", Toast.LENGTH_SHORT).show();
        Vibrator v=(Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        v.vibrate(10000);






    }
}
