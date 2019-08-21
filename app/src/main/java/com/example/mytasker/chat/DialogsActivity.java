package com.example.mytasker.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mytasker.R;
import com.example.mytasker.chat.data.model.Dialog;
import com.example.mytasker.chat.data.model.DialogHelper;
import com.example.mytasker.chat.data.model.Message;
import com.example.mytasker.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;

public class DialogsActivity extends DemoDialogsActivity {

    private DatabaseReference mConvDatabase;
//    private DatabaseReference mMessageDatabase;
//    private DatabaseReference mUsersDatabase;


    public static void open(Context context) {
        context.startActivity(new Intent(context, DialogsActivity.class));
    }

    private DialogsList dialogsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);
        Tools.initMinToolbar(this,"CHATS",false);
        dialogsList = findViewById(R.id.dialogsList);
        initAdapter();


        //TODO Firebase rules
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrent_user_id);
        mConvDatabase.keepSynced(true);
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
//        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrent_user_id);
//        mUsersDatabase.keepSynced(true);
        queryFireBase();

    }

    private void queryFireBase(){
        Query conversationQuery = mConvDatabase.orderByChild("lastActivity");
        conversationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Dialog dialog = dataSnapshot.getValue(DialogHelper.class).toDialog();
                onNewDialog(dialog);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        MessagesActivity.open(this);
    }

    private void initAdapter() {
        super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
        super.dialogsAdapter.setItems(new ArrayList<>());
        super.dialogsAdapter.setOnDialogClickListener(this);
        super.dialogsAdapter.setOnDialogLongClickListener(this);
        dialogsList.setAdapter(super.dialogsAdapter);
    }

    //for example
    private void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

    //for example
    private void onNewDialog(Dialog dialog) {
        dialogsAdapter.addItem(dialog);
    }
}
