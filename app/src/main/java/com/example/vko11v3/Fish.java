package com.example.vko11v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

//REMEMBER to insert temperature as a variable in fish constructor

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

    //TEMP -> dont think these are needed -> unless we cant fix empty file crash issue otherwise
    /*String titleWeight;
    String titleLength;
    String titleDate;
    String titleTempCelcius;*/


    /*public Fish(String title, Double weight, Double length, String picture, String latitude, String longitude, Long date) {
        this.title = title;
        this.weight = weight;
        this.length = length;
        this.picture = picture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }*/


    //Constructor with "Date date2" -> will be final when working with AddNewFishPopup
    public Fish(String title, Double weight, boolean inGrams,Double length, String picture, String latitude, String longitude, Double tempCelcius, String locality) {
    //public Fish(String title, Double weight, Double length, String picture, String latitude, String longitude) {
        this.title = title;
        this.weight = weight;
        this.inGrams = inGrams;
        this.length = length;
        this.picture = picture;
        this.latitude = latitude;
        this.longitude = longitude;
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        this.date = date.format(cal.getTime());
        this.tempCelcius = tempCelcius;
        this.locality = locality;
    }

    //Constructor for when user has not given permission for location
    public Fish(String title, Double weight, boolean inGrams,Double length, String picture) {
        //public Fish(String title, Double weight, Double length, String picture, String latitude, String longitude) {
        this.title = title;
        this.weight = weight;
        this.inGrams = inGrams;
        this.length = length;
        this.picture = picture;
        this.locality = null;
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        this.date = date.format(cal.getTime());
    }

    //temp constructor -> remove later
    public Fish(String title, Double weight, Double length, String latitude, String longitude) {
        this.title = title;
        this.weight = weight;
        this.length = length;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //temp constructor -> remove later
    /*public Fish() {
        this.title = "Särki";
        this.weight = 8.2;
        this.length = 4.5;
        this.picture = "https://fi.wikipedia.org/wiki/S%C3%A4rki#/media/Tiedosto:Rutilus_rutilus_Prague_Vltava_3.jpg";
        this.latitude = String.valueOf(61.011333);
        this.longitude = String.valueOf(25.614806);
        this.date2 = Calendar.getInstance().getTime();

        this.title = "Species";
        this.titleWeight = "Weigth";
        this.titleLength = "Lenght";
        this.picture = "Picture filename";
        this.latitude = "Latitude";
        this.longitude = "Longitude";
        this.titleDate = "Time";
        this.titleTempCelcius = "Temperature";
    }*/


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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //getFish method only for test purposes -> remove in final version
    /*public String getFish() {
        return
                "Fish [species=" + title
                        + ", weight=" + weight
                        + ", length=" + length
                        + ", picture=" + picture
                        + ",latitude=" + latitude
                        + ",longitude=" + longitude
                        + ",date=" + date + "]";
    }*/

}