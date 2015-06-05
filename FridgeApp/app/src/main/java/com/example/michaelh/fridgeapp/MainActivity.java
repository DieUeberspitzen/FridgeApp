package com.example.michaelh.fridgeapp;


import android.annotation.TargetApi;

import android.app.Fragment;
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

import android.app.Fragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.ArrayList;
import java.util.HashMap;


import static com.example.michaelh.fridgeapp.Constants.FIRST_COLUMN;
import static com.example.michaelh.fridgeapp.Constants.SECOND_COLUMN;


public class MainActivity extends ActionBarActivity implements ProjectsListFragment.OnProjectInteractionListener {


    //ArrayList<Product> list = new ArrayList<Product>();
    private Product product;
    private ArrayList<HashMap<String,String>> list;
    private ArrayList<Product> products = new ArrayList<Product>();
    ProjectDataSource dataSource;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ProjectsListFragment.newInstance("a","b");

        ListView listview = (ListView) findViewById(R.id.listview);
       /* String[] values = new String[]{""};


        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }*/
        product = new Product("test","1234");
       products.add(product);
       ProductToList(products);

        //list.add(product);
        //final StableArrayAdapter adapter = new StableArrayAdapter(this,android.R.layout.simple_list_item_1, list);
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

                //TextView placeholder = (TextView)findViewById(R.id.code_text);
                //placeholder.setText("ulululul");
            }



        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
            final ListView listview = (ListView) findViewById(R.id.listview);

            if (contents != null) {
                product = new Product("test",contents);
                products.add(product);
                ProductToList(products);
                ListViewAdapter adapter = new ListViewAdapter(this,list);

               // list.add(contents);
                /*final StableArrayAdapter adapter = new StableArrayAdapter(this,
                        android.R.layout.simple_list_item_1, list);*/
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


    @Override
    public void onProjectInteraction(String id) {

    }
}
