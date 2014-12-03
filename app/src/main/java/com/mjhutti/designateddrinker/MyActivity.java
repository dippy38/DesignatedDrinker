package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyActivity extends Activity {

    private LocationManager locMan;
    private GoogleMap myMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Map and Location Manager
        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        myMap.setMyLocationEnabled(true);

        loadMarkers();
        addMarkers();
        zoomToMyLocation();


    }


    public void loadMarkers(){

        BufferedReader in = null;
        String baseUrl = "https://pplabs.co.uk/~mandeep/myServer2.php?user=2&format=json&num=10";

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(baseUrl);
        try {
            HttpResponse response = httpclient.execute(httpget);
            if(response != null) {
                String line = "";
                InputStream inputstream = response.getEntity().getContent();
                return convertStreamToString(inputstream);
            } else {
                return "Unable to complete your request";
            }
        } catch (ClientProtocolException e) {
            return "Caught ClientProtocolException";
        } catch (IOException e) {
            return "Caught IOException";
        }

    }

    private String convertStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            return "Stream Exception";
        }
        return total.toString();
    }


    public void addMarkers(){
        final LatLng CIU = new LatLng(51.4586850,-0.3085940);
        MarkerOptions markerOptions = new MarkerOptions().position(CIU).title("Some Bar");
        markerOptions.position(CIU).snippet("Serves many non-alcoholic beers");
        myMap.addMarker(markerOptions);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
