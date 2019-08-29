package com.example.mytasker.models;

import java.util.ArrayList;
import java.util.Map;

public class Profile extends Message{

    Map rating;
    private String on_time, on_budget, quality, behaviour, t_done;
    private int bucks;
    private long t_posted;
    private ArrayList<String> skills, details;

    Profile() {
        on_budget = on_time = quality = behaviour = t_done = "0";
        bucks = 0;
        t_posted = 0;
        skills = new ArrayList<>();
        details = new ArrayList<>();

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
