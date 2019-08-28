package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.HashMap;
import java.util.Map;

public class FeedbackByTaskerActivity extends BaseActivity {

    RatingBar price,clearity,behaviour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_tasker);
        Tools.initMinToolbar(this,"Confirm Bid",false);
        findViewById(R.id.submit).setOnClickListener(v -> checkFields());
        initFields();
    }

    private void initFields(){
        price = findViewById(R.id.ratingBar);
        clearity = findViewById(R.id.ratingBar2);
        behaviour = findViewById(R.id.ratingBar3);
    }


    private  void checkFields(){
        float a =  price.getRating();
        float b =  clearity.getRating();
        float c =  behaviour.getRating();
        if( a*b*c==0.0f){
            Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
        }else {
            callRetrofit(a, b, c);
        }
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
        callAPI(map);
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
            Log.v("tag", t.getResult());
            Toast.makeText(this, "Rating Successful", Toast.LENGTH_SHORT).show();
        });
    }
}
