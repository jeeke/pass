package com.esselion.pass.models;

import androidx.annotation.Keep;

import java.util.ArrayList;

@Keep
public class Profile extends Message {


    private Rating byTasker, byPoster;
    private long t_done;
    private ArrayList<String> skills;
    private String about;

    Profile() {
        t_done = 0;
        bucks = 0;
        t_posted = 0;
        skills = new ArrayList<>();
        about = "A Hobbyist";

    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setByTasker(Rating byTasker) {
        if (byTasker == null) this.byTasker = new Rating();
        else this.byTasker = byTasker;
    }

    private int bucks;
    private long t_posted;

    public void setByPoster(Rating byPoster) {
        if (byPoster == null) this.byPoster = new Rating();
        else this.byPoster = byPoster;
    }

    public float getPosterRating() {
        if (byTasker.task_rated != 0)
            return (byTasker.r1 + byTasker.r2 + byTasker.r3) / (3 * byTasker.task_rated);
        return 0;
    }

    public float getTaskerRating() {
        if (byPoster.task_rated != 0)
            return (byPoster.r1 + byPoster.r2 + byPoster.r3) / (3 * byPoster.task_rated);
        return 0;
    }

    public int getR1() {
        if (byPoster.task_rated != 0) {
            return (int) (byPoster.r1 * 20 / byPoster.task_rated);
        }
        return 0;
    }

    public int getR3() {
        if (byPoster.task_rated != 0) {
            return (int) (byPoster.r3 * 20 / byPoster.task_rated);
        }
        return 0;
    }

    public int getR2() {
        if (byPoster.task_rated != 0) {
            return (int) (byPoster.r2 * 20 / byPoster.task_rated);
        }
        return 0;
    }

    public void addSkill(String skill) {
        this.skills.add(skill);
    }


    public long getT_posted() {
        return t_posted;
    }

    public void setT_posted(long t_posted) {
        this.t_posted = t_posted;
    }

    public int getBucks() {
        return bucks;
    }

    public void setBucks(int bucks) {
        this.bucks = bucks;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public long getT_done() {
        return t_done;
    }

    public void setT_done(long t_done) {
        this.t_done = t_done;
    }
}
