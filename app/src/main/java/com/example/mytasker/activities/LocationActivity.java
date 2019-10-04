package com.example.mytasker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
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
    private static boolean locationOn = false;
    private static boolean locationOnShown = false;
    private LocationListener mLocationListener;

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
                startLocationUpdates(mLocationListener);
            });

            task.addOnFailureListener(this, e -> {
                if (e instanceof ResolvableApiException) {
                    if (!locationOnShown)
                        try {
                            locationOnShown = true;
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


    public void startLocationUpdates(LocationListener locationListener) {
        mLocationListener = locationListener;
        if (checkLocationPermission() && checkLocationOn()) {
            if (server != null) server.getLocation(locationListener);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates(mLocationListener);
            } else {
                showSnackBar(this, "Please give the location permission");
            }
        }
    }

    public interface LocationListener {
        void onLocationFetched(Location location);
    }
}
