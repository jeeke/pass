package com.example.mytasker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mytasker.R;
import com.example.mytasker.activities.HistoryQues;
import com.example.mytasker.activities.QuestionDetailActivity;
import com.example.mytasker.adapters.QuestionAdapter;
import com.example.mytasker.models.Question;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.RetrofitParser;
import com.example.mytasker.util.FilterHelper;
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


public class QuestionFragment extends Fragment implements FilterHelper.FilterListener,QuestionAdapter.RecyclerViewClickListener{

    private ShimmerFrameLayout shimmerContainer;
    ArrayList<Question> questions;

    public QuestionFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    FilterHelper filterHelper;

    private void callRetrofit(String token) {
        shimmerContainer.startShimmer();
        Retrofit retrofit = getRetrofit(token);

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<RetrofitParser> call = jsonPlaceHolder.getQuestions(
                filterHelper.loc,
                filterHelper.radius,
                filterHelper.tags);

//        new double[]{25.0, 25.0},
//                100,
//                new String[]{"tech", "null"}

        call.enqueue(new Callback<RetrofitParser>() {
            @Override
            public void onResponse(Call<RetrofitParser> call, Response<RetrofitParser> response) {
                if (!response.isSuccessful()) {
                    Log.e("Code: ", response.toString());
                    return;
                }
                RetrofitParser details = response.body();

                if (details != null) {
                    questions = details.toQuesList();
                }
                adapter.update(questions);

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

    private void callRetrofitHelper() {
        if (questions != null) {
            shimmerContainer.setVisibility(View.GONE);
            adapter.update(questions);
            listView.setAlpha(1.0f);
        }else {
            verifyNCall();
        }
    }
    private RecyclerView listView;
    private SwipeRefreshLayout swipeContainer;
    private QuestionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question_list, container, false);
        ToolbarHelper toolbarHelper = new ToolbarHelper((AppCompatActivity) getActivity(), (MotionLayout) v, HistoryQues.class);
        filterHelper = new FilterHelper(this, (MotionLayout) v);
        adapter = new QuestionAdapter(getContext(),this, new ArrayList<>(),false);
        initViews(v);
        initListeners(v);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        callRetrofitHelper();
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light);
        return v;
    }

    private void initViews(View v) {
        listView = v.findViewById(R.id.list);
        shimmerContainer = v.findViewById(R.id.shimmer_container);
        swipeContainer = v.findViewById(R.id.swipe_refresh_layout);
    }

    private void initListeners(View v) {
        swipeContainer.setOnRefreshListener(this::verifyNCall);
    }

    @Override
    public void closedMenu() {

    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
        intent.putExtra("ques", questions.get(position));
        startActivity(intent);
    }
}
