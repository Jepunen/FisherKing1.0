package com.example.vko11v3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//REMEMBER to insert temperature as a variable in fish constructor

public class Fish implements Serializable {

    String title;
    Double weight;
    Double length;
    String picture;
    String latitude;
    String longitude;
    Long date;
    Date date2;
    Double tempCelcius;
    String locality;

    //TEMP -> dont think these are needed -> unless we cant fix empty file crash issue otherwise
    String titleWeight;
    String titleLength;
    String titleDate;
    String titleTempCelcius;


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
    public Fish(String title, Double weight, Double length, String picture, String latitude, String longitude, Double tempCelcius, String locality) {
    //public Fish(String title, Double weight, Double length, String picture, String latitude, String longitude) {
        this.title = title;
        this.weight = weight;
        this.length = length;
        this.picture = picture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date2 = Calendar.getInstance().getTime();
        this.tempCelcius = tempCelcius;
        this.locality = locality;
    }

    //Constructor for when user has not given permission for location
    public Fish(String title, Double weight, Double length, String picture) {
        //public Fish(String title, Double weight, Double length, String picture, String latitude, String longitude) {
        this.title = title;
        this.weight = weight;
        this.length = length;
        this.picture = picture;
        this.date2 = Calendar.getInstance().getTime();
    }

    //temp rakentaja2
    public Fish(String title, Double weight, Double length, String latitude, String longitude) {
        this.title = title;
        this.weight = weight;
        this.length = length;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //temp rakentaja3
    public Fish() {
        /*this.title = "Särki";
        this.weight = 8.2;
        this.length = 4.5;
        this.picture = "https://fi.wikipedia.org/wiki/S%C3%A4rki#/media/Tiedosto:Rutilus_rutilus_Prague_Vltava_3.jpg";
        this.latitude = String.valueOf(61.011333);
        this.longitude = String.valueOf(25.614806);
        this.date2 = Calendar.getInstance().getTime();*/

        this.title = "Species";
        this.titleWeight = "Weigth";
        this.titleLength = "Lenght";
        this.picture = "Picture filename";
        this.latitude = "Latitude";
        this.longitude = "Longitude";
        this.titleDate = "Time";
        this.titleTempCelcius = "Temperature";
    }


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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    //getFish method only for test purposes -> remove in final version
    public String getFish() {
        return
                "Fish [species=" + title
                        + ", weight=" + weight
                        + ", length=" + length
                        + ", picture=" + picture
                        + ",latitude=" + latitude
                        + ",longitude=" + longitude
                        + ",date=" + date + "]";
    }

}
/*
    //location

    // Initialize fusedLocationProviderClient
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity()); //ei pelkkä this, koska ollaan fragmentissa

    //check permission
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            //mainActivity vai MainFragment -> doesnt seem to work
            //not context: vs. activity -> with either
            == PackageManager.PERMISSION_GRANTED) {
        //When permission granted
        getLocation();
    } else {
        //When permission denied
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }


    //get location:
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Snackbar.make(getView(), "Give permissions **temp**", 3);

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(getActivity(),
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                        //Set latitude on TextView
                        latitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLatitude())));
                        //Set longitude on TextView
                        longitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLongitude())));
                        //Set country name
                        countryName.setText(addresses.get(0).getCountryName());
                        //Set locality
                        locality.setText(addresses.get(0).getLocality());
                        //Set address
                        address.setText(addresses.get(0).getAddressLine(0));

                        //get weather:
                        URLWeather = WEATHER_URL + "?lat=" +addresses.get(0).getLatitude()+"&lon="+addresses.get(0).getLongitude()+"&appid="+APP_ID;
                        System.out.println("*** URLWeather *** :"+URLWeather);
                        readJSON(URLWeather);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

*/

