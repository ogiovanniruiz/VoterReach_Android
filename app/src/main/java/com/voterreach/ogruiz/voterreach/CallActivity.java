package com.voterreach.ogruiz.voterreach;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;


import android.widget.TextView;
import android.widget.Toast;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;



public class CallActivity extends AppCompatActivity{
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private String voterid;
    private SharedPreferences prefs;
    private String voterphonenumber;
    private String fullname;
    private String script_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String pbuuid = prefs.getString(getString(R.string.pref_pbuuid), "DEFAULT");
        String campaign_code = prefs.getString(getString(R.string.pref_code), "DEFAULT");
        script_link = prefs.getString("Script_link", "DEFAULT");
        String activity = prefs.getString("Activities", "DEFAULT");

        new RequestCall().execute(campaign_code, pbuuid, activity);
    }

    public void ExitButton(View arg0) {

        //your code when back button pressed
        Intent intent = new Intent(CallActivity.this, LoginActivity.class);
        startActivity(intent);
        CallActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        //your code when back button pressed
        Intent intent = new Intent(CallActivity.this, LoginActivity.class);
        startActivity(intent);
        CallActivity.this.finish();
    }


    private class RequestCall extends AsyncTask<String, String, String>{

        ProgressDialog pdLoading = new ProgressDialog(CallActivity.this,R.style.AppCompatAlertDialogStyle);
        HttpURLConnection conn;
        URL url = null;

        protected void onPreExecute() {

            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tGetting New Voter...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        protected String doInBackground(String... params) {

            try {

                // Enter URL address where your php file resides
                url = new URL("https://voterreach.org/manager/cgi-bin/app/call.php");

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

            pdLoading.dismiss();

            if (result.equalsIgnoreCase("false")){

                Toast.makeText(CallActivity.this, "All voters have been called.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CallActivity.this, LoginActivity.class);
                startActivity(intent);
                CallActivity.this.finish();

            }

            else {

                String delims = "[,]";
                String[] voter_data = result.split(delims);

                final TextView voterNameTextView = (TextView) findViewById(R.id.voterName);
                final TextView callernumberTextView = (TextView) findViewById(R.id.numbercalls);
                final TextView totalcallsTextView = (TextView) findViewById(R.id.totalcalls);

                final TextView voterCityTextView = (TextView) findViewById(R.id.voterCity);
                final TextView voterPartyTextView = (TextView) findViewById(R.id.voterParty);
                final TextView voterAgeTextView = (TextView) findViewById(R.id.voterAge);

                if (voter_data.length> 1) {

                    fullname = voter_data[0] + " " + voter_data[1];

                    voterNameTextView.setText(fullname);

                    voterphonenumber = voter_data[2];

                    voterid = voter_data[3];

                    String feedback = "You have made " + voter_data[4] + " calls.";

                    String total_feedback = "The Phonebank has made " + voter_data[5] + " total calls.";


                    totalcallsTextView.setText(total_feedback);

                    callernumberTextView.setText(feedback);

                    voterCityTextView.setText(voter_data[6]);

                    voterPartyTextView.setText(voter_data[7]);

                    voterAgeTextView.setText(voter_data[8]);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("FullName", fullname);
                    edit.apply();

                } else{

                    Toast.makeText(CallActivity.this, "The connection is slow. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CallActivity.this, LoginActivity.class);
                    startActivity(intent);
                    CallActivity.this.finish();

                }
            }

        }
    }

    public void CallButton(View arg0) {

        Date currentTime = Calendar.getInstance().getTime();

        int hour = currentTime.getHours();

        if ((1==2) &&((hour >= 23) || (hour <= 9))){

            Toast.makeText(CallActivity.this, "It is after 11pm or before 9am. The Call function has been disabled.", Toast.LENGTH_LONG).show();
        }

        else{

            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(getString(R.string.pref_voterid), voterid);
            edit.apply();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1234);

            } else {

                Intent intent = new Intent(CallActivity.this,SurveyActivity.class);
                startActivity(intent);

                startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", voterphonenumber, null)));
                String prepare = "Preparing to call " + fullname;
                Toast.makeText(CallActivity.this, prepare, Toast.LENGTH_LONG).show();
                CallActivity.this.finish();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1234: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(CallActivity.this,SurveyActivity.class);
                    startActivity(intent);

                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", voterphonenumber, null)));

                    String prepare = "Preparing to call " + fullname;
                    Toast.makeText(CallActivity.this, prepare, Toast.LENGTH_LONG).show();
                    CallActivity.this.finish();


                }

            }

        }
    }

    // Triggers when LOGIN Button clicked
    public void ScriptButton(View arg0) {

        String redirect = "Redirecting to " + script_link;

        Toast.makeText(CallActivity.this, redirect, Toast.LENGTH_LONG).show();

        Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
        openURL.setData(Uri.parse(script_link));
        startActivity(openURL);


    }

}


