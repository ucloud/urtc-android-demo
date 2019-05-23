package com.urtcdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionUtils {
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void needsPermissions(Context context, String[] permission) {
        if (!hasPermissions(context, permission)) {
            ActivityCompat.requestPermissions((Activity) context, permission, 1);
        }
    }
}
