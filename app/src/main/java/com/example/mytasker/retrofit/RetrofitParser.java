package com.example.mytasker.retrofit;

import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitParser {
    @SerializedName("tasks")
    private ArrayList<String> taskStrings;

    public ArrayList<Task> toTaskList() {
        if (taskStrings.size() == 0) return new ArrayList<>();
        ArrayList<Task> tasks = new ArrayList<>();
        Task task = null;
        int i = 0;
        for (String s : taskStrings) {
            if (i % 2 == 0) {
                Gson g = new Gson();
                task = g.fromJson(s, Task.class);
            } else {
                task.setDis(s);
                tasks.add(task);
            }
            i++;
        }
        return tasks;
    }

    public ArrayList<Question> toQuesList() {
        if (taskStrings.size() == 0) return new ArrayList<>();
        ArrayList<Question> tasks = new ArrayList<>();
        Question task = null;
        int i = 0;
        for (String s : taskStrings) {
            if (i % 2 == 0) {
                Gson g = new Gson();
                task = g.fromJson(s, Question.class);
            } else {
                task.setDis(s);
                tasks.add(task);
            }
            i++;
        }
        return tasks;
    }

    public void setTaskStrings(ArrayList<String> taskStrings) {
        this.taskStrings = taskStrings;
    }
}
