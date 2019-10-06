package com.example.mytasker.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mytasker.BuildConfig;
import com.example.mytasker.R;
import com.example.mytasker.fragments.FeedFragment;
import com.example.mytasker.fragments.HomeFragment;
import com.example.mytasker.fragments.ProfileFragment;
import com.example.mytasker.fragments.QuestionFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ServerValue;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

import static com.example.mytasker.util.Cache.getUser;
import static com.example.mytasker.util.Contracts.CODE_SETTINGS_ACTIVITY;
import static com.example.mytasker.util.Tools.launchActivity;
import static com.example.mytasker.util.Tools.setOnline;
import static com.example.mytasker.util.Tools.setToken;

public class DashboardActivity extends LocationActivity implements ProfileFragment.ActivityListener {

    public static final String VERSION_CODE_KEY = "latest_app_version";
    ImageView bhome, bqna, bfeed, bprofile;
    ImageView prevbselection;
    View bottomAppBar;
    boolean fabActivated;
    Intent starterIntent;

    int centerX;
    int centerY;
    int startRadius;
    int endRadius;
    View fabExpanded;
    //    Fragment[] fragments = new Fragment[]{null, null, null, null};
    FloatingActionButton fab;
    private static final String TAG = "DashboardActivity";
    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private HashMap<String, Object> firebaseDefaultMap;

    private void loadFragment(Fragment fragment) {
        if (fabActivated) circularReveal(fab);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void checkForUpdate() {
        int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
        if (latestAppVersion > getCurrentVersionCode()) {
            new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("Please Update the App")
                    .setMessage("A new version of PASS is available. Please update it")
                    .setPositiveButton("OK", null)
                    .setCancelable(false)
                    .show();
        }
    }

    private long getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setUpdate() {
        firebaseDefaultMap = new HashMap<>();
        firebaseDefaultMap.put(VERSION_CODE_KEY, getCurrentVersionCode());
        mFirebaseRemoteConfig.setDefaults(firebaseDefaultMap);
        mFirebaseRemoteConfig.setConfigSettings(
                new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build());
        //Fetching the values here
        mFirebaseRemoteConfig.fetch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFirebaseRemoteConfig.activateFetched();
                Log.d(TAG, "Fetched value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));
                //calling function to check if new version is available or not
                checkForUpdate();
            } else {
                Toast.makeText(DashboardActivity.this, "Something went wrong please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(TAG, "Default value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));
    }
    //TODO check for play services android for notification handling

    @Override
    public void onBackPressed() {
        if (fabActivated) {
            circularReveal(fab);
            return;
        }
        super.onBackPressed();
    }

    private void initFab() {
        fabActivated = false;
        fab = findViewById(R.id.fab);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        centerX = size.x / 2;
        centerY = size.y;
        startRadius = 0;
        endRadius = size.y;
        fabExpanded = findViewById(R.id.fab_expanded);
    }


    public void circularReveal(View v) {
        float angle;
        if (fabActivated) {
            angle = 0f;
            Animator anim = ViewAnimationUtils.createCircularReveal(fabExpanded, centerX, centerY, endRadius, startRadius);
            anim.setDuration(100);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    fabExpanded.setVisibility(View.GONE);
                }
            });
            anim.start();
            fabActivated = false;
        } else {
            angle = 45f;
            fabExpanded.setVisibility(View.VISIBLE);
            Animator anim = ViewAnimationUtils.createCircularReveal(fabExpanded, centerX, centerY, startRadius, endRadius);
            anim.setDuration(300);
            anim.start();
            fabActivated = true;
        }

        final OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(v).
                rotation(angle).
                withLayer().
                setDuration(300).
                setInterpolator(interpolator).
                start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_SETTINGS_ACTIVITY) {
//            if (resultCode == RESULT_OK) {
//            }
            finish();
//            starterIntent.putExtra("theme", getThemeToLaunch());
            startActivity(starterIntent);
        }
    }

    private void init() {
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        bhome = findViewById(R.id.home);
        bqna = findViewById(R.id.qna);
        bfeed = findViewById(R.id.feed);
        bprofile = findViewById(R.id.profile);

    }


    @Override
    protected void onStop() {
        super.onStop();
        setOnline(this, ServerValue.TIMESTAMP);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (findViewById(R.id.fragment_container) != null) {
            starterIntent = getIntent();
            setToken(this);
            init();
            initFab();
            loadFragment(new HomeFragment());
            prevbselection = bhome;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpdate();
        setContentView(R.layout.activity_dashboard);
    }


    public void postTask(View v) {
        circularReveal(fab);
        launchActivity(this, PostTask.class);
    }

    public void postQues(View v) {
        circularReveal(fab);
        launchActivity(this, PostQuestion.class);

    }

    public void postFeed(View v) {
        circularReveal(fab);
        launchActivity(this, PostFeed.class);

    }

    public void bselected(View v) {
        ImageView current = (ImageView) v;
        if (v.getId() != prevbselection.getId()) {
            prevbselection.setImageResource(toggleImage(prevbselection.getId(), false));
            current.setImageResource(toggleImage(current.getId(), true));
            prevbselection = current;
        }
    }


    private int toggleImage(int id, boolean selected) {
        switch (id) {
            case R.id.home:
                loadFragment(new HomeFragment());
                return selected ? R.mipmap.home_fill : R.mipmap.home;
            case R.id.feed:
                loadFragment(new FeedFragment());
                return selected ? R.mipmap.scroll_fill : R.mipmap.scroll;
            case R.id.qna:
                loadFragment(new QuestionFragment());
                return selected ? R.mipmap.qna_fill : R.mipmap.qna;
            case R.id.profile:
                loadFragment(new ProfileFragment());
                return selected ? R.mipmap.profile_fill : R.mipmap.profile;
        }
        return 0;
    }

    @Override
    public boolean getMine() {
        return true;
    }

    @Override
    public String getUId() {
        return getUser(this).getUid();
    }

    @Override
    public String getUName() {
        return getUser(this).getDisplayName();
    }

    @Override
    public String getImageUrl() {
        return getUser(this).getPhotoUrl().toString();
    }
}
