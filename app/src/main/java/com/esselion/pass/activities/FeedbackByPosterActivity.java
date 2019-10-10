package com.esselion.pass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.esselion.pass.R;
import com.esselion.pass.Server;

import java.util.HashMap;
import java.util.Map;

import static com.esselion.pass.util.Tools.showSnackBar;

public class FeedbackByPosterActivity extends BaseActivity {

    RatingBar bar1, bar2, bar3;
    String taskerId, taskId, taskTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        taskerId = intent.getStringExtra("tasker_id");
        taskId = intent.getStringExtra("task_id");
        taskTitle = intent.getStringExtra("task_title");
        setContentView(R.layout.activity_feedback_poster);
        initView();
        bar1 = findViewById(R.id.ratingBar);
        bar2 = findViewById(R.id.ratingBar2);
        bar3 = findViewById(R.id.ratingBar3);
    }

    private void submit() {
        float r = bar1.getRating() * bar3.getRating() * bar2.getRating();
        if (!prevCallResolved || server == null)
            showSnackBar(this, "Error, Please try later");
        else if (r == 0.0f) {
            showSnackBar(this, "Please rate all the fields");
        } else call();
    }

    private void call() {
        Map map = new HashMap();
        Map rating = new HashMap();
        rating.put("r1", bar1.getRating());
        rating.put("r2", bar2.getRating());
        rating.put("r3", bar3.getRating());
        map.put("rating", rating);
        map.put("by", "Poster");
        map.put("task_id", taskId);
        map.put("user_id", taskerId);
        server.rate(map);
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_RATE) {
            finish();
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.title)).setText(taskTitle);
        findViewById(R.id.skip).setOnClickListener(v -> finish());
        findViewById(R.id.submit).setOnClickListener(v -> submit());
    }

}
