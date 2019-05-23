package com.urtcdemo.Application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.urtcdemo.utils.CommonUtils;
import com.urtclib.sdkengine.UCloudRtcSdkEnv;
import com.urtclib.sdkengine.define.UCloudRtcSdkLogLevel;
import com.urtclib.sdkengine.define.UCloudRtcSdkMode;

public class URTCApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UCloudRtcSdkEnv.initEnv(getApplicationContext(), this);
        UCloudRtcSdkEnv.setLogLevel(UCloudRtcSdkLogLevel.URTC_SDK_LogLevelDebug) ;
        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.RTC_SDK_MODE_TRIVAL);
        UCloudRtcSdkEnv.setTokenSeckey(CommonUtils.SEC_KEY);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        CommonUtils.mItemWidth = outMetrics.widthPixels / 3;
        CommonUtils.mItemHeight = CommonUtils.mItemWidth;
    }


}
