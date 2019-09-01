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

import java.util.Map;

public class BidConfirm extends BaseActivity {


    EditText price, desc;
    private Task task;

    boolean prevCallResolved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = (Task) getIntent().getSerializableExtra("task");
        if (task == null) finish();
        setContentView(R.layout.activity_bid_confirm);
        Tools.initMinToolbar(this, "Confirm Bid", false);
        findViewById(R.id.confirm_bid).setOnClickListener(v -> checkFields());
        initFields();
    }

    private void initFields() {
        price = findViewById(R.id.bidPrice);
        desc = findViewById(R.id.extraText);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void checkFields() {
        if (price.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter bid price", Toast.LENGTH_SHORT).show();
        } else if (desc.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter some message", Toast.LENGTH_SHORT).show();
        } else {
            callAPI();
        }
    }

    private void callAPI() {
        if (!prevCallResolved) return;
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your Bid..");
        dlg.show();
        Map bid = Bid.toMap((int) Float.parseFloat(price.getText().toString()),
                task,
                desc.getText().toString());

        Contracts.call(bid, "bid").addOnCompleteListener(t -> {
            prevCallResolved = true;
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
            Log.v("tag", t.getResult());
            Toast.makeText(this, "Bidding Successful", Toast.LENGTH_SHORT).show();
        });
        prevCallResolved = false;
    }
}
