package com.example.mytasker.retrofit;

import com.example.mytasker.models.Answer;
import com.example.mytasker.models.Bid;
import com.example.mytasker.models.Feed;
import com.example.mytasker.models.Message;
import com.example.mytasker.models.PrevPostModel;
import com.example.mytasker.models.Profile;
import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolder {


    @POST("task/create")
    Call<Task> createTask(@Body Task task);

    @POST("raw-ques")
    Call<Question> createQuestion(@Body Question question);

    @GET("task-feed")
    Call<TaskList> getTasks(
            @Query("loc") double[] loc,
            @Query("radius") Integer radius,
            @Query("tags") ArrayList<String> tags,
            @Query("price") int[] price,
            @Query("remoteTask") boolean remoteTask
    );

    @GET("bids-list")
    Call<ArrayList<Bid>> getBids(@Query("task_id") String id);

    @GET("answer-list")
    Call<ArrayList<Answer>> getAnswers(@Query("ques_id") String id);

    @GET("ques-feed")
    Call<QuestionList> getQuestions(@Query("loc") double[] loc);

    @GET("notifications")
    Call<NotificationList> getNotifications();

    @GET("prev-post")
    Call<PrevPostModel> getPrevTask();

    @GET("prev-ques")
    Call<QuestionList> historyQues();

    //TODO add token in all request

    @GET("profile")
    Call<Profile> getProfile();

    @POST("add-skill")
    Call<Profile> addskill(@Query("skill") String string);

    @POST("bid")
    Call<Message> confirmBid(@Body Bid bid,
                             @Query("task_id") String id);

    @POST("answer")
    Call<Message> submitAnswer(@Body Answer answer,
                               @Query("ques_id") String id);

    @POST("feedback")
    Call<Message> feedback(@Query("rating") float rating,
                           @Query("tasker_id") String t_id,
                           @Query("poster_id") String p_id,
                           @Query("task_id") String id
    );

    @POST("create-feed")
    Call<Feed> createFeed(@Body Feed feed);
}
