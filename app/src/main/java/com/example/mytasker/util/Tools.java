package com.example.mytasker.util;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mytasker.R;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;

import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        if (elapsedMonths != 0) elapsed = elapsedMonths + " months";
        else if (elapsedDays != 0) elapsed = elapsedDays + " days";
        else if (elapsedHours != 0) elapsed = elapsedHours + " hours";
        else if (elapsedMinutes != 0) elapsed = elapsedMinutes + " minutes";
        else elapsed = elapsedSeconds + " seconds";
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

    public static Retrofit getRetrofit(String token){
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

}