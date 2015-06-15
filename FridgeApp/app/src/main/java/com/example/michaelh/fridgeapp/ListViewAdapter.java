package com.example.michaelh.fridgeapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import static com.example.michaelh.fridgeapp.Constants.FIRST_COLUMN;
import static com.example.michaelh.fridgeapp.Constants.SECOND_COLUMN;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by MichaelH on 04.06.2015.
 */
public class ListViewAdapter extends BaseAdapter{

   public ArrayList<HashMap<String,String>> list;

   Activity activity;

    public ListViewAdapter(Activity activity, ArrayList<HashMap<String,String>> list)
    { super();
       this.activity = activity;
        this.list = list;

    }

    @Override
    public int getCount() {
        return  list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    public Activity getActivity() {
        return activity;
    }

    private class ViewHolder{
        TextView txtFirst;
        TextView txtSecond;

    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.colom_row, null);
            holder = new ViewHolder();

            holder.txtFirst = (TextView) convertView.findViewById(R.id.TextFirst);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.TextSecond);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductActivity prod_act = new ProductActivity();

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

        HashMap<String, String> map = list.get(pos);

        final String actual_date = String.valueOf(actual_year) + "-" + leading_zero_month + String.valueOf(actual_month) + "-" + leading_zero_day + String.valueOf(actual_day);
        final String expiry_date = map.get(SECOND_COLUMN);


        String days_until_expiry = prod_act.getDifference(actual_date, expiry_date);


        holder.txtFirst.setText(Html.fromHtml(map.get(FIRST_COLUMN)));
        holder.txtSecond.setText(map.get(SECOND_COLUMN));

        /*
        String asdf = map.get(SECOND_COLUMN);
        System.out.println(asdf + "\n");
        */

        ArrayList<String> positive_days = new ArrayList<String>(Arrays.asList(days_until_expiry.split(" ")));

        if (days_until_expiry.startsWith("-")) {
            holder.txtSecond.setTextColor(Color.parseColor("#ff0000"));
        } else if (Integer.parseInt(positive_days.get(0)) < 3) {
            holder.txtSecond.setTextColor(Color.parseColor("#eea114"));
        } else {
            holder.txtSecond.setTextColor(Color.parseColor("#1eb61e"));
        }


        return convertView;
    }


}
