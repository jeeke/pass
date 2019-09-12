package com.example.mytasker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.example.mytasker.models.Feed;
import com.example.mytasker.models.Message;
import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.Contracts;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Cache.getDatabase;
import static com.example.mytasker.util.Contracts.avatars;
import static com.example.mytasker.util.Tools.getRetrofit;

public class Server extends Service {


    private String imageUrl;

    static ProgressDialog dialog;

    public static void setServerCallCompleteListener(ServerCallCompleteListener listener) {
        if (listener == null) dialog = null;
        mListener = listener;
    }

    private void showProgressBar() {
        Activity activity = ((Activity) mListener);
        if (mListener != null) {
            ProgressBar progressBar = activity.findViewById(R.id.progress_bar);
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            else {
                dialog = new ProgressDialog(activity);
                dialog.show();
            }
        }
    }

    private void notifyListener(boolean success, String titlePos, String titleNeg, OnRetryListener retryListener) {
        if (mListener != null)
            if (success) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                    mListener.onServerCallSuccess(null);
                } else mListener.onServerCallSuccess(titlePos);
            } else {
                if (dialog != null) dialog.dismiss();
                mListener.onServerCallFailure(titleNeg, retryListener);
            }
        //TODO send notification
        //not for auth actions
    }

    private static ServerCallCompleteListener mListener;
    private final ServerBinder mBinder = new ServerBinder();

    //method 1
    private void uploadImage(OnInternalCallCompleteListener listener, Uri mUri, ImageView image) {
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

        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        ((BitmapDrawable) image.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        uploadTask = imageRef.putBytes(data);
        uploadTask.addOnProgressListener(taskSnapshot -> {
        }).addOnPausedListener(taskSnapshot -> {
            uploadTask.cancel();
            imageUrl = null;
            listener.onCallComplete(false);
        }).addOnFailureListener(exception -> {
            uploadTask.cancel();
            imageUrl = null;
            listener.onCallComplete(false);
        }).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
            imageUrl = uri1.toString();
            listener.onCallComplete(true);
        }));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //method 1
    public void updateImage(FirebaseUser user, String url, Uri uri, ImageView image) {
        showProgressBar();
        imageUrl = url;
        final OnRetryListener retry = () -> updateImage(user, imageUrl, uri, image);
        if (url == null) {
            uploadImage(success -> {
                if (success) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(imageUrl))
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task ->
                                    notifyListener(task.isSuccessful(),
                                            "Profile Picture Updated",
                                            "Couldn't Update Picture", retry));
                } else {
                    notifyListener(false, "", "Picture couldn't be Uploaded", retry);
                }
            }, uri, image);
        } else {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(url))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task ->
                            notifyListener(task.isSuccessful(),
                                    "Profile Picture Updated",
                                    "Couldn't Update Picture", retry));
        }
    }

    //method 2
    public void postBid(Map bid) {
        showProgressBar();
        Contracts.call(bid, "bid").addOnCompleteListener(t -> {
            notifyListener(t.isSuccessful(),
                    "Bidding Successful", "Bidding Unsuccessful", () -> postBid(bid));
        });
    }

    //method 3
    public void editPassword(FirebaseUser user, String password, String newPassword) {
        showProgressBar();
        final OnRetryListener retry = () -> editPassword(user, password, newPassword);
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        notifyListener(false, "", "Authentication Error", retry);
                    else user.updatePassword(newPassword)
                            .addOnCompleteListener(task1 -> notifyListener(task1.isSuccessful(), "Password Updated", "Password Updating Unsuccessful", retry));
                });
    }

    //Method 4
    public void rate(Map data) {
        showProgressBar();
        Contracts.call(data, "rate").addOnCompleteListener(t -> {
            notifyListener(t.isSuccessful(), "Rating Successful", "Rating Unsuccessful", () -> rate(data));
        });

    }

    // method 5
    public void signUp(String name, String email, String password) {
        showProgressBar();
        FirebaseAuth.getInstance().
                createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        initProfile(name);
                    } else notifyListener(false, "",
                            "SignUp Unsuccessful", () -> signUp(name, email, password));
                });
    }

    private void initProfile(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(avatars[(new Random().nextInt() % 5)]))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task ->
                            notifyListener(task.isSuccessful(), "SignUp Successful",
                                    "SignUp Unsuccessful", () -> initProfile(name)));
        } else {
            notifyListener(false, "",
                    "SignUp Unsuccessful", () -> initProfile(name));
        }
    }

    // Method 6
    public void login(String email, String password) {
        showProgressBar();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
//                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                    notifyListener(task.isSuccessful(),
                            "Login Successful",
                            "Login Unsuccessful", () -> login(email, password));
                });
    }

    //Method 7
    public void postFeed(FirebaseUser user, boolean onPortfolio, String text, ImageView image, Uri uri, String url) {
        showProgressBar();
        imageUrl = url;
        final OnRetryListener retry = () -> postFeed(user, onPortfolio, text, image, uri, imageUrl);
        if (uri == null || imageUrl != null) {
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
                notifyListener(task.isSuccessful(), "Feed Posted", "Couldn't Post Feed", retry);
            });
        } else {
            uploadImage(success -> retry.retryTask(), uri, image);
        }

    }

    //Method 8
    public void postQuestion(FirebaseUser user, String token, String q, double lon, double lat) {
        showProgressBar();
        Date date = new Date();
        Question question = new Question(
                date.getTime(),
                q,
                user.getUid(),
                user.getDisplayName(),
                user.getPhotoUrl().toString(),
                lon,
                lat
        );
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Question> call = jsonPlaceHolder.createQuestion(question);
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                notifyListener(response.isSuccessful(),
                        "Question Posted",
                        "Couldn't Post Question", () ->
                                postQuestion(user, token, q, lon, lat));
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                notifyListener(false,
                        "",
                        "Couldn't Post Question", () ->
                                postQuestion(user, token, q, lon, lat));
            }
        });
    }

    public void postTask(String token, Task tsk) {
        showProgressBar();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Task> call = jsonPlaceHolder.createTask(tsk);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                notifyListener(response.isSuccessful(),
                        "Task Posted",
                        "Couldn't Post Task", () ->
                                postTask(token, tsk));
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                notifyListener(false,
                        "",
                        "Couldn't Post Task", () ->
                                postTask(token, tsk));
            }
        });
    }

    public void deleteQuestion(String token, long c_date, String id) {
        showProgressBar();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Message> call = jsonPlaceHolder.deleteQues(id, c_date);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
            }
        });
    }

    public void assignTsk(String token, String tasker_id, Task task) {
        showProgressBar();
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Assigning Task...");
        dlg.show();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Map map = new HashMap();
        map.put("task", task);
        map.put("tasker_id", tasker_id);
        Call<Message> call = jsonPlaceHolder.assignTask(map);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                notifyListener(response.isSuccessful(), "Task Assigned", "Couldn't Assign Task", () -> {
                    assignTsk(token, tasker_id, task);
                });
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("error ", t.getMessage());
                notifyListener(false, "Task Assigned", "Couldn't Assign Task", () -> {
                    assignTsk(token, tasker_id, task);
                });
            }
        });
    }

    public void cancelBid(String tid) {
        showProgressBar();
        Map map = new HashMap();
        map.put("t_id", tid);
        Contracts.call(map, "cancelBid").addOnCompleteListener(t -> {
            notifyListener(t.isSuccessful(), "Bid Cancelled", "Couldn't Cancel Bid", () -> cancelBid(tid));
        });
    }

    public void taskDone(Task current) {
        showProgressBar();
        Map map = new HashMap();
        map.put("p_id", current.getPoster_id());
        map.put("task_id", current.getId());
        map.put("task_title", current.getTitle());
        map.put("price", current.getCost());
        Contracts.call(map, "taskDone").addOnCompleteListener(t -> {
            notifyListener(t.isSuccessful(), "Task Done", "Couldn't Mark Task", new OnRetryListener() {
                @Override
                public void retryTask() {
                    taskDone(current);
                }
            });
//            Intent intent = new Intent(this, FeedbackByTaskerActivity.class);
//            intent.putExtra("task_id", current.getId());
//            intent.putExtra("task_title", current.getTitle());
//            intent.putExtra("poster_id", current.getPoster_id());
//            startActivity(intent);
        });
        //TODO rating task done
    }

    public void saveAbout(String about, String uid) {
        showProgressBar();
        getDatabase()
                .child("/Profiles/" + uid + "/about").setValue(about)
                .addOnCompleteListener(task -> {
                    notifyListener(task.isSuccessful(),
                            "Updated About",
                            "Couldn't Update About", () -> saveAbout(about, uid));
                });
    }

    public void addSkill(String skill, String uid) {
        showProgressBar();
        getDatabase()
                .child("/Profiles/" + uid + "/Skills/" + skill).setValue(true)
                .addOnCompleteListener(t -> notifyListener(t.isSuccessful(),
                        "Added Skill",
                        "Skill Removed", () -> addSkill(skill, uid)));
    }

    public void removeSkill(String skill, String uid) {
        showProgressBar();
        getDatabase()
                .child("/Profiles/" + uid + "/Skills/" + skill).removeValue()
                .addOnCompleteListener(task -> {
                    notifyListener(task.isSuccessful(),
                            "Skill Removed",
                            "Couldn't Remove Skill", () -> removeSkill(skill, uid));
                });
    }

    public interface OnRetryListener {
        void retryTask();
    }

    public interface ServerCallCompleteListener {
        void onServerCallSuccess(String title);

        void onServerCallFailure(String title, OnRetryListener retryListener);

    }

    private interface OnInternalCallCompleteListener {
        void onCallComplete(boolean success);
    }

    public class ServerBinder extends Binder {
        public Server getService() {
            return Server.this;
        }
    }


}
