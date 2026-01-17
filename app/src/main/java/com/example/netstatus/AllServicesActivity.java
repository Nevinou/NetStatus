package com.example.netstatus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    List<Service> services;
    int count;
    final int increment = 10;

    List<Service> loadMore(){
        List<Service> next = new ArrayList<Service>();
        for (int i=count-increment;i<count;i++){
            if (i<services.size()){
                next.add(services.get(i));
            }
        }
        return next;
    }

    void filterServices(String text) {
        LinearLayout layout = findViewById(R.id.main);
        List<Service> toLoad = new ArrayList<Service>();

        layout.removeAllViews();
        for (Service elem : services) {
            if (elem.getName().toLowerCase().contains(text.toLowerCase())) {
                toLoad.add(elem);
            }
        }
        Thread thread = new Thread(new LoadAPI(this,toLoad)); //on charge la suite
        thread.start();
    }
    void init(){;
        Intent intent = getIntent();
        services = (ArrayList<Service>) intent.getSerializableExtra("allServices");
        count = 10;

        List<Service> mustLoad = loadMore();
        Activity current = this;

        Thread thread = new Thread(new LoadAPI(current,mustLoad)); //le réseau doit être fait dans un thread externe
        thread.start();

        FloatingActionButton back = findViewById(R.id.retour);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ScrollView scroll = findViewById(R.id.scroll);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            long last = 0; // doit être ici, Java n’autorise pas la modification d’une variable locale de la méthode à l’intérieur d’une classe interne anonyme
            @Override
            public void onScrollChanged() {
                LinearLayout layout = findViewById(R.id.main);

                int diff = layout.getBottom() - (scroll.getHeight() + scroll.getScrollY());
                //diff = distance entre le bas de la frame et le bas visible
                //getHeight : Retourne la hauteur visible du ScrollView, en pixels
                //getScrollY : Retourne la distance verticale déjà scrollée depuis le haut, en pixels
                long time = System.currentTimeMillis(); //temps en ms depuis 1er janvier 1970
                if (diff <= 0 && (time-last) >= 1000){ //on vérifie que l'on a 1s entre les requetes pour ne pas charger en double car l'event peut s'activer plusieurs fois alors que l'on est en bas
                    last = time;
                    count += increment;
                    List<Service> mustLoad = loadMore();
                    Thread thread = new Thread(new LoadAPI(current,mustLoad)); //on charge la suite
                    thread.start();
                    //Log.d("Time","load");
                }
            }
        });

        SearchView search = findViewById(R.id.search); //barre de recherche
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            long last = 0;

            @Override
            public boolean onQueryTextChange(String newText) { //appuit sur une touche
                if (newText.equals("")){
                    count = 10;
                    List<Service> mustLoad = loadMore();
                    Thread thread = new Thread(new LoadAPI(current,mustLoad));
                    thread.start();
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                long time = System.currentTimeMillis(); //temps en ms depuis 1er janvier 1970
                if ((time-last) < 1000){
                    return true;
                }
                last = time;
                filterServices(query);
                return true;
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