package com.example.mytasker.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.mytasker.activities.MainActivity;
import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Cache {
    private static DatabaseReference mDataBase;

    public static ArrayList<Task> tasks;
    public static ArrayList<Question> questions;
    public static FirebaseUser mUser;
    private static String token;

    public static void emptyCache() {
        token = null;
        mUser = null;
        tasks = null;
        questions = null;
    }

    public static void getToken(OnTokenReceivedListener listener, Activity activity) {
        if (token == null) {
            getUser(activity).getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            token = task.getResult().getToken();
                        } else {
                            token = "null";
                            Log.e("Cache getToken Error", "Token could not be fetched");
                        }
                        listener.onTokenReceived(token);
                    });
        } else
            listener.onTokenReceived(token);
    }

    public static FirebaseUser getUser(Activity context) {
        if (mUser == null) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (mUser == null) {
                context.startActivity(new Intent(context, MainActivity.class));
                context.finish();
            }
        }
        return mUser;
    }

    public static DatabaseReference getDatabase() {
        if (mDataBase == null) {
            mDataBase = FirebaseDatabase.getInstance().getReference();
        }
        return mDataBase;
    }

    public interface OnTokenReceivedListener {
        void onTokenReceived(String token);
    }
}
