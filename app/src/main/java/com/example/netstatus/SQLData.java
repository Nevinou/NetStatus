package com.example.netstatus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLData extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 8; //incrémenté = delete tout, refaire les appels d'ajout
    public static final String DATABASE_NAME = "services.db";

    public static final String TABLE_NAME = "services";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_API = "api";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_FAVORITE = "favorite";
    public SQLData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String command = "CREATE TABLE "+TABLE_NAME+"("
                +COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COLUMN_NAME+" TEXT,"
                +COLUMN_API+" TEXT,"
                +COLUMN_IMAGE+" TEXT,"
                +COLUMN_FAVORITE+" INTEGER DEFAULT 0" //boolean existe pas sql lite
                +")";
        db.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME); //appel de cette methode lorsque la version change
        onCreate(db);
    }
}
