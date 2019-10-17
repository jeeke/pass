package com.esselion.pass;

import android.app.Activity;
import android.app.Dialog;
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

import com.esselion.pass.models.Feed;
import com.esselion.pass.models.Message;
import com.esselion.pass.models.Question;
import com.esselion.pass.models.Task;
import com.esselion.pass.retrofit.JsonPlaceHolder;
import com.esselion.pass.util.Contracts;
import com.esselion.pass.util.Tools;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Contracts.avatars;
import static com.esselion.pass.util.Contracts.getPushKey;
import static com.esselion.pass.util.Tools.getRetrofit;

public class Server extends Service {
    public static int SERVER_UPDATE_IMAGE = 1110;
    public static int SERVER_POST_BID = 1111;
    public static int SERVER_EDIT_PASSWORD = 1112;
    public static int SERVER_RATE = 1113;
    public static int SERVER_SIGNUP = 1114;
    public static int SERVER_RESEND_MAIL = 1126;
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
    public static int SERVER_RESET_PASSWORD = 1154;

    static Dialog dialog;

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
                dialog = Tools.getLoadingAnim(activity);
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
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                mListener.onServerCallFailure(methodId, titleNeg, retryListener);
            }
        //TODO send notification
        //not for auth actions
    }

    private static ServerCallCompleteListener mListener;
    private final ServerBinder mBinder = new ServerBinder();

    //method 1
    private void uploadImage(OnInternalCallCompleteListener listener, Uri mUri, ImageView image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();
            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            if (drawable == null) {
                notifyListener(false,
                        SERVER_UPDATE_IMAGE,
                        "",
                        "Please pick an image",
                        null);
            } else {
                drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Random rand = new Random();
                int n = rand.nextInt(10);
                String path = "images/" + n + "/";
                n = rand.nextInt(10);
                path += n + "/";
                UploadTask uploadTask;
                path += new Date().getTime() + mUri.getLastPathSegment();
                StorageReference imageRef = storage.getReference().child(path);
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
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCallComplete(false, null);
        }

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
        if (user != null) {
            final OnRetryListener retry = () -> editPassword(user, password, newPassword);
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful())
                            notifyListener(false, SERVER_EDIT_PASSWORD,
                                    "", "Wrong Password!", null);
                        else user.updatePassword(newPassword)
                                .addOnCompleteListener(task1 -> notifyListener(task1.isSuccessful(),
                                        SERVER_EDIT_PASSWORD, "Password Updated",
                                        "Password Updating Unsuccessful", retry));
                    });
        } else notifyListener(false,
                SERVER_EDIT_PASSWORD, "",
                "Password Updating Unsuccessful", null);

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
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        notifyListener(false, SERVER_SIGNUP, "",
                                "Email already in use!, SIGN IN", null);
                    } else notifyListener(false, SERVER_SIGNUP, "",
                            "SignUp Unsuccessful", () -> signUp(name, email, password));
                });
    }

    private void initProfile(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(avatars[(int) (new Date().getTime() % 3)]))
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

    public void resendVerificationMail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            notifyListener(task.isSuccessful(), SERVER_RESEND_MAIL, "Mail Resent",
                    "Mail Couldn't be sent", () -> resendVerificationMail(user));
        });
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

    public void sendPasswordResetMail(String emailAddress) {
        showProgressBar();
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(task -> {
                    Exception e = task.getException();
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        notifyListener(
                                false, SERVER_RESET_PASSWORD,
                                "", "Invalid Email",
                                null);
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        notifyListener(
                                false, SERVER_RESET_PASSWORD,
                                "", "Email Not Registered",
                                null);
                    } else notifyListener(task.isSuccessful(), SERVER_RESET_PASSWORD,
                            "Password reset email sent", "Some error occurred",
                            () -> sendPasswordResetMail(emailAddress));
                });
    }

    // Method 6
    public void login(String email, String password) {
        showProgressBar();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    Exception e = task.getException();
                    if (e == null) notifyListener(task.isSuccessful(),
                            SERVER_LOGIN,
                            "Login Successful",
                            "Login Unsuccessful", () -> login(email, password));
                    else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        notifyListener(false,
                                SERVER_LOGIN,
                                "",
                                "Invalid Email or Password", null);
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        notifyListener(false,
                                SERVER_LOGIN,
                                "",
                                "Email not registered!, SIGN UP", null);
                    }

                });
    }

    //Method 7
    public void postFeed(FirebaseUser user, boolean portfolio, String text, ImageView image, Uri uri, String url) {
        showProgressBar();
        final OnRetryListener retry = () -> postFeed(user, portfolio, text, image, uri, url);
        if (uri == null || url != null) {
            postFeedHelper(user, portfolio, url, text, retry);
        } else {
            uploadImage((success, imageUrl) -> postFeedHelper(user, portfolio, imageUrl, text, retry), uri, image);
        }

    }


    private void postFeedHelper(FirebaseUser user, boolean portfolio, String url, String text, OnRetryListener retry) {
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
        if (portfolio) {
            push = push.child("Portfolios/" + user.getUid());
            String key = getPushKey(push);
            feed.setId(key);
            push.child(key).setValue(feed).addOnCompleteListener(task -> {
                notifyListener(task.isSuccessful(), SERVER_POST_FEED,
                        "Portfolio item added", "Couldn't add portfolio item", retry);
            });
        } else {
            String key = getPushKey(push.child("Feeds"));
            feed.setId(key);
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("Feeds/" + key, feed);
            updateMap.put("PrevFeeds/" + user.getUid() + "/" + key, feed);
            push.updateChildren(updateMap).addOnCompleteListener(task -> {
                notifyListener(task.isSuccessful(), SERVER_POST_FEED,
                        "Feed Posted", "Couldn't Post Feed", retry);
            });
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

    public void deleteTask(String token, long c_date, String tid, String category) {
        showProgressBar();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Message> call = jsonPlaceHolder.deleteTask(tid, c_date, category);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                notifyListener(response.isSuccessful(), SERVER_DELETE_QUESTION,
                        "Task Deleted", "Couldn't Delete Task",
                        () -> deleteTask(token, c_date, tid, category));
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("error ", t.getMessage());
                notifyListener(false, SERVER_DELETE_QUESTION,
                        "", "Couldn't Delete Question",
                        () -> deleteTask(token, c_date, tid, category));
            }
        });
    }

    public void assignTask(String token, Task task) {
        showProgressBar();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Map<String, Object> map = new HashMap<>();
        map.put("task", task);
        Call<Message> call = jsonPlaceHolder.assignTask(map);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                notifyListener(response.isSuccessful(), SERVER_ASSIGN_TASK,
                        "Task Assigned", "Couldn't Assign Task",
                        () -> assignTask(token, task));
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("error ", t.getMessage());
                notifyListener(false,
                        SERVER_ASSIGN_TASK,
                        "Task Assigned",
                        "Couldn't Assign Task", () -> {
                            assignTask(token, task);
                        });
            }
        });
    }

    public void cancelBid(String tid) {
        showProgressBar();
        Map<String, Object> map = new HashMap<>();
        map.put("t_id", tid);
        Contracts.call(map, "cancelBid").addOnCompleteListener(t -> notifyListener(t.isSuccessful(),
                SERVER_CANCEL_BID,
                "Bid Cancelled", "Couldn't Cancel Bid", () -> cancelBid(tid)));
    }

    public void taskDone(Task current) {
        showProgressBar();
        Map<String, Object> map = new HashMap<>();
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
