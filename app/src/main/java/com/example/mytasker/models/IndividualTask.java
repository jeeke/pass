package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class IndividualTask {
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
    @SerializedName("address")
    String address;
    @SerializedName("poster_id")
    String poster_id;
    @SerializedName("category")
    String category;
    @SerializedName("attachments")
    String attachment;
    @SerializedName("deadline")
    String deadline;
    @SerializedName("loc")
    Integer[] location;
    @SerializedName("tags")
    String[] tags;
    @SerializedName("must_haves")
    ArrayList<String> musthaves;


    // For query Thing that will be returned use getter !!
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

    public long getC_date() {
        return c_date;
    }

    // For Post use constructor that shoud be send to server !!
    public IndividualTask(long c_date, String job_des, String title, Integer cost, String address, String poster_id, String category, String attachment, String deadline, Integer[] location, String[] tags, ArrayList<String> musthaves) {
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
        this.musthaves = musthaves;
    }


}