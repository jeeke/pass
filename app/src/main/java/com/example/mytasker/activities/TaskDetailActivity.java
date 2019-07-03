package com.example.mytasker.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mytasker.R;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.NetworkCache;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class TaskDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private int position;
    ChipGroup chipGroup;
    Toolbar toolbar;
    //    int colorTo;
//    int colorFrom;
    boolean toolbarColor;

    private void initTags(String[] tagList) {
        for (String tag : tagList) {
            chipGroup.addView(createChip(this, tag));
        }
    }

    private Chip createChip(Context context, String title) {
        Chip chip = new Chip(context);
        chip.setText(title);
//        chip.setCheckable(true);
//        chip.setClickable(false);
        return chip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
//        Tools.initMinToolbar(this,"Task Details",false);


        toolbar = findViewById(R.id.toolbar);
//        initBackground();
        toolbarColor = false;
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);


        position = getIntent().getIntExtra("position",0);
        Task current = NetworkCache.tasks.get(position);
        ((TextView)findViewById(R.id.taskTitle)).setText(current.getTitle());
        ((TextView)findViewById(R.id.taskDesc)).setText(current.getJob_des());
        ((TextView)findViewById(R.id.rewardValue)).setText(current.getCost()+"");
        ((TextView)findViewById(R.id.numBids)).setText(current.getCost()+"");

        chipGroup = findViewById(R.id.tagGroup);
//        initTags(current.getTags());
        String[] array = {"tech","hello","others","three","nothing"};
        initTags(array);
    }
//
//    ObjectAnimator forward,reverse;
//    private void initBackground(){
//        colorTo = getResources().getColor(R.color.creme);
//        colorFrom = 0;
//        forward = ObjectAnimator.ofObject(toolbar,"backgroundColor",new ArgbEvaluator(),colorFrom,colorTo).setDuration(300);
//        reverse = ObjectAnimator.ofObject(toolbar,"backgroundColor",new ArgbEvaluator(),colorTo,colorFrom).setDuration(70);
//    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset < -32 && !toolbarColor) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.creme));
//            forward.start();
            toolbarColor = !toolbarColor;
        } else if (verticalOffset > -32 && toolbarColor) {
            toolbar.setBackgroundColor(0);
//            forward.reverse();
            toolbarColor = !toolbarColor;
        }
    }
}
