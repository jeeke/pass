package com.esselion.pass.retrofit;

import com.esselion.pass.models.Question;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QuestionList {
    public QuestionList(ArrayList<Question> questions) {
        this.questions = questions;
    }

    @SerializedName("questions")
    ArrayList<Question> questions;

    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
