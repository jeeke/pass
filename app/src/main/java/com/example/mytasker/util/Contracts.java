package com.example.mytasker.util;

import android.content.res.Resources;

import com.example.mytasker.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Contracts {
    public static String added_skill="";
    public static final String[] TASK_DETAIL_BUTTONS = {"SHOW ALL BIDS","ASSIGNED TO ","DONE BY ",
            "ALREADY BID", "ASSIGNED", "COMPLETED"
    };
    public static final int[] TASK_STAGE_COLORS = {R.color.orange,R.color.blue_400,R.color.green_400};
    public static final int[] QUES_STAGE_COLORS = {R.color.orange,R.color.green_400};
    public static final String[] TASK_STAGE_TEXT = {"NEW","ONGOING","COMPLETED"};
    public static final String[] QUES_STAGE_TEXT = {"POSTED","REPLIED"};
//    public static String BASE_GET_URL = BASE_POST_URL = "http://27908e14.ngrok.io";
        public static String BASE_GET_URL = "https://sheltered-escarpment-49063.herokuapp.com/";
//    public static String BASE_POST_URL ;
    //=  "https://2a61d6f5.ngrok.io";
        public static String BASE_POST_URL = "https://sheltered-escarpment-49063.herokuapp.com/";
    public static int CODE_SETTINGS_ACTIVITY = 100;
    public static int CODE_NOTIFICATION_ACTIVITY = 101;
    public static int PICK_IMAGE_REQUEST = 102;
    public static int ADD_SKILL_REQUEST = 103;
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

        public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .build();
    private Contracts(){}
}