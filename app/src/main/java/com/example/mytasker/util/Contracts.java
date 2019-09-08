package com.example.mytasker.util;

import android.content.res.Resources;

import com.example.mytasker.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Contracts {

    public static final String[] TASK_DETAIL_BUTTONS = {"SHOW ALL BIDS", "ASSIGNED TO ", "DONE BY ",
            "ALREADY BID", "ASSIGNED", "COMPLETED"
    };
    public static final int[] TASK_STAGE_COLORS = {R.color.orange, R.color.blue_400, R.color.green_400};
    public static final int[] QUES_STAGE_COLORS = {R.color.orange, R.color.green_400};
    public static final String[] TASK_STAGE_TEXT = {"NEW", "ONGOING", "COMPLETED"};
    public static final String[] QUES_STAGE_TEXT = {"POSTED", "REPLIED"};
    public static String[] avatars = {
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar2.png?alt=media&token=e682859d-1f08-4703-96f6-944476a5aa43",
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar4.png?alt=media&token=3d643200-a8cf-4614-a508-0438dcfde5b5",
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar3.png?alt=media&token=2e348dbe-a436-4670-9159-1948a77e2b12",
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar1.png?alt=media&token=2d22f9fb-f1b1-48a3-8dd8-c5b1f8946f2f",
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar6.png?alt=media&token=cf4d2845-bccf-43f4-826e-f4aac81c38cd"
    };
    public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    public static int CODE_SETTINGS_ACTIVITY = 100;
    public static int CODE_NOTIFICATION_ACTIVITY = 101;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    //    static String BASE_POST_URL = "https://bae70d07.ngrok.io";
    static String BASE_POST_URL = "https://sheltered-escarpment-49063.herokuapp.com/";

    private Contracts() {
    }

    public static Task<String> call(Map data, String fn) {
        // Create the arguments to the callable function.
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions
                .getHttpsCallable(fn)
                .call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    return task.getResult().toString();
//                    return new Message((HashMap) task.getResult().getData());
                });
    }


}