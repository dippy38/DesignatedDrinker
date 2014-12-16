package com.mjhutti.designateddrinker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v7.app.ActionBarActivity;

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
