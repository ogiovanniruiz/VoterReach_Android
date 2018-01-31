package com.voterreach.ogruiz.voterreach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
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
import android.widget.Button;
import android.widget.ToggleButton;

import static java.lang.String.valueOf;


public class SurveyActivity extends AppCompatActivity{
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private String[] a = {"N","N","N","N"};
    private String noAnswer = "N";
    private String badNumber = "N";
    private String voter_id;
    private String[] type;
    private String[] question;
    private SeekBar[] seekers;
    private ToggleButton[] buttons;
    private String campaigncode;

    private String pbuuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String questions = prefs.getString("Questions", "DEFAULT");

        String response_types = prefs.getString("Response_Type", "DEFAULT");

        campaigncode = prefs.getString(getString(R.string.pref_code), "DEFAULT");

        pbuuid = prefs.getString(getString(R.string.pref_pbuuid), "DEFAULT");

        String delims = "[,]";

        type = response_types.split(delims);
        question = questions.split(delims);

        buttons = new ToggleButton[question.length];
        seekers = new SeekBar[question.length];

        final int ID= R.id.b1;
        final int PID= R.id.s1;

        for (int i = 0; i < question.length; i = i + 1){

            seekers[i] = (SeekBar) findViewById(PID + i);

            buttons[i] = (ToggleButton)findViewById(ID + i);
            buttons[i].setText(question[i]);
            buttons[i].setPadding(15,0,15,0);
            buttons[i].setTextOn(null);
            buttons[i].setTextOff(null);
            buttons[i].setVisibility(View.VISIBLE);
        }

        voter_id = prefs.getString(getString(R.string.pref_voterid), "DEFAULT");

    }

    public void b1(View arg0) {

        if (type[0].equals("s")) {
            buttons[0].setVisibility(View.GONE);

            seekers[0].setVisibility(View.VISIBLE);

            TextView t = (TextView) findViewById(R.id.st1);
            t.setVisibility(View.VISIBLE);
        }

        if (a[0].equals("Y")) {

            a[0] = "N";
        }
        else
        {
            a[0] = "Y";
        }
    }

    public void b2(View arg0) {

        if (type[1].equals("s")) {

            buttons[1].setVisibility(View.GONE);
            seekers[1].setVisibility(View.VISIBLE);

            TextView t = (TextView) findViewById(R.id.st2);
            t.setVisibility(View.VISIBLE);
        }

        if (a[1].equals("Y")) {


            a[1] = "N";
        }
        else
        {

            a[1] = "Y";
        }
    }

    public void b3(View arg0) {

        if (type[2].equals("s")) {

            buttons[2].setVisibility(View.GONE);
            seekers[2].setVisibility(View.VISIBLE);

            TextView t = (TextView) findViewById(R.id.st3);
            t.setVisibility(View.VISIBLE);
        }

        if (a[2].equals("Y")) {

            a[2]= "N";
        }
        else
        {

            a[2] = "Y";
        }
    }

    public void b4(View arg0) {

        if (type[3].equals("s")) {

            buttons[3].setVisibility(View.GONE);
            seekers[3].setVisibility(View.VISIBLE);

            TextView t = (TextView) findViewById(R.id.st4);
            t.setVisibility(View.VISIBLE);

        }

        if (a[3].equals("Y")) {
            a[3] = "N";
        }
        else
        {
            a[3] = "Y";
        }
    }

    public void noAnswer(View arg0) {

        Button button = (Button)findViewById(R.id.noAnswer);

        if (noAnswer.equals("Y")) {

            button.setBackgroundResource(R.drawable.noanswer);
            noAnswer = "N";
        }
        else
        {
            button.setBackgroundResource(R.drawable.noanswerblue);
            noAnswer = "Y";
        }
    }

    public void badNumber(View arg0) {

        Button button = (ToggleButton)findViewById(R.id.badNumber);

        if (badNumber.equals("Y")) {
            button.setBackgroundResource(R.drawable.bad);
            badNumber = "N";
        }
        else
        {
            button.setTextColor(Color.parseColor("#fdfdfd"));
            button.setBackgroundResource(R.drawable.badblue);
            badNumber = "Y";
        }
    }

    @Override
    public void onBackPressed() {
        //your code when back button pressed
        Intent intent = new Intent(SurveyActivity.this, CallActivity.class);
        startActivity(intent);
        SurveyActivity.this.finish();
    }

    public void checkSave(View arg0) {
        Date currentTime = Calendar.getInstance().getTime();

        String dateTime = currentTime.toString();

        for (int i = 0; i < question.length; i = i + 1) {
            if (type[i].equals("s")) {
                a[i] = valueOf(seekers[i].getProgress() + 1);
            }
        }

        new SaveData().execute(a[0], a[1], a[2], a[3], noAnswer, badNumber, voter_id, dateTime, campaigncode, pbuuid);
    }

    private class SaveData extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(SurveyActivity.this);
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
                url = new URL("https://voterreach.org/cgi-bin/survey.php");

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
                        .appendQueryParameter("a1", params[0])
                        .appendQueryParameter("a2", params[1])
                        .appendQueryParameter("a3", params[2])
                        .appendQueryParameter("a4", params[3])
                        .appendQueryParameter("noanswer", params[4])
                        .appendQueryParameter("badnumber", params[5])
                        .appendQueryParameter("voteruuid", params[6])
                        .appendQueryParameter("calldate", params[7])
                        .appendQueryParameter("campaigncode", params[8])
                        .appendQueryParameter("pbuuid", params[9]);

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

            if(result.equalsIgnoreCase("\uFEFFtrue")) {


                Intent intent = new Intent(SurveyActivity.this, CallActivity.class);
                startActivity(intent);
                SurveyActivity.this.finish();
            }

            else{

                Toast.makeText(SurveyActivity.this, "A problem with remote server has occured.", Toast.LENGTH_LONG).show();
            }
        }

    }

}
