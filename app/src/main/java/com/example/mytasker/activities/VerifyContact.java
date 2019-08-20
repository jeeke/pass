package com.example.mytasker.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.mytasker.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class VerifyContact extends BaseActivity {

    View layout1,layout2;
    AlertDialog dialog_verifying;

    private static final String TAG = "FirebasePhoneNumAuth";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private EditText phoneNum;
    private PinView verifyCodeET;
    private TextView phonenumberText;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_contact2);

        mAuth = FirebaseAuth.getInstance();

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        Button sendCodeButton =  findViewById(R.id.send);
        Button verifyCodeButton = findViewById(R.id.verify);
        phoneNum = findViewById(R.id.phone);
        verifyCodeET = findViewById(R.id.pinView);
        phonenumberText = findViewById(R.id.phonenumberText);

        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.GONE);

        sendCodeButton.setOnClickListener(view -> {

            String phoneNumber = phoneNum.getText().toString();
            phonenumberText.setText(phoneNumber);

            if (TextUtils.isEmpty(phoneNumber)) {
                phoneNum.setError("Enter a Phone Number");
                phoneNum.requestFocus();
            } else if (phoneNumber.length() < 10) {
                phoneNum.setError("Please enter a valid phone");
                phoneNum.requestFocus();
            } else {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        VerifyContact.this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.processing_dialog,null);
                AlertDialog.Builder show = new AlertDialog.Builder(VerifyContact.this);

                show.setView(alertLayout);
                show.setCancelable(false);
                dialog_verifying = show.create();
                dialog_verifying.show();
                verifyCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        verifyCodeButton.setOnClickListener(view -> {
            String verificationCode = verifyCodeET.getText().toString();
            if(verificationCode.isEmpty()){
                Toast.makeText(VerifyContact.this,"Enter verification code",Toast.LENGTH_SHORT).show();
            }else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                verifyCredential(credential);
            }
        });

    }

    private void verifyCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    dialog_verifying.dismiss();
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        finish();
                        startActivity(new Intent(VerifyContact.this, SignUpActivity.class));
                    } else {
                        Toast.makeText(VerifyContact.this,"Something wrong",Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        }
                    }
                });
    }


}