package com.example.phonebook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class DBConnection extends SQLiteOpenHelper {
//    public static final String DB_NAME = "./PhoneBook1.db";
    public static final String APP_PACKAGE_NAME = "com.example.phonebook";
    public static final File DB_FILE =
            new File("/data/data/" + APP_PACKAGE_NAME + "/databases/PhoneBook1.db");
    public static final String DB_NAME = DB_FILE.getPath();
    static final int DB_VERSION = 1;
    SQLiteDatabase db;
    public int id_this;
    Cursor cursor;
    static String TABLE_NAME = "PhoneBook1";
    static String ID = "id";
    static String NAME = "name";
    static String SEX = "sex";
    static String PHONE = "phone";

    DBConnection(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String querySql = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER primary key autoincrement,"
                + NAME + " text not null,"
                + SEX + " text not null,"
                + PHONE + " INTEGER not null" + ");";
        db.execSQL(querySql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
