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
import android.widget.Button;
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


public class ChooseActivity extends AppCompatActivity{

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private SharedPreferences prefs;
    private String code;
    private String[] activities;
    private String mode;
    private String activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        code = prefs.getString("Code", "DEFAULT");
        mode = prefs.getString("Mode", "DEFAULT");
        String activities_string = prefs.getString("Activities", "DEFAULT");

        String delims = "[,]";
        activities = activities_string.split(delims);

        Button[] buttons = new Button[activities.length];
        final int BID= R.id.ab1;

        for (int i = 0; i < activities.length; i = i + 1){
            buttons[i] = (Button) findViewById(BID + i);
            buttons[i].setVisibility(View.VISIBLE);
            buttons[i].setText(activities[i]);

        }
    }


    // Triggers when LOGIN Button clicked
    public void first_activity(View arg0) {

        activity = activities[0];

        new AsyncLogin().execute(code, activity);

    }

    // Triggers when LOGIN Button clicked
    public void second_activity(View arg0) {

        activity = activities[1];
        new AsyncLogin().execute(code, activity);

    }

    // Triggers when LOGIN Button clicked
    public void third_activity(View arg0) {

        activity = activities[2];
        new AsyncLogin().execute(code, activity);

    }

    // Triggers when LOGIN Button clicked
    public void fourth_activity(View arg0) {

        activity = activities[3];
        new AsyncLogin().execute(code, activity);

    }

    // Triggers when LOGIN Button clicked
    public void fifth_activity(View arg0) {

        activity = activities[4];
        new AsyncLogin().execute(code, activity);

    }

    // Triggers when LOGIN Button clicked
    public void sixth_activity(View arg0) {

        activity = activities[5];
        new AsyncLogin().execute(code, activity);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {

        ProgressDialog pdLoading = new ProgressDialog(ChooseActivity.this,R.style.AppCompatAlertDialogStyle);
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
                url = new URL("https://voterreach.org/cgi-bin/activity.php");

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
                        .appendQueryParameter("activity", params[1]);

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

            if (result.equalsIgnoreCase("\uFEFFfalse")){

                // If username and password does not match display a error message
                Toast.makeText(ChooseActivity.this, "You have connected but have an invalid Campaign Code.", Toast.LENGTH_LONG).show();

            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(ChooseActivity.this, "Connection is slow. Please try again in a few seconds.", Toast.LENGTH_LONG).show();
            }
            else if (response[0].equalsIgnoreCase("\uFEFFtrue")){

                String questions = response[1] + "," + response[2] + "," + response[3] +"," + response[4];

                String script_link = response[5];

                String response_types = response[6] + "," + response[7] + "," + response[8] +"," + response[9];

                Intent intent;

                if (mode.equalsIgnoreCase("canvas")){

                    intent = new Intent(ChooseActivity.this,CanvasActivity.class);
                    startActivity(intent);
                }else if (mode.equalsIgnoreCase("phonebank")){

                    intent = new Intent(ChooseActivity.this,CallActivity.class);
                    startActivity(intent);
                }

                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Questions", questions);
                edit.putString("Script_link", script_link);
                edit.putString("Response_Type", response_types);
                edit.putString("Activities", activity);
                edit.apply();

                ChooseActivity.this.finish();

            }
        }
    }

    @Override
    public void onBackPressed() {
        //your code when back button pressed

        Intent intent = new Intent(ChooseActivity.this, LoginActivity.class);
        startActivity(intent);

        ChooseActivity.this.finish();
    }
}