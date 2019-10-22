package com.esselion.pass.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.chat.MessagesActivity;
import com.esselion.pass.util.RemoteConfigHelper;
import com.esselion.pass.util.Tools;

import java.util.Random;

import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.finishNLaunchActivity;
import static com.esselion.pass.util.Tools.launchActivity;
import static com.esselion.pass.util.Tools.showSnackBar;

public class ContactUs extends BaseActivity {
    EditText feedBackEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("CONTACT US");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.cardView6).setOnClickListener(this::callUs);
        feedBackEdit = findViewById(R.id.edit_feedback);
        findViewById(R.id.cardView7).setOnClickListener(v -> {
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra("id", getSupportId());
            intent.putExtra("name", "Pass Support Team");
            intent.putExtra("avatar", "https://firebasestorage.googleapis.com/v0/b/esselion-pass.appspot.com/o/pass-logo.png?alt=media&token=252f7ae5-de88-4114-9e8e-810503272afa");
            finishNLaunchActivity(this, intent);
        });
        findViewById(R.id.btn_feedback).setOnClickListener(v -> {
            String feedBack = feedBackEdit.getText().toString();
            if (feedBack.equals("")) {
                showSnackBar(this, "Please write some message");
            } else if (server != null) {
                server.sendFeedback(feedBack, getUser().getUid());
            }
        });
    }

    private String getSupportId() {
        String[] supportIds = RemoteConfigHelper.getInstance().getSupportIds();
        int index = new Random().nextInt(supportIds.length);
        return supportIds[index];
    }

    private void callUs(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+919679280831"));
        launchActivity(this, intent);
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_SEND_FEEDBACK) {
            feedBackEdit.setText("");
            Tools.closeKeyboard(this);
        }
    }
}
