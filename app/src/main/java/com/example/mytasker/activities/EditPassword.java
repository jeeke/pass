package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import static com.example.mytasker.util.Cache.getUser;

public class EditPassword extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        Tools.initMinToolbar(this, "Edit Password", false);
        initFields();
    }

    EditText pass, newPass;
    Button save;

    private void initFields() {
        pass = findViewById(R.id.pass);
        newPass = findViewById(R.id.newPass);
        save = findViewById(R.id.save);
        save.setOnClickListener(v -> checkFields());
    }

    String password, newPassword;

    boolean prevCallResolved = true;

    private void checkFields() {
        password = pass.getText().toString();
        newPassword = newPass.getText().toString();
        if (password.equals("")) {
            Toast.makeText(this, "Passwords can not be empty", Toast.LENGTH_SHORT).show();
        } else if (newPassword.length() < 8) {
            Toast.makeText(this, "Password less than 8 characters!", Toast.LENGTH_SHORT).show();
        } else {
            callRetrofit();
        }
    }

    private void callRetrofit() {
        if (!prevCallResolved) return;
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Updating Password..");
        dlg.show();
        FirebaseUser user = getUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    prevCallResolved = true;
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(this, "Password updated successful", Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            });
                });
        prevCallResolved = false;
    }

}
