package com.esselion.pass.retrofit;

import com.esselion.pass.models.Task;
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
