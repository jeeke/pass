package com.example.mytasker.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mytasker.R;
import com.example.mytasker.adapters.FeedAdapter;
import com.example.mytasker.adapters.QuestionAdapter;
import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.fragments.FeedFragment;
import com.example.mytasker.fragments.HomeFragment;
import com.example.mytasker.fragments.ProfileFragment;
import com.example.mytasker.fragments.QuestionFragment;
import com.example.mytasker.models.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.mytasker.util.Contracts.CODE_SETTINGS_ACTIVITY;

public class DashboardActivity extends AppCompatActivity implements TaskListAdapter.RecyclerViewClickListener,HomeFragment.OnFragmentInteractionListener,QuestionFragment.OnListFragmentInteractionListener, QuestionAdapter.RecyclerViewClickListener,FeedFragment.OnListFragmentInteractionListener, FeedAdapter.RecyclerViewClickListener,ProfileFragment.OnFragmentInteractionListener {

    ImageView bhome, bqna, bfeed, bprofile;
    ImageView prevbselection;
    View bottomAppBar;
    boolean fabActivated;
    Intent starterIntent;

    int centerX;
    int centerY;
    int startRadius;
    int endRadius;
    View fabExpanded;
    Fragment[] fragments = new Fragment[]{null,null,null,null};

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void loadNextFrag(View view) {
    }

    FloatingActionButton fab;
    private void initFab() {
        fabActivated = false;
        fab = findViewById(R.id.fab);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        centerX = size.x / 2;
        centerY = size.y;
        startRadius = 0;
        endRadius = size.y;
        fabExpanded = findViewById(R.id.fab_expanded);
    }


    public void circularReveal(View v) {
        float angle;
        if (fabActivated) {
            angle = 0f;
            Animator anim = ViewAnimationUtils.createCircularReveal(fabExpanded, centerX, centerY, endRadius, startRadius);
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
        } else {
            angle = 45f;
            fabExpanded.setVisibility(View.VISIBLE);
            Animator anim = ViewAnimationUtils.createCircularReveal(fabExpanded, centerX, centerY, startRadius, endRadius);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_SETTINGS_ACTIVITY) {
//            if (resultCode == RESULT_OK) {
//            }
            finish();
//            starterIntent.putExtra("theme", getThemeToLaunch());
            startActivity(starterIntent);
        }
    }


    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(this,TaskDetailActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    private void init() {
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        bhome = findViewById(R.id.home);
        bqna = findViewById(R.id.qna);
        bfeed = findViewById(R.id.feed);
        bprofile = findViewById(R.id.profile);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starterIntent = getIntent();
        int theme = starterIntent.getIntExtra("theme", R.style.LightMode);
        setTheme(theme);
        setContentView(R.layout.activity_dashboard);
        init();
        initFab();
        fragments[0] = new HomeFragment();
        loadFragment(fragments[0]);
        prevbselection = bhome;
    }

    //    TODO

    public void postTask(View v) {
        Intent intent = new Intent(DashboardActivity.this, PostTask.class);
        circularReveal(fab);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void postQues(View v) {
        circularReveal(fab);
        Intent intent = new Intent(DashboardActivity.this, PostQuestion.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void bselected(View v) {
        ImageView current = (ImageView) v;
        if(v.getId()!=prevbselection.getId()){
            prevbselection.setImageResource(toogleImage(prevbselection.getId(), false));
            current.setImageResource(toogleImage(current.getId(), true));
            prevbselection = current;
        }
    }


    private int toogleImage(int id, boolean selected) {
        switch (id) {
            case R.id.home:
                fragments[0] = fragments[0]==null? new HomeFragment():fragments[0];
                loadFragment(fragments[0]);
                return selected ? R.mipmap.home_fill : R.mipmap.home;
            case R.id.feed:
                fragments[2] = fragments[2]==null? new FeedFragment():fragments[2];
                loadFragment(fragments[2]);
                return selected ? R.mipmap.scroll_fill : R.mipmap.scroll;
            case R.id.qna:
                fragments[1] = fragments[1]==null? new QuestionFragment():fragments[1];
                loadFragment(fragments[1]);
                return selected ? R.mipmap.qna_fill : R.mipmap.qna;
            case R.id.profile:
                fragments[3] = fragments[3]==null? new ProfileFragment():fragments[3];
                loadFragment(fragments[3]);
                return selected ? R.mipmap.profile_fill : R.mipmap.profile;
        }
        return 0;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
// TODO
    }

    @Override
    public void onListFragmentInteraction(Question question) {

    }
}
