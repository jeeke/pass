package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
