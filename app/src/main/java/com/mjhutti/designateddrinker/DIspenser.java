package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mjhutti on 27/11/2014.
 */
public class Dispenser extends Activity {

        public void queryDatabase(View v) {
            try {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams,2500);
                HttpConnectionParams.setSoTimeout(httpParams, 2500);

                HttpParams p = new BasicHttpParams();
                p.setParameter("user", "1");

                // Instantiate an HttpClient
                HttpClient httpclient = new DefaultHttpClient(p);
                String url = "https://pplabs.co.uk/~mandeep/myServer2.php?user=1&format=json";


                HttpPost httppost = new HttpPost(url);

                // Instantiate a GET HTTP method
                try {
                    Log.i(getClass().getSimpleName(), "send  task - start");

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("user", "1"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = httpclient.execute(httppost,responseHandler);

                    // Parse
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray jArray = json.getJSONArray("posts");
                    ArrayList<HashMap<String, String>> mylist =new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < jArray.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject e = jArray.getJSONObject(i);
                        String s = e.getString("post");
                        JSONObject jObject = new JSONObject(s);

                        map.put("ID", jObject.getString("DispenserID"));
                        map.put("Name", jObject.getString("Name"));
                        map.put("Drinks", jObject.getString("Drinks"));
                        map.put("lat", jObject.getString("lat"));
                        map.put("Lng", jObject.getString("lng"));

                        mylist.add(map);
                    }
                    Toast.makeText(this, responseBody, Toast.LENGTH_LONG).show();

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Log.i(getClass().getSimpleName(), "send  task - end");

            } catch (Throwable t) {
                Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
            }
        }

    }

