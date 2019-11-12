package com.esselion.pass.activities;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.badoualy.stepperindicator.StepperIndicator;
import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.fragments.PostTaskCat;
import com.esselion.pass.fragments.PostTaskDetail;
import com.esselion.pass.fragments.PostTaskExtra;
import com.esselion.pass.models.Location;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Cache;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.esselion.pass.util.Cache.getToken;
import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.launchActivity;
import static com.esselion.pass.util.Tools.showSnackBar;


public class PostTask extends BaseActivity implements Cache.LocationListener {

    StepperIndicator indicator;
    FloatingActionButton fab;
    ArrayList<String> tags, mustHaves;
    int currentPage = 1;
    String title, desc, category, reward, deadline;
    Fragment fragment;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            fab.setImageDrawable(getDrawable(R.drawable.post_done_anim));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            } else {
                Cache.getLocation(this);
            }
        }
    }

    @Override
    public void onLocationFetched(Location location) {
        if (prevCallResolved && server != null) {
            getToken(token -> {
                Date date = new Date();
                FirebaseUser user = getUser();
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
                        location.getLatitude(),
                        location.getLongitude(),
                        tags,
                        mustHaves,
                        false);
                server.postTask(token, task);
            });
            prevCallResolved = false;
        }
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
//        super.onServerCallSuccess(methodId, title);
        Toast.makeText(this, "Task Posted", Toast.LENGTH_SHORT).show();
        if (methodId == Server.SERVER_POST_TASK) {
            launchActivity(this, HistoryTask.class);
            finish();
        }
    }
}