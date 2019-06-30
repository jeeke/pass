package com.example.mytasker.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Contracts {
    public static String BASE_GET_URL = "http://be08270c.ngrok.io/";
    public static String BASE_POST_URL = "http://be08270c.ngrok.io/";
    public static int CODE_SETTINGS_ACTIVITY = 100;
    public static int CODE_NOTIFICATION_ACTIVITY = 101;
    public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .build();
    private Contracts(){}
}