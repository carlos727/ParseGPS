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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.text.ParseException;
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

        listView = (ListView) findViewById(R.id.listView);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,this);

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

    public void DataFromParse(View view){
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        new GetData().execute();
    }

    public void DataToParse(View view){
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,this);
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
            Toast.makeText(this, "Sending data", Toast.LENGTH_LONG)
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
            Log.d("Latitude: ", mLocation.getLatitude()+"");
            testObject.put("Longitude",mLocation.getLongitude());
            Log.d("Longitude: ", mLocation.getLongitude()+"\n");
            testObject.saveInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
        }

    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            pDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            pDialog.setTitle("Loading data from Parse");
            // Set progressdialog message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressdialog
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            values = new ArrayList<String>();
            try {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Route");
                ob = query.find();
                for (ParseObject data : ob)
                    values.add("\nLatitude: "+ data.get("Latitude")+ "\nLongitude: " + data.get("Longitude") + "\n");

            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            listView.setAdapter(adapter);

            // Close the progressdialog
            pDialog.dismiss();
        }
    }
}
