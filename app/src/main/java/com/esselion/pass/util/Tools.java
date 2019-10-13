package com.esselion.pass.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.retrofit.NullOnEmptyConverterFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.esselion.pass.MyFirebaseMessagingService.MY_PREFS_NAME;

public class Tools {

    public static Dialog getLoadingAnim(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        return dialog;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat formatter =
                new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
        return formatter.format(date);
    }

    public static String formatCount(long count) {
        if (count > 1000000) {
            return (count / 1000000) + "m+";
        } else if (count > 1000) {
            return (count / 1000) + "k+";
        } else return count + "";
    }

    public static String formatDate(Long date) {

        SimpleDateFormat formatter =
                new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
        return formatter.format(date);
    }


    public static String elapsedTime(long cDate) {
        long different = new Date().getTime() - cDate;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthsInMilli = daysInMilli * 30;

        long elapsedMonths = different / monthsInMilli;
        different = different % monthsInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;

        String elapsed;
        long e;
        if (elapsedMonths != 0) {
            elapsed = elapsedMonths + " month";
            e = elapsedMonths;
        } else if (elapsedDays != 0) {
            elapsed = elapsedDays + " day";
            e = elapsedDays;
        } else if (elapsedHours != 0) {
            elapsed = elapsedHours + " hour";
            e = elapsedHours;
        } else if (elapsedMinutes != 0) {
            elapsed = elapsedMinutes + " minute";
            e = elapsedMinutes;
        } else {
            elapsed = elapsedSeconds + " second";
            e = elapsedSeconds;
        }
        if (e > 1) elapsed += 's';
        return elapsed + " ago";
    }

    public static void initMinToolbar(AppCompatActivity activity, String title) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void launchActivity(Activity activity, Class className) {
        if (activity != null) activity.startActivity(new Intent(activity, className));
    }

    public static void launchActivity(Activity activity, Intent intent) {
        if (activity != null) activity.startActivity(intent);
    }

    public static void launchActivityForResult(Activity activity, Intent intent, int requestCode) {
        if (activity != null) activity.startActivityForResult(intent, requestCode);
    }

    public static void finishNLaunchActivity(Activity activity, Intent intent) {
        if (activity != null) {
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public static void finishNLaunchActivity(Activity activity, Class className) {
        if (activity != null) {
            activity.startActivity(new Intent(activity, className));
            activity.finish();
        }
    }


    public static Retrofit getRetrofit(String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", token); // <-- this is the important line
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        OkHttpClient client = httpClient.build();
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(Contracts.BASE_POST_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void setToken(Activity context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        setOnline(context, "true");
        boolean changed = prefs.getBoolean("token_changed", true);
        if (changed) {
            FirebaseUser currentUser = Cache.getUser();
            String token = prefs.getString("token", "0");
            DatabaseReference mUserRef = Cache.getDatabase().child("Users").child(currentUser.getUid()).child("device_token");
            mUserRef.setValue(token).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    prefs.edit().putBoolean("token_changed", false).apply();
                }
            });
        }
    }

    public static void removeToken(Activity context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("token_changed", true).apply();
        Cache.getDatabase().child("Users").child(Cache.getUser().getUid()).child("device_token").removeValue();
    }

    public static void setOnline(Activity activity, Object online) {
        FirebaseUser currentUser = Cache.getUser();
        if (currentUser != null) {
            DatabaseReference mUserRef = Cache.getDatabase().child("Users").child(currentUser.getUid());
            mUserRef.child("online").setValue(online);
        }
    }

    public static void showSnackBar(Activity activity, String mes) {
        Snackbar.make(activity.findViewById(android.R.id.content), mes, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackBar(View view, String mes) {
        Snackbar.make(view, mes, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackBar(Activity activity, String mes, Server.OnRetryListener retryListener) {
        Snackbar.make(activity.findViewById(android.R.id.content)
                , mes, Snackbar.LENGTH_SHORT)
                .setAction("RETRY", v -> retryListener.retryTask()).show();
    }

    public static void showSnackBar(View view, String mes, Server.OnRetryListener retryListener) {
        Snackbar.make(view, mes,
                Snackbar.LENGTH_SHORT)
                .setAction("RETRY", v -> retryListener.retryTask()).show();
    }

}