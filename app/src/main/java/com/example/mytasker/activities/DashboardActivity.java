package com.example.mytasker.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.TaskDetail;
import com.example.mytasker.util.CollapsibleToolbar;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity implements TaskListAdapter.RecyclerViewClickListener {

    ImageView bhome, bqna, bfeed, bprofile,logo;
    ImageView prevbselection;
    AnimatedVectorDrawable bottomAppBar;
    FloatingActionButton fab;
    ShimmerFrameLayout shimmerContainer;
    AppBarLayout appBarLayout;
    CollapsibleToolbar collapsibleToolbar;
    boolean appBarExpanded;
    boolean fabActivated;
    int CODE_SETTINGS_ACTIVITY = 100;
    Intent starterIntent;
    private int themeId;

    int centerX;
    int centerY;
    int startRadius;
    int endRadius;
    View fabExpanded;


    private void initFab(){
        fabActivated = false;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        centerX = size.x /2;
        centerY = size.y;
        startRadius = 0;
        endRadius = size.y;
        fabExpanded = findViewById(R.id.fab_expanded);
    }


    MotionLayout catMenu,filterMenu;
    private void initListHead(){
        catOpen = false;
        filterOpen = false;
        catMenu = findViewById(R.id.list_head_cat_menu);
        filterMenu = findViewById(R.id.list_head_filter_menu);
    }

    private void setMotionListener(final MotionLayout firstLayout, final MotionLayout secondLayout){


        firstLayout.setTransitionListener( new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                secondLayout.transitionToEnd();
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }

            @Override
            public boolean allowsTransition(MotionScene.Transition transition) {
                return false;
            }
        });

        secondLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                firstLayout.setTransitionListener(null);
                secondLayout.setTransitionListener(null);
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }

            @Override
            public boolean allowsTransition(MotionScene.Transition transition) {
                return false;
            }
        });
        firstLayout.transitionToStart();
    }
    boolean catOpen;
    public void categories(View v){
        if(catOpen){
            catMenu.transitionToStart();
        }else {
            if(filterOpen){
                filterOpen = false;
                setMotionListener(filterMenu, catMenu);
            }else {
                catMenu.transitionToEnd();
            }
        }
        catOpen = !catOpen;
    }


    boolean filterOpen;
    public void filters(View v){
        if(filterOpen){
            filterMenu.transitionToStart();
        }else {
            if(catOpen){
                catOpen = false;
                setMotionListener(catMenu,filterMenu);
            }else {
                filterMenu.transitionToEnd();
            }
        }
        filterOpen = !filterOpen;
    }

    public void circularReveal(View v){
        float angle;
        if(fabActivated){
            angle = 0f;
            Animator anim = ViewAnimationUtils.createCircularReveal(fabExpanded,centerX,centerY,endRadius,startRadius);
            anim.setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    fabExpanded.setVisibility(View.GONE);
                }
            });
            anim.start();
            fabActivated = false;
        }else {
            angle = 45f;
            fabExpanded.setVisibility(View.VISIBLE);
            Animator anim = ViewAnimationUtils.createCircularReveal(fabExpanded,centerX,centerY,startRadius,endRadius);
            anim.setDuration(500);
            anim.start();
            fabActivated = true;
        }

        final OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(v).
                rotation(angle).
                withLayer().
                setDuration(500).
                setInterpolator(interpolator).
                start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CODE_SETTINGS_ACTIVITY) {
//            if (resultCode == RESULT_OK) {
//            }
            finish();
            starterIntent.putExtra("theme",getThemeToLaunch());
            startActivity(starterIntent);
        }
    }


    @Override
    public void setTheme(int themeId) {
        super.setTheme(themeId);
        this.themeId = themeId;
    }

    private int getThemeToLaunch(){
        return SettingActivity.darkMode ? R.style.DarkMode : R.style.LightMode;
    }

    public int getThemeId() {
        return themeId;
    }


    public void toogleAppBarExpansion(View v) {
        appBarExpanded = !appBarExpanded;
        appBarLayout.setExpanded(appBarExpanded);
    }

