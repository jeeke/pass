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
        String uid, name, avatar;
        uid = mCurrentUser.getUid();
        name = mCurrentUser.getDisplayName();
        avatar = mCurrentUser.getPhotoUrl().toString();
        MessageHelper messageHelper = new MessageHelper(mCurrentUser, input.toString());
        String current_user_ref = "Messages/" + uid + "/" + mChatUId;
        String chat_user_ref = "Messages/" + mChatUId + "/" + uid;
        DatabaseReference user_message_push = mRootRef.child(current_user_ref).push();
        String push_id = user_message_push.getKey();
        Map messageUserMap = new HashMap();

        DialogHelper dialogHelperMe = new DialogHelper(mChatUId, mChatUName, mChatAvatar, 0, null, input.toString());
        DialogHelper dialogHelperHim = new DialogHelper(uid, name, avatar, 0, null, input.toString());
        //TODO update and make all the queries in one by putting them in mUpdatemap
        messageUserMap.put(current_user_ref + "/" + push_id, messageHelper.toMap());
        messageUserMap.put(chat_user_ref + "/" + push_id, messageHelper.toMap());
//        messageUserMap.put("Chats/" + uid + "/" + mChatUId,dialogHelperMe.toMap());
//        messageUserMap.put("Chats/" + mChatUId + "/" + uid,dialogHelperHim.toMap());
        mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
            if(databaseError != null){
                Log.d("CHAT_LOG", databaseError.getMessage());
            }
        });
        return true;
    }

    private void initAdapter() {
        super.messagesAdapter = new MessagesListAdapter<>(mCurrentUser.getUid(), super.imageLoader);
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
