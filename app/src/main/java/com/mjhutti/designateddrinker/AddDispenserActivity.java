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

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=cruise&key=AIzaSyDKD8t3co5hiLOzDBlkX5bQv8yuI2BfX3g");
            URLConnection connection = url.openConnection();
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }


            JSONObject json = new JSONObject(builder.toString());
            JSONArray results = null;
//need to sort this loop out
                    try{
                        results  = json.getJSONArray("results");
                        for (int i=0; i<results.length(); i++) {
                            JSONObject row = results.getJSONObject(i);
                            JSONArray geometry = row.getJSONArray("location");
                            JSONObject geometryRow = results.getJSONObject(i);

                            String name = row.getString("name");
                            double lat = geometryRow.getDouble("lat");
                            double lng = geometryRow.getDouble("lng");
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
