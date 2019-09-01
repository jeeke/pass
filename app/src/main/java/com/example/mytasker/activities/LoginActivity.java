package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytasker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Date;

import static com.example.mytasker.util.Cache.getUser;
import static com.example.mytasker.util.Contracts.avatars;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LOGIN_ACTIVITY";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText first, second, third;
    Button action;
    private String name, email, password;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_auth);
        boolean from = getIntent().getBooleanExtra("from", true);
        initViews(from);
    }

    boolean prevCallResolved = true;

    private void initViews(boolean from) {
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        action = findViewById(R.id.action_button);
        first = findViewById(R.id.first);
        action.setOnClickListener(v -> checkFields(from));
        if (!from) {
            first.setVisibility(View.GONE);
            action.setText("LOG IN");
        }
    }

    private void checkFields(boolean from) {
        dialog = new ProgressDialog(this);
        email = second.getText().toString();
        password = third.getText().toString();
        name = first.getText().toString();
        if (from && name.equals("")) {
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        } else if (email.equals("") || !email.contains("@") || !email.contains("."))
            Toast.makeText(this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
        else if (password.length() < 8)
            Toast.makeText(this, "Password can not be less than 8 characters", Toast.LENGTH_SHORT).show();
        else if (from) {
            dialog.setTitle("Creating your account,Please Wait...");
            dialog.show();
            signUp();
        } else {
            dialog.setTitle("Logging you in,Please Wait...");
            dialog.show();
            login();
        }
    }

    private void signUp() {
        if (!prevCallResolved) return;
        prevCallResolved = false;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        initProfile();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "SignUp failed.",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    prevCallResolved = true;
                });
    }

    private void login() {
        if (!prevCallResolved) return;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    prevCallResolved = true;
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        dialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        prevCallResolved = false;
    }

    private void initProfile() {
        if (!prevCallResolved) return;
        FirebaseUser user = getUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(avatars[(int) (new Date().getTime() % 6)]))
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        prevCallResolved = true;
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Log.d(TAG, "User profile initiated.");
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    });
            prevCallResolved = false;
        }
        dialog.dismiss();
    }
}
