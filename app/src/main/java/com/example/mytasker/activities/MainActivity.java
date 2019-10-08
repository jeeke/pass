package com.example.mytasker.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mytasker.R;
import com.example.mytasker.util.Cache;
import com.example.mytasker.util.Tools;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.mytasker.MyFirebaseMessagingService.MY_PREFS_NAME;
import static com.example.mytasker.util.Tools.launchActivity;
import static com.example.mytasker.util.Tools.showSnackBar;

public class MainActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    ProgressDialog dialog;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.MainActivity);
        super.onCreate(savedInstanceState);
//        setUpdate();
        dialog = new ProgressDialog(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        updateUI();
        if (sp.getBoolean("showIntro", true)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("showIntro", false);
            editor.apply();
            Intent intent = new Intent(this, IntroActivity.class); // Call the AppIntro java class
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_main);
            findViewById(R.id.google).setOnClickListener(this);
            findViewById(R.id.login).setOnClickListener(this);
            findViewById(R.id.signup).setOnClickListener(this);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            dialog.dismiss();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUI();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        dialog.setTitle("Logging You In, Please Wait....");
        dialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        updateUI();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        showSnackBar(this, "Authentication Failed.");
                        updateUI();
                    }
                });
    }

    private void signIn() {
        dialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void signOut(Activity context) {
        //remove token firebase
        Tools.removeToken(context);

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mGoogleSignInClient.signOut();

        //Empty cache
        Cache.emptyCache();

        //redirect to login screen
        Intent intent = new Intent(context, MainActivity.class);
//        intent.putExtra("from", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

//    private void revokeAccess() {
//        // Firebase sign out
//        mAuth.signOut();
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                task -> updateUI(null));
//    }

    private void updateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
//            TODO handle pending feedbacks
            launchActivity(this, DashboardActivity.class);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        Intent intent = new Intent(this, LoginActivity.class);
        if (i == R.id.google) {
            signIn();
        } else if (i == R.id.login) {
            intent.putExtra("from", false);
            startActivity(intent);
        } else if (i == R.id.signup) {
            startActivity(intent);
        }
    }

}