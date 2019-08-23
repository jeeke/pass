package com.example.mytasker.models;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Task extends Message{
    long c_date;
    String job_des;
    String dis;
    String title;
    Integer cost;
    boolean remote;
    String address;
    String poster_name;
    String poster_avatar;
    String poster_id;
    String category;
    String attachments;
    String deadline;
    double lot, lon;
    String[] tags;
    int stage;
    ArrayList<String> must_haves;

    public String getPoster_name() {
        return poster_name;
    }
    public String getPoster_avatar() {
        return poster_avatar;
    }
    public String getPoster_id() {
        return poster_id;
    }
    public int getStage() {
        return stage;
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

    public static Map toMap(boolean remote, int cost, double lat, double lon, Long deadline, String category, String title, String jobDes, String address, JSONArray tags, JSONArray mustHaves) {
        Map map = new HashMap();
        map.put("job_des", jobDes);
        map.put("title", title);
        map.put("cost", cost);
        map.put("address", address);
        map.put("category", category);
        map.put("deadline", deadline);
        map.put("lat", lat);
        map.put("lon", lon);
        map.put("tags", tags);
        map.put("remote", remote);
        map.put("must_haves", mustHaves);
        return map;
    }
}