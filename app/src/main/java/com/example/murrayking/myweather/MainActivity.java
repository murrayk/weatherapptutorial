package com.example.murrayking.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
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
            Intent settingActivity = new Intent(this, SettingsActivity.class);

            //detailIntent.putExtra(ROUTE_CHOSEN_KEY, row);

            // TODO: add any other data you'd like as Extras

            // start the next Activity using your prepared Intent
            startActivity(settingActivity);
            return true;
        }

        if (id == R.id.action_show_on_map) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String geoLocation  = sharedPref.getString(this.getString(R.string.pref_location_key), this.getString(R.string.pref_location_default));

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("geo")
                    .appendQueryParameter("q", geoLocation);

            Uri uri= builder.build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
