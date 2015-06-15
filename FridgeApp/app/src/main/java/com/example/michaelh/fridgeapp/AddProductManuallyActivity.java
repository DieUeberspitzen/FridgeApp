package com.example.michaelh.fridgeapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Calendar;


public class AddProductManuallyActivity extends ActionBarActivity {

    Calendar c = Calendar.getInstance();
    int startYear;
    int startMonth;
    int startDay;
    String titel = "";
    String descr = "";
    String image_url = "";


    ProjectDataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_manually);

        dataSource = MainActivity.dataSource;
        Button addButton = (Button)findViewById(R.id.addbutton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titelbox= (EditText)findViewById(R.id.tbTitel);
                titel = titelbox.getText().toString();
                EditText descrbox= (EditText)findViewById(R.id.tbdescr);
                descr = descrbox.getText().toString();
                EditText image_url_tb = (EditText)findViewById(R.id.tbUrlImage);
                image_url = image_url_tb.getText().toString();

                DialogFragment dialogFragment = new StartDatePicker();
                dialogFragment.show(getFragmentManager(), "start_date_picker");



            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_product_manually, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
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

            DatePickerDialog dialog = new DatePickerDialog(AddProductManuallyActivity.this, this, startYear, startMonth, startDay);

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
            write();

        }


    }

    public void write()
    {
        String leading_zero_month = "";
        String leading_zero_day = "";
        if (startMonth < 10){
            leading_zero_month = "0";
        }
        if (startDay < 10){
            leading_zero_day = "0";
        }
        DialogFragment dialogFragment = new StartDatePicker();
        dialogFragment.show(getFragmentManager(), "start_date_picker");
        String expiry_date = String.valueOf(startYear) + "-" + leading_zero_month + String.valueOf(startMonth) + "-" + leading_zero_day + String.valueOf(startDay);


        dataSource.createProduct(titel,descr,image_url,"",expiry_date);

        Intent it = new Intent();
        it.setClass(AddProductManuallyActivity.this, MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(it);
    }


}
