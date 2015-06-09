package com.example.michaelh.fridgeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by MichaelH on 04.06.2015.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "fridge.db";
    public static final int DB_Version = 3;
    public static final String TABLE_FRIDGE = "fridgeitems";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESECRTIPTION = "description";
    public static final String COLUMN_Image = "image";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_DATE = "expiry";

    public static final String SQL_CREATE =
            "CREATE Table " + TABLE_FRIDGE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COLUMN_NAME + " text not null, " +
                    COLUMN_DESECRTIPTION + " text, " +
                    COLUMN_Image + " text, " +
                    COLUMN_URL + " text, " +
                    COLUMN_DATE + " text);";


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       sqLiteDatabase.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIDGE);
        onCreate(sqLiteDatabase);
    }
}
