package com.urtcdemo.utils;

import android.content.Context;
import android.widget.Toast;

import com.cmcc.sdkengine.CMCCRtcEnv;

public class ToastUtils {
    public static void shortShow(Context context, String msg) {
        if(CMCCRtcEnv.getApplication() != null)
        Toast.makeText(CMCCRtcEnv.getApplication().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void longShow(Context context, String msg) {
        if(CMCCRtcEnv.getApplication() != null)
        Toast.makeText(CMCCRtcEnv.getApplication().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
