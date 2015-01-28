package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;


/**
 * Created by mjhutti on 13/01/2015.
 */
public class SettingsActivity extends Activity{
    public static final String PREFERENCES = "com.mjhutti.designateddrinker.updatewindow";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Need to add checkboxes for search radius and parameters and perhaps number of results.
        //Need to send old screen locations back to MainActivity
        Bundle bundle = getIntent().getExtras();
        double oldLat  = Double.valueOf(bundle.getDouble("LATITUDE"));
        double oldLng = Double.valueOf(bundle.getDouble("LONGITUDE"));


        Intent intent = new Intent(getApplicationContext(), MyActivity.class);
        intent.putExtra("MY_LATITUDE",oldLat);
        intent.putExtra("MY_LONGITUDE",oldLng);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        final SharedPreferences prefs = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        final TextView lblUpdate_Window = (TextView)findViewById(R.id.lblUpdate_Window);
        int currentUpdateWindow = prefs.getInt("update_window",100);
        seekBar.setProgress(currentUpdateWindow);
        lblUpdate_Window.setText("Update Window (Days): " + currentUpdateWindow);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                lblUpdate_Window.setText("Update Window (Days): " + String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("update_window", seekBar.getProgress());

                // Commit the edits!
                editor.commit();
            }
        });

    }



}
