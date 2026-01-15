package com.example.netstatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLGestion {
    private SQLData dbContent;

    public SQLGestion(Context context) {
        dbContent = new SQLData(context);
    }

    public void addService(String name, String apiLink, String imageLink) {
        SQLiteDatabase db = dbContent.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLData.COLUMN_NAME, name);
        values.put(SQLData.COLUMN_API, apiLink);
        values.put(SQLData.COLUMN_IMAGE, imageLink);

        db.insert(SQLData.TABLE_NAME, null, values);
        db.close();
    }

    //fonctions de récup a faire
}
