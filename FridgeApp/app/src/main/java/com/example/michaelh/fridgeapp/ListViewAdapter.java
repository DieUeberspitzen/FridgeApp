package com.example.michaelh.fridgeapp;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import static com.example.michaelh.fridgeapp.Constants.FIRST_COLUMN;
import static com.example.michaelh.fridgeapp.Constants.SECOND_COLUMN;
import org.w3c.dom.Text;

import java.util.ArrayList;
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

    private class ViewHolder{
        TextView txtFirst;
        TextView txtSecond;

    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = activity.getLayoutInflater();

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.colom_row,null);
            holder = new ViewHolder();

            holder.txtFirst = (TextView) convertView.findViewById(R.id.TextFirst);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.TextSecond);

            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }

        HashMap<String,String> map = list.get(pos);
        holder.txtFirst.setText(map.get(FIRST_COLUMN));
        holder.txtSecond.setText(map.get(SECOND_COLUMN));

        return convertView;
    }
}
