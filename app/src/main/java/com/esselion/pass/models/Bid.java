package com.esselion.pass.models;

import androidx.annotation.Keep;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Keep
public class Bid {
    //    TODO id is bidder id
    private String id;
    private String name;
    private String avatar;
    private String c_date;
    private String des;
    private String price;
    private String assigned;
    private Long sort_id;

    public static Map toMap(int price, Task task, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("task", task.toMap());
        map.put("price", price);
        map.put("des", message);
        map.put("sort_id", -new Date().getTime());
        return map;
    }

//    public static Bid fromMap(Map<String, String> map) {
//        Bid bid = new Bid();
//        bid.setId(map.get("id"));
//        bid.setName(map.get("name"));
//        bid.setAvatar(map.get("avatar"));
//        bid.setPrice(map.get("price"));
//        bid.setDes(map.get("des"));
//        bid.setC_date(map.get("c_date"));
//        bid.setAssigned(map.get("assigned"));
//        return bid;
//    }

    public String isAssigned() {
        return assigned;
    }


    public void setAssigned(boolean assigned) {
        this.assigned = assigned + "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getC_date() {
        return c_date;
    }

    public void setC_date(String c_date) {
        this.c_date = c_date;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
