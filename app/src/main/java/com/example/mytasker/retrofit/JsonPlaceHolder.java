package com.example.mytasker.retrofit;

import com.example.mytasker.models.Message;
import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolder {

    @POST("createTask")
    Call<Task> createTask(@Body Task task);

    @POST("createQues")
    Call<Question> createQuestion(@Body Question question);

    @POST("assignTask")
    Call<Message> assignTask(@Body Map map);

    @GET("task-feed")
    Call<RetrofitParser> getTasks(@Query("lat") double lat,
                                  @Query("lon") double lon,
                                  @Query("radius") Integer radius,
                                  @Query("tags") ArrayList<String> tags
    );


    @GET("ques-feed")
    Call<RetrofitParser> getQuestions(@Query("lat") double lat,
                                      @Query("lon") double lon);
}
