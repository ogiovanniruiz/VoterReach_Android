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
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.UUID;


public class SearchActivity extends AppCompatActivity{

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etPhonenumber;
    private String campaigncode;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        setContentView(R.layout.activity_search);

        campaigncode = prefs.getString(getString(R.string.pref_code), "DEFAULT");

        etPhonenumber = (EditText) findViewById(R.id.search);

    }
    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0) {

        // Get text from user, password and Campaign Code fields.
        final String phone = etPhonenumber.getText().toString();
        final String code = campaigncode;
        String activity = prefs.getString("Activities", "DEFAULT");


        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(phone, code, activity);

    }

    @Override
    public void onBackPressed() {
        //your code when back button pressed
        Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
        startActivity(intent);
        SearchActivity.this.finish();
    }

    public void ExitButton(View arg0) {

        //your code when back button pressed
        Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
        startActivity(intent);
        SearchActivity.this.finish();
    }


    private class AsyncLogin extends AsyncTask<String, String, String>
    {

        ProgressDialog pdLoading = new ProgressDialog(SearchActivity.this,R.style.AppCompatAlertDialogStyle);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL( "https://voterreach.org/manager/cgi-bin/app/search.php");

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
                        .appendQueryParameter("phonenumber", params[0])
                        .appendQueryParameter("campaigncode", params[1])
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

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            pdLoading.dismiss();

            if (result.equalsIgnoreCase("false")){

                Toast.makeText(SearchActivity.this, "You connected but you have entered an invalid Phone Number.", Toast.LENGTH_LONG).show();


            }else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(SearchActivity.this, "The connection is slow. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }

            else {

                System.out.println(result);

                String delims = "[,]";
                String[] voter_data = result.split(delims);

                String voterid = voter_data[0];

                String fullname = voter_data[1] + " " + voter_data[2];

                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(getString(R.string.pref_voterid), voterid);
                edit.putString("FullName", fullname);
                edit.apply();

                Intent intent = new Intent(SearchActivity.this,SurveyActivity.class);
                startActivity(intent);
                SearchActivity.this.finish();


            }

        }
    }
}