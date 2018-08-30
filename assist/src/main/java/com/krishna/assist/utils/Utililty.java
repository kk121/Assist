package com.krishna.assist.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Utililty {
    private static final String TAG = "Util";

    public static Map<String, String> getDeviceInfo(Context ctx) {
        final Map<String, String> deviceInfo = new HashMap<String, String>();
        deviceInfo.put("android_os_version", Build.VERSION.RELEASE == null ? "UNKNOWN" : Build.VERSION.RELEASE);
        deviceInfo.put("android_manufacturer", Build.MANUFACTURER == null ? "UNKNOWN" : Build.MANUFACTURER);
        deviceInfo.put("android_brand", Build.BRAND == null ? "UNKNOWN" : Build.BRAND);
        deviceInfo.put("android_model", Build.MODEL == null ? "UNKNOWN" : Build.MODEL);
        try {
            final PackageManager manager = ctx.getPackageManager();
            final PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            deviceInfo.put("app_name", getApplicationName(ctx));
            deviceInfo.put("android_app_version", info.versionName);
            deviceInfo.put("android_app_version_code", Integer.toString(info.versionCode));
        } catch (final PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Exception getting app version name");
        }
        return deviceInfo;
    }

    private static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}
