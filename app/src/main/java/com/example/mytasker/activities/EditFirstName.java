package com.example.mytasker.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.example.mytasker.R;

public class EditFirstName extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_first_name);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit First Name");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
