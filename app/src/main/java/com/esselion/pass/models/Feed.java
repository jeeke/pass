package com.esselion.pass.models;

import androidx.annotation.Keep;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Keep
public class Feed {


    public int likeCount = 0;
    private String id = "";
    private long c_date = new Date().getTime();
    private String poster_id = "";
    private String poster_name = "";
    private String poster_avatar = "";
    private String image = "";
    public Map<String, Boolean> likes = new HashMap<>();
    private String text = "";
    private Long sort_id = -new Date().getTime();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public long getC_date() {
        return c_date;
    }

    public void setC_date(long c_date) {
        this.c_date = c_date;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public String getPoster_name() {
        return poster_name;
    }

    public void setPoster_name(String poster_name) {
        this.poster_name = poster_name;
    }

    public String getPoster_avatar() {
        return poster_avatar;
    }

    public void setPoster_avatar(String poster_avatar) {
        this.poster_avatar = poster_avatar;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Feed(long c_date, String poster_id, String poster_name, String poster_avatar, String image, String text) {
        this.c_date = c_date;
        this.poster_id = poster_id;
        this.poster_name = poster_name;
        this.poster_avatar = poster_avatar;
        this.image = image;
        this.text = text;
        sort_id = -new Date().getTime();
    }

    public int getLikeCount() {
        return likeCount;
    }

    public Feed() {
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

}
