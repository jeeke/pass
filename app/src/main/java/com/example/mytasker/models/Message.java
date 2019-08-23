package com.example.mytasker.models;

import java.util.HashMap;

public class Message {

    private String message;

    public Message(HashMap data) {
        this.message = data.get("message") + "";
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
