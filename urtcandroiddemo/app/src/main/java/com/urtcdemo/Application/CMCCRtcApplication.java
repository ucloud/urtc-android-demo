package com.urtcdemo.Application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.github.moduth.blockcanary.BlockCanaryContext;
import com.tencent.bugly.crashreport.CrashReport;
import com.cmcc.sdkengine.CMCCRtcEngine;
import com.cmcc.sdkengine.CMCCEnvHelper;
import com.cmcc.sdkengine.define.CMCCLogLevel;
import com.cmcc.sdkengine.define.CMCCSDKMode;
import com.cmcc.sdkengine.define.CMCCPushEncode;
import com.cmcc.sdkengine.listener.ICMCCRtcEngineEventHandler;
import com.urtcdemo.BuildConfig;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.UiHelper;

public class CMCCRtcApplication extends Application {

    private static final String TAG = "UCloudRtcApplication";
    private static Context sContext;
    private static String sUserId;
    private static CMCCRtcEngine rtcSdkEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + this);
        if (TextUtils.equals(getCurrentProcessName(this), getPackageName())) {
//            Log.d(TAG, "init: ");
            init();//判断成功后才执行初始化代码
        }
    }

    public CMCCRtcEngine createRtcEngine(ICMCCRtcEngineEventHandler eventListener){
        if(rtcSdkEngine == null){
            rtcSdkEngine = CMCCRtcEngine.create(eventListener);
        }else{
            rtcSdkEngine.setEventListener(eventListener);
        }
        return  rtcSdkEngine;
    }

    public void destroyEngine(){
        if(rtcSdkEngine != null){
            Log.d(TAG, "destroyEngine: ");
            CMCCRtcEngine.destroy();
            rtcSdkEngine = null;
        }
    }

    private void init(){
        sContext = this;
        CMCCEnvHelper.initEnv(getApplicationContext());
        CMCCEnvHelper.setWriteToLogCat(true);
        CMCCEnvHelper.setLogReport(true);
        CMCCEnvHelper.setEncodeMode(CMCCPushEncode.PUSH_ENCODE_MODE_H264);
        CMCCEnvHelper.setLogLevel(CMCCLogLevel.LOG_LEVEL_INFO);
        CMCCEnvHelper.setSdkMode(CMCCSDKMode.MODE_TRIVIAL);
        CMCCEnvHelper.setReConnectTimes(60);
        CMCCEnvHelper.setTokenSecKey(CommonUtils.APP_KEY);
        //UCloudRtcSdkEnv.setDeviceChannelType(UCloudRtcSdkChannelType.UCLOUD_RTC_SDK_CHANNEL_TYPE_VOICE);
        //推流方向
        //UCloudRtcSdkEnv.setPushOrientation(UCloudRtcSdkPushOrentation.UCLOUD_RTC_PUSH_LANDSCAPE_MODE);
        //视频输出模式
        //UCloudRtcSdkEnv.setVideoOutputOrientation(UCloudRtcSdkVideoOutputOrientationMode.UCLOUD_RTC_VIDEO_OUTPUT_FIXED_LANDSCAPE_MODE);
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
                PackageInfo info = CMCCRtcApplication.getAppContext().getPackageManager()
                        .getPackageInfo(CMCCRtcApplication.getAppContext().getPackageName(), 0);
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

    public static CMCCRtcApplication getInstance() {
        return (CMCCRtcApplication) sContext;
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
            if (appProcess.pid ==  pid) {
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
