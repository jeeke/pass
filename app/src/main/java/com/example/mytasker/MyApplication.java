package com.example.mytasker;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.mytasker.broadcastReceivers.ConnectionReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.example.mytasker.MyFirebaseMessagingService.MY_PREFS_NAME;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        setToken();
    }

    private void setToken() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean changed = prefs.getBoolean("token_changed", true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String token = prefs.getString("token", "0");
        if (currentUser != null && changed && !Objects.equals(token, "0")) {
            DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("device_token");
            mUserRef.setValue(token).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    prefs.edit().putBoolean("token_changed", false).apply();
                }
            });
        }
    }


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectionListener(ConnectionReceiver.ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;
    }
}