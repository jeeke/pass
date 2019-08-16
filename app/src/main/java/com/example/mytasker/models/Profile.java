package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Profile {

    @SerializedName("medals")
    ArrayList<String> medals;

    @SerializedName("statistics")
    ArrayList<String> stats;

    @SerializedName("skills")
    ArrayList<String> skills;

    @SerializedName("detail")
    ArrayList<String> detail;

    String tasker_rating;
    String poster_rating;
    String message;

    public ArrayList<String> getMedals() {
        return medals;
    }

    public void setMedals(ArrayList<String> medals) {
        this.medals = medals;
    }

    public ArrayList<String> getStats() {
        return stats;
    }

    public void setStats(ArrayList<String> stats) {
        this.stats = stats;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }

    public ArrayList<String> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<String> detail) {
        this.detail = detail;
    }

    public String getTasker_rating() {
        return tasker_rating;
    }

    public void setTasker_rating(String tasker_rating) {
        this.tasker_rating = tasker_rating;
    }

    public String getPoster_rating() {
        return poster_rating;
    }

    public void setPoster_rating(String poster_rating) {
        this.poster_rating = poster_rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
