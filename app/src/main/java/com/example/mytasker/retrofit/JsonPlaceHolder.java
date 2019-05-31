package com.example.mytasker.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolder {


    @GET("task-feed")
    Call<TaskDetail> getDetail(
            @Query("loc") Integer[] loc,
            @Query("radius") Integer radius,
            @Query("tags") String[] tags
    ) ;
}
