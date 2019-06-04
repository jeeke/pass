package com.example.mytasker.retrofit;

import com.example.mytasker.models.IndividualTask;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolder {


    @POST("verified-task")
    Call<IndividualTask> createDetail(@Body IndividualTask task);

    @GET("task-feed")
    Call<TaskDetail> getDetail(
            @Query("loc") Integer[] loc,
            @Query("radius") Integer radius,
            @Query("tags") String[] tags
    ) ;
}
