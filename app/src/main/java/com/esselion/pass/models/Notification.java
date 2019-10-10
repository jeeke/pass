package com.esselion.pass.models;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;

import androidx.core.app.TaskStackBuilder;

import com.esselion.pass.activities.BidsListActivity;
import com.esselion.pass.activities.DashboardActivity;
import com.esselion.pass.activities.FeedbackByPosterActivity;
import com.esselion.pass.activities.HistoryTask;
import com.esselion.pass.activities.QuestionDetailActivity;
import com.esselion.pass.activities.TaskDetailActivity;
import com.esselion.pass.chat.DialogsActivity;
import com.esselion.pass.chat.MessagesActivity;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notification {

    public String content;
    public String title;
    public String image;
    private int type;
    private Task task;
    private Question ques;
    private String senderId;
    private String taskId;
    private String taskerId;
    private String taskTitle;
    private Long sort_id;

    Notification() {
    }

    public Notification(Map<String, String> data) {
        String type = data.get("type");
        this.type = Integer.parseInt(type == null ? "-1" : type);
        title = data.get("title");
        content = data.get("content");
        image = data.get("image");
        senderId = data.get("sender_id");
        taskerId = data.get("tasker_id");
        taskTitle = data.get("task_title");
        taskId = data.get("task_id");
        sort_id = Long.valueOf(data.get("sort_id"));


        String task = data.get("task");
        if (task != null)
            this.task = new Gson().fromJson(task, Task.class);

        task = data.get("ques");
        if (task != null)
            ques = new Gson().fromJson(task, Question.class);
    }

    public int getType() {
        return this.type;
    }

    public Map toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("title", title);
        map.put("image", image);
        map.put("sort_id", -new Date().getTime());
        return map;
    }

    public Spanned fetchTitle() {
        return Html.fromHtml(this.title);
    }

    public PendingIntent getPendingIntent(Context context) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent(context, DashboardActivity.class);
        stackBuilder.addNextIntent(intent);
        switch (type) {
            case 0: {
                stackBuilder.addNextIntent(new Intent(context, HistoryTask.class));
                Intent intent1 = new Intent(context, TaskDetailActivity.class);
                intent1.putExtra("task", task);
                stackBuilder.addNextIntent(intent1);
                Intent intent2 = new Intent(context, BidsListActivity.class);
                intent2.putExtra("task", task);
                stackBuilder.addNextIntent(intent2);
                break;
            }
            case 1: {
                stackBuilder.addNextIntent(new Intent(context, HistoryTask.class));
                Intent intent1 = new Intent(context, TaskDetailActivity.class);
                intent1.putExtra("task", task);
                intent1.putExtra("from", 2);
                stackBuilder.addNextIntent(intent1);
                break;
            }
            case 2: {
                Intent intent1 = new Intent(context, FeedbackByPosterActivity.class);
                intent1.putExtra("tasker_id", taskerId);
                intent1.putExtra("task_title", taskTitle);
                intent1.putExtra("task_id", taskId);
                stackBuilder.addNextIntent(intent1);
                break;
            }
            case 3: {
                Intent intent1 = new Intent(context, QuestionDetailActivity.class);
                intent1.putExtra("ques", ques);
                stackBuilder.addNextIntent(intent1);
                break;

            }
            case 4: {
                Intent intent1 = new Intent(context, DialogsActivity.class);
                stackBuilder.addNextIntent(intent1);
                Intent intent2 = new Intent(context, MessagesActivity.class);
                intent2.putExtra("id", senderId);
                intent2.putExtra("name", fetchTitle().toString());
                intent2.putExtra("avatar", image);
                stackBuilder.addNextIntent(intent2);
                break;

            }
            default:
        }
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

//    public Intent getForegroundIntent(Context context) {
//        Intent intent = null;
//        switch (type) {
//            case 0: {
//                intent = new Intent(context, BidsListActivity.class);
//                intent.putExtra("task", task);
//                break;
//            }
//            case 1: {
//                intent = new Intent(context, TaskDetailActivity.class);
//                intent.putExtra("task", task);
//                intent.putExtra("from", 2);
//                break;
//            }
//            case 2: {
//                intent = new Intent(context, FeedbackByPosterActivity.class);
//                intent.putExtra("tasker_id", taskerId);
//                intent.putExtra("task_title", taskTitle);
//                intent.putExtra("task_id", taskId);
//                break;
//            }
//            case 3: {
//                intent = new Intent(context, QuestionDetailActivity.class);
//                intent.putExtra("ques", ques);
//                break;
//
//            }
//            case 4: {
//                intent = new Intent(context, MessagesActivity.class);
//                intent.putExtra("id", senderId);
//                intent.putExtra("name", title);
//                intent.putExtra("avatar", image);
//                break;
//
//            }
//            default:
//        }
//        return intent;
//    }


}
