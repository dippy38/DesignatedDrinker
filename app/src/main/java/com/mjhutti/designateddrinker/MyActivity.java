package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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


public class MyActivity extends Activity {

    private LocationManager locMan;
    public static GoogleMap myMap;
    private long AGE_THRESHOLD=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Temporary fix according to http://stackoverflow.com/questions/19266553/android-caused-by-android-os-networkonmainthreadexception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Get Map and Location Manager
        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locMan.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ))
        {
            Context context = getApplicationContext();
            CharSequence text = "Please enable GPS";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        }

        myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        myMap.setMyLocationEnabled(true);

        try {
            loadMarkers();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            private final View contents = getLayoutInflater().inflate(R.layout.popup, null);

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

        zoomToMyLocation();


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
                Context context = getApplicationContext();
                CharSequence text = "Database is down";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        } catch (ClientProtocolException e) {
            parseJSON(response);
        } catch (IOException e) {
            parseJSON(response);
        }

    }

    public void parseJSON(String in)  {
        JSONArray posts  = null;
        try {
            JSONObject reader = new JSONObject(in);
            posts  = reader.getJSONArray("posts");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            for (int i=0; i<posts.length(); i++){
                JSONObject row = posts.getJSONObject(i);
                JSONObject subRow =  row.getJSONObject("post");

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;

                Date inputDate = null;
                try {
                    inputDate = dateFormat.parse(subRow.getString("dateAdded").substring(0,10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Dispenser myMarker = new Dispenser(subRow.getInt("dispenserID"),subRow.getString("name"),subRow.getString("drinks"),subRow.getDouble("lat"),subRow.getDouble("lng"), inputDate);
                addMarkers(myMarker);
            }
        }
        catch (JSONException e) {};

    }

    public void addMarkers(Dispenser myDispenser){
        final LatLng CIU = new LatLng(myDispenser.getLat(),myDispenser.getLng());
        MarkerOptions markerOptions = new MarkerOptions();

        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        String tempDate = formatter.format(myDispenser.getDateAdded());


        if (calculateOpacity(myDispenser.getDateAdded())*100<=AGE_THRESHOLD){
            markerOptions.alpha(calculateOpacity(myDispenser.getDateAdded()));
            markerOptions.position(CIU).title(myDispenser.getName());
            markerOptions.position(CIU).snippet(myDispenser.getFormattedDrinks() + "\n Last Updated: " + tempDate );
            myMap.addMarker(markerOptions);
        }
    }

    public float calculateOpacity(Date dateAdded){
        Date todaysDate = new Date();
        float diffInMillies = todaysDate.getTime() - dateAdded.getTime();
        float diffInDays = AGE_THRESHOLD-(diffInMillies/1000/60/60/24);
    return diffInDays/100;
    }


    public void zoomToMyLocation(){
        if (myMap != null) {
            Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng myLatLng = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
            CameraUpdate center=CameraUpdateFactory.newLatLng(myLatLng);
            myMap.moveCamera(center);
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
            myMap.animateCamera(zoom);

        } else {
            int isEnabled = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (isEnabled != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(isEnabled, this, 0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent = new Intent(this, SearchDispenserActivity.class);

        switch (item.getItemId()) {
            case R.id.action_addDispenser:
                Location myLocation = myMap.getMyLocation();
                if (myLocation!=null){
                    intent.putExtra("MY_LATITUDE",myLocation.getLatitude());
                    intent.putExtra("MY_LONGITUDE",myLocation.getLongitude());
                    startActivity(intent);
                    return true;
            }
                else{
                    Context context = getApplicationContext();
                    CharSequence text = "Unable to find location please wait and try again";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return false;
                }




            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
