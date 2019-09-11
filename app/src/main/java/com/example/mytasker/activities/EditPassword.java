package com.example.mytasker.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;

import static com.example.mytasker.util.Tools.showSnackBar;

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

    private void checkFields() {
        password = pass.getText().toString();
        newPassword = newPass.getText().toString();
        if (!prevCallResolved || server == null) ;
        else if (password.equals("")) {
            showSnackBar(this, "Passwords can not be empty");
        } else if (newPassword.length() < 8) {
            showSnackBar(this, "Password less than 8 characters!");
        } else {
            server.editPassword(password, newPassword);
            prevCallResolved = false;
        }
    }


}
