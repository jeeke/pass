package com.example.mytasker.retrofit;

import com.example.mytasker.models.IndividualTask;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TaskDetail {
    public TaskDetail(ArrayList<IndividualTask> tasks) {
        this.tasks = tasks;
    }

    @SerializedName("tasks")
    ArrayList<IndividualTask> tasks;

    public ArrayList<IndividualTask> getTasks() {
        return tasks;
    }
}
