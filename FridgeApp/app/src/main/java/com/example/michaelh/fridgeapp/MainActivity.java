package com.example.michaelh.fridgeapp;


import android.annotation.TargetApi;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


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

    ProgressDialog mProgressDialog;
    static MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       ma = this;
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
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent i = new Intent();
                HashMap<String, String> temp = (HashMap<String, String>)parent.getItemAtPosition(position);
                titel = temp.get(FIRST_COLUMN);
                //System.out.print("ersd" + titel + "\n");
                i.setClass(MainActivity.this, ProductActivity.class);
                i.putExtra("titel", titel);
                Product prod = dataSource.getProduct(titel);
                i.putExtra("url", prod.get_url());
                i.putExtra("expiry", prod.get_expiry());
                i.putExtra("image", prod.get_image());
                System.out.println(prod.getDescription());
                i.putExtra("description", prod.getDescription());

                startActivity(i);

            }
        });


        /*
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }

        });
        */


    }

    public void ProductToList(ArrayList<Product> listproduct) {
       list = new ArrayList<HashMap<String, String>>();

        for(Product product: listproduct) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put(FIRST_COLUMN, product.getTitle());
            temp.put(SECOND_COLUMN, product.getDescription());


            list.add(temp);
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (result != null) {

            String contents = result.getContents();
            barcode = contents;

            new GetDataOnline().execute();

        }
    }


    public void WriteList(){
        final ListView listview = (ListView) findViewById(R.id.listview);

        if (barcode != null) {

            if(title_for_list.startsWith("Es wurde kein Produkt") || title_for_list == null)
            {

                Context context = getApplicationContext();
                CharSequence text = "...find leider nix... =(";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();


                //title_for_list = "<find nix..sry>";
            }
            else {
                Product prod = dataSource.createProduct(title_for_list, description_for_list, image_for_list, url_for_list, expiry_date);

                ProductToList(dataSource.getProducts());
                ListViewAdapter adapter = new ListViewAdapter(this, list);

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
            mProgressDialog.setMessage("...schau ma mol... =)");
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


}
