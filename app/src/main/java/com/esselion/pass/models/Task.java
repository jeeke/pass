package com.esselion.pass.models;

import android.location.Location;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Keep
public class Task extends Message implements Serializable {
    @SerializedName("dis")
    String dis;
    @SerializedName("sort_id")
    private Long sort_id;
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
    @SerializedName("tasker_name")
    String tasker_name;
    @SerializedName("tasker_avatar")
    String tasker_avatar;

    @SerializedName("tasker_id")
    String tasker_id;
    @SerializedName("category")
    String category;
    @SerializedName("deadline")
    String deadline;
    @SerializedName("lon")
    Double lon;
    @SerializedName("lat")
    Double lat;
    @SerializedName("tags")
    ArrayList<String> tags;
    @SerializedName("must_haves")
    ArrayList<String> musthaves;

    // For Post use constructor that shoud be send to server !!
    public Task(String poster_id, String poster_name, String poster_avatar, long c_date, String job_des, String title, float cost, String category, String deadline, Double lat, Double lon, ArrayList<String> tags, ArrayList<String> musthaves, boolean remote) {
        this.c_date = c_date;
        this.poster_avatar = poster_avatar;
        this.poster_name = poster_name;
        this.job_des = job_des;
        this.title = title;
        this.cost = cost;
        this.poster_id = poster_id;
        this.category = category;
        this.deadline = deadline;
        this.lon = lon;
        this.lat = lat;
        this.tags = tags;
        this.remote = remote;
        this.musthaves = musthaves;
        sort_id = -new Date().getTime();
    }


    public String getTasker_name() {
        return tasker_name;
    }

    public void setTasker_name(String tasker_name) {
        this.tasker_name = tasker_name;
    }

    public String getTasker_avatar() {
        return tasker_avatar;
    }

    public void setTasker_avatar(String tasker_avatar) {
        this.tasker_avatar = tasker_avatar;
    }

    public String getTasker_id() {
        return tasker_id;
    }

    public void setTasker_id(String tasker_id) {
        this.tasker_id = tasker_id;
    }



    Map toMap() {
        Map<String, Object> map = new HashMap<>();
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
        map.put("lon", lon);
        map.put("lat", lat);
        map.put("tags", tags);
        map.put("remote", remote);
        map.put("must_haves", musthaves);
        map.put("sort_id", -new Date().getTime());
        return map;
    }

    public String getId() {
        return id;
    }

    public Task() {
        musthaves = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public String parseDistance(String dis) {
        DecimalFormat value = new DecimalFormat("#.#");
        float d = Float.parseFloat(dis);
        if (d < 1.0) {
            d = d * 1000;
            dis = value.format(d) + 'm';
        } else {
            dis = value.format(d) + "km";
        }
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

    public String getDistance(com.esselion.pass.models.Location l) {
        if (dis != null) return dis;
        Location locationB = new Location("point B");
        locationB.setLatitude(l.getLatitude());
        locationB.setLongitude(l.getLongitude());
        Location locationA = new Location("point A");
        locationA.setLatitude(lat);
        locationA.setLongitude(lon);
        return parseDistance((locationB.distanceTo(locationA) / 1000.0) + "");
    }
}