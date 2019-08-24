package com.example.mytasker.retrofit;

import com.example.mytasker.models.Answer;
import com.example.mytasker.models.Bid;
import com.example.mytasker.models.Message;
import com.example.mytasker.models.Profile;
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
    Call<RetrofitParser> getTasks(
            @Query("loc") double[] loc,
            @Query("radius") Integer radius,
            @Query("tags") ArrayList<String> tags
    );


    @GET("bids-list")
    Call<ArrayList<Bid>> getBids(@Query("task_id") String id);

    @GET("answer-list")
    Call<ArrayList<Answer>> getAnswers(@Query("ques_id") String id);

    @GET("ques-feed")
    Call<RetrofitParser> getQuestions(@Query("loc") double[] loc,
                                      @Query("radius") Integer radius,
                                      @Query("tags") ArrayList<String> tags);

    @GET("notifications")
    Call<NotificationList> getNotifications();

    @POST("add-skill")
    Call<Profile> addskill(@Query("skill") String string);

    @POST("answer")
    Call<Message> submitAnswer(@Body Answer answer,
                               @Query("ques_id") String id);

    @POST("feedback")
    Call<Message> feedback(@Query("rating") float rating,
                           @Query("tasker_id") String t_id,
                           @Query("poster_id") String p_id,
                           @Query("task_id") String id
    );
}
