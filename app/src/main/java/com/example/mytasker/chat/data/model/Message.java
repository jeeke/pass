package com.example.mytasker.chat.data.model;

import com.google.firebase.database.ServerValue;
import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message implements IMessage{

    public static final String MESSAGE_SEEN = "Seen";
    public static final String MESSAGE_SENT = "Sent";
    public static final String MESSAGE_NOT_SENT = "Not Sent";

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String text;
    private Date createdAt;
    private User user;
    private String status;



    public Map toMap(){
        Map map = new HashMap();
        map.put("text",text);
        map.put("user",user.toMap());
        map.put("createdAt", ServerValue.TIMESTAMP);
        map.put("status",MESSAGE_SENT);
        return map;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Message(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Message(User user, String text) {
        this.text = text;
        this.user = user;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
