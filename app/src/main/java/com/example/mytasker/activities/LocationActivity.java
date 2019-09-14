package com.example.mytasker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

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
    private static boolean locationOn = false;
    private static boolean locationOnShown = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000)
                .setExpirationDuration(120 * 1000); // 2 minutes, in milliseconds
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        fusedLocationClient.removeLocationUpdates(mLocationCallback);
                    }
                }
            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private boolean checkLocationOn() {
        if (locationOn) return true;
        else {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, locationSettingsResponse -> {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                locationOn = true;
                startLocationUpdates();
            });

            task.addOnFailureListener(this, e -> {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    if (!locationOnShown)
                        try {
                            locationOnShown = true;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(LocationActivity.this,
                                    100);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                }
            });
        }
        return false;
    }


    private void startLocationUpdates() {
        if (checkLocationPermission())
            fusedLocationClient.
                    requestLocationUpdates(mLocationRequest,
                            mLocationCallback,
                            Looper.getMainLooper());
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

    public void getLocation() {
        if (lon != null && lat != null) {
            mLocationListener.onLocationFetched(true, lon, lat);
        } else if (checkLocationPermission()) {
//            locationOnShown = false;
            if (checkLocationOn()) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                lon = location.getLongitude();
                                lat = location.getLatitude();
                                mLocationListener.onLocationFetched(true, lon, lat);
                            } else mLocationListener.onLocationFetched(false, -1, -1);

                        });
            } else mLocationListener.onLocationFetched(false, -1, -1);
        }
    }

    public interface LocationListener {
        void onLocationFetched(boolean success, double lon, double lat);
    }
}
