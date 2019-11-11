package com.esselion.pass.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.esselion.pass.MyApplication;
import com.esselion.pass.activities.MainActivity;
import com.esselion.pass.models.Location;
import com.esselion.pass.models.Question;
import com.esselion.pass.models.Task;
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
    private static Location location;

    public static void getLocation(LocationListener locationListener) {
        if (location != null) {
            if (locationListener != null) locationListener.onLocationFetched(location);
        } else MyApplication.fetchLocation(locationListener);
    }

    public static void clearLocationPreferences() {
        SharedPrefAdapter.getInstance().clearLocation();
        clearLocationCache();
    }

    public static void clearLocationCache() {
        location = null;
        MyApplication.fetchLocation(null);
    }

    public static Location getNullLocation() {
        return location;
    }

    public static void initLocation(Location l) {
        location = l;
    }

    public interface LocationListener {
        void onLocationFetched(Location location);
    }

    public static void emptyCache() {
        token = null;
        mUser = null;
        tasks = null;
        questions = null;
    }

    public static void getToken(OnTokenReceivedListener listener) {
        if (token == null) {
            getUser().getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            token = task.getResult().getToken();
                        } else {
                            token = "null";
                            Log.e("Cache getToken Error", "Token could not be fetched");
                        }
                        listener.onTokenReceived(token);
                    });
        } else listener.onTokenReceived(token);
    }

    public static FirebaseUser getUser() {
        if (mUser == null) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (mUser == null) {
                Context context = MyApplication.getInstance().getApplicationContext();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
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
