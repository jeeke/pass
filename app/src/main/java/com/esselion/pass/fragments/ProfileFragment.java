package com.esselion.pass.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esselion.pass.FBMsgService;
import com.esselion.pass.R;
import com.esselion.pass.activities.NotificationActivity;
import com.esselion.pass.activities.SettingActivity;
import com.esselion.pass.adapters.ProfileTabAdapter;
import com.esselion.pass.models.Profile;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.SharedPrefAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Contracts.CODE_NOTIFICATION_ACTIVITY;
import static com.esselion.pass.util.Contracts.CODE_SETTINGS_ACTIVITY;
import static com.esselion.pass.util.Tools.launchActivityForResult;

public class ProfileFragment extends Fragment {

    private Toolbar toolbar;

    private ActivityListener mListener;
    private DatabaseReference mDatabase;

    private TextView aboutText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ActivityListener) getActivity();
        mListener.getMine();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private ImageView profileImage;

    @Override
    public void onStop() {
        super.onStop();
        FBMsgService.unregisterNotificationListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileImage();
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
            FBMsgService.registerNotificationListener(activity::invalidateOptionsMenu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (SharedPrefAdapter.getInstance().hasUnseenNotification()) {
            menu.getItem(0).setIcon(R.drawable.ic_bell_avd);
            Drawable menuItem = menu.getItem(0).getIcon();
            Animatable animatable = (Animatable) menuItem;
            animatable.start();
            AnimatedVectorDrawableCompat.registerAnimationCallback
                    (menuItem, new Animatable2Compat.AnimationCallback() {
                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            super.onAnimationEnd(drawable);
                            animatable.start();
                        }
                    });
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Activity activity = getActivity();
        if (activity != null && item.getItemId() == R.id.notification) {
            launchActivityForResult(activity, new Intent(getContext(), NotificationActivity.class), CODE_NOTIFICATION_ACTIVITY);
            SharedPrefAdapter.getInstance().setHasNotification();
            activity.invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.setting) {
            launchActivityForResult(getActivity(), new Intent(getContext(), SettingActivity.class), CODE_SETTINGS_ACTIVITY);
            return true;
        }
        return false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_profile_new, container, false);
        TabLayout tabs = v.findViewById(R.id.tab_layout);
        ViewPager viewPager = v.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ProfileTabAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        tabs.setupWithViewPager(v.findViewById(R.id.view_pager));
        profileImage = v.findViewById(R.id.profile_image);
        mDatabase = getDatabase();
        toolbar = v.findViewById(R.id.toolbar);
        aboutText = v.findViewById(R.id.about);
        progressBar = v.findViewById(R.id.progress_bar);
//        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(this::myapi);
        myapi();
        setHasOptionsMenu(mListener.getMine());
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity a = (AppCompatActivity) getActivity();
        a.setSupportActionBar(toolbar);
    }

    public void updateProfileImage() {
        if (mListener.getMine()) {
            Cache.mUser = null;
            Cache.getUser().getPhotoUrl();
            Glide.with(Objects.requireNonNull(getContext())).load(mListener.getImageUrl())
                    .apply(RequestOptions.circleCropTransform()).into(profileImage);
        } else {
            Glide.with(Objects.requireNonNull(getContext())).load(mListener.getImageUrl())
                    .apply(RequestOptions.circleCropTransform()).into(profileImage);
        }
    }

    private void myapi() {
        updateProfileImage();
        DatabaseReference r = mDatabase.child("Profiles").child(mListener.getUId());
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Profile profile = snapshot.getValue(Profile.class);
                    if (profile != null) {
                        aboutText.setText(profile.getAbout());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                r.removeEventListener(this);
                swipeRefreshLayout.setRefreshing(false);
                Log.e("error", databaseError.toString());
            }
        });
    }


    public interface ActivityListener {
        boolean getMine();

        String getUId();

        String getUName();

        String getImageUrl();
    }
}
