package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.util.Contracts;
import com.google.firebase.functions.FirebaseFunctionsException;

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
        if (r == 0.0f) {
            Toast.makeText(this, "Please rate all the fields", Toast.LENGTH_SHORT).show();
        } else call();
    }

    private void call() {
        try {
            Map map = new HashMap();
            Map rating = new HashMap();
            rating.put("r1", bar1.getRating());
            rating.put("r2", bar2.getRating());
            rating.put("r3", bar3.getRating());
            map.put("rating", rating);
            map.put("by", "Poster");
            map.put("task_id", task.getString("task_id"));
            map.put("user_id", task.getString("tasker_id"));
            callAPI(map);
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

    private void callAPI(Map data) {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your feedback..");
        dlg.show();

        Contracts.call(data, "rate").addOnCompleteListener(t -> {
            dlg.dismiss();
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.e("tag", ffe + "\n" + code + "\n" + details);
                }
                Toast.makeText(this, "Rating Unsuccessful", Toast.LENGTH_SHORT).show();
                Log.e("tag", e + "");
                return;
            }
            finish();
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            int pending_feedbacks = prefs.getInt("pending_feedbacks", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("pending_feedbacks", pending_feedbacks - 1);
            editor.remove("feedback" + pending_feedbacks);
            editor.apply();
            Log.v("tag", t.getResult());
            Toast.makeText(this, "Rating Successful", Toast.LENGTH_SHORT).show();
        });
    }
}
