package com.example.mytasker.models;

import java.util.HashMap;
import java.util.Map;

public class Bid {
    private String id;
    private long c_date;

    private boolean assigned;

    public static Map toMap(int price, long contact, Task task, String message) {
        Map map = new HashMap();
        map.put("task", task);
        map.put("price", price);
        map.put("contact", contact);
        map.put("des", message);
        return map;
    }

    public boolean isAssigned() {
        return assigned;
    }
    private String bidder_id;
    private String name;
    private String avatar;
    private long contact;
    private String des;
    private int price;

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public long getC_date() {
        return c_date;
    }

    public void setC_date(long c_date) {
        this.c_date = c_date;
    }

    public String getBidder_id() {
        return bidder_id;
    }

    public void setBidder_id(String bidder_id) {
        this.bidder_id = bidder_id;
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
