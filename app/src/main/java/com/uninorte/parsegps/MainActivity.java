package com.uninorte.parsegps;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LocationListener {

    LocationManager mLocationManager;
    private ProgressDialog pDialog;
    private ListView listView;
    List<ParseObject> ob;
    private ArrayList values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ibEJg13ONSbk3wKIW9VfS2sefpCBP4DuAUeiZhyg", "KlWccS4kub3utRSvmRZlRAzyREmgygPTCwhZDAd2");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isNetworkAvailable()){
            new SendData(location).execute();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isNetworkAvaible = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isNetworkAvaible = true;
            Toast.makeText(this, "Network is available ", Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(this, "Network not available ", Toast.LENGTH_LONG)
                    .show();
        }
        return isNetworkAvaible;
    }

    private class SendData extends AsyncTask<Void, Void, Void> {

        Location mLocation;
        public SendData(Location location){
            super();
            mLocation = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ParseObject testObject = new ParseObject("Route");
            testObject.put("Latitude",mLocation.getLatitude());
            testObject.put("Longitude",mLocation.getLongitude());
            testObject.put("Altitude",mLocation.getAltitude());
            testObject.saveInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
        }

    }
}
