package com.example.mytasker.models;

public class Feed {
    private long c_date;
    private String poster_id;
    private String poster_name;
    private String poster_avatar;
    private String image;

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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    private String text;
    private int likes;

    public Feed(){}

    public Feed(long c_date, String poster_id, String poster_name, String poster_avatar, String image, String text, int likes) {
        this.c_date = c_date;
        this.poster_id = poster_id;
        this.poster_name = poster_name;
        this.poster_avatar = poster_avatar;
        this.image = image;
        this.text = text;
        this.likes = likes;
    }

}
