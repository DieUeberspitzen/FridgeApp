package com.example.michaelh.fridgeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class ProductActivity extends ActionBarActivity {

    ProjectDataSource dataSource;
    String titel;
    String url;
    String expiry;
    String image;
    String description;
    long id;

    Calendar c = Calendar.getInstance();

    final int actual_year = c.get(Calendar.YEAR);
    final int actual_month = c.get(Calendar.MONTH) + 1;
    final int actual_day = c.get(Calendar.DAY_OF_MONTH);

    String leading_zero_month = "";
    String leading_zero_day = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Intent i = getIntent();

        this.titel = i.getStringExtra("titel");
        this.expiry = i.getStringExtra("expiry");
        this.description = i.getStringExtra("description");
        this.image = i.getStringExtra("image");
        this.url = i.getStringExtra("url");
        this.id = i.getLongExtra("id", 0);
        dataSource = MainActivity.dataSource;



        TextView tbtitel = (TextView) findViewById(R.id.tbTitel);
        TextView tbdescr = (TextView) findViewById(R.id.tbDescr);
        TextView tbexp = (TextView) findViewById(R.id.tbExpDate);
        TextView tbinfo = (TextView) findViewById(R.id.tbInfo);
        TextView tbtime = (TextView) findViewById(R.id.tbTimeToGo);

        ImageView imageview = (ImageView) findViewById(R.id.imageProduct);


        String url_html = "<a href=\"" + url + "\">\nURL [codecheck.info]</a>";

        tbtitel.setMovementMethod(new ScrollingMovementMethod());
        tbdescr.setMovementMethod(new ScrollingMovementMethod());
        tbinfo.setMovementMethod(new ScrollingMovementMethod());


        tbinfo.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());


        ArrayList<String> new_date_format = new ArrayList<String>(Arrays.asList(expiry.split("-")));

        String expiry_new_format = new_date_format.get(2) + "." + new_date_format.get(1) + "." + new_date_format.get(0);

        tbtitel.setText(Html.fromHtml(titel));
        tbdescr.setText(Html.fromHtml(description));
        tbexp.setText(expiry_new_format);
        tbinfo.setText(Html.fromHtml(url_html));

        if (actual_month < 10){
            leading_zero_month = "0";
        }
        if (actual_day < 10){
            leading_zero_day = "0";
        }

        final String actual_date = String.valueOf(actual_year) + "-" + leading_zero_month + String.valueOf(actual_month) + "-" + leading_zero_day + String.valueOf(actual_day);

        String until_expiry_in_days = getDifference(actual_date, expiry);

        ArrayList<String> positive_days = new ArrayList<String>(Arrays.asList(until_expiry_in_days.split(" ")));

        if (until_expiry_in_days.startsWith("-")){
            tbtime.setTextColor(Color.parseColor("#ff0000"));
        }
        else if (Integer.parseInt(positive_days.get(0)) < 3){
            tbtime.setTextColor(Color.parseColor("#eea114"));
        }
        else {
            tbtime.setTextColor(Color.parseColor("#1eb61e"));
        }



        tbtime.setText(until_expiry_in_days);

        if(!(image.startsWith("http://www.codecheck.info/img/")))
        {
            image = "http://www.ffwhirschhorn.de/images/Einsatzabteilung/Noch_kein_Bild_vorhanden.jpg";
        }
            new DownloadImageTask(imageview).execute(image);


        Button deleteButton = (Button)findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(ProductActivity.this);
                myAlertDialog.setTitle("Sind Sie sicher?");
                myAlertDialog.setMessage(Html.fromHtml(titel) + " wirklich löschen?");
                myAlertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something when the OK button is clicked

                        dataSource.deleteProduct(id);
                        //finish();
                        Intent it = new Intent();
                        it.setClass(ProductActivity.this, MainActivity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(it);
                    }
                });
                myAlertDialog.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something when the Cancel button is clicked
                    }
                });
                myAlertDialog.show();


            }


        });



    }



    public String getDifference (String actual_date, String expiry_date){

        ArrayList<String> actual_date_parts = new ArrayList<String>(Arrays.asList(actual_date.split("-")));
        ArrayList<String> expiry_date_parts = new ArrayList<String>(Arrays.asList(expiry_date.split("-")));

        Calendar act = Calendar.getInstance();
        act.clear();

        act.set(Calendar.YEAR, Integer.parseInt(actual_date_parts.get(0)));
        act.set(Calendar.MONTH, Integer.parseInt(actual_date_parts.get(1)));
        act.set(Calendar.DATE, Integer.parseInt(actual_date_parts.get(2)));

        long absolute_actual_date = act.getTimeInMillis();


        Calendar exp = Calendar.getInstance();
        exp.clear();

        exp.set(Calendar.YEAR, Integer.parseInt(expiry_date_parts.get(0)));
        exp.set(Calendar.MONTH, Integer.parseInt(expiry_date_parts.get(1)));
        exp.set(Calendar.DATE, Integer.parseInt(expiry_date_parts.get(2)));

        long absolute_expiry_date = exp.getTimeInMillis();

        // let's assume the actual date is before the expiry date
        long time_diff_in_millis = absolute_expiry_date - absolute_actual_date;
        long time_diff_in_days = time_diff_in_millis / 1000 / 60 / 60 / 24;

        return Long.toString(time_diff_in_days);

    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */


        return super.onOptionsItemSelected(item);
    }
}
