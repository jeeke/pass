package com.example.mytasker.models;

public class Task {

    private String description;

    public Task(String description, String price, String location, String distance, String date, int image) {
        this.description = description;
        this.price = price;
        this.location = location;
        this.distance = distance;
        this.date = date;
        this.image = image;
    }

    private String price;
    private String location;

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getDistance() {
        return distance;
    }

    public String getDate() {
        return date;
    }

    public int getImage() {
        return image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImage(int image) {
        this.image = image;
    }

    private String distance;
    private String date;
    private int image;

}
