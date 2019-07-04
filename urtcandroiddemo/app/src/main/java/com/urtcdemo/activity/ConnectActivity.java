package com.urtcdemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
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
import com.urtcdemo.utils.ToastUtils;
import com.urtclib.sdkengine.UCloudRtcSdkEngine;
import com.urtclib.sdkengine.UCloudRtcSdkEnv;
import com.urtclib.sdkengine.define.UCloudRtcSdkMode;

import java.util.UUID;

public class ConnectActivity extends AppCompatActivity {
    private static final String TAG = "ConnectActivity";

    private EditText roomEditText;
    private String mUserId = "";
    private String mRoomid = "" ;
    private String mAppid = "" ;
    private String mRoomToken = "" ;
    private View connectButton;
    private ImageButton setButton ;
    private TextView mTextSDKVersion;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private boolean mStartSuccess=false;
    private ImageView mAnimal ;

    @Override
    @TargetApi(21)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "获取桌面采集权限失败",
                    Toast.LENGTH_LONG).show() ;
            return;
        }
        if (requestCode != UCloudRtcSdkEngine.SCREEN_CAPTURE_REQUEST_CODE) {
            Toast.makeText(this, "获取桌面采集权限失败",
                    Toast.LENGTH_LONG).show() ;
            return;
        }
        UCloudRtcSdkEngine.onScreenCaptureResult(data);
        if (!mStartSuccess) {
            mStartSuccess=true;
            Intent intent = new Intent(ConnectActivity.this, RoomActivity.class);
            intent.putExtra("room_id", mRoomid);
            intent.putExtra("user_id", mUserId);
            intent.putExtra("app_id", mAppid);
            intent.putExtra("token", mRoomToken);
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                    mStartSuccess=false;
                }
            },500);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = "android_"+ UUID.randomUUID().toString().replace("-", "");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_connect);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        mAppid = preferences.getString(CommonUtils.APPID_KEY, CommonUtils.APP_ID) ;
        mAnimal = findViewById(R.id.userporta);
        ((AnimationDrawable) mAnimal.getBackground()).start();
        setButton = findViewById(R.id.setting_btn);
        roomEditText = findViewById(R.id.room_edittext);
        roomEditText.requestFocus();
        mTextSDKVersion = findViewById(R.id.tv_sdk_version);
        mTextSDKVersion.setText(getString(R.string.app_name)+"\n" + UCloudRtcSdkEngine.getSdkVersion());
        connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(view -> {
            mRoomid = roomEditText.getText().toString() ;
            if (mRoomid.isEmpty()) {
                ToastUtils.shortShow(getApplicationContext(),"房间id 不能为空");
            }
            else
            {
                //测试环境下SDK自动生成token
                if (UCloudRtcSdkEnv.getSdkMode() == UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIVAL) {
                    mRoomToken = "testoken" ;
                    Log.d(TAG, " appid "+ mAppid) ;
                    UCloudRtcSdkEngine.requestScreenCapture(ConnectActivity.this);
                }else {
                    //正式环境请参考下述代码传入用户自己的userId,roomId,appId来获取自己服务器上的返回token
                    ToastUtils.shortShow(this,"正式环境下请获取自己服务器的token");
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//
//                                String result = AppHttpUtil.getInstance().getTestRoomToken(mUserId, mRoomid, mAppid) ;
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.d(TAG, " gettokenresult "+ result) ;
//                                        if (result != null && result.length()>0) {
//                                            try {
//                                                JSONObject jsonObject = new JSONObject(result);
//                                                if (jsonObject != null) {
//                                                    JSONObject data = jsonObject.getJSONObject("data") ;
//                                                    if (data != null) {
//                                                        mRoomToken = data.getString("access_token" );
//                                                        Log.d(TAG, " token "+ mRoomToken) ;
//                                                        if (mRoomToken.length()>0) {
//                                                            UCloudRtcSdkEngine.requestScreenCapture(ConnectActivity.this);
//                                                        }
//                                                    }
//                                                }
//                                            }catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                        }else {
//                                            ToastUtils.shortShow(getApplicationContext(),"解析token 失败");
//                                        }
//                                    }
//                                });
//                            }catch (Exception e) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ToastUtils.shortShow(getApplicationContext(),"请求失败 "+ e.getMessage());
//                                    }
//                                });
//
//                            }
//                        }
//                    }).start() ;
                }
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        
        RelativeLayout root = findViewById(R.id.id_rl_root);
        root.setOnTouchListener((v, event) -> {
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
        });

        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionUtils.needsPermissions(this, permissions);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AnimationDrawable) mAnimal.getBackground()).start();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "activity onStop") ;
        super.onStop();
        ((AnimationDrawable) mAnimal.getBackground()).stop();
    }

}
