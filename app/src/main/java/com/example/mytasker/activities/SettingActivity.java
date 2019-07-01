package com.example.mytasker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;


public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Tools.initMinToolbar(this, "SETTINGS", false);
        findViewById(R.id.textView81).setOnClickListener(this::editName);
        findViewById(R.id.textView82).setOnClickListener(this::editName);
        findViewById(R.id.textView83).setOnClickListener(this::editEmail);
        findViewById(R.id.textView84).setOnClickListener(this::editPassword);
        findViewById(R.id.textView92).setOnClickListener(this::invite);
        findViewById(R.id.textView90).setOnClickListener(this::playStore);
        findViewById(R.id.textView94).setOnClickListener(this::facebook);
        findViewById(R.id.textView93).setOnClickListener(this::linkedIn);
        findViewById(R.id.textView91).setOnClickListener(this::twitter);
        findViewById(R.id.textView76).setOnClickListener(this::policy);
        findViewById(R.id.textView69).setOnClickListener(this::tnc);
        findViewById(R.id.textView58).setOnClickListener(this::contactUs);
    }

    private void contactUs(View view) {
        Intent intent = new Intent(this,ContactUs.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void tnc(View view) {
        Intent intent = new Intent(this,TandC.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void policy(View view) {
        Intent intent = new Intent(this,PrivacyPolicy.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void linkedIn(View view) {
        openUri("https://linkedin.com");
    }

    private void twitter(View view) {
        openUri("https://twitter.com");
    }

    private void facebook(View view) {
        openUri("https://facebook.com");
    }

    private void playStore(View v){
        openUri("https://google.com");
    }

    private void openUri(String uri){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void editName(View v){
        Intent intent = new Intent(this,EditFirstName.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
    private void invite(View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Esselion.com");
        intent.putExtra(Intent.EXTRA_TEXT,"Checkout this awesome app! \nhttps://esselion.com");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent,"Share MyTasker"));
    }
    private void editEmail(View v){
        Intent intent = new Intent(this,EditEmail.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
    private void editPassword(View v){
        Intent intent = new Intent(this,EditPassword.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}