package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

public class Bid {
    @SerializedName("bidder_id")
    private String id;
    @SerializedName("bidder_name")
    private String name;
    @SerializedName("contact")
    private String contact;
    @SerializedName("message")
    private String message;
    @SerializedName("bid_price")
    private int bidPrice;
    public Bid(String id,String name,String message,String contact,int bidPrice) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.contact = contact;
        this.bidPrice = bidPrice;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(int bidPrice) {
        this.bidPrice = bidPrice;
    }
}
