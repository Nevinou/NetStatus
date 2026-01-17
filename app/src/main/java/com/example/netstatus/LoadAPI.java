package com.example.netstatus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.StringTokenizer;

public class LoadAPI implements Runnable{ //implements runnabe, redéfinition de run
    Activity activity;
    List<Service> services;
    LoadAPI(Activity activity, List<Service> services){ //constructeur
        this.activity = activity;
        this.services = services;
    }

    void createCard(JSONObject json, Service service, Bitmap bitMapImage){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String serviceName;
                String status;
                int color;
                try {
                    JSONObject data = json.getJSONObject("page"); //récupération json page, dans le futur, utilisé pour afficher l'heure d'update
                    JSONObject stat = json.getJSONObject("status"); //récupération du status json
                    serviceName = service.getName();
                    String result = Service.status(stat.getString("indicator"),activity);
                    StringTokenizer tokenizer = new StringTokenizer(result,";");
                    status = tokenizer.nextToken();
                    color = Integer.parseInt(tokenizer.nextToken());
                } catch (JSONException e) {
                    displayAlert("Erreur JSON, récupération de la page"); //affichage alerte
                    return; //on execute pas le reste
                }

                // doit être fait après le setContentView ! Récupération du parent (layout)
                LinearLayout parent = activity.findViewById(R.id.main);
                //Log.d("working",serviceName);
                //on copie le layout card avec inflate.from et on définit le parent. False = ne pas parenter automatiquement
                CardView card = (CardView) LayoutInflater.from(activity).inflate(R.layout.template_main,parent,false);

                TextView titre = card.findViewById(R.id.titre); //récupération du titre de la card
                titre.setText(serviceName); //on définit un texte
                TextView serviceStatus = card.findViewById(R.id.status);
                serviceStatus.setText(activity.getText(R.string.string_status)+" "+status); //on concatène "status :" avec le stauts actuel
                serviceStatus.setTextColor(color);

                ImageView image = card.findViewById(R.id.logo);
                image.setImageBitmap(bitMapImage);

                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //création d'un itent vers Details
                        Intent intent = new Intent(activity,DetailsActivity.class);
                        // Envoie le service à la page détail Activity
                        intent.putExtra("service", service);

                        activity.startActivity(intent); //on affiche l'activity
                    }
                });
                parent.addView(card); //parentage pour affichage
            }
        });
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

    @Override
    public void run() {
        for (Service service : services) {
            new Thread(new Runnable() { // on fait dans un nouveau thread les requetes pour aller plus vite
                @Override
                public void run() { //l'ordre est cassé car le premier qui a finit de chargé s'affiche...
                    try {
                        URL url = new URL(service.getApi() + "/api/v2/status.json"); //création url
                        URLConnection conn = url.openConnection(); //ajouter la permission internet dans le manifest ! Ouverture de la connexion
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); // Flux de lecture
                        String total = "";
                        String line;
                        while ((line = reader.readLine()) != null) {
                            total += line; //lecture de toute les lignes
                        }
                        reader.close();
                        //Log.d("JSON",total);
                        JSONObject json = new JSONObject(total); //création d'un JSONObject

                        InputStream inputImage = new URL(service.getImage()).openStream(); // vu que la méthode setImageURI
                        // de l'imageView ne prend que des URI locaux, il faut download l'image depuis internet en bitmap
                        //on transforme l'image en bitmap
                        Bitmap bitMapImage = BitmapFactory.decodeStream(inputImage);

                        createCard(json,service,bitMapImage); //appel methode
                    } catch (MalformedURLException e) {
                        displayAlert("Mauvaise URL");
                    } catch (JSONException e) {
                        displayAlert("Erreur JSON");
                    } catch (IOException e) {
                        Log.d("Noob",e.toString());
                        displayAlert("Erreur de donnée pour un service");
                    }
                }
            }).start();
        }
    }
}
