package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.util.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditName extends BaseActivity {

    public static boolean FROM = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Tools.initMinToolbar(this,"Edit First Name",false);
        initViews();
    }

    EditText firstName;
    Button save;

    private void initViews() {
        firstName = findViewById(R.id.email);
        save = findViewById(R.id.save);
        save.setOnClickListener(v -> checkFields());
    }

    private void checkFields() {
        if (firstName.getText().toString().equals("")) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }else callRetrofit();
    }


    private void callRetrofit() {

        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Updating your name...");
        dlg.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName.getText().toString())
                .build();

        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        dlg.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Name Updated Successfully", Toast.LENGTH_SHORT).show();
    //                        Log.d(TAG, "User profile updated.");
                        }
                    });
        }
    }

}
