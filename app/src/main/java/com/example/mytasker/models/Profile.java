package com.example.mytasker.models;

import java.util.ArrayList;

public class Profile extends Message{

    private ArrayList<String> medals, skills, details;
    private String on_time, on_budget, quality, behaviour, t_done;
    private int bucks;
    private long t_posted;
    private float t_rating, p_rating;

    Profile() {
        t_rating = 0;
        p_rating = 0;
        on_budget = on_time = quality = behaviour = t_done = "0";
        bucks = 0;
        t_posted = 0;
        medals = new ArrayList<>();
        skills = new ArrayList<>();
        details = new ArrayList<>();

    }

    public float getTasker_rating() {
        return t_rating;
    }

    public void setTasker_rating(float tasker_rating) {
        this.t_rating = tasker_rating;
    }

    public float getPoster_rating() {
        return p_rating;
    }

    public void setPoster_rating(float poster_rating) {
        this.p_rating = poster_rating;
    }


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

    public long getT_posted() {
        return t_posted;
    }

    public void setT_posted(long t_posted) {
        this.t_posted = t_posted;
    }

    public String getT_done() {
        return t_done;
    }

    public void setT_done(String t_done) {
        this.t_done = t_done;
    }

    public int getBucks() {
        return bucks;
    }

    public void setBucks(int bucks) {
        this.bucks = bucks;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }

    public ArrayList<String> getDetail() {
        return details;
    }

    public void setDetail(ArrayList<String> details) {
        this.details = details;
    }

}
