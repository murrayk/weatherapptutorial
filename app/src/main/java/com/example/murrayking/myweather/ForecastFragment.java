package com.example.murrayking.myweather;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    public static final String WEATHER_KEY = "weatherKey";
    String LOG_TAG = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> arrayAdapter;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.forecast_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }



    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.

        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";

        //new FetchWeatherTask().execute(url);

        String[] weekForecast = new String[]{"1", "2", "3"};
        ArrayList<String> week = new ArrayList<String>(Arrays.asList(weekForecast));
        arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, week);
        ListView listView = (ListView)rootView.findViewById(R.id.listViewForecast);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "this is my Toast message!!! =)" + arrayAdapter.getItem(i),
                        Toast.LENGTH_LONG).show();
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class).putExtra(WEATHER_KEY, arrayAdapter.getItem(i));

                //detailIntent.putExtra(ROUTE_CHOSEN_KEY, row);

                // TODO: add any other data you'd like as Extras

                // start the next Activity using your prepared Intent
                startActivity(detailIntent);

            }
        });
        return rootView;
    }

    class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] result) {
            if(result!= null){

                Log.v(LOG_TAG, Arrays.asList(result).toString());
                ForecastFragment.this.arrayAdapter.clear();
                for(String day : result)
                    ForecastFragment.this.arrayAdapter.add(day);
                ForecastFragment.this.arrayAdapter.notifyDataSetChanged();

            }
        }

        @Override
        protected String[] doInBackground(String ... urlVarArg) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(urlVarArg[0]);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, forecastJsonStr );

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            String[] weather = new String[]{};
            WeatherDataParser weatherDataParser = new WeatherDataParser();
            try {
                weather =weatherDataParser.getWeatherDataFromJson(forecastJsonStr, 7);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //q=94043&mode=json&units=metric&cnt=7
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("q", "94043")
                .appendQueryParameter("mode", "json")
                .appendQueryParameter("units","metric")
                .appendQueryParameter("cnt", "7");

        Uri uri= builder.build();
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();

            fetchWeatherTask.execute(uri.toString());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}