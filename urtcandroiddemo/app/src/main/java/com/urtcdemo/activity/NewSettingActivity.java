package com.urtcdemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cmcc.sdkengine.define.CMCCChannelProfile;
import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.RadioGroupFlow;
import com.urtcdemo.utils.StatusBarUtils;
import com.urtcdemo.utils.ToastUtils;
import com.urtcdemo.utils.VideoProfilePopupWindow;
import com.urtcdemo.view.BaseSwitch;
import com.urtcdemo.view.LSwitch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewSettingActivity extends AppCompatActivity {
    private TextView mConfigTextView;
    private int mSelectPos = 1;
    private ArrayAdapter<String> mAdapter;
    private VideoProfilePopupWindow mSpinnerPopupWindow;
    private EditText mPriDeployEditText;
    private RadioGroupFlow mExtendVideoFormatRadioGroup;
    private RadioButton mNV21Format;
    private RadioButton mNV12Format;
    private RadioButton mI420Format;
    private RadioButton mRGBAFormat;
    private RadioButton mARGBFormat;
    private RadioButton mRGB24Format;
    private RadioButton mRGB565Format;

    private ImageButton mBackButton;

    @CommonUtils.PubScribeMode
    private int mPublishMode;
    @CommonUtils.PubScribeMode
    private int mSubScribeMode;
    private CMCCChannelProfile mRoomType;

    private List<String> mDefaultConfiguration = new ArrayList<>();
    private String mAppid;
    private String mPriAddr;

    private LSwitch mCameraSwitch;
    private LSwitch mMicSwitch;
    private LSwitch mScreenShareSwitch;
    private LSwitch mAutoPubSwitch;
    private LSwitch mAutoSubSwitch;
    private LSwitch mBroadcastSwitch;
    private LSwitch mPriDeploySwitch;
    private LSwitch mExtendCameraSwitch;

    private boolean mEnableCamera;
    private boolean mEnableMic;
    private boolean mEnableScreen;
    private boolean mExtendCamera;
    private boolean mPriDeploy;
    private int mExtendVideoFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEnableCamera = true;
        mEnableMic = true;
        mEnableScreen = false;
        mExtendCamera = false;
        mPriDeploy = false;

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
        mAppid = preferences.getString(CommonUtils.APP_ID_TAG, CommonUtils.APP_ID);
        mEnableCamera = preferences.getBoolean(CommonUtils.CAMERA_ENABLE, CommonUtils.CAMERA_ON);
        mEnableMic = preferences.getBoolean(CommonUtils.MIC_ENABLE, CommonUtils.MIC_ON);
        mEnableScreen = preferences.getBoolean(CommonUtils.SCREEN_ENABLE, CommonUtils.SCREEN_OFF);
        mPublishMode = preferences.getInt(CommonUtils.PUBLISH_MODE, CommonUtils.AUTO_MODE);
        mSubScribeMode = preferences.getInt(CommonUtils.SUBSCRIBE_MODE, CommonUtils.AUTO_MODE);
        mExtendCamera = preferences.getBoolean(CommonUtils.CAMERA_CAPTURE_MODE, false);
        mPriDeploy = preferences.getBoolean(CommonUtils.PRIVATISATION_MODE, false);
        mPriAddr = preferences.getString(CommonUtils.PRIVATISATION_ADDRESS, "");
        int roomInt = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, CMCCChannelProfile.CHANNEL_PROFILE_COMMUNICATION.ordinal());
        mRoomType = CMCCChannelProfile.valueOf(roomInt);
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
                mRoomType = isChecked ? CMCCChannelProfile.CHANNEL_PROFILE_LIVE_BROADCASTING : CMCCChannelProfile.CHANNEL_PROFILE_COMMUNICATION;

            }
        });
        mPriDeployEditText = findViewById(R.id.privatisation_edittext);
        mPriDeployEditText.setText(mPriAddr);
        mPriDeploySwitch = findViewById(R.id.privatisation_switch);
        mPriDeploySwitch.setOnCheckedListener(new BaseSwitch.OnCheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                mPriDeploy = isChecked;
                if (!isChecked) {
                    mPriDeployEditText.setVisibility(View.GONE);
                }
                else {
                    mPriDeployEditText.setVisibility(View.VISIBLE);
                }
            }
        });
        mExtendCameraSwitch = findViewById(R.id.extend_camera_switch);
        mExtendCameraSwitch.setOnCheckedListener(new BaseSwitch.OnCheckedListener() {
            @Override
            public void onChecked(boolean isChecked) {
                mExtendCamera = isChecked;
                if (!isChecked) {
                    mExtendVideoFormatRadioGroup.setVisibility(View.GONE);
                }
                else {
                    mExtendVideoFormatRadioGroup.setVisibility(View.VISIBLE);
                }
            }
        });

        mExtendVideoFormatRadioGroup = findViewById(R.id.extend_video_format_button);
        mNV21Format = findViewById(R.id.nv21_format);
        mNV12Format = findViewById(R.id.nv12_format);
        mI420Format = findViewById(R.id.i420_format);
        mRGBAFormat = findViewById(R.id.rgba_format);
        mARGBFormat = findViewById(R.id.argb_format);
        mRGB24Format = findViewById(R.id.rgb24_format);
        mRGB565Format = findViewById(R.id.rgb565_format);
        mExtendVideoFormatRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.nv21_format:
                        mExtendVideoFormat = CommonUtils.nv21_format;
                        break;
                    case R.id.nv12_format:
                        mExtendVideoFormat = CommonUtils.nv12_format;
                        break;
                    case R.id.i420_format:
                        mExtendVideoFormat = CommonUtils.i420_format;
                        break;
                    case R.id.rgba_format:
                        mExtendVideoFormat = CommonUtils.rgba_format;
                        break;
                    case R.id.argb_format:
                        mExtendVideoFormat = CommonUtils.argb_format;
                        break;
                    case R.id.rgb24_format:
                        mExtendVideoFormat = CommonUtils.rgb24_format;
                        break;
                    case R.id.rgb565_format:
                        mExtendVideoFormat = CommonUtils.rgb565_format;
                        break;
                }
            }
        });

        mCameraSwitch.setChecked(mEnableCamera);
        mMicSwitch.setChecked(mEnableMic);
        mScreenShareSwitch.setChecked(mEnableScreen);
        mAutoPubSwitch.setChecked(mPublishMode == CommonUtils.AUTO_MODE);
        mAutoSubSwitch.setChecked(mSubScribeMode == CommonUtils.AUTO_MODE);
        mBroadcastSwitch.setChecked(mRoomType == CMCCChannelProfile.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mPriDeploySwitch.setChecked(mPriDeploy);
        mExtendCameraSwitch.setChecked(mExtendCamera);

        mExtendVideoFormat = preferences.getInt(CommonUtils.EXTEND_CAMERA_VIDEO_FORMAT, CommonUtils.i420_format);
        switch (mExtendVideoFormat) {
            case CommonUtils.nv21_format:
                mNV21Format.setChecked(true);
                break;
            case CommonUtils.nv12_format:
                mNV12Format.setChecked(true);
                break;
            case CommonUtils.i420_format:
                mI420Format.setChecked(true);
                break;
            case CommonUtils.rgba_format:
                mRGBAFormat.setChecked(true);
                break;
            case CommonUtils.argb_format:
                mARGBFormat.setChecked(true);
                break;
            case CommonUtils.rgb24_format:
                mRGB24Format.setChecked(true);
                break;
            case CommonUtils.rgb565_format:
                mRGB565Format.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveSettings();
    }

    private void showPopupWindow() {
        mSpinnerPopupWindow.setAdapter(mAdapter);
        mSpinnerPopupWindow.setWidth(mConfigTextView.getWidth());
        mSpinnerPopupWindow.showAsDropDown(mConfigTextView);
    }

    private VideoProfilePopupWindow.OnSpinnerItemClickListener mOnSpinnerItemClickListener = new VideoProfilePopupWindow.OnSpinnerItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            if (mExtendCamera && pos < 4) {
                mSelectPos = 5;
                ToastUtils.shortShow(NewSettingActivity.this,"外接摄像头目前不支持640*480以下分辨率" );
            }
            else {
                mSelectPos = pos;
            }
            mConfigTextView.setText(mDefaultConfiguration.get(mSelectPos));
            mSpinnerPopupWindow.dismiss();
        }
    };

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE).edit();

        editor.putString(CommonUtils.APP_ID_TAG, mAppid);
        if (mExtendCamera && mSelectPos < 4) {
            editor.putInt(CommonUtils.videoprofile, 5);
        }
        else {
            editor.putInt(CommonUtils.videoprofile, mSelectPos);
        }
        editor.putBoolean(CommonUtils.CAMERA_ENABLE, mEnableCamera);
        editor.putBoolean(CommonUtils.MIC_ENABLE, mEnableMic);
        editor.putBoolean(CommonUtils.SCREEN_ENABLE, mEnableScreen);
        editor.putInt(CommonUtils.PUBLISH_MODE, mPublishMode);
        editor.putInt(CommonUtils.SUBSCRIBE_MODE, mSubScribeMode);
        editor.putInt(CommonUtils.SDK_CLASS_TYPE, mRoomType.ordinal());
        editor.putBoolean(CommonUtils.PRIVATISATION_MODE, mPriDeploy);
        mPriAddr = mPriDeployEditText.getText().toString();
        editor.putString(CommonUtils.PRIVATISATION_ADDRESS, mPriAddr);
        editor.putBoolean(CommonUtils.CAMERA_CAPTURE_MODE, mExtendCamera);
        editor.putInt(CommonUtils.EXTEND_CAMERA_VIDEO_FORMAT, mExtendVideoFormat);

        editor.apply();
    }

}

