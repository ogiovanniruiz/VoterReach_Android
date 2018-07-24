package com.voterreach.ogruiz.voterreach;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.Date;


public class TextActivity extends AppCompatActivity{
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
        setContentView(R.layout.activity_texting);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String pbuuid = prefs.getString(getString(R.string.pref_pbuuid), "DEFAULT");
        String campaign_code = prefs.getString(getString(R.string.pref_code), "DEFAULT");
        script_link = prefs.getString("Script_link", "DEFAULT");
        String activity = prefs.getString("Activities", "DEFAULT");

        new RequestCall().execute(campaign_code, pbuuid, activity);
    }

    public void ExitButton(View arg0) {

        //your code when back button pressed
        Intent intent = new Intent(TextActivity.this, LoginActivity.class);
        startActivity(intent);
        TextActivity.this.finish();
    }

    public void SearchButton(View arg0) {

        //your code when back button pressed
        Intent intent = new Intent(TextActivity.this, SearchActivity.class);
        startActivity(intent);
        TextActivity.this.finish();
    }

    public void TextButton(View arg0) {

        Toast.makeText(TextActivity.this,  script_link, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        //your code when back button pressed
        Intent intent = new Intent(TextActivity.this, LoginActivity.class);
        startActivity(intent);
        TextActivity.this.finish();
    }


    private class RequestCall extends AsyncTask<String, String, String>{

        ProgressDialog pdLoading = new ProgressDialog(TextActivity.this,R.style.AppCompatAlertDialogStyle);
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
                url = new URL("https://voterreach.org/manager/cgi-bin/app/text.php");

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

                Toast.makeText(TextActivity.this, "All voters have been contacted.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(TextActivity.this, LoginActivity.class);
                startActivity(intent);
                TextActivity.this.finish();

            }

            else {

                String delims = "[,]";
                String[] voter_data = result.split(delims);

                final TextView voterNameTextView = (TextView) findViewById(R.id.voterName);

                final TextView voterCityTextView = (TextView) findViewById(R.id.voterCity);
                final TextView voterPartyTextView = (TextView) findViewById(R.id.voterParty);
                final TextView voterAgeTextView = (TextView) findViewById(R.id.voterAge);

                if (voter_data.length> 1) {

                    fullname = voter_data[0] + " " + voter_data[1];

                    voterNameTextView.setText(fullname);

                    voterphonenumber = voter_data[2];

                    voterid = voter_data[3];

                    voterCityTextView.setText(voter_data[6]);

                    voterPartyTextView.setText(voter_data[7]);

                    voterAgeTextView.setText(voter_data[8]);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("FullName", fullname);
                    edit.apply();

                } else{

                    Toast.makeText(TextActivity.this, "The connection is slow. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TextActivity.this, LoginActivity.class);
                    startActivity(intent);
                    TextActivity.this.finish();

                }
            }

        }
    }

    public void SendButton(View arg0) {

        Date currentTime = Calendar.getInstance().getTime();

        int hour = currentTime.getHours();

        if ((1==2) &&((hour >= 23) || (hour <= 9))){

            Toast.makeText(TextActivity.this, "It is after 11pm or before 9am. The Call function has been disabled.", Toast.LENGTH_LONG).show();
        }

        else{

            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(getString(R.string.pref_voterid), voterid);
            edit.apply();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

            } else {

                Intent intent = new Intent(TextActivity.this,TextActivity.class);
                startActivity(intent);

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(voterphonenumber, null, script_link, null, null);

                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "951-258-0698", null)));
                String prepare = "Text sent to " + fullname;
                Toast.makeText(TextActivity.this, prepare, Toast.LENGTH_LONG).show();
                TextActivity.this.finish();
            }

        }

    }

}


