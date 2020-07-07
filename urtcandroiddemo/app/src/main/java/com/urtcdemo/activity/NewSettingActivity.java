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
import android.widget.ImageButton;
import android.widget.TextView;

import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;

import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.VideoProfilePopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class NewSettingActivity extends AppCompatActivity {
    private TextView mConfigTextView;
    private int mSelectPos = 0;
    private ArrayAdapter<String> mAdapter;
    private VideoProfilePopupWindow mSpinnerPopupWindow;

    private ImageButton mBackButton;

    @CommonUtils.PubScribeMode
    private int mPublishMode;
    @CommonUtils.PubScribeMode
    private int mScribeMode;

    private List<String> mDefaultConfiguration = new ArrayList<>();
    private String mAppid;

    private String mMixFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name),
                        Context.MODE_PRIVATE).edit();

                editor.putInt(CommonUtils.videoprofile, mSelectPos);
                editor.putString(CommonUtils.APPID_KEY, mAppid);
                editor.putInt(CommonUtils.PUBLISH_MODE, mPublishMode);
                editor.putInt(CommonUtils.SCRIBE_MODE, mScribeMode);

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
        mAppid = preferences.getString(CommonUtils.APPID_KEY, CommonUtils.APP_ID);
        mConfigTextView.setText(mDefaultConfiguration.get(mSelectPos));
        mAdapter = new ArrayAdapter<String>(this, R.layout.videoprofile_item, mDefaultConfiguration);

        mSpinnerPopupWindow = new VideoProfilePopupWindow(this);
        mSpinnerPopupWindow.setOnSpinnerItemClickListener(mOnSpinnerItemClickListener);
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
}

