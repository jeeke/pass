package com.example.mytasker.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mytasker.R;
import com.example.mytasker.chat.data.model.Message;
import com.example.mytasker.chat.data.model.User;
import com.example.mytasker.chat.utils.AppUtils;
import com.example.mytasker.util.Tools;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener {

    public static void open(Context context) {
        context.startActivity(new Intent(context, MessagesActivity.class));
    }

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Tools.initMinToolbar(this, "Someone Pandey", false);

        this.messagesList = findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        User user = new User(mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getPhotoUrl().toString(),"false");
        Message message = new Message(null,user,input.toString());
        super.messagesAdapter.addToStart(
                message, true);
        String current_user_ref = "Messages/" + mCurrentUserId + "/" + mChatUser;
        String chat_user_ref = "Messages/" + mChatUser + "/" + mCurrentUserId;

        DatabaseReference user_message_push = mRootRef.child("Messages")
                .child(mCurrentUserId).child(mChatUser).push();
        String push_id = user_message_push.getKey();

        Map messageMap = new HashMap();
        messageMap.put("text", input.toString());
        messageMap.put("seen", false);
        messageMap.put("createdAt", ServerValue.TIMESTAMP);
        messageMap.put("from", mCurrentUserId);

        Map messageUserMap = new HashMap();
        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

//            mChatMessageView.setText("");

        mRootRef.child("Chats").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
        mRootRef.child("Chats").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

        mRootRef.child("Chats").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
        mRootRef.child("Chats").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

        mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {

            if(databaseError != null){
                Log.d("CHAT_LOG", databaseError.getMessage());
            }

        });
        return true;
    }

    private void initAdapter() {
        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, super.imageLoader);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        super.messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                (view, message) -> AppUtils.showToast(MessagesActivity.this,
                        message.getUser().getName() + " avatar click",
                        false));
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


    @Override
    public void onAddAttachments() {

    }
}
