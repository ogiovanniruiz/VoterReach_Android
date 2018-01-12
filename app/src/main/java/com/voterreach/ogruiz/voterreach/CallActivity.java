package com.voterreach.ogruiz.voterreach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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


public class CallActivity extends AppCompatActivity{
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private String voterid;
    private String pbuuid;
    private String campaign_code;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        pbuuid = prefs.getString(getString(R.string.pref_pbuuid), "DEFAULT");
        campaign_code = prefs.getString(getString(R.string.pref_code), "DEFAULT");

        final String code = campaign_code;
        final String id = pbuuid;

        new RequestCall().execute(code, id);
    }

    private class RequestCall extends AsyncTask<String, String, String>{

        ProgressDialog pdLoading = new ProgressDialog(CallActivity.this);
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
                url = new URL("http://voterreach.org/cgi-bin/call.php");


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
                        .appendQueryParameter("pbuuid", params[1]);

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

            if (result.equalsIgnoreCase("\uFEFFfalse")){

                Toast.makeText(CallActivity.this, "Could not Retrieve Voter Data.", Toast.LENGTH_LONG).show();

            }

            else {

                System.out.println(result);
                String delims = "[,]";
                String[] voter_data = result.split(delims);

                final TextView voterNameTextView = (TextView) findViewById(R.id.textView);
                String fullname = voter_data[0] + " " + voter_data[1];

                voterNameTextView.setText(fullname);

                voterid = voter_data[3];
            }

        }
    }

    // Triggers when LOGIN Button clicked
    public void CallButton(View arg0) {

        // Initialize  AsyncLogin() class with email and password
        Toast.makeText(CallActivity.this, "Calling Voter...", Toast.LENGTH_LONG).show();

    }

    // Triggers when LOGIN Button clicked
    public void SurveyButton(View arg0) {

        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(getString(R.string.pref_voterid), voterid);
        edit.apply();

        // Initialize  AsyncLogin() class with email and password
        Intent intent = new Intent(CallActivity.this,SurveyActivity.class);
        startActivity(intent);
        CallActivity.this.finish();

    }

}
