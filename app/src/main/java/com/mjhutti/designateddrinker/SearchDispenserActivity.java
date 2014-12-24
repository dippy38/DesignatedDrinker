package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class SearchDispenserActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> nearbyDispensers = new ArrayList<String>();
        setContentView(R.layout.activity_add_dispenser);

        String myLatLong = new String (MyActivity.myMap.getMyLocation().getLatitude() + "," + MyActivity.myMap.getMyLocation().getLongitude());
        String types = "bar|liquor_store|restaurant|grocery_or_supermarket";
        String rankBy="distance";
        String radius="1000";

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
            String status = json.getString("status");
            String statusMessage="";
            switch (status){
                case "OK" : statusMessage = "We found the following places nearby";break;
                case "ZERO_RESULTS" : statusMessage = "We couldnt find any places nearby";break;
                case "OVER_QUERY_LIMIT": statusMessage = "Our servers are taking the rest of the day off";break;
                case "REQUEST_DENIED":statusMessage= "The app did not provide a key to Google";break;
                case "INVALID_REQUEST": statusMessage="The location or radius parameter is missing";break;
            }

            Context context = getApplicationContext();
            CharSequence text = statusMessage;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            JSONArray resultsArray = null;
                    try{
                        resultsArray  = json.getJSONArray("results");
                        for (int i=0; i<resultsArray.length(); i++) {

                            JSONObject result = resultsArray.getJSONObject(i);
                            Double priceLevel=null;
                            Double rating=null;
                            Boolean openFlag=null;
                            String icon=null;


                            JSONObject resultGeometry = result.getJSONObject("geometry");
                            JSONObject resultLocation = resultGeometry.getJSONObject("location");

                            //Mandatory Stuff
                            String id = result.getString("place_id");
                            String name = result.getString("name");
                            double lng = resultLocation.getDouble("lng");
                            double lat = resultLocation.getDouble("lat");

                            //Additional data that may not be available
                            if (!result.isNull("price_level")){priceLevel = result.getDouble("price_level");}
                            if (!result.isNull("rating")){rating = result.getDouble("rating");}
                            if (!result.isNull("icon")){icon = result.getString("icon");}
                            if (!result.isNull("open_now")){openFlag = result.getBoolean("open_now");}

                            //double price_level, double rating, String icon, boolean open_now){
                            //Dispenser nearbyDispenser = new Dispenser(1,name,null,lat,lng,null,priceLevel,rating,icon,openFlag);
                            nearbyDispensers.add("Name: " + name + " Latitude:" + lat + " Longitude" + lng);
                            //nearbyDispensers.add(nearbyDispenser.toString());
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

        //Create the ListAdapter and populate it
        ListAdapter listAdapter = new ArrayAdapter<String>(this, R.layout.list_item,nearbyDispensers);

        //Connect the ListAdapter and ListView
        ListView lv = new ListView(this);
        lv.setAdapter(listAdapter);


        //Set what happens when each list item is clicked
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

                String item = ((TextView)arg1).getText().toString();
                CharSequence text = item;
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), AddDispenserActivity.class);

                intent.putExtra("DISPENSER_NAME",text);

                startActivity(intent);
            }


        });



        //Connect the Layout and now-populated View
        myLayout.addView(lv, lvParams);
        setContentView(myLayout);
    }




}
