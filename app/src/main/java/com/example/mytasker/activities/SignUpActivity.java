package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytasker.R;
import com.example.mytasker.models.User;
import com.example.mytasker.retrofit.JsonPlaceHolder;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;
import com.example.mytasker.util.Contracts;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.mytasker.util.Tools.launchActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }


    EditText firstName, lastName, password;
    Button signUp;

    private void initViews() {
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        password = findViewById(R.id.signup_pass);
        signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(v -> checkFields());
    }

    private void checkFields() {
        if (firstName.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show();
        } else if (lastName.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().length() < 8) {
            Toast.makeText(this, "Password can not be less than 8 characters", Toast.LENGTH_SHORT).show();
        } else {
            callRetrofit();
        }
    }


    private void callRetrofit() {

        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Creating your profile...");
        dlg.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_POST_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        User user = new User(firstName.getText().toString(),
                lastName.getText().toString(), "9453449939",
                null, password.getText().toString(), null, -1);

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<User> call = jsonPlaceHolder.signUp(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Sign Up Unsuccessful", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SignUpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                launchActivity(SignUpActivity.this, LoginActivity.class);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Sign Up Unsuccessful", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }
}
