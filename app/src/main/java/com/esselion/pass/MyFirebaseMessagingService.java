package com.esselion.pass;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.esselion.pass.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.esselion.pass.util.Contracts.dpToPx;
import static com.esselion.pass.util.Contracts.getPushKey;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String CHANNEL_ID = "7312";
    public static final String MY_PREFS_NAME = "PASS";

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    //TODO index firebase data

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


    final String GROUP_KEY_NOTIFICATION = "com.example.mytasker.GROUP_NOTIFICATION";
    //TODO Grouping notifications
    private int id = 0;

    private void sendNotification(Bitmap bitmap, Notification notification) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notification.fetchTitle())
                .setContentText(notification.content)
                .setContentIntent(notification.getPendingIntent(this))
                .setLargeIcon(bitmap)
                .setGroupSummary(true)
                .setStyle(inboxStyle)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroup(GROUP_KEY_NOTIFICATION)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setColor(ContextCompat.getColor(this, R.color.orange))
                .setAutoCancel(true);
//                        .bigText("Much longer text that cannot fit one line..."))
        //GROUPING will not work if auto cancel of
        notificationManager.notify("MY TASKER", id++, builder.build());

    }

    private void getImage(Notification notification) {
        Handler uiHandler = new Handler(getMainLooper());
        uiHandler.post(() -> Glide.with(this).asBitmap().load(notification.image)
                .apply(RequestOptions.circleCropTransform())
                .into(new BaseTarget<Bitmap>() {
                    int display_size = dpToPx(64);

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        sendNotification(resource, notification);
                    }

                    @Override
                    public void getSize(@NotNull SizeReadyCallback cb) {
//                        cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL);
                        cb.onSizeReady(display_size, display_size);
                    }

                    @Override
                    public void removeCallback(@NotNull SizeReadyCallback cb) {
                    }
                }));
    }


    private void notificationHandler(Map<String, String> map) {
        Notification notification = new Notification(map);
        if (!(notification.getType() == 4)) {
            DatabaseReference ref =
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("/Notifications/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.child(getPushKey(ref)).setValue(notification.toMap()).addOnCompleteListener(task -> getImage(notification));
        } else getImage(notification);
//        TODO handle default case notification

    }


}
//    Because you must create the notification channel before posting any notifications on Android 8.0 and higher,
//    you should execute this code as soon as your app starts. It's safe to call this repeatedly because creating an
//    existing notification channel performs no operation.