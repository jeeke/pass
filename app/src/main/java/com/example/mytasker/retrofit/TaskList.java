package com.example.mytasker.retrofit;

import com.example.mytasker.models.Task;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TaskList {
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @SerializedName("tasks")
    ArrayList<Task> tasks;

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
