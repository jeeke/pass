package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.mytasker.R;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.mytasker.util.Tools.launchActivity;

public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;

    //ProgressDialog
    private ProgressDialog mRegProgress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("t\n\n\n\n\nt", "onresult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                finish();
                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            } else {
                Toast.makeText(this, "Sign In Error", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(this, "Rc sign in", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//    Toast.makeText(this, "OnCreate", Toast.LENGTH_SHORT).show();
        Log.e("t\n\n\n\n\nt", "oncreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_main);
        //TODO start progress dialog before verify and call
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Button click = findViewById(R.id.click);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "current user not null", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        click.setOnClickListener(v -> launchActivity(MainActivity.this, LoginActivity.class));

//        click.setOnClickListener(view -> startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setIsSmartLockEnabled(false)
////                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
//                        .setAvailableProviders(Arrays.asList(
//                                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                                new AuthUI.IdpConfig.EmailBuilder().build(),
//                                new AuthUI.IdpConfig.PhoneBuilder().build()))
//                        .setTheme(R.style.LightMode)
//                        .setLogo(R.mipmap.ic_launcher_round)
//                        .setTosAndPrivacyPolicyUrls("https://joebirch.co/terms.html", "https://joebirch.co/privacy.html")
//                        .build(),
//                RC_SIGN_IN));
    }
}