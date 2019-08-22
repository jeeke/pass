package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.badoualy.stepperindicator.StepperIndicator;
import com.example.mytasker.R;
import com.example.mytasker.fragments.PostQuesCat;
import com.example.mytasker.fragments.PostQuesDetail;
import com.example.mytasker.models.Question;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Tools.getRetrofit;

public class PostQuestion extends BaseActivity {

    StepperIndicator indicator;
    FloatingActionButton fab;
    int currentpage = 0;
    Fragment fragment;
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
        indicator.setStepCount(2);
        fragment = new PostQuesCat();
        loadFragment(fragment);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Post Question");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        fab = findViewById(R.id.floatingActionButton);
        dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your question, Please Wait....");
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    public void loadNextFrag(View view) {
        if (currentpage < 2) {
            indicator.setCurrentStep(++currentpage);
        }
        if (currentpage == 1) {
            fragment = new PostQuesDetail();
//            fragment = new OrdersFrag();
            loadFragment(fragment);
            AnimatedVectorDrawable icon = (AnimatedVectorDrawable) fab.getDrawable();
            icon.start();
        } else {
            //done
            verifyNCall();
        }
    }

    private void verifyNCall() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postQuestion(task.getResult().getToken());
                    } else {
                        // Handle error -> task.getException();
                        Toast.makeText(this, "Authentication Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void postQuestion(String token) {
        dlg.show();
        //result.setText("sending");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Date date = new Date();
        Question question = new Question(
                date.getTime(),
                ((PostQuesDetail) fragment).getQuestion(),
                user.getUid(),
                user.getDisplayName(),
                null,
                null,
                new double[]{25.0, 25.0},
                new String[]{"tech", "null"}
        );
        Retrofit retrofit = getRetrofit(token);

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Question> call = jsonPlaceHolder.createQuestion(question);
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {

                dlg.dismiss();
                Log.e("error", response.toString());
                if (!response.isSuccessful()) {
                    return;
                }

                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.e("error", t.getMessage());
                dlg.dismiss();
            }
        });
    }
}