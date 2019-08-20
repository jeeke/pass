package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.models.Message;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedbackByTaskerActivity extends BaseActivity {

    RatingBar price,clearity,behaviour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_tasker);
        Tools.initMinToolbar(this,"Confirm Bid",false);
        findViewById(R.id.submit).setOnClickListener(v -> checkFields());
        initFields();
    }

    private void initFields(){
        price = findViewById(R.id.ratingBar);
        clearity = findViewById(R.id.ratingBar2);
        behaviour = findViewById(R.id.ratingBar3);
    }

    private float calculateRat(float a,float b,float c){
        return (a*3 + (b+c)*2)/7.0f;
    }

    private  void checkFields(){
        float a =  price.getRating();
        float b =  clearity.getRating();
        float c =  behaviour.getRating();
        if( a*b*c==0.0f){
            Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
        }else {
            callRetrofit(calculateRat(a,b,c));
        }
    }

    private void callRetrofit(float rating) {

        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your Bid..");
        dlg.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_POST_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<Message> call = jsonPlaceHolder.feedback(rating,"rakesh","id","id");
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Log.e("error",response.toString());
                    return;
                }
                Toast.makeText(FeedbackByTaskerActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("error",t.getMessage());
                dlg.dismiss();
            }
        });
    }
}
