package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    //    private FrameLayout linearLayout;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    private void checkLogin() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean logged = prefs.getBoolean("logged", false);
        if (logged) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    }

    EditText contact,password;
    Button signUp,logIn;
    private void initViews(){
        contact = findViewById(R.id.contact);
        password = findViewById(R.id.password);
        signUp = findViewById(R.id.signup);
        logIn = findViewById(R.id.login);
        signUp.setOnClickListener(v -> {
            finish();
            launchActivity(LoginActivity.this, VerifyContact.class);
        });
        logIn.setOnClickListener(v -> {
            checkFields();
        });
    }

    private  void checkFields(){
        if(contact.getText().toString().trim().length() != 10){
            Toast.makeText(this, "Please enter valid contact number", Toast.LENGTH_SHORT).show();
        }
        else if(password.getText().toString().equals("")){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }else {
            callRetrofit();
        }
    }


    private void callRetrofit() {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Logging you in, Please Wait...");
        dlg.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_POST_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        User user = new User(null,
                null,"9453449939",
                null,password.getText().toString(),null, -1);

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<User> call = jsonPlaceHolder.login(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                dlg.dismiss();
                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("logged", true);
                editor.apply();

                finish();
                launchActivity(LoginActivity.this,DashboardActivity.class);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }
}
