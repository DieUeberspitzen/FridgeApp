package com.example.michaelh.fridgeapp;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import android.content.Intent;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import android.app.PendingIntent;


import static com.example.michaelh.fridgeapp.Constants.FIRST_COLUMN;
import static com.example.michaelh.fridgeapp.Constants.SECOND_COLUMN;


public class MainActivity extends ActionBarActivity  {
    private Product product;
    private ArrayList<HashMap<String,String>> list;
    private ArrayList<Product> products = new ArrayList<Product>();
    ProjectDataSource dataSource;


    final String url_start = "http://www.codecheck.info/product.search?q=";
    final String url_end = "&OK=Suchen";

    String barcode = "";

    String titel = "";
    String title_for_list = "";
    String description_for_list = "";
    String image_for_list = "";
    String url_for_list = "";
    String expiry_date = "";

    Calendar c = Calendar.getInstance();
    int startYear;
    int startMonth;
    int startDay;

    int number_of_items_soon_expire = -1;

    ProgressDialog mProgressDialog;
    static MainActivity ma;


    private PendingIntent pendingIntent;

    ListView listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       ma = this;
        listview = (ListView) findViewById(R.id.listview);

        dataSource = new ProjectDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ProductToList(dataSource.getProducts());
        ListViewAdapter adapter = new ListViewAdapter(this,list);
        listview.setAdapter(adapter);


