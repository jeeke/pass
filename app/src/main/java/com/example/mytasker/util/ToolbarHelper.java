package com.example.mytasker.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.constraintlayout.motion.widget.MotionLayout;

import com.example.mytasker.R;
import com.example.mytasker.activities.SearchActivity;
import com.example.mytasker.activities.SettingActivity;

import static com.example.mytasker.util.Contracts.CODE_SETTINGS_ACTIVITY;

public class ToolbarHelper {
    private MotionLayout toolbar;
    private boolean open;
    private Activity context;
    public ToolbarHelper(Activity context, MotionLayout v){
        this.context = context;
        toolbar = v;
        open = false;
        v.findViewById(R.id.action_search).setOnClickListener(this::search);
        v.findViewById(R.id.menu_icon).setOnClickListener(this::toggleToolBar);
        v.findViewById(R.id.chats).setOnClickListener(this::setting);
        v.findViewById(R.id.support).setOnClickListener(this::support);
        v.findViewById(R.id.my_posts).setOnClickListener(this::profile);
    }

    private void toggleToolBar(View v){
        if(open){
            toolbar.transitionToStart();
        }else{
            toolbar.transitionToEnd();
        }
        open = !open;
    }

    private void search(View view) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void profile(View v) {
//        Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void setting(View v) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivityForResult(intent, CODE_SETTINGS_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void support(View v) {
//        Intent intent = new Intent(DashboardActivity.this,Support_activity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}
