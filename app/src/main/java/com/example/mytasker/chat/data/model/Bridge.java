package com.example.mytasker.chat.data.model;

import java.util.ArrayList;

public class Bridge {
    public Bridge() {
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online.equals("true");
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    private String id;
    private String name;
    private String avatar;
    private boolean online;
    private Message lastMessage;
    private int unreadCount;

    public Bridge(String id, String name, String avatar, String online, Message lastMessage, int unreadCount) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.online = online.equals("true");
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public Dialog getDialog(){
        ArrayList<User> user = new ArrayList<>();
        user.add(new User(id,name,avatar,online+""));
        return new Dialog(id,name,avatar,user,lastMessage,unreadCount);
    }
}
