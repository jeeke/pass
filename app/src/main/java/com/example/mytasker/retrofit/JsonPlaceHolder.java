package com.example.mytasker.retrofit;

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
    ) ;

    @GET("ques-feed")
    Call<QuestionList> getQuestions(
            @Query("loc") double[] loc
//            @Query("radius") Integer radius,
//            @Query("tags") String[] tags
    ) ;

    @GET("notifications")
    Call<NotificationList> getNotifications(
            @Query("id") String id
    ) ;

    @GET("prev-post")
    Call<PrevPostModel> getPrevTask(
            @Query("id") String id,
            @Query("index") int index
    );


    @GET("profile")
    Call<Profile> getProfile(
    );
}
