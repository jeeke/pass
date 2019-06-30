package com.example.mytasker.retrofit;

import com.example.mytasker.models.Notification;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationList {
    public NotificationList(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    @SerializedName("notifications")
    ArrayList<Notification> notifications;

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }
}
