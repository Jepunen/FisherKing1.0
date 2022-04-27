package com.example.vko11v3;

import java.io.Serializable;

public class Weather implements Serializable {

    String currentTemperature;
    String feelsLikeTemperature;
    String morningTemperature;
    String eveningTemperature;
    String nightTemperature;
    String locality;
    String onScreenDate;
    String onScreenDayOfWeek;
    String weatherTypeText;
    String weatherIcon;
    String windSpeedText;
    String sunriseText;
    String sunsetText;

    public Weather(String currentTemperature, String feelsLikeTemperature,
                   String morningTemperature, String eveningTemperature,
                   String nightTemperature, String locality, String onScreenDate,
                   String onScreenDayOfWeek, String weatherTypeText, String icon,String windSpeedText,
                   String sunriseText, String sunsetText) {
        this.currentTemperature = currentTemperature + "°";
        this.feelsLikeTemperature = "Feels like " + feelsLikeTemperature + "°c";
        this.morningTemperature = "Morning " + morningTemperature + "°c";
        this.eveningTemperature = "Evening " + eveningTemperature + "°c";
        this.nightTemperature = "Night " + nightTemperature + "°c";
        this.locality = locality;
        this.onScreenDate = onScreenDate;
        this.onScreenDayOfWeek = onScreenDayOfWeek;
        this.weatherTypeText = capitalize(weatherTypeText);
        this.weatherIcon = icon;
        this.windSpeedText = windSpeedText + " m/s";
        this.sunriseText = sunriseText;
        this.sunsetText = sunsetText;
    }
    public String getCurrentTemperature() {
        return currentTemperature;
    }
    public String getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }
    public String getMorningTemperature() {
        return morningTemperature;
    }
    public String getEveningTemperature() {
        return eveningTemperature;
    }
    public String getNightTemperature() {
        return nightTemperature;
    }
    public String getLocality() {
        return locality;
    }
    public String getOnScreenDate() {
        return onScreenDate;
    }
    public String getOnScreenDayOfWeek() {
        return onScreenDayOfWeek;
    }
    public String getWeatherTypeText() {
        return weatherTypeText;
    }
    public String getWeatherIcon() {
        return weatherIcon;
    }
    public String getWindSpeedText() {
        return windSpeedText;
    }
    public String getSunriseText() {
        return sunriseText;
    }
    public String getSunsetText() {
        return sunsetText;
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
