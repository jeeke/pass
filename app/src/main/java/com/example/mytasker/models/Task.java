package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Task extends Message{


    String dis;
    @SerializedName("attachment")
    String attachment;

    public Task() {
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public void setC_date(long c_date) {
        this.c_date = c_date;
    }

    public void setJob_des(String job_des) {
        this.job_des = job_des;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public ArrayList<String> getMusthaves() {
        return musthaves;
    }

    @SerializedName("c_date")
    long c_date;
    @SerializedName("job_des")
    String job_des;
    @SerializedName("title")
    String title;
    @SerializedName("cost")
    Integer cost;
    @SerializedName("remote")
    boolean remote;
    @SerializedName("address")
    String address;
    @SerializedName("poster_name")
    String poster_name;
    @SerializedName("poster_avatar")
    String poster_avatar;
    @SerializedName("poster_id")
    String poster_id;
    @SerializedName("category")
    String category;

    public void setMusthaves(ArrayList<String> musthaves) {
        this.musthaves = musthaves;
    }
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

    public String getPoster_name() {
        return poster_name;
    }

    public void setPoster_name(String poster_name) {
        this.poster_name = poster_name;
    }

    public String getPoster_avatar() {
        return poster_avatar;
    }

    public void setPoster_avatar(String poster_avatar) {
        this.poster_avatar = poster_avatar;
    }

    public String getPoster_id() {
        return poster_id;
    }
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
    public Task(String poster_id,String poster_name,String poster_avatar,long c_date, String job_des, String title, Integer cost, String address,  String category, String attachment, String deadline, double[] location, String[] tags, ArrayList<String> musthaves,boolean remote) {
        this.c_date = c_date;
        this.poster_avatar = poster_avatar;
        this.poster_name = poster_name;
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