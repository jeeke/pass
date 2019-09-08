package com.example.mytasker.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mytasker.R;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;
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
import static com.example.mytasker.MyFirebaseMessagingService.MY_PREFS_NAME;

public class Tools {
//    public static void displayImageRound(final Context ctx, final ImageView img, @DrawableRes int drawable) {
//        try {
//            Glide.with(ctx).asBitmap().load(drawable).apply(RequestOptions.centerCropTransform()).into(new BitmapImageViewTarget(img) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    img.setImageDrawable(circularBitmapDrawable);
//                }
//            });
//        } catch (Exception e) {
//        }
//    }

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
        Log.e("Elapsed Time", new Date().getTime() + "\n\n " + cDate);
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

    public static void initMinToolbar(AppCompatActivity activity, String title, boolean zeroElevation) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (zeroElevation) {
            toolbar.setElevation(0f);
        }
//        Drawable drawable = activity.getDrawable(R.drawable.ic_back);
//        drawable.setTint(Color.parseColor("#80ffffff"));
//        toolbar.setNavigationIcon(drawable);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void launchActivity(Activity context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
//        context.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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

    public static void setToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        setOnline("true");
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

    public static void removeToken(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("token_changed", true).apply();
        Cache.getDatabase().child("Users").child(Cache.getUser().getUid()).child("device_token").removeValue();
    }

    public static void setOnline(Object online) {
        FirebaseUser currentUser = Cache.getUser();
        if (currentUser != null) {
            DatabaseReference mUserRef = Cache.getDatabase().child("Users").child(currentUser.getUid());
            mUserRef.child("online").setValue(online);
        }
    }

    public static void showToast(Context context, @StringRes int text, boolean isLong) {
        showToast(context, context.getString(text), isLong);
    }

    public static void showToast(Context context, String text, boolean isLong) {
        Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

}