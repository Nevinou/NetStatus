package com.example.netstatus;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class LoadApiDetail  implements Runnable{
    Activity activity;
    Service service;

    // Constructeur
    LoadApiDetail (Activity activity, Service urlApi){
        this.activity = activity;
        this.service = urlApi;
    }
    void addLogo (Bitmap logo){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView image = activity.findViewById(R.id.logo);
                image.setImageBitmap(logo);
            }
        });
    }
    void addProbleme (JSONArray incidents) {
        if (incidents.length()==0){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout card = activity.findViewById(R.id.cardPb);
                    TextView pasPb = new TextView(activity);
                    pasPb.setText(activity.getString(R.string.pasPb));
                    pasPb.setTextColor(activity.getColor(R.color.status_operational));
                    card.addView(pasPb);
                }
            });
        } else {
            //Log.d("text","Trace 1 "+incidents);
            for (int i = 0; i < incidents.length();i++){
                try {
                    ajoutCardProbleme(incidents.getJSONObject(i));
                } catch (JSONException e) {
                    Log.e("ProblemLaod", "Erreur lors de la récupération de l'incident: " + e.getMessage(),e);
                }
            }
        }
    }

    void ajoutCardProbleme(JSONObject incident)  {
        // Extraction des données du JSONObject
        String name = activity.getString(R.string.unknown);
        String hour = extractHour(incident);
        String impact = activity.getString(R.string.unknown);
        int color = activity.getColor(R.color.status_inconnu);
        int colorBg = activity.getColor(R.color.status_inconnu_bg);
        try {
            name = incident.getString("name");
            StringTokenizer st = new StringTokenizer(Service.status(incident.getString("impact"),activity),";");
            impact = st.nextToken();
            color = Integer.parseInt(st.nextToken());
            colorBg = Integer.parseInt(st.nextToken());
        } catch (JSONException e ){
            Log.e("ProblemLaod","Erreur lors de la lecture des données de l'incident : " + e.getMessage(),e);
        } catch (NoSuchElementException e){
            Log.e("ProblemLaod","Format de statut invalide : " + e.getMessage(),e);
        } catch (NumberFormatException e) {
            Log.e("ProblemLaod","Erreur inattendue: " + e.getMessage(),e);
        }

        String finalName = name;
        int finalColor = color;
        int finalColorBg = colorBg;
        String finalImpact = impact;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LinearLayout cardPb = activity.findViewById(R.id.cardPb);
                // Récupère le template de Problème
                LayoutInflater inflater = LayoutInflater.from(activity);
                CardView templatePb = (CardView) inflater.inflate(R.layout.template_probleme,cardPb,false);
                templatePb.setBackgroundTintList(ColorStateList.valueOf(finalColorBg));
                // Modifie les éléments du template
                TextView nameTextView = templatePb.findViewById(R.id.namePb);
                nameTextView.setText(finalName);
                // AJout de l'heure de dernière modification
                TextView clockLastUpdate = templatePb.findViewById(R.id.lastModif);
                clockLastUpdate.setText(hour);
                clockLastUpdate.setTextColor(finalColor);
                // Ajout de l'impact
                TextView impactTextView = templatePb.findViewById(R.id.impact);
                impactTextView.setText(finalImpact);
                impactTextView.setTextColor(finalColor);
                //Log.d("test",finalImpact);
                // Modification couleur rond
                View impactIndicator = templatePb.findViewById(R.id.impactIndicator);
                impactIndicator.setBackgroundTintList(ColorStateList.valueOf(finalColor));
                // Modification couleur du background de l'indicateur
                LinearLayout indicator = templatePb.findViewById(R.id.indicator);
                indicator.setBackgroundColor(finalColorBg);
                // AJout du template à l'activity
                cardPb.addView(templatePb);
            }
        });
    }
    void addMaintenances (JSONArray maintenances) {
        if (maintenances.length() ==0){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout card = activity.findViewById(R.id.cardMaintenance);
                    TextView pasPb = new TextView(activity);
                    pasPb.setText(activity.getString(R.string.pasMaintenance));
                    pasPb.setTextColor(activity.getColor(R.color.status_operational));
                    card.addView(pasPb);
                }
            });
        } else {
            for (int i = 0; i<maintenances.length();i++){
                try {
                    ajoutCardMaintenance(maintenances.getJSONObject(i));
                } catch (JSONException e) {
                    Log.e("maintenanceLoad", "Erreur de la lecture des données de l'incident : " + e.getMessage(),e);
                }
            }
        }
    }

    void ajoutCardMaintenance (JSONObject maintenance){
        // Valeur par défaut
        String name = activity.getString(R.string.unknown);
        String status = activity.getString(R.string.unknown);
        String impact = activity.getString(R.string.unknown);
        ArrayList<String> dateHour = dateHourMaintenance(maintenance);
        try {
            name = maintenance.getString("name");
            status = maintenance.getString("status");
            impact = maintenance.getString("impact");
        } catch (JSONException e ){
            Log.e("maintenanceLoad","Erreur lors de la lecture des données de l'incident : " + e.getMessage(),e);
        }
        String finalName = name;
        StringTokenizer st = new StringTokenizer(Service.status(impact,activity),";");
        String finalImpact = st.nextToken();
        int color = Integer.parseInt(st.nextToken());
        String finalStatus = Service.statusMaintenance(status,activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout card = activity.findViewById(R.id.cardMaintenance);
                LayoutInflater inflater = LayoutInflater.from(activity);
                CardView template = (CardView) inflater.inflate(R.layout.template_maintenance,card,false);
                // Modification du title
                TextView title = template.findViewById(R.id.maintenanceTitle);
                title.setText(finalName);
                // Modification de la date de début et fin
                TextView hourDebut = template.findViewById(R.id.heure_debut);
                hourDebut.setText(dateHour.get(0));
                TextView dateDebut = template.findViewById(R.id.date_debut);
                dateDebut.setText(dateHour.get(1));
                TextView hourFin = template.findViewById(R.id.heure_fin);
                hourFin.setText(dateHour.get(2));
                TextView dateFin = template.findViewById(R.id.date_fin);
                dateFin.setText(dateHour.get(3));
                // Modification du status
                TextView status = template.findViewById(R.id.maintenanceStatus);
                status.setText(finalStatus);
                // Impacte de la maintenance
                TextView impact = template.findViewById(R.id.impact);
                impact.setText(finalImpact);
                impact.setTextColor(color);

                // Impacte indicator changement de couleur
                View impactIndicator = template.findViewById(R.id.impactIndicator);
                impactIndicator.setBackgroundTintList(ColorStateList.valueOf(color));
                card.addView(template);
            }
        });
    }
    ArrayList<String> dateHourMaintenance (JSONObject maintenance){
        ArrayList<String> dateHour = new ArrayList<String>();
        try {
            // Extraire les Date/heure du json
            String scheduledFor = maintenance.getString("scheduled_for");
            String scheduledUntil = maintenance.getString("scheduled_until");
            // Format de sortie
            DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());

            OffsetDateTime dateDebut = OffsetDateTime.parse(scheduledFor);
            //notre date est ISO 8601, on instancie un objet date ISO 8601
            dateHour.add(hourFormat.format(dateDebut));  // [0] Heure de début
            dateHour.add(dateFormat.format(dateDebut));  // [1] Date de début

            OffsetDateTime dateFin = OffsetDateTime.parse(scheduledUntil);
            //notre date est ISO 8601, on instancie un objet date ISO 8601
            dateHour.add(hourFormat.format(dateFin));    // [2] Heure de fin
            dateHour.add(dateFormat.format(dateFin));    // [3] Date de fin

        }catch (JSONException | DateTimeParseException e) {
            dateHour.add("--:--");
            dateHour.add("--/--/----");
            dateHour.add("--:--");
            dateHour.add("--/--/----");
            Log.e("DateParsing", "Erreur inattendue : " + e.getMessage(), e);
        }
        return dateHour;
    }
    void addComponents (JSONArray components){
        if (components.length() == 0){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout card = activity.findViewById(R.id.cardComponents);
                    TextView pasPb = new TextView(activity);
                    pasPb.setText(activity.getString(R.string.pasComponents));
                    pasPb.setTextColor(activity.getColor(R.color.hard_broken));
                    card.addView(pasPb);
                }
            });
        } else {
            for (int i = 0; i<components.length();i ++){
                try {
                    JSONObject component = components.getJSONObject(i);
                    addComponent(component);
                } catch (JSONException e) {
                    Log.e("componentLoad","Composant invalide à l'index " + i + " : " + e.getMessage(),e);
                }
            }
        }

    }

    void addComponent (JSONObject component) {
        String name;
        String status;
        try {
            name = component.getString("name");
            status = component.getString("status");
        } catch (JSONException e) {
            name = activity.getString(R.string.unknowName);
            status = activity.getString(R.string.unknown);
            Log.e("componentLoad","Erreur lors de la récupération des noms: "+e.getMessage(),e);
        }
        String finalName = name;
        StringTokenizer st = new StringTokenizer(Service.statusComponent(status,activity),";");
        String finalStatus = st.nextToken();
        int color = Integer.parseInt(st.nextToken());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Récupération card component
                LinearLayout card = activity.findViewById(R.id.cardComponents);
                // Création d'une nouvelle template
                LayoutInflater inflater = LayoutInflater.from(activity);
                LinearLayout template = (LinearLayout) inflater.inflate(R.layout.template_component,card,false);
                // textView Name component
                TextView title = template.findViewById(R.id.nameComponent);
                title.setText(finalName);
                // TextView status
                TextView textStatus = template.findViewById(R.id.statusComponent);
                textStatus.setText(finalStatus);
                textStatus.setTextColor(color);
                // Indicator
                View indicator = template.findViewById(R.id.componentIndicator);
                indicator.setBackgroundTintList(ColorStateList.valueOf(color));

                card.addView(template);
            }
        });
    }

    String extractHour(JSONObject json){
        String delta;
        try {
            String input = json.getString("updated_at");

            OffsetDateTime isoDate = OffsetDateTime.parse(input);

            //Date actuelle en UTC+0, plus moderne que Date
            Instant now = Instant.now();
            //conversion de l'objet OffsetDateTime ISO vers Instant pour etre aussi en UTC+0
            Instant dateUpdate = isoDate.toInstant();

            // Calculer la différence en millisecondes
            long diffMillis = now.toEpochMilli()-dateUpdate.toEpochMilli();
            long diffMinutes = diffMillis / (60 * 1000);
            long diffHours = diffMinutes / 60;
            long remainingMinutes = diffMinutes % 60;
            long diffDays = diffHours / 24;

            // Formater selon la durée
            if (diffDays > 0) {
                delta = "Il y a " + diffDays + " j";
            } else if (diffHours > 0) {
                delta = "Il y a " + diffHours + " h " + remainingMinutes + " min";
            } else if (diffMinutes > 0) {
                delta = "Il y a " + diffMinutes + " min";
            } else {
                delta = "À l'instant";
            }

        } catch (JSONException | DateTimeParseException e) {
            Log.e("DateParsing","Erreur lors de l'extraction de l'heure : " + e.getMessage(),e);
            delta = "Il y a -- h";
        }
        return delta;
    }

    void addGeneralStatus (String statusCouleur, JSONObject json) throws NumberFormatException{
        StringTokenizer st = new StringTokenizer(statusCouleur,";");
        String status = st.nextToken();
        int color = Integer.parseInt(st.nextToken());
        int colorBg = Integer.parseInt(st.nextToken());
        String hour;
        try {
            hour = extractHour(json.getJSONObject("page"));
        } catch (JSONException e) {
            hour = "Il y a -- h";
            Log.e("statusGeneLoad", "Erreur lors de la récupération de l'heure: " + e.getMessage(),e);
        }
        String finalHour = hour;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Modification du texte et de la couleur
                TextView textStatus = activity.findViewById(R.id.statusText);
                textStatus.setText(status);
                textStatus.setTextColor(color);
                View indicator = activity.findViewById(R.id.statusIndicator);
                indicator.setBackgroundTintList(ColorStateList.valueOf(color));
                // Couleur background
                CardView bg = activity.findViewById(R.id.statusGeneralBG);
                bg.setBackgroundTintList(ColorStateList.valueOf(colorBg));
                //CardView bg = activity.findViewById(R.id.statusGeneralBG);
                //bg.setBackgroundColor(colorBg);
                // Horloge
                TextView clock = activity.findViewById(R.id.textClock);
                clock.setText(finalHour);
                clock.setTextColor(color);

            }
        });
    }
    Bitmap loadLogo () throws MalformedURLException, IOException{
        Bitmap logo;
        // vu que la méthode setImageURI
        InputStream inputImage = new URL(service.getImage()).openStream();
        // de l'imageView ne prend que des URI locaux,
        // il faut download l'image depuis internet en bitmap
        // on transforme l'image en bitmap
        logo = BitmapFactory.decodeStream(inputImage);
        inputImage.close();
        return logo;
    }

    JSONObject loadJson (String path){
        try {
            URL url = new URL(service.getApi() + "/api/v2/"+path); //création url
            URLConnection conn = url.openConnection(); //ajouter la permission internet dans le manifest ! Ouverture de la connexion
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); // Flux de lecture
            String pageWeb = "";
            String line;
            while ((line = reader.readLine()) != null) {
                pageWeb += line; //lecture de toute les lignes
            }
            reader.close();
            //Log.d("JSON",total);
            return new JSONObject(pageWeb); //création d'un JSONObject
        } catch (MalformedURLException e) {
            Log.e("APICall", "URL invalide : " + e.getMessage(), e);
        } catch (IOException e) {
            Log.e("APICall", "Erreur réseau : " + e.getMessage(), e);
        } catch (JSONException e) {
            Log.e("APICall", "Réponse JSON invalide : " + e.getMessage(), e);
        }
        return null;
    }

    String extractGeneralStatus (JSONObject json){
        String generalStatus = "";
        try {
            JSONObject stat = json.getJSONObject("status");
            generalStatus = stat.getString("indicator");
        } catch (JSONException e) {
            Log.e("StatusParsing", "Erreur JSON : " + e.getMessage(), e);
            generalStatus = activity.getString(R.string.unknown);
        }
        return generalStatus;
    }

    static JSONArray extractDataFromJson (JSONObject json, String name){
        JSONArray data;
        try {
            data = json.getJSONArray(name);
            //Log.d("test",incidents.toString());
        } catch (JSONException e ){
            Log.e("DataParcing", "Erreur lors de la récupération du champs "+ name +" : " + e.getMessage(), e);
            data = new JSONArray();
        }
        return data;
    }

    void displayAlert(String text){
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
        //
        // Requête Image
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap logo;
                try {
                    logo = loadLogo();
                    // Ajout du log sur l'interface graphique
                    addLogo(logo);
                } catch (MalformedURLException e) {
                    Log.e("ImageLoad", "Erreur inattendue: " + e.getMessage());
                } catch (IOException e) {
                    Log.e("ImageLoad", "Erreur lors du téléchargement de l'image: " + e.getMessage());
                }
            }
        }).start();

        //
        // Récupération du json de l'api et mise dans les différentes variables
        //
        JSONObject json = loadJson("summary.json");

        //
        // Requête Status Général
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                String generalStatus = extractGeneralStatus(json);
                addGeneralStatus(Service.status(generalStatus,activity),json);
            }
        }).start();


        //
        // Requête Problème en cours
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray incidents = extractDataFromJson(json,"incidents");
                addProbleme(incidents);
            }
        }).start();


        //
        // Affichage des maintenance prévue
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray maintenances = extractDataFromJson(json,"scheduled_maintenances");
                // Log.d("test","Maintenances : "+ maintenances);
                addMaintenances(maintenances);
            }
        }).start();


        //
        // Affichage des component et de leur état
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray components = extractDataFromJson(json,"components");
                addComponents(components);
            }
        }).start();


        //
        // Boutton dropdown pour les différents champ (Maintenance, composant, probleme)
        //
        // Bouton Maintenance
        ImageButton buttonMaintenance = activity.findViewById(R.id.dropdown_maintenance);
        buttonMaintenance.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout maintenance = activity.findViewById(R.id.cardMaintenance);
                                ImageButton buttonMaintenance = activity.findViewById(R.id.dropdown_maintenance);

                                if (maintenance.getVisibility() == View.VISIBLE){
                                    maintenance.setVisibility(View.GONE);
                                    buttonMaintenance.setSelected(true);
                                } else {
                                    maintenance.setVisibility(View.VISIBLE);
                                    buttonMaintenance.setSelected(false);
                                }

                            }
                        });
                    }
                }
        );
        ImageButton buttonProbleme = activity.findViewById(R.id.dropdown_probleme);
        buttonProbleme.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout maintenance = activity.findViewById(R.id.cardPb);
                                ImageButton buttonMaintenance = activity.findViewById(R.id.dropdown_probleme);

                                if (maintenance.getVisibility() == View.VISIBLE){
                                    maintenance.setVisibility(View.GONE);
                                    buttonMaintenance.setSelected(true);
                                } else {
                                    maintenance.setVisibility(View.VISIBLE);
                                    buttonMaintenance.setSelected(false);
                                }

                            }
                        });
                    }
                }
        );
        ImageButton buttonComponents = activity.findViewById(R.id.dropdown_components);
        buttonComponents.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout maintenance = activity.findViewById(R.id.cardComponents);
                                ImageButton buttonMaintenance = activity.findViewById(R.id.dropdown_components);

                                if (maintenance.getVisibility() == View.VISIBLE){
                                    maintenance.setVisibility(View.GONE);
                                    buttonMaintenance.setSelected(true);
                                } else {
                                    maintenance.setVisibility(View.VISIBLE);
                                    buttonMaintenance.setSelected(false);
                                }

                            }
                        });
                    }
                }
        );










    }
}
