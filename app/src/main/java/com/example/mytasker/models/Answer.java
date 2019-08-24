package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

public class Answer extends Message{
    @SerializedName("name")
    private String name;

    public Answer(String name, String poster_id, String answer) {
        this.name = name;
        this.poster_id = poster_id;
        this.answer = answer;
    }

    public Answer() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @SerializedName("poster_id")
    private String poster_id;
    @SerializedName("answer")
    private String answer;
}
