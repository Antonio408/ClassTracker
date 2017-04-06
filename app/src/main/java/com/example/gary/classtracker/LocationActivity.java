package com.example.gary.classtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/*
Created by Gary
Class to record locations as waypoints for app
 */
public class LocationActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
/*
Setup variables and constants to call list intent to add the current location and save to file
 */
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 2;
    public static final String LONGITUDE_KEY = "longitude";
    public static final String LATITUDE_KEY = "latitude";
    public static final String NAME_KEY = "name";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location mLastLocation;


    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private TextView mLatitudeText;
    private TextView mLongitudeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Setup variables for labels
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLatitudeText = (TextView) findViewById((R.id.latitude_TextView));
        mLongitudeText = (TextView) findViewById((R.id.longitude_TextView));

        // make sure permissions have been accepted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }

        // initialize gps client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // initialize location requester
        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        }

        // setup button listener
        findViewById(R.id.record_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = 0;
                double longitude = 0;
                EditText et = (EditText) findViewById(R.id.location_editText);
                String s = et.getText().toString();
                GetLocation();
                // make sure the location exists

                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                }
                if (latitude != 0) {
                    // make sure name was entered
                    if (!(s == null || s.length() == 0)) {
                        // set up intent
                        Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
                        // add variables to intent and start it
                        intent.putExtra(NAME_KEY, s);
                        intent.putExtra(LONGITUDE_KEY, longitude);
                        intent.putExtra(LATITUDE_KEY, latitude);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LocationActivity.this, "need location name to save", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LocationActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // process permissions request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need location permission!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    // log and toast if connection failed
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.i("---------ERROR-------", "Connection failed: ConnectionResult.getErrorCode() = " +
                result.getErrorCode());
        Toast.makeText(this, "Google Location Services Failed", Toast.LENGTH_LONG).show();
    }

    // setup menu not used yet
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // process menu item clicked
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

    // when connected verify permissions and get location
    @Override
    public void onConnected(Bundle connectionHint) {
     /*   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // return;
        } */
        Log.i(LocationActivity.class.getSimpleName(), "Connected to Google Play Services!");

        GetLocation();
    }

    private void GetLocation () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                        mLastLocation.getLatitude()));
                mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                        mLastLocation.getLongitude()));
            } else {
                // if no last location setup requester
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    // if connection is suspended reconnect
    @Override
    public void onConnectionSuspended(int i) {
        Log.i("------ STATUS------", "Connection suspended");
        mGoogleApiClient.connect();
    }

    // connect gps client when resume
    @Override
    protected void onResume() {
        super.onResume();
        //   setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    // when paused stop connection and requester
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    // when location has changed update currrent location
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }


}
