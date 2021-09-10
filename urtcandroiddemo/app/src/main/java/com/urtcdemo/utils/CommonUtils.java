package com.urtcdemo.utils;

import android.support.annotation.IntDef;

public class CommonUtils {
    public static int mItemWidth;
    public static int mItemHeight;
    public static int videoprofilesel = 1;

    public static final int camera_capture_mode = 1;
    public static final int audio_capture_mode = 2;
    public static final int screen_capture_mode = 3;
    public static final int screen_Audio_mode = 4;
    public static final int multi_capture_mode = 5;

    public static final int AUTO_MODE = 0;
    public static final int MANUAL_MODE = 1;

    public static final boolean CAMERA_ON = true;
    public static final boolean SCREEN_OFF = false;
    public static final boolean MIC_ON = true;
    public static final boolean HARDWARE_ACC = false;

    public static final int nv21_format = 1;
    public static final int nv12_format = 2;
    public static final int i420_format = 3;
    public static final int rgba_format = 4;
    public static final int argb_format = 5;
    public static final int rgb24_format = 6;
    public static final int rgb565_format = 7;

    public static final String videoprofile = "videoprofile";
    public static final String capture_mode = "capturemode";
    public static final String CAMERA_ENABLE = "CAMERA_ENABLE";
    public static final String MIC_ENABLE = "MIC_ENABLE";
    public static final String SCREEN_ENABLE = "SCREEN_ENABLE";
    public static final String PUBLISH_MODE = "PUBLISH_MODE";
    public static final String SUBSCRIBE_MODE = "SUBSCRIBE_MODE";
    public static final String SDK_STREAM_ROLE = "SDK_STREAM_ROLE";
    public static final String SDK_CLASS_TYPE = "SDK_CLASS_TYPE";
    public static final String CAMERA_CAPTURE_MODE = "CAMERA_CAPTURE_MODE";
    public static final String PRIVATISATION_MODE = "PRIVATISATION_MODE";
    public static final String EXTEND_CAMERA_VIDEO_FORMAT = "EXTEND_CAMERA_VIDEO_FORMAT";
    public static final String VIDEO_HW_ACC = "VIDEO_HW_ACC";

    public static final String APP_ID_TAG = "APPID";
    public static final String APP_ID = "urtc-bjeqcgyt";
    public static final String APP_KEY = "adcfa9ce62da3883e39a8b8d3cd74553";
    public static final String PRIVATISATION_ADDRESS = "PRIVATISATION_ADDRESS";
    public static final String BUCKET = "";
    public static final String REGION = "";

    @IntDef({AUTO_MODE, MANUAL_MODE})
    public @interface  PubScribeMode {}
}
