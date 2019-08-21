package com.example.mytasker.chat.data.model;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;

public class Message implements IMessage{

    public Message(String text, User user) {
        this.text = text;
        this.createdAt = new Date();
        this.user = user;
    }

    public Message(String id, String text, String status,Date createdAt) {
        this.text = text;
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Message(){}

    private String id;
    private String text;

    private Date createdAt;
    private User user;
    private String status;

    public void setCreatedAt(Long createdAt) {
        this.createdAt = new Date(createdAt);
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setUser(User user) {
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
}
