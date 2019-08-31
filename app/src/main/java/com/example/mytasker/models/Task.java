package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Task extends Message implements Serializable {
    @SerializedName("dis")
    String dis;
    @SerializedName("id")
    String id;
    @SerializedName("c_date")
    long c_date;
    @SerializedName("stage")
    int stage;
    @SerializedName("cost")
    float cost;
    @SerializedName("remote")
    boolean remote;
    @SerializedName("job_des")
    String job_des;
    @SerializedName("title")
    String title;
    @SerializedName("poster_name")
    String poster_name;
    @SerializedName("poster_avatar")
    String poster_avatar;
    @SerializedName("poster_id")
    String poster_id;
    @SerializedName("category")
    String category;
    @SerializedName("deadline")
    String deadline;
    @SerializedName("loc")
    ArrayList<Double> location;
    @SerializedName("tags")
    ArrayList<String> tags;
    @SerializedName("must_haves")
    ArrayList<String> musthaves;

    // For Post use constructor that shoud be send to server !!
    public Task(String poster_id, String poster_name, String poster_avatar, long c_date, String job_des, String title, float cost, String category, String deadline, ArrayList<Double> location, ArrayList<String> tags, ArrayList<String> musthaves, boolean remote) {
        this.c_date = c_date;
        this.poster_avatar = poster_avatar;
        this.poster_name = poster_name;
        this.job_des = job_des;
        this.title = title;
        this.cost = cost;
        this.poster_id = poster_id;
        this.category = category;
        this.deadline = deadline;
        this.location = location;
        this.tags = tags;
        this.remote = remote;
        this.musthaves = musthaves;
    }

//    public static Task fromMap(Map<String, String> map) {
//        Task task = new Task();
//        task.setStage(Integer.parseInt(map.get("stage")));
//        task.setC_date(Long.parseLong(map.get("c_date")));
//        task.setPoster_avatar(map.get("poster_avatar"));
//        task.setJob_des(map.get("job_des"));
//        task.setPoster_name(map.get("poster_name"));
//        return task;
//    }

    Map toMap() {
        Map map = new HashMap();
        map.put("id", id);
        map.put("stage", stage);
        map.put("c_date", c_date);
        map.put("poster_avatar", poster_avatar);
        map.put("poster_name", poster_name);
        map.put("job_des", job_des);
        map.put("title", title);
        map.put("cost", cost);
        map.put("poster_id", poster_id);
        map.put("category", category);
        map.put("deadline", deadline);
        map.put("loc", location);
        map.put("tags", tags);
        map.put("remote", remote);
        map.put("must_haves", musthaves);
        return map;
    }

    public String getId() {
        return id;
    }

    public Task() {
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    private void setC_date(long c_date) {
        this.c_date = c_date;
    }

    private void setJob_des(String job_des) {
        this.job_des = job_des;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Double> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Double> location) {
        this.location = location;
    }

    public ArrayList<String> getMusthaves() {
        return musthaves;
    }

    public void setMusthaves(ArrayList<String> musthaves) {
        this.musthaves = musthaves;
    }


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

    public float getCost() {
        return cost;
    }
    public String getTitle() {
        return title;
    }
    public String getJob_des() {
        return job_des;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
    public long getC_date() {
        return c_date;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}