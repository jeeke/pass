package com.esselion.pass.models;

public class Location {
    public Double lat;
    public Double lon;
    public String tag = "My Location";
    public String desc = "No Description";
    public String id = "";

    public Double getLatitude() {
        return lat;
    }

    public Double getLongitude() {
        return lon;
    }
}
