package com.esselion.pass.activities;

import android.os.Bundle;
import android.widget.RatingBar;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.util.Tools;

import java.util.HashMap;
import java.util.Map;

import static com.esselion.pass.util.Tools.showSnackBar;

public class FeedbackByTaskerActivity extends BaseActivity {

    RatingBar price, clearity, behaviour;
    boolean prevCallResolved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_tasker);
        Tools.initMinToolbar(this, "Confirm Bid");
        findViewById(R.id.submit).setOnClickListener(v -> checkFields());
        initFields();
    }

    private void initFields() {
        price = findViewById(R.id.ratingBar);
        clearity = findViewById(R.id.ratingBar2);
        behaviour = findViewById(R.id.ratingBar3);
    }

    private void callRetrofit(float a, float b, float c) {
        Map map = new HashMap();
        Map rating = new HashMap();
        rating.put("r1", a);
        rating.put("r2", b);
        rating.put("r3", c);
        map.put("rating", rating);
        map.put("by", "Tasker");
        map.put("user_id", getIntent().getStringExtra("poster_id"));
        map.put("task_id", getIntent().getStringExtra("task_id"));
        server.rate(map);
    }


    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_RATE) {
            finish();
        }
    }

    private void checkFields() {
        float a = price.getRating();
        float b = clearity.getRating();
        float c = behaviour.getRating();
        if (!prevCallResolved || server == null)
            showSnackBar(this, "Error, Please try later");
        else if (a * b * c == 0.0f) {
            showSnackBar(this, "Please answer all questions");
        } else {
            callRetrofit(a, b, c);
        }
    }


}
