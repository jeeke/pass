package com.esselion.pass.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.esselion.pass.R;
import com.esselion.pass.activities.HistoryQues;
import com.esselion.pass.activities.LocationActivity;
import com.esselion.pass.activities.TaskDetailActivity;
import com.esselion.pass.adapters.QuestionAdapter;
import com.esselion.pass.retrofit.JsonPlaceHolder;
import com.esselion.pass.retrofit.RetrofitParser;
import com.esselion.pass.util.Cache;
import com.facebook.shimmer.ShimmerFrameLayout;

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

    private void initToolbar(View v) {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("Questions");
        toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.blue_grey));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private ShimmerFrameLayout shimmerContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_history) {
            launchActivity((AppCompatActivity) getActivity(), HistoryQues.class);
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        shimmerContainer.stopShimmer();
    }

    private boolean prevCallResolved = true;

    private void verifyNCall() {
        if (!prevCallResolved) return;
        LocationActivity activity = (LocationActivity) getActivity();
        activity.startLocationUpdates(location -> {
            TaskDetailActivity.location = location;
            listView.animate().alpha(0.0f).setDuration(0).start();
            shimmerContainer.animate().alpha(1.0f).setDuration(0).start();
            getToken(token -> callRetrofit(token, location.getLongitude(),
                    location.getLatitude()), getActivity());
        });
    }


    private void checkCache() {
        if (Cache.questions != null) {
            adapter.update(Cache.questions);
            listView.animate().alpha(1.0f).setDuration(100).start();
            shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
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
                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
                listView.animate().alpha(1.0f).setDuration(100).start();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RetrofitParser> call, Throwable t) {
                prevCallResolved = true;
                Log.e("error ", t.getMessage());
                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
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
        shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
        adapter = new QuestionAdapter(getActivity(), new ArrayList<>());
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);
        swipeContainer.setOnRefreshListener(this::verifyNCall);
        swipeContainer.setRefreshing(true);
        checkCache();
        return v;
    }

    private void initViews(View v) {
        listView = v.findViewById(R.id.list);
        swipeContainer = v.findViewById(R.id.swipe_refresh_layout);
        shimmerContainer = v.findViewById(R.id.shimmer_container);
    }

}
