package com.example.mytasker.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mytasker.R;
import com.example.mytasker.activities.BaseActivity;
import com.example.mytasker.chat.data.model.DialogHelper;
import com.example.mytasker.chat.data.model.Message;
import com.example.mytasker.chat.data.model.MessageHelper;
import com.example.mytasker.chat.utils.AppUtils;
import com.example.mytasker.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessagesActivity extends BaseActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener ,
        MessageInput.TypingListener {

    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;


    private Menu menu;
    private int selectionCount;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MessagesActivity.class));
    }

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = (imageView, url, payload) -> Picasso.with(this).load(url).into(imageView);
        initFireBase();
        onLoadMore(1,10);

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
        MessageHelper messageHelper = new MessageHelper(mAuth.getCurrentUser(),input.toString());
        Message message = messageHelper.toMessage();
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
        DialogHelper dialogHelper = new DialogHelper(mChatUId,"rakesh",mAuth.getCurrentUser().getPhotoUrl().toString(),0,null,input.toString());
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
        messagesAdapter = new MessagesListAdapter<>(mCurrentUserId, imageLoader);
        messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                (view, message) -> AppUtils.showToast(MessagesActivity.this,
                        message.getUser().getName() + " avatar click",
                        false));
        this.messagesList.setAdapter(messagesAdapter);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
        AppUtils.showToast(this, R.string.copied_message, true);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }


    private boolean firstLoad = true;
    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        loadMessages();
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }


    protected DatabaseReference mRootRef;
    protected String mCurrentUserId;
    protected String mChatUId;
    protected String mChatAvatar;
    protected String mChatUName;
    protected FirebaseAuth mAuth;

    private void initFireBase() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mChatUId = getIntent().getStringExtra("id");
        mChatUName = getIntent().getStringExtra("name");
        mChatAvatar = getIntent().getStringExtra("avatar");

//        TODO online indicator to true

    }

    String mKey = "";
    private void loadMessages() {
        DatabaseReference messageRef;
        Query messageQuery;
        if (firstLoad) {
            messageRef = mRootRef.child("Messages").child(mCurrentUserId).child(mChatUId);
            messageQuery = messageRef.orderByChild("createdAt").limitToFirst(20);
            firstLoad = false;
        } else {
            messageRef = mRootRef.child("Messages").child(mCurrentUserId).child(mChatUId);
            messageQuery = messageRef.orderByChild("createdAt").startAt(mKey).limitToFirst(10);
        }
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageHelper message = dataSnapshot.getValue(MessageHelper.class);
//                List<Message> messages= new ArrayList<>();
//                messages.add(message.toMessage());
                messagesAdapter.addToStart(message.toMessage(), true);
                DialogHelper dialogHelper = new DialogHelper(mChatUId,"rakesh",mAuth.getCurrentUser().getPhotoUrl().toString(),0,null,message.getText());
                mRootRef.child("Chats").child(mCurrentUserId).child(mChatUId).setValue(dialogHelper.toMap());
//                TODO updaate seen value
//                dataSnapshot.child("status").getRef().setValue(Message.MESSAGE_SEEN);
//                mRootRef.child("Messages").child(mChatUId).child(mCurrentUserId).child("status").setValue(Message.MESSAGE_SEEN);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return message -> {
            String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(message.getCreatedAt());

            String text = message.getText();
            if (text == null) text = "[attachment]";

            return String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.getUser().getName(), text, createdAt);
        };
    }
}
