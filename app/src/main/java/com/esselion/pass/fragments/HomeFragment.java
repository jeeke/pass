package com.esselion.pass.fragments;

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

import com.esselion.pass.R;
import com.esselion.pass.activities.HistoryTask;
import com.esselion.pass.activities.LocationActivity;
import com.esselion.pass.activities.TaskDetailActivity;
import com.esselion.pass.adapters.TaskListAdapter;
import com.esselion.pass.chat.DialogsActivity;
import com.esselion.pass.holders.TaskHolder;
import com.esselion.pass.models.Task;
import com.esselion.pass.retrofit.JsonPlaceHolder;
import com.esselion.pass.retrofit.RetrofitParser;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.FilterHelper;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.esselion.pass.util.Cache.getToken;
import static com.esselion.pass.util.Tools.getRetrofit;
import static com.esselion.pass.util.Tools.launchActivity;


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

    private void verifyNCall() {
        LocationActivity activity = (LocationActivity) getActivity();
        listView.animate().alpha(0.0f).setDuration(0).start();
        shimmerContainer.animate().alpha(1.0f).setDuration(0).start();
        activity.startLocationUpdates(location -> getToken(
                token -> callRetrofit(token, location.getLongitude(),
                        location.getLatitude()), getActivity()));
    }

    private void callRetrofit(String token, double lon, double lat) {
        if (!prevCallResolved) return;
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
                    details.toTaskList(adapter, filterHelper.price[0], filterHelper.price[1], Cache.getUser(getActivity()).getUid());
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
        shimmerContainer.animate().alpha(0.0f).setDuration(0).start();
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
