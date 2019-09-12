package com.example.mytasker.activities;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.badoualy.stepperindicator.StepperIndicator;
import com.example.mytasker.R;
import com.example.mytasker.fragments.PostTaskCat;
import com.example.mytasker.fragments.PostTaskDetail;
import com.example.mytasker.fragments.PostTaskExtra;
import com.example.mytasker.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.example.mytasker.util.Cache.getToken;
import static com.example.mytasker.util.Cache.getUser;
import static com.example.mytasker.util.Tools.showSnackBar;


public class PostTask extends LocationActivity implements LocationActivity.LocationListener {

    StepperIndicator indicator;
    FloatingActionButton fab;
    ArrayList<String> tags, mustHaves;
    int currentPage = 1;
    String title, desc, category, reward, deadline;
    Fragment fragment;

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
        fragment = new PostTaskCat();
        loadFragment(fragment);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        fab = findViewById(R.id.floatingActionButton);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    public void loadNextFrag(View view) {
        if (currentPage == 1) {
            PostTaskCat frag = (PostTaskCat) fragment;
            category = frag.getTaskCat();
            tags = frag.getTaskTags();
            mustHaves = frag.getTaskMustHaves();
            if (category.equals("")) {
                showSnackBar(this, "Please select the task type");
            } else {
                fragment = new PostTaskDetail();
                loadFragment(fragment);
                indicator.setCurrentStep(currentPage++);
            }
        } else if (currentPage == 2) {
            PostTaskDetail frag = (PostTaskDetail) fragment;
            title = frag.getTitle();
            desc = frag.getDesc();
            if (title.equals("")) {
                showSnackBar(this, "Please enter task title");
            } else {
                fragment = new PostTaskExtra();
                loadFragment(fragment);
                AnimatedVectorDrawable icon = (AnimatedVectorDrawable) fab.getDrawable();
                icon.start();
                indicator.setCurrentStep(currentPage++);
            }
        } else {
            PostTaskExtra frag = (PostTaskExtra) fragment;
            reward = frag.getReward();
            deadline = frag.getDate();
            if (reward.equals("")) {
                showSnackBar(this, "Please Enter Reward Value");
            } else
                setLocationListener(this);
            super.getLocation();
        }
    }

    @Override
    public void onLocationFetched(boolean success, double lon, double lat) {
        if (success && prevCallResolved && server != null) {
            getToken(token -> {
                Date date = new Date();
                FirebaseUser user = getUser(PostTask.this);
                Task task = new Task(
                        user.getUid(),
                        user.getDisplayName(),
                        user.getPhotoUrl().toString(),
                        date.getTime(),
                        desc,
                        title,
                        Float.parseFloat(reward),
                        category,
                        deadline,
                        lat,
                        lon,
                        tags,
                        mustHaves,
                        false);
                server.postTask(token, task);
            }, this);
            prevCallResolved = false;
        }
    }
}