package com.voterreach.ogruiz.voterreach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class LoginActivity extends AppCompatActivity{

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etCode;
    private String uniqueid;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Get Reference to variables
        uniqueid = prefs.getString(getString(R.string.pref_pbuuid), "DEFAULT");
        etCode = (EditText) findViewById(R.id.code);
    }

    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0) {

        // Get text from user, password and Campaign Code fields.
        final String code = etCode.getText().toString();
        final String id = uniqueid;

        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(getString(R.string.pref_code), code);
        edit.apply();

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(code, id);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {

        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
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
                url = new URL("http://voterreach.org/cgi-bin/login.php");

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
            //this method will be running on UI thread

            pdLoading.dismiss();

            String delims = "[,]";
            String[] response = result.split(delims, 2);

            if (result.equalsIgnoreCase("\uFEFFfalse")){

                // If username and password does not match display a error message
                Toast.makeText(LoginActivity.this, "You connected but have Invalid Campaign Code.", Toast.LENGTH_LONG).show();

            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(LoginActivity.this, "You have MASSIVE Connection Problems.", Toast.LENGTH_LONG).show();
            }

            else if (response[0].equalsIgnoreCase("\uFEFFtrue")){

                Intent intent = new Intent(LoginActivity.this,CallActivity.class);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Questions", response[1]);
                edit.apply();

                startActivity(intent);
                LoginActivity.this.finish();

            }
        }
    }
}