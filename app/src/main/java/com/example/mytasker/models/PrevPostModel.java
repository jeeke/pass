package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PrevPostModel extends Message{

    @SerializedName("poster")
    ArrayList<Task> posted;
    @SerializedName("tasker")
    ArrayList<Task> done;

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
}
