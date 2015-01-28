package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Double oldScreenLatitude=null;
    private Double oldScreenLongitude=null;
    private LocationManager locMan;
    public static GoogleMap myMap;
    public static Location myLocation=null;
    private LocationRequest mLocationRequest;
    public static final String PREFERENCES = "com.mjhutti.designateddrinker.updatewindow";
    SharedPreferences prefs = null;
    private float AGE_THRESHOLD;

    public GoogleApiClient mGoogleApiClient;

    protected synchronized void buildGoogleApiClient() {
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getApplicationContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        AGE_THRESHOLD = prefs.getInt("update_window", 100);


        //Temporary fix according to http://stackoverflow.com/questions/19266553/android-caused-by-android-os-networkonmainthreadexception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Get Map and Location Manager
        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locMan.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ))
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enable GPS", Toast.LENGTH_LONG);
            toast.show();
            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
       // myMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        zoomToMyLocation();

        try {
            loadMarkers();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


//what if nothing from DB? does that even make sense?
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            private final View contents = getLayoutInflater().inflate(R.layout.marker_popup, null);

            public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {

                String title = marker.getTitle();

                TextView txtTitle = ((TextView) contents.findViewById(R.id.txtInfoWindowTitle));
                    SpannableString titleText = new SpannableString(title);
                    titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                    txtTitle.setText(titleText);

                TextView txtSnippet = ((TextView) contents.findViewById(R.id.txtInfoWindowDrinks));
                txtSnippet.setText(marker.getSnippet());
                return contents;

            }
        });




    }


    public void loadMarkers() throws UnsupportedEncodingException {

        String baseUrl = "http://www.dipjhutti.zz.vc/myServer3.php?user=" + URLEncoder.encode("2","UTF-8")+"&format=" + URLEncoder.encode("json","UTF-8") + "&num=" + URLEncoder.encode("10","UTF-8");

        HttpClient httpclient = new DefaultHttpClient();
        String response = "";

        try {

            HttpGet httpget = new HttpGet(baseUrl);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httpget, responseHandler);
            if(response != null) {
                parseJSON(response);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Database is down", Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseJSON(String in)  {
        JSONArray posts  = null;
        int postsLength=0;
        try {
            JSONObject reader = new JSONObject(in);
            posts  = reader.getJSONArray("posts");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            postsLength= posts.length();
        }
        catch(NullPointerException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Error parsing results from database. Here's an empty map", Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        }

        try{
            for (int i=0; i<postsLength; i++){
                JSONObject row = posts.getJSONObject(i);
                JSONObject subRow =  row.getJSONObject("post");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
                Date inputDate = null;
                try {
                    inputDate = dateFormat.parse(subRow.getString("dateAdded").substring(0,10));
                } catch (ParseException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error parsing dates", Toast.LENGTH_LONG);
                    toast.show();
                    e.printStackTrace();
                }

                Dispenser myMarker = new Dispenser(subRow.getInt("dispenserID"),subRow.getString("name"),subRow.getString("drinks"),subRow.getDouble("lat"),subRow.getDouble("lng"), inputDate);
                addMarkers(myMarker);
            }
        }
        catch (JSONException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error parsing results from database", Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
        };

    }

    public void addMarkers(Dispenser myDispenser){
        final LatLng dispenserPosition = new LatLng(myDispenser.getLat(),myDispenser.getLng());
        MarkerOptions markerOptions = new MarkerOptions();

        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        String tempDate = formatter.format(myDispenser.getDateAdded());


        if (myDispenser.getAgeInDays()<=AGE_THRESHOLD){
            markerOptions.alpha(calculateOpacity(myDispenser.getDateAdded()));
            markerOptions.position(dispenserPosition).title(myDispenser.getName());
            markerOptions.position(dispenserPosition).snippet(myDispenser.getFormattedDrinks() + "\n" + Math.round(myDispenser.getAgeInDays())+" Days Old");
            myMap.addMarker(markerOptions);
        }
    }

    public float calculateOpacity(Date dateAdded){
        Date todaysDate = new Date();
        float diffInMillies = todaysDate.getTime() - dateAdded.getTime();
        float diffInDays = (AGE_THRESHOLD-(diffInMillies/1000/60/60/24));

        float opacity=0;

        if (AGE_THRESHOLD<=100){
            opacity = diffInDays/100;
        }
        else{
            opacity = (diffInDays*(100/AGE_THRESHOLD))/100;

        }

    return opacity;
    }



    public void zoomToMyLocation(){
        LatLng myLatLng = null;


/*
        Bundle bundle = getIntent().getExtras();
        try{
            oldScreenLatitude = Double.valueOf(bundle.getDouble("LATITUDE"));
            oldScreenLongitude = Double.valueOf(bundle.getDouble("LONGITUDE"));
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

*/
        if (myMap != null) {

            //If we're returning to MyActivity from another use our last screen coordinates
            if (oldScreenLatitude!=null & oldScreenLongitude!=null){
                myLatLng = new LatLng(oldScreenLatitude,oldScreenLongitude);
                CameraUpdate center = CameraUpdateFactory.newLatLng(myLatLng);
                myMap.moveCamera(center);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                myMap.animateCamera(zoom);
            }
            //If it's the first time, get our current location
            else{
               // myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (myLocation!=null){
                    myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    CameraUpdate center = CameraUpdateFactory.newLatLng(myLatLng);
                    myMap.moveCamera(center);
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                    myMap.animateCamera(zoom);
                }

            }


        }

        else {
            int isEnabled = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (isEnabled != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(isEnabled, this, 0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_addUpdate_Dispenser:
             //   Location myLocation = myMap.getMyLocation();
                if (myLocation!=null){
                    Intent intent = new Intent(this, SearchDispenserActivity.class);
                    intent.putExtra("MY_LATITUDE",myLocation.getLatitude());
                    intent.putExtra("MY_LONGITUDE", myLocation.getLongitude());
                    startActivity(intent);
                    return true;
            }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to find location please wait and try again", Toast.LENGTH_LONG);
                    toast.show();
                    return true;
                }
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                LatLng latlngFarRight= myMap.getProjection().getVisibleRegion().farRight;
                LatLng latlngNearLeft = myMap.getProjection().getVisibleRegion().nearLeft;
                double lastLatitude = latlngNearLeft.latitude + (latlngFarRight.latitude-latlngNearLeft.latitude)/2;
                double lastLongitude = latlngNearLeft.longitude + (latlngFarRight.longitude-latlngNearLeft.longitude)/2;
                intent.putExtra("MY_LATITUDE",lastLatitude);
                intent.putExtra("MY_LONGITUDE", lastLongitude);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        myLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (myLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
           handleNewLocation(myLocation);
        }

    }

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        myLocation.setLatitude(currentLatitude);
        myLocation.setLongitude(currentLongitude);
        zoomToMyLocation();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            //Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }
}
