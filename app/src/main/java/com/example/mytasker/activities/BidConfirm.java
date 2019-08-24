package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.models.Bid;
import com.example.mytasker.models.Task;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;

import java.util.Map;

public class BidConfirm extends BaseActivity {


    EditText price,desc,contact;
    private Task task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = (Task) getIntent().getSerializableExtra("task");
        if (task == null) finish();
        setContentView(R.layout.activity_bid_confirm);
        Tools.initMinToolbar(this,"Confirm Bid",false);
        findViewById(R.id.confirm_bid).setOnClickListener(v -> checkFields());
        initFields();
    }

    private void initFields(){
        price = findViewById(R.id.bidPrice);
        contact = findViewById(R.id.contact);
        desc = findViewById(R.id.extraText);
    }

    private  void checkFields(){
        if(price.getText().toString().equals("")){
            Toast.makeText(this, "Please enter bid price", Toast.LENGTH_SHORT).show();
        }
        else if(contact.getText().toString().length()!=10){
            Toast.makeText(this, "Please enter valid contact", Toast.LENGTH_SHORT).show();
        }
        else if(desc.getText().toString().equals("")){
            Toast.makeText(this, "Please enter some message", Toast.LENGTH_SHORT).show();
        }else {
            callAPI();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void callAPI() {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your Bid..");
        dlg.show();
        Gson gson = new Gson();
        String t1 = gson.toJson(task);
        Map bid = Bid.toMap((int) Float.parseFloat(price.getText().toString()),
                Long.parseLong(contact.getText().toString()),
                t1,
                desc.getText().toString());

        Contracts.call(bid, "bid").addOnCompleteListener(t -> {
            dlg.dismiss();
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.e("tag", ffe + "\n" + code + "\n" + details);
                }
                Toast.makeText(this, "Bidding Unsuccessful", Toast.LENGTH_SHORT).show();
                Log.e("tag", e + "");
                return;
            }
            finish();
            Toast.makeText(this, t.getResult().getMessage() + "", Toast.LENGTH_SHORT).show();
        });
    }
}
