package com.example.netstatus;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

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
    void addProbleme (JSONArray incidents) throws JSONException{
        if (incidents.length()==0){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout card = activity.findViewById(R.id.cardPb);
                    TextView pasPb = new TextView(activity);
                    pasPb.setText(activity.getString(R.string.pasPb));
                    pasPb.setTextColor(activity.getColor(R.color.status_operational));
                    card.addView(pasPb);
                }
            });
        } else {
            Log.d("text","Trace 1 "+incidents);
            for (int i = 0; i < incidents.length();i++){
                ajoutCardProbleme(incidents.getJSONObject(i));
            }
        }
    }

    void ajoutCardProbleme(JSONObject incident)  {
        // Extraction des données du JSONObject
        String name;
        String hour = extractHour(incident);
        String impact;
        try {
            name = incident.getString("name");
            impact = incident.getString("impact");
        } catch (JSONException e ){
            name = activity.getString(R.string.unknown);
            impact = activity.getString(R.string.unknown);
        }
        String finalName = name;
        String finalImpact = impact;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LinearLayout cardPb = activity.findViewById(R.id.cardPb);
                // Récupère le template de Problème
                LayoutInflater inflater = LayoutInflater.from(activity);
                CardView templatePb = (CardView) inflater.inflate(R.layout.template_probleme,cardPb,false);
                // Modifie les éléments du template
                TextView nameTextView = templatePb.findViewById(R.id.namePb);
                nameTextView.setText(finalName);
                // AJout de l'heure de dernière modification
                TextView clockLastUpdate = templatePb.findViewById(R.id.lastModif);
                clockLastUpdate.setText(hour);
                // Ajout de l'impact
                TextView impactTextView = templatePb.findViewById(R.id.impact);
                impactTextView.setText(finalImpact);

                // AJout du template à l'activity
                cardPb.addView(templatePb);
            }
        });
    }
    String extractHour(JSONObject json){
        String hour;
        try {
            String input = json.getString("updated_at");

            // Parser la chaîne ISO 8601 en OffsetDateTime
            OffsetDateTime odt = OffsetDateTime.parse(input);

            // Formatter en hh:mm (12h)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
            hour = odt.format(formatter);
        } catch (JSONException e) {
            hour = "--:--";
        }

        return hour;
    }
    String extractHour (JSONObject json,String name){
        String hour;
        try {
            String input = json.getJSONObject(name).getString("updated_at");

            // Parser la chaîne ISO 8601 en OffsetDateTime
            OffsetDateTime odt = OffsetDateTime.parse(input);

            // Formatter en hh:mm (12h)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
            hour = odt.format(formatter);
        } catch (JSONException e) {
            hour = "--:--";
        }

        return hour;
    }
    void addGeneralStatus (String statusCouleur, String hour) throws NumberFormatException{
        StringTokenizer st = new StringTokenizer(statusCouleur,";");
        String status = st.nextToken();
        int color = Integer.parseInt(st.nextToken());
        int colorBg = Integer.parseInt(st.nextToken());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Modification du texte et de la couleur
                TextView textStatus = activity.findViewById(R.id.statusText);
                textStatus.setText(status);
                textStatus.setTextColor(color);
                View indicator = activity.findViewById(R.id.statusIndicator);
                indicator.setBackgroundTintList(ColorStateList.valueOf(color));
                // Couleur background
                LinearLayout bg = activity.findViewById(R.id.statusGeneralBG);
                bg.setBackgroundColor(colorBg);
                // Horloge
                TextView clock = activity.findViewById(R.id.textClock);
                clock.setText(hour);
                clock.setTextColor(color);

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

    JSONObject loadJson (String path){
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

    String extractGeneralStatus (JSONObject json){
        String generalStatus = "";
        try {
            JSONObject stat = json.getJSONObject("status");
            generalStatus = stat.getString("indicator");
        } catch (JSONException e){
            return "Inconnue";
        }
        return generalStatus;
    }

    static JSONArray extractIncidents (JSONObject json){
        JSONArray incidents;
        try {
            incidents = json.getJSONArray("incidents");
            Log.d("test",incidents.toString());
        } catch (JSONException e ){
           incidents = new JSONArray();
        }
        return incidents;
    }

    static void parcourArray (JSONArray array) throws JSONException{
        for (int i = 0; i < array.length(); i++){
            Log.d("test",array.getJSONObject(i).toString());
        }
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
        // Récupération du json de l'api et mise dans les différentes variables
        //
        JSONObject json = loadJson("summary.json");

        //
        // Requête Status Général
        //
        String generalStatus = extractGeneralStatus(json);
        addGeneralStatus(Service.status(generalStatus,activity),extractHour(json,"page"));

        //
        // Requête Problème en cours
        //
        JSONArray incidents = extractIncidents(json);
        try {
            addProbleme(incidents);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }









    }
}
