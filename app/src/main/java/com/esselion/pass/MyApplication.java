package com.esselion.pass;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.esselion.pass.broadcastReceivers.ConnectionReceiver;
import com.esselion.pass.util.Cache;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    public static void fetchLocation(Cache.LocationListener listener) {
        if (Cache.getNullLocation() == null) {
            if (ActivityCompat.checkSelfPermission(MyApplication.getInstance()
                    .getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            FusedLocationProviderClient fusedLocationClient;
            fusedLocationClient = LocationServices.getFusedLocationProviderClient
                    (MyApplication.getInstance().getApplicationContext());
            LocationRequest mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setExpirationDuration(120 * 1000); // 2 minutes, in milliseconds
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            Cache.initLocation(location);
                            if (listener != null) listener.onLocationFetched(null);
                            fusedLocationClient.removeLocationUpdates(this);
                        }
                    }
                }
            };
            fusedLocationClient.
                    requestLocationUpdates(mLocationRequest,
                            mLocationCallback,
                            Looper.getMainLooper());
        }
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectionListener(ConnectionReceiver.ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        fetchLocation(null);
    }
}