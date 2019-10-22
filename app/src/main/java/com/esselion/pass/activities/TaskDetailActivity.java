package com.esselion.pass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.esselion.pass.R;
import com.esselion.pass.chat.MessagesActivity;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.ChipAdapter;
import com.esselion.pass.util.Contracts;
import com.esselion.pass.util.Tools;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static com.esselion.pass.Server.SERVER_TASK_DONE;
import static com.esselion.pass.util.Cache.getToken;
import static com.esselion.pass.util.Tools.finishNLaunchActivity;
import static com.esselion.pass.util.Tools.launchActivity;

public class TaskDetailActivity extends BaseActivity {
    ChipAdapter tagAdapter, mustAdapter;
    public int FROM = 0;

    Task current;

    private void initButton() {
        Button action = findViewById(R.id.bid);
        if (FROM == 0) {
            action.setOnClickListener(v -> {
                Intent intent = new Intent(this, BidConfirm.class);
                intent.putExtra("task", current);
                finishNLaunchActivity(this, intent);
            });
            return;
        }
        int stage = current.getStage();
        FROM = ((FROM - 1) * 3) + stage + 1;
        String title = Contracts.TASK_DETAIL_BUTTONS[FROM - 1];
        switch (FROM) {
//          Poster stages
            case 1:
                findViewById(R.id.query).setVisibility(View.GONE);
                findViewById(R.id.mustDivider).setVisibility(View.GONE);
                action.setOnClickListener(v -> {
                    Intent intent = new Intent(this, BidsListActivity.class);
                    intent.putExtra("task", current);
                    launchActivity(this, intent);
                });
                break;
            case 2:
                findViewById(R.id.mustDivider).setVisibility(View.GONE);
                findViewById(R.id.query).setVisibility(View.GONE);
                title += current.getTasker_name().split(" (?!.* )")[0];
                action.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("id", current.getTasker_id());
                    intent.putExtra("name", current.getTasker_name());
                    intent.putExtra("avatar", current.getTasker_avatar());
                    launchActivity(this, intent);
                });
                break;
            case 3:
                findViewById(R.id.mustDivider).setVisibility(View.GONE);
                findViewById(R.id.query).setVisibility(View.GONE);
                title += current.getTasker_name().split("(?!.* )")[0];
                action.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("id", current.getTasker_id());
                    intent.putExtra("name", current.getTasker_name());
                    intent.putExtra("avatar", current.getTasker_avatar());
                    launchActivity(this, intent);
                });
                break;
//          Tasker Stages
            case 4:
                action.setOnClickListener(v -> new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("CANCEL BID")
                        .setMessage("Do you want to cancel bid on this task?")
                        .setPositiveButton("YES, CANCEL NOW", (dialog, which) -> {
                            if (prevCallResolved && server != null)
                                server.cancelBid(current.getId());
                        })
                        .show());

                break;
            case 5:
                action.setOnClickListener(v -> new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("")
                        .setMessage("This task has been assigned to you.")
                        .setPositiveButton("Mark As Done", (dialog, which) -> {
                            if (prevCallResolved && server != null) server.taskDone(current);
                        })
                        .show());

                break;
            case 6:
                break;
        }
        action.setBackgroundResource(Contracts.TASK_STAGE_BTNS[stage]);
        action.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (FROM == 1)
            getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete && FROM == 1) {
            verifyNCall();
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void verifyNCall() {
        if (!prevCallResolved || server == null) return;
        getToken(token -> server.deleteTask(token, current.getC_date(), current.getId(), current.getCategory()));
        prevCallResolved = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current = (Task) getIntent().getSerializableExtra("task");
        FROM = getIntent().getIntExtra("from", 0);

        if (current == null) finish();
        setContentView(R.layout.activity_task_detail);
        initButton();
        Tools.initMinToolbar(this, "Task Details");
        findViewById(R.id.chat).setOnClickListener(v -> {
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra("id", current.getPoster_id());
            intent.putExtra("name", current.getPoster_name());
            intent.putExtra("avatar", current.getPoster_avatar());
            launchActivity(this, intent);
        });
        TextView name = findViewById(R.id.poster_name);
        name.setText(Html.fromHtml("Task By:  <b>" + (current.getPoster_name() + " ").toUpperCase() + "</b>"));
        name.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", current.getPoster_id());
            intent.putExtra("name", current.getPoster_name());
            intent.putExtra("avatar", current.getPoster_avatar());
            launchActivity(this, intent);
        });
        setUpDistance();
        ((TextView) findViewById(R.id.taskTitle)).setText(current.getTitle());
        ((TextView) findViewById(R.id.taskDesc)).setText(current.getJob_des());
        ((TextView) findViewById(R.id.rewardValue)).setText(current.getCost() + "");
        if (current.getDeadline() != null)
            ((TextView) findViewById(R.id.deadline)).setText(current.getDeadline());
        ChipGroup chipGroup = findViewById(R.id.tagGroup);
        ChipGroup mustGroup = findViewById(R.id.mustGroup);
        tagAdapter = new ChipAdapter(chipGroup, current.getTags());
        mustAdapter = new ChipAdapter(mustGroup, current.getMusthaves());
        if (current.getMusthaves().size() < 1) {
            mustGroup.setVisibility(View.GONE);
            findViewById(R.id.mustText).setVisibility(View.GONE);
            findViewById(R.id.mustDivider).setVisibility(View.GONE);
        }
    }

    private void setUpDistance() {
        TextView dis = findViewById(R.id.taskDis);
        Cache.getLocation(location -> getToken(token -> dis.setText(current.getDistance(location))));
    }


    boolean prevCallResolved = true;

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == SERVER_TASK_DONE) {
            Intent intent = new Intent(this, FeedbackByTaskerActivity.class);
            intent.putExtra("task_id", current.getId());
            intent.putExtra("task_title", current.getTitle());
            intent.putExtra("poster_id", current.getPoster_id());
            finishNLaunchActivity(this, intent);
        }
    }
}
