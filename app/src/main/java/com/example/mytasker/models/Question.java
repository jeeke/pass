package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Question extends Message implements Serializable {
    @SerializedName("poster_image")
    private String poster_image;
    @SerializedName("id")
    private String id;
    @SerializedName("loc")
    private ArrayList<Double> location;
    @SerializedName("tags")
    private ArrayList<String> tags;

    public Question() {
    }

    @SerializedName("c_date")
    private long c_date;

    @SerializedName("dis")
    private String dis;

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }
    @SerializedName("ques")
    private String ques;
    @SerializedName("poster_id")
    private String poster_id;
    @SerializedName("poster_name")
    private String poster_name;
    @SerializedName("category")
    private String category;
    @SerializedName("attachments")
    private String attachment;

    // For Post use constructor that shoud be send to server !!
    public Question(long c_date, String ques, String poster_id, String poster_name, String poster_image, String category, String attachment, ArrayList<Double> location, ArrayList<String> tags) {
        this.c_date = c_date;
        this.poster_id = poster_id;
        this.poster_name = poster_name;
        this.category = category;
        this.attachment = attachment;
        this.location = location;
        this.poster_image = poster_image;
        this.tags = tags;
        this.ques = ques;
    }

    public String getPoster_image() {
        return poster_image;
    }

    @SerializedName("accepted")
    private int done;

    public int getStage() {
        return done;
    }

    public String getQues() {
        return ques;
    }

    public String getCategory() {
        return category;
    }

    public void setPoster_image(String poster_image) {
        this.poster_image = poster_image;
    }
    public long getC_date() {
        return c_date;
    }

    public ArrayList<String> getTags() {
        return tags;
    }


    public String getPosterName() {
        return poster_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDone(int done) {
        this.done = done;
    }
}