package com.example.mytasker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;

public class AddSkill extends BaseActivity {

    Button save;
    EditText skill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skill);
        save = findViewById(R.id.save);
        skill = findViewById(R.id.pass);
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
        Tools.initMinToolbar(this,"Add Skill",false);
    }
}
