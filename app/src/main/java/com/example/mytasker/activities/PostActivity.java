package com.example.mytasker.activities;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.badoualy.stepperindicator.StepperIndicator;
import com.example.mytasker.R;
import com.example.mytasker.fragments.Post1frag;
import com.example.mytasker.fragments.PostFrag2;
import com.example.mytasker.fragments.PostFrag3;
import com.example.mytasker.models.IndividualTask;
import com.example.mytasker.models.People;
import com.example.mytasker.models.PeopleChip;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostActivity extends AppCompatActivity {

    private ChipsInput mChipsInput;
    private List<PeopleChip> items = new ArrayList<>();
    private List<ChipInterface> items_added = new ArrayList<>();
    private List<People> items_people = new ArrayList<>();
    StepperIndicator indicator;
    FloatingActionButton fab;
    int currentpage = 0;

    @Override
    protected void onResume() {
        super.onResume();
        fab.setImageDrawable(getDrawable(R.drawable.post_done_anim));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        indicator = findViewById(R.id.stepperIndicator);
        Fragment fragment = new Post1frag();
        loadFragment(fragment);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = findViewById(R.id.floatingActionButton);

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    public void loadNextFrag(View view) {
        if (currentpage < 3) {
            indicator.setCurrentStep(++currentpage);
        }
        Fragment fragment;
        if (currentpage == 1) {
            fragment = new PostFrag2();
//            fragment = new OrdersFrag();
            loadFragment(fragment);
            //Load frag
        } else if (currentpage == 2) {
            fragment = new PostFrag3();
            loadFragment(fragment);
            AnimatedVectorDrawable icon = (AnimatedVectorDrawable) fab.getDrawable();
            icon.start();
            //Load frag and change fab drawable
        } else {
            //done
            postmytask();
        }
    }

    public void postmytask()
    {
        //result.setText("sending");
        Date date = new Date();
        IndividualTask task = new IndividualTask(
                date.getTime(),
                PostFrag2.description.getText().toString(),
                PostFrag2.title.getText().toString(),
                2500,
                "mumbai",
                "rakesh",
                Post1frag.category,
                "videos",
                "time",
                new Integer[]{25,25},
                new String[]{"tech","null"},
                PostFrag2.must_have

        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://post-gateway-ewezudecda-uc.a.run.app/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<IndividualTask> call = jsonPlaceHolder.createDetail(task);
        call.enqueue(new Callback<IndividualTask>() {
            @Override
            public void onResponse(Call<IndividualTask> call, Response<IndividualTask> response) {

                if (!response.isSuccessful()) {
                    return;
                }

                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }

            @Override
            public void onFailure(Call<IndividualTask> call, Throwable t) {
                Log.v("error",t.getMessage());
            }
        });
    }
}