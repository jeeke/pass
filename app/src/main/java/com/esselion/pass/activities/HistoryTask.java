package com.esselion.pass.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.esselion.pass.R;
import com.esselion.pass.adapters.HistoryTaskAdapter;
import com.google.android.material.tabs.TabLayout;

public class HistoryTask extends BaseActivity {

    TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_task);
        initToolbar();
        HistoryTaskAdapter sectionsPagerAdapter = new HistoryTaskAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Tasks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}