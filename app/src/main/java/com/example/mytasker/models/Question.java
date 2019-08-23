package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

public class Question extends Message{
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
    @SerializedName("loc")
    private double[] location;
    @SerializedName("tags")
    private String[] tags;

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
    public String[] getTags(){
        return tags;
    }
    public long getC_date() {
        return c_date;
    }

    // For Post use constructor that shoud be send to server !!
    public Question(long c_date, String ques, String poster_id, String poster_name, String category, String attachment,double[] location, String[] tags) {
        this.c_date = c_date;
        this.poster_id = poster_id;
        this.poster_name = poster_name;
        this.category = category;
        this.attachment = attachment;
        this.location = location;
        this.tags = tags;
        this.ques = ques;
    }


    public String getPosterName() {
        return poster_name;
    }
}