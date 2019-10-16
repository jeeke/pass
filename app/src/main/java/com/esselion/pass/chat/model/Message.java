package com.esselion.pass.chat.model;

import androidx.annotation.Keep;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;

@Keep
public class Message implements IMessage {

    private String id;
    private String text;
    private Date createdAt;
    private User user;
    private String status;

    public Message(String text, User user) {
        this.text = text;
        this.createdAt = new Date();
        this.user = user;
    }

    public Message(String id, String text, String status, Date createdAt) {
        this.text = text;
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Message() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = new Date(createdAt);
    }

    @Override
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
