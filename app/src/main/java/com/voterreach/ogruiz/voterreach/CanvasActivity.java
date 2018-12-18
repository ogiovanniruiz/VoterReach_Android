package com.voterreach.ogruiz.voterreach;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.maps.android.ui.IconGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class CanvasActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener
{
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Location mLastLocation;
    SharedPreferences prefs;

    private String script_link;
    private Boolean select_flag = false;
    private String flag_toggle = "my_location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);


        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        script_link = prefs.getString("Script_link", "DEFAULT");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mapFragment.getMapAsync(this);
    }

    private void setUpMap() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);

        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            System.out.println(mLastLocation);

            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String campaign_code = prefs.getString(getString(R.string.pref_code), "DEFAULT");

        String pbuuid = prefs.getString(getString(R.string.pref_pbuuid), "DEFAULT");
        String activity = prefs.getString("Activities", "DEFAULT");

        setUpMap();

        new RequestCanvas().execute(campaign_code, pbuuid,activity);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        setUpMap();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        SharedPreferences.Editor edit = prefs.edit();
        String delim_data = "[:]";

        String full_name = (marker.getTitle()).split(delim_data)[0];
        Double lat = marker.getPosition().latitude;
        Double lng = marker.getPosition().longitude;

        String l1 = lat.toString();
        String l2 = lng.toString();

        String location = l1 + "," + l2;

        edit.putString("FullName", full_name);
        edit.putString(getString(R.string.pref_voterid), (String)marker.getTag());
        edit.putString("Location", location);
        edit.apply();

        flag_toggle = "house_location";

        select_flag = true;

        return false;
    }

    private class RequestCanvas extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(CanvasActivity.this,R.style.AppCompatAlertDialogStyle);
        HttpURLConnection conn;
        URL url = null;

        protected void onPreExecute() {

            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tGetting Canvas locations...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        protected String doInBackground(String... params) {

            try {

                // Enter URL address where your php file resides
                url = new URL("https://voterreach.org/manager/cgi-bin/app/canvas.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("campaigncode", params[0])
                        .appendQueryParameter("pbuuid", params[1])
                        .appendQueryParameter("activity", params[2]);

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                } else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        protected void onPostExecute(String result) {

            System.out.println(result);

            IconGenerator icg = new IconGenerator(CanvasActivity.this);
            icg.setColor(Color.parseColor("#ac2000"));
            icg.setTextAppearance(R.style.iconGenText);

            IconGenerator icg_visited = new IconGenerator(CanvasActivity.this);
            icg_visited.setColor(Color.WHITE);
            icg_visited.setBackground(getResources().getDrawable(R.drawable.mini_visit));


            pdLoading.dismiss();

            String delim_data = "[:]";
            String delim_coords = "[,]";
            String delim_voters = "[*]";
            String[] voters = result.split(delim_voters);



            for(int i=0; i<(voters.length); i++){

                String[] data = voters[i].split(delim_data);
                String[] coords = data[1].split(delim_coords);
                String[] voter_info = data[0].split(delim_coords);

                LatLng location = new LatLng(Float.valueOf(coords[0]), Float.valueOf(coords[1]));
                Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(voter_info[1]));
                marker.setTag(voter_info[0]);
                marker.setSnippet(voter_info[2] + " " + coords[2] + " " + coords[3]);

                if (!voter_info[3].equalsIgnoreCase("N")){
                    Bitmap bm = icg_visited.makeIcon();
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bm));

                    //marker.setIcon(BitmapDescriptorFactory
                            //.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }else
                {


                    if (coords[4].equalsIgnoreCase("0")){
                        Bitmap bm = icg.makeIcon("?");
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bm));

                    } else{

                        Bitmap bm = icg.makeIcon(coords[4]);
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bm));
                    }



                }
            }

            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());

                Toast.makeText(CanvasActivity.this, "LOCATION FOUND", Toast.LENGTH_LONG).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            } else{
                Toast.makeText(CanvasActivity.this, "NO LOCATION", Toast.LENGTH_LONG).show();


            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        //your code when back button pressed
        Intent intent = new Intent(CanvasActivity.this, LoginActivity.class);
        startActivity(intent);
        CanvasActivity.this.finish();
    }

    // Triggers when LOGIN Button clicked
    public void ScriptButton(View arg0) {

        String redirect = "Redirecting to " + script_link;

        Toast.makeText(CanvasActivity.this, redirect, Toast.LENGTH_LONG).show();

        Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
        openURL.setData(Uri.parse(script_link));
        startActivity(openURL);

    }

    // Triggers when LOGIN Button clicked
    public void flagButton(View arg0) {

        String prev_location_string = prefs.getString("Location", "DEFAULT");

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);

        if (prev_location_string.equalsIgnoreCase("DEFAULT")){

            Toast.makeText(CanvasActivity.this, "Please select a valid marker to toggle. Zoom to find a canvasing area.", Toast.LENGTH_LONG).show();

        } else {

            if (flag_toggle.equalsIgnoreCase("house_location")) {


                if (null != locationAvailability && locationAvailability.isLocationAvailable()) {

                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    System.out.println(mLastLocation);

                    if (mLastLocation != null) {
                        LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                                .getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));

                    }
                }

                flag_toggle = "my_location";

            } else if (flag_toggle.equalsIgnoreCase("my_location")){

                String delim_coords = "[,]";

                String[] prev_coords = prev_location_string.split(delim_coords);

                LatLng prev_location = new LatLng(Float.valueOf(prev_coords[0]), Float.valueOf(prev_coords[1]));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prev_location, 19));

                flag_toggle = "house_location";
            }
        }
    }

    // Triggers when LOGIN Button clicked
    public void VisitButton(View arg0) {

        if (!select_flag) {

            Toast.makeText(CanvasActivity.this, "Please select a valid marker.", Toast.LENGTH_LONG).show();

        }else {

            Intent intent = new Intent(CanvasActivity.this,SurveyActivity.class);
            startActivity(intent);

        }

    }

    public void ExitButton(View arg0) {

        //your code when back button pressed
        Intent intent = new Intent(CanvasActivity.this, LoginActivity.class);
        startActivity(intent);
        CanvasActivity.this.finish();
    }

}
