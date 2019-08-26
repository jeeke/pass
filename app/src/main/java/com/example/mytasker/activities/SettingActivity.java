package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import static com.example.mytasker.util.Tools.launchActivity;


public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Tools.initMinToolbar(this, "SETTINGS", false);
        initViews();

    }

    private void initViews() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener(this,
                    task -> {
                        FirebaseAuth.getInstance().signOut();
                        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
                        dialog.dismiss();
                        finish();
                        launchActivity(SettingActivity.this, MainActivity.class);
                    });
        });
        TextView name, contact, contactHead;
        name = findViewById(R.id.textView85);
        contact = findViewById(R.id.textView87);
        contactHead = findViewById(R.id.textView79);
        if (user != null) {
            name.setText(user.getDisplayName());
            if (user.getEmail() != null) contact.setText(user.getEmail());
            else {
                contact.setText(user.getPhoneNumber());
                contactHead.setText("Phone No");
            }
        }
    }


    private void openUri(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void invite(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Esselion.com");
        intent.putExtra(Intent.EXTRA_TEXT, "Checkout this awesome app! \nhttps://esselion.com");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share MyTasker"));
    }
}