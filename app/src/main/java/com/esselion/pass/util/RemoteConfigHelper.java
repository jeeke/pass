package com.esselion.pass.util;

import android.content.Context;
import android.content.pm.PackageManager;

import com.esselion.pass.MyApplication;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class RemoteConfigHelper {

    private static final String VERSION_CODE_KEY = "latest_app_version";
    private static final String SUPPORT_IDS = "support_ids";
    private final static RemoteConfigHelper configHelper = new RemoteConfigHelper();
    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    private RemoteConfigHelper() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(VERSION_CODE_KEY, getCurrentVersionCode(MyApplication.getInstance()));
        defaults.put(SUPPORT_IDS, "UOJktSpfyxPZ33Z1abvZLrJwkrq2");
        mFirebaseRemoteConfig.setDefaultsAsync(defaults);
        mFirebaseRemoteConfig.fetchAndActivate();
    }

    public static RemoteConfigHelper getInstance() {
        return configHelper;
    }

    public boolean hasUpdate() {
        int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
        return latestAppVersion > getCurrentVersionCode(MyApplication.getInstance());
    }

    public String[] getSupportIds() {
        return mFirebaseRemoteConfig.getString(SUPPORT_IDS).split(" ");
    }

    private int getCurrentVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}