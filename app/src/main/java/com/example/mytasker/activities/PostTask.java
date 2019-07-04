package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.badoualy.stepperindicator.StepperIndicator;
import com.example.mytasker.R;
import com.example.mytasker.fragments.PostCategory;
import com.example.mytasker.fragments.PostTaskDetail;
import com.example.mytasker.fragments.TaskAdditional;
import com.example.mytasker.models.People;
import com.example.mytasker.models.PeopleChip;
import com.example.mytasker.models.Task;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;
import com.example.mytasker.util.Contracts;
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

public class PostTask extends AppCompatActivity {

    private ChipsInput mChipsInput;
    private List<PeopleChip> items = new ArrayList<>();
    private List<ChipInterface> items_added = new ArrayList<>();
    private List<People> items_people = new ArrayList<>();
    StepperIndicator indicator;
    FloatingActionButton fab;
    int currentpage = 0;
    ProgressDialog dlg;

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
        Fragment fragment = new PostCategory();
        loadFragment(fragment);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        fab = findViewById(R.id.floatingActionButton);
        dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your task, Please Wait....");
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
            fragment = new PostTaskDetail();
//            fragment = new OrdersFrag();
            loadFragment(fragment);
            //Load frag
        } else if (currentpage == 2) {
            fragment = new TaskAdditional();
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
        dlg.show();
        //result.setText("sending");
        Date date = new Date();
        Task task = new Task(
                date.getTime(),
                PostTaskDetail.description.getText().toString(),
                PostTaskDetail.title.getText().toString(),
                2500,
                "mumbai",
                "rakesh",
                PostCategory.category,
                "videos",
                "time",
                new double[]{25.0,25.0},
                new String[]{"tech","null"},
                new ArrayList<>()
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_POST_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Task> call = jsonPlaceHolder.createTask(task);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {

                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Log.e("error",response.toString());
                    return;
                }
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("error",t.getMessage());
                dlg.dismiss();
            }
        });
    }
}