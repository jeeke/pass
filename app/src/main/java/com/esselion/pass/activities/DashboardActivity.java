package com.esselion.pass.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.fragments.ChatContainer;
import com.esselion.pass.fragments.HistoryContainer;
import com.esselion.pass.fragments.HomeContainer;
import com.esselion.pass.fragments.ProfileFragment;
import com.esselion.pass.fragments.QuestionFragment;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.Contracts;
import com.esselion.pass.util.RemoteConfigHelper;
import com.esselion.pass.util.SharedPrefAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.launchActivity;
import static com.esselion.pass.util.Tools.redirectToPlayStore;

public class DashboardActivity extends BaseActivity implements ProfileFragment.ActivityListener {

    private static final String FRAGMENT_TAG = "m-fragment";
    ImageView bhome, bqna, bfeed, bprofile;
    ImageView prevbselection;
    int btnSelected;
    int fragmentPosition = 0;
    View bottomAppBar;
    boolean fabActivated;
    Intent starterIntent;

    int centerX;
    int centerY;
    int startRadius;
    int endRadius;
    View fabExpanded;
    Fragment mFragment;
    FloatingActionButton fab;

    private void loadFragment(int entry, int exit) {
        if (fabActivated) circularReveal(fab);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (entry != -1 && exit != -1) transaction.setCustomAnimations(entry, exit);
        transaction.replace(R.id.fragment_container, mFragment, FRAGMENT_TAG);
        transaction.commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BaseActivity.REQUEST_TURN_ON_LOCATION && mFragment != null) {
            if (mFragment instanceof HomeContainer) {
                HomeContainer fragment = (HomeContainer) mFragment;
                fragment.verifyNCall();
            } else if (mFragment instanceof QuestionFragment) {
                QuestionFragment fragment = (QuestionFragment) mFragment;
                fragment.verifyNCall();
            }
        }
    }

    private void showUpdateDialog() {
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Please Update the App")
                .setMessage("A new version of PASS is available. Please update from PlayStore")
                .setPositiveButton("OK", (dialog, which) -> redirectToPlayStore(this))
                .setCancelable(false)
                .show();
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


    private void init() {
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        bhome = findViewById(R.id.home);
        bqna = findViewById(R.id.qna);
        bfeed = findViewById(R.id.feed);
        bprofile = findViewById(R.id.profile);

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (findViewById(R.id.fragment_container) != null) {
                mFragment = mFragment == null ? new HomeContainer() : mFragment;
                starterIntent = getIntent();
                init();
                initFab();
                loadFragment(R.anim.slide_from_right, R.anim.slide_to_left);
                prevbselection = prevbselection == null ? bhome : findViewById(btnSelected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String deviceToken = SharedPrefAdapter.getInstance().getToken();
            Cache.getDatabase().child("Users/" + Cache.getUser().getUid() + "/device_token").setValue(deviceToken);
            if (RemoteConfigHelper.getInstance().hasUpdate()) showUpdateDialog();
            if (savedInstanceState != null) {
                mFragment = getSupportFragmentManager()
                        .findFragmentByTag(FRAGMENT_TAG);
                btnSelected = savedInstanceState.getInt("selectedButton");
                fragmentPosition = savedInstanceState.getInt("fragmentPosition");
            }
            setContentView(R.layout.activity_dashboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        btnSelected = prevbselection.getId();
        savedInstanceState.putInt("fragmentPosition", fragmentPosition);
        savedInstanceState.putInt("selectedButton", btnSelected);
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
            prevbselection.setImageResource(toggleImage(prevbselection.getId(), false, false));
            current.setImageResource(toggleImage(current.getId(), true, true));
            prevbselection = current;
        }
    }


    private int toggleImage(int id, boolean selected, boolean first) {
        int imageId = 0;
        int prevFragmentPos = fragmentPosition;
        switch (id) {
            case R.id.home:
                mFragment = new HomeContainer();
                fragmentPosition = 0;
                imageId = selected ? R.drawable.ic_home_fill : R.drawable.ic_home;
                break;
            case R.id.feed:
                mFragment = new ChatContainer();
                fragmentPosition = 2;
                imageId = selected ? R.drawable.ic_chat : R.drawable.ic_chat;
                break;
            case R.id.qna:
                mFragment = new HistoryContainer();
                fragmentPosition = 1;
                imageId = selected ? R.drawable.ic_history : R.drawable.ic_history;
                break;
            case R.id.profile:
                mFragment = new ProfileFragment();
                fragmentPosition = 3;
                imageId = selected ? R.drawable.ic_profile_fill : R.drawable.ic_profile;
        }
        //Load fragment only once
        if (first) {
            if (prevFragmentPos < fragmentPosition)
                loadFragment(R.anim.slide_from_right, R.anim.slide_to_left);
            else loadFragment(R.anim.slide_from_left, R.anim.slide_to_right);
        }
        return imageId;
    }

    @Override
    public boolean getMine() {
        return true;
    }

    @Override
    public String getUId() {
        return getUser().getUid();
    }

    @Override
    public String getUName() {
        return getUser().getDisplayName();
    }

    @Override
    public String getImageUrl() {
        Uri uri = getUser().getPhotoUrl();
        if (uri == null) return Contracts.avatars[2];
        return getUser().getPhotoUrl().toString();
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_UPDATE_IMAGE && mFragment instanceof ProfileFragment) {
            try {
                ProfileFragment fragment = (ProfileFragment) mFragment;
                fragment.updateProfileImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
