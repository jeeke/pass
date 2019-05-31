package com.example.mytasker.retrofit;

import com.google.gson.annotations.SerializedName;

public class IndividualTask {
    @SerializedName("c_date")
    String c_date;
    @SerializedName("poster_id")
    String poster_id;
    @SerializedName("job_des")
    String job_des;
    @SerializedName("dis")
    String dis;
    @SerializedName("tasker_id")
    String tasker_id;
    @SerializedName("title")
    String title;
    @SerializedName("cost")
    String cost;

    public String getCost() {
        return cost;
    }

    public String getTitle() {
        return title;
    }

    public String getTasker_id() {
        return tasker_id;
    }

    public String getDis() {
        return dis;
    }
    public String getJob_des() {
        return job_des;
    }

    public String getC_date() {

        return c_date;
    }

    public String getPoster_id() {
        return poster_id;
    }
}
