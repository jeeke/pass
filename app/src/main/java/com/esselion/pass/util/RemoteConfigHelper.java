package com.esselion.pass.util;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import static com.esselion.pass.util.Tools.getCurrentVersionCode;

public class RemoteConfigHelper {

    private static final String VERSION_CODE_KEY = "latest_app_version";
    private static final String SUPPORT_IDS = "support_ids";
    private final static RemoteConfigHelper configHelper = new RemoteConfigHelper();
    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    private RemoteConfigHelper() {
        Map<String, Object> defaults = new HashMap<>();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        defaults.put(VERSION_CODE_KEY, getCurrentVersionCode());
        defaults.put(SUPPORT_IDS, "UOJktSpfyxPZ33Z1abvZLrJwkrq2");
        mFirebaseRemoteConfig.setDefaultsAsync(defaults);
        mFirebaseRemoteConfig.fetchAndActivate();
    }

    public static RemoteConfigHelper getInstance() {
        return configHelper;
    }

    public boolean hasUpdate() {
        int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
        return latestAppVersion > getCurrentVersionCode();
    }

    public String[] getSupportIds() {
        return mFirebaseRemoteConfig.getString(SUPPORT_IDS).split(" ");
    }


}