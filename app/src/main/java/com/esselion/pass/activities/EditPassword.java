package com.esselion.pass.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.util.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static com.esselion.pass.util.Cache.getUser;
import static com.esselion.pass.util.Tools.showSnackBar;

public class EditPassword extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        Tools.initMinToolbar(this, "Edit Password");
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

    private void checkFields() {
        password = pass.getText().toString();
        newPassword = newPass.getText().toString();
        if (!prevCallResolved || server == null) ;
        else if (password.equals("")) {
            showSnackBar(this, "Passwords can not be empty");
        } else if (newPassword.length() < 8) {
            showSnackBar(this, "Password less than 8 characters!");
        } else {
            server.editPassword(getUser(), password, newPassword);
            prevCallResolved = false;
        }
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_EDIT_PASSWORD) {
            new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("Password Updated")
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .show();
        }
    }
}
