package com.example.murrayking.myweather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.murrayking.myweather.data.WeatherDbHelper;

import static com.example.murrayking.myweather.data.WeatherContract.LocationEntry;
import static com.example.murrayking.myweather.data.WeatherContract.WeatherEntry;
/**
 * Created by murrayking on 28/10/2014.
 */
public class TestDb extends AndroidTestCase {
    private String TAG = TestDb.class.getSimpleName();
    public void testCreateDB(){
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }


    public void testInsertReadDb(){

        String testLocationSetting = "99705";
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, "Edinburgh");
        values.put(LocationEntry.COLUMN_COORD_LAT, 12.2);
        values.put(LocationEntry.COLUMN_COORD_LONG, 44);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        long i = db.insert(LocationEntry.TABLE_NAME, null, values);

        Log.i(TAG, "Inserted row");

        assertEquals(true,i != -1);
        String[] columns = new String [] {
            LocationEntry._ID,
            LocationEntry.COLUMN_COORD_LONG,
            LocationEntry.COLUMN_COORD_LAT,
            LocationEntry.COLUMN_CITY_NAME,
            LocationEntry.COLUMN_LOCATION_SETTING
        };

        Cursor c = db.query(LocationEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null);

        if(c.moveToNext()){

            int locationIndex = c.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING);
            String location = c.getString(locationIndex);

            int nameIndex = c.getColumnIndex(LocationEntry.COLUMN_CITY_NAME);
            String name = c.getString(nameIndex);

            int latIndex = c.getColumnIndex(LocationEntry.COLUMN_COORD_LAT);
            double lat = c.getDouble(latIndex);

            int longIndex = c.getColumnIndex(LocationEntry.COLUMN_COORD_LONG);
            double lng = c.getDouble(longIndex);

            assertEquals("Edinburgh", name);
            assertEquals(12.2, lat, 0.001 );



        } else {
            fail("No row");
        }

        c.close();

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, testLocationSetting);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        long j= db.insert(WeatherEntry.TABLE_NAME,null, weatherValues);
        assertTrue(j != -1);


        Cursor wc = db.query(WeatherEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if(wc.moveToNext()){

            int weatherIdIndex = wc.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
            int weatherId = wc.getInt(weatherIdIndex);

            assertEquals(321, weatherId);



        } else {
            fail("No row");
        }

        wc.close();
        db.close();

    }
}
