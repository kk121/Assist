package com.krishna.assist.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private static AesCbcWithIntegrity.SecretKeys generateSecretKeys(Context context, String password) {
        //use the password to generate the key
        AesCbcWithIntegrity.SecretKeys keys = null;
        try {
            final byte[] salt = getDeviceSerialNumber(context).getBytes();
            keys = AesCbcWithIntegrity.generateKeyFromPassword(password, salt);
            if (keys == null) {
                throw new GeneralSecurityException("Problem generating Key From Password");
            }
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Error init using user password:" + e.getMessage());
            throw new IllegalStateException(e);
        }
        return keys;
    }

    /**
     * Gets the hardware serial number of this device.
     *
     * @return serial number or Settings.Secure.ANDROID_ID if not available.
     */
    private static String getDeviceSerialNumber(Context context) {
        // We're using the Reflection API because Build.SERIAL is only available
        // since API Level 9 (Gingerbread, Android 2.3).
        try {
            String deviceSerial = (String) Build.class.getField("SERIAL").get(
                    null);
            if (TextUtils.isEmpty(deviceSerial)) {
                return Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                return deviceSerial;
            }
        } catch (Exception ignored) {
            // Fall back  to Android_ID
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
    }

    /**
     * The Pref keys must be same each time so we're using a hash to obscure the stored value
     *
     * @param prefKey
     * @return SHA-256 Hash of the preference key
     */
    public static String hashPrefKey(String prefKey) {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = prefKey.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);

            return Base64.encodeToString(digest.digest(), AesCbcWithIntegrity.BASE64_FLAGS);

        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "Problem generating hash", e);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "Problem generating hash", e);
        }
        return null;
    }

    /**
     * @param ciphertext
     * @return decrypted plain text, unless decryption fails, in which case null
     */
    public static String decrypt(Context context, final String ciphertext, String password) {
        if (TextUtils.isEmpty(ciphertext)) {
            return ciphertext;
        }
        AesCbcWithIntegrity.SecretKeys keys = generateSecretKeys(context, password);
        try {
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(ciphertext);

            return AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys);
        } catch (GeneralSecurityException e) {
            Log.w(TAG, "decrypt", e);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "decrypt", e);
        }
        return null;
    }

    public String encrypt(String cleartext, AesCbcWithIntegrity.SecretKeys keys) {
        if (TextUtils.isEmpty(cleartext)) {
            return cleartext;
        }
        try {
            return AesCbcWithIntegrity.encrypt(cleartext, keys).toString();
        } catch (GeneralSecurityException e) {
            Log.w(TAG, "encrypt", e);
            return null;
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "encrypt", e);
        }
        return null;
    }
}
