package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;
import com.firebase.ui.auth.AuthUI;

import static com.example.mytasker.util.Tools.launchActivity;


public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Tools.initMinToolbar(this, "SETTINGS", false);
        findViewById(R.id.textView81).setOnClickListener((View v) -> launchActivity(this, EditName.class));
        findViewById(R.id.textView82).setOnClickListener((View v) -> launchActivity(this, EditName.class));
        findViewById(R.id.textView83).setOnClickListener((View v) -> launchActivity(this, EditEmail.class));
        findViewById(R.id.textView84).setOnClickListener((View v) -> launchActivity(this, EditPassword.class));
        findViewById(R.id.textView92).setOnClickListener(this::invite);
        findViewById(R.id.textView90).setOnClickListener((View v) -> openUri("https://google.com"));
        findViewById(R.id.textView94).setOnClickListener((View v) -> openUri("https://facebook.com"));
        findViewById(R.id.textView93).setOnClickListener((View v) -> openUri("https://linkedin.com"));
        findViewById(R.id.textView91).setOnClickListener((View v) -> openUri("https://twitter.com"));
        findViewById(R.id.textView76).setOnClickListener((View v) -> launchActivity(this, PrivacyPolicy.class));
        findViewById(R.id.textView69).setOnClickListener((View v) -> launchActivity(this, TandC.class));
        findViewById(R.id.textView58).setOnClickListener((View v) -> launchActivity(this, ContactUs.class));
        findViewById(R.id.logout).setOnClickListener(v -> {
            ProgressDialog dialog = new ProgressDialog(SettingActivity.this);
            dialog.setTitle("Logging out, Please Wait....");
            dialog.show();
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    launchActivity(SettingActivity.this,MainActivity.class);
                });
        });
    }


    private void openUri(String uri){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void invite(View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Esselion.com");
        intent.putExtra(Intent.EXTRA_TEXT,"Checkout this awesome app! \nhttps://esselion.com");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent,"Share MyTasker"));
    }
}