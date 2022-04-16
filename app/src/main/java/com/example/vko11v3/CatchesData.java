package com.example.vko11v3;

public class CatchesData {

    String title;
    String details;
    String timePlace;

    public CatchesData(String title, String details, String dateTime) {
        this.title = title;
        this.details = details;
        this.timePlace = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public String getTimePlace() {
        return timePlace;
    }
}
