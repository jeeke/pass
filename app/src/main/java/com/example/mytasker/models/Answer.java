package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

public class Answer extends Message{
    @SerializedName("replier_name")
    private String replier_name;
    @SerializedName("replier_id")
    private String replier_id;
    @SerializedName("answer")
    private String answer;
    @SerializedName("replier_avatar")
    private String replier_avatar;
    @SerializedName("asker_id")
    private String asker_id;

    public Answer(String replier_id, String replier_name, String replier_avatar, String asker_id, String answer) {
        this.replier_name = replier_name;
        this.replier_id = replier_id;
        this.answer = answer;
        this.replier_avatar = replier_avatar;
        this.asker_id = asker_id;
    }

    public Answer() {
    }

    public String getReplier_name() {
        return replier_name;
    }

    public void setReplier_name(String replier_name) {
        this.replier_name = replier_name;
    }

    public String getReplier_id() {
        return replier_id;
    }

    public void setReplier_id(String replier_id) {
        this.replier_id = replier_id;
    }

    public String getReplier_avatar() {
        return replier_avatar;
    }

    public void setReplier_avatar(String replier_avatar) {
        this.replier_avatar = replier_avatar;
    }

    public String getAsker_id() {
        return asker_id;
    }

    public void setAsker_id(String asker_id) {
        this.asker_id = asker_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
