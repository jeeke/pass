package com.example.mytasker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.mytasker.activities.LocationActivity;
import com.example.mytasker.models.Feed;
import com.example.mytasker.models.Message;
import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.Contracts;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import static com.example.mytasker.util.Contracts.getPushKey;
import static com.example.mytasker.util.Tools.getRetrofit;

public class Server extends Service {


    public static int SERVER_UPDATE_IMAGE = 1110;
    public static int SERVER_POST_BID = 1111;
    public static int SERVER_EDIT_PASSWORD = 1112;
    public static int SERVER_RATE = 1113;
    public static int SERVER_SIGNUP = 1114;
    public static int SERVER_LOGIN = 1115;
    public static int SERVER_POST_FEED = 1116;
    public static int SERVER_POST_TASK = 1117;
    public static int SERVER_POST_QUESTION = 1118;
    public static int SERVER_DELETE_QUESTION = 1119;
    public static int SERVER_ASSIGN_TASK = 1120;
    public static int SERVER_CANCEL_BID = 1121;
    public static int SERVER_TASK_DONE = 1122;
    public static int SERVER_SAVE_ABOUT = 1123;
    public static int SERVER_ADD_SKILL = 1124;
    public static int SERVER_REMOVE_SKILL = 1125;

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

    private void notifyListener(boolean success, int methodId, String titlePos, String titleNeg, OnRetryListener retryListener) {
        if (mListener != null)
            if (success) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                    mListener.onServerCallSuccess(methodId, null);
                } else mListener.onServerCallSuccess(methodId, titlePos);
            } else {
                if (dialog != null) dialog.dismiss();
                mListener.onServerCallFailure(methodId, titleNeg, retryListener);
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
            listener.onCallComplete(false, null);
        }).addOnFailureListener(exception -> {
            uploadTask.cancel();
            listener.onCallComplete(false, null);
        }).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
            listener.onCallComplete(true, uri1.toString());
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
        final OnRetryListener retry = () -> updateImage(user, url, uri, image);
        if (url == null) {
            uploadImage((success, imageUrl) -> {
                if (success) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(imageUrl))
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task ->
                                    notifyListener(task.isSuccessful(),
                                            SERVER_UPDATE_IMAGE,
                                            "Profile Picture Updated",
                                            "Couldn't Update Picture", retry));
                } else {
                    notifyListener(false,
                            SERVER_UPDATE_IMAGE,
                            "",
                            "Picture couldn't be Uploaded",
                            retry);
                }
            }, uri, image);
        } else {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(url))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task ->
                            notifyListener(task.isSuccessful(),
                                    SERVER_UPDATE_IMAGE,
                                    "Profile Picture Updated",
                                    "Couldn't Update Picture", retry));
        }
    }

    //method 2
    public void postBid(Map bid) {
        showProgressBar();
        Contracts.call(bid, "bid").addOnCompleteListener(t -> {
            notifyListener(t.isSuccessful(),
                    SERVER_POST_BID,
                    "Bidding Successful",
                    "Bidding Unsuccessful",
                    () -> postBid(bid));
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
                        notifyListener(false, SERVER_EDIT_PASSWORD,
                                "", "Authentication Error", retry);
                    else user.updatePassword(newPassword)
                            .addOnCompleteListener(task1 -> notifyListener(task1.isSuccessful(),
                                    SERVER_EDIT_PASSWORD, "Password Updated",
                                    "Password Updating Unsuccessful", retry));
                });
    }

    //Method 4
    public void rate(Map data) {
        showProgressBar();
        Contracts.call(data, "rate").addOnCompleteListener(t -> {
            notifyListener(t.isSuccessful(), SERVER_RATE,
                    "Rating Successful", "Rating Unsuccessful", () -> rate(data));
        });

    }

    // method 5
    public void signUp(String name, String email, String password) {
        showProgressBar();
        FirebaseAuth.getInstance().
                createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendEmailVerificationLink(name);
                    } else notifyListener(false, SERVER_SIGNUP, "",
                            "SignUp Unsuccessful", () -> signUp(name, email, password));
                });
    }

    private void initProfile(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(avatars[(int) (new Date().getTime() % 5)]))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task ->
                            notifyListener(task.isSuccessful(), SERVER_SIGNUP, "SignUp Successful",
                                    "SignUp Unsuccessful", () -> initProfile(name)));
        } else {
            notifyListener(false, SERVER_SIGNUP, "",
                    "SignUp Unsuccessful", () -> initProfile(name));
        }
    }

    private void sendEmailVerificationLink(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            initProfile(name);
                        } else notifyListener(false, SERVER_SIGNUP, "",
                                "SignUp Unsuccessful", () -> sendEmailVerificationLink(name));
                    });
        } else {
            notifyListener(false, SERVER_SIGNUP, "",
                    "SignUp Unsuccessful", () -> sendEmailVerificationLink(name));
        }
    }

    // Method 6
    public void login(String email, String password) {
        showProgressBar();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    notifyListener(task.isSuccessful(),
                            SERVER_LOGIN,
                            "Login Successful",
                            "Login Unsuccessful", () -> login(email, password));
                });
    }

    public static Location location;

    //Method 7
    public void postFeed(FirebaseUser user, boolean onPortfolio, String text, ImageView image, Uri uri, String url) {
        showProgressBar();
        final OnRetryListener retry = () -> postFeed(user, onPortfolio, text, image, uri, url);
        if (uri == null || url != null) {
            postFeedHelper(user, onPortfolio, url, text, retry);
        } else {
            uploadImage((success, imageUrl) -> postFeedHelper(user, onPortfolio, imageUrl, text, retry), uri, image);
        }

    }

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LocationActivity.LocationListener mLocationListener;

    private void postFeedHelper(FirebaseUser user, boolean onPortfolio, String url, String text, OnRetryListener retry) {
        Date date = new Date();
        Feed feed = new Feed(
                date.getTime(),
                user.getUid(),
                user.getDisplayName(),
                user.getPhotoUrl().toString(),
                url,
                text
        );
        DatabaseReference push = getDatabase();
        String key = getPushKey(push.child("Feeds"));
        feed.setId(key);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("Feeds/" + key, feed);
        if (onPortfolio)
            updateMap.put("Portfolios/" + user.getUid() + "/" + key, feed);
        updateMap.put("PrevFeeds/" + user.getUid() + "/" + key, feed);
        push.updateChildren(updateMap).addOnCompleteListener(task -> {
            notifyListener(task.isSuccessful(), SERVER_POST_FEED,
                    "Feed Posted", "Couldn't Post Feed", retry);
        });
    }

    public void getLocation(LocationActivity.LocationListener locationListener) {
        if (location != null) locationListener.onLocationFetched(location);
        mLocationListener = locationListener;
    }

    public void fetchLocation() {
        if (location == null) {
            FusedLocationProviderClient fusedLocationClient;
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setExpirationDuration(120 * 1000); // 2 minutes, in milliseconds
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            Server.location = location;
                            if (mLocationListener != null)
                                mLocationListener.onLocationFetched(location);
                            fusedLocationClient.removeLocationUpdates(this);
                        }
                    }
                }
            };
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.
                    requestLocationUpdates(mLocationRequest,
                            mLocationCallback,
                            Looper.getMainLooper());
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
                        SERVER_POST_QUESTION,
                        "Question Posted",
                        "Couldn't Post Question", () ->
                                postQuestion(user, token, q, lon, lat));
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                notifyListener(false,
                        SERVER_POST_QUESTION,
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
                        SERVER_POST_TASK,
                        "Task Posted",
                        "Couldn't Post Task", () ->
                                postTask(token, tsk));
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                notifyListener(false,
                        SERVER_POST_TASK,
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
                notifyListener(response.isSuccessful(), SERVER_DELETE_QUESTION,
                        "Question Deleted", "Couldn't Delete Question",
                        () -> deleteQuestion(token, c_date, id));
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("error ", t.getMessage());
                notifyListener(false, SERVER_DELETE_QUESTION,
                        "", "Couldn't Delete Question",
                        () -> deleteQuestion(token, c_date, id));
            }
        });
    }

    public void assignTask(String token, String tasker_id, Task task) {
        showProgressBar();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Map map = new HashMap();
        map.put("task", task);
        map.put("tasker_id", tasker_id);
        Call<Message> call = jsonPlaceHolder.assignTask(map);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                notifyListener(response.isSuccessful(), SERVER_ASSIGN_TASK,
                        "Task Assigned", "Couldn't Assign Task",
                        () -> assignTask(token, tasker_id, task));
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("error ", t.getMessage());
                notifyListener(false,
                        SERVER_ASSIGN_TASK,
                        "Task Assigned",
                        "Couldn't Assign Task", () -> {
                            assignTask(token, tasker_id, task);
                        });
            }
        });
    }

    public void cancelBid(String tid) {
        showProgressBar();
        Map map = new HashMap();
        map.put("t_id", tid);
        Contracts.call(map, "cancelBid").addOnCompleteListener(t -> notifyListener(t.isSuccessful(),
                SERVER_CANCEL_BID,
                "Bid Cancelled", "Couldn't Cancel Bid", () -> cancelBid(tid)));
    }

    public void taskDone(Task current) {
        showProgressBar();
        Map map = new HashMap();
        map.put("p_id", current.getPoster_id());
        map.put("task_id", current.getId());
        map.put("task_title", current.getTitle());
        map.put("price", current.getCost());
        Contracts.call(map, "taskDone").addOnCompleteListener(t -> {
            notifyListener(t.isSuccessful(), SERVER_TASK_DONE, "Task Done",
                    "Couldn't Mark Task", () -> taskDone(current));
        });
        //TODO rating task done
    }

    public void saveAbout(String about, String uid) {
        showProgressBar();
        getDatabase()
                .child("/Profiles/" + uid + "/about").setValue(about)
                .addOnCompleteListener(task -> {
                    notifyListener(task.isSuccessful(),
                            SERVER_SAVE_ABOUT,
                            "Updated About",
                            "Couldn't Update About", () -> saveAbout(about, uid));
                });
    }

    public void addSkill(String skill, String uid) {
        showProgressBar();
        getDatabase()
                .child("/Profiles/" + uid + "/Skills/" + skill).setValue(true)
                .addOnCompleteListener(t -> notifyListener(t.isSuccessful(),
                        SERVER_ADD_SKILL,
                        "Added Skill",
                        "Skill Removed", () -> addSkill(skill, uid)));
    }

    public void removeSkill(String skill, String uid) {
        showProgressBar();
        getDatabase()
                .child("/Profiles/" + uid + "/Skills/" + skill).removeValue()
                .addOnCompleteListener(task -> {
                    notifyListener(task.isSuccessful(),
                            SERVER_REMOVE_SKILL,
                            "Skill Removed",
                            "Couldn't Remove Skill",
                            () -> removeSkill(skill, uid));
                });
    }

    public interface OnRetryListener {
        void retryTask();
    }

    public interface ServerCallCompleteListener {
        void onServerCallSuccess(int methodId, String title);

        void onServerCallFailure(int methodId, String title, OnRetryListener retryListener);

    }

    private interface OnInternalCallCompleteListener {
        void onCallComplete(boolean success, String url);
    }

    public class ServerBinder extends Binder {
        public Server getService() {
            return Server.this;
        }
    }


}
