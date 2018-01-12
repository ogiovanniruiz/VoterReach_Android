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


public class SurveyActivity extends AppCompatActivity{
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private String a1 = "N";
    private String a2= "N";
    private String a3 = "N";
    private String a4 = "N";
    private String a5 = "N";
    private String a6 = "N";
    private String a7 = "N";
    private String noAnswer = "N";
    private String badNumber = "N";
    private String dateTime = "";
    private String voter_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String questions = prefs.getString("Questions", "DEFAULT");

        String delims = "[,]";
        String[] question = questions.split(delims);

        Button[] buttons = new Button[question.length];

        final int ID= R.id.b1;

        for (int i = 0; i < question.length; i = i + 1){

            buttons[i] = (Button)findViewById(ID + i);
            buttons[i].setText(question[i]);
            buttons[i].setPadding(0,0,0,0);

        }

        voter_id = prefs.getString(getString(R.string.pref_voterid), "DEFAULT");

        System.out.println(voter_id);

    }

    public void b1(View arg0) {

        if (a1.equals("Y")) {
            a1 = "N";
        }
        else
        {
            a1 = "Y";
        }
    }

    public void b2(View arg0) {
        if (a2.equals("Y")) {
            a2 = "N";
        }
        else
        {
            a2 = "Y";
        }
    }

    public void b3(View arg0) {
        if (a3.equals("Y")) {
            a3= "N";
        }
        else
        {
            a3 = "Y";
        }
    }

    public void b4(View arg0) {
        if (a4.equals("Y")) {
            a4 = "N";
        }
        else
        {
            a4 = "Y";
        }
    }

    public void b5(View arg0) {
        if (a5.equals("Y")) {
            a5 = "N";
        }
        else
        {
            a5 = "Y";
        }
    }

    public void b6(View arg0) {
        if (a6.equals("Y")) {
            a6 = "N";
        }
        else
        {
            a6 = "Y";
        }
    }

    public void b7(View arg0) {
        if (a7.equals("Y")) {
            a7 = "N";
        }
        else
        {
            a7 = "Y";
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


    public void checkSave(View arg0) {
        Date currentTime = Calendar.getInstance().getTime();

        dateTime = currentTime.toString();

        new SaveData().execute(a1, a2, a3, a4, a5, a6, a7, noAnswer, badNumber, voter_id, dateTime);
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
                url = new URL("http://voterreach.org/cgi-bin/survey.php");

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
                        .appendQueryParameter("a5", params[4])
                        .appendQueryParameter("a6", params[5])
                        .appendQueryParameter("a7", params[6])
                        .appendQueryParameter("noanswer", params[7])
                        .appendQueryParameter("badnumber", params[8])
                        .appendQueryParameter("voteruuid", params[9])
                        .appendQueryParameter("calldate", params[10]);

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

            //Toast.makeText(SurveyActivity.this, currentTime.toString(), Toast.LENGTH_LONG).show();

            if(result.equalsIgnoreCase("\uFEFFtrue")) {


                Intent intent = new Intent(SurveyActivity.this, CallActivity.class);
                startActivity(intent);
                SurveyActivity.this.finish();
            }

            else{

                Toast.makeText(SurveyActivity.this, "Nope.", Toast.LENGTH_LONG).show();
            }
        }

    }

}
