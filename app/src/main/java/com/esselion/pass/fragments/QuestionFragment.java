package com.esselion.pass.fragments;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.esselion.pass.MyFirebaseMessagingService;
import com.esselion.pass.R;
import com.esselion.pass.activities.HistoryQues;
import com.esselion.pass.adapters.QuestionAdapter;
import com.esselion.pass.retrofit.JsonPlaceHolder;
import com.esselion.pass.retrofit.RetrofitParser;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.SharedPrefAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.esselion.pass.util.Cache.getToken;
import static com.esselion.pass.util.Tools.getRetrofit;
import static com.esselion.pass.util.Tools.launchActivity;


public class QuestionFragment extends Fragment {

    public QuestionFragment() {
    }

    private ProgressBar bar;
    private void initToolbar(View v) {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        bar = v.findViewById(R.id.progress_bar);
        toolbar.setTitle("Questions");
        toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.blue_grey));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyFirebaseMessagingService.unregisterNotificationListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
            MyFirebaseMessagingService.registerNotificationListener(activity::invalidateOptionsMenu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (SharedPrefAdapter.getInstance().hasUnseenQuesHistory()) {
            menu.getItem(0).setIcon(R.drawable.ic_history_avd);
            Drawable menuItem = menu.getItem(0).getIcon();
            Animatable animatable = (Animatable) menuItem;
            animatable.start();
            AnimatedVectorDrawableCompat.registerAnimationCallback
                    (menuItem, new Animatable2Compat.AnimationCallback() {
                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            super.onAnimationEnd(drawable);
                            animatable.start();
                        }
                    });
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Activity activity = getActivity();
        if (activity != null && item.getItemId() == R.id.action_history) {
            launchActivity(activity, HistoryQues.class);
            SharedPrefAdapter.getInstance().setHasQuesHistory(false);
            activity.invalidateOptionsMenu();
        }
        return false;
    }

    private boolean prevCallResolved = true;

    private void verifyNCall() {
        if (!prevCallResolved) return;
        Cache.getLocation(location -> {
            listView.animate().alpha(0.0f).setDuration(0).start();
            bar.setVisibility(View.VISIBLE);
            getToken(token -> callRetrofit(token, location.getLongitude(),
                    location.getLatitude()));
        });
    }


    private void checkCache() {
        if (Cache.questions != null) {
            adapter.update(Cache.questions);
            listView.animate().alpha(1.0f).setDuration(100).start();
            bar.setVisibility(View.GONE);
            swipeContainer.setRefreshing(false);
        } else verifyNCall();
    }

    private RecyclerView listView;
    private SwipeRefreshLayout swipeContainer;
    private QuestionAdapter adapter;

    private void callRetrofit(String token, double lon, double lat) {
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<RetrofitParser> call = jsonPlaceHolder.getQuestions(lat, lon);
        call.enqueue(new Callback<RetrofitParser>() {
            @Override
            public void onResponse(Call<RetrofitParser> call, Response<RetrofitParser> response) {
                prevCallResolved = true;
                if (!response.isSuccessful()) {
                    Log.e("Code: ", response.toString());
                    return;
                }
                RetrofitParser details = response.body();

                if (details != null) {
                    Cache.questions = details.toQuesList();
                }
                adapter.update(Cache.questions);
                bar.setVisibility(View.GONE);
                listView.animate().alpha(1.0f).setDuration(100).start();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RetrofitParser> call, Throwable t) {
                prevCallResolved = true;
                Log.e("error ", t.getMessage());
                bar.setVisibility(View.GONE);
                listView.animate().alpha(1.0f).setDuration(100).start();
                swipeContainer.setRefreshing(false);
            }
        });
        prevCallResolved = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        initToolbar(v);
        initViews(v);
        adapter = new QuestionAdapter(getActivity(), new ArrayList<>());
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);
        swipeContainer.setOnRefreshListener(this::verifyNCall);
        checkCache();
        return v;
    }

    private void initViews(View v) {
        listView = v.findViewById(R.id.list);
        swipeContainer = v.findViewById(R.id.swipe_refresh_layout);
    }

}
