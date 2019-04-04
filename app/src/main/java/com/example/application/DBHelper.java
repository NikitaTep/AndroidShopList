package com.example.application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context) {
        super(context, "shoplists", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table shoplists (" +
                "_id integer primary key autoincrement," +
                "title text," +
                "name text," +
                "amount int," +
                "unit text," +
                "availability text" + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
