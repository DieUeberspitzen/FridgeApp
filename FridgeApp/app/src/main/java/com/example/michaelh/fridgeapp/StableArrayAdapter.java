package com.example.michaelh.fridgeapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MichaelH on 02.06.2015.
 */
public class StableArrayAdapter extends ArrayAdapter<Product> {
    HashMap<Product, Integer> mIdMap = new HashMap<Product, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId,
                              List<Product> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        Product item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }



}
