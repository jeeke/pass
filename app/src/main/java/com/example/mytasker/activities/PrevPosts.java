package com.example.mytasker.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mytasker.R;
import com.example.mytasker.activities.ui.main.SectionsPagerAdapter;
import com.example.mytasker.util.Tools;
import com.google.android.material.tabs.TabLayout;

public class PrevPosts extends AppCompatActivity {

    final int[] tabIcon = {
            R.mipmap.home,
            R.mipmap.qna,
            R.mipmap.scroll
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev_posts);
        Tools.initMinToolbar(this, "ACTIVITIES", true);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabIconTint(getResources().getColorStateList(R.color.tab_icon_color));
        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.getTabAt(i).setIcon(tabIcon[i]);
        }
    }
}