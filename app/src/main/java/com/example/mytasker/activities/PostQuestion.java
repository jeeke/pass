package com.example.mytasker.activities;

import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;

import static com.example.mytasker.util.Cache.getToken;
import static com.example.mytasker.util.Cache.getUser;
import static com.example.mytasker.util.Tools.showSnackBar;

public class PostQuestion extends LocationActivity implements LocationActivity.LocationListener {

    Button fab;
    EditText ques;
    private String q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ques);
        Tools.initMinToolbar(this, "Post Question");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            q = ques.getText().toString();
            if (q.equals("")) {
                showSnackBar(this, "Please enter your question");
                return;
            }
            server.getLocation(this);
        });
        ques = findViewById(R.id.question);
    }

    @Override
    public void onLocationFetched(Location location) {
        if (prevCallResolved && server != null) {
            getToken(token -> server.postQuestion(getUser(this), token, q, location.getLongitude(), location.getLatitude()), this);
            prevCallResolved = false;
        }
    }
}