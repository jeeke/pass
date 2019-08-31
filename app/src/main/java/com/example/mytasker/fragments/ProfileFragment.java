package com.example.mytasker.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mytasker.R;
import com.example.mytasker.activities.HistoryFeed;
import com.example.mytasker.activities.NotificationActivity;
import com.example.mytasker.activities.SettingActivity;
import com.example.mytasker.models.Profile;
import com.example.mytasker.models.Rating;
import com.example.mytasker.util.ChipAdapter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.mytasker.util.Contracts.CODE_NOTIFICATION_ACTIVITY;
import static com.example.mytasker.util.Contracts.CODE_SETTINGS_ACTIVITY;
import static com.example.mytasker.util.Contracts.dpToPx;

public class ProfileFragment extends Fragment {

    private RatingBar taskerrating, posterrating;
    private TextView taskdone, taskposted, bucksearned;
    private ProgressBar ontime, behaviour, quality;
    private TextView ontimet, behaviourt, qualityt;
    private ChipGroup chipGroup;
    private ChipAdapter adapter;
    private ProgressDialog dlg;
    private ConstraintLayout constraintLayout, layout;
    private View divider;
    private Toolbar toolbar;


    private boolean mine;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    public ProfileFragment(boolean mine) {
        this.mine = mine;
    }

    TextView aboutText;

    private void forMe(View v) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("PROFILE");
        TextView name = v.findViewById(R.id.poster_name);
        if (mUser != null) {
            name.setText(mUser.getDisplayName().toUpperCase());
//            Toast.makeText(getContext(), user.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        imageView.setOnClickListener(v1 -> {
            EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            int pad = dpToPx(24);
            int p = dpToPx(16);
            input.setPadding(pad, pad, pad, p);
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme).setTitle("ADD TAG").setView(input)
                    .setPositiveButton("ADD", (dialog, which) -> {
                        addSkill(input.getText().toString());
                    })
                    .show();
            input.requestFocus();
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ImageView imageView, sugga;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_profile, container, false);
        v.findViewById(R.id.portfolio).setOnClickListener(v1 -> {
            Intent intent = new Intent(getContext(), HistoryFeed.class);
            intent.putExtra("from", true);
            startActivity(intent);
        });
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ImageView profileImage = v.findViewById(R.id.profile_image);
        Glide.with(v.getContext()).load(mUser.getPhotoUrl().toString()).into(profileImage);
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
        dlg = new ProgressDialog(getContext());
        dlg.setTitle("Getting Profile Info..");
        constraintLayout = v.findViewById(R.id.layoutstats);
        sugga = v.findViewById(R.id.sugga);
        layout = v.findViewById(R.id.layoutrating);
        divider = v.findViewById(R.id.divider11);
        toolbar = v.findViewById(R.id.toolbar);
        aboutText = v.findViewById(R.id.about);
        v.findViewById(R.id.edit_about).setOnClickListener(v12 -> {
            EditText input = new EditText(getContext());
//            input.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            input.setGravity(Gravity.START);
            input.setHeight(dpToPx(200));
            int pad = dpToPx(24);
            int p = dpToPx(16);
            input.setPadding(pad, pad, pad, p);
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme).setTitle("EDIT ABOUT").setView(input)
                    .setPositiveButton("SAVE", (dialog, which) -> {
                        saveAbout(input.getText().toString());
                    })
                    .show();
            input.requestFocus();
        });
        if (mine) forMe(v);
        myapi();
        adapter = new ChipAdapter(this::removeSkill, chipGroup, new ArrayList<>());
        return v;
    }

    private void saveAbout(String about) {
        dlg.setTitle("Saving...");
        dlg.show();
        mDatabase
                .child("/Profiles/" + mUser.getUid() + "/about").setValue(about)
                .addOnCompleteListener(task -> {
                    dlg.dismiss();
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(), "Could not be updated", Toast.LENGTH_SHORT).show();
                    } else aboutText.setText(about);
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.setting) {
            intent = new Intent(getContext(), SettingActivity.class);
            getActivity().startActivityForResult(intent, CODE_SETTINGS_ACTIVITY);
//            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        } else {
            intent = new Intent(getContext(), NotificationActivity.class);
            getActivity().startActivityForResult(intent, CODE_NOTIFICATION_ACTIVITY);
//            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_menu, menu);
    }

    private void addSkill(String skill) {
        if (!adapter.isSafe(skill)) {
            Toast.makeText(getContext(), "Skill Already Exist or Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        dlg.setTitle("Adding Skill...");
        dlg.show();
        mDatabase
                .child("/Profiles/" + mUser.getUid() + "/Skills/" + skill).setValue(true)
                .addOnCompleteListener(task -> {
                    dlg.dismiss();
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(), "Skills could not be updated", Toast.LENGTH_SHORT).show();
                    } else adapter.addChild(skill);
                });
    }

    private void removeSkill(String skill) {
        dlg.setTitle("Removing Skill...");
        dlg.show();
        mDatabase
                .child("/Profiles/" + mUser.getUid() + "/Skills/" + skill).removeValue()
                .addOnCompleteListener(task -> {
                    dlg.dismiss();
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(), "Skill could not be removed", Toast.LENGTH_SHORT).show();
                        adapter.addChild(skill);
                    }
                });
    }

    private void myapi() {
        dlg.setTitle("Getting Profile Info..");
        dlg.show();
        DatabaseReference r = mDatabase.child("Profiles").child(mUser.getUid());
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                r.removeEventListener(this);
                dlg.dismiss();
                if (mine) imageView.setVisibility(View.VISIBLE);
                Profile profile = snapshot.getValue(Profile.class);
//                Log.e("\nP\nr\no\nf\ni\nl\ne\n",snapshot.toString());
                if (profile != null) {
                    profile.setByTasker(snapshot.child("Ratings/ByTasker").getValue(Rating.class));
                    profile.setByPoster(snapshot.child("Ratings/ByPoster").getValue(Rating.class));
                    for (DataSnapshot skill : snapshot.child("Skills").getChildren()) {
                        profile.addSkill(skill.getKey());
                    }
                    aboutText.setText(profile.getAbout());
                    setupstats(profile);
                    setupskills(profile.getSkills());
                    settupdetail(profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                r.removeEventListener(this);
                dlg.dismiss();
                Toast.makeText(getContext(), "Something went wrong, Try later ", Toast.LENGTH_SHORT).show();
                Log.e("error", databaseError.toString());
            }
        });
    }

    private void settupdetail(Profile p) {
        taskdone.setText(p.getT_done() + "");
        bucksearned.setText("$" + p.getBucks());
        taskposted.setText(p.getT_posted() + "");
    }

    private void setupskills(ArrayList<String> skills) {
        adapter = new ChipAdapter(this::removeSkill, chipGroup, skills);
    }

    private void setupstats(Profile p) {
        float rating = p.getPosterRating();
        float rating2 = p.getTaskerRating();
        if (rating + rating2 == 0) {
            constraintLayout.setVisibility(View.GONE);
            sugga.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
            sugga.setVisibility(View.GONE);
            posterrating.setRating(rating);
            taskerrating.setRating(rating2);
            int r1 = p.getR1(), r2 = p.getR2(), r3 = p.getR3();
            ontime.setProgress(r1);
            ontimet.setText(r1 + "%");
            quality.setProgress(r2);
            qualityt.setText(r2 + "%");
            behaviour.setProgress(r3);
            behaviourt.setText(r3 + "%");
        }
    }

}
