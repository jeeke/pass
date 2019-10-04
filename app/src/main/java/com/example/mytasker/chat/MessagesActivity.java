package com.example.mytasker.chat;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mytasker.R;
import com.example.mytasker.chat.model.DialogHelper;
import com.example.mytasker.chat.model.Message;
import com.example.mytasker.chat.model.MessageHelper;
import com.example.mytasker.util.Tools;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.mytasker.util.Contracts.getPushKey;

public class MessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.TypingListener {

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Tools.initMinToolbar(this, super.mChatUName);
        this.messagesList = findViewById(R.id.messagesList);
        initAdapter();
        mRootRef.child("Messages").child(mCurrentUser.getUid()).
                child(mChatUId).orderByChild("createdAt").startAt(new Date().getTime() + "").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(MessageHelper.class).toMessage();
                messagesAdapter.addToStart(message, true);
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
        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        String uid, name, avatar;
        uid = mCurrentUser.getUid();
        name = mCurrentUser.getDisplayName();
        avatar = mCurrentUser.getPhotoUrl().toString();
        String current_user_ref = "Messages/" + uid + "/" + mChatUId;
        String chat_user_ref = "Messages/" + mChatUId + "/" + uid;
        Map<String, Object> messageUserMap = new HashMap<>();

        if (messagesAdapter.getItemCount() == 0) {
            MessageHelper messageHelper1 = new MessageHelper(mCurrentUser, "Welcome to Pass!");
            messageUserMap.put(current_user_ref + "/" +
                    getPushKey(mRootRef.child(current_user_ref)), messageHelper1.toMap());
            messageUserMap.put(chat_user_ref + "/" +
                    getPushKey(mRootRef.child(current_user_ref)), messageHelper1.toMap());
        }
        MessageHelper messageHelper = new MessageHelper(mCurrentUser, input.toString());
        DatabaseReference user_message_push = mRootRef.child(current_user_ref);
        user_message_push = user_message_push.child(getPushKey(user_message_push));
        String push_id = user_message_push.getKey();
        DialogHelper dialogHelperMe = new DialogHelper(mChatUId, mChatUName, mChatAvatar, 0, null, input.toString());
        DialogHelper dialogHelperHim = new DialogHelper(uid, name, avatar, 0, null, input.toString());
        //TODO update and make all the queries in one by putting them in mUpdatemap
        messageUserMap.put(current_user_ref + "/" + push_id, messageHelper.toMap());
        messageUserMap.put(chat_user_ref + "/" + push_id, messageHelper.toMap());
        messageUserMap.put("Chats/" + uid + "/" + mChatUId, dialogHelperMe.toMap());
        messageUserMap.put("Chats/" + mChatUId + "/" + uid, dialogHelperHim.toMap());
        mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                Log.d("CHAT_LOG", databaseError.getMessage());
//                messagesAdapter.addToStart(messageHelper.toMessage(),true);
            }
        });
        return true;
    }

    private void initAdapter() {
        super.messagesAdapter = new MessagesListAdapter<>(mCurrentUser.getUid(), super.imageLoader);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        this.messagesList.setAdapter(super.messagesAdapter);
    }

    @Override
    public void onStartTyping() {
        Log.v("Typing listener", getString(R.string.start_typing_status));
    }

    @Override
    public void onStopTyping() {
        Log.v("Typing listener", getString(R.string.stop_typing_status));
    }
}
