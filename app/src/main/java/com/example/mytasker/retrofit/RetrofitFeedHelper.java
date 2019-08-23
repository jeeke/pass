package com.example.mytasker.retrofit;

import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitFeedHelper {
    @SerializedName("tasks")
    ArrayList<String[]> taskStrings;

    public ArrayList<Task> toTaskList() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (String[] s : taskStrings) {
            Gson g = new Gson();
            Task task = g.fromJson(s[0], Task.class);
            task.setDis(s[1]);
            tasks.add(task);
        }
        return tasks;
    }

    public ArrayList<Question> toQuesList() {
        ArrayList<Question> tasks = new ArrayList<>();
        for (String[] s : taskStrings) {
            Gson g = new Gson();
            Question task = g.fromJson(s[0], Question.class);
            task.setDis(s[1]);
            tasks.add(task);
        }
        return tasks;
    }
}
