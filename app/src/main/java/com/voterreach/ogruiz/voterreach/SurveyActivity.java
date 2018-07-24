package com.voterreach.ogruiz.voterreach;

<<<<<<< HEAD
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
=======
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
<<<<<<< HEAD
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
=======
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import android.widget.Button;
import android.widget.ToggleButton;

import static java.lang.String.valueOf;


public class SurveyActivity extends AppCompatActivity{
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

<<<<<<< HEAD
    private String[] a = {"N","N","N","N","N"};
=======
    private String[] a = {"N","N","N","N"};
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
    private String noAnswer = "N";
    private String badNumber = "N";
    private String voter_id;
    private String[] type;
    private String[] question;
    private SeekBar[] seekers;
    private EditText[] text_answers;
    private TextView[] seekText;
<<<<<<< HEAD

    private String campaigncode;
    private String mode;
    private String activity;
    private String pbuuid;
    private ToggleButton noButton;
    private ToggleButton badButton;
    private ToggleButton buttons[];

    private String fullname;
=======
    private ToggleButton[] buttons;
    private String campaigncode;
    private String mode;
    private String activity;

    private String pbuuid;
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String questions = prefs.getString("Questions", "DEFAULT");
        String response_types = prefs.getString("Response_Type", "DEFAULT");

<<<<<<< HEAD
        campaigncode = prefs.getString(getString(R.string.pref_code), "DEFAULT");
=======
        campaigncode = prefs.getString("Code", "DEFAULT");
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        pbuuid = prefs.getString(getString(R.string.pref_pbuuid), "DEFAULT");
        mode = prefs.getString("Mode", "DEFAULT");
        activity = prefs.getString("Activities", "DEFAULT");

        String delims = "[,]";

        type = response_types.split(delims);
        question = questions.split(delims);

        buttons = new ToggleButton[question.length];
        seekers = new SeekBar[question.length];
        seekText = new TextView[question.length];
        text_answers = new EditText[question.length];

<<<<<<< HEAD
        noButton = (ToggleButton) findViewById(R.id.no);

        badButton = (ToggleButton) findViewById(R.id.bad);

        if (mode.equalsIgnoreCase("phonebank")) {

            noButton.setText("No Answer");
            badButton.setText("Bad Number");
        }else{
            noButton.setText("Not Home");
            badButton.setText("Bad Address");

        }


=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        final int BID= R.id.b1;
        final int SID= R.id.s1;
        final int QID = R.id.st1;
        final int TID = R.id.ti1;

        for (int i = 0; i < question.length; i = i + 1){

            if (type[i].equals("s")) {

                final int x = i;

                seekers[i] = (SeekBar) findViewById(SID + i);
                seekText[i] = (TextView) findViewById(QID + i);

                seekers[i].setVisibility(View.VISIBLE);
                seekText[i].setVisibility(View.VISIBLE);
                seekText[i].setText(question[i]);

                seekers[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        // TODO Auto-generated method stub

                        String scale_text = "";
                        if (progress == 0) {

                            scale_text = question[x] + ": ?";

                        } else {

                            scale_text = question[x] + ": " + String.valueOf(progress);
                        }

<<<<<<< HEAD
=======

>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
                        seekText[x].setText(scale_text);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }
                });

            } else if (type[i].equals("t")) {

                text_answers[i] = (EditText) findViewById(TID + i);
                text_answers[i].setVisibility(View.VISIBLE);
                text_answers[i].setHint(question[i]);

            }else if (type[i].equals("b")){

                buttons[i] = (ToggleButton) findViewById(BID + i);
                buttons[i].setText(question[i]);
<<<<<<< HEAD
=======
                buttons[i].setPadding(15, 0, 15, 0);
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
                buttons[i].setTextOn(null);
                buttons[i].setTextOff(null);
                buttons[i].setVisibility(View.VISIBLE);
            }
        }



        voter_id = prefs.getString(getString(R.string.pref_voterid), "DEFAULT");

<<<<<<< HEAD
        fullname = "Responses for "+ prefs.getString("FullName", "DEFAULT") + ":";
