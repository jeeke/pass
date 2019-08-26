package com.example.mytasker.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytasker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Date;

import static com.example.mytasker.util.Tools.launchActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_ACTIVITY";
    private static String[] avatars = {
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar6.png?alt=media&token=cf4d2845-bccf-43f4-826e-f4aac81c38cd",
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar4.png?alt=media&token=3d643200-a8cf-4614-a508-0438dcfde5b5",
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar3.png?alt=media&token=2e348dbe-a436-4670-9159-1948a77e2b12",
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar1.png?alt=media&token=2d22f9fb-f1b1-48a3-8dd8-c5b1f8946f2f",
            "https://firebasestorage.googleapis.com/v0/b/mytasker-d3ef5.appspot.com/o/avatars%2Favatar6.png?alt=media&token=cf4d2845-bccf-43f4-826e-f4aac81c38cd"
    };
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText first, second, third;
    Button action;
    private String name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean from = getIntent().getBooleanExtra("from", false);
        initViews(from);
    }

    private void checkFields(boolean from) {
        name = first.getText().toString();
        email = second.getText().toString();
        password = third.getText().toString();
        if (from && name.equals(""))
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        else if (email.equals("") || !email.contains("@") || !email.contains("."))
            Toast.makeText(this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
        else if (password.length() < 8)
            Toast.makeText(this, "Password can not be less than 8 characters", Toast.LENGTH_SHORT).show();
        else if (from) {
            signUp();
        } else login();
    }

    private void initViews(boolean from) {
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        action = findViewById(R.id.action_button);
        action.setOnClickListener(v -> {
            checkFields(from);
        });
        if (from) {
            first = findViewById(R.id.first);
            first.setVisibility(View.GONE);
        }
    }

    private void signUp() {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        launchActivity(LoginActivity.this, DashboardActivity.class);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void login() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        initProfile();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(avatars[(int) (new Date().getTime() % 6)]))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile initiated.");
                            launchActivity(LoginActivity.this, DashboardActivity.class);
                        }
                    });
        }
    }
}
