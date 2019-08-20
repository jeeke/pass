package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditEmail extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        Tools.initMinToolbar(this,"Edit Email",false);
        initViews();
    }

    EditText email;
    Button save;

    private void initViews() {
        email = findViewById(R.id.email);
        save = findViewById(R.id.save);
        save.setOnClickListener(v -> checkFields());
    }
    String em;
    private void checkFields() {
//        Toast.makeText(this, "Ema", Toast.LENGTH_SHORT).show();
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Verify Email")
                .setMessage("An email has been sent to you, please verify it to change your email")
                .setPositiveButton("OK", null)
                .show();
        em = email.getText().toString();
        if (em.equals("") || (!em.contains("@")) || (!em.contains("."))) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
        }else callRetrofit();
    }


    private void callRetrofit() {

        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Updating your name...");
        dlg.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updateEmail(em)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d("Email updated", "User email address updated.");
                                        }
                                    });
                        }
                    });
        }
    }
}
