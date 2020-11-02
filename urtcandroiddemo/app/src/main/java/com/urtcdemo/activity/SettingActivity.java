package com.urtcdemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRoomType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamRole;
import com.urtcdemo.Application.UCloudRtcApplication;
import com.urtcdemo.BuildConfig;
import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.RadioGroupFlow;
import com.urtcdemo.utils.VideoProfilePopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
    private RadioButton mRBRolePublish;
    private RadioButton mRBRoleScribe;
    private RadioButton mRBRoleBoth;

    private RadioGroupFlow mRGClass;
    private RadioButton mRBRoomSmall;
    private RadioButton mRBRoomLarge;


    private RadioGroupFlow mEnvGroup;
    private RadioButton mDevenv;
    private RadioButton mTestenv;

    private ImageButton mBackButton;
    private Button saveButton;
    private int mCaptureMode;
    @CommonUtils.PubScribeMode
    private int mPublishMode;
    @CommonUtils.PubScribeMode
    private int mScribeMode;
    private UCloudRtcSdkStreamRole mRole;
    private UCloudRtcSdkRoomType mRoomType;
    private boolean mTestMode;
    private List<String> mDefaultConfiguration = new ArrayList<>();
    private String mAppid;
    private EditText mAppidEditText;
    private EditText mUserIdEditText;
    private EditText mGatewayEditText;
    private EditText mEditTextMixFilePath;
    private CheckBox mCheckBoxMixSupport;
    private CheckBox mCheckBoxLoop;
    private boolean mSupportMix;
    private boolean mIsLoop;
    private String mMixFilePath;

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
        mRGRole = findViewById(R.id.rg_role);
        mRBRolePublish = findViewById(R.id.rb_role_pub);
        mRBRoleScribe = findViewById(R.id.rb_role_scribe);
        mRBRoleBoth = findViewById(R.id.rb_role_both);
        mRGClass = findViewById(R.id.rg_room_type);
        mRBRoomSmall = findViewById(R.id.rb_type_small_room);
        mRBRoomLarge = findViewById(R.id.rb_type_large_room);
        mEnvGroup = findViewById(R.id.env_mode);
        mTestenv = findViewById(R.id.test_env);
        mDevenv = findViewById(R.id.dev_env);

        mAppidEditText = findViewById(R.id.appid_edittext);
        mUserIdEditText = findViewById(R.id.userid_edittext);
        mGatewayEditText = findViewById(R.id.gateway_edittext);
        mCheckBoxLoop = findViewById(R.id.cb_loop);
        mCheckBoxMixSupport = findViewById(R.id.cb_mix_support);
        mEditTextMixFilePath = findViewById(R.id.edit_mix_file_path);

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
                String userId = stringFilter(mUserIdEditText.getEditableText().toString());
                if(!TextUtils.isEmpty(userId)){
                    if(!userId.startsWith("android_")){
                        userId = "android_" + userId;
                    }
                    UCloudRtcApplication.setUserId(userId);
                }
                editor.putInt(CommonUtils.videoprofile, mSelectPos);
                editor.putInt(CommonUtils.capture_mode, mCaptureMode);
                editor.putString(CommonUtils.APP_ID_TAG, mAppid);
                editor.putInt(CommonUtils.PUBLISH_MODE, mPublishMode);
                editor.putInt(CommonUtils.SUBSCRIBE_MODE, mScribeMode);
                editor.putInt(CommonUtils.SDK_STREAM_ROLE, mRole.ordinal());
                editor.putInt(CommonUtils.SDK_CLASS_TYPE, mRoomType.ordinal());
                editor.putBoolean(CommonUtils.SDK_SUPPORT_MIX, mSupportMix);
                UCloudRtcSdkEnv.setMixSupport(mSupportMix);
                editor.putBoolean(CommonUtils.SDK_IS_LOOP, mIsLoop);
                UCloudRtcSdkEnv.setLoop(mIsLoop);
                mMixFilePath = mEditTextMixFilePath.getText().toString();
                UCloudRtcSdkEnv.setMixFilePath(mMixFilePath);
                editor.putString(CommonUtils.SDK_MIX_FILE_PATH, mMixFilePath);
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
        mAppid = preferences.getString(CommonUtils.APP_ID_TAG, CommonUtils.APP_ID);
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

        mScribeMode = preferences.getInt(CommonUtils.SUBSCRIBE_MODE, CommonUtils.AUTO_MODE);
        switch (mScribeMode) {
            case CommonUtils.AUTO_MODE:
                mRBScribeAuto.setChecked(true);
                break;
            case CommonUtils.MANUAL_MODE:
                mRBScribeManual.setChecked(true);
                break;
        }

        int roleInt = preferences.getInt(CommonUtils.SDK_STREAM_ROLE, UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH.ordinal());
        mRole = UCloudRtcSdkStreamRole.valueOf(roleInt);
        switch (mRole) {
            case UCLOUD_RTC_SDK_STREAM_ROLE_BOTH:
                mRBRoleBoth.setChecked(true);
                break;
            case UCLOUD_RTC_SDK_STREAM_ROLE_PUB:
                mRBRolePublish.setChecked(true);
                break;
            case UCLOUD_RTC_SDK_STREAM_ROLE_SUB:
                mRBRoleScribe.setChecked(true);
                break;
        }


        int roomInt = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL.ordinal());
        mRoomType = UCloudRtcSdkRoomType.valueOf(roomInt);
        switch (mRoomType) {
            case UCLOUD_RTC_SDK_ROOM_SMALL:
                mRBRoomSmall.setChecked(true);
                mRBRoleBoth.setEnabled(true);
                break;
            case UCLOUD_RTC_SDK_ROOM_LARGE:
                mRBRoomLarge.setChecked(true);
                mRBRoleBoth.setEnabled(true);
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

        mRGRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rb_role_pub:
                        mRole = UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_PUB;
                        break;
                    case R.id.rb_role_scribe:
                        mRole = UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_SUB;
                        break;
                    case R.id.rb_role_both:
                        mRole = UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH;
//                    if (mRoomType == UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_LARGE) {
//                        ToastUtils.shortShow(this, "大班课模式不能选择全部");
//                    } else {
//                        mRole = UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH;
//                    }
                        break;
                }
            }
        });

        mRGClass.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rb_type_small_room:
                        mRoomType = UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL;
                        mRBRoleBoth.setEnabled(true);
                        break;
                    case R.id.rb_type_large_room:
                        mRoomType = UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_LARGE;
                        mRBRoleBoth.setEnabled(true);
                        checkRole();
                        break;
                }
            }
        });

        mRGPublish.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rb_pub_auto:
                        mPublishMode = CommonUtils.AUTO_MODE;
                        break;
                    case R.id.rb_pub_manual:
                        mPublishMode = CommonUtils.MANUAL_MODE;
                        break;
                }
            }
        });

        mRGScribe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rb_scribe_auto:
                        mScribeMode = CommonUtils.AUTO_MODE;
                        break;
                    case R.id.rb_scribe_manual:
                        mScribeMode = CommonUtils.MANUAL_MODE;
                        break;
                }
            }
        });

        boolean supportMix = preferences.getBoolean(CommonUtils.SDK_SUPPORT_MIX, false);
        mSupportMix = supportMix;
        mCheckBoxMixSupport.setChecked(supportMix);
        mCheckBoxMixSupport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSupportMix = isChecked;
            }
        });

        boolean isLoop = preferences.getBoolean(CommonUtils.SDK_IS_LOOP, true);
        mIsLoop = isLoop;
        mCheckBoxLoop.setChecked(isLoop);
        mCheckBoxLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsLoop = isChecked;
            }
        });
        String mixFilePath = preferences.getString(CommonUtils.SDK_MIX_FILE_PATH, getResources().getString(R.string.mix_file_path));
        mEditTextMixFilePath.setText(mixFilePath);

        //test log pre
        TextView textView = findViewById(R.id.btn_log_pre);
        textView.setVisibility(BuildConfig.DEBUG? View.VISIBLE:View.GONE);
//        textView.setText(URTCLogReportManager.logPre ? "pre" : "online");
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (URTCLogReportManager.logPre) {
//                    URTCLogReportManager.logPre = false;
//                } else {
//                    URTCLogReportManager.logPre = true;
//                }
//                URTCLogReportManager.refreshUrl();
//                textView.setText(URTCLogReportManager.logPre ? "pre" : "online");
//            }
//        });
        if(!TextUtils.isEmpty(UCloudRtcApplication.getUserId())){
            mUserIdEditText.setText(UCloudRtcApplication.getUserId());
        }
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

    private void checkRole() {
        if (mRole == UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH) {
//            ToastUtils.shortShow(this, "大班课模式不能选择全部权限,默认重新选择下行权限");
        }
//        SettingActivity.this.mRBRolePublish.setChecked(false);
//        SettingActivity.this.mRBRoleScribe.setChecked(true);
//        SettingActivity.this.mRBRoleBoth.setChecked(false);
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

