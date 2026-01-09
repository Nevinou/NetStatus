package com.example.netstatus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    DetailsActivity (String status, String urlApi, String serviceName){
        this.status = status;
        this.urlApi = urlApi;
        this.serviceName = serviceName;
        initGeneralStatus();
    }

    @NonNull
    @Override
    public String toString() {
        return serviceName + " | "+urlApi+" | Status : "+status;
    }

    void initGeneralStatus (){
        TextView statusGlobal = findViewById(R.id.global_status);
        statusGlobal.setText((CharSequence) status);
    }
    void init(){
        Intent datas = getIntent();
        Log.d("test","trace 1");
        DetailsActivity service = new DetailsActivity(datas.getStringExtra("status"),datas.getStringExtra("LienAPI"),datas.getStringExtra("serviceName"));
        Log.d("test",service.toString());
        TextView title = findViewById(R.id.nomService);
        title.setText(service.serviceName);

        // Création du boutons pour revenir en arrière
        FloatingActionButton back = findViewById(R.id.retour);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Thread thread = new Thread(new LoadApiDetail(this,urlApi));
        //thread.start();

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