package com.example.michaelh.fridgeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Created by MichaelH on 05.06.2015.
 */
public class ProjectDataSource {

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    private String[] columns = {
            DbHelper.COLUMN_ID,
            DbHelper.COLUMN_NAME,
            DbHelper.COLUMN_DESECRTIPTION,
            DbHelper.COLUMN_Image,
            DbHelper.COLUMN_URL,
            DbHelper.COLUMN_DATE
    };

    public ProjectDataSource(Context context){
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close()  {
        dbHelper.close();
    }

    public Product createProduct(String name,String description, String image, String url, String date) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_NAME, name);
        values.put(DbHelper.COLUMN_DESECRTIPTION, description);
        values.put(DbHelper.COLUMN_Image, image);
        values.put(DbHelper.COLUMN_URL, url);
        values.put(DbHelper.COLUMN_DATE, date);
        long insertId = database.insert(DbHelper.TABLE_FRIDGE,null,values);
        //System.out.println(insertId);
        Cursor cursor = database.query(DbHelper.TABLE_FRIDGE, columns,
               dbHelper.COLUMN_ID + "=" + insertId, null, null,null,null);

        cursor.moveToFirst();
        Product product;
        product = populateProject(cursor);
        cursor.close();
        return product;
    }

    public Product populateProject(Cursor cursor) {
       int idIndex = cursor.getColumnIndex(DbHelper.COLUMN_ID);
       int titleIndex = cursor.getColumnIndex(DbHelper.COLUMN_NAME);
        int descrIndex = cursor.getColumnIndex(DbHelper.COLUMN_DESECRTIPTION);
        int imageIndex = cursor.getColumnIndex(DbHelper.COLUMN_Image);
        int urlIndex = cursor.getColumnIndex(DbHelper.COLUMN_URL);
        int dateIndex = cursor.getColumnIndex(DbHelper.COLUMN_DATE);

        Product product = new Product(cursor.getString(titleIndex));
        product.setID(cursor.getLong(idIndex));
        product.setDescription(cursor.getString(descrIndex));
        product.set_image(cursor.getString(imageIndex));
        product.set_url(cursor.getString(urlIndex));
        product.set_expiry(cursor.getString(dateIndex));


        return product;
    }

    public Product getProduct(String titel)
    {
        Cursor cursor = database.query(DbHelper.TABLE_FRIDGE, columns, null,null,null,null,null);
       // Cursor cursor = database.query(DbHelper.TABLE_FRIDGE, columns, dbHelper.COLUMN_NAME  + "=" + titel,null,null,null,null);
        cursor.moveToFirst();
        Product prod = populateProject(cursor);
        cursor.close();
        return prod;
    }

    public ArrayList<Product> getProducts() {
       ArrayList<Product> listproduct = new ArrayList<Product>();
        Cursor cursor = database.query(DbHelper.TABLE_FRIDGE, columns, null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Product prod = populateProject(cursor);
            listproduct.add(prod);
            cursor.moveToNext();
        }
        cursor.close();
        return listproduct;

    }



}