=======
        String fullname = "Responses for "+ prefs.getString("FullName", "DEFAULT") + ":";
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e

        final TextView voterNameTextView = (TextView) findViewById(R.id.voterName);
        voterNameTextView.setText(fullname);

<<<<<<< HEAD
        if (mode.equalsIgnoreCase("phonebank")) {

            String prepare = "Calling " + prefs.getString("FullName", "DEFAULT");
            Toast.makeText(SurveyActivity.this, prepare, Toast.LENGTH_LONG).show();

        }



=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
    }


    public void b1(View arg0) {


        if (a[0].equals("Y")) {

            a[0] = "N";
<<<<<<< HEAD

=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
        else
        {
            a[0] = "Y";
<<<<<<< HEAD

=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
    }

    public void b2(View arg0) {

        if (a[1].equals("Y")) {


            a[1] = "N";
<<<<<<< HEAD

=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
        else
        {

            a[1] = "Y";
<<<<<<< HEAD

=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
    }

    public void b3(View arg0) {



        if (a[2].equals("Y")) {

            a[2]= "N";
<<<<<<< HEAD


=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
        else
        {

            a[2] = "Y";
<<<<<<< HEAD

=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
    }

    public void b4(View arg0) {



        if (a[3].equals("Y")) {
            a[3] = "N";
<<<<<<< HEAD

=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
        else
        {
            a[3] = "Y";
<<<<<<< HEAD

        }
    }

    public void b5(View arg0) {



        if (a[4].equals("Y")) {
            a[4] = "N";

        }
        else
        {
            a[4] = "Y";

=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
    }

    public void noAnswer(View arg0) {

<<<<<<< HEAD
        if (mode.equalsIgnoreCase("phonebank")) {

            noButton.setText("No Answer");

        }else{

            noButton.setText("Not Home");

        }

        if (noAnswer.equals("Y")) {

            noAnswer = "N";

        }else{

            noAnswer = "Y";


=======
        Button button = (Button)findViewById(R.id.noAnswer);

        if (noAnswer.equals("Y")) {

            button.setBackgroundResource(R.drawable.noanswer);
            noAnswer = "N";
        }
        else
        {
            button.setBackgroundResource(R.drawable.noanswerblue);
            noAnswer = "Y";
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
    }

    public void badNumber(View arg0) {

<<<<<<< HEAD
        if (mode.equalsIgnoreCase("phonebank")) {
            badButton.setText("Bad Number");
        }else{
            badButton.setText("Bad Address");
        }

        if (badNumber.equals("Y")) {
            badNumber = "N";

        }
        else
        {

            badNumber = "Y";

=======
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
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
    }

    @Override
    public void onBackPressed() {
        //your code when back button pressed

        if (mode.equals("phonebank")) {
            Intent intent = new Intent(SurveyActivity.this, CallActivity.class);
            startActivity(intent);
        }else if (mode.equals("canvas")) {
            Intent intent = new Intent(SurveyActivity.this, CanvasActivity.class);
            startActivity(intent);
<<<<<<< HEAD
        }else if (mode.equals("texting")) {
            Intent intent = new Intent(SurveyActivity.this, SearchActivity.class);
            startActivity(intent);
=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }

        SurveyActivity.this.finish();
    }

    public void checkSave(View arg0) {
        Date currentTime = Calendar.getInstance().getTime();

        String dateTime = currentTime.toString();

        for (int i = 0; i < question.length; i = i + 1) {
            if (type[i].equals("s")) {
<<<<<<< HEAD

                a[i] = valueOf(seekers[i].getProgress());
                //if (seekers[i].getProgress() == 0){

                 //   a[i] = "0";

               // }else {
                //    a[i] = valueOf(seekers[i].getProgress());
               // }
            }else if (type[i].equals("t")){
                a[i] = text_answers[i].getText().toString().replace("0", "").replace("'", "");
                //a[i] = text_answers[i].getText().toString();
=======
                if (seekers[i].getProgress() == 0){

                    a[i] = "";

                }else {
                    a[i] = valueOf(seekers[i].getProgress());
                }
            }else if (type[i].equals("t")){
                a[i] = text_answers[i].getText().toString();
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e

            }
        }

        if (noAnswer.equals(badNumber) && noAnswer.equals("Y")) {

            Toast.makeText(SurveyActivity.this, "Invalid Survey Response.", Toast.LENGTH_LONG).show();

        }else if ((Arrays.asList(a).contains("Y") ||
                   Arrays.asList(a).contains("1") ||
                   Arrays.asList(a).contains("2") ||
                   Arrays.asList(a).contains("3") ||
                   Arrays.asList(a).contains("4") ||
                   Arrays.asList(a).contains("5") ||
                   Arrays.asList(a).contains("6")) && (noAnswer.equals("Y") || badNumber.equals("Y"))){

            Toast.makeText(SurveyActivity.this, "Invalid Survey Response.", Toast.LENGTH_LONG).show();

        }
<<<<<<< HEAD

        else if ((Arrays.asList(question).contains("Refused") || Arrays.asList(question).contains("Spanish"))){

            if (a[0].equals("0") && a[3].equals("N") && a[4].equals("N")  && noAnswer.equals("N") && badNumber.equals("N")){

                Toast.makeText(SurveyActivity.this, "Scale Response Required.", Toast.LENGTH_LONG).show();

            } else{

                new SaveData().execute(a[0], a[1], a[2], a[3], a[4],noAnswer, badNumber, voter_id, dateTime, campaigncode, pbuuid, mode, activity);

            }

    }
        else{
            new SaveData().execute(a[0], a[1], a[2], a[3], a[4],noAnswer, badNumber, voter_id, dateTime, campaigncode, pbuuid, mode, activity);
=======
        else{
            new SaveData().execute(a[0], a[1], a[2], a[3], noAnswer, badNumber, voter_id, dateTime, campaigncode, pbuuid, mode, activity);
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
        }
    }

    private class SaveData extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(SurveyActivity.this,R.style.AppCompatAlertDialogStyle);
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
<<<<<<< HEAD
                url = new URL("https://voterreach.org/manager/cgi-bin/app/survey.php");
=======
                url = new URL("https://voterreach.org/cgi-bin/survey.php");
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e

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
<<<<<<< HEAD
                        .appendQueryParameter("a5", params[4])
                        .appendQueryParameter("no", params[5])
                        .appendQueryParameter("bad", params[6])
                        .appendQueryParameter("voteruuid", params[7])
                        .appendQueryParameter("calldate", params[8])
                        .appendQueryParameter("campaigncode", params[9])
                        .appendQueryParameter("pbuuid", params[10])
                        .appendQueryParameter("mode", params[11])
                        .appendQueryParameter("activity", params[12]);
=======
                        .appendQueryParameter("no", params[4])
                        .appendQueryParameter("bad", params[5])
                        .appendQueryParameter("voteruuid", params[6])
                        .appendQueryParameter("calldate", params[7])
                        .appendQueryParameter("campaigncode", params[8])
                        .appendQueryParameter("pbuuid", params[9])
                        .appendQueryParameter("mode", params[10])
                        .appendQueryParameter("activity", params[11]);
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e

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

<<<<<<< HEAD
            if(result.equalsIgnoreCase("true")) {
=======
            if(result.equalsIgnoreCase("\uFEFFtrue")) {
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e

                if (mode.equals("phonebank")) {

                    Intent intent = new Intent(SurveyActivity.this, CallActivity.class);
                    startActivity(intent);
                    SurveyActivity.this.finish();
                }else if (mode.equals("canvas")){

                    Intent intent = new Intent(SurveyActivity.this, CanvasActivity.class);
                    startActivity(intent);
                    SurveyActivity.this.finish();
<<<<<<< HEAD
                }else if (mode.equals("texting")){

                    Intent intent = new Intent(SurveyActivity.this, SearchActivity.class);
                    startActivity(intent);
                    SurveyActivity.this.finish();
=======
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
                }
            }

            else{

                Toast.makeText(SurveyActivity.this, "Connection is slow. Please try again in a few seconds.", Toast.LENGTH_LONG).show();
            }
        }
<<<<<<< HEAD

    }

=======
    }
>>>>>>> d1b255f2ee8a88f2a386e407553958a525ed0b6e
}
