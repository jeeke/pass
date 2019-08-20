package com.example.mytasker.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytasker.MyApplication;
import com.example.mytasker.R;
import com.example.mytasker.broadcastReceivers.ConnectionReceiver;

public abstract class BaseActivity extends AppCompatActivity implements ConnectionReceiver.ConnectionReceiverListener {

    private static final BroadcastReceiver MyReceiver = new ConnectionReceiver();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            //show a No Internet Alert or Dialog
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.error_page);
            Button click=findViewById(R.id.retry);
            click.setOnClickListener(v -> checkConnection());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        registerReceiver(MyReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        MyApplication.getInstance().setConnectionListener(this);
    }


    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if(isConnected) {
            //show a No Internet Alert or Dialog
            finish();
            startActivity(getIntent());
//            Toast.makeText(this, "Internet Connected Alive", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MyReceiver);
    }





    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
