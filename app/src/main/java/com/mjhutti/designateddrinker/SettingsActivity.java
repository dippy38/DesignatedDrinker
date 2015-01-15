package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

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
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        final SharedPreferences prefs = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        final TextView tvCurrentUpdateWindowValue = (TextView)findViewById(R.id.tvSeekBarValue);
      //  tvCurrentUpdateWindowValue.setText(prefs.getInt("updateWindow",0));


        final TextView lblUpdate_Window = (TextView)findViewById(R.id.lblUpdate_Window);
        int currentUpdateWindow = prefs.getInt("update_window",0);
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
/*
        Button saveSettingsButton = (Button) findViewById(R.id.btnSaveSettings);
        saveSettingsButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                prefs.edit().putInt("update_window", seekBar.getProgress()) ;
                prefs.edit().commit();
            }
        });
        */
    }


    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context

    }

}
