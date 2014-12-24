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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;

import java.io.UnsupportedEncodingException;

/**
 * Created by mjhutti on 24/12/2014.
 */
public class AddDispenserActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create a Layout
        LinearLayout myLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lvParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        Button addButton = new Button(this);
        addButton.setText("Add");

        //Button buttonOne = (Button) findViewById(R.id.button1);
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //TODO
                Toast toast = Toast.makeText(getApplicationContext(), "Need to Implement", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        TextView tv = new TextView(this);
        Bundle bundle = getIntent().getExtras();
        tv.setText(bundle.getString("DISPENSER_NAME"));

        myLayout.addView(tv, lvParams);
        myLayout.addView(addButton, lvParams);

        setContentView(myLayout);
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
