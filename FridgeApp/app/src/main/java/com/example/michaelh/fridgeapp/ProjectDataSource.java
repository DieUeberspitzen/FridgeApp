package com.example.michaelh.fridgeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by MichaelH on 05.06.2015.
 */
public class ProjectDataSource {

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    private String[] columns = {
            DbHelper.COLUMN_ID,
            DbHelper.COLUMN_NAME,
            DbHelper.COLUMN_CODE,
            DbHelper.COLUMN_Image
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

    public Product createProduct(String name) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_NAME, name);
        long insertId = database.insert(DbHelper.TABLE_FRIDGE,null,values);

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
        int codeIndex = cursor.getColumnIndex(DbHelper.COLUMN_CODE);
        //int imageIndex = cursor.getColumnIndex(DbHelper.COLUMN_Image);

        Product product = new Product(cursor.getString(titleIndex));
        product.setID(cursor.getLong(idIndex));
        product.setCode(cursor.getString(codeIndex));

        return product;
    }

    public List<Product> getProducts() {
      return null;
    }



}
