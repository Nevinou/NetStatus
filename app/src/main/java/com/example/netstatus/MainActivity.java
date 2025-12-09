package com.example.netstatus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

class LoadAPI implements Runnable{ //implements runnabe, redéfinition de run
    Activity activity;
    LoadAPI(Activity activity){ //constructeur
        this.activity = activity;
    }

    void createCard(JSONObject json, String site){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String serviceName;
                String status;
                int color;
                try {
                    JSONObject data = json.getJSONObject("page"); //récupération json page
                    JSONObject stat = json.getJSONObject("status"); //récupération du status json
                    serviceName = data.getString("name"); //récupération valeur name

                    switch (stat.getString("indicator")) {
                        case "none":
                            status = activity.getString(R.string.oper); //récupération du string du fichier xml
                            color = activity.getColor(R.color.oper);
                            break;
                        case "minor":
                            status = activity.getString(R.string.warn);
                            color = activity.getColor(R.color.warn);
                            break;
                        case "major":
                            status = activity.getString(R.string.broken);
                            color = activity.getColor(R.color.broken);
                            break;
                        case "critical":
                            status = activity.getString(R.string.hard_broken);
                            color = activity.getColor(R.color.hard_broken);
                            break;
                        case "maintenance":
                            status = activity.getString(R.string.disabled);
                            color = activity.getColor(R.color.disabled);
                            break;
                        default:
                            status = activity.getString(R.string.unknown);
                            color = activity.getColor(R.color.unknown);
                            break;
                    }
                } catch (JSONException e) {
                    displayAlert("Erreur JSON, récupération de la page"); //affichage alerte
                    return; //on execute pas le reste
                }

                LinearLayout parent = activity.findViewById(R.id.main); // doit être fait après le setContentView ! Récupération du parent (layout)
                //Log.d("working",serviceName);
                CardView card = (CardView) LayoutInflater.from(activity).inflate(R.layout.template_main,parent,false); //on copie le layout card avec inflate.from et on définit le parent. False = ne pas parenter automatiquement

                TextView titre = card.findViewById(R.id.titre); //récupération du titre de la card
                titre.setText(serviceName); //on définit un texte
                TextView serviceStatus = card.findViewById(R.id.status);
                serviceStatus.setText(activity.getText(R.string.string_status)+" "+status); //on concatène "status :" avec le stauts actuel
                serviceStatus.setTextColor(color);

                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity,DetailsActivity.class); //création d'un itent vers Details
                        intent.putExtra("LienAPI",site); //on met un extra de donnée
                        activity.startActivity(intent); //on affiche l'activity
                    }
                });
                parent.addView(card); //parentage pour affichage
            }
        });
    }

    void displayAlert(String text){
        activity.runOnUiThread(new Runnable() { //les uis doivent tourner sur le thread ui, on utilise cette méthode pour le faire
            @Override
            public void run() {
                Toast alert = Toast.makeText(activity,text,Toast.LENGTH_SHORT); //Activity hérite de context
                alert.show(); //on affiche le Toast
            }
        });
    }

    @Override
    public void run() {
        for (String site : MainActivity.apis) {
            new Thread(new Runnable() { // on fait dans un nouveau thread les requetes pour aller plus vite
                @Override
                public void run() {
                    try {
                        URL url = new URL(site + "/api/v2/status.json"); //création url
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
                        createCard(json,site); //appel methode
                    } catch (MalformedURLException e) {
                        displayAlert("Mauvaise URL");
                    } catch (JSONException e) {
                        displayAlert("Erreur JSON");
                    } catch (IOException e) {
                        displayAlert("Erreur de donnée pour un service");
                    }
                }
            }).start();
        }
    }
}


public class MainActivity extends AppCompatActivity {
    public static List<String> apis;

    void init(){
        apis = new ArrayList<String>();
        apis.add("https://discordstatus.com");
        apis.add("https://www.githubstatus.com");
        apis.add("https://www.cloudflarestatus.com");
        apis.add("https://www.redditstatus.com");
        apis.add("https://status.atlassian.com");
        apis.add("https://shopify.statuspage.io");
        apis.add("https://status.digitalocean.com");
        apis.add("https://status.dropbox.com");
        apis.add("https://status.twilio.com");
        apis.add("https://status.newrelic.com");
        apis.add("https://status.aweber.com");
        apis.add("https://status.duo.com");
        apis.add("https://status.librato.com");
        apis.add("https://status.coinbase.com");
        apis.add("https://status.iadvize.com");
        apis.add("https://status.searchspring.com");
        apis.add("https://status.opentext.com");
        apis.add("https://status.mindbodyonline.com");
        apis.add("https://status.stitchdata.com");
        apis.add("https://alerts.library.nyu.edu");
        apis.add("https://status.developer.intuit.com");
        apis.add("https://status.epicgames.com");
        apis.add("https://status.scaleway.com"); //ajout des urls qui utilisent statuspage.io

        Thread thread = new Thread(new LoadAPI(this)); //le réseau doit être fait dans un thread externe
        thread.start();

        Button more = findViewById(R.id.more); //on prend le boutton de l'activity qui a l'id "more"
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AllServicesActivity.class); //on fait un itent pour changer de page
                startActivity(intent); //on change d'activity
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main); //rend visible
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left+20, systemBars.top, systemBars.right+20, systemBars.bottom);
            return insets;
        });

        init(); //appel de la méthode init
    }
}