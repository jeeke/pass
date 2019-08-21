package com.example.mytasker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.chat.MessagesActivity;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.ChipAdapter;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.NetworkCache;
import com.example.mytasker.util.Tools;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.mytasker.util.Tools.launchActivity;

public class TaskDetailActivity extends BaseActivity {
    ChipAdapter tagAdapter,mustAdapter;
    public static int FROM = 0;

    private void initButton(int position){

        Button action = findViewById(R.id.bid);
        if(FROM==0) {
            current = NetworkCache.tasks.get(position);
            action.setOnClickListener(v -> launchActivity(this, BidConfirm.class));
            return;
        }else if(FROM==1){
            current = NetworkCache.prevPostModel.getPosted().get(position);
        }else current = NetworkCache.prevPostModel.getDone().get(position);

        int stage = current.getStage();

        FROM = ((FROM -1) * 3) + stage + 1;
        String title = Contracts.TASK_DETAIL_BUTTONS[FROM-1];


        switch (FROM ){
//          Poster stages
            case 1:
                action.setOnClickListener(v -> launchActivity(this,BidsListActivity.class));
                break;
            case 2:
                title+="Rakesh";
                action.setOnClickListener(v -> launchActivity(this,ProfileActivity.class));
                break;
            case 3:
                title +="Rakesh";
                action.setOnClickListener(v -> launchActivity(this,ProfileActivity.class));
                break;
//          Tasker Stages
            case 4:
                action.setOnClickListener(v -> new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("CANCEL BID")
                        .setMessage("Do you want to cancel bid on this task?")
                        .setPositiveButton("YES, CANCEL NOW", null)
                        .show());

                break;
            case 5:
                action.setOnClickListener(v -> new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("")
                        .setMessage("This task has been assigned to you.")
                        .setPositiveButton("Mark As Done", (dialog, which) -> launchActivity(this,FeedbackByTaskerActivity.class))
                        .setNegativeButton("Resign Task",null)
                        .show());

                break;
            case 6:
                break;
        }

        action.setBackgroundColor(getResources().getColor(Contracts.TASK_STAGE_COLORS[stage]));
        action.setText(title);
    }

    Task current;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail2);
        Tools.initMinToolbar(this,"Task Details",false);

        int position = getIntent().getIntExtra("position", 0);
        initButton(position);
        findViewById(R.id.chat).setOnClickListener(v -> {
            Toast.makeText(this, current.getPoster_id(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra("user_id",current.getPoster_id());
            startActivity(intent);
            finish();
        });

        ((TextView)findViewById(R.id.taskTitle)).setText(current.getTitle());
        ((TextView)findViewById(R.id.taskDesc)).setText(current.getJob_des());
        ((TextView)findViewById(R.id.rewardValue)).setText(current.getCost()+"");
//
        ChipGroup chipGroup = findViewById(R.id.tagGroup);
        ChipGroup mustGroup = findViewById(R.id.mustGroup);
        String[] array = {"tech","hello","others","three","nothing"};
        tagAdapter = new ChipAdapter(chipGroup,new ArrayList<>(Arrays.asList(array)));
        mustAdapter = new ChipAdapter(mustGroup,new ArrayList<>(Arrays.asList(array)));
    }
}
