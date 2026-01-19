package com.example.netstatus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {
    final HashMap<String,String> dicoSatus = new HashMap<>();
    String status,urlApi,serviceName;

    @NonNull
    @Override
    public String toString() {
        return serviceName + " | "+urlApi+" | Status : "+status;
    }


    void init(){
        Intent datas = getIntent();
        // Création du service
        // Récupération de l'objet Title et on set le text avec le nom du service transmis par mainActivity
        Service service =  (Service) datas.getSerializableExtra("service");

        //
        // Requètes api et affichage des éléments visuel
        // Voir méthode run de la class LoadApiDetail
        //
        LoadApiDetail api = new LoadApiDetail(this,service);
        Thread thread = new Thread(api);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) { }

        //
        // Ajout du titre
        //
        TextView title = findViewById(R.id.nomService);
        title.setText(service.getName());

        //
        // Création des Boutons
        //
        // Création du boutons pour revenir en arrière
        FloatingActionButton back = findViewById(R.id.retour);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Création du boutons d'actualisation
        ImageButton refresh = findViewById(R.id.actualliser);
        refresh.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.displayAlert("Refresh ...");
                        recreate();
                        // clear l'activity et rappeler la fonction qui fait les requètes et affiche les templates
                    }
                }
        );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left+20, systemBars.top, systemBars.right+20, systemBars.bottom);
            return insets;
        });

        init();
    }
}