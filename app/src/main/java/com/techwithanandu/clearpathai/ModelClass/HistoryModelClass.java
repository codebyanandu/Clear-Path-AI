package com.nextgen.clearpathai.ModelClass;

public class HistoryModelClass {

    String id;
    String image;
    String description;
    String date;
    String latitude;
    String longitude;
    String location;
    public HistoryModelClass(String id, String image, String description,String date, String latitude, String longitude, String location) {
        this.id = id;
        this.image = image;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLocation() {
        return location;
    }
}
