package com.esselion.pass.models;

public class ModelPost extends Message {

    private String title;
    private int person;



    public ModelPost(String title, int person) {
        this.title = title;
        this.person = person;

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }
}
