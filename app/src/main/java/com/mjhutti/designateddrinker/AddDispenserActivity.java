package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v7.app.ActionBarActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mjhutti on 16/12/2014.
 */
public class AddDispenserActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dispenser);

        //Create a Layout
        LinearLayout myLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lvParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        // The request also includes the userip parameter which provides the end
// user's IP address. Doing so will help distinguish this legitimate
// server-side traffic from traffic which doesn't come from an end-user.
        try {
           // URL url = new URL("https://ajax.googleapis.com/ajax/services/search/web?v=1.0&" + "q=Pubs%20Near%20Me&userip="+ip);
            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=cruise&key=AIzaSyBRsFSpfeMqVVchfJfz67UHvIM4nslz3qo");
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Referer","www.dipjhutti.vv.cz" /* Enter the URL of your site here */);

            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }


            JSONObject json = new JSONObject(builder.toString());
        }
        catch (Exception e) {}
//need ability to add to google maps so others can find in the future

// now have some fun with the results...

        //Create the ListAdapter and populate it
      //  ListAdapter listAdapter = new ArrayAdapter<String>(this, R.layout.list_item, Basket.getFullBasketList() );

        //Connect the ListAdapter and ListView
      //  ListView lv = new ListView(this);
      //  lv.setAdapter(listAdapter);

        //Connect the Layout and now-populated View
       // myLayout.addView(lv, lvParams);
        setContentView(myLayout);
    }
}
