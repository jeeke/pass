package com.example.mytasker.activities;

import android.os.Bundle;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;

public class PostFeed extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed);
        Tools.initMinToolbar(this, "Create Post", false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
    }
}
