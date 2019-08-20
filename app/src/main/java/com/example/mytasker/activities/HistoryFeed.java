package com.example.mytasker.activities;

import android.os.Bundle;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;

public class HistoryFeed extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_feed);
        Tools.initMinToolbar(this, "My Posts", false);
    }
}
