package com.example.vko11v3;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Fish implements Serializable {

    String title;
    Double weight;
    Double length;
    String picture;
    String latitude;
    String longitude;
    Long date;
    Date date2;


    public Fish(String title, Double weight, Double length, String picture, String latitude, String longitude, Long date) {
        this.title = title;
        this.weight = weight;
        this.length = length;
        this.picture = picture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
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
        this.title = "Särki";
        this.weight = 8.2;
        this.length = 4.5;
        this.picture = "https://fi.wikipedia.org/wiki/S%C3%A4rki#/media/Tiedosto:Rutilus_rutilus_Prague_Vltava_3.jpg";
        this.latitude = String.valueOf(61.011333);
        this.longitude = String.valueOf(25.614806);
        this.date2 = Calendar.getInstance().getTime();;
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

    public String getFish() {
        return
                "Fish [species=" +title
                        +", weight="+weight
                        +", length="+length
                        +", picture="+picture
                        +",latitude="+latitude
                        +",longitude="+longitude
                        +",date="+date +"]";
    }

}
//serialisoi arraylist puhelimen muistiin
//serialisointi muuttaa biteiksi
