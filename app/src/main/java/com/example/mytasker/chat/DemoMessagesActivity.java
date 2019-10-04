package com.example.mytasker.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytasker.R;
import com.example.mytasker.activities.BaseActivity;
import com.example.mytasker.activities.ProfileActivity;
import com.example.mytasker.chat.model.Message;
import com.example.mytasker.chat.model.MessageHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.mytasker.util.Tools.setOnline;
import static com.example.mytasker.util.Tools.showSnackBar;


public abstract class DemoMessagesActivity extends BaseActivity
        implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;
    private Menu menu;
    private int selectionCount;
    protected FirebaseUser mCurrentUser;
    private static final int PAGE_COUNT = 100;
    public static String lastKey;
    private static String topKey;
    boolean firstLoad = true;
    private Query messageQuery;
    public ProgressBar bar;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    boolean emptyData = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnline(this, "true");
        initFireBase();
        imageLoader = (imageView, url, payload) -> Glide.with(DemoMessagesActivity.this).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bar = findViewById(R.id.progress_bar);
        onLoadMore(0, 0);
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
        if (!emptyData) {
            bar.setVisibility(View.VISIBLE);
            loadMessages();
        }
    }


    protected DatabaseReference mRootRef;

    @Override
    protected void onStop() {
        super.onStop();
        setOnline(this, ServerValue.TIMESTAMP);
    }

    protected String mChatUId;
    protected String mChatAvatar;
    protected String mChatUName;

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_profile).setVisible(mChatAvatar != null);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    private void initFireBase() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mChatUId = getIntent().getStringExtra("id");
        mChatUName = getIntent().getStringExtra("name");
        mChatAvatar = getIntent().getStringExtra("avatar");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_copy) {
            messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
            showSnackBar(this, getString(R.string.copied_message));
            return true;
        } else if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("id", mChatUId);
            intent.putExtra("name", mChatUName);
            intent.putExtra("avatar", mChatAvatar);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMessages() {
        messageQuery = mRootRef.child("Messages").child(mCurrentUser.getUid()).
                child(mChatUId).orderByKey().limitToLast(PAGE_COUNT);
        if ((!firstLoad) && (!emptyData)) {
            messageQuery = messageQuery.endAt(topKey);
        }
        messageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) emptyData = true;
                ArrayList<Message> messages = new ArrayList<>();
                boolean firstElement = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (firstElement) {
                        topKey = snapshot.getKey();
                    } else {
                        messages.add(0, snapshot.getValue(MessageHelper.class).toMessage());
                    }
                    firstElement = false;
                    if (firstLoad) lastKey = snapshot.getKey();
                }
                messagesAdapter.addToEnd(messages, false);
                messageQuery.removeEventListener(this);
                bar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        firstLoad = false;
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
