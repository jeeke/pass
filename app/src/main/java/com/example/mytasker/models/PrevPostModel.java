package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PrevPostModel {

    @SerializedName("posted")
    ArrayList<Task> posted;
    @SerializedName("done")
    ArrayList<Task> done;
    @SerializedName("saved")
    ArrayList<Task> saved;

    public ArrayList<Task> getPosted() {
        return posted;
    }

    public void setPosted(ArrayList<Task> posted) {
        this.posted = posted;
    }

    public ArrayList<Task> getDone() {
        return done;
    }

    public void setDone(ArrayList<Task> done) {
        this.done = done;
    }

    public ArrayList<Task> getSaved() {
        return saved;
    }

    public void setSaved(ArrayList<Task> saved) {
        this.saved = saved;
    }
}
