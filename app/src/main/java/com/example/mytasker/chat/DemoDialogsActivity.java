package com.example.mytasker.chat;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytasker.activities.BaseActivity;
import com.example.mytasker.chat.data.model.Dialog;
import com.google.firebase.database.ServerValue;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.jetbrains.annotations.Nullable;

import static com.example.mytasker.util.Tools.setOnline;

public abstract class DemoDialogsActivity extends BaseActivity
        implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnline(this, "true");
        imageLoader = (imageView, url, payload) -> Glide.with(DemoDialogsActivity.this).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setOnline(this, ServerValue.TIMESTAMP);
    }

}
