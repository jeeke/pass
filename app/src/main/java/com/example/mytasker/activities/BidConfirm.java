package com.example.mytasker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytasker.R;
import com.example.mytasker.models.Bid;
import com.example.mytasker.retrofit.NullOnEmptyConverterFactory;
import com.example.mytasker.util.Contracts;
import com.example.mytasker.util.Tools;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BidConfirm extends BaseActivity {


    EditText price,desc,contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            callRetrofit();
        }
    }

    private void callRetrofit() {

        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setTitle("Posting your Bid..");
        dlg.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contracts.BASE_POST_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Bid bid = new Bid("rakesh","Gyanesh",desc.getText().toString(),
                contact.getText().toString(),
                Integer.parseInt(price.getText().toString()));

        API api = new API();
        api.createTask(task).addOnCompleteListener(t -> {
            dlg.dismiss();
            if (!t.isSuccessful()) {
                Exception e = t.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                    Log.e("tag", ffe + "\n" + code + "\n" + details);
                }
                Toast.makeText(this, "Posting Unsuccessful", Toast.LENGTH_SHORT).show();
                Log.e("tag", e + "");
                return;
            }
            Toast.makeText(this, t.getResult().getMessage() + "", Toast.LENGTH_SHORT).show();
        });
    }
}
