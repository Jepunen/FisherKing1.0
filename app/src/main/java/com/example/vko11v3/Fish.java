package com.example.vko11v3;

public class Fish {

    String species;
    Double weight;
    Double length;
    String picture;
    String latitude;
    String longitude;
    Long date;


    public Fish(String species, Double weight, Double length, String picture, String latitude, String longitude, Long date) {
        this.species = species;
        this.weight = weight;
        this.length = length;
        this.picture = picture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }


    public String getSpecies() {
        return species;
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

    public void setSpecies(String species) {
        this.species = species;
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

}
