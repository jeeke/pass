package com.esselion.pass.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.esselion.pass.R;
import com.esselion.pass.chat.model.Dialog;
import com.esselion.pass.chat.model.DialogHelper;
import com.esselion.pass.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DialogsActivity extends DemoDialogsActivity {

    private DatabaseReference mConvDatabase;

    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);
        Tools.initMinToolbar(this, "CHATS");
        bar = findViewById(R.id.progress_bar);
        dialogsList = findViewById(R.id.dialogsList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bar.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrent_user_id);
        mConvDatabase.keepSynced(true);
        queryFireBase();
    }

    private void queryFireBase() {
        Query conversationQuery = mConvDatabase.orderByChild("lastActivity");
        conversationQuery.
                addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Dialog> dialogs = new ArrayList<>();
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    dialogs.add(data.getValue(DialogHelper.class).toDialog());
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
