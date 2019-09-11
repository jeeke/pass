package com.example.mytasker.util;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mytasker.models.Feed;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.mytasker.util.Cache.getDatabase;
import static com.example.mytasker.util.Cache.getUser;
import static com.example.mytasker.util.Contracts.avatars;

public class Server extends Service {

    private static ServerCallCompleteListener mListener;
    private final ServerBinder mBinder = new ServerBinder();

    public static void setListener(ServerCallCompleteListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void notifyListener(boolean success, int method) {
        if (mListener != null) mListener.onCallComplete(success, method);
        else {
//           int i = 5;
        }
        //TODO send notification
    }

    //method 1
    private void uploadImage(Uri mUri, Bitmap bitmap, int method) {
        UploadTask uploadTask;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Random rand = new Random();
        int n = rand.nextInt(10);
        String path = "images/" + n + "/";
        n = rand.nextInt(10);
        path += n + "/";
        path += new Date().getTime() + mUri.getLastPathSegment();
        StorageReference imageRef = storage.getReference().child(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        uploadTask = imageRef.putBytes(data);
        uploadTask.addOnProgressListener(taskSnapshot -> {
        }).addOnPausedListener(taskSnapshot -> {
            uploadTask.cancel();
            notifyListener(false, method);
        }).addOnFailureListener(exception -> {
            uploadTask.cancel();
            notifyListener(false, method);
        }).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
            if (method == 1)
                updateImage(null, null, uri1.toString());
            else {

            }
        }));
    }

    //method 1
    public void updateImage(Uri uri, Bitmap bitmap, String imageUrl) {
        if (imageUrl != null) {
            FirebaseUser user = getUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(imageUrl))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> notifyListener(task.isSuccessful(), 1));
        } else {
            uploadImage(uri, bitmap, 1);
        }
    }

    //method 2
    public void postBid(Map bid) {
        Contracts.call(bid, "bid").addOnCompleteListener(t -> {
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.e("tag", ffe + "\n" + code + "\n" + details);
                }
                notifyListener(false, 2);
            } else notifyListener(true, 2);
        });
    }

    //method 3
    public void editPassword(String password, String newPassword) {
        FirebaseUser user = getUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) notifyListener(false, 3);
                    else user.updatePassword(newPassword)
                            .addOnCompleteListener(task1 -> notifyListener(task1.isSuccessful(), 3));
                });
    }

    //Method 4
    public void rate(Map data) {
        Contracts.call(data, "rate").addOnCompleteListener(t -> {
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.e("tag", ffe + "\n" + code + "\n" + details);
                }
                notifyListener(false, 4);
            } else notifyListener(true, 4);
        });

    }

    // method 5
    public void signUp(String name, String email, String password) {
        FirebaseAuth.getInstance().
                createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        initProfile(name);
                    } else notifyListener(false, 5);
                });
    }

    private void initProfile(String name) {
        FirebaseUser user = getUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(avatars[(int) (new Date().getTime() % 6)]))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> notifyListener(task.isSuccessful(), 5));
        }
    }

    // Method 6
    public void login(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
//                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                    notifyListener(task.isSuccessful(), 6);
                });
    }

    //Method 7
    public void postFeed(boolean onPortfolio, String text, Bitmap bitmap, Uri mUri, String imageUrl) {
        if (imageUrl != null) {
            FirebaseUser user = getUser();
            Date date = new Date();
            Feed feed = new Feed(
                    date.getTime(),
                    user.getUid(),
                    user.getDisplayName(),
                    user.getPhotoUrl().toString(),
                    imageUrl,
                    text
            );
            DatabaseReference push = getDatabase();
            String key = push.child("Feeds").push().getKey();
            feed.setId(key);
            Map updateMap = new HashMap();
            updateMap.put("Feeds/" + key, feed);
            if (onPortfolio)
                updateMap.put("Portfolios/" + user.getUid() + "/" + key, feed);
            updateMap.put("PrevFeeds/" + user.getUid() + "/" + key, feed);
            push.updateChildren(updateMap).addOnCompleteListener(task -> {
                notifyListener(task.isSuccessful(), 7);
            });
        } else {

        }

    }

    public interface ServerCallCompleteListener {
        void onCallComplete(boolean success, int method);
    }

    public class ServerBinder extends Binder {
        public Server getService() {
            return Server.this;
        }
    }

}
