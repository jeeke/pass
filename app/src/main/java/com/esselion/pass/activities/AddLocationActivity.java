package com.esselion.pass.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.models.Location;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.Tools;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import static com.esselion.pass.util.Tools.showSnackBar;

public class AddLocationActivity extends BaseActivity {

    EditText tag, desc;
    Location locationData;
    TextView selectedAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Tools.initMinToolbar(this, "My Locations");
        tag = findViewById(R.id.tag);
        desc = findViewById(R.id.desc);
        findViewById(R.id.save_location).setOnClickListener(v -> checkNSaveLoc());
        findViewById(R.id.editLocation).setOnClickListener(v -> {
            Cache.getLocation(location -> {
                Intent intent = new PlacePicker.IntentBuilder()
                        .setLatLong(location.lat, location.lon)  // Initial Latitude and Longitude the Map will load into
                        .showLatLong(true)  // Show Coordinates in the Activity
                        .setMapZoom(14.0f)  // Map Zoom Level. Default: 14.0
                        .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                        .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
//                        .setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
                        .setMarkerImageImageColor(R.color.orange)
                        .setFabColor(R.color.orange)
                        .setPrimaryTextColor(R.color.blue_grey) // Change text color of Shortened Address
                        .setSecondaryTextColor(R.color.blue_grey) // Change text color of full Address
//                        .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
                        .setMapType(MapType.NORMAL)
//                        .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                        .build(AddLocationActivity.this);
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
            });

        });
        selectedAdd = findViewById(R.id.selected_location);
    }

    private void populateAddressData(AddressData addressData) {
        try {
            locationData = new Location();
            locationData.desc = addressData.getAddressList().get(0).getAddressLine(0);
            locationData.lat = addressData.getLatitude();
            locationData.lon = addressData.getLongitude();
            desc.setText(locationData.desc);
            selectedAdd.setText("Dropped Pin");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                populateAddressData(addressData);
//                Log.e("Location Data",addressData.toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkNSaveLoc() {
        String t = tag.getText().toString();
        String d = desc.getText().toString();
        if (t.equals("")) {
            showSnackBar(this, "Please add a tag");
            return;
        }
        if (locationData == null) {
            Cache.getLocation(location -> {
                locationData = new Location();
                locationData.lat = location.getLatitude();
                locationData.lon = location.getLongitude();
                locationData.desc = d;
                locationData.tag = t;
                if (server != null)
                    server.saveLocation(Cache.getUser().getUid(), locationData);
            });
        } else if (server != null) {
            locationData.tag = t;
            server.saveLocation(Cache.getUser().getUid(), locationData);
        }

    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_SAVE_LOCATION) finish();
    }
}
