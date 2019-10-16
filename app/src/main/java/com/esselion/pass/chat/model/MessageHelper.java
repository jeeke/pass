package com.esselion.pass.chat.model;

import androidx.annotation.Keep;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Keep
public class MessageHelper {
    public static final String MESSAGE_SEEN = "Seen";
    public static final String MESSAGE_SENT = "Sent";
    public static final String MESSAGE_NOT_SENT = "Not Sent";
    private static int idH = 0;
    private String createdAt;
    private String status;
    private String text;
    private String uid;
    private String name;
    private String avatar;
    private String id;

    public MessageHelper(FirebaseUser user, String text) {
        init();
        this.text = text;
        name = user.getDisplayName();
        avatar = user.getPhotoUrl().toString();
        uid = user.getUid();
    }

    public MessageHelper() {
        init();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private void init() {
        id = (idH + 1) + "";
        idH++;
        createdAt = new Date().getTime() + "";
    }

    public Map toMap() {
        Map map = new HashMap();
        map.put("text", text);
        map.put("uid", uid);
        map.put("name", name);
        map.put("avatar", avatar);
        map.put("createdAt", new Date().getTime() + "");
        map.put("status", MESSAGE_SENT);
        return map;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Message toMessage() {
        Message message = new Message(id, text, status, new Date(Long.parseLong(createdAt)));
        User user = new User(uid, name, avatar);
        message.setUser(user);
        return message;
    }
}
