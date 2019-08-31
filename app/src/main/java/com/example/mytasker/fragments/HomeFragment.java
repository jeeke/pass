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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.activities.HistoryFeed;
import com.example.mytasker.activities.TaskDetailActivity;
import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.holders.TaskHolder;
import com.example.mytasker.models.Task;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.RetrofitParser;
import com.example.mytasker.util.FilterHelper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
            launchActivity((AppCompatActivity) getActivity(), HistoryFeed.class);
        } else if (id == R.id.action_chats) {

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

    private void verifyNCall() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callRetrofit(task.getResult().getToken());
                    } else {
                        // Handle error -> task.getException();
                        Toast.makeText(getContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private ArrayList<Task> tasks;
    private void callRetrofit(String token) {
        shimmerContainer.startShimmer();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<RetrofitParser> call = jsonPlaceHolder.getTasks(
                filterHelper.loc,
                filterHelper.radius,
                filterHelper.tags
        );

        call.enqueue(new Callback<RetrofitParser>() {
            @Override
            public void onResponse(Call<RetrofitParser> call, Response<RetrofitParser> response) {
                if (!response.isSuccessful()) {
                    Log.v("Code: ", String.valueOf(response.code()));
                    swipeContainer.setRefreshing(false);
                    return;
                }
                RetrofitParser details = response.body();
                if (details != null) {
                    tasks = details.toTaskList();
                }
                adapter.update(tasks);
                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(500).start();
                listView.animate().alpha(1.0f).setDuration(1000).start();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RetrofitParser> call, Throwable t) {
                Log.e("error ", t.getMessage());
                swipeContainer.setRefreshing(false);
            }
        });
    }


    private FilterHelper filterHelper;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initToolbar(v);
        filterHelper = new FilterHelper(this, v.findViewById(R.id.scrollable));
        initViews(v);
        adapter = new TaskListAdapter(getContext(), this, new ArrayList<>(), false);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer.setOnRefreshListener(this::verifyNCall);
        verifyNCall();
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
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
