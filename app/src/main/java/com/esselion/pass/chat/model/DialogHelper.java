package com.esselion.pass.chat.model;

import androidx.annotation.Keep;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Keep
public class DialogHelper {

    private String uid;
    private String name;

    private String avatar;

    private int unreadCount;

    private Long lastActivity;

    private String text;

    public DialogHelper(String uid, String name, String avatar, String text) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.text = text;
        init();
    }

    public DialogHelper() {
        init();
    }

    public Map toMap() {
        Map map = new HashMap();
        map.put("text", text);
        map.put("uid", uid);
        map.put("name", name);
        map.put("avatar", avatar);
        map.put("unreadCount", 1);
        map.put("lastActivity", ServerValue.TIMESTAMP);
        return map;
    }

    public Long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Long lastActivity) {
        this.lastActivity = lastActivity;
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

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private void init() {
        lastActivity = new Date().getTime();
    }

    public Dialog toDialog() {
        Dialog dialog = new Dialog(uid, avatar, name, unreadCount);
        ArrayList<User> users = new ArrayList<>();
        User user = new User(uid, name, "default");
        users.add(user);
        dialog.setUsers(users);

        Message message = new Message();
        message.setText(text);
        message.setUser(user);
        message.setCreatedAt(lastActivity);
        dialog.setLastMessage(message);
        return dialog;
    }

}

