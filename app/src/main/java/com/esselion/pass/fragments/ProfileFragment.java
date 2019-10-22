package com.esselion.pass.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esselion.pass.MyFirebaseMessagingService;
import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.activities.AvatarChooser;
import com.esselion.pass.activities.BaseActivity;
import com.esselion.pass.activities.HistoryFeed;
import com.esselion.pass.activities.NotificationActivity;
import com.esselion.pass.activities.SettingActivity;
import com.esselion.pass.models.Profile;
import com.esselion.pass.models.Rating;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.ChipAdapter;
import com.esselion.pass.util.SharedPrefAdapter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Contracts.CODE_NOTIFICATION_ACTIVITY;
import static com.esselion.pass.util.Contracts.CODE_SETTINGS_ACTIVITY;
import static com.esselion.pass.util.Contracts.dpToPx;
import static com.esselion.pass.util.Tools.launchActivity;
import static com.esselion.pass.util.Tools.launchActivityForResult;
import static com.esselion.pass.util.Tools.showSnackBar;

public class ProfileFragment extends Fragment {

    private RatingBar taskerrating, posterrating;
    private TextView taskdone, taskposted, bucksearned;
    private ProgressBar ontime, behaviour, quality;
    private TextView ontimet, behaviourt, qualityt;
    private ChipGroup chipGroup;
    private ChipAdapter adapter;
    private ConstraintLayout constraintLayout, layout;
    private View divider;
    private Toolbar toolbar;

    private ActivityListener mListener;
    private DatabaseReference mDatabase;

    private TextView aboutText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ActivityListener) getActivity();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private Server getServer() {
        BaseActivity activity = (BaseActivity) getActivity();
        return activity != null ? activity.server : null;
    }

    private ImageView profileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ImageView imageView, lottieAnim;

    public ProfileFragment() {
    }

    @Override
    public void onStop() {
        super.onStop();
        MyFirebaseMessagingService.unregisterNotificationListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileImage();
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
            MyFirebaseMessagingService.registerNotificationListener(activity::invalidateOptionsMenu);
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

    private void forMe(View v, boolean mine) {
        TextView name = v.findViewById(R.id.poster_name);
        name.setText(mListener.getUName().toUpperCase());
        View editAvatar = v.findViewById(R.id.avatar_edit);
        View editAbout = v.findViewById(R.id.edit_about);
        toolbar.setTitle("PROFILE");
        v.findViewById(R.id.portfolio).setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), HistoryFeed.class);
            intent.putExtra("from", true);
            intent.putExtra("mine", mine);
            intent.putExtra("uid", mListener.getUId());
            launchActivity(getActivity(), intent);
        });
        BaseActivity activity = (BaseActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        if (mine) {
            setHasOptionsMenu(true);
            imageView.setOnClickListener(v1 -> {
                EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                int pad = dpToPx(24);
                int p = dpToPx(16);
                input.setPadding(pad, pad, pad, p);
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme).setTitle("ADD TAG").setView(input)
                        .setPositiveButton("ADD", (dialog, which) -> {
                            String skill = input.getText().toString();
                            if (!adapter.isSafe(skill)) {
                                showSnackBar(getActivity(), "Skill Already Exist or Empty");
                                return;
                            }
                            Server server = getServer();
                            if (server != null) server.addSkill(skill, mListener.getUId());
                        })
                        .show();
                input.requestFocus();
            });
            editAvatar.setOnClickListener(v13 -> launchActivity(getActivity(), AvatarChooser.class));
            editAbout.setOnClickListener(v12 -> {
                EditText input = new EditText(getContext());
//            input.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
                input.setGravity(Gravity.START);
                input.setHeight(dpToPx(200));
                input.setText(aboutText.getText());
                int pad = dpToPx(24);
                int p = dpToPx(16);
                input.setPadding(pad, pad, pad, p);
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme).setTitle("EDIT ABOUT").setView(input)
                        .setPositiveButton("SAVE", (dialog, which) -> {
                            Server server = getServer();
                            if (server != null)
                                server.saveAbout(input.getText().toString(), mListener.getUId());
                        })
                        .show();
                input.requestFocus();
            });
        } else {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            editAvatar.setVisibility(View.GONE);
            editAbout.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_profile, container, false);
        try {
            profileImage = v.findViewById(R.id.profile_image);
            mDatabase = getDatabase();
            taskerrating = v.findViewById(R.id.taskerrating);
            posterrating = v.findViewById(R.id.posterating);
            taskdone = v.findViewById(R.id.taskdone);
            taskposted = v.findViewById(R.id.taskposted);
            bucksearned = v.findViewById(R.id.bucksearned);
            ontime = v.findViewById(R.id.progressontime);
            ontimet = v.findViewById(R.id.textViewontime);
            behaviour = v.findViewById(R.id.progressbehaviour);
            behaviourt = v.findViewById(R.id.textViewbehaviour);
            quality = v.findViewById(R.id.progressquality);
            qualityt = v.findViewById(R.id.textViewquality);
            chipGroup = v.findViewById(R.id.skillschip);
            imageView = v.findViewById(R.id.addskill);
            constraintLayout = v.findViewById(R.id.layoutstats);
            lottieAnim = v.findViewById(R.id.lottie_anim);
            layout = v.findViewById(R.id.layoutrating);
            divider = v.findViewById(R.id.divider11);
            toolbar = v.findViewById(R.id.toolbar);
            aboutText = v.findViewById(R.id.about);
            progressBar = v.findViewById(R.id.progress_bar);
            forMe(v, mListener.getMine());
            adapter = new ChipAdapter(title -> {
                Server server = getServer();
                if (server != null) server.removeSkill(title, mListener.getUId());
            }, chipGroup, new ArrayList<>());
            swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(this::myapi);
            myapi();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;

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
                        profile.setByTasker(snapshot.child("Ratings/ByTasker").getValue(Rating.class));
                        profile.setByPoster(snapshot.child("Ratings/ByPoster").getValue(Rating.class));
                        chipGroup.removeAllViews();
                        for (DataSnapshot skill : snapshot.child("Skills").getChildren()) {
                            profile.addSkill(skill.getKey());
                        }
                        aboutText.setText(profile.getAbout());
                        setupstats(profile);
                        setupskills(profile.getSkills());
                        settupdetail(profile);
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

    private void settupdetail(Profile p) {
        taskdone.setText(p.getT_done() + "");
        bucksearned.setText("₹" + p.getBucks());
        taskposted.setText(p.getT_posted() + "");
    }

    public interface ActivityListener {
        boolean getMine();

        String getUId();

        String getUName();

        String getImageUrl();
    }

    private void setupskills(ArrayList<String> skills) {
        adapter = new ChipAdapter(title -> {
            Server server = getServer();
            if (server != null) server.removeSkill(title, mListener.getUId());
        }, chipGroup, skills);
    }

    private void setupstats(Profile p) {
        float rating = p.getPosterRating();
        float rating2 = p.getTaskerRating();
        if (rating + rating2 == 0) {
            constraintLayout.setVisibility(View.GONE);
            lottieAnim.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            lottieAnim.setVisibility(View.GONE);
            posterrating.setRating(rating);
            taskerrating.setRating(rating2);
            int r1 = p.getR1(), r2 = p.getR2(), r3 = p.getR3();
            if (r1 + r2 + r3 != 0) {
                divider.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
                ontime.setProgress(r1);
                ontimet.setText(r1 + "%");
                quality.setProgress(r2);
                qualityt.setText(r2 + "%");
                behaviour.setProgress(r3);
                behaviourt.setText(r3 + "%");
            }
        }
    }
}
