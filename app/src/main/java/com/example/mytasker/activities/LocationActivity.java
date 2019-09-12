package com.example.mytasker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import static com.example.mytasker.util.Tools.showSnackBar;

@SuppressLint("Registered")
public class LocationActivity extends BaseActivity {

    public static int REQUEST_LOCATION = 176;
    public Double lat, lon;
    private LocationListener mLocationListener;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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

    public void getLocation() {
        if (lon != null && lat != null) {
            mLocationListener.onLocationFetched(true, lon, lat);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
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
