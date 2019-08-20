package com.example.mytasker.models;

import com.google.gson.annotations.SerializedName;

public class User extends Message{
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("contact")
    private String contact;
    @SerializedName("token")
    private String token;
    @SerializedName("password")
    private String password;
    @SerializedName("new-password")
    private String newPassword;

    public int getChangeType() {
        return changeType;
    }

    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }

    @SerializedName("change-type")
    private int changeType;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public User(String firstName, String lastName, String contact, String token, String password, String newPassword, int changeType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.token = token;
        this.password = password;
        this.newPassword = newPassword;
        this.changeType = changeType;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
