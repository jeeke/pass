package com.example.mytasker.fragments;

import android.content.Intent;
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

import com.example.mytasker.R;
import com.example.mytasker.activities.HistoryTask;
import com.example.mytasker.activities.LocationActivity;
import com.example.mytasker.activities.TaskDetailActivity;
import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.chat.DialogsActivity;
import com.example.mytasker.holders.TaskHolder;
import com.example.mytasker.models.Task;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.RetrofitParser;
import com.example.mytasker.util.Cache;
import com.example.mytasker.util.FilterHelper;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Cache.getToken;
import static com.example.mytasker.util.Tools.getRetrofit;
import static com.example.mytasker.util.Tools.launchActivity;


public class HomeFragment extends Fragment implements FilterHelper.FilterListener, TaskHolder.RecyclerViewClickListener {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history) {
            launchActivity(getActivity(), HistoryTask.class);
        } else if (id == R.id.action_chats) {
            launchActivity(getActivity(), DialogsActivity.class);
        }
        return false;
    }

    private ShimmerFrameLayout shimmerContainer;
    private RecyclerView listView;
    private SwipeRefreshLayout swipeContainer;
    private TaskListAdapter adapter;

    private void initViews(View v) {
        listView = v.findViewById(R.id.list);
        shimmerContainer = v.findViewById(R.id.shimmer_container);
        swipeContainer = v.findViewById(R.id.swipe_refresh_layout);
    }


    private void initToolbar(View v) {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }


    private boolean prevCallResolved = true;

    private Double lat, lon;

    private void verifyNCall() {
        LocationActivity activity = (LocationActivity) getActivity();
        activity.setListener(() -> {
            lat = activity.lat;
            lon = activity.lon;
            getToken(HomeFragment.this::callRetrofit);
        });
        activity.getLocation();
    }

    private void callRetrofit(String token) {
        if (!prevCallResolved) return;
        listView.animate().alpha(0.0f).setDuration(0).start();
        shimmerContainer.animate().alpha(1.0f).setDuration(0).start();
        shimmerContainer.startShimmer();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<RetrofitParser> call = jsonPlaceHolder.getTasks(
                lat,
                lon,
                100,
                filterHelper.tags
        );

        call.enqueue(new Callback<RetrofitParser>() {
            @Override
            public void onResponse(Call<RetrofitParser> call, Response<RetrofitParser> response) {
                prevCallResolved = true;
                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
                listView.animate().alpha(1.0f).setDuration(100).start();
                swipeContainer.setRefreshing(false);
                if (!response.isSuccessful()) {
                    Log.v("Code: ", String.valueOf(response.code()));
                    swipeContainer.setRefreshing(false);
                    return;
                }
                RetrofitParser details = response.body();
                if (details != null) {
                    details.toTaskList(adapter, filterHelper.price[0], filterHelper.price[1], filterHelper.remote);
                }
            }

            @Override
            public void onFailure(Call<RetrofitParser> call, Throwable t) {
                Log.e("error ", t.getMessage());
                prevCallResolved = true;
                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
                listView.animate().alpha(1.0f).setDuration(100).start();
                swipeContainer.setRefreshing(false);
            }
        });
        prevCallResolved = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        shimmerContainer.stopShimmer();
    }

    private void checkCache() {
        if (Cache.tasks != null) {
            adapter.update(Cache.tasks);
            listView.animate().alpha(1.0f).setDuration(100).start();
            shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
            swipeContainer.setRefreshing(false);
        } else verifyNCall();
    }


    private FilterHelper filterHelper;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        filterHelper = new FilterHelper(this, v.findViewById(R.id.scrollable));
        initToolbar(v);
        initViews(v);
        adapter = new TaskListAdapter(getContext(), this, new ArrayList<>(), false);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer.setOnRefreshListener(this::verifyNCall);
        checkCache();
        swipeContainer.setColorSchemeResources(
                R.color.orange,
                R.color.green_A400);
        return v;
    }

    @Override
    public void closedMenu() {
        swipeContainer.setRefreshing(true);
        verifyNCall();
    }


    @Override
    public void onClick(View view, Task task) {
        Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
        intent.putExtra("from", 0);
        intent.putExtra("task", task);
        startActivity(intent);
    }
}
