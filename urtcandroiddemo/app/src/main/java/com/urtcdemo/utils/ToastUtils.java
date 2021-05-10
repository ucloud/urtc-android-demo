package com.urtcdemo.utils;

import android.content.Context;
import android.widget.Toast;

import com.cmcc.sdkengine.CMCCEnvHelper;

public class ToastUtils {
    public static void shortShow(Context context, String msg) {
        if(CMCCEnvHelper.getApplication() != null)
        Toast.makeText(CMCCEnvHelper.getApplication().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void longShow(Context context, String msg) {
        if(CMCCEnvHelper.getApplication() != null)
        Toast.makeText(CMCCEnvHelper.getApplication().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
