package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Profile extends Message{

    @SerializedName("medals")
    ArrayList<String> medals;

    String on_time,on_budget,quality,behaviour,t_posted,t_done,bucks;

    @SerializedName("skills")
    ArrayList<String> skills;

    @SerializedName("details")
    ArrayList<String> detail;

    @SerializedName("t_rating")
    String tasker_rating;

    @SerializedName("p_rating")
    String poster_rating;

    public ArrayList<String> getMedals() {
        return medals;
    }

    public void setMedals(ArrayList<String> medals) {
        this.medals = medals;
    }

    public String getOn_time() {
        return on_time;
    }

    public void setOn_time(String on_time) {
        this.on_time = on_time;
    }

    public String getOn_budget() {
        return on_budget;
    }

    public void setOn_budget(String on_budget) {
        this.on_budget = on_budget;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public String getT_posted() {
        return t_posted;
    }

    public void setT_posted(String t_posted) {
        this.t_posted = t_posted;
    }

    public String getT_done() {
        return t_done;
    }

    public void setT_done(String t_done) {
        this.t_done = t_done;
    }

    public String getBucks() {
        return bucks;
    }

    public void setBucks(String bucks) {
        this.bucks = bucks;
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

}
