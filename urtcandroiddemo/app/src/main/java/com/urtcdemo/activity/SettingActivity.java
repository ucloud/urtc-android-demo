package com.urtcdemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRoomType;
import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.RadioGroupFlow;
import com.urtcdemo.utils.ToastUtils;
import com.urtcdemo.utils.VideoProfilePopupWindow;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private TextView mConfigTextView;
    private int mSelectPos = 0;
    private ArrayAdapter<String> mAdapter;
    private VideoProfilePopupWindow mSpinnerPopupWindow;
    private RadioGroupFlow mCaptureModeRadioGroup;
    private RadioButton mScreenCapture;
    private RadioButton mCameraCapture;
    private RadioButton mOnlyAudioCapture;
    private RadioButton mScreenAudioCapture;
    private RadioButton mMutiTrackCapture;
    private RadioGroupFlow mRGPublish;
    private RadioButton mRBPublishAuto;
    private RadioButton mRBPublishManul;
    private RadioGroupFlow mRGScribe;
    private RadioButton mRBScribeAuto;
    private RadioButton mRBScribeManual;
    private RadioGroupFlow mRGRole;
    private RadioButton mRBRoleTeacher;
    private RadioButton mRBRoleStudent;
    private RadioGroupFlow mRGClass;
    private RadioButton mRBRoomSmall;
    private RadioButton mRBRoomLarge;
    private RadioGroupFlow mEnvGroup;
    private RadioButton mDevenv;
    private RadioButton mTestenv;
    private AppCompatCheckBox mCheckBox;
    private ImageButton mBackButton;
    private Button saveButton;
    private int mCaptureMode;
    @CommonUtils.PubScribeMode
    private int mPublishMode;
    @CommonUtils.PubScribeMode
    private int mScribeMode;
    @CommonUtils.WhiteRoleType
    private int mWhiteRole;
    private UCloudRtcSdkRoomType mRoomType;
    private boolean mTestMode;
    private List<String> mDefaultConfiguration = new ArrayList<>();
    private String mAppid;
    private boolean mIsWhiteMode = false;
    private EditText mAppidEditText;
    private EditText mGatewayEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mConfigTextView = findViewById(R.id.config_text_view);
        mCaptureModeRadioGroup = findViewById(R.id.capture_mode_button);
        mScreenCapture = findViewById(R.id.screen_capture_button);
        mCameraCapture = findViewById(R.id.camera_capture_button);
        mOnlyAudioCapture = findViewById(R.id.audio_capture_button);
        mScreenAudioCapture = findViewById(R.id.screen_audio_track_button);
        mMutiTrackCapture = findViewById(R.id.muti_track_button);
        mRGPublish = findViewById(R.id.rg_publish);
        mRBPublishAuto = findViewById(R.id.rb_pub_auto);
        mRBPublishManul = findViewById(R.id.rb_pub_manual);
        mRGScribe = findViewById(R.id.rg_scribe);
        mRBScribeAuto = findViewById(R.id.rb_scribe_auto);
        mRBScribeManual = findViewById(R.id.rb_scribe_manual);
        mRGRole = findViewById(R.id.rg_white_role);
        mRBRoleTeacher = findViewById(R.id.rb_role_teacher);
        mRBRoleStudent = findViewById(R.id.rb_role_student);
        mRGClass = findViewById(R.id.rg_room_type);
        mRBRoomSmall = findViewById(R.id.rb_type_small_room);
        mRBRoomLarge = findViewById(R.id.rb_type_large_room);
        mEnvGroup = findViewById(R.id.env_mode);
        mTestenv = findViewById(R.id.test_env);
        mDevenv = findViewById(R.id.dev_env);
        mCheckBox = findViewById(R.id.checkbox_white);

        mAppidEditText = findViewById(R.id.appid_edittext);
        mGatewayEditText = findViewById(R.id.gateway_edittext);

        String[] configurations = getResources().getStringArray(R.array.videoResolutions);
        mDefaultConfiguration.addAll(Arrays.asList(configurations));

        mBackButton = findViewById(R.id.back_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name),
                        Context.MODE_PRIVATE).edit();
                mAppid = mAppidEditText.getEditableText().toString();
                editor.putInt(CommonUtils.videoprofile, mSelectPos);
                editor.putInt(CommonUtils.capture_mode, mCaptureMode);
                editor.putString(CommonUtils.APPID_KEY, mAppid);
                editor.putInt(CommonUtils.PUBLISH_MODE, mPublishMode);
                editor.putInt(CommonUtils.SCRIBE_MODE, mScribeMode);
                editor.putInt(CommonUtils.WHITE_ROLE, mWhiteRole);
                editor.putInt(CommonUtils.SDK_CLASS_TYPE, mRoomType.ordinal());
                editor.putBoolean(CommonUtils.BUS_WHITE_MODE , mIsWhiteMode);
                editor.apply();
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
        mAppidEditText.setText(mAppid);
        mConfigTextView.setText(mDefaultConfiguration.get(mSelectPos));
        mAdapter = new ArrayAdapter<String>(this, R.layout.videoprofile_item, mDefaultConfiguration);

        mSpinnerPopupWindow = new VideoProfilePopupWindow(this);
        mSpinnerPopupWindow.setOnSpinnerItemClickListener(mOnSpinnerItemClickListener);

        mCaptureMode = preferences.getInt(CommonUtils.capture_mode, CommonUtils.camera_capture_mode);
        switch (mCaptureMode) {
            case CommonUtils.audio_capture_mode:
                mOnlyAudioCapture.setChecked(true);
                break;
            case CommonUtils.camera_capture_mode:
                mCameraCapture.setChecked(true);
                break;
            case CommonUtils.screen_capture_mode:
                mScreenCapture.setChecked(true);
                break;
            case CommonUtils.multi_capture_mode:
                mMutiTrackCapture.setChecked(true);
                break;
            case CommonUtils.screen_Audio_mode:
                mScreenAudioCapture.setChecked(true);
                break;
        }

        mPublishMode = preferences.getInt(CommonUtils.PUBLISH_MODE, CommonUtils.AUTO_MODE);
        switch (mPublishMode) {
            case CommonUtils.AUTO_MODE:
                mRBPublishAuto.setChecked(true);
                break;
            case CommonUtils.MANUAL_MODE:
                mRBPublishManul.setChecked(true);
                break;
        }

        mScribeMode = preferences.getInt(CommonUtils.SCRIBE_MODE, CommonUtils.AUTO_MODE);
        switch (mScribeMode) {
            case CommonUtils.AUTO_MODE:
                mRBScribeAuto.setChecked(true);
                break;
            case CommonUtils.MANUAL_MODE:
                mRBScribeManual.setChecked(true);
                break;
        }

        mWhiteRole = preferences.getInt(CommonUtils.WHITE_ROLE, CommonUtils.TEACHER_ROLE);
        switch (mWhiteRole) {
            case CommonUtils.TEACHER_ROLE:
                mRBRoleTeacher.setChecked(true);
                break;
            case CommonUtils.STUDENT_ROLE:
                mRBRoleStudent.setChecked(true);
                break;
        }

        int roomInt = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL.ordinal());
        mRoomType = UCloudRtcSdkRoomType.valueOf(roomInt);
        switch (mRoomType) {
            case UCLOUD_RTC_SDK_ROOM_SMALL:
                mRBRoomSmall.setChecked(true);
                break;
            case UCLOUD_RTC_SDK_ROOM_LARGE:
                mRBRoomLarge.setChecked(true);
                break;
        }

        if (UCloudRtcSdkEnv.getSdkMode() == UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIVAL) {
            mDevenv.setChecked(false);
            mTestenv.setChecked(true);
        } else {
            mDevenv.setChecked(true);
            mTestenv.setChecked(false);
        }

        mEnvGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.dev_env:
                        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_NORMAL);
                        break;
                    case R.id.test_env:
                        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIVAL);
                        break;
                }
            }
        });

        mCaptureModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.camera_capture_button:
                        mCaptureMode = CommonUtils.camera_capture_mode;
                        break;
                    case R.id.screen_capture_button:
                        mCaptureMode = CommonUtils.screen_capture_mode;
                        break;
                    case R.id.audio_capture_button:
                        mCaptureMode = CommonUtils.audio_capture_mode;
                        break;
                    case R.id.muti_track_button:
                        mCaptureMode = CommonUtils.multi_capture_mode;
                        break;
                    case R.id.screen_audio_track_button:
                        mCaptureMode = CommonUtils.screen_Audio_mode;
                        break;
                }
            }
        });

        mRGClass.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.rb_type_small_room:
                    mRoomType = UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL;
                    break;
                case R.id.rb_type_large_room:
                    mRoomType = UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_LARGE;
                    break;
            }
        });

        mRGRole.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.rb_role_teacher:
                    mWhiteRole = CommonUtils.TEACHER_ROLE;
                    break;
                case R.id.rb_role_student:
                    mWhiteRole = CommonUtils.STUDENT_ROLE;
                    break;
            }
        });

        mRGPublish.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.rb_pub_auto:
                    mPublishMode = CommonUtils.AUTO_MODE;
                    break;
                case R.id.rb_pub_manual:
                    mPublishMode = CommonUtils.MANUAL_MODE;
                    break;
            }
        });

        mRGScribe.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.rb_scribe_auto:
                    mScribeMode = CommonUtils.AUTO_MODE;
                    break;
                case R.id.rb_scribe_manual:
                    mScribeMode = CommonUtils.MANUAL_MODE;
                    break;
            }
        });

        mIsWhiteMode = preferences.getBoolean(CommonUtils.BUS_WHITE_MODE,false);
        mCheckBox.setChecked(mIsWhiteMode);
        if(mIsWhiteMode){
            mRBPublishManul.setEnabled(false);
            mRBScribeManual.setEnabled(false);
            mRBRoomSmall.setEnabled(false);
        }
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               mIsWhiteMode = isChecked;
               if(isChecked){
                   mRBPublishAuto.setChecked(true);
                   mRBPublishManul.setEnabled(false);
                   mRBScribeAuto.setChecked(true);
                   mRBScribeManual.setEnabled(false);
                   mRBRoomSmall.setEnabled(false);
                   mRBRoomLarge.setChecked(true);
                   ToastUtils.shortShow(SettingActivity.this,"白板模式只支持自动发布和订阅，大班课模式");
               }else{
                   mRBPublishManul.setEnabled(true);
                   mRBScribeManual.setEnabled(true);
                   mRBRoomSmall.setEnabled(true);

               }
            }
        });
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
}

