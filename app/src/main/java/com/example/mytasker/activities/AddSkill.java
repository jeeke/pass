package com.example.mytasker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.mytasker.R;
import com.example.mytasker.util.Contracts;

public class AddSkill extends BaseActivity {

    Button save;
    EditText skill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skill);
        Toolbar toolbar = findViewById(R.id.toolbar);
        save = findViewById(R.id.save);
        skill = findViewById(R.id.email);
        save.setOnClickListener(v -> {
            Contracts.added_skill = skill.getText().toString();
            if(Contracts.added_skill.equals("")){
                Toast.makeText(AddSkill.this, "Please Enter the Skill", Toast.LENGTH_SHORT).show();
            }else {
                Intent returnIntent = new Intent();
//              returnIntent.putExtra("result",result);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        toolbar.setTitle("Add Skill");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
