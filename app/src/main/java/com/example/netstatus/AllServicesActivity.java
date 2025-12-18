package com.example.netstatus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AllServicesActivity extends AppCompatActivity {
    List<String> apis;
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

        Thread thread = new Thread(new LoadAPI(this,apis)); //le réseau doit être fait dans un thread externe
        thread.start();

        FloatingActionButton back = findViewById(R.id.retour);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SearchView search = findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextChange(String newText) { //appuit sur une touche
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) { //recherche
                //clear la view, inflate des nouvelles cards qui matchent
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_services);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left+20, systemBars.top, systemBars.right+20, systemBars.bottom);
            return insets;
        });

        init();
    }
}