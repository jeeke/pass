package com.esselion.pass.chat.model;

import androidx.annotation.Keep;

import com.stfalcon.chatkit.commons.models.IUser;

import java.util.HashMap;
import java.util.Map;

@Keep
public class User implements IUser {

    private String id;
    private String name;
    private String avatar;

    public User() {
    }

    public User(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;

    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Map toMap() {
        Map map = new HashMap();
        map.put("id", id);
        map.put("name", name);
        map.put("avatar", avatar);
        return map;
    }

}
