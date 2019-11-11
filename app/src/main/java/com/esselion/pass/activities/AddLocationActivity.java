package com.esselion.pass.activities;

import android.os.Bundle;
import android.widget.EditText;

import com.esselion.pass.R;
import com.esselion.pass.Server;
import com.esselion.pass.models.Location;
import com.esselion.pass.util.Cache;
import com.esselion.pass.util.Tools;

import static com.esselion.pass.util.Tools.showSnackBar;

public class AddLocationActivity extends BaseActivity {

    EditText tag, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Tools.initMinToolbar(this, "My Locations");
        tag = findViewById(R.id.tag);
        desc = findViewById(R.id.desc);
        findViewById(R.id.save_location).setOnClickListener(v -> checkNSaveLoc());
    }

    private void checkNSaveLoc() {
        String t = tag.getText().toString();
        String d = desc.getText().toString();
        if (t.equals("")) {
            showSnackBar(this, "Please add a tag");
            return;
        }
        Cache.getLocation(location -> {
            Location loc = new Location();
            loc.lat = location.getLatitude();
            loc.lon = location.getLongitude();
            loc.desc = d;
            loc.tag = t;
            if (server != null)
                server.saveLocation(Cache.getUser().getUid(), loc);
        });
    }

    @Override
    public void onServerCallSuccess(int methodId, String title) {
        super.onServerCallSuccess(methodId, title);
        if (methodId == Server.SERVER_SAVE_LOCATION) finish();
    }
}
