package com.urtcdemo.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

public class EDensityUtils {

    //    private static final float  WIDTH = 480;//参考设备的宽，单位是dp DPI:640
    //    private static final float  WIDTH = 640;//参考设备的宽，单位是dp DPI:480
    //    private static final float  WIDTH = 960;//参考设备的宽，单位是dp DPI:320
    private static final float  WIDTH = 1920;//参考设备的宽，单位是dp DPI:160时
    private static float appDensity;//表示屏幕密度
    private static float appScaleDensity; //字体缩放比例，默认appDensity

    private static final String TAG = "EDensityUtils";
    private EDensityUtils() {
        throw new UnsupportedOperationException("you can't instantiate EDensityUtils...");
    }

    public static void setDensity(final Application application, Activity activity){
        //获取当前app的屏幕显示信息
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (appDensity == 0){
            //初始化赋值操作
            appDensity = displayMetrics.density;
            Log.d(TAG, "appDensity: " + appDensity);
            appScaleDensity = displayMetrics.scaledDensity;
            Log.d(TAG, "appScaleDensity: " + appScaleDensity);

            //添加字体变化监听回调
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体发生更改，重新对scaleDensity进行赋值
                    if (newConfig != null && newConfig.fontScale > 0){
                        appScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                        Log.d(TAG, "onConfigurationChanged appScaleDensity: " + appScaleDensity);
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        //计算目标值density, scaleDensity, densityDpi
//        float targetDensity = displayMetrics.widthPixels / WIDTH; // 1920 / 1920 = 1.0
//        float targetScaleDensity = targetDensity * (appScaleDensity / appDensity);
        float targetDensity = 3.0f; // 1920 / 1920 = 1.0
        float targetScaleDensity = 3.0f;
        int targetDensityDpi = (int) (targetDensity * 160);
        Log.d(TAG, "targetDensity: " + targetDensity);
        Log.d(TAG, "targetScaleDensity: " + targetScaleDensity);
        //替换Activity的density, scaleDensity, densityDpi
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        dm.density = targetDensity;
        dm.scaledDensity = targetScaleDensity;
        dm.densityDpi = targetDensityDpi;
    }

}
