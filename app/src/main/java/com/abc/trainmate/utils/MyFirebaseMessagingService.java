package com.abc.trainmate.utils;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.abc.trainmate.Splash;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

//    DbMethods dbMethods;
    int db=0;
    String title = "TrainMate";
    String body = "";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        //Calling method to generate notification
//        sendNotification(remoteMessage.getNotification().getBody());
//        dbMethods = new DbMethods(this);


        Map data = remoteMessage.getData();
        Log.d(TAG, data.toString());

        try {
            body = (String) data.get("body");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("NOTIF BODY CATCH", e.toString());
        }



        String TAG="NOTIF";
        Random r=new Random();
        long id= r.nextInt();
        Log.d("db val",""+db);

        Log.d(TAG,"without image");
           sendNotification(id, body, title);
//        id = dbMethods.insertUpdates(body, Calendar.getInstance().getTimeInMillis(), title);
    }


    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(long id, String messageBody, String title) {
        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        long[] pattern = {500,500,500,500,500};


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.logo_white)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int)id, notificationBuilder.build());
    }

}