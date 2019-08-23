package com.example.mytasker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.activities.HistoryTask;
import com.example.mytasker.activities.TaskDetailActivity;
import com.example.mytasker.adapters.TaskListAdapter;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.RetrofitFeedHelper;
import com.example.mytasker.util.FilterHelper;
import com.example.mytasker.util.NetworkCache;
import com.example.mytasker.util.ToolbarHelper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Tools.getRetrofit;


public class HomeFragment extends Fragment implements FilterHelper.FilterListener, TaskListAdapter.RecyclerViewClickListener {


    public HomeFragment() {
        // Required empty public constructor
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


    private void callRetrofit(String token) {
        shimmerContainer.startShimmer();
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<RetrofitFeedHelper> call = jsonPlaceHolder.getTasks(
                filterHelper.loc,
                filterHelper.radius,
                filterHelper.tags,
                filterHelper.price,
                filterHelper.remote
        );

        call.enqueue(new Callback<RetrofitFeedHelper>() {
            @Override
            public void onResponse(Call<RetrofitFeedHelper> call, Response<RetrofitFeedHelper> response) {
                if (!response.isSuccessful()) {
                    Log.v("Code: ", String.valueOf(response.code()));
                    swipeContainer.setRefreshing(false);
                    return;
                }
                RetrofitFeedHelper details = response.body();


                if (details != null) {
                    NetworkCache.tasks = details.toTaskList();
                }
                adapter.update(NetworkCache.tasks);


                shimmerContainer.stopShimmer();
                shimmerContainer.animate().alpha(0.0f).setDuration(500).start();
                listView.animate().alpha(1.0f).setDuration(1000).start();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RetrofitFeedHelper> call, Throwable t) {
                Log.e("error ", t.getMessage());
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void callRetrofitHelper() {
        if (NetworkCache.tasks != null) {
            shimmerContainer.setVisibility(View.GONE);
            adapter.update(NetworkCache.tasks);
            listView.setAlpha(1.0f);
        } else {
            verifyNCall();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FilterHelper filterHelper;
    private ToolbarHelper toolbarHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        toolbarHelper = new ToolbarHelper(getActivity(), (MotionLayout) v, HistoryTask.class);
        filterHelper = new FilterHelper(this, (MotionLayout) v);
        initViews(v);

        adapter = new TaskListAdapter(getContext(), this, new ArrayList<>(), false);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer.setOnRefreshListener(this::verifyNCall);
        callRetrofitHelper();
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);
        return v;
    }

    @Override
    public void closedMenu() {
        swipeContainer.setRefreshing(true);
        verifyNCall();
    }


    @Override
    public void onClick(View view, int position) {
        TaskDetailActivity.FROM = 0;
        Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}
