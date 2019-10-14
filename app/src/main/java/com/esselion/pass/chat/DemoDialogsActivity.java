package com.esselion.pass.chat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esselion.pass.R;
import com.esselion.pass.activities.BaseActivity;
import com.esselion.pass.chat.model.Dialog;
import com.esselion.pass.chat.model.DialogSelectionAdapter;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.esselion.pass.util.Cache.getDatabase;
import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.launchActivity;
import static com.esselion.pass.util.Tools.showSnackBar;

public abstract class DemoDialogsActivity extends BaseActivity
        implements DialogsListAdapter.OnDialogViewClickListener<Dialog>,
        DialogsListAdapter.OnDialogViewLongClickListener<Dialog> {

    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;
    public DialogsList dialogsList;
    Set<Dialog> selectedDialogs = new HashSet<>();
    ArrayList<Dialog> items;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = (imageView, url, payload) -> Glide.with(DemoDialogsActivity.this).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
        dialogsAdapter = new DialogSelectionAdapter(imageLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        onSelectionChanged();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            deleteSelectedItems();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSelectionChanged() {
        menu.findItem(R.id.action_delete).setVisible(selectedDialogs.size() > 0);
    }

    @Override
    public void onBackPressed() {
        if (selectedDialogs.size() == 0) {
            super.onBackPressed();
        } else {
            unSelectAllItems();
            onSelectionChanged();
        }
    }


    private void deleteSelectedItems() {
        try {
            if (selectedDialogs.size() > 0) {
                Map<String, Object> u = new HashMap<>();
                String rootRef1 = "Chats/" + getUser().getUid() + "/";
                String rootRef2 = "Messages/" + getUser().getUid() + "/";
                for (Dialog d : selectedDialogs) {
                    u.put(rootRef1 + d.getId(), null);
                    u.put(rootRef2 + d.getId(), null);
                }
                showProgressBar(true);
                callFireBase(u);
                unSelectAllItems();
                onSelectionChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callFireBase(Map<String, Object> map) {
        try {
            getDatabase().updateChildren(map).addOnCompleteListener(task -> {
                showProgressBar(false);
                if (task.isSuccessful())
                    showSnackBar(DemoDialogsActivity.this, "Chats Deleted Successfully");
                else
                    showSnackBar(DemoDialogsActivity.this, "Couldn't be deleted", () -> callFireBase(map));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void unSelectAllItems() {
        try {

            for (Dialog d : selectedDialogs) d.unSelect();
            dialogsAdapter = new DialogSelectionAdapter(imageLoader);
            initAdapter();
            selectedDialogs.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void initAdapter() {
        try {

            dialogsAdapter.setItems(items);
            dialogsAdapter.setOnDialogViewClickListener(this);
            dialogsAdapter.setOnDialogViewLongClickListener(this);
            dialogsList.setAdapter(dialogsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogViewLongClick(View view, Dialog dialog) {
        view.setBackgroundColor(getResources().getColor(R.color.blue_50));
        dialog.select();
        selectedDialogs.add(dialog);
        onSelectionChanged();
    }

    @Override
    public void onDialogViewClick(View view, Dialog dialog) {
        try {
            if (selectedDialogs.size() == 0) {
                Intent intent = new Intent(this, MessagesActivity.class);
                intent.putExtra("id", dialog.getId());
                intent.putExtra("name", dialog.getDialogName());
                intent.putExtra("avatar", dialog.getDialogPhoto());
                launchActivity(DemoDialogsActivity.this, intent);
            } else {
                if (selectedDialogs.contains(dialog)) {
                    view.setBackgroundColor(Color.parseColor("#ffffff"));
                    dialog.unSelect();
                    selectedDialogs.remove(dialog);
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.blue_50));
                    dialog.select();
                    selectedDialogs.add(dialog);
                }
                onSelectionChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
