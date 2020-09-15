package com.urtcdemo.Application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaCodecInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.github.moduth.blockcanary.BlockCanaryContext;
import com.tencent.bugly.crashreport.CrashReport;
//import com.ucloudrtclib.sdkengine.define.UcloudRtcSdkPushEncode;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkPushEncode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkPushOrentation;
import com.ucloudrtclib.sdkengine.listener.UCloudRtcSdkEventListener;
import com.urtcdemo.BuildConfig;
import com.urtcdemo.utils.CommonUtils;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkLogLevel;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMode;
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

    public UCloudRtcSdkEngine createRtcEngine(UCloudRtcSdkEventListener eventListener){
        if(rtcSdkEngine == null){
            rtcSdkEngine = UCloudRtcSdkEngine.createEngine(eventListener);
        }else{
            rtcSdkEngine.setEventListener(eventListener);
        }
        return  rtcSdkEngine;
    }

    public void destroyEngine(){
        if(rtcSdkEngine != null){
            Log.d(TAG, "destroyEngine: ");
            UCloudRtcSdkEngine.destory();
            rtcSdkEngine = null;
        }
    }

    private void init(){
        sContext = this;
        UCloudRtcSdkEnv.initEnv(getApplicationContext());
        UCloudRtcSdkEnv.setWriteToLogCat(true);
        UCloudRtcSdkEnv.setLogReport(true);
        UCloudRtcSdkEnv.setEncodeMode(UCloudRtcSdkPushEncode.UCLOUD_RTC_PUSH_ENCODE_MODE_H264);
        UCloudRtcSdkEnv.setHDMIRenderByTexture(true);
        UCloudRtcSdkEnv.setMediaCodecBitRateMode(MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
        UCloudRtcSdkEnv.setLogLevel(UCloudRtcSdkLogLevel.UCLOUD_RTC_SDK_LogLevelInfo);
        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIVAL);
        UCloudRtcSdkEnv.setReConnectTimes(60);
        UCloudRtcSdkEnv.setTokenSeckey(CommonUtils.SEC_KEY);

//        UCloudRtcSdkEnv.setPushOrientation(UCloudRtcSdkPushOrentation.UCLOUD_RTC_PUSH_LANDSCAPE_MODE);
        //私有化部署
//        UCloudRtcSdkEnv.setPrivateDeploy(true);
//        UCloudRtcSdkEnv.setPrivateDeployRoomURL("wss://223.82.75.118:5005/ws");
        //无限重连
//        UCloudRtcSdkEnv.setReConnectTimes(-1);
        //默认vp8编码，可以改成h264
//        UCloudRtcSdkEnv.setEncodeMode(UcloudRtcSdkPushEncode.);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
       // CommonUtils.mItemWidth = (outMetrics.widthPixels - UiHelper.dipToPx(this, 15)) / 6;
        CommonUtils.mItemWidth = (UiHelper.dipToPx(this, 80)) ;
//        CommonUtils.mItemWidth = outMetrics.widthPixels  / 3;
        CommonUtils.mItemHeight = CommonUtils.mItemWidth;
        CrashReport.initCrashReport(getApplicationContext(), "9a51ae062a", true);
//        BlockCanary.install(this, new AppContext()).start();
    }

    //参数设置
    public class AppContext extends BlockCanaryContext {
        private static final String TAG = "AppContext";

        @Override
        public String provideQualifier() {
            String qualifier = "";
            try {
                PackageInfo info = UCloudRtcApplication.getAppContext().getPackageManager()
                        .getPackageInfo(UCloudRtcApplication.getAppContext().getPackageName(), 0);
                qualifier += info.versionCode + "_" + info.versionName + "_YYB";
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "provideQualifier exception", e);
            }
            return qualifier;
        }

        @Override
        public int provideBlockThreshold() {
            return 500;
        }

        @Override
        public boolean displayNotification() {
            return BuildConfig.DEBUG;
        }

        @Override
        public boolean stopWhenDebugging() {
            return false;
        }
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
