package com.example.mytasker;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mytasker.activities.NotificationActivity;
import com.example.mytasker.models.Bid;
import com.example.mytasker.models.Config;
import com.example.mytasker.util.CircularTransform;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String CHANNEL_ID = "123";
    public static final String MY_PREFS_NAME = "MyTasker";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            sendNotification(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // loading of the bitmap failed
            // TODO do some action/warning/error message
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notificationHandler(remoteMessage.getData());
        Log.e(TAG, "\n\n" + remoteMessage.getData().toString() + "\n\n");
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.putBoolean("token_changed", true);
        editor.apply();
    }

    private void sendNotification(Bitmap bitmap) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(Config.title)
                .setContentText(Config.content)
                .setContentIntent(pendingIntent)
                .setLargeIcon(bitmap)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());

    }

    //TODO combine notifications

    private void notificationHandler(Map<String, String> map) {
        int type = Integer.parseInt(map.get("type"));
        switch (type) {
            case 0:
                // Bid notification
                Bid bid = Bid.fromMap(map);
                Config.title = bid.getName().toUpperCase() + " bid on your task";
                Config.content = bid.getDes();
                Config.image = bid.getAvatar();
                break;
            case 1:
//                Task task = Task.fromMap(map);
                Config.title = "Congrats";
                Config.content = map.get("poster_name").toUpperCase() + " assigned you a task";
                Config.image = map.get("poster_avatar");
                break;
            case 2:
                Config.title = map.get("title");
                Config.content = map.get("content");
                Config.image = map.get("image");
                break;
            case 3:
                Config.title = "You have a new reply from " + map.get("replier_name").toUpperCase();
                Config.content = map.get("answer");
                Config.image = map.get("replier_avatar");
                break;
            case 4:
                Config.title = map.get("name");
                Config.content = map.get("text");
                Config.image = map.get("avatar");
        }
        Handler uiHandler = new Handler(getMainLooper());
        uiHandler.post(() -> Picasso.with(this)
                .load(Config.image)
                .transform(new CircularTransform())
                .into(target));
    }
}
//    Because you must create the notification channel before posting any notifications on Android 8.0 and higher,
//    you should execute this code as soon as your app starts. It's safe to call this repeatedly because creating an
//    existing notification channel performs no operation.