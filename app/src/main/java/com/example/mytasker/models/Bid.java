package com.example.mytasker.models;

public class Bid {
    private String id;
    private String name;
    private String contact;
    private String message;
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
