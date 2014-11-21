package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;


public class MyActivity extends Activity {

    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            Location myLocation = mMap.getMyLocation();
            LatLng myLatLng = new LatLng(51.526643, -0.080259);
            CameraUpdate center=CameraUpdateFactory.newLatLng(myLatLng);
            mMap.moveCamera(center);
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
            mMap.animateCamera(zoom);
            final LatLng CIU = new LatLng(51.525543, -0.081259);
            mMap.addMarker(new MarkerOptions().position(CIU).title("Some Bar"));
            final LatLng CIU2 = new LatLng(51.526543, -0.091259);
            mMap.addMarker(new MarkerOptions().position(CIU2).title("Some Other Bar"));

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
