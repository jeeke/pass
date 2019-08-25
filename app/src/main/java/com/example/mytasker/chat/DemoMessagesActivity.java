package com.example.mytasker.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.example.mytasker.R;
import com.example.mytasker.activities.BaseActivity;
import com.example.mytasker.chat.data.model.Message;
import com.example.mytasker.chat.data.model.MessageHelper;
import com.example.mytasker.chat.utils.AppUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.mytasker.util.Contracts.setOnline;


public abstract class DemoMessagesActivity extends BaseActivity
        implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private Menu menu;
    private int selectionCount;

    protected FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnline("true");
        initFireBase();
        imageLoader = (imageView, url, payload) -> Picasso.with(DemoMessagesActivity.this).load(url).into(imageView);
        onLoadMore(1,10);
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
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.action_copy) {
            messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
            AppUtils.showToast(this, R.string.copied_message, true);
        }
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

    @Override
    protected void onStop() {
        super.onStop();
        setOnline(ServerValue.TIMESTAMP);
    }
    protected String mChatUId;
    protected String mChatAvatar;
    protected String mChatUName;

    private void initFireBase() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
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
            messageRef = mRootRef.child("Messages").child(mCurrentUser.getUid()).child(mChatUId);
            messageQuery = messageRef.orderByChild("createdAt").limitToFirst(20);
            firstLoad = false;
        } else {
            messageRef = mRootRef.child("Messages").child(mCurrentUser.getUid()).child(mChatUId);
            messageQuery = messageRef.orderByChild("createdAt").startAt(mKey).limitToFirst(10);
        }
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageHelper message = dataSnapshot.getValue(MessageHelper.class);
                messagesAdapter.addToStart(message.toMessage(), true);
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
