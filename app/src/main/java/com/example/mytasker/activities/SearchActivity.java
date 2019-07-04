package com.example.mytasker.activities;

import android.os.Bundle;
import android.view.View;

import com.example.mytasker.R;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void finishSearch(View v) {
        finish();
    }

}
