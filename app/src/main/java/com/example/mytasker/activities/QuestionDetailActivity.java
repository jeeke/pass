package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytasker.R;
import com.example.mytasker.adapters.AnswerAdapter;
import com.example.mytasker.models.Answer;
import com.example.mytasker.models.Message;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionDetailActivity extends BaseActivity {

    public static boolean FROM = false;
    AnswerAdapter adapter;
    EditText answer;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        Tools.initMinToolbar(this,"Answers",false);
        RecyclerView listView = findViewById(R.id.recyclerView);
        adapter = new AnswerAdapter(new ArrayList<>());
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        callRetrofit();
        fab = findViewById(R.id.submit_answer);
        answer = findViewById(R.id.editText);
        fab.setOnClickListener(v -> {
            if(answer.getText().toString().equals(""))
                Toast.makeText(QuestionDetailActivity.this, "Enter valid answer", Toast.LENGTH_SHORT).show();
            else{
                callRetrofit2();
            }
        });
    }

    private void callRetrofit2() {

        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your Answer..");
        dlg.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_POST_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Answer ans = new Answer("rakesh","Rakesh Pandey",answer.getText().toString());

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Message> call = jsonPlaceHolder.submitAnswer(ans,"1");
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Log.e("error",response.toString());
                    Toast.makeText(QuestionDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                    return;
                }
                Toast.makeText(QuestionDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                adapter.addItem(ans);
                answer.setText("");
                closeKeyboard();
            }


            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("error",t.getMessage());
                Toast.makeText(QuestionDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                dlg.dismiss();
            }
        });
    }


    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void callRetrofit() {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Getting Your Questions, Please Wait....");
        dlg.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_GET_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
//        Call<ArrayList<Answer>> call = jsonPlaceHolder.historyQues("rakesh");
        Call<ArrayList<Answer>> call = jsonPlaceHolder.getAnswers("question");
        call.enqueue(new Callback<ArrayList<Answer>>() {
            @Override
            public void onResponse(Call<ArrayList<Answer>> call, Response<ArrayList<Answer>> response) {
                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Toast.makeText(QuestionDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.update(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Answer>> call, Throwable t) {
                Toast.makeText(QuestionDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }
}
