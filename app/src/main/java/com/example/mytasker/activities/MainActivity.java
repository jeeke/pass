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
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;

    //TODO start progress dialog before verify and call

//    private fun deleteUser() {
//        AuthUI.getInstance()
//                .delete(this)
//                .addOnCompleteListener { }
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_firebase_ui);
//    }
//
//    public void createSignInIntent() {
//        // [START auth_fui_create_intent]
//        // Choose authentication providers
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());
//
//        // Create and launch sign-in intent
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .build(),
//                RC_SIGN_IN);
//        // [END auth_fui_create_intent]
//    }

//    public void signOut() {
//        // [START auth_fui_signout]
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
//    }
//
//    public void delete() {
//        // [START auth_fui_delete]
//        AuthUI.getInstance()
//                .delete(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
//    }
//
//    public void privacyAndTerms() {
//        List<AuthUI.IdpConfig> providers = Collections.emptyList();
//        // [START auth_fui_pp_tos]
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .setTosAndPrivacyPolicyUrls(
//                                "https://example.com/terms.html",
//                                "https://example.com/privacy.html")
//                        .build(),
//                RC_SIGN_IN);
//    }

    private FirebaseAuth mAuth;
    //ProgressDialog
    private ProgressDialog mRegProgress;

//    TODO complete email link signup
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                String device_token = FirebaseInstanceId.getInstance().getToken();
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("name", current_user.getDisplayName());
                userMap.put("status", "Hi there I'm using Lapit Chat App.");
                userMap.put("image", "default");
                userMap.put("thumb_image", "default");
                userMap.put("device_token", device_token);
                mDatabase.setValue(userMap).addOnFailureListener(e -> Log.e("Value putting error", "f\n\n\n\n" + e + "\n\n\n\nf"));
//                        .addOnCompleteListener(task -> {
//                    if(task.isSuccessful()){
////                        finish();
//                        Toast.makeText(this, "Putted value", Toast.LENGTH_SHORT).show();
////                        launchActivity(MainActivity.this,DashboardActivity.class);
//                    }
//                    Log.e("Value putting error","f\n\n\n\n" + uid+ task + "\n\n\n\nf");
//                    Toast.makeText(this, "Value putting error", Toast.LENGTH_SHORT).show();
//                });
            } else {
                Toast.makeText(this, "Sign In Error", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(this, "Rc sign in", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Button click = findViewById(R.id.click);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
//            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
//            finish();
            Toast.makeText(this, "current user null", Toast.LENGTH_SHORT).show();
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("MOBILE ID", "getInstanceId failed", task.getException());
                return;
            }
            // Get new Instance ID token
            String token = task.getResult().getToken();
            // Log and toast
            String msg = "MOBILE TOKEN" + token;
            Log.d("MOBILE TOKEN", msg);
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        click.setOnClickListener(view -> startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
//                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build()))
                        .setTheme(R.style.LightMode)
                        .setLogo(R.mipmap.ic_launcher_round)
                        .setTosAndPrivacyPolicyUrls("https://joebirch.co/terms.html", "https://joebirch.co/privacy.html")
                        .build(),
                RC_SIGN_IN));
    }
//
//
//        Smart Lock
//
//        By default, FirebaseUI uses Smart Lock for Passwords to store the user's credentials and automatically sign users into your app on subsequent attempts. Using Smart Lock is recommended to provide the best user experience, but in some cases you may want to disable Smart Lock for testing or development. To disable Smart Lock, you can use the setIsSmartLockEnabled method when building your sign-in Intent:
//
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setIsSmartLockEnabled(false)
//                        .build(),
//                RC_SIGN_IN);
//
//        Smart Lock hints
//
//        If you'd like to keep Smart Lock's "hints" but disable the saving/retrieving of credentials, then you can use the two-argument version of setIsSmartLockEnabled:
//
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setIsSmartLockEnabled(false, true)
//                        .build(),
//                RC_SIGN_IN);
//
//        Smart Lock in dev builds
//
//        It is often desirable to disable Smart Lock in development but enable it in production. To achieve this, you can use the BuildConfig.DEBUG flag to control Smart Lock:
//
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
//                        .build(),
//                RC_SIGN_IN);
}

