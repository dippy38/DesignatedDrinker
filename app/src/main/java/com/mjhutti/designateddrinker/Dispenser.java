package com.mjhutti.designateddrinker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mjhutti on 03/12/2014.
 */
public class Dispenser {

    private int dispenserID;
    private String name;
    private String drinks;
    private double lat;
    private double lng;
    private Date dateAdded;


    public Dispenser(int dispenserID, String name, String drinks, double lat, double lng, Date dateAdded){
        this.dispenserID= dispenserID;
        this.name=name;
        this.drinks=drinks;
        this.lat=lat;
        this.lng=lng;
        this.dateAdded=dateAdded;
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

    public String getFormattedDrinks(){
        ArrayList drinksList = new ArrayList<String>(Arrays.asList(drinks.split(",")));
        for(int i=0; i<drinksList.size(); i++){
            drinksList.set(i,"\n"+drinksList.get(i));
        }
        String formatedString = drinksList.toString()
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();           //remove trailing spaces from partially initialized arrays

        return formatedString;
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


    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
