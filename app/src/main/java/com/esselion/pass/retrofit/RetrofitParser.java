package com.esselion.pass.retrofit;

import com.esselion.pass.adapters.TaskListAdapter;
import com.esselion.pass.models.Question;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Cache;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetrofitParser {
    @SerializedName("tasks")
    private ArrayList<String> taskStrings;

    public void toTaskList(TaskListAdapter adapter, int price0, int price1, String uid) {
        Cache.tasks = new ArrayList<>();
        //for emptying previous listings
        adapter.update(Cache.tasks);
        if (taskStrings.size() == 0) return;
        Gson g = new Gson();
        for (int i = 0; i < taskStrings.size(); i += 2) {
            String s = taskStrings.get(i);
            Task task = g.fromJson(s, Task.class);
            int cost = (int) task.getCost();
            if ((cost <= price1) && (cost >= price0) && !task.getPoster_id().equals(uid)) {
                String d = task.parseDistance(taskStrings.get(i + 1));
                task.setDis(d);
                Cache.tasks.add(task);
                adapter.addItem(task);
            }
        }
    }

    public ArrayList<Question> toQuesList() {
        if (taskStrings.size() == 0) return new ArrayList<>();
        ArrayList<Question> ques = new ArrayList<>();
        Gson g = new Gson();
        for (int i = 0; i < taskStrings.size(); i += 2) {
            Question q = g.fromJson(taskStrings.get(i), Question.class);
            ques.add(q);
        }
        return ques;
    }

    public void setTaskStrings(ArrayList<String> taskStrings) {
        this.taskStrings = taskStrings;
    }
}
