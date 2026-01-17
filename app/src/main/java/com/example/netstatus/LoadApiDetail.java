package com.example.netstatus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class LoadApiDetail  implements Runnable{
    Activity activity;
    Service service;

    // Constructeur
    LoadApiDetail (Activity activity, Service urlApi){
        this.activity = activity;
        this.service = urlApi;
    }
    void addLogo (Bitmap logo){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView image = activity.findViewById(R.id.logo);
                image.setImageBitmap(logo);
            }
        });
    }
    Bitmap loadLogo () throws MalformedURLException, IOException{
        Bitmap logo;
        // vu que la méthode setImageURI
        InputStream inputImage = new URL(service.getImage()).openStream();
        // de l'imageView ne prend que des URI locaux,
        // il faut download l'image depuis internet en bitmap
        // on transforme l'image en bitmap
        logo = BitmapFactory.decodeStream(inputImage);
        return logo;
    }

    JSONObject loadJson (String path ){
        JSONObject json;
        try {
            URL url = new URL(service.getApi() + "/api/v2/"+path); //création url
            URLConnection conn = url.openConnection(); //ajouter la permission internet dans le manifest ! Ouverture de la connexion
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); // Flux de lecture
            String pageWeb = "";
            String line;
            while ((line = reader.readLine()) != null) {
                pageWeb += line; //lecture de toute les lignes
            }
            reader.close();
            //Log.d("JSON",total);
            json = new JSONObject(pageWeb); //création d'un JSONObject
        } catch (Exception e) {
            json = null;
        }
        return json;
    }

    String extractGeneralStatus (JSONObject status){
        String generalStatus = "";
        try {
            JSONObject data = status.getJSONObject("page");
            JSONObject stat = status.getJSONObject("status");
            Log.d("Trace 1 ", ""+stat);
        } catch (JSONException e){
            return "Inconnue";
        }


        return generalStatus;
    }

    @Override
    public void run() {
        //
        // Requête Image
        //
        Bitmap logo;
        try {
            logo = loadLogo();
            // Ajout du log sur l'interface graphique
            addLogo(logo);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        // Requête Status Général
        //
        JSONObject status = loadJson("status.json");
        String generalStatus = extractGeneralStatus(status);






    }
}
