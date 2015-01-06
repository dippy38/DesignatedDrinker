package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mjhutti on 24/12/2014.
 */
public class AddDispenserActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Bundle
        Bundle bundle = getIntent().getExtras();
        final double lat = new Double(bundle.getString("LATITUDE"));
        final double lng = new Double(bundle.getString("LONGITUDE"));
        final String dispenserName = bundle.getString("DISPENSER_NAME");
        setContentView(R.layout.add_dispenser);


        //Define EditBoxes
        final EditText etName = (EditText) findViewById(R.id.etName);
                        etName.setText(dispenserName);
        final EditText etLatitude = (EditText) findViewById(R.id.etLatitude);
                        etLatitude.setText(bundle.getString("LATITUDE"));
        final EditText etLongitude = (EditText) findViewById(R.id.etLongitude);
                        etLongitude.setText(bundle.getString("LONGITUDE"));
        final EditText etDrinks = (EditText) findViewById(R.id.etDrinks);
                        etDrinks.setHint("List available drinks here (comma separated)");

        //Define button
        Button addButton = (Button) findViewById(R.id.btnAddDispenser);
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Dispenser newDispenser = new Dispenser(99,dispenserName,etDrinks.getEditableText().toString(),lat,lng,new Date());
                addDispenser(newDispenser);
            }
        });

        //TODO GENERALLY JUST THINK ABOUT THREADS
        //TODO need to remove dispenserID from SQL as lat lng should suffice
        //TODO Have proximity checker to make sure dupilicates aren't added when adding from multiple sources (i.e. once moved on from just Google)
        //TODO need update function. Should this be the same as Add if found?
        //TODO modify pop-up to show all contents available and update add dispenser and SearchDispenser to show this
    }


    public void addDispenser(Dispenser newDispenser){
            Format formatter = new SimpleDateFormat("yyyy-MM-dd");

        //TODO add ability to escape special characters
        //TODO how to send commas separated values
        //TODO How to encode this to stop hacking
            String baseUrl = "http://www.dipjhutti.zz.vc/addDispenser.php?dispenserName="+newDispenser.getName().replaceAll(" ","%20")+"&drinks="+newDispenser.getDrinks().replaceAll(" ","%20")+"&lat="+ newDispenser.getLat()+"&lng="+newDispenser.getLng()+"&dateAdded="+formatter.format(newDispenser.getDateAdded());
            HttpClient httpclient = new DefaultHttpClient();
            String response = "";

            try {

                HttpPost httppost = new HttpPost(baseUrl);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = httpclient.execute(httppost, responseHandler);
                Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
                if(response != null) {
                    toast.setText("Added Successfully");
                    Intent intent = new Intent(getApplicationContext(), MyActivity.class);
                    startActivity(intent);
                } else {
                    toast.setText("Could not add to database");
                }
                toast.show();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
