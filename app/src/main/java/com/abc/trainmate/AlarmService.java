package com.abc.trainmate;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmService extends AppCompatActivity {

    private Button btn_start, btn_stop,btn_notif;
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;
    static String filename="MyLocations";
    SharedPreferences locationdata;
    String loc_name="";
    Float loc_lat;
    Float loc_long;
    Location destlocal;
    Location curr_location;


    @Override
    protected void onResume() {
        super.onResume();
        locationdata=getSharedPreferences(filename,0);
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Double lat_cords = (Double) intent.getExtras().get("lat_cordinates");
                    Double long_cords = (Double) intent.getExtras().get("long_cordinates");
//                    textView.append("\n"+lat_cords+" "+long_cords);;
                    locationdata=getSharedPreferences(filename,0);



                       curr_location=new Location("");
                       destlocal=new Location("");
                        curr_location.setLatitude(lat_cords);
                        curr_location.setLongitude(long_cords);
                       // Set<String> locationset=locationdata.getStringSet("destination_data",null);

                        loc_name=locationdata.getString("destination_name","default string");
                        loc_lat=locationdata.getFloat("destination_latitude",0);
                        loc_long=locationdata.getFloat("destination_longitude",0);


                          destlocal.setLatitude(loc_lat);
                          destlocal.setLongitude(loc_long);

                    textView.setText("Distance Remaining: \n"+ destlocal.distanceTo(curr_location)/1000);
                        NotificationCompat.Builder notificationbuilder=new NotificationCompat.Builder(AlarmService.this).setSmallIcon(android.R.drawable.stat_notify_error)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                                .setContentTitle("Alarm Notification")
                                .setContentText("Distance remaining: "+curr_location.distanceTo(destlocal)/1000+"km");
                        notificationbuilder.setDefaults( Notification.DEFAULT_LIGHTS);
                        notificationbuilder.setOngoing(true);
                        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(AlarmService.this);
                        Integer alarmdistance=locationdata.getInt("alarm_distance",10);

                          if(destlocal.distanceTo(curr_location)<=alarmdistance*1000)
                          {//set alarm stop service close notification
                              Toast.makeText(context, "u reached", Toast.LENGTH_SHORT).show();
                              Intent i=new Intent(AlarmService.this,Alarm.class);
                              PendingIntent pi=PendingIntent.getBroadcast(getApplicationContext(),0,i,0);
                              AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);

                              am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000,pi);
                             // MediaPlayer player=MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
                              startActivity(new Intent(AlarmService.this, BigNotification.class));
                              finish();


                          }
                          else
                          {



                        //textView.append("\n" +intent.getExtras().get("coordinates"));

                        notificationManagerCompat.notify(1,notificationbuilder.build() );}


                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = (9 * getWindowManager().getDefaultDisplay().getHeight()) / 12;
        params.width = (9 * getWindowManager().getDefaultDisplay().getWidth()) / 12;
        this.getWindow().setAttributes(params);
        setContentView(R.layout.layout_alarmservice);
        locationdata=getSharedPreferences(filename,0);
       // MediaPlayer player=MediaPlayer.create(this,Settings.System.DEFAULT_RINGTONE_URI);

        btn_start = (Button) findViewById(R.id.button);
        btn_stop = (Button) findViewById(R.id.button2);
        btn_notif=(Button)findViewById(R.id.notif);
        textView = (TextView) findViewById(R.id.textView);

        if(!runtime_permissions())
            enable_buttons();

    }

    private void enable_buttons() {

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),LocationService.class);
                startService(i);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),LocationService.class);
                stopService(i);

            }
        });
        btn_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }
}