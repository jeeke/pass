package com.esselion.pass.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.airbnb.lottie.LottieAnimationView;
import com.esselion.pass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.esselion.pass.Server.SERVER_LOGIN;
import static com.esselion.pass.Server.SERVER_SIGNUP;
import static com.esselion.pass.util.Tools.launchActivity;
import static com.esselion.pass.util.Tools.showSnackBar;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LOGIN_ACTIVITY";
    EditText first, second, third;
    Button action;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.LightMode);
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
//        headTitle = findViewById(R.id.headTitle);
        action.setOnClickListener(v -> checkFields(from));
        if (!from) {
            first.setVisibility(View.GONE);
            action.setText("LOG IN");
            LottieAnimationView anim = findViewById(R.id.lottie_anim);
            anim.setAnimation(R.raw.login_hi);
        }
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == SERVER_LOGIN) {
            updateUI();
        } else if (methodId == SERVER_SIGNUP) {
            showMailVerifyDialog(false, null);
        }
    }

    private void showMailVerifyDialog(boolean resend, FirebaseUser user) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_mail_verification);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.findViewById(R.id.bt_ok).setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
//                intent.setData(Uri.parse("mailto:"));
                launchActivity(this, Intent.createChooser(intent, "Check mail"));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        if (resend) {
            dialog.findViewById(R.id.bt_resend).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.bt_resend).setOnClickListener(v -> server.resendVerificationMail(user));
        } else dialog.findViewById(R.id.bt_resend).setVisibility(View.GONE);
        dialog.show();
    }

    private void updateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                launchActivity(this, intent);
            } else {
                showMailVerifyDialog(true, user);
            }

        }
    }

    private void checkFields(boolean from) {
        dialog = new ProgressDialog(this);
        String email = second.getText().toString();
        String password = third.getText().toString();
        String name = first.getText().toString();

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
