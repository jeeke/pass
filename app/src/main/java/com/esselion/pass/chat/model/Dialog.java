package com.esselion.pass.chat.model;

import com.stfalcon.chatkit.commons.models.IDialog;

import java.util.ArrayList;

public class Dialog implements IDialog<Message> {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<User> users;
    private Message lastMessage;
    private int unreadCount;
    private boolean isSelected = false;

    Dialog() {
    }

    Dialog(String id, String dialogPhoto, String dialogName, int unreadCount) {
        this.id = id;
        this.dialogPhoto = dialogPhoto;
        this.dialogName = dialogName;
        this.unreadCount = unreadCount;
    }

    public boolean getSelection() {
        return isSelected;
    }

    public void select() {
        isSelected = true;
    }

    public void unSelect() {
        isSelected = false;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return -1;
    }
}