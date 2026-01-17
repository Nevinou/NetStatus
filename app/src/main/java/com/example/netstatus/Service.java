package com.example.netstatus;

import android.app.Activity;

import java.io.Serializable;
import java.util.Map;
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

    String toCsv (){
        return name+";"+api+";"+image+";"+favorite;
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

    static String status(String indicator, Activity activity){
        String status;
        int color;
        switch (indicator) {
            case "none":
                //récupération du string du fichier xml
                status = activity.getString(R.string.oper);
                color = activity.getColor(R.color.oper);
                break;
            case "minor":
                status = activity.getString(R.string.warn);
                color = activity.getColor(R.color.warn);
                break;
            case "major":
                status = activity.getString(R.string.broken);
                color = activity.getColor(R.color.broken);
                break;
            case "critical":
                status = activity.getString(R.string.hard_broken);
                color = activity.getColor(R.color.hard_broken);
                break;
            case "maintenance":
                status = activity.getString(R.string.disabled);
                color = activity.getColor(R.color.disabled);
                break;
            default:
                status = activity.getString(R.string.unknown);
                color = activity.getColor(R.color.unknown);
                break;
        }
        return status+";"+color;
    }
}
