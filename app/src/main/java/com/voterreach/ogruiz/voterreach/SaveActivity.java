package com.voterreach.ogruiz.voterreach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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



public class SaveActivity extends AppCompatActivity{
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private String isVoting = "N";
    private String isDonating = "N";
    private String isParty = "N";
    private String isCanvass = "N";
    private String noAnswer = "N";
    private String badNumber = "N";
    private String contacted = "N";
    private String primary_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        primary_id = getIntent().getStringExtra("PRIMARY_ID");

    }

    public void checkVote(View arg0) {

        if (isVoting.equals("Y")) {
            isVoting = "N";
        }
        else
        {
            isVoting = "Y";
        }
    }

    public void checkParty(View arg0) {
        if (isParty.equals("Y")) {
            isParty = "N";
        }
        else
        {
            isParty = "Y";
        }
    }

    public void checkCanvass(View arg0) {
        if (isCanvass.equals("Y")) {
            isCanvass= "N";
        }
        else
        {
            isCanvass = "Y";
        }
    }

    public void checkDonate(View arg0) {
        if (isDonating.equals("Y")) {
            isDonating = "N";
        }
        else
        {
            isDonating = "Y";
        }
    }

    public void noAnswer(View arg0) {
        if (noAnswer.equals("Y")) {
            noAnswer = "N";
        }
        else
        {
            noAnswer = "Y";
        }
    }

    public void badNumber(View arg0) {
        if (badNumber.equals("Y")) {
            badNumber = "N";
        }
        else
        {
            badNumber = "Y";
        }
    }

    public void contacted(View arg0) {
        if (contacted.equals("Y")) {
            contacted = "N";
        }
        else
        {
            contacted = "Y";
        }
    }

    public void checkSave(View arg0) {

        new SaveData().execute(isVoting, isDonating, isParty, isCanvass, noAnswer, badNumber, contacted, primary_id);
    }

    private class SaveData extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(SaveActivity.this);
        HttpURLConnection conn;
        URL url = null;


        protected void onPreExecute() {

            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tSaving to Database...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://epicentermagazine.org/1234.response.write.php");


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
                        .appendQueryParameter("voting", params[0])
                        .appendQueryParameter("donate", params[1])
                        .appendQueryParameter("host_party", params[2])
                        .appendQueryParameter("canvass", params[3])
                        .appendQueryParameter("no_answer", params[4])
                        .appendQueryParameter("bad_number", params[5])
                        .appendQueryParameter("contacted", params[6])
                        .appendQueryParameter("v1_primaryid", params[7]);

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

        protected void onPostExecute(String result){
            pdLoading.dismiss();

            Toast.makeText(SaveActivity.this, result, Toast.LENGTH_LONG).show();

            if(result.equalsIgnoreCase("successful")) {

                Intent intent = new Intent(SaveActivity.this, CallActivity.class);
                startActivity(intent);
                SaveActivity.this.finish();
            }

            else{

                Toast.makeText(SaveActivity.this, "Nope.", Toast.LENGTH_LONG).show();
            }
        }

    }

}