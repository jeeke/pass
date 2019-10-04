package com.example.mytasker.retrofit;

import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.Cache;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitParser {
    @SerializedName("tasks")
    private ArrayList<String> taskStrings;

    public void toTaskList(TaskListAdapter adapter, int price0, int price1, String uid) {
        Cache.tasks = new ArrayList<>();
        adapter.update(Cache.tasks);
        if (taskStrings.size() == 0) return;
        Task task = null;
        Gson g = new Gson();
        int cost = 0;
        int i = 0;
//        if (false) {
            for (String s : taskStrings) {
                if (i % 2 == 0) {
                    task = g.fromJson(s, Task.class);
                    cost = (int) task.getCost();
                    if ((cost <= price1) && (cost >= price0) && !task.getPoster_id().equals(uid)) {
                        adapter.addItem(task);
                        Cache.tasks.add(task);
                    }
                }
                i++;
            }
//        } else {
//            for (String s : taskStrings) {
//                if (i % 2 == 0) {
//                    task = g.fromJson(s, Task.class);
//                    cost = (int) task.getCost();
//                } else if ((cost <= price1) && (cost >= price0)) {
//                    task.setDis(s);
//                    adapter.addItem(task);
//                    Cache.tasks.add(task);
//                }
//                i++;
//            }
//        }
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
