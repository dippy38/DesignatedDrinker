package com.mjhutti.designateddrinker;

/**
 * Created by mjhutti on 03/12/2014.
 */
public class Marker {

    private int dispenserID;
    private String name;
    private String drinks;
    private double lat;
    private double lng;

    public Marker(int dispenserID, String name, String drinks, double lat, double lng){
        this.dispenserID= dispenserID;
        this.name=name;
        this.drinks=drinks;
        this.lat=lat;
        this.lng=lng;
    }

    public int getDispenserID() {
        return dispenserID;
    }

    public void setDispenserID(int dispenserID) {
        this.dispenserID = dispenserID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDrinks() {
        return drinks;
    }

    public void setDrinks(String drinks) {
        this.drinks = drinks;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


}