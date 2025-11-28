package com.example.netstatus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static List<String> apis;

    void displayAlert(String text){
        Toast alert = Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT);
        alert.show();
    }
    void init(){
        apis = new ArrayList<String>();
        apis.add("https://discordstatus.com");
        apis.add("https://www.githubstatus.com");
        apis.add("https://www.githubstatus.com");
        apis.add("https://www.cloudflarestatus.com");
        apis.add("https://www.redditstatus.com");

        for (String site:apis){
            try {
                URL url = new URL(site+"/api/v2/status.json");
                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                //le flux fait crash l'app pour une raison inconnue...
                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println("Allo");
                }

                reader.close();
            } catch (MalformedURLException e) {
                displayAlert("Mauvaise URL");
            } catch (IOException e) {
                displayAlert("Erreur de donnée pour un service");
            }
        }

        CardView service1 = findViewById(R.id.card_service_1); // doit être fait après le setContentView !

        service1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                intent.putExtra("Name","Github");
                startActivity(intent);
            }
        });

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