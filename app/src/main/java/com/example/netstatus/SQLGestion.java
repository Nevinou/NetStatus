package com.example.netstatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<Service>();
        SQLiteDatabase db = dbContent.getReadableDatabase();

        Cursor cursor = db.query(dbContent.TABLE_NAME, null, null, null, null, null, SQLData.COLUMN_NAME+" ASC");
        //en java android, on ne fait pas de requete SQL directement on passe par cette fonction
        //équivalent ici a SELECT * FROM services ORDER BY name ASC;

        while (cursor.moveToNext()) { //place au prochain ou retourne false si ce n'est plus possible
            String name = cursor.getString(cursor.getColumnIndex(SQLData.COLUMN_NAME));
            String apiLink = cursor.getString(cursor.getColumnIndex(SQLData.COLUMN_API));
            String imageLink = cursor.getString(cursor.getColumnIndex(SQLData.COLUMN_IMAGE)); //lecture de la colonne pour l'index
            boolean favorite = cursor.getInt(cursor.getColumnIndex(SQLData.COLUMN_FAVORITE)) == 1;

            services.add(new Service(name, apiLink, imageLink, favorite)); //ajout a l'array de services
        }

        cursor.close();
        db.close();
        return services;
    }

    public void updateService(String serviceName, boolean favorite){
        SQLiteDatabase db = dbContent.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLData.COLUMN_FAVORITE, favorite? 1:0);
        // SQLite = pas de boolean, donc 1/0
        db.update(SQLData.TABLE_NAME,values,SQLData.COLUMN_NAME+" = ?",new String[]{serviceName});
        db.close();
    }
}
