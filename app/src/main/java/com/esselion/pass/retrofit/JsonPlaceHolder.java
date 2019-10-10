package com.esselion.pass.retrofit;

import com.esselion.pass.models.Message;
import com.esselion.pass.models.Question;
import com.esselion.pass.models.Task;

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

    @POST("deleteTask")
    Call<Message> deleteTask(@Query("tid") String tid,
                             @Query("c_date") long c_date,
                             @Query("category") String category);

    @POST("deleteQues")
    Call<Message> deleteQues(@Query("qid") String qid,
                             @Query("c_date") long c_date);

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
