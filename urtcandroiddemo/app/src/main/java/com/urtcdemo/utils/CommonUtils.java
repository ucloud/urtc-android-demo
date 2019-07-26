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

    public static final int TEACHER_ROLE = 0;
    public static final int STUDENT_ROLE = 1;
    //这个值和SDK内部的值无关，只和业务有关
    public static final String BUS_WHITE_MODE = "BUS_WHITE_MODE";
    public static final String WHITE_ROLE = "WHITE_ROLE";
    public static final String WHITE_LOG_INFO = "WHITE_LOG_INFO";

    public static final String videoprofile = "videoprofile";
    public static final String capture_mode = "capturemode";
    public static final String PUBLISH_MODE = "PUBLISH_MODE";
    public static final String SCRIBE_MODE = "SCRIBE_MODE";

    public static final String SDK_STREAM_ROLE = "SDK_STREAM_ROLE";
    public static final String SDK_CLASS_TYPE = "SDK_CLASS_TYPE";

    public static final String APPID_KEY = "APPID_KEY";
    //    public static final String APP_ID = "urtc-cwykkqyx";
////    public static final String SEC_KEY = "b3b59d28dea5e4eb3318d0a441c0801c" ;
    public static final String APP_ID = "URtc-h4r1txxy";
    public static final String SEC_KEY = "9129304dbf8c5c4bf68d70824462409f";

    @IntDef({AUTO_MODE, MANUAL_MODE})
    public @interface  PubScribeMode {}

    @IntDef({TEACHER_ROLE, STUDENT_ROLE})
    public @interface  WhiteRoleType {}
}
