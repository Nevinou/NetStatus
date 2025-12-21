package com.example.netstatus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    List<String> apis;

    void init(){
        //ajout des urls qui utilisent statuspage.io
        apis = new ArrayList<String>();
        apis.add("https://discordstatus.com");
        apis.add("https://www.githubstatus.com");
        apis.add("https://www.cloudflarestatus.com");
        apis.add("https://www.redditstatus.com");
        apis.add("https://status.atlassian.com");
        apis.add("https://status.dropbox.com");

        //le réseau doit être fait dans un thread externe
        Thread thread = new Thread(new LoadAPI(this,apis));
        thread.start();

        //on prend le boutton de l'activity qui a l'id "more"
        Button more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on fait un itent pour changer de page
                Intent intent = new Intent(MainActivity.this,AllServicesActivity.class);
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