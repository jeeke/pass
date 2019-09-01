package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.models.Question;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.util.Tools;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.mytasker.util.Cache.getToken;
import static com.example.mytasker.util.Cache.getUser;
import static com.example.mytasker.util.Tools.getRetrofit;

public class PostQuestion extends BaseActivity {

    Button fab;
    ProgressDialog dlg;
    TextView ques;
    private String q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ques);
        Tools.initMinToolbar(this, "Post Question", false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> verifyNCall());
        ques = findViewById(R.id.question);
        dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your question, Please Wait....");
    }

    private boolean prevCallResolved = true;

    private void verifyNCall() {
        q = ques.getText().toString();
        if (q.equals("")) {
            Toast.makeText(this, "Please enter your question", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!prevCallResolved) return;
        getToken(this::postQuestion);
    }

    public void postQuestion(String token) {
        dlg.show();
        //result.setText("sending");
        ArrayList<Double> loc = new ArrayList<>();
        loc.add(25.0);
        loc.add(25.0);

        FirebaseUser user = getUser();
        Date date = new Date();
        Question question = new Question(
                date.getTime(),
                q,
                user.getUid(),
                user.getDisplayName(),
                user.getPhotoUrl().toString(),
                loc
        );
        Retrofit retrofit = getRetrofit(token);
        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Question> call = jsonPlaceHolder.createQuestion(question);
        call.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                prevCallResolved = true;
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
                prevCallResolved = true;
                Log.e("error", t.getMessage());
                dlg.dismiss();
            }
        });
        prevCallResolved = false;

    }
}