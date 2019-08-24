package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.badoualy.stepperindicator.StepperIndicator;
import com.example.mytasker.R;
import com.example.mytasker.fragments.PostTaskCat;
import com.example.mytasker.fragments.PostTaskDetail;
import com.example.mytasker.fragments.PostTaskExtra;
import com.example.mytasker.models.Task;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Contracts.PICK_IMAGE_REQUEST;
import static com.example.mytasker.util.Tools.getRetrofit;

public class PostTask extends BaseActivity {

    StepperIndicator indicator;
    FloatingActionButton fab;
    int currentpage = 0;
    ProgressDialog dlg;
    String title, desc;
    float reward;
    Fragment fragment;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Log.e("URI", String.valueOf(uri));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = findViewById(R.id.pickedImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setImageDrawable(getDrawable(R.drawable.post_done_anim));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        indicator = findViewById(R.id.stepperIndicator);
        Fragment fragment = new PostTaskCat();
        loadFragment(fragment);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        fab = findViewById(R.id.floatingActionButton);
        dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your task, Please Wait....");
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    public void loadNextFrag(View view) {
        if (currentpage < 3) {
            indicator.setCurrentStep(++currentpage);
        }
        if (currentpage == 1) {
            fragment = new PostTaskDetail();
//            fragment = new OrdersFrag();
            loadFragment(fragment);
            //Load frag
        } else if (currentpage == 2) {
            title = ((PostTaskDetail) fragment).title.getText().toString();
            desc = ((PostTaskDetail) fragment).description.getText().toString();
            fragment = new PostTaskExtra();
            loadFragment(fragment);
            AnimatedVectorDrawable icon = (AnimatedVectorDrawable) fab.getDrawable();
            icon.start();
            //Load frag and change fab drawable
        } else {
            //done
            reward = Float.parseFloat(((PostTaskExtra) fragment).reward.getText().toString());
            verifyNCall();
        }
    }

    private void verifyNCall() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postmytask(task.getResult().getToken());
                    } else {
                        // Handle error -> task.getException();
                        Toast.makeText(this, "Authentication Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void postmytask(String token) {
        dlg.show();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("tech");
        strings.add("null");
        ArrayList<Double> doubles = new ArrayList<>(2);
        doubles.add(25.0);
        doubles.add(25.0);
        Date date = new Date();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Task task = new Task(
                user.getUid(),
                user.getDisplayName(),
                user.getPhotoUrl().toString(),
                date.getTime(),
                desc,
                title,
                (int) reward,
                "mumbai",
                "tech",
                "videos",
                "time",
                doubles,
                strings,
                new ArrayList<>(),
                false
        );
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Task> call = jsonPlaceHolder.createTask(task);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {

                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Log.e("error",response.toString());
                    return;
                }
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("error",t.getMessage());
                dlg.dismiss();
            }
        });
    }
}