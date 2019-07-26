package com.urtcdemo.Application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.urtcdemo.utils.CommonUtils;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkLogLevel;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMode;
import com.urtcdemo.utils.UiHelper;

public class UCloudRtcApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UCloudRtcSdkEnv.initEnv(getApplicationContext(), this);
        UCloudRtcSdkEnv.setLogLevel(UCloudRtcSdkLogLevel.UCLOUD_RTC_SDK_LogLevelInfo) ;
        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIVAL);
        UCloudRtcSdkEnv.setTokenSeckey(CommonUtils.SEC_KEY);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        CommonUtils.mItemWidth = (outMetrics.widthPixels - UiHelper.dipToPx(this,15))/ 3;
        CommonUtils.mItemHeight = CommonUtils.mItemWidth;
    }


}
