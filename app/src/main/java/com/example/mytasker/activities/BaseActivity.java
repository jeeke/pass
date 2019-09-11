package com.example.mytasker.activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mytasker.MyApplication;
import com.example.mytasker.R;
import com.example.mytasker.broadcastReceivers.ConnectionReceiver;
import com.example.mytasker.util.Server;

import static com.example.mytasker.MyFirebaseMessagingService.CHANNEL_ID;
import static com.example.mytasker.util.Tools.showSnackBar;

public abstract class BaseActivity extends AppCompatActivity implements ConnectionReceiver.ConnectionReceiverListener, Server.ServerCallCompleteListener {

    private static final BroadcastReceiver MyReceiver = new ConnectionReceiver();
    private static final int REQUEST_PERMISSIONS = 177;

    public Server server;

    @Override
    protected void onPause() {
        super.onPause();
        Server.setServerCallCompleteListener(null);
    }
    boolean prevCallResolved = true;

    public static void checkPermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        checkPermission(this);
    }

    boolean mBound = false;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PASS";
            String description = "PASS RELATED NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            server = ((Server.ServerBinder) service).getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            //show a No Internet Alert or Dialog
            setContentView(R.layout.error_page);
            showSnackBar(this, "No Internet Connection");
            Button click = findViewById(R.id.retry);
            click.setOnClickListener(v -> checkConnection());
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (isConnected) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(MyReceiver);
        unbindService(connection);
        mBound = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        MyApplication.getInstance().setConnectionListener(this);
        Server.setServerCallCompleteListener(this);
        Intent intent = new Intent(this, Server.class);
        startService(intent);
        bindService(intent, connection, 0);
    }
    //    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
//
//    }
//
//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServerCallSuccess(String title) {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        prevCallResolved = true;
        showSnackBar(findViewById(android.R.id.content), title);
    }

    @Override
    public void onServerCallFailure(String title, Server.OnRetryListener retryListener) {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        prevCallResolved = true;
        showSnackBar(findViewById(android.R.id.content), title, retryListener);
    }


}
