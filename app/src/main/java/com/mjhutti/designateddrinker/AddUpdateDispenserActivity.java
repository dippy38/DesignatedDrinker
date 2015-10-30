package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mjhutti on 24/12/2014.
 */
public class AddUpdateDispenserActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // View v = inflater.inflate(R.layout.new_location_dialog, null);
        //builder.setView(v);

        setContentView(R.layout.activity_addupdate_dispenser);
        //Get Bundle
        Bundle bundle = getIntent().getExtras();
        final double lat = Double.valueOf(bundle.getString("LATITUDE"));
        final double lng = Double.valueOf(bundle.getString("LONGITUDE"));
        final String dispenserName = bundle.getString("DISPENSER_NAME");


        //Define EditBoxes
        final EditText etName = (EditText) findViewById(R.id.etName);
                        etName.setText(dispenserName);
        final EditText etLatitude = (EditText) findViewById(R.id.etLatitude);
                        etLatitude.setText(bundle.getString("LATITUDE"));
        final EditText etLongitude = (EditText) findViewById(R.id.etLongitude);
                        etLongitude.setText(bundle.getString("LONGITUDE"));

        //Populate drinks list if already on database
        String drinks="";
        Dispenser myDispenser = new Dispenser(99, dispenserName, "", lat, lng, new Date());
        try{
            drinks = dispenserExists(myDispenser);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        final EditText etDrinks = (EditText) findViewById(R.id.etDrinks);
        if (drinks.length()>0){
            etDrinks.setText(drinks);
        }
        else{
            etDrinks.setHint("List available drinks here (comma separated)");
        }

        //Define button
        Button addUpdateButton = (Button) findViewById(R.id.btnAddDispenser);
        addUpdateButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Dispenser myDispenser = new Dispenser(99, dispenserName, etDrinks.getEditableText().toString(), lat, lng, new Date());
                try {
                    if (dispenserExists(myDispenser).length() > 0) {

                        updateDispenser(myDispenser);
                    } else {
                        addDispenser(myDispenser);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        //TODO GENERALLY JUST THINK ABOUT THREADS
        //TODO need to remove dispenserID from SQL as lat lng should suffice
        //TODO Have proximity checker to make sure dupilicates aren't added when adding from multiple sources (i.e. once moved on from just Google)
        //TODO need update function. Should this be the same as Add if found?
        //TODO modify pop-up to show all contents available and update add dispenser and SearchDispenser to show this
    }
    public String dispenserExists(Dispenser myDispenser) throws UnsupportedEncodingException{

        String dispenserExists ="";
        String baseUrl = "http://www.dipjhutti.esy.es/dispenserExists.php?lat=" + URLEncoder.encode(String.valueOf(myDispenser.getLat()),"UTF-8")+"&lng=" + URLEncoder.encode(String.valueOf(myDispenser.getLng()));

        HttpClient httpclient = new DefaultHttpClient();
        String response = "";

        try {

            HttpGet httpget = new HttpGet(baseUrl);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httpget, responseHandler);
            dispenserExists = parseJSON(response);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dispenserExists;
    }


    public String parseJSON(String in)  {
        String drinks="";
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
                drinks = subRow.getString("drinks");
            }
        }
        catch (JSONException e) {};

       return drinks;
    }


    public void updateDispenser(Dispenser oldDispenser){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");

        String dispenserName;
        String drinks;

        try {
            dispenserName = URLEncoder.encode(oldDispenser.getName().replace("'", "''"), "UTF-8");  //had to hack the apostrophes to work with UTF-8
            drinks = URLEncoder.encode(oldDispenser.getDrinks().replace("'", "''"), "UTF-8");
        }
        catch (UnsupportedEncodingException ex){
            throw new RuntimeException("UTF-8 not supported", ex);
        }
        String baseUrl = "http://www.dipjhutti.esy.es/updateDispenser.php?drinks="+drinks+"&lat="+ oldDispenser.getLat()+"&lng="+oldDispenser.getLng()+"&dateAdded="+formatter.format(oldDispenser.getDateAdded());

        HttpClient httpclient = new DefaultHttpClient();
        String response = "";

        try {

            HttpPost httppost = new HttpPost(baseUrl);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);
            Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
            if(response != null) {
                toast.setText("Updated Successfully");
                Intent intent = new Intent(getApplicationContext(), MyActivity.class);
                startActivity(intent);
            } else {
                toast.setText("Could not update in database");
            }
            toast.show();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void addDispenser(Dispenser newDispenser){
            Format formatter = new SimpleDateFormat("yyyy-MM-dd");

        String dispenserName;
        String drinks;

        try {
            dispenserName = URLEncoder.encode(newDispenser.getName().replace("'", "''"), "UTF-8");  //had to hack the apostrophes to work with UTF-8
            drinks = URLEncoder.encode(newDispenser.getDrinks().replace("'", "''"), "UTF-8");
        }
        catch (UnsupportedEncodingException ex){
            throw new RuntimeException("UTF-8 not supported", ex);
        }
        String baseUrl = "http://www.dipjhutti.esy.es/addDispenser.php?dispenserName="+dispenserName+"&drinks="+drinks+"&lat="+ newDispenser.getLat()+"&lng="+newDispenser.getLng()+"&dateAdded="+formatter.format(newDispenser.getDateAdded());

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

      /*
        Intent intent = new Intent(this, SearchDispenserActivity.class);

        switch (item.getItemId()) {
            case R.id.action_addUpdate_Dispenser:
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
      */
        return super.onOptionsItemSelected(item);
    }



}
