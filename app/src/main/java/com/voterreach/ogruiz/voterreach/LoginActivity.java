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
    private String mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Get Reference to variables
        uniqueid = prefs.getString(getString(R.string.pref_pbuuid), "DEFAULT");
        etCode = (EditText) findViewById(R.id.campaigncode);
    }

    // Triggers when LOGIN Button clicked            Toast.makeText(SurveyActivity.this, result, Toast.LENGTH_LONG).show();
    public void checkLoginPhoneBank(View arg0) {

        // Get text from user, password and Campaign Code fields.
        final String code = etCode.getText().toString();
        final String id = uniqueid;

        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(getString(R.string.pref_code), code);
        edit.apply();

        mode = "phonebank";

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(code, id, mode);

    }

    // Triggers when LOGIN Button clicked
    public void checkLoginCanvas(View arg0) {
        // Get text from user, password and Campaign Code fields.
        final String code = etCode.getText().toString();
        final String id = uniqueid;

        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(getString(R.string.pref_code), code);
        edit.apply();

        mode = "canvas";
        new AsyncLogin().execute(code, id, mode);

    }

    // Triggers when LOGIN Button clicked
    public void checkLoginTexting(View arg0) {
        // Get text from user, password and Campaign Code fields.
        final String code = etCode.getText().toString();
        final String id = uniqueid;

        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(getString(R.string.pref_code), code);
        edit.apply();

        mode = "texting";
        new AsyncLogin().execute(code, id, mode);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {

        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this,R.style.AppCompatAlertDialogStyle);
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
                url = new URL("https://voterreach.org/manager/cgi-bin/app/login.php");

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
                        .appendQueryParameter("mode", params[2]);

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
            String[] response = result.split(delims);

            if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(LoginActivity.this, "You have connected but have an invalid Campaign Code.", Toast.LENGTH_LONG).show();

            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(LoginActivity.this, "Connection is slow. Please try again in a few seconds.", Toast.LENGTH_LONG).show();
            }

            else if (result.equalsIgnoreCase("Blocked"))
            {
                Toast.makeText(LoginActivity.this, "Could not login. Please contact your campaign manager.", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("None"))
            {
                Toast.makeText(LoginActivity.this, "Activities of this type are not live on this Campaign Code.", Toast.LENGTH_LONG).show();
            }

            else if (response[0].equalsIgnoreCase("true")){

                StringBuilder output = new StringBuilder("");

                for (int i = 1; i < response.length; i = i + 1) {


                    output.append(response[i]);
                    output.append(",");
                }

                String new_output = output.substring(0, output.length() - 1);

                Intent intent;

                intent = new Intent(LoginActivity.this,ChooseActivity.class);
                startActivity(intent);

                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Activities", new_output);
                edit.putString("Mode", mode);
                edit.apply();

                LoginActivity.this.finish();

            }
        }
    }
}