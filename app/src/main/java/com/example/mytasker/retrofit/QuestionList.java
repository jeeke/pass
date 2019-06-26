package com.example.mytasker.retrofit;

import com.example.mytasker.models.Question;
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
