package com.example.netstatus;

import android.app.Activity;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Service implements Serializable {
    private String name;
    private String api;
    private String image;
    private boolean favorite;

    Service(String name, String api, String image, boolean favorite){
        this.name = name;
        this.api = api;
        this.image = image;
        this.favorite = favorite;
    }

    Service(String serviceCsv){
        // Créer un service grâce à avec un string de la forme
        // "{name};{api};{image};{favorite}"
        StringTokenizer st = new StringTokenizer(serviceCsv,";");
        name = st.nextToken();
        api = st.nextToken();
        image = st.nextToken();
        favorite = Boolean.parseBoolean(st.nextToken());
    }

    public String toString(){
        return "Application "+name+" avec comme api "+api+" et comme image "+image+" favori : "+favorite;
    }

    public String getName() {
        return name;
    }

    public String getApi() {
        return api;
    }

    public String getImage() {
        return image;
    }
    public boolean getFavorite(){
        return favorite;
    }
    public void setFavorite(boolean value){
        favorite = value;
    }
    static String status(String indicator, Activity activity){
        String status;
        int color,colorBg;
        switch (indicator) {
            case "none":
                //récupération du string du fichier xml
                status = activity.getString(R.string.oper);
                color = activity.getColor(R.color.oper);
                colorBg = activity.getColor(R.color.status_operational_bg);
                break;
            case "minor":
                status = activity.getString(R.string.warn);
                color = activity.getColor(R.color.warn);
                colorBg = activity.getColor(R.color.status_mineur_bg);
                break;
            case "major":
                status = activity.getString(R.string.broken);
                color = activity.getColor(R.color.broken);
                colorBg = activity.getColor(R.color.status_majeur_bg);
                break;
            case "critical":
                status = activity.getString(R.string.hard_broken);
                color = activity.getColor(R.color.hard_broken);
                colorBg = activity.getColor(R.color.status_critique_bg);
                break;
            case "maintenance":
                status = activity.getString(R.string.disabled);
                color = activity.getColor(R.color.disabled);
                colorBg = activity.getColor(R.color.status_maintenance_bg);
                break;
            default:
                status = activity.getString(R.string.unknown);
                color = activity.getColor(R.color.unknown);
                colorBg = activity.getColor(R.color.status_inconnu_bg);
                break;
        }
        return status+";"+color+";"+colorBg;
    }
    static String statusComponent (String indicator, Activity activity){
        String status;
        int color,colorBg;
        switch (indicator) {
            case "operational":
                //récupération du string du fichier xml
                status = activity.getString(R.string.oper);
                color = activity.getColor(R.color.oper);
                colorBg = activity.getColor(R.color.status_operational_bg);
                break;
            case "degraded_performance":
                status = activity.getString(R.string.degraded_performance);
                color = activity.getColor(R.color.warn);
                colorBg = activity.getColor(R.color.status_mineur_bg);
                break;
            case "partial_outage":
                status = activity.getString(R.string.partial_outage);
                color = activity.getColor(R.color.broken);
                colorBg = activity.getColor(R.color.status_majeur_bg);
                break;
            case "major_outage":
                status = activity.getString(R.string.major_outage);
                color = activity.getColor(R.color.hard_broken);
                colorBg = activity.getColor(R.color.status_critique_bg);
                break;
            default:
                status = activity.getString(R.string.unknown);
                color = activity.getColor(R.color.unknown);
                colorBg = activity.getColor(R.color.status_inconnu_bg);
                break;
        }
        return status+";"+color+";"+colorBg;
    }

    static String statusMaintenance (String indicator, Activity activity){
        String status;
        switch (indicator) {
            case "scheduled":
                //récupération du string du fichier xml
                status = activity.getString(R.string.scheduled);
                break;
            case "in_progress":
                status = activity.getString(R.string.in_progress);
                break;
            case "verifying":
                status = activity.getString(R.string.verifying);
                break;
            case "completed":
                status = activity.getString(R.string.completed);
                break;
            case "resolved":
                status = activity.getString(R.string.resolved);
                break;
            default:
                status = activity.getString(R.string.unknown);
                break;
        }
        return status;
    }
}
