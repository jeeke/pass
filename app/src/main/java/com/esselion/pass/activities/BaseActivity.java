package com.esselion.pass.activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esselion.pass.MyApplication;
import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.broadcastReceivers.ConnectionReceiver;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.Tools;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import static com.esselion.pass.MyFirebaseMessagingService.CHANNEL_ID;
import static com.esselion.pass.util.Tools.showSnackBar;

public abstract class BaseActivity extends AppCompatActivity implements ConnectionReceiver.ConnectionReceiverListener, Server.ServerCallCompleteListener {

    private static final BroadcastReceiver MyReceiver = new ConnectionReceiver();
    private static final int REQUEST_PERMISSIONS = 177;

    public Server server;
    boolean prevCallResolved = true;

    public static int REQUEST_LOCATION = 176;
    private static boolean locationOn = false;
    private static boolean locationOnShown = false;
    public static final int REQUEST_TURN_ON_LOCATION = 4327;

    public static void checkPermission(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET},
                    REQUEST_PERMISSIONS);
        }
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            return false;
        }
        return true;
    }

    private boolean checkLocationOn() {
        if (locationOn) return true;
        else {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(5000)
                            .setExpirationDuration(120 * 1000)); // 2 minutes, in milliseconds);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, locationSettingsResponse -> {
                locationOn = true;
                startLocationUpdates();
                onRequestPermissionsResult(REQUEST_TURN_ON_LOCATION, new String[0], new int[0]);
            });
            task.addOnFailureListener(this, e -> {
                if (e instanceof ResolvableApiException) {
                    if (!locationOnShown)
                        try {
                            locationOnShown = true;
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(BaseActivity.this,
                                    100);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                }
            });
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                showSnackBar(this, "Please give the location permission");
            }
        }
    }

    public void startLocationUpdates() {
        if (!(this instanceof MainActivity) && !(this instanceof LoginActivity))
            if (checkLocationPermission())
                if (checkLocationOn()) Cache.getLocation(null);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        checkPermission(this);
        startLocationUpdates();
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
            setContentView(R.layout.no_internet);
            showSnackBar(this, "No Internet Connection");
            Button click = findViewById(R.id.retry);
            click.setOnClickListener(v -> checkConnection());
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (isConnected) {
            Tools.finishNLaunchActivity(this, getIntent());
        }
    }

    ProgressBar progressBar;

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(MyReceiver);
        unbindService(connection);
        mBound = false;
//TODO resolve this issue
//        Server.setServerCallCompleteListener(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar = findViewById(R.id.progress_bar);
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        MyApplication.getInstance().setConnectionListener(this);
        Server.setServerCallCompleteListener(this);
        Intent intent = new Intent(this, Server.class);
        startService(intent);
        bindService(intent, connection, 0);
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

    public void showProgressBar(boolean visibility) {
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar != null)
            if (visibility) progressBar.setVisibility(View.VISIBLE);
            else progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        if (title != null) {
            showSnackBar(findViewById(android.R.id.content), title);
        }
        showProgressBar(false);
        prevCallResolved = true;
    }

    @Override
    public void onServerCallFailure(int methodId, String title, Server.OnRetryListener retryListener) {
        showProgressBar(false);
        prevCallResolved = true;
        showSnackBar(findViewById(android.R.id.content), title, retryListener);
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
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


}
