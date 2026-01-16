package com.example.netstatus;

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
}
