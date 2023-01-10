package com.urtcdemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.PermissionUtils;
import com.urtcdemo.utils.StatusBarUtils;
import com.urtcdemo.utils.ToastUtils;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMode;
import com.urtcdemo.utils.URTCFileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ConnectActivity extends AppCompatActivity {
    private static final String TAG = "ConnectActivity";

    private EditText userEditText,roomEditText;
    private String mUserId = "";
    private String mRoomid = "";
    private String mAppid = "";
    private String mRoomToken = "";
    private View connectButton;
    private View previewButton;
    private View exportButton;
    private ImageButton setButton;
    private TextView mTextSDKVersion;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private boolean mStartSuccess = false;
    private boolean mJoinChannel = true;

    @Override
    @TargetApi(21)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "获取桌面采集权限失败",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (requestCode != UCloudRtcSdkEngine.SCREEN_CAPTURE_REQUEST_CODE) {
            Toast.makeText(this, "获取桌面采集权限失败",
                    Toast.LENGTH_LONG).show();
            return;
        }
        UCloudRtcSdkEngine.onScreenCaptureResult(data);
        startLivingActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_connect);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        mAppid = preferences.getString(CommonUtils.APP_ID_TAG, CommonUtils.APP_ID);
        UCloudRtcSdkEnv.setLogReport(true);

        setButton = findViewById(R.id.setting_btn);
        userEditText = findViewById(R.id.user_edittext);
        roomEditText = findViewById(R.id.room_edittext);
        roomEditText.requestFocus();
        mTextSDKVersion = findViewById(R.id.tv_sdk_version);
        mTextSDKVersion.setText(getString(R.string.app_name) + "\n" + UCloudRtcSdkEngine.getSdkVersion());
        connectButton = findViewById(R.id.connect_button);
        previewButton = findViewById(R.id.preview_button);
        exportButton = findViewById(R.id.log_output_button);
        StatusBarUtils.setAndroidNativeLightStatusBar(this,true);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoomid = roomEditText.getText().toString();
                if (mRoomid.isEmpty()) {
                    ToastUtils.shortShow(getApplicationContext(), "房间id 不能为空");
                } else {
                    //测试环境下SDK自动生成token
                    if (UCloudRtcSdkEnv.getSdkMode() == UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIAL) {
                        mRoomToken = "testoken";
                        mJoinChannel = true;
                        Log.d(TAG, " appid " + mAppid);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            boolean mVideoHwAcc = preferences.getBoolean(CommonUtils.VIDEO_HW_ACC, CommonUtils.HARDWARE_ACC);
                            UCloudRtcSdkEnv.setVideoHardWareAcceleration(mVideoHwAcc);
                            UCloudRtcSdkEngine.requestScreenCapture(ConnectActivity.this);
                        } else {
                            startLivingActivity();
                        }
                    } else {
                        ToastUtils.shortShow(ConnectActivity.this, "正式环境下请获取自己服务器的token");
                    }
                }
            }
        });

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoomid = roomEditText.getText().toString();
                if (mRoomid.isEmpty()) {
                    ToastUtils.shortShow(getApplicationContext(), "房间id 不能为空");
                } else {
                    //测试环境下SDK自动生成token
                    if (UCloudRtcSdkEnv.getSdkMode() == UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIAL) {
                        mRoomToken = "testoken";
                        mJoinChannel = false;
                        Log.d(TAG, " appid " + mAppid);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            boolean mVideoHwAcc = preferences.getBoolean(CommonUtils.VIDEO_HW_ACC, CommonUtils.HARDWARE_ACC);
                            UCloudRtcSdkEnv.setVideoHardWareAcceleration(mVideoHwAcc);
                            UCloudRtcSdkEngine.requestScreenCapture(ConnectActivity.this);
                        } else {
                            startLivingActivity();
                        }
                    } else {
                        ToastUtils.shortShow(ConnectActivity.this, "正式环境下请获取自己服务器的token");
                    }
                }
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String basePath = getApplicationContext().getExternalFilesDir("").getPath();
                        String newPath = basePath + "/urtc/app_bugly";
                        Log.d(TAG, "copy new path : " + newPath);
                        URTCFileUtil.getInstance().copyFolder("/data/user/0/com.urtcdemo/app_bugly", newPath);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.shortShow(ConnectActivity.this, "拷贝完成,日志路径： " + newPath);
                            }
                        });

                    }
                });
                thread.start();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectActivity.this, NewSettingActivity.class);
                startActivity(intent);
            }
        });

        final RelativeLayout root = findViewById(R.id.id_rl_root);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
                        } else {
                            Log.e(TAG, "InputMethodManager is null !");
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        List<String> needPermissions = new ArrayList<>();
        needPermissions.add(Manifest.permission.CAMERA);
        needPermissions.add(Manifest.permission.RECORD_AUDIO);
        needPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        needPermissions.add(Manifest.permission.READ_PHONE_STATE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            needPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
            needPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        String[] requestPermissions = new String[needPermissions.size()];
        needPermissions.toArray(requestPermissions);
        PermissionUtils.needsPermissions(this, requestPermissions);
        Thread thread = new Thread(new CopyMixFileTask(this));
        thread.start();
    }

    private void startLivingActivity() {
        if (!mStartSuccess) {
            mStartSuccess = true;
            final Intent intent = new Intent(ConnectActivity.this, UCloudRTCLiveActivity.class);
            intent.putExtra("room_id", mRoomid);
            String autoGenUserId = "android_" + UUID.randomUUID().toString().replace("-", "");
            mUserId = !TextUtils.isEmpty(userEditText.getText()) ? userEditText.getText().toString() : autoGenUserId;
            intent.putExtra("user_id", mUserId);
            intent.putExtra("app_id", mAppid);
            intent.putExtra("token", mRoomToken);
            intent.putExtra("join_channel", mJoinChannel);
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    //finish();
                    mStartSuccess = false;
                }
            }, 500);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "activity onStop");
        super.onStop();
    }

    static class CopyMixFileTask implements Runnable {

        WeakReference<AppCompatActivity> context;

        public CopyMixFileTask(AppCompatActivity context) {
            this.context = new WeakReference<AppCompatActivity>(context);
        }

        @Override
        public void run() {
            if (context != null && context.get() != null) {
                String storageFileDir = context.get().getResources().getString(R.string.mix_file_dir);
                String storageFilePath = storageFileDir + File.separator + "dy.mp3";
                File fileStorage = new File(storageFilePath);
                boolean needCopy = false;
                if (!fileStorage.exists()) {
                    needCopy = true;
                }
                Handler handler = new Handler(Looper.getMainLooper());
                if (needCopy) {
                    File file = new File(storageFileDir);
                    if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
                        file.mkdirs();
                    }
                    readInputStream(storageFilePath, context.get().getResources().openRawResource(R.raw.dy));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (UCloudRtcSdkEnv.getApplication() != null) {
                                ToastUtils.shortShow(UCloudRtcSdkEnv.getApplication(), "default mix file copy success");
                            }
                        }
                    });
                } else {
                }
                context.clear();
                context = null;
            }
        }

        /**
         * 读取输入流中的数据写入输出流
         *
         * @param storagePath 目标文件路径
         * @param inputStream 输入流
         */
        public void readInputStream(String storagePath, InputStream inputStream) {
            File file = new File(storagePath);
            try {
                if (!file.exists()) {
                    // 1.建立通道对象
                    FileOutputStream fos = new FileOutputStream(file);
                    // 2.定义存储空间
                    byte[] buffer = new byte[1024];
                    // 3.开始读文件
                    int length = 0;
                    while ((length = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                        // 将Buffer中的数据写到outputStream对象中
                        fos.write(buffer, 0, length);
                    }
                    fos.flush();// 刷新缓冲区
                    // 4.关闭流
                    fos.close();
                    inputStream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
