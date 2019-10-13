package com.esselion.pass.util;

import android.content.res.Resources;

import com.esselion.pass.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Contracts {

    public static final String[] TASK_DETAIL_BUTTONS = {"SHOW ALL BIDS", "ASSIGNED TO ", "DONE BY ",
            "STATUS: ALREADY BID", "STATUS: ASSIGNED", "STATUS: COMPLETED"
    };

    public static String[] avatars = {
            "https://firebasestorage.googleapis.com/v0/b/esselion-pass.appspot.com/o/avatar2.png?alt=media&token=768b9748-b959-409f-865a-af3ca195d53e",
            "https://firebasestorage.googleapis.com/v0/b/esselion-pass.appspot.com/o/avatar3.png?alt=media&token=1a488954-cd6d-454d-ac53-5e9bfc073145",
            "https://firebasestorage.googleapis.com/v0/b/esselion-pass.appspot.com/o/avatar4.png?alt=media&token=3a5cbca5-cbb2-405f-931d-97012fa14f1e"
    };
    private static Map<Character, Character> charMap = new HashMap<>();

    static {
        ArrayList<Character> chars = new ArrayList<>();
        chars.add('-');
        for (Character c = '0'; c <= '9'; c++) chars.add(c);
        for (Character c = 'A'; c <= 'Z'; c++) chars.add(c);
        chars.add('_');
        for (Character c = 'a'; c <= 'z'; c++) chars.add(c);
        for (int i = 0; i < chars.size(); i++) {
            char a = chars.get(i), b = chars.get(chars.size() - i - 1);
            charMap.put(a, b);
            charMap.put(b, a);
        }
    }

    public static final int[] TASK_STAGE_COLORS = {R.color.orange, R.color.blue_400, R.color.green_400};
    public static final int[] TASK_STAGE_BTNS = {R.drawable.btn_round_orange, R.drawable.btn_round_blue, R.drawable.btn_round_green};
    public static final int[] QUES_STAGE_COLORS = {R.color.orange, R.color.green_400};
    public static final String[] TASK_STAGE_TEXT = {"NEW", "ONGOING", "COMPLETED"};
    public static final String[] QUES_STAGE_TEXT = {"POSTED", "REPLIED"};
    static String BASE_POST_URL = "https://esselion-pass.herokuapp.com/";
    //    static String BASE_POST_URL = "https://a27190e4.ngrok.io";
    public static int CODE_SETTINGS_ACTIVITY = 100;
    public static int CODE_NOTIFICATION_ACTIVITY = 101;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getPushKey(DatabaseReference reference) {
        String s = reference.push().getKey();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            str.append(charMap.get(s.charAt(i)));
        }
        return str.toString();
    }

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