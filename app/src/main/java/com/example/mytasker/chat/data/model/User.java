package com.example.mytasker.chat.data.model;

import com.google.firebase.auth.FirebaseUser;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.HashMap;
import java.util.Map;

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

    private String id;
    private String name;
    private String avatar;

    public User(){

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public Map toMap(){
        Map map = new HashMap();
        map.put("id",id);
        map.put("name",name);
        map.put("avatar",avatar);
        return map;
    }

    public static User fromFireBaseUser(FirebaseUser user){
        return new User(user.getUid(),user.getDisplayName(),user.getPhotoUrl().toString());
    }

}
