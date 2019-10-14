package com.esselion.pass.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.esselion.pass.R;
import com.esselion.pass.chat.model.Dialog;
import com.esselion.pass.chat.model.DialogHelper;
import com.esselion.pass.util.Tools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.esselion.pass.util.Cache.getUser;

public class DialogsActivity extends DemoDialogsActivity {

    private DatabaseReference mConvDatabase;

    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dialogs);
            Tools.initMinToolbar(this, "CHATS");
            bar = findViewById(R.id.progress_bar);
            dialogsList = findViewById(R.id.dialogsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();
            bar.setVisibility(View.VISIBLE);
            String mCurrent_user_id = getUser().getUid();
            mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrent_user_id);
            mConvDatabase.keepSynced(true);
            queryFireBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryFireBase() {
        try {
            Query conversationQuery = mConvDatabase.orderByChild("lastActivity");
            conversationQuery.
                    addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArrayList<Dialog> dialogs = new ArrayList<>();
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        DialogHelper helper = data.getValue(DialogHelper.class);
                                        if (helper != null) {
                                            dialogs.add(helper.toDialog());
                                        }
                                    }
                                    bar.setVisibility(View.GONE);
                                    if (dialogs.isEmpty()) {
                                        dialogsList.setVisibility(View.GONE);
                                        findViewById(R.id.anim).setVisibility(View.VISIBLE);
                                    } else {
                                        dialogsList.setVisibility(View.VISIBLE);
                                        items = dialogs;
                                        initAdapter();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //for example
//    private void onNewMessage(String dialogId, Message message) {
//        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(dialogId, message);
//        if (!isUpdated) {
//            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
//        }
//    }

    //for example
    private void onNewDialog(Dialog dialog) {
        dialogsAdapter.addItem(dialog);
    }
}
