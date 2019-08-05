package com.example.mytasker.retrofit;

import com.example.mytasker.models.PrevTaskModel;
import com.example.mytasker.models.Question;
import com.example.mytasker.models.Task;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolder {


    @POST("raw-task")
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
    ) ;

    @GET("ques-feed")
    Call<QuestionList> getQuestions(
            @Query("loc") double[] loc,
            @Query("radius") Integer radius,
            @Query("tags") String[] tags
    ) ;

    @GET("notifications")
    Call<NotificationList> getNotifications(
            @Query("id") String id
    ) ;

    @GET("task-prev")
    Call<PrevTaskModel> getPrevTask(
            @Query("id") String id
    );
}
