package com.example.michaelh.fridgeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class ProductActivity extends ActionBarActivity {

    ProjectDataSource dataSource;
    String titel;
    String url;
    String expiry;
    String image;
    String description;

    Calendar c = Calendar.getInstance();

    final int actual_year = c.get(Calendar.YEAR);
    final int actual_month = c.get(Calendar.MONTH);
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



        TextView tbtitel = (TextView) findViewById(R.id.tbTitel);
        TextView tbdescr = (TextView) findViewById(R.id.tbDescr);
        TextView tbexp = (TextView) findViewById(R.id.tbExpDate);
        TextView tbinfo = (TextView) findViewById(R.id.tbInfo);
        TextView tbtime = (TextView) findViewById(R.id.tbTimeToGo);

        ImageView imageview = (ImageView) findViewById(R.id.imageProduct);


        String url_html = "<a href=\"" + url + "\">\nURL [codecheck.info]</a>";

        tbtitel.setMovementMethod(new ScrollingMovementMethod());
        tbdescr.setMovementMethod(new ScrollingMovementMethod());

        tbinfo.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        tbtitel.setText(Html.fromHtml(titel));
        tbdescr.setText(Html.fromHtml(description));
        tbexp.setText(expiry);
        tbinfo.setText(Html.fromHtml(url_html));

        if (actual_month < 10){
            leading_zero_month = "0";
        }
        if (actual_day < 10){
            leading_zero_day = "0";
        }

        final String actual_date = String.valueOf(actual_year) + "-" + leading_zero_month + String.valueOf(actual_month) + "-" + leading_zero_day + String.valueOf(actual_day);

        tbtime.setText(getDifference(actual_date, expiry));


        if(!(image.startsWith("http://www.codecheck.info/img/")))
        {
            image = "http://www.ffwhirschhorn.de/images/Einsatzabteilung/Noch_kein_Bild_vorhanden.jpg";
        }
            new DownloadImageTask(imageview).execute(image);



    }



    public String getDifference (String actual_date, String expiry_date){

        return "5 Tage";

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
