package com.esselion.pass.util;

import android.content.SharedPreferences;

import com.esselion.pass.MyApplication;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefAdapter {
    private static final String PASS_SHARED_PREF = "PASS_PREF";
    private static final String TOKEN = "token";
    private static final String INTRO_STATUS = "intro_shown";
    private static final String UNSEEN_CHATS = "unseen_chats";
    private static final String UNSEEN_TASK_HISTORY = "unseen_task_history";
    private static final String UNSEEN_QUES_HISTORY = "unseen_ques_history";
    //    private static final String UNSEEN_NOTIFICATION = "unseen_notification";
    private final static SharedPrefAdapter sharedPrefAdapter = new SharedPrefAdapter();
    private SharedPreferences prefs;

    private SharedPrefAdapter() {
        prefs = MyApplication.getInstance().getSharedPreferences(PASS_SHARED_PREF, MODE_PRIVATE);
    }

    public static SharedPrefAdapter getInstance() {
        return sharedPrefAdapter;
    }

    public boolean hasUnseenChats() {
        return prefs.getBoolean(UNSEEN_CHATS, false);
    }

    public boolean hasUnseenTaskHistory() {
        return prefs.getBoolean(UNSEEN_TASK_HISTORY, false);
    }

    public boolean hasUnseenQuesHistory() {
        return prefs.getBoolean(UNSEEN_QUES_HISTORY, false);
    }

    public boolean hasUnseenNotification() {
        return
//                prefs.getBoolean(UNSEEN_CHATS, false) ||
                prefs.getBoolean(UNSEEN_QUES_HISTORY, false) ||
                        prefs.getBoolean(UNSEEN_TASK_HISTORY, false);
    }

    public void setHasNotification() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(UNSEEN_TASK_HISTORY, false);
        editor.putBoolean(UNSEEN_QUES_HISTORY, false);
//        editor.putBoolean(UNSEEN_CHATS, false);
        editor.apply();
    }

    public void setHasChats(boolean has) {
        prefs.edit().putBoolean(UNSEEN_CHATS, has).apply();
    }

    public void setHasTaskHistory(boolean has) {
        prefs.edit().putBoolean(UNSEEN_TASK_HISTORY, has).apply();
    }

    public void setHasQuesHistory(boolean has) {
        prefs.edit().putBoolean(UNSEEN_QUES_HISTORY, has).apply();
    }

    public boolean isIntroShown() {
        return prefs.getBoolean(INTRO_STATUS, false);
    }

    public void setIntroShown() {
        prefs.edit().putBoolean(INTRO_STATUS, true).apply();
    }

    public String getToken() {
        return prefs.getString(TOKEN, null);
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

}
