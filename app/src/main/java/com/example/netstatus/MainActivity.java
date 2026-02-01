package com.example.netstatus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends NetworkDetector {
    List<Service> services;
    LinearLayout layout;

    void loadAll(){
        SQLGestion gestionnaire = new SQLGestion(this); //création objet db
        services = gestionnaire.getAllServices();
        if (services.isEmpty()){
            makeSQL(gestionnaire); //run si la BDD est vide
            services = gestionnaire.getAllServices();
        }
    }

    void makeSQL(SQLGestion gestionnaire){
        //images en SVG ne chargent pas !
        gestionnaire.addService("GitHub","https://www.githubstatus.com","https://icones.pro/wp-content/uploads/2021/06/icone-github-noir.png");
        gestionnaire.addService("Discord","https://discordstatus.com","https://pngimg.com/d/discord_PNG3.png");
        gestionnaire.addService("Cloudflare","https://www.cloudflarestatus.com","https://boostmypresta.com/109-large_default/configuration-cloudflare.jpg");
        gestionnaire.addService("Reddit","https://www.redditstatus.com","https://upload.wikimedia.org/wikipedia/en/thumb/b/bd/Reddit_Logo_Icon.svg/250px-Reddit_Logo_Icon.svg.png");
        gestionnaire.addService("Atlassian","https://status.atlassian.com","https://miro.medium.com/1*XBjByL76rrguN7qEqd1KzA.jpeg");
        gestionnaire.addService("Shopify","https://shopify.statuspage.io","https://cdn-icons-png.flaticon.com/512/5968/5968919.png");
        gestionnaire.addService("DigitalOcean","https://status.digitalocean.com","https://cdn.iconscout.com/icon/free/png-256/free-digital-ocean-logo-icon-svg-download-png-3029953.png");
        gestionnaire.addService("Dropbox","https://status.dropbox.com","https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Dropbox_Icon.svg/960px-Dropbox_Icon.svg.png");
        gestionnaire.addService("Twilio","https://status.twilio.com","https://ewebinar.com/hubfs/twilio-logo-2.png");
        gestionnaire.addService("New Relic","https://status.newrelic.com","https://s3.eu-west-1.amazonaws.com/www.jobfluent.com/company_logos/1/2/7/127_170_196.png");
        gestionnaire.addService("AWeber","https://status.aweber.com","https://s3-eu-west-1.amazonaws.com/tpd/logos/46d38432000064000500b19f/0x0.png");
        gestionnaire.addService("Duo","https://status.duo.com","https://pbs.twimg.com/profile_images/1661452580772368395/8i70taZS_400x400.png");
        gestionnaire.addService("Librato","https://status.librato.com","https://s3.amazonaws.com/awsmp-logos/9-29-Librato-Company.png");
        gestionnaire.addService("Coinbase","https://status.coinbase.com","https://s3-symbol-logo.tradingview.com/coinbase--600.png");
        gestionnaire.addService("iAdvize","https://status.iadvize.com","https://apps.oxatis.com/Files/112496/Img/11/Apps-IAdvize.png");
        gestionnaire.addService("Searchspring","https://status.searchspring.com","https://cdn.shopify.com/app-store/listing_images/050e23926e6a37f5c54cb66b9fed66dc/icon/CJ-Rg7L0lu8CEAE=.png");
        gestionnaire.addService("OpenText","https://status.opentext.com","https://pbs.twimg.com/profile_images/1810309639046606848/PT9magcD_400x400.jpg");
        gestionnaire.addService("Mindbody","https://status.mindbodyonline.com","https://521324.fs1.hubspotusercontent-na1.net/hubfs/521324/__hs-marketplace__/mindbody-B2C-logomark-1.png");
        gestionnaire.addService("Stitch Data","https://status.stitchdata.com","https://www.stitchdata.com/images/og-site-icon.png");
        gestionnaire.addService("New York University Division of Libraries","https://alerts.library.nyu.edu","https://www.pngitem.com/pimgs/m/336-3368911_logo-new-york-university-hd-png-download.png");
        gestionnaire.addService("Intuit Developer Group","https://status.developer.intuit.com","https://quickbooks.intuit.com/oidam/intuit/sbseg/en_us/Blog/Logos/intuit-logo-image-us-en.png");
        gestionnaire.addService("Epic Games","https://status.epicgames.com","https://upload.wikimedia.org/wikipedia/commons/a/a7/Epic_Games_logo.png");
        gestionnaire.addService("Scaleway","https://status.scaleway.com","https://images.icon-icons.com/2407/PNG/512/scaleway_icon_146103.png");
        //ajout des urls qui utilisent statuspage.io + image + nom (utile pour la recherche)
    }

    List<Service> getFavoris(){
        List<String> bestNames = new ArrayList<String>();
        bestNames.add("Discord");
        bestNames.add("Epic Games");
        bestNames.add("Cloudflare");
        //nom des services en cas de non favoris

        List<Service> bestServices = new ArrayList<Service>();
        //liste a afficher en cas de non favoris
        List<Service> favories = new ArrayList<Service>();
        for(Service serv:services) {
            //Log.d("Resultat",serv.toString());
            if (serv.getFavorite()){
                favories.add(serv);
            }
            if (bestNames.contains(serv.getName())){
                bestServices.add(serv);
            }
        }
        if (favories.isEmpty()){
            return bestServices;
        }
        return favories;
    }

    void init(){

        //on prend le boutton de l'activity qui a l'id "more"
        Button more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on fait un itent pour changer de page
                Intent intent = new Intent(MainActivity.this,AllServicesActivity.class);
                intent.putExtra("allServices", (ArrayList<Service>)services);
                startActivity(intent); //on change d'activity
            }
        });

    }

    @Override
    void resumeApp(){
        if (services != null){
            hideError();
            //clear si les frames existes deja
            List<Service> favories = getFavoris();
            Thread thread = new Thread(new LoadAPI(this,favories));
            thread.start();
        }
    }

    @Override
    void hideAction(){
        layout.removeAllViews();//UI thread
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); //rend visible

        layout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(layout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left+20, systemBars.top, systemBars.right+20, systemBars.bottom);
            return insets;
        });

        init(); //appel de la méthode init
    }

    @Override
    protected void onResume() { //run a chaque affichage
        //run au démarrage et a chaque reprise due au finish()
        super.onResume();

        layout.removeAllViews(); //clear

        loadAll();

        List<Service> favories = getFavoris();

        boolean internet = checkInternet();

        if (!internet){
            displayError();
        }
        else{
            //le réseau doit être fait dans un thread externe
            Thread thread = new Thread(new LoadAPI(this,favories));
            thread.start();
        }
    }
}