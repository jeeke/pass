package com.example.mytasker.models;

import java.util.HashMap;
import java.util.Map;

public class Bid {
    private String id;
    private long createdAt;
    private String bidderId;
    private String name;
    private long contact;
    private String message;
    private int price;

    public static Map toMap(int price, long contact, String message) {
        Map map = new HashMap();
        map.put("price", price);
        map.put("contact", contact);
        map.put("message", message);
        return map;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
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
}
