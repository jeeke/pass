package com.example.mytasker.models;

public class Rating {
    float r1;
    float r2;
    float r3;
    int task_rated;

    public void setR1(float r1) {
        this.r1 = r1;
    }

    public void setR2(float r2) {
        this.r2 = r2;
    }

    public void setR3(float r3) {
        this.r3 = r3;
    }

    public void setTask_rated(int task_rated) {
        this.task_rated = task_rated;
    }


    public Rating() {
        r1 = 0;
        r2 = 0;
        r3 = 0;
        task_rated = 0;
    }
}
