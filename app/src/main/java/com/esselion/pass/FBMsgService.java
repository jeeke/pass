package com.esselion.pass;

import android.graphics.Bitmap;
import android.os.Handler;

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
import com.esselion.pass.util.SharedPrefAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Contracts.dpToPx;
import static com.esselion.pass.util.Contracts.getPushKey;

public class FBMsgService extends com.google.firebase.messaging.FirebaseMessagingService {

    //    private static final String TAG = "FBMsgService";
    public static final String CHANNEL_ID = "7312";

    @Override
    public void onNewToken(@NotNull String token) {
        sendRegistrationToServer(token);
    }

    //TODO index firebase data
    private static OnNewNotificationListener newNotificationListener;
    final String GROUP_KEY_NOTIFICATION = "com.esselion.pass.GROUP_NOTIFICATION";

    public static void registerNotificationListener(OnNewNotificationListener listener) {
        newNotificationListener = listener;
    }

    //TODO Grouping notifications
    private int id = 0;

    public static void unregisterNotificationListener() {
        newNotificationListener = null;
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
                        cb.onSizeReady(display_size, display_size);
                    }

                    @Override
                    public void removeCallback(@NotNull SizeReadyCallback cb) {
                    }
                }));
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notificationHandler(remoteMessage.getData());
    }

    private void sendRegistrationToServer(String token) {
        SharedPrefAdapter.getInstance().setToken(token);
    }

    private void sendNotification(Bitmap bitmap, Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        android.app.Notification n = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setGroup(GROUP_KEY_NOTIFICATION)
                .setGroupSummary(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notification.fetchTitle())
                .setContentText(notification.content)
                .setContentIntent(notification.getPendingIntent(this))
                .setLargeIcon(bitmap)
                .setColor(ContextCompat.getColor(this, R.color.orange))
                .build();
//                .setStyle(new NotificationCompat.InboxStyle())
//                .setStyle(new NotificationCompat.BigTextStyle())
        notificationManager.notify(id++, n);

    }

    private void notificationHandler(Map<String, String> map) {
        Notification notification = new Notification(map);
        setSeenStatus(notification);
        if (notification.getType() == 4) getImage(notification);
        else {
            DatabaseReference ref =
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("/Notifications/" + getUser().getUid());
            ref.child(getPushKey(ref)).setValue(notification.toMap()).addOnCompleteListener(task -> getImage(notification));

        }
    }

    private void setSeenStatus(Notification notification) {
        SharedPrefAdapter spAdapter = SharedPrefAdapter.getInstance();
        switch (notification.getType()) {
            case 0:
            case 1:
            case 2:
                spAdapter.setHasTaskHistory(true);
                break;
            case 3:
                spAdapter.setHasQuesHistory(true);
                break;
            case 4:
                spAdapter.setHasChats(true);
        }
        if (newNotificationListener != null) newNotificationListener.onNewNotification();
    }

    public interface OnNewNotificationListener {
        void onNewNotification();
    }
}