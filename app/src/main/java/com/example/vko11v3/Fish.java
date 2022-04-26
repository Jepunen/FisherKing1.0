package com.example.vko11v3;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Fish implements Serializable {

    String title;
    Double weight;
    boolean inGrams;
    Double length;
    String picture;
    String latitude;
    String longitude;
    String date;
    Double tempCelcius;
    String locality;


    //Constructor for when location data is available
    public Fish(String title, Double weight, boolean inGrams,Double length, String picture, String latitude, String longitude, Double tempCelcius, String locality) {
        this.title = title;
        this.weight = weight;
        this.inGrams = inGrams;
        this.length = length;
        this.picture = picture;
        this.latitude = latitude;
        this.longitude = longitude;
        Calendar cal = Calendar.getInstance(); // Save as object created
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        this.date = date.format(cal.getTime());
        this.tempCelcius = tempCelcius;
        this.locality = locality;
    }

    // Constructor for when location data not available
    public Fish(String title, Double weight, boolean inGrams, Double length, String picture) {
        this.title = title;
        this.weight = weight;
        this.inGrams = inGrams;
        this.length = length;
        this.picture = picture;
        this.locality = null;
        Calendar cal = Calendar.getInstance(); // Save as object created
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        this.date = date.format(cal.getTime());
    }

    // Getters for all variables
    public String getTitle() {
        return title;
    }
    public Double getWeight() {
        return weight;
    }
    public Double getLength() {
        return length;
    }
    public String getPicture() {
        return picture;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public String getLocality() {
        return locality;
    }
    public boolean isInGrams() {
        return inGrams;
    }
    public String getDate() {
        return date;
    }

    // Setters for all variables
    public void setTitle(String title) {
        this.title = title;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public void setLength(Double length) {
        this.length = length;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public void setInGrams(boolean inGrams) {
        this.inGrams = inGrams;
    }
    public void setDate(String date) {
        this.date = date;
    }
}