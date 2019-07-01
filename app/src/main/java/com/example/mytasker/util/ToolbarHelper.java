package com.example.mytasker.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.constraintlayout.motion.widget.MotionLayout;

import com.example.mytasker.R;
import com.example.mytasker.activities.ContactUs;
import com.example.mytasker.activities.PrevPosts;
import com.example.mytasker.activities.SearchActivity;
import com.example.mytasker.activities.SettingActivity;

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
        v.findViewById(R.id.support).setOnClickListener(this::contactUs);
        v.findViewById(R.id.my_posts).setOnClickListener(this::posts);
    }

    private void posts(View view) {
        launchActivity(PrevPosts.class);
    }

    private void launchActivity(Class className){
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void contactUs(View view) {
        launchActivity(ContactUs.class);
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
        launchActivity(SearchActivity.class);
    }

    private void setting(View v) {
        launchActivity(SettingActivity.class);
//        context.startActivityForResult(intent, CODE_SETTINGS_ACTIVITY);
    }
}
