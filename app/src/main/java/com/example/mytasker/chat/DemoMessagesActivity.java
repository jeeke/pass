package com.example.mytasker.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.example.mytasker.R;
import com.example.mytasker.activities.BaseActivity;
import com.example.mytasker.chat.data.model.Message;
import com.example.mytasker.chat.utils.AppUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.Locale;


public abstract class DemoMessagesActivity extends BaseActivity
        implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    protected final String senderId = "0";
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private Menu menu;
    private int selectionCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = (imageView, url, payload) -> Picasso.with(DemoMessagesActivity.this).load(url).into(imageView);
        initFireBase();
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
    protected String mChatUser;
    protected FirebaseAuth mAuth;

    private void initFireBase() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mChatUser = getIntent().getStringExtra("user_id");
//        String userName = getIntent().getStringExtra("user_name");
        //TODO set title bar user id

//        mRootRef.child("Chats").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if (!dataSnapshot.hasChild(mChatUser)) {
//
//                    Map chatAddMap = new HashMap();
//                    chatAddMap.put("seen", false);
//                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
//
//                    Map chatUserMap = new HashMap();
//                    chatUserMap.put("Chats/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
//                    chatUserMap.put("Chats/" + mChatUser + "/" + mCurrentUserId, chatAddMap);
//
//                    mRootRef.updateChildren(chatUserMap, (databaseError, databaseReference) -> {
//
//                        if (databaseError != null) {
//
//                            Log.d("CHAT_LOG", databaseError.getMessage());
//
//                        }
//
//                    });
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }


//        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String online = dataSnapshot.child("online").getValue().toString();
//
//                if(online.equals("true")) {
//
//                    TODO online indicator to true
//
//                } else {
//
//                    GetTimeAgo getTimeAgo = new GetTimeAgo();
//
//                    long lastTime = Long.parseLong(online);
//
//                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
//
//                    mLastSeenView.setText(lastSeenTime);
//                }
//
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//    }

    String mKey = "";
    private boolean firstLoad = true;

    private void loadMessages() {
        DatabaseReference messageRef;
        Query messageQuery;
        if (firstLoad) {
            messageRef = mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser);
            messageQuery = messageRef.orderByChild("createdAt").limitToFirst(20);
            firstLoad = false;
        } else {
            messageRef = mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser);
            messageQuery = messageRef.orderByChild("createdAt").startAt(mKey).limitToFirst(10);
        }
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messagesAdapter.addToStart(message, false);
                dataSnapshot.child("status");
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
