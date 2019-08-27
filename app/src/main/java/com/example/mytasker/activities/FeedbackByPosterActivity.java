package com.example.mytasker.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytasker.R;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.mytasker.MyFirebaseMessagingService.MY_PREFS_NAME;

public class FeedbackByPosterActivity extends BaseActivity {

    RatingBar bar1, bar2, bar3;
    SharedPreferences prefs;
    JSONObject task;
    int pending_feedbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_feedback_poster);
        initView();
        bar1 = findViewById(R.id.ratingBar);
        bar2 = findViewById(R.id.ratingBar2);
        bar3 = findViewById(R.id.ratingBar3);
    }

    private void submit() {
        float r = bar1.getRating() * bar3.getRating() * bar2.getRating();
        if (r == 0) {
            Toast.makeText(this, "Please rate all the fields", Toast.LENGTH_SHORT).show();
        } else callFirebase();
    }

    private void callFirebase() {
        try {
            String tasker_profile_ref = "Profiles/" + task.getString("tasker_id") + "/Ratings";
            String rating_ref = "Ratings/" + task.getString("task_id") + "/ByPoster";
            Map map = new HashMap();
            Map rating = new HashMap();
            rating.put("r1", bar1.getRating());
            rating.put("r2", bar2.getRating());
            rating.put("r3", bar3.getRating());
            map.put(tasker_profile_ref, rating);
            map.put(rating_ref, rating);
            FirebaseDatabase.getInstance().getReference().updateChildren(map, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("pending_feedbacks", pending_feedbacks - 1);
                    editor.remove("feedback" + pending_feedbacks);
                    editor.apply();
                    finish();
                }
                Toast.makeText(this, "Response could not be saved", Toast.LENGTH_SHORT).show();
            });
        } catch (JSONException e) {
            finish();
            e.printStackTrace();
        }
    }

    private void initView() {
        pending_feedbacks = prefs.getInt("pending_feedbacks", 0);
        try {
            task = new JSONObject(prefs.getString("feedback" + pending_feedbacks, ""));
            ((TextView) findViewById(R.id.title)).setText(task.getString("task_title"));
            findViewById(R.id.skip).setOnClickListener(v -> finish());
            findViewById(R.id.submit).setOnClickListener(v -> submit());
        } catch (JSONException e) {
            finish();
            e.printStackTrace();
        }
    }
}
