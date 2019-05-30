package com.example.mytasker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class AuthActivity extends AppCompatActivity {
    View login_selector,signup_selector;
    ConstraintLayout login_tab,signup_tab;
    private EditText usernameView;
    private EditText passwordView;
    Button btn;
    Button l,s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//    TODO
//        SettingActivity.toolbar = getColor(R.color.colorPrimaryDark);
//        SettingActivity.bottombar = getColor(R.color.colorPrimaryDark);
//        SettingActivity.fab = getColor(R.color.colorAccent);
//        SettingActivity.list = getColor(R.color.colorPrimaryDark);
//        SettingActivity.listHead = getColor(R.color.colorPrimary);
        setContentView(R.layout.activity_auth);
        btn = findViewById(R.id.bt);
//        ParseInstallation.getCurrentInstallation().saveInBackground();
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if (currentUser != null) {
//            startActivity(new Intent(this,MainActivity.class));
//        }

        ConstraintLayout linearLayout =  findViewById(R.id.login2cl);

        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

        login_selector = findViewById(R.id.view);
        signup_selector = findViewById(R.id.view5);
        login_tab = findViewById(R.id.constraintLayout3);
        signup_tab = findViewById(R.id.constraintLayout3_1);
        s= findViewById(R.id.button_1);

        usernameView = findViewById(R.id.editText_email);
        passwordView = findViewById(R.id.editText_pass);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              mview.show(getSupportFragmentManager(), "");
                login();
            }
        });

    }

    public void logintab(View v)
    {
        login_selector.setAlpha(1f);
        login_tab.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        signup_selector.setAlpha(0f);
        signup_tab.setVisibility(View.GONE);
        s.setVisibility(View.GONE);
    }

    public void signuptab(View v)
    {
        signup_selector.setAlpha(1f);
        signup_tab.setVisibility(View.VISIBLE);
        s.setVisibility(View.VISIBLE);

        login_selector.setAlpha(0f);
        login_tab.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);

    }

    private void login() {
        //Validating the log in data
        boolean validationError = false;
//        btn.startAnimation();
        StringBuilder validationErrorMessage = new StringBuilder("Please insert ");
        if (isEmpty(usernameView)) {
            validationError = true;
            validationErrorMessage.append("an username");
        }
        if (isEmpty(passwordView) || !isPasswordValid(passwordView.getText().toString())) {
            if (validationError) {
                validationErrorMessage.append(" and ");
            }
            validationError = true;
            validationErrorMessage.append("a valid password");
        }
        validationErrorMessage.append(".");

        if (validationError) {
            Toast.makeText(this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
//            btn.revertAnimation();
            return;
        }

        //Setting up a progress dialog

        passwordView.setError(null);
        Intent intent = new Intent(AuthActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isEmpty(EditText text) {
            return (text.getText().toString().trim().length() <= 0);
    }

}
