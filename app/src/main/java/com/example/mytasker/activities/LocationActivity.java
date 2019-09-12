package com.example.mytasker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import static com.example.mytasker.util.Tools.showSnackBar;

@SuppressLint("Registered")
public class LocationActivity extends BaseActivity {

    public static int REQUEST_LOCATION = 176;
    public Double lat, lon;
    private LocationListener mLocationListener;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                showSnackBar(this, "Please give the location permission");
                mLocationListener.onLocationFetched(false, -1, -1);
            }
        }
    }

    public void setLocationListener(LocationListener mLocationListener) {
        this.mLocationListener = mLocationListener;
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

    private void startLocationUpdate() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        lon = location.getLongitude();
                        lat = location.getLatitude();
                        fusedLocationClient.removeLocationUpdates(mLocationCallback);
                    }
                }
            }

        };
        if (checkLocationPermission()) {
            fusedLocationClient
                    .requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    public void getLocation() {
        if (lon != null && lat != null) {
            mLocationListener.onLocationFetched(true, lon, lat);
        } else if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            lon = location.getLongitude();
                            lat = location.getLatitude();
                            mLocationListener.onLocationFetched(true, lon, lat);
                        } else showSnackBar(LocationActivity.this, "Location can't be fetched");
                    });
        }
    }

    public interface LocationListener {
        void onLocationFetched(boolean success, double lon, double lat);
    }
}
