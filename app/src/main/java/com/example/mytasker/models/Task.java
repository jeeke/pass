package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Task extends Message{
    @SerializedName("c_date")
    long c_date;
    @SerializedName("job_des")
    String job_des;
    @SerializedName("dis")
    String distance;
    @SerializedName("title")
    String title;
    @SerializedName("cost")
    Integer cost;
    @SerializedName("remote")
    boolean remote;
    @SerializedName("address")
    String address;

    public String getPoster_id() {
        return poster_id;
    }

    @SerializedName("poster_id")
    String poster_id;
    @SerializedName("category")
    String category;
    @SerializedName("attachments")
    String attachment;
    @SerializedName("deadline")
    String deadline;
    @SerializedName("loc")
    double[] location;
    @SerializedName("tags")
    String[] tags;

    @SerializedName("stage")
    int stage;

    @SerializedName("must_haves")
    ArrayList<String> musthaves;




    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
    // For query Thing that will be returned use getter !!
    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }
    public Integer getCost() {
        return cost;
    }
    public String getTitle() {
        return title;
    }
    public String getDistance() {
        return distance;
    }
    public String getJob_des() {
        return job_des;
    }
    public String getAddress() { return address; }

    public String getCategory() {
        return category;
    }
    public String[] getTags(){
        return tags;
    }
    public long getC_date() {
        return c_date;
    }

    // For Post use constructor that shoud be send to server !!
    public Task(long c_date, String job_des, String title, Integer cost, String address, String poster_id, String category, String attachment, String deadline, double[] location, String[] tags, ArrayList<String> musthaves,boolean remote) {
        this.c_date = c_date;
        this.job_des = job_des;
        this.title = title;
        this.cost = cost;
        this.address = address;
        this.poster_id = poster_id;
        this.category = category;
        this.attachment = attachment;
        this.deadline = deadline;
        this.location = location;
        this.tags = tags;
        this.remote = remote;
        this.musthaves = musthaves;
    }


}