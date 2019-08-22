package com.example.mytasker.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.mytasker.R;
import com.example.mytasker.activities.AddSkill;
import com.example.mytasker.activities.NotificationActivity;
import com.example.mytasker.activities.SettingActivity;
import com.example.mytasker.models.Profile;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.ChipAdapter;
import com.example.mytasker.util.Contracts;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Contracts.ADD_SKILL_REQUEST;
import static com.example.mytasker.util.Contracts.CODE_NOTIFICATION_ACTIVITY;
import static com.example.mytasker.util.Contracts.CODE_SETTINGS_ACTIVITY;
import static com.example.mytasker.util.Tools.getRetrofit;

public class ProfileFragment extends Fragment {

    private RatingBar taskerrating, posterrating;
    private TextView taskdone, taskposted, bucksearned;
    private ProgressBar ontime, budget, behaviour, quality;
    private TextView ontimet, behaviourt, qualityt, budgett;
    private ChipGroup chipGroup;
    private ImageView imageView, sugga;
    private ProgressDialog dlg;
    private ConstraintLayout constraintLayout,layout;
    private View divider;
    private Toolbar toolbar;


    boolean mine;
    public ProfileFragment(boolean mine) {
        this.mine = mine;
    }

    private void forMe(View v){
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("PROFILE");
        TextView name = v.findViewById(R.id.name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name.setText(user.getDisplayName().toUpperCase());
//            Toast.makeText(getContext(), user.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        imageView.setOnClickListener(v1 -> {
            Intent intent = new Intent(getContext(), AddSkill.class);
            getActivity().startActivityForResult(intent,ADD_SKILL_REQUEST);
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_profile, container, false);
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
        budget = v.findViewById(R.id.progressbudget);
        budgett = v.findViewById(R.id.textViewbudget);
        chipGroup = v.findViewById(R.id.skillschip);
        imageView = v.findViewById(R.id.addskill);
        dlg = new ProgressDialog(getContext());
        dlg.setTitle("Getting Profile Info..");
        constraintLayout = v.findViewById(R.id.layoutstats);
        sugga = v.findViewById(R.id.sugga);
        layout = v.findViewById(R.id.layoutrating);
        divider  = v.findViewById(R.id.divider11);
        constraintLayout.setVisibility(View.GONE);
        sugga.setVisibility(View.VISIBLE);
        divider.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        if(mine) forMe(v);
        verifyNCall();
        return v;
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

    public void addskill(String token) {
        Log.e("AddedSkill","added_skill");
        if (!adapter.isSafe(Contracts.added_skill)){
            Toast.makeText(getContext(), "Skill Already Exist", Toast.LENGTH_SHORT).show();
            return;
        }
        dlg.setTitle("Adding Skill...");
        dlg.show();

        Retrofit retrofit = getRetrofit(token);

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Profile> call = jsonPlaceHolder.addskill(Contracts.added_skill);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong, Try later ", Toast.LENGTH_SHORT).show();
                    Log.e("error", response.toString());
                    return;
                }
                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                adapter.addChild(Contracts.added_skill);
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong, Try later ", Toast.LENGTH_SHORT).show();
                Log.e("error", t.getMessage());
                dlg.dismiss();
            }
        });


    }

    public void verifyNCall2() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addskill(task.getResult().getToken());
                    } else {
                        // Handle error -> task.getException();
                        Toast.makeText(getContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyNCall() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myapi(task.getResult().getToken());
                    } else {
                        // Handle error -> task.getException();
                        Toast.makeText(getContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void myapi(String token) {
        dlg.setTitle("Getting Profile Info..");
        dlg.show();
        Retrofit retrofit = getRetrofit(token);

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Profile> call = jsonPlaceHolder.getProfile();
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong, Try later ", Toast.LENGTH_SHORT).show();
                    Log.e("error", response.toString());
                    return;
                }
                if(mine) imageView.setVisibility(View.VISIBLE);
                taskerrating.setRating(Float.parseFloat(response.body().getTasker_rating()));
                posterrating.setRating(Float.parseFloat(response.body().getPoster_rating()));
                setupstats(response.body());
                setupskills(response.body().getSkills());
                setupmedals(response.body().getMedals());
                settupdetail(response.body());
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong, Try later ", Toast.LENGTH_SHORT).show();
                Log.e("error", t.getMessage());
                dlg.dismiss();
            }
        });
    }

    private void setupmedals(ArrayList<String> medals) {

    }

    private void settupdetail(Profile p) {
        taskdone.setText(p.getT_done());
        bucksearned.setText("$"+p.getBucks());
        taskposted.setText(p.getT_posted());

    }

    ChipAdapter adapter;

    private void setupskills(ArrayList<String> skills) {
        adapter = new ChipAdapter(chipGroup, skills);
    }

    private void setupstats(Profile p) {
        if (Integer.parseInt(p.getOn_time()) + Integer.parseInt(p.getOn_budget()) + Integer.parseInt(p.getQuality()) + Integer.parseInt(p.getBehaviour()) == 0) {
            constraintLayout.setVisibility(View.GONE);
            sugga.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        else {
            divider.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
            sugga.setVisibility(View.GONE);
            ontime.setProgress(Integer.parseInt(p.getOn_time()));
            ontimet.setText(p.getOn_time()+"%");
            budget.setProgress(Integer.parseInt(p.getOn_budget()));
            budgett.setText(p.getOn_budget()+"%");
            quality.setProgress(Integer.parseInt(p.getQuality()));
            qualityt.setText(p.getQuality()+"%");
            behaviour.setProgress(Integer.parseInt(p.getBehaviour()));
            behaviourt.setText(p.getBehaviour()+"%");
        }
    }
}