        Button scanButton = (Button)findViewById(R.id.scan_button);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.addExtra("SCAN_WIDTH", 640);
                integrator.addExtra("SCAN_HEIGHT", 480);
                integrator.addExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
                //customize the prompt message before scanning
                integrator.addExtra("PROMPT_MESSAGE", "Scannen Sie das gewünschte Produkt!");
                integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
            }


        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent i = new Intent();
                HashMap<String, String> temp = (HashMap<String, String>) parent.getItemAtPosition(position);
                titel = temp.get(FIRST_COLUMN);
                expiry_date = temp.get(SECOND_COLUMN);
                //System.out.print("ersd" + titel + "\n");
                i.setClass(MainActivity.this, ProductActivity.class);
                i.putExtra("titel", titel);
                Product prod = dataSource.getProduct(titel, expiry_date);
                i.putExtra("url", prod.get_url());
                i.putExtra("expiry", prod.get_expiry());
                i.putExtra("image", prod.get_image());
                i.putExtra("description", prod.getDescription());

                startActivity(i);

            }
        });


        /*
        Calendar calendar = Calendar.getInstance();

        // we can set time by open date and time picker dialog

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        //setSoonExpire();


        Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);
        intent1.putExtra("number_of_soon_exp", Integer.toString(number_of_items_soon_expire + 1));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MainActivity.this
                .getSystemService(MainActivity.this.ALARM_SERVICE);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        //        AlarmManager.INTERVAL_DAY, pendingIntent);

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        */


        setSoonExpire();
        if (number_of_items_soon_expire > 0) Notification();

    }



    public void ProductToList(ArrayList<Product> listproduct) {
       list = new ArrayList<HashMap<String, String>>();

        for(Product product: listproduct) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put(FIRST_COLUMN, product.getTitle());
            temp.put(SECOND_COLUMN, product.get_expiry());


            list.add(temp);
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (result != null) {

            String contents = result.getContents();
            barcode = contents;

            if (contents != null) new GetDataOnline().execute();

        }
    }


    public void WriteList(){


        if (barcode != null) {

            if(title_for_list.startsWith("Es wurde kein Produkt") || title_for_list == null || title_for_list.startsWith("<"))
            {

                Context context = getApplicationContext();
                CharSequence text = "Es wurden kein Resultat gefunden.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();


                //title_for_list = "<find nix..sry>";
            }
            else {
                DialogFragment dialogFragment = new StartDatePicker();
                dialogFragment.show(getFragmentManager(), "start_date_picker");



            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_sort) {

            System.out.println("\n\nsettings\n\n");
            return true;
        }
        */


        return super.onOptionsItemSelected(item);
    }

   public void setTitel(String titel_loc)
   {
       titel  = titel_loc;
   }

    public String getTitel()
    {
        return titel;
    }


    // Title AsyncTask
    public class GetDataOnline extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Produktinformationen");
            mProgressDialog.setMessage("Suche Produkt ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();

            }


        @Override
        protected Void doInBackground(Void  ... params) {
            try {
                url_for_list = url_start + barcode + url_end;

                // Connect to the web site
                Document document = Jsoup.connect(url_for_list).get();
                // Get the html document elements
                Elements title = document.select("h1");
                Elements description = document.select("h3[class=page-title-subline]");
                Elements image = document.select("meta[property=og:image]");
                Elements url = document.select("meta[property=og:url]");

                // get urls
                title_for_list = title.html();
                description_for_list = description.html();
                image_for_list = image.attr("content");
                url_for_list = url.attr("content");


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.dismiss();

            WriteList();
        }

    }

     @SuppressLint("ValidFragment")
     class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{



         @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            // Use the current date as the default date in the picker

             startYear = c.get(Calendar.YEAR);
             startMonth = c.get(Calendar.MONTH);
             startDay = c.get(Calendar.DAY_OF_MONTH);

             DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, this, startYear, startMonth, startDay);

             dialog.getDatePicker().setCalendarViewShown(false);
             dialog.getDatePicker().setSpinnersShown(true);

             dialog.setTitle("Ablaufdatum des Produkts:");

             return dialog;

        }
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // Do something with the date chosen by the user
            startYear = year;
            startMonth = monthOfYear + 1;
            startDay = dayOfMonth;
            updateStartDateDisplay();
        }


    }

    private void updateStartDateDisplay() {
        final ListView listview = (ListView) findViewById(R.id.listview);
        String leading_zero_month = "";
        String leading_zero_day = "";
        if (startMonth < 10){
            leading_zero_month = "0";
        }
        if (startDay < 10){
            leading_zero_day = "0";
        }
        expiry_date = String.valueOf(startYear) + "-" + leading_zero_month + String.valueOf(startMonth) + "-" + leading_zero_day + String.valueOf(startDay);
        Product prod = dataSource.createProduct(title_for_list, description_for_list, image_for_list, url_for_list, expiry_date);

        ProductToList(dataSource.getProducts());
        ListViewAdapter adapter = new ListViewAdapter(this, list);

        listview.setAdapter(adapter);
    }


    public void Notification() {

        setSoonExpire();

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "Fridgee";
        long when = System.currentTimeMillis();


        Notification checkin_notification = new Notification(icon, tickerText,
                when);
        Context context = getApplicationContext();
        CharSequence contentTitle = "Fridgee";
        CharSequence contentText = "Sie sollten " + Integer.toString(number_of_items_soon_expire + 1) + " Artikel bald verbrauchen.";

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        //checkin_notification.setLatestEventInfo(context, contentTitle,
        //        contentText, null);
        checkin_notification.setLatestEventInfo(context, contentTitle,
                contentText, PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0));
        checkin_notification.flags = Notification.FLAG_AUTO_CANCEL;

        // notification_id zero because this overwrites it
        notificationManager.notify(0, checkin_notification);

    }



    public void setSoonExpire (){

        String expiry_to_check = "";

        Calendar c = Calendar.getInstance();

        final int actual_year = c.get(Calendar.YEAR);
        final int actual_month = c.get(Calendar.MONTH) + 1;
        final int actual_day = c.get(Calendar.DAY_OF_MONTH);

        String leading_zero_month = "";
        String leading_zero_day = "";

        if (actual_month < 10) {
            leading_zero_month = "0";
        }
        if (actual_day < 10) {
            leading_zero_day = "0";
        }

        final String actual_date = String.valueOf(actual_year) + "-" + leading_zero_month + String.valueOf(actual_month) + "-" + leading_zero_day + String.valueOf(actual_day);


        number_of_items_soon_expire = -1;

        ArrayList<Product> prod = dataSource.getProducts();
        for (Product products_to_check_date : prod){
            expiry_to_check = products_to_check_date.get_expiry();

            //System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIII:\n\n" + expiry_to_check);

            ProductActivity prod_act = new ProductActivity();
            String days_until_expiry = prod_act.getDifference(actual_date, expiry_to_check);

            if (Integer.parseInt(days_until_expiry) < 3){
                number_of_items_soon_expire++;
            }

        }



        System.out.println("do schau, es sind: \n\n\n" + Integer.toString(number_of_items_soon_expire+1));


        /*
        for (int counter = 0; counter < listview.getAdapter().getCount(); counter++){
            temporary_list_item = listview.getAdapter().getItem(counter);

            temporary_expiry = temporary_list_item.toString();
            temporary_expiry = temporary_expiry.substring(temporary_expiry.length() - 11, temporary_expiry.length() - 1);

            ProductActivity prod_act = new ProductActivity();
            String days_until_expiry = prod_act.getDifference(actual_date, temporary_expiry);

            if (Integer.parseInt(days_until_expiry) < 3){
                number_of_items_soon_expire++;
            }

            System.out.println(temporary_expiry);
        }
        */
    }

    /*
    public int getSoonExpire (){
        setSoonExpire();
        return number_of_items_soon_expire;
    }

    */





}
