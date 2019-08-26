package com.example.mytasker.activities;
//package com.example.mytasker.activities;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.WindowManager;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import com.example.mytasker.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import static com.example.mytasker.util.Tools.launchActivity;
//
//public class MainActivity extends BaseActivity {
//
//    private static final int RC_SIGN_IN = 123;
//
//    //ProgressDialog
//    private ProgressDialog mRegProgress;
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.e("t\n\n\n\n\nt", "onresult");
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
////            IdpResponse response = IdpResponse.fromResultIntent(data);
//            if (resultCode == RESULT_OK) {
//                // Successfully signed in
//                finish();
//                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
//            } else {
//                Toast.makeText(this, "Sign In Error", Toast.LENGTH_SHORT).show();
//            }
//        } else Toast.makeText(this, "Rc sign in", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
////    Toast.makeText(this, "OnCreate", Toast.LENGTH_SHORT).show();
//        Log.e("t\n\n\n\n\nt", "oncreate");
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(1);
//        setContentView(R.layout.activity_main);
//        //TODO start progress dialog before verify and call
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        ImageButton click = findViewById(R.id.google);
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            Toast.makeText(this, "current user not null", Toast.LENGTH_SHORT).show();
//            finish();
//            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//
//        click.setOnClickListener(v -> launchActivity(MainActivity.this, LoginActivity.class));
//
////        click.setOnClickListener(view -> startActivityForResult(
////                AuthUI.getInstance()
////                        .createSignInIntentBuilder()
////                        .setIsSmartLockEnabled(false)
//////                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
////                        .setAvailableProviders(Arrays.asList(
////                                new AuthUI.IdpConfig.GoogleBuilder().build(),
////                                new AuthUI.IdpConfig.EmailBuilder().build(),
////                                new AuthUI.IdpConfig.PhoneBuilder().build()))
////                        .setTheme(R.style.LightMode)
////                        .setLogo(R.mipmap.ic_launcher_round)
////                        .setTosAndPrivacyPolicyUrls("https://joebirch.co/terms.html", "https://joebirch.co/privacy.html")
////                        .build(),
////                RC_SIGN_IN));
//    }
//}


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mytasker.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.mytasker.util.Tools.launchActivity;

public class MainActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    ProgressDialog dialog;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Logging you in, Please Wait....");
        // Button listeners
        findViewById(R.id.google).setOnClickListener(this);
        findViewById(R.id.fb).setOnClickListener(this);
//        findViewById(R.id.disconnectButton).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        dialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                    dialog.dismiss();
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    private void signOut() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google sign out
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                task -> updateUI(null));
//    }

//    private void revokeAccess() {
//        // Firebase sign out
//        mAuth.signOut();
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                task -> updateUI(null));
//    }

    private void updateUI(FirebaseUser user) {
//        hideProgressDialog();
        if (user != null) {
            launchActivity(this, DashboardActivity.class);
        } else {
            Toast.makeText(this, "No Signed In User", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.google) {
            signIn();
        }
//        else if (i == R.id.signOutButton) {
//            signOut();
//        } else if (i == R.id.disconnectButton) {
//            revokeAccess();
//        }
    }
}