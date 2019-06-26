package com.example.mytasker.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytasker.R;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.NetworkCache;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class TaskDetailActivity extends AppCompatActivity {

    private int position;
    ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
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

    private void initTags(String[] tagList){
        for(String tag:tagList){
            chipGroup.addView(createChip(this,tag));
        }
    }

    private Chip createChip(Context context, String title){
        Chip chip = new Chip(context);
        chip.setText(title);
//        chip.setCheckable(true);
//        chip.setClickable(false);
        return chip;
    }
}
