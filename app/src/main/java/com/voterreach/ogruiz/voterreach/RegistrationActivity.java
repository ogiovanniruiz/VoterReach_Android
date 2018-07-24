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


public class RegistrationActivity extends AppCompatActivity{

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etUserName;
    private String uniqueid;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);

        if (previouslyStarted){

            Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
            startActivity(intent);
            RegistrationActivity.this.finish();

        }
        else{

            setContentView(R.layout.activity_registration);

            uniqueid = UUID.randomUUID().toString();
            etUserName = (EditText) findViewById(R.id.username);
        }




    }
    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0) {

        // Get text from user, password and Campaign Code fields.
        final String code = etUserName.getText().toString();
        final String id = uniqueid;

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(id, code);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {

        ProgressDialog pdLoading = new ProgressDialog(RegistrationActivity.this,R.style.AppCompatAlertDialogStyle);
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
                url = new URL( "https://voterreach.org/manager/cgi-bin/app/register.php");

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
                        .appendQueryParameter("pbuuid", params[0])
                        .appendQueryParameter("pbname", params[1]);

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

            System.out.println(result);

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.*/

                System.out.println(result);

                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
                edit.putString(getString(R.string.pref_pbuuid), uniqueid);
                edit.apply();

                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
                RegistrationActivity.this.finish();
            }
            else if (result.equalsIgnoreCase("false")){

                Toast.makeText(RegistrationActivity.this, "You connected but there was an issue with the database.", Toast.LENGTH_LONG).show();

            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(RegistrationActivity.this, "There was problem with the connection.", Toast.LENGTH_LONG).show();
            }
        }
    }
}