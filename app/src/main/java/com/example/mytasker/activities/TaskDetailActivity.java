package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.chat.MessagesActivity;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.ChipAdapter;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.mytasker.util.Tools.launchActivity;

public class TaskDetailActivity extends BaseActivity {
    ChipAdapter tagAdapter,mustAdapter;
    public int FROM = 0;

    Task current;

    private void initButton() {
        Button action = findViewById(R.id.bid);
        if(FROM==0) {
            action.setOnClickListener(v -> {
                Intent intent = new Intent(this, BidConfirm.class);
                intent.putExtra("task", current);
                startActivity(intent);
                finish();
            });
            return;
        }
        int stage = current.getStage();
        FROM = ((FROM -1) * 3) + stage + 1;
        String title = Contracts.TASK_DETAIL_BUTTONS[FROM-1];
        switch (FROM ){
//          Poster stages
            case 1:
                action.setOnClickListener(v -> {
                    Intent intent = new Intent(this, BidsListActivity.class);
                    intent.putExtra("task", current);
                    startActivity(intent);
                });
                break;
            case 2:
                title += current.getPoster_name().split(" (?!.* )")[0];
                action.setOnClickListener(v -> launchActivity(this,ProfileActivity.class));
                break;
            case 3:
                title += current.getPoster_name().split(" (?!.* )")[0];
                action.setOnClickListener(v -> launchActivity(this,ProfileActivity.class));
                break;
//          Tasker Stages
            case 4:
                action.setOnClickListener(v -> new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("CANCEL BID")
                        .setMessage("Do you want to cancel bid on this task?")
                        .setPositiveButton("YES, CANCEL NOW", (dialog, which) -> cancelBid())
                        .show());

                break;
            case 5:
                action.setOnClickListener(v -> new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("")
                        .setMessage("This task has been assigned to you.")
                        .setPositiveButton("Mark As Done", (dialog, which) -> {
                            markDone();
                        })
//                (dialog, which) -> launchActivity(this,FeedbackByTaskerActivity.class))
//                        .setNegativeButton("Resign Task",null)
                        .show());

                break;
            case 6:
                break;
        }
        action.setBackgroundColor(getResources().getColor(Contracts.TASK_STAGE_COLORS[stage]));
        action.setText(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current = (Task) getIntent().getSerializableExtra("task");
        FROM = getIntent().getIntExtra("from", 0);
        if (current == null) finish();
        setContentView(R.layout.activity_task_detail2);
        initButton();
        Tools.initMinToolbar(this,"Task Details",false);
        findViewById(R.id.chat).setOnClickListener(v -> {
            Toast.makeText(this, current.getPoster_id(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra("id",current.getPoster_id());
            intent.putExtra("name",current.getPoster_name());
            intent.putExtra("avatar",current.getPoster_avatar());
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

    private void markDone() {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Marking task..");
        dlg.show();
        Map map = new HashMap();
        map.put("p_id", current.getPoster_id());
        map.put("task_id", current.getId());
        map.put("task_title", current.getTitle());
        Contracts.call(map, "taskDone").addOnCompleteListener(t -> {
            dlg.dismiss();
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.e("tag", ffe + "\n" + code + "\n" + details);
                }
                Toast.makeText(this, "Could not be marked", Toast.LENGTH_SHORT).show();
                Log.e("tag", e + "");
                return;
            }
            finish();
            Log.e("tag", t.getResult() + "");
            Toast.makeText(this, "Task Done Successfully", Toast.LENGTH_SHORT).show();
            launchActivity(this, FeedbackByTaskerActivity.class);
        });
    }

    private void cancelBid() {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Cancelling your Bid..");
        dlg.show();
        Map map = new HashMap();
        map.put("t_id", current.getId());
        Contracts.call(map, "cancelBid").addOnCompleteListener(t -> {
            dlg.dismiss();
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.e("tag", ffe + "\n" + code + "\n" + details);
                }
                Toast.makeText(this, "Bid could not be cancelled", Toast.LENGTH_SHORT).show();
                Log.e("tag", e + "");
                return;
            }
            finish();
            Log.e("tag", t.getResult() + "");
            Toast.makeText(this, "Bid Cancelled", Toast.LENGTH_SHORT).show();
        });
    }
}
