package com.urtcdemo.Application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.tencent.bugly.crashreport.CrashReport;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkLogLevel;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkPushEncode;
import com.ucloudrtclib.sdkengine.listener.UCloudRtcSdkEventListener;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.UiHelper;

public class UCloudRtcApplication extends Application {

    private static final String TAG = "UCloudRtcApplication";
    private static Context sContext;
    private static String sUserId;
    private static UCloudRtcSdkEngine rtcSdkEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + this);
        if (TextUtils.equals(getCurrentProcessName(this), getPackageName())) {
//            Log.d(TAG, "init: ");
            init();//判断成功后才执行初始化代码
        }
    }

    public UCloudRtcSdkEngine createRtcEngine(UCloudRtcSdkEventListener eventListener) {
        if (rtcSdkEngine == null) {
            rtcSdkEngine = UCloudRtcSdkEngine.createEngine(eventListener);
        } else {
            rtcSdkEngine.setEventListener(eventListener);
        }
        return rtcSdkEngine;
    }

    public void destroyEngine() {
        if (rtcSdkEngine != null) {
            Log.d(TAG, "destroyEngine: ");
            UCloudRtcSdkEngine.destroy();
            rtcSdkEngine = null;
        }
    }

    private void init() {
        sContext = this;
        UCloudRtcSdkEnv.initEnv(getApplicationContext());
        UCloudRtcSdkEnv.setWriteToLogCat(true);
        UCloudRtcSdkEnv.setLogReport(true);
        UCloudRtcSdkEnv.setEncodeMode(UCloudRtcSdkPushEncode.UCLOUD_RTC_PUSH_ENCODE_MODE_H264);
        UCloudRtcSdkEnv.setLogLevel(UCloudRtcSdkLogLevel.UCLOUD_RTC_SDK_LogLevelInfo);
        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIAL);
        UCloudRtcSdkEnv.setReConnectTimes(60);
        UCloudRtcSdkEnv.setTokenSecKey(CommonUtils.APP_KEY);
        UCloudRtcSdkEnv.setVideoTrackOpenCamera(true);
        //UCloudRtcSdkEnv.setDeviceChannelType(UCloudRtcSdkChannelType.UCLOUD_RTC_SDK_CHANNEL_TYPE_VOICE);
        //推流方向
//        UCloudRtcSdkEnv.setPushOrientation(UCloudRtcSdkPushOrentation.UCLOUD_RTC_PUSH_PORTRAIT_MODE);
        //视频输出模式
//        UCloudRtcSdkEnv.setVideoOutputOrientation(UCloudRtcSdkVideoOutputOrientationMode.UCLOUD_RTC_VIDEO_OUTPUT_FIXED_PORTRAIT_MODE);
        //私有化部署
//        UCloudRtcSdkEnv.setPrivateDeploy(true);
//        UCloudRtcSdkEnv.setPrivateDeployRoomURL("wss://xxx:5005/ws");
        //无限重连
//        UCloudRtcSdkEnv.setReConnectTimes(-1);
        //默认vp8编码，可以改成h264
//        UCloudRtcSdkEnv.setEncodeMode(UcloudRtcSdkPushEncode.UCLOUD_RTC_PUSH_ENCODE_MODE_H264);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        CommonUtils.mItemWidth = (outMetrics.widthPixels - UiHelper.dipToPx(this, 15)) / 3;
        CommonUtils.mItemHeight = CommonUtils.mItemWidth;
        CrashReport.initCrashReport(getApplicationContext(), "a2b3f83b36", true);
    }

    public static Context getAppContext() {
        return sContext;
    }

    public static UCloudRtcApplication getInstance() {
        return (UCloudRtcApplication) sContext;
    }

    public static String getUserId() {
        return sUserId;
    }

    public static void setUserId(String userId) {
        sUserId = userId;
    }

    private String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
