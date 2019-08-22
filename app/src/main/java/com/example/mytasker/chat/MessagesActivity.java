package com.example.mytasker.chat;

import android.os.Bundle;
import android.util.Log;

import com.example.mytasker.R;
import com.example.mytasker.chat.data.model.DialogHelper;
import com.example.mytasker.chat.data.model.MessageHelper;
import com.example.mytasker.chat.utils.AppUtils;
import com.example.mytasker.util.Tools;
import com.google.firebase.database.DatabaseReference;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.TypingListener {

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Tools.initMinToolbar(this, super.mChatUName, false);

        this.messagesList = findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        MessageHelper messageHelper = new MessageHelper(mAuth.getCurrentUser(),input.toString());
//        Message message = messageHelper.toMessage();
//        super.messagesAdapter.addToStart(
//                message, true);
        String current_user_ref = "Messages/" + mCurrentUserId + "/" + mChatUId;
        String chat_user_ref = "Messages/" + mChatUId + "/" + mCurrentUserId;

        DatabaseReference user_message_push = mRootRef.child("Messages")
                .child(mCurrentUserId).child(mChatUId).push();
        String push_id = user_message_push.getKey();
        Map messageUserMap = new HashMap();
        messageUserMap.put(current_user_ref + "/" + push_id, messageHelper.toMap());
        messageUserMap.put(chat_user_ref + "/" + push_id, messageHelper.toMap());
//        TODO add chat user name,avatar
        DialogHelper dialogHelper = new DialogHelper(mChatUId,mChatUName,mChatAvatar,0,null,input.toString());
        mRootRef.child("Chats").child(mCurrentUserId).child(mChatUId).setValue(dialogHelper.toMap());
//        mRootRef.child("Chats").child(mCurrentUserId).child(mChatUId).child("lastMessage").setValue(message.toMap());
//        TODO update lastMessage
//        mRootRef.child("Chats").child(mChatUId).child(mCurrentUserId).child("lastActivity").setValue(ServerValue.TIMESTAMP);
        mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
            if(databaseError != null){
                Log.d("CHAT_LOG", databaseError.getMessage());
            }

        });
        return true;
    }

    private void initAdapter() {
        super.messagesAdapter = new MessagesListAdapter<>(mCurrentUserId, super.imageLoader);
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
}
