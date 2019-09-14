package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.mytasker.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.mytasker.Server.SERVER_LOGIN;
import static com.example.mytasker.util.Tools.showSnackBar;

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

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == SERVER_LOGIN) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void checkFields(boolean from) {
        dialog = new ProgressDialog(this);
        email = second.getText().toString();
        password = third.getText().toString();
        name = first.getText().toString();

        if (!prevCallResolved || server == null)
            showSnackBar(this, "Error, Please try later");
        else if (from && name.equals("")) {
            showSnackBar(this, "Please Enter Your Name");
        } else if (email.equals("") || !email.contains("@") || !email.contains("."))
            showSnackBar(this, "Please Enter Valid Email");
        else if (password.length() < 8)
            showSnackBar(this, "Password can not be less than 8 characters");
        else if (from) {
            server.signUp(name, email, password);
        } else {
            server.login(email, password);
        }
    }
}
