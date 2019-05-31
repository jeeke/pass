package com.example.mytasker.retrofit;

import com.google.gson.annotations.SerializedName;

public class TaskDetail {
    public TaskDetail(IndividualTask[] tasks) {
        this.tasks = tasks;
    }

    @SerializedName("tasks")
    IndividualTask[] tasks;

    public IndividualTask[] getTasks() {
        return tasks;
    }
}
