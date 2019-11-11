package com.esselion.pass.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.models.Location;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.Tools;

import static com.esselion.pass.util.Cache.getToken;
import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.showSnackBar;

public class PostQuestion extends BaseActivity implements Cache.LocationListener {

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
            Cache.getLocation(this);
        });
        ques = findViewById(R.id.question);
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_POST_QUESTION) finish();
    }

    @Override
    public void onLocationFetched(Location location) {
        if (prevCallResolved && server != null) {
            getToken(token -> server.postQuestion(getUser(), token, q, location.getLongitude(), location.getLatitude()));
            prevCallResolved = false;
        }
    }
}