package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.identity.intents.AddressConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mjhutti on 16/12/2014.
 */
public class AddDispenserActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> nearbyDispensers = new ArrayList<String>();
        setContentView(R.layout.activity_add_dispenser);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        Bundle extras = getIntent().getExtras();
        double myLongitude=extras.getDouble("MY_LONGITUDE");
        double myLatitude=extras.getDouble("MY_LATITUDE");
        
        String myLatLong = myLatitude + "," + myLongitude;
        String types = "bar|liquor_store|restaurant|grocery_or_supermarket";
        String rankBy="distance";
        String radius="250";

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+myLatLong+"&radius="+radius+"&types="+types+"&rankBy="+rankBy+"&key=AIzaSyDKD8t3co5hiLOzDBlkX5bQv8yuI2BfX3g");
            URLConnection connection = url.openConnection();
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }


            JSONObject json = new JSONObject(builder.toString());
            JSONArray resultsArray = null;
            //Idea should have filter which filters out those dispensers that aren't open
                    try{
                        resultsArray  = json.getJSONArray("results");
                        for (int i=0; i<resultsArray.length(); i++) {
                            JSONObject result = resultsArray.getJSONObject(i);
                            JSONObject resultGeometry = result.getJSONObject("geometry");
                            JSONObject resultLocation = resultGeometry.getJSONObject("location");

                            String name = result.getString("name");
                            double lng = resultLocation.getDouble("lng");
                            double lat = resultLocation.getDouble("lat");
                           // Dispenser googleDispenser = new Dispenser(i,name,null,lat,lng,new Date());
                            nearbyDispensers.add("Name: " + name + " Latitude:" + lat + "Longitude" + lng);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

        }
        catch (Exception e) {}


        //Create a Layout
        LinearLayout myLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lvParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        //TODO, Fill a String array to populate the listadapter
        //Create the ListAdapter and populate it
        ListAdapter listAdapter = new ArrayAdapter<String>(this, R.layout.list_item,nearbyDispensers);

        //Connect the ListAdapter and ListView
        ListView lv = new ListView(this);
        lv.setAdapter(listAdapter);

        //Connect the Layout and now-populated View
        myLayout.addView(lv, lvParams);
        setContentView(myLayout);
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

            }
        }
        catch (JSONException e) {};

    }
}
