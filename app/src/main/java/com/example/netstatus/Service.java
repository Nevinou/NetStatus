package com.example.netstatus;

import java.util.StringTokenizer;

public class Service {
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
}
