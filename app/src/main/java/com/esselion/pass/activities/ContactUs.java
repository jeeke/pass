package com.esselion.pass.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.esselion.pass.R;
import com.esselion.pass.chat.MessagesActivity;
import com.esselion.pass.util.RemoteConfigHelper;

import java.util.Random;

import static com.esselion.pass.util.Tools.finishNLaunchActivity;
import static com.esselion.pass.util.Tools.launchActivity;

public class ContactUs extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("CONTACT US");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.cardView6).setOnClickListener(this::callUs);
        findViewById(R.id.cardView7).setOnClickListener(v -> {
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra("id", getSupportId());
            intent.putExtra("name", "Pass Support Team");
            intent.putExtra("avatar", "https://firebasestorage.googleapis.com/v0/b/esselion-pass.appspot.com/o/pass-logo.png?alt=media&token=252f7ae5-de88-4114-9e8e-810503272afa");
            finishNLaunchActivity(this, intent);
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
}
