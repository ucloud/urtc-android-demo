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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.RadioGroupFlow;
import com.urtcdemo.utils.VideoProfilePopupWindow;
import com.urtclib.sdkengine.UCloudRtcSdkEnv;
import com.urtclib.sdkengine.define.UCloudRtcSdkMode;

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

    private RadioGroupFlow mEnvGroup;
    private RadioButton mDevenv;
    private RadioButton mTestenv;

    private ImageButton mBackButton ;
    private Button saveButton ;
    private int mCaptureMode ;
    private boolean mTestMode ;
    private List<String> mDefaultConfiguration = new ArrayList<>();
    private String mAppid ;

    private EditText mAppidEditText;
    private EditText mGatewayEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mConfigTextView = findViewById(R.id.config_text_view) ;
        mCaptureModeRadioGroup = findViewById(R.id.capture_mode_button);
        mScreenCapture = findViewById(R.id.screen_capture_button);
        mCameraCapture = findViewById(R.id.camera_capture_button);
        mOnlyAudioCapture = findViewById(R.id.audio_capture_button);
        mScreenAudioCapture = findViewById(R.id.screen_audio_track_button);
        mMutiTrackCapture = findViewById(R.id.muti_track_button);

        mEnvGroup = findViewById(R.id.env_mode) ;
        mTestenv = findViewById(R.id.test_env) ;
        mDevenv = findViewById(R.id.dev_env) ;

        mAppidEditText = findViewById(R.id.appid_edittext) ;
       // mGatewayEditText = findViewById(R.id.gateway_edittext) ;

        String[] configurations = getResources().getStringArray(R.array.videoResolutions);
        mDefaultConfiguration.addAll(Arrays.asList(configurations));

        mBackButton = findViewById(R.id.back_btn) ;
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton = findViewById(R.id.save_button) ;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name),
                        Context.MODE_PRIVATE).edit();

                mAppid = mAppidEditText.getEditableText().toString() ;
                editor.putInt(CommonUtils.videoprofile, mSelectPos);
                editor.putInt(CommonUtils.capture_mode, mCaptureMode);
                editor.putString(CommonUtils.APPID_KEY, mAppid) ;

                editor.apply();
                finish();
            }
        });

        mConfigTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow() ;
            }
        });
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        mSelectPos = preferences.getInt(CommonUtils.videoprofile, CommonUtils.videoprofilesel);
        mAppid = preferences.getString(CommonUtils.APPID_KEY, CommonUtils.APP_ID) ;
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

        if (UCloudRtcSdkEnv.getSdkMode() == UCloudRtcSdkMode.RTC_SDK_MODE_TRIVAL) {
            mDevenv.setChecked(false);
            mTestenv.setChecked(true);
        }else {
            mDevenv.setChecked(true);
            mTestenv.setChecked(false);
        }

        mEnvGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.env_mode:
                        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.URTC_SDK_MODE_NORMAL);
                        break;
                    case R.id.test_env:
                        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.RTC_SDK_MODE_TRIVAL);
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
                    case R.id.screen_audio_track_button :
                        mCaptureMode = CommonUtils.screen_Audio_mode;
                        break;
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

