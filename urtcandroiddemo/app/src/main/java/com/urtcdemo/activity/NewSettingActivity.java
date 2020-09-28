package com.urtcdemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRoomType;
import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.StatusBarUtils;
import com.urtcdemo.utils.VideoProfilePopupWindow;
import com.urtcdemo.view.BaseSwitch;
import com.urtcdemo.view.LSwitch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class NewSettingActivity extends AppCompatActivity {
    private TextView mConfigTextView;
    private int mSelectPos = 1;
    private ArrayAdapter<String> mAdapter;
    private VideoProfilePopupWindow mSpinnerPopupWindow;

    private ImageButton mBackButton;

    @CommonUtils.PubScribeMode
    private int mPublishMode;
    @CommonUtils.PubScribeMode
    private int mSubScribeMode;
    private UCloudRtcSdkRoomType mRoomType;

    private List<String> mDefaultConfiguration = new ArrayList<>();
    private String mAppid;

    private LSwitch mCameraSwitch;
    private LSwitch mMicSwitch;
    private LSwitch mScreenShareSwitch;
    private LSwitch mAutoPubSwitch;
    private LSwitch mAutoSubSwitch;
    private LSwitch mBroadcastSwitch;

    private boolean mEnableCamera;
    private boolean mEnableMic;
    private boolean mEnableScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEnableCamera = true;
        mEnableMic = true;
        mEnableScreen = false;

        setContentView(R.layout.activity_setting_new);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mConfigTextView = findViewById(R.id.config_text_view);
        String[] configurations = getResources().getStringArray(R.array.videoResolutions);
        mDefaultConfiguration.addAll(Arrays.asList(configurations));

        mBackButton = findViewById(R.id.back_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                finish();
            }
        });

        mConfigTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        mSelectPos = preferences.getInt(CommonUtils.videoprofile, CommonUtils.videoprofilesel);
        mAppid = preferences.getString(CommonUtils.APPID_KEY, CommonUtils.APP_ID);
        mEnableCamera = preferences.getBoolean(CommonUtils.CAMERA_ENABLE, CommonUtils.CAMERA_ON);
        mEnableMic = preferences.getBoolean(CommonUtils.MIC_ENABLE, CommonUtils.MIC_ON);
        mEnableScreen = preferences.getBoolean(CommonUtils.SCREEN_ENABLE, CommonUtils.SCREEN_OFF);
        mPublishMode = preferences.getInt(CommonUtils.PUBLISH_MODE, CommonUtils.AUTO_MODE);
        mSubScribeMode = preferences.getInt(CommonUtils.SUBSCRIBE_MODE, CommonUtils.AUTO_MODE);
        int roomInt = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL.ordinal());
        mRoomType = UCloudRtcSdkRoomType.valueOf(roomInt);
        StatusBarUtils.setAndroidNativeLightStatusBar(this,true);

        mConfigTextView.setText(mDefaultConfiguration.get(mSelectPos));
        mAdapter = new ArrayAdapter<String>(this, R.layout.videoprofile_item, mDefaultConfiguration);

        mSpinnerPopupWindow = new VideoProfilePopupWindow(this);
        mSpinnerPopupWindow.setOnSpinnerItemClickListener(mOnSpinnerItemClickListener);

        mCameraSwitch = findViewById(R.id.camera_switch);
        mCameraSwitch.setOnCheckedListener(new BaseSwitch.OnCheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                mEnableCamera = isChecked;
            }
        });
        mMicSwitch = findViewById(R.id.mic_switch);
        mMicSwitch.setOnCheckedListener(new BaseSwitch.OnCheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                mEnableMic = isChecked;
            }
        });
        mScreenShareSwitch = findViewById(R.id.screen_switch);
        mScreenShareSwitch.setOnCheckedListener(new BaseSwitch.OnCheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                mEnableScreen = isChecked;
            }
        });
        mAutoPubSwitch = findViewById(R.id.pub_switch);
        mAutoPubSwitch.setOnCheckedListener(new BaseSwitch.OnCheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                mPublishMode = isChecked ? CommonUtils.AUTO_MODE : CommonUtils.MANUAL_MODE;
            }
        });
        mAutoSubSwitch = findViewById(R.id.sub_switch);
        mAutoSubSwitch.setOnCheckedListener(new BaseSwitch.OnCheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                mSubScribeMode = isChecked ? CommonUtils.AUTO_MODE : CommonUtils.MANUAL_MODE;
            }
        });
        mBroadcastSwitch = findViewById(R.id.broadcast_switch);
        mBroadcastSwitch.setOnCheckedListener(new BaseSwitch.OnCheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                mRoomType = isChecked ? UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_LARGE : UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL;

            }
        });
        mCameraSwitch.setChecked(mEnableCamera);
        mMicSwitch.setChecked(mEnableMic);
        mScreenShareSwitch.setChecked(mEnableScreen);
        mAutoPubSwitch.setChecked(mPublishMode == CommonUtils.AUTO_MODE);
        mAutoSubSwitch.setChecked(mSubScribeMode == CommonUtils.AUTO_MODE);
        mBroadcastSwitch.setChecked(mRoomType == UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_LARGE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveSettings();
    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    private void showPopupWindow() {
        mSpinnerPopupWindow.setAdapter(mAdapter);
        mSpinnerPopupWindow.setWidth(mConfigTextView.getWidth());
        mSpinnerPopupWindow.showAsDropDown(mConfigTextView);
    }

    private VideoProfilePopupWindow.OnSpinnerItemClickListener mOnSpinnerItemClickListener = new VideoProfilePopupWindow.OnSpinnerItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            mSelectPos = pos;
            mConfigTextView.setText(mDefaultConfiguration.get(mSelectPos));
            mSpinnerPopupWindow.dismiss();
        }
    };

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE).edit();

        editor.putString(CommonUtils.APPID_KEY, mAppid);
        editor.putInt(CommonUtils.videoprofile, mSelectPos);
        editor.putBoolean(CommonUtils.CAMERA_ENABLE, mEnableCamera);
        editor.putBoolean(CommonUtils.MIC_ENABLE, mEnableMic);
        editor.putBoolean(CommonUtils.SCREEN_ENABLE, mEnableScreen);
        editor.putInt(CommonUtils.PUBLISH_MODE, mPublishMode);
        editor.putInt(CommonUtils.SUBSCRIBE_MODE, mSubScribeMode);
        editor.putInt(CommonUtils.SDK_CLASS_TYPE, mRoomType.ordinal());

        editor.apply();
    }

}