//    private void disableEnableControls(boolean enable, ViewGroup vg){
//        for (int i = 0; i < vg.getChildCount(); i++){
//            View child = vg.getChildAt(i);
//            child.setEnabled(enable);
//            child.setFocusable(enable);
//            if (child instanceof ViewGroup){
//                disableEnableControls(enable, (ViewGroup)child);
//            }
//        }
//    }

    @Override
    public void onClick(View view, int position) {
        FragmentManager fm = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction ft = fm.beginTransaction();
        androidx.fragment.app.Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

//        DialogFragment dialogFragment = new TaskDetailFragment();
//        dialogFragment.show(fm, "dialog");
    }


    @Override
    protected void onPause() {
        super.onPause();
        shimmerContainer.stopShimmer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starterIntent = getIntent();
        int theme = starterIntent.getIntExtra("theme",R.style.LightMode);
        setTheme(theme);
        setContentView(R.layout.activity_dashboard);
        initFab();
        shimmerContainer = findViewById(R.id.shimmer_container);
        shimmerContainer.startShimmer();
        final RecyclerView listView = findViewById(R.id.ListView_dashboard);

        initListHead();

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                shimmerContainer.stopShimmer();
//                shimmerContainer.animate().alpha(0.0f).setDuration(500).start();
//                listView.animate().alpha(1.0f).setDuration(1000).start();
//                listView.scrollToPosition(0);
//            }
//        }, 2000);
        ConstraintLayout layout = findViewById(R.id.root);
//        TODO
//        layout.setBackgroundColor(SettingActivity.toolbar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nkliobv7w5.execute-api.ap-south-1.amazonaws.com/dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<TaskDetail> call = jsonPlaceHolder.getDetail(
                new Integer[]{25, 25},
                100,
                new String[]{"tech", "null"}
        );

        call.enqueue(new Callback<TaskDetail>() {
            @Override
            public void onResponse(Call<TaskDetail> call, Response<TaskDetail> response) {
                if (!response.isSuccessful()) {
                    Log.v("Code: ", String.valueOf(response.code()));
                    return;
                }

                TaskDetail details = response.body();
                TaskListAdapter adapter = new TaskListAdapter(DashboardActivity.this, details.getTasks());
                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(500).start();
                listView.setAdapter(adapter);
                listView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
                listView.scrollToPosition(0);
                listView.animate().alpha(1.0f).setDuration(1000).start();
            }

            @Override
            public void onFailure(Call<TaskDetail> call, Throwable t) {
                Log.v("error ", t.getMessage());
            }
        });

//        CustomListAdapter adapter = new CustomListAdapter(list, getApplicationContext());

        bhome = findViewById(R.id.home);
        bqna = findViewById(R.id.qna);
        bfeed = findViewById(R.id.feed);
        bprofile = findViewById(R.id.profile);
        appBarLayout = findViewById(R.id.app_bar);
        collapsibleToolbar = appBarLayout.findViewById(R.id.motionLayout);
        logo = findViewById(R.id.esselion);
        appBarLayout.setExpanded(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        AppBarLayout.Behavior.DragCallback dragCallback = new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        };
        behavior.setDragCallback(dragCallback);
        params.setBehavior(behavior);


        prevbselection = bhome;
//        listView.setBackgroundColor(SettingActivity.list);
        fab = findViewById(R.id.fab);
        View listHead = findViewById(R.id.list_head);
//        GradientDrawable shape = new GradientDrawable();
//        shape.setShape(GradientDrawable.RECTANGLE);
//        shape.setCornerRadii(new float[]{320, 320, 320, 320, 64, 64, 64, 64});
//        shape.setColor(getResources().getColor(R.color.colorPrimary));
//        listHead.setBackground(shape);
        listHead.setBackground(getResources().getDrawable(R.drawable.list_head_dark));
        if (getThemeId()==R.style.DarkMode)
            logo.setImageResource(R.drawable.esselion_white);
        else
            logo.setImageResource(R.drawable.esselion_dark);
    }

//    TODO
//    public void profile(View v) {
//        Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//    }

    public void setting(View v) {
        Intent intent = new Intent(DashboardActivity.this, SettingActivity.class);
        startActivityForResult(intent,CODE_SETTINGS_ACTIVITY);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
//
//    public void support(View v)
//    {
//        Intent intent = new Intent(DashboardActivity.this,Support_activity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//    }

    public void post(View v) {
//        fab.setEnabled(false);
//        ViewCompat.animate(fab).yBy(-120).setDuration(330).start();
//        bottomAppBar.start();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ViewCompat.animate(fab).yBy(120).setDuration(330).start();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(DashboardActivity.this, CreateTask.class);
//                        startActivityForResult(intent, 100);
//                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//                    }
//                }, 350);
//            }
//        }, 350);

        Intent intent = new Intent(DashboardActivity.this, PostActivity.class);
        startActivity(intent);
    }

    public void bselected(View v) {
        ImageView current = (ImageView) v;
        prevbselection.setImageResource(toogleImage(prevbselection.getId(),false));
        current.setImageResource(toogleImage(current.getId(),true));
        prevbselection = current;
    }

    private int toogleImage(int id, boolean selected) {
        switch (id) {
            case R.id.home:
                return selected ? R.mipmap.home_fill : R.mipmap.home;
            case R.id.feed:
                return selected ? R.mipmap.scroll_fill : R.mipmap.scroll;
            case R.id.qna:
                return selected ? R.mipmap.qna_fill : R.mipmap.qna;
            case R.id.profile:
                return selected ? R.mipmap.profile_fill : R.mipmap.profile;
        }
        return 0;
    }

//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

}
