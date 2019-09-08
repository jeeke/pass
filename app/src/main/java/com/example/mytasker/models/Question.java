package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Question extends Message implements Serializable {
    @SerializedName("poster_image")
    private String poster_image;
    @SerializedName("id")
    private String id;
    @SerializedName("lon")
    private Double lon;
    @SerializedName("lat")
    private Double lat;

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
    @SerializedName("accepted")
    private int done;

    // For Post use constructor that shoud be send to server !!
    public Question(long c_date, String ques, String poster_id, String poster_name, String poster_image, Double lon, Double lat) {
        this.c_date = c_date;
        this.poster_id = poster_id;
        this.poster_name = poster_name;
        this.lon = lon;
        this.lat = lat;
        this.poster_image = poster_image;
        this.ques = ques;
    }

    public String getPoster_image() {
        return poster_image;
    }

    public String getPoster_id() {
        return poster_id;
    }


    public int getStage() {
        return done;
    }

    public String getQues() {
        return ques;
    }

    public void setPoster_image(String poster_image) {
        this.poster_image = poster_image;
    }
    public long getC_date() {
        return c_date;
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