package com.abc.trainmate;

import android.Manifest;
import android.app.Notification;
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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by admin on 2/2/2018.
 */

public class Destinations extends AppCompatActivity implements View.OnClickListener {
     static String filename="MyLocations";
     SharedPreferences locationdata;
     TextView textview_source,textview_desintaion,distance;
     Button button_source,button_destination,button_startservice,button_stopservice,button_notif,serviceacti;
   // Iterator<String> iterator;
    BroadcastReceiver broadcastReceiver;
    Location sourcelocal,destilocal;
    EditText alarmdist;

    @Override
    protected void onResume() {
        super.onResume();
        locationdata=getSharedPreferences(filename,0);
        sourcelocal=new Location("");
        destilocal=new Location("");
        String destname="";
        String sourcename="";
        destname=locationdata.getString("destination_name","default");
        destilocal.setLatitude(locationdata.getFloat("destination_latitude",0));
        destilocal.setLongitude(locationdata.getFloat("destination_longitude",0));
       // Toast.makeText(this, "dest data received successsfully", Toast.LENGTH_SHORT).show();
        sourcename=locationdata.getString("source_name","default");
        sourcelocal.setLatitude(locationdata.getFloat("source_latitude",0));
        sourcelocal.setLongitude(locationdata.getFloat("source_longitude",0));









            textview_desintaion.setText(destname);
            textview_source.setText(sourcename);
            if(broadcastReceiver == null){
                final Location finalDest_location = destilocal;
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Double lat_cords= (Double) intent.getExtras().get("lat_cordinates");
                        Double long_cords= (Double) intent.getExtras().get("long_cordinates");
                        //location_update.setText(lat_cords+" "+long_cords);
                        distance.setText( Double.toString(sourcelocal.distanceTo(destilocal)/1000)+"km");

                        // textView.append("\n" +intent.getExtras().get("coordinates"));

                        Location  current_location=new Location("");
                        current_location.setLatitude(lat_cords);
                        current_location.setLongitude(long_cords);
                        //Toast.makeText(context, "location"+current_location.getLatitude()+" "+current_location.getLongitude(), Toast.LENGTH_SHORT).show();
                        //distance.setText((int) current_location.distanceTo(finalDest_location));


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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_destinations);

        locationdata=getSharedPreferences(filename,0);
        if(!runtime_permissions())
        setupvars();
        SharedPreferences.Editor editor = locationdata.edit();
        editor.putInt("alarm_distance",10);
        editor.commit();

    }
    private void setupvars()
    {
        textview_source=(TextView)findViewById(R.id.text_source);
        textview_desintaion=(TextView)findViewById(R.id.text_destination);
        button_destination=(Button)findViewById(R.id.button_destination);
        button_source=(Button)findViewById(R.id.button_current_location);
        //button_proceed=(Button)findViewById(R.id.button_proceed);
        button_startservice=(Button)findViewById(R.id.start_service);
        button_stopservice=(Button)findViewById(R.id.stop_service);
        button_notif=(Button)findViewById(R.id.button_notification);
        serviceacti=(Button)findViewById(R.id.serviceactivity);
        ///location_update=(TextView) findViewById(R.id.loc_update);
        distance=(TextView)findViewById(R.id.textview_distance);
        alarmdist=(EditText)findViewById(R.id.alarm_distance) ;
        button_destination.setOnClickListener(this);
        button_source.setOnClickListener(this);
        //button_proceed.setOnClickListener(this);
        button_startservice.setOnClickListener(this);
        button_stopservice.setOnClickListener(this);
        button_notif.setOnClickListener(this);
        serviceacti.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button_current_location:
                Bundle sourcebundle=new Bundle();
                Intent sourceintent=new Intent(this,MapsActivity2.class);
                sourcebundle.putString("location","source");
                sourceintent.putExtras(sourcebundle);
                startActivity(sourceintent);

//                Toast.makeText(this, " source shared prefs successfully saved", Toast.LENGTH_SHORT).show();
//                Set<String> sourceset=new HashSet<>();
//                sourceset=locationdata.getStringSet("source_data",null);
//
//                Toast.makeText(this, "source sharedpref data received successsfully", Toast.LENGTH_SHORT).show();
//                try{
//                    iterator=sourceset.iterator();
//                    textview_source.setText(iterator.next().toString());
//
//                }catch (NullPointerException e)
//                {
//                    e.printStackTrace();
//                }finally {
//
//                }
//                iterator=sourceset.iterator();
//                textview_source.setText(iterator.next().toString());


                break;
            case R.id.button_destination:

                Bundle destinationbundle=new Bundle();
                Intent destinationintent=new Intent(this,MapsActivity2.class);
                destinationbundle.putString("location","destination");
                destinationintent.putExtras(destinationbundle);
                startActivity(destinationintent);

//                Toast.makeText(this, "destinatoin shared prefs successfully saved", Toast.LENGTH_SHORT).show();
//                Set<String> destinationset=new HashSet<>();
//                destinationset=locationdata.getStringSet("destintion_data",null);
//                Toast.makeText(this, "dest data received successsfully", Toast.LENGTH_SHORT).show();




                break;

//            case R.id.button_proceed:
////
//                Toast.makeText(this, "u pressed the procees button", Toast.LENGTH_SHORT).show();
////
////
//                break;

            case R.id.start_service:
                startService(new Intent(this,LocationService.class));
                //Toast.makeText(this, "intent passed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.stop_service:
                stopService(new Intent(this,LocationService.class));
                break;

            case R.id.button_notification:
                NotificationCompat.Builder notificationbuilder=new NotificationCompat.Builder(this).setSmallIcon(android.R.drawable.stat_notify_error)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round))
                        .setContentTitle("Alarm notification")
                        .setContentText("Alarm rings in XX km");
                notificationbuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
                notificationbuilder.setOngoing(true);
                NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);
                notificationManagerCompat.notify(1,notificationbuilder.build() );



                break;


            case R.id.serviceactivity:
                startActivity(new Intent(this,AlarmService.class));
                break;



        }
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
                setupvars();
            }else {
                runtime_permissions();
            }
        }
    }
}
