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

class LoadAPI implements Runnable{
    Activity activity;
    LoadAPI(Activity activity){
        this.activity = activity;
    }

    void createCard(JSONObject json){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String serviceName;
                try {
                    JSONObject data = json.getJSONObject("page");
                    serviceName = data.getString("name");
                } catch (JSONException e) {
                    displayAlert("Erreur JSON, récupération de la page");
                    return;
                }

                LinearLayout parent = activity.findViewById(R.id.main); // doit être fait après le setContentView !
                Log.d("working",serviceName);
                CardView card = (CardView) LayoutInflater.from(activity).inflate(R.layout.template_main,parent,false);

                TextView titre = card.findViewById(R.id.titre);
                titre.setText(serviceName);


                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity,DetailsActivity.class);
                        intent.putExtra("Name",serviceName);
                        activity.startActivity(intent);
                    }
                });
                parent.addView(card);
            }
        });
    }

    void displayAlert(String text){
        activity.runOnUiThread(new Runnable() { //les uis doivent tourner sur le thread ui, on utilise cette méthode pour le faire
            @Override
            public void run() {
                Toast alert = Toast.makeText(activity,text,Toast.LENGTH_SHORT); //Activity hérite de context
                alert.show();
            }
        });
    }

    @Override
    public void run() {
        for (String site : MainActivity.apis) {
            try {
                URL url = new URL(site + "/api/v2/status.json");
                URLConnection conn = url.openConnection(); //ajouter la permission internet dans le manifest !
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String total = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    total += line;
                }
                reader.close();
                Log.d("JSON",total);
                JSONObject json = new JSONObject(total);
                createCard(json);
            } catch (MalformedURLException e) {
                displayAlert("Mauvaise URL");
            } catch (JSONException e) {
                displayAlert("Erreur JSON");
            } catch (IOException e) {
                displayAlert("Erreur de donnée pour un service");
            }
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
        apis.add("https://status.scaleway.com");

        Thread thread = new Thread(new LoadAPI(this)); //le réseau doit être fait dans un thread externe
        thread.start();

        Button more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AllServicesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left+20, systemBars.top, systemBars.right+20, systemBars.bottom);
            return insets;
        });

        init();
    }
}