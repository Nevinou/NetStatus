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
import java.time.Duration;
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
        int color,colorBg;
        try {
            name = incident.getString("name");
            StringTokenizer st = new StringTokenizer(Service.status(incident.getString("impact"),activity),";");
            impact = st.nextToken();
            color = Integer.parseInt(st.nextToken());
            colorBg = Integer.parseInt(st.nextToken());

        } catch (JSONException e ){
            name = activity.getString(R.string.unknown);
            impact = activity.getString(R.string.unknown);
            color = activity.getColor(R.color.status_inconnu);
            colorBg = activity.getColor(R.color.status_inconnu_bg);
        }
        String finalName = name;
        int finalColor = color;
        int finalColorBg = colorBg;
        String finalImpact = impact;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LinearLayout cardPb = activity.findViewById(R.id.cardPb);
                // Récupère le template de Problème
                LayoutInflater inflater = LayoutInflater.from(activity);
                CardView templatePb = (CardView) inflater.inflate(R.layout.template_probleme,cardPb,false);
                templatePb.setBackgroundTintList(ColorStateList.valueOf(finalColorBg));
                // Modifie les éléments du template
                TextView nameTextView = templatePb.findViewById(R.id.namePb);
                nameTextView.setText(finalName);
                // AJout de l'heure de dernière modification
                TextView clockLastUpdate = templatePb.findViewById(R.id.lastModif);
                clockLastUpdate.setText(hour);
                clockLastUpdate.setTextColor(finalColor);
                // Ajout de l'impact
                TextView impactTextView = templatePb.findViewById(R.id.impact);
                impactTextView.setText(finalImpact);
                impactTextView.setTextColor(finalColor);
                Log.d("test",finalImpact);
                // Modification couleur rond
                View impactIndicator = templatePb.findViewById(R.id.impactIndicator);
                impactIndicator.setBackgroundTintList(ColorStateList.valueOf(finalColor));
                // Modification couleur du background de l'indicateur
                LinearLayout indicator = templatePb.findViewById(R.id.indicator);
                indicator.setBackgroundColor(finalColorBg);
                // AJout du template à l'activity
                cardPb.addView(templatePb);
            }
        });
    }
    String extractHour(JSONObject json){
        String delta;
        try {
            String input = json.getString("updated_at");
            OffsetDateTime odt = OffsetDateTime.parse(input);
            OffsetDateTime now = OffsetDateTime.now();
            Duration duration = Duration.between(odt,now);

            delta = "Il y a "+duration.toHours()+" h";
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
            //hour = odt.format(formatter);
        } catch (JSONException e) {
            delta = "Il y a -- h";
        }

        return delta;
    }
    long extractHour (JSONObject json,String name){
        long delta;
        try {
            String input = json.getJSONObject(name).getString("updated_at");
            OffsetDateTime odt = OffsetDateTime.parse(input);
            OffsetDateTime now = OffsetDateTime.now();
            Duration duration = Duration.between(odt,now);

            delta = duration.toHours();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
            //hour = odt.format(formatter);
        } catch (JSONException e) {
            delta = -1;
        }

        return delta;
    }
    void addGeneralStatus (String statusCouleur, JSONObject json) throws NumberFormatException{
        StringTokenizer st = new StringTokenizer(statusCouleur,";");
        String status = st.nextToken();
        int color = Integer.parseInt(st.nextToken());
        int colorBg = Integer.parseInt(st.nextToken());
        String hour;
        try {
            hour = extractHour(json.getJSONObject("page"));
        } catch (JSONException e) {
            hour = "Il y a -- h";
        }
        String finalHour = hour;

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
                clock.setText(finalHour);
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

    static JSONArray extractDataFromJson (JSONObject json, String name){
        JSONArray data;
        try {
            data = json.getJSONArray(name);
            //Log.d("test",incidents.toString());
        } catch (JSONException e ){
           data = new JSONArray();
        }
        return data;
    }

    static void parcourArray (JSONArray array) throws JSONException{
        for (int i = 0; i < array.length(); i++){
            Log.d("test",array.getJSONObject(i).toString());
        }
    }
    void displayAlert(String text){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast alert = Toast.makeText(activity,text,Toast.LENGTH_SHORT); //Activity hérite de context
                alert.show(); //on affiche le Toast
            }
        });
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
        addGeneralStatus(Service.status(generalStatus,activity),json);

        //
        // Requête Problème en cours
        //
        JSONArray incidents = extractDataFromJson(json,"incidents");
        try {
            addProbleme(incidents);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //
        // Affichage des maintenance prévue
        //










    }
}
