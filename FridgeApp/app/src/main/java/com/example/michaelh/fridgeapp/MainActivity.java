package com.example.michaelh.fridgeapp;


import android.annotation.TargetApi;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


import static com.example.michaelh.fridgeapp.Constants.FIRST_COLUMN;
import static com.example.michaelh.fridgeapp.Constants.SECOND_COLUMN;


public class MainActivity extends ActionBarActivity  {
    private Product product;
    private ArrayList<HashMap<String,String>> list;
    private ArrayList<Product> products = new ArrayList<Product>();
    ProjectDataSource dataSource;


    String url = "http://www.google.com/search?ie=UTF-8&oe=UTF-8&sourceid=navclient&gfns=1&q=";
    String barcode = "";
    String title_for_list = "";
    String description_for_list = "";
    String image_for_list = "";
    String url_for_list = "";
    Date expiry_date = new Date();

    //ProgressDialog mProgressDialog;

    boolean finished_get_data = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listview = (ListView) findViewById(R.id.listview);

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

            //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
               /* final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });*/
            }

        });
    }

    public void ProductToList(ArrayList<Product> listproduct) {
       list = new ArrayList<HashMap<String, String>>();

        for(Product product: listproduct) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put(FIRST_COLUMN, product.getProductName());
            temp.put(SECOND_COLUMN, product.getCode());

            list.add(temp);
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (result != null) {

            String contents = result.getContents();
            barcode = contents;

            new GetDataOnline().execute();


            final ListView listview = (ListView) findViewById(R.id.listview);

            if (contents != null) {

                while(!finished_get_data) {}

                if(title_for_list.startsWith("<") || title_for_list == null)
                {
                    title_for_list = "<find nix..sry>";
                }

                Product prod = dataSource.createProduct(title_for_list,contents);

                finished_get_data = false;

                ProductToList(dataSource.getProducts());
                ListViewAdapter adapter = new ListViewAdapter(this,list);

                listview.setAdapter(adapter);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    // Title AsyncTask
    public class GetDataOnline extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Produktinformationen");
            mProgressDialog.setMessage("...hob's glei...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
            */
        }


        @Override
        protected Void doInBackground(Void  ... params) {
            try {
                url_for_list = url + barcode;

                // Connect to the web site
                Document document = Jsoup.connect(url_for_list).get();
                // Get the html document title
                Elements headline_one = document.select("h1");
                Elements description = document.select("h3[class=page-title-subline]");
                Elements image = document.select("meta[property=og:image]");

                // get url of title
                title_for_list = headline_one.html();

                // get url of description
                description_for_list = description.html();

                // get url of image
                image_for_list = image.attr("content");

                finished_get_data = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //mProgressDialog.dismiss();
        }

    }


}
