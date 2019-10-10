package com.esselion.pass.models;

import java.util.HashMap;

public class Message {


    public Message() {
    }

    private String message;

    public Message(HashMap map) {
        this.message = (String) map.get("message");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
