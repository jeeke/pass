package com.example.mytasker.chat;

import android.os.Bundle;

import com.example.mytasker.activities.BaseActivity;
import com.example.mytasker.chat.data.model.Dialog;
import com.example.mytasker.chat.utils.AppUtils;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.jetbrains.annotations.Nullable;

public abstract class DemoDialogsActivity extends BaseActivity
        implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = (imageView, url, payload) -> Picasso.with(DemoDialogsActivity.this).load(url).into(imageView);
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        AppUtils.showToast(
                this,
                dialog.getDialogName(),
                false);
    }
}
