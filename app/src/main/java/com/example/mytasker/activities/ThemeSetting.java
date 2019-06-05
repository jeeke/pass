package com.example.mytasker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytasker.R;

import static com.example.mytasker.activities.AuthActivity.MY_PREFS_NAME;

public class ThemeSetting extends AppCompatActivity {
    EditText Etoolbar, Ebottombar, Efab, Elist, ElistHead;
    View view1,view2,view3,view4,view5;
    boolean previewDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_setting);

        Etoolbar = findViewById(R.id.toolbarBack);
        Ebottombar = findViewById(R.id.bottomNavBar);
        Efab = findViewById(R.id.fabColor);
        Elist = findViewById(R.id.listBackground);
        ElistHead = findViewById(R.id.listHead);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
    }


    public void done(View v) {
        preview(null);
        Intent intent = new Intent(this,DashboardActivity.class);
        startActivity(intent);
    }

    private void setValue(String editString,View view,String key,SharedPreferences.Editor editor){
        if(editString.length()>0){
            int color = Color.parseColor(editString);
            view.setBackgroundColor(color);
            editor.putInt(key,color);
        }
    }

    public void preview(View v){
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        setValue(Etoolbar.getText().toString(),view1,"toolbar",editor);
        setValue(Ebottombar.getText().toString(),view2,"bottombar",editor);
        setValue(Efab.getText().toString(),view3,"fab",editor);
        setValue(ElistHead.getText().toString(),view4,"listHead",editor);
        setValue(Elist.getText().toString(),view5,"list",editor);
        editor.apply();
    }
}
