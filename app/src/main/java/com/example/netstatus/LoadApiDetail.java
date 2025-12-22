package com.example.netstatus;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class LoadApiDetail implements Runnable{
    Activity activity;
    String urlApi;

    // Constructeur
    LoadApiDetail (Activity activity, String urlApi){
        this.activity = activity;
        this.urlApi = urlApi;
    }
    void displayAlert(String text){
        //les uis doivent tourner sur le thread ui, on utilise cette méthode pour le faire
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast alert = Toast.makeText(activity,text,Toast.LENGTH_SHORT); //Activity hérite de context
                alert.show(); //on affiche le Toast
            }
        });
    }

    JSONObject requeteApiSum (){
        JSONObject json;
        try {

            URL url = new URL(urlApi + "/api/v2/summary.json"); //création url
            URLConnection conn = url.openConnection(); //ajouter la permission internet dans le manifest ! Ouverture de la connexion
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); // Flux de lecture
            String total = "";
            String line;
            while ((line = reader.readLine()) != null) {
                total += line; //lecture de toute les lignes
            }
            reader.close();
            //Log.d("test",total);
            json = new JSONObject(total); //création d'un JSONObject
            return json;
        } catch (MalformedURLException e) {
            displayAlert("Mauvaise URL");
        } catch (JSONException e) {
            displayAlert("Erreur JSON");
        } catch (IOException e) {
            displayAlert("Erreur de donnée pour un service");
        }
        return new JSONObject();
    }

    void modifStatusGlobal (JSONObject json){
        String status = "";
        try {
            JSONObject stat = json.getJSONObject("status");
            Log.d("test","Trace 3 : \n Json Status : "+stat);
            switch (stat.getString("indicator")) {
                case "none":
                    status = activity.getString(R.string.oper); //récupération du string du fichier xml
                    break;
                case "minor":
                    status = activity.getString(R.string.warn);
                    break;
                case "major":
                    status = activity.getString(R.string.broken);
                    break;
                case "critical":
                    status = activity.getString(R.string.hard_broken);
                    break;
                case "maintenance":
                    status = activity.getString(R.string.disabled);
                    break;
                default:
                    status = activity.getString(R.string.unknown);
                    break;
            }
        } catch (JSONException e) {
            Log.w("test","test");
        }
        Log.d("test","Trace 4 : Status "+status);
        TextView statusGlobal = activity.findViewById(R.id.global_status);
        Log.d("test","Trace 5 : Status : "+status);
        statusGlobal.setText((CharSequence) status);
        Log.d("Test",status);

    }
    @Override
    public void run() {
        Log.w("test","Trace 1");
        JSONObject json = requeteApiSum();
        Log.d("test","Trace 2 \n Json : "+json);
        //txt_global_status
        TextView statusGlobal = activity.findViewById(R.id.global_status);
        statusGlobal.setText("Bleu");
        //modifStatusGlobal(json);


    }
}
