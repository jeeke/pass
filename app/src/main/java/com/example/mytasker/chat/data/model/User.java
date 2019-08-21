package com.example.mytasker.chat.data.model;

import com.stfalcon.chatkit.commons.models.IUser;

/*
 * Created by troy379 on 04.04.17.
 */
public class User implements IUser {

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(Long online) {
        this.online = online+"";
    }

    private String id;
    private String name;
    private String avatar;
    private String online;

    public User(){

    }

    public User(String id, String name, String avatar, String online) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.online = online+"";

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

}
