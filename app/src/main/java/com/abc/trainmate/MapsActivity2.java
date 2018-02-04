package com.abc.trainmate;
/**
 * Created by admin on 2/2/2018.
 */

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private String TAG="";
    static String filename="MyLocations";
    SharedPreferences locationdata;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    public static final int REQUEST_CHECK_SETTINGS = 99;
    int PROXIMITY_RADIUS = 10000;
    String sharedress="";
    FusedLocationProviderClient mFusedLocationProviderClient;
    private static final LatLngBounds bounds=new LatLngBounds(new
            LatLng(-40,-168),new LatLng(71,136));
    private String arr[]=new String[3];

    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    Button button_select,button_search;

    double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(intent);


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        //setting up for autosuggestions
        button_search=(Button)findViewById(R.id.selectlocation);
        button_select=(Button)findViewById(R.id.B_search);
        button_select.setOnClickListener(this);
        button_search.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        placeAutocompleteAdapter=new PlaceAutocompleteAdapter(this,mGoogleApiClient,bounds,null);


            displayLocationSettingsRequest(this); //to ask for turning on location if turned off


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();

            }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            //Toast.makeText(this, "switch on the internet on ur phone", Toast.LENGTH_SHORT).show();
            //finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(mGoogleApiClient == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }



// to turn on the locatoin if turned off
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity2.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });}


        // funtion to ask user to turn on wifi or internet for accessing internet
//    private void displayinternetsettingsrequest(Context context)
//    {
//    if(ContextCompat.checkSelfPermission(this,Manifest.permission.))
//
//    }

//    private boolean haveNetworkConnection() {
//        boolean haveConnectedWifi = false;
//        boolean haveConnectedMobile = false;
//
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
//                if (ni.isConnected())
//                    haveConnectedWifi = true;
//            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//                if (ni.isConnected())
//                    haveConnectedMobile = true;
//        }
//        return haveConnectedWifi || haveConnectedMobile;
//    }


    protected synchronized void bulidGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(mGoogleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    public void onClick(View v)
    {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        String url;
        AutoCompleteTextView tf_location =  (AutoCompleteTextView)findViewById(R.id.TF_location);
        tf_location.setAdapter(placeAutocompleteAdapter);

        sharedress=tf_location.getText().toString();
        try {


            switch (v.getId()) {
                case R.id.B_search:
                    sharedress=tf_location.getText().toString();

                    String location = tf_location.getText().toString();
                    List<Address> addressList;


                    if (!location.equals("")) {
                        Geocoder geocoder = new Geocoder(this);

                        try {
                            addressList = geocoder.getFromLocationName(location, 5);

                            if (addressList != null) {
                                for (int i = 0; i < addressList.size(); i++) {
                                    LatLng latLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    markerOptions.title(location);
                                    arr[0]=location;
                                    arr[1]=Double.toString(latLng.latitude);
                                    arr[2]=Double.toString(latLng.longitude);
                                    mMap.addMarker(markerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
//            case R.id.B_hopistals:
//                mMap.clear();
//                String hospital = "hospital";
//                String url = getUrl(latitude, longitude, hospital);
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//
//                getNearbyPlacesData.execute(dataTransfer);
//                Toast.makeText(MapsActivity2.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
//                break;
                case R.id.selectlocation:

                    //code to save data of location in shared pref
                    String value = getIntent().getExtras().getString("location");
                    locationdata = getSharedPreferences(filename, 0);
                    SharedPreferences.Editor editor = locationdata.edit();
                    if (value.equals("source")) {

                       editor.putString("source_name",sharedress);
                        editor.putFloat("source_latitude",Float.parseFloat(arr[1]));
                        editor.putFloat("source_longitude",Float.parseFloat(arr[2]));
                        editor.commit();
                        Toast.makeText(this, "source data added in shared pred", Toast.LENGTH_LONG).show();
                        Toast.makeText(this, sharedress+"  "+arr[1]+"  "+arr[2], Toast.LENGTH_SHORT).show();
//
//                        Toast.makeText(this, "sourcelocation data added in shared pref", Toast.LENGTH_LONG).show();
//                        Toast.makeText(this, sharedress+"  "+arr[1]+"  "+arr[2], Toast.LENGTH_SHORT).show();


                    } else if (value.equals("destination")) {
//                        Set<String> destinationset = new HashSet<>();
//                        destinationset.add(sharedress);
//                        destinationset.add(arr[1]);
//                        destinationset.add(arr[2]);
//                        editor.putStringSet("destination_data", destinationset);
                        editor.putString("destination_name",sharedress);
                        editor.putFloat("destination_latitude",Float.parseFloat(arr[1]));
                        editor.putFloat("destination_longitude",Float.parseFloat(arr[2]));
                        editor.commit();
                        Toast.makeText(this, "destintaion data added in shared pred", Toast.LENGTH_LONG).show();
                        Toast.makeText(this, sharedress+"  "+arr[1]+"  "+arr[2], Toast.LENGTH_SHORT).show();
                    }
                    else if(value.equals("alarm")){

//                        Set<String> alarmset=new HashSet<>();
//                        alarmset.add(sharedress);
//                        alarmset.add(arr[1]);
//                        alarmset.add(arr[2]);
//                        editor.putStringSet("alarm_data",alarmset);
//                        Toast.makeText(this, "alarm data saved in prefs", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(this, sharedress+"  "+arr[1]+"  "+arr[2], Toast.LENGTH_SHORT).show();

                    }


                    break;



            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
//    private void getDeviceLocation(){
//        Log.d(TAG, "getDeviceLocation: getting the devices current location");
//
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        try{
//            if(mLocationPermissionsGranted){
//
//                final Task location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if(task.isSuccessful()){
//                            Log.d(TAG, "onComplete: found location!");
//                            Location currentLocation = (Location) task.getResult();
//
//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                                    DEFAULT_ZOOM,
//                                    "My Location");
//
//                        }else{
//                            Log.d(TAG, "onComplete: current location is null");
//                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }catch (SecurityException e){
//            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
//        }
//    }


    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyDIQhZZPikPzVdAZ4RobWtWGbpzUnVxVkk");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest,this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}