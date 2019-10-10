package com.esselion.pass.activities;

import android.os.Bundle;
import android.widget.EditText;

import com.esselion.pass.R;
import com.esselion.pass.models.Bid;
import com.esselion.pass.models.Task;
import com.esselion.pass.util.Tools;

import java.util.Map;

import static com.esselion.pass.util.Tools.showSnackBar;

public class BidConfirm extends BaseActivity {


    EditText price, desc;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = (Task) getIntent().getSerializableExtra("task");
        if (task == null) finish();
        setContentView(R.layout.activity_bid_confirm);
        Tools.initMinToolbar(this, "Confirm Bid");
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
        if (!prevCallResolved || server == null)
            showSnackBar(this, "Error, Please try later");
        else if (price.getText().toString().equals("")) {
            showSnackBar(this, "Please enter bid price");
        } else if (desc.getText().toString().equals("")) {
            showSnackBar(this, "Please enter some message");
        } else {
            Map bid = Bid.toMap((int) Float.parseFloat(price.getText().toString()),
                    task,
                    desc.getText().toString());
            server.postBid(bid);
            prevCallResolved = false;
        }
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        finish();
    }
}
