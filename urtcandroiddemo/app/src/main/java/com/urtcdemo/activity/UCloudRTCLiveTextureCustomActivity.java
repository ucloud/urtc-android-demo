package com.urtcdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faceunity.nama.FURenderer;
import com.faceunity.nama.ui.FaceUnityView;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderTextureView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkAudioDevice;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkAuthInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkCaptureMode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkErrorCode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaServiceStatus;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMixProfile;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkNetWorkQuality;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRoomType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkScaleType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStats;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamRole;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkTrackType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkVideoProfile;
import com.ucloudrtclib.sdkengine.listener.UCloudRtcRecordListener;
import com.ucloudrtclib.sdkengine.listener.UCloudRtcSdkEventListener;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCDataProvider;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCDataReceiver;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCScreenShot;
import com.urtcdemo.R;
import com.urtcdemo.adpter.RemoteHasViewVideoAdapter;
import com.urtcdemo.adpter.RemoteVideoAdapter;
import com.urtcdemo.custom.CameraRenderer;
import com.urtcdemo.custom.LifeCycleSensorManager;
import com.urtcdemo.service.UCloudRtcForeGroundService;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.StatusBarUtils;
import com.urtcdemo.utils.ToastUtils;
import com.urtcdemo.utils.VideoProfilePopupWindow;
import com.urtcdemo.view.URTCVideoViewInfo;

import org.wrtca.record.RtcRecordManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkErrorCode.NET_ERR_CODE_OK;
import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN;
import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO;

/**
 * @author ciel
 * @create 2020/7/2
 * @Describe
 */
public class UCloudRTCLiveTextureCustomActivity extends AppCompatActivity
        implements TextureView.SurfaceTextureListener, RemoteHasViewVideoAdapter.SwapInterface {
    private static final String TAG = "URTCLiveTextureActivity";
    private final String mBucket = "urtc-test";
    private final String mRegion = "cn-bj";
    private final int COL_SIZE_P = 3;
    private final int COL_SIZE_L = 6;

    private String mUserid = "test001";
    private String mRoomid = "urtc1";
    private String mRoomToken = "test token";
    private String mAppid = "";

    private boolean mSwitchCamera = false;
    private boolean mMuteMic = false;
    private boolean mMuteVideo = false;
    private boolean mSpeakerOn = true;
    private boolean mMirror = false;
    private boolean isScreenCaptureSupport = true;
    private boolean mCameraEnable = true;
    private boolean mMicEnable = true;
    private boolean mScreenEnable = false;
    private boolean mVideoIsPublished = false;
    private boolean mScreenIsPublished = false;
    private boolean mIsRemoteRecording = false;
    private boolean mIsLocalRecording = false;
    private boolean mAtomOpStart = false;
    private boolean mIsMixing = false;
    private boolean mLocalViewFullScreen = false;
    private boolean mRemoteVideoMute;
    private boolean mRemoteAudioMute;
    private boolean mIsPreview = false;
    @CommonUtils.PubScribeMode
    private int mPublishMode;
    @CommonUtils.PubScribeMode
    private int mScribeMode;
    private int mVideoProfileSelect;
    private int localViewWidth;
    private int localViewHeight;
    private int screenWidth;
    private int screenHeight;
    private boolean mExtendCameraCapture;
    private int mExtendVideoFormat;
    private int mUVCCameraFormat;
    private int mURTCVideoFormat;

    UCloudRtcSdkEngine sdkEngine = null;
    private UCloudRtcSdkRoomType mClass;
    private UCloudRtcSdkStreamInfo mLocalStreamInfo;
    private UCloudRtcSdkStreamInfo mSwapStreamInfo;
    private UCloudRtcSdkAudioDevice defaultAudioDevice;
    private List<UCloudRtcSdkStreamInfo> mSteamList;
    private List<String> mResolutionOption = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private TextureView mLocalVideoView = null; //TextureView
    private UCloudRtcRenderTextureView mLocalRender;

    private UCloudRtcSdkMediaType mPublishMediaType;

    private GridLayoutManager gridLayoutManager;
    private RemoteHasViewVideoAdapter mVideoAdapter;
    private RecyclerView mRemoteGridView = null;
    private DrawerLayout mDrawer;
    private ViewGroup mDrawerContent;
    private FrameLayout mDrawerMenu;
    private FrameLayout mTitleBar;
    private LinearLayout mToolBar;
    private ImageButton mImgBtnMore;
    private ImageButton mImgBtnSwitchCam;
    private ImageButton mImgBtnMuteMic;
    private ImageButton mImgBtnMuteVideo;
    private ImageButton mImgBtnEndCall;
    private ImageButton mImgBtnMuteSpeaker;
    private ImageButton mImgBtnMirror;
    private Chronometer timeShow;
    private ImageView mImgMix;
    private TextView mTextMix;
    private ImageView mImgLocalRecord;
    private TextView mTextLocalRecord;
    private ImageView mImgScreenshot;
    private TextView mTextScreenshot;
    private ImageView mImgRemoteRecord;
    private TextView mTextRemoteRecord;
    private ImageView mImgManualPubVideo;
    private TextView mTextManualPubVideo;
    private ImageView mImgManualPubScreen;
    private TextView mTextManualPubScreen;
    private TextView mTextRoomId;
    private ImageView mImgPreview;
    private TextView mTextPreview;
    private TextView mTextResolution;
    private VideoProfilePopupWindow mResolutionPopupWindow;
    //音量图片
    private ImageView mImgSoundVolume = null;
    private ImageView mImgMicSts = null;
    private final Object mSync = new Object();
    private boolean isActive, isPreview;
    private boolean mLeaveRoomFlag;
    private CameraRenderer mCameraRenderer;

    private final Object extendByteBufferSync = new Object();
    private ByteBuffer videoSourceData = null;
    //外部摄像数据读取
    private ArrayBlockingQueue<ByteBuffer> mQueueByteBuffer = new ArrayBlockingQueue(8);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        sdkEngine = UCloudRtcSdkEngine.createEngine(eventListener);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;
        setContentView(R.layout.activity_living_texture_custom);
        mVideoProfileSelect = preferences.getInt(CommonUtils.videoprofile, CommonUtils.videoprofilesel);
        mRemoteGridView = findViewById(R.id.remoteGridView);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, COL_SIZE_L);
        } else {
            gridLayoutManager = new GridLayoutManager(this, COL_SIZE_P);
        }
        mRemoteGridView.setLayoutManager(gridLayoutManager);
        //点击remoteView交换
        mVideoAdapter = new RemoteHasViewVideoAdapter(this,sdkEngine,this);
        //点击remoteView截图
        //mVideoAdapter = new RemoteHasViewVideoAdapter(this,sdkEngine,null);
//        mVideoAdapter.setRemoveRemoteStreamReceiver(mRemoveRemoteStreamReceiver);
        mRemoteGridView.setAdapter(mVideoAdapter);

        mLocalVideoView = findViewById(R.id.localvideoview);
        mLocalRender = new UCloudRtcRenderTextureView(mLocalVideoView);
        mDrawer = findViewById(R.id.drawer_layout);
        mDrawer.setScrimColor(0x00ffffff);
        mDrawerContent = findViewById(R.id.drawer_content);
        mDrawerMenu = findViewById(R.id.menu_drawer);
        timeShow = findViewById(R.id.timer);
        mDrawerContent.setPadding(0,StatusBarUtils.getStatusBarHeight(this),0,0);
        mTitleBar= findViewById(R.id.title_bar);
        mToolBar = findViewById(R.id.tool_bar);
        mImgBtnMore = findViewById(R.id.img_btn_more);
        mImgBtnSwitchCam = findViewById(R.id.img_btn_switch_camera);
        mImgBtnMuteMic = findViewById(R.id.img_btn_toggle_mic);
        mImgBtnMuteVideo = findViewById(R.id.img_btn_toggle_video);
        mImgBtnMuteSpeaker = findViewById(R.id.img_btn_speaker);
        mImgBtnMirror = findViewById(R.id.img_btn_mirror);
        mImgBtnEndCall = findViewById(R.id.img_btn_endcall);
        StatusBarUtils.setColor(this,getResources().getColor(R.color.color_7F04A5EB));
        mImgMix = findViewById(R.id.mix_pic);
        mTextMix = findViewById(R.id.mix_text);
        mImgLocalRecord = findViewById(R.id.local_record_pic);
        mTextLocalRecord = findViewById(R.id.local_record_text);
        mImgScreenshot = findViewById(R.id.screenshot_pic);
        mTextScreenshot = findViewById(R.id.screenshot_text);
        mImgRemoteRecord = findViewById(R.id.remote_record_pic);
        mTextRemoteRecord = findViewById(R.id.remote_record_text);
        mImgManualPubVideo = findViewById(R.id.manual_publish_pic);
        mTextManualPubVideo = findViewById(R.id.manual_publish_text);
        mImgManualPubScreen = findViewById(R.id.manual_publish_screen_pic);
        mTextManualPubScreen = findViewById(R.id.manual_publish_screen_text);
        mImgSoundVolume = findViewById(R.id.sound_volume_img);
        mImgMicSts = findViewById(R.id.mic_status_img);
        mTextResolution = findViewById(R.id.resolution_text);
        mImgPreview = findViewById(R.id.preview_pic);
        mTextPreview = findViewById(R.id.preview_text);

        mUserid = getIntent().getStringExtra("user_id");
        mRoomid = getIntent().getStringExtra("room_id");
        mRoomToken = getIntent().getStringExtra("token");
        mAppid = getIntent().getStringExtra("app_id");
        isScreenCaptureSupport = UCloudRtcSdkEnv.isSupportScreenCapture();
        mCameraEnable = preferences.getBoolean(CommonUtils.CAMERA_ENABLE, CommonUtils.CAMERA_ON);
        mMicEnable = preferences.getBoolean(CommonUtils.MIC_ENABLE, CommonUtils.MIC_ON);
        mScreenEnable = preferences.getBoolean(CommonUtils.SCREEN_ENABLE, CommonUtils.SCREEN_OFF);
        int classType = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL.ordinal());
        mClass = UCloudRtcSdkRoomType.valueOf(classType);
        mPublishMode = preferences.getInt(CommonUtils.PUBLISH_MODE, CommonUtils.AUTO_MODE);
        mScribeMode = preferences.getInt(CommonUtils.SUBSCRIBE_MODE, CommonUtils.AUTO_MODE);
        mExtendCameraCapture = preferences.getBoolean(CommonUtils.CAMERA_CAPTURE_MODE, false);
        mExtendVideoFormat = preferences.getInt(CommonUtils.EXTEND_CAMERA_VIDEO_FORMAT, CommonUtils.i420_format);
        updateVideoFormat(mExtendVideoFormat);
        mSteamList = new ArrayList<>();

        //房间号
        mTextRoomId = findViewById(R.id.roomid_text);
        mTextRoomId.setText("RoomID:" + mRoomid);
        mMirror = UCloudRtcSdkEnv.isFrontCameraMirror();
        mImgBtnMirror.setImageResource(mMirror ? R.mipmap.mirror_on :
                R.mipmap.mirror);
        //分辨率选择菜单
        String[] resolutions = getResources().getStringArray(R.array.videoResolutions);
        mResolutionOption.addAll(Arrays.asList(resolutions));

        Log.d(TAG, " Camera enable is: " + mCameraEnable + " Mic enable is: " + mMicEnable + " ScreenShare enable is: " + mScreenEnable);
        if (!mScreenEnable && !mCameraEnable && mMicEnable) {
            sdkEngine.setAudioOnlyMode(true);
        }
        else {
            sdkEngine.setAudioOnlyMode(false);
        }
        sdkEngine.configLocalCameraPublish(mCameraEnable);
        sdkEngine.configLocalAudioPublish(mMicEnable);
        if (isScreenCaptureSupport) {
            sdkEngine.configLocalScreenPublish(mScreenEnable);
        }
        else {
            sdkEngine.configLocalScreenPublish(false);
            mImgManualPubScreen.setVisibility(View.GONE);
            mTextManualPubScreen.setVisibility(View.GONE);
        }
        defaultAudioDevice = sdkEngine.getDefaultAudioDevice();
        if (defaultAudioDevice == UCloudRtcSdkAudioDevice.UCLOUD_RTC_SDK_AUDIODEVICE_SPEAKER) {
            mImgBtnMuteSpeaker.setImageResource(R.mipmap.speaker);
            mSpeakerOn = true;
        } else {
            mSpeakerOn = false;
            mImgBtnMuteSpeaker.setImageResource(R.mipmap.speaker_off);
        }
        sdkEngine.setStreamRole(UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH);
        sdkEngine.setClassType(mClass);
        sdkEngine.setAutoPublish(mPublishMode == CommonUtils.AUTO_MODE ? true : false);
        sdkEngine.setAutoSubscribe(mScribeMode == CommonUtils.AUTO_MODE ? true : false);
        sdkEngine.setScreenProfile(UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_1280_720);

        //分辨率菜单显示
        mTextResolution.setText(mResolutionOption.get(mVideoProfileSelect));
        mAdapter = new ArrayAdapter<String>(this, R.layout.videoprofile_item, mResolutionOption);

        mResolutionPopupWindow = new VideoProfilePopupWindow(this);
        mResolutionPopupWindow.setOnSpinnerItemClickListener(mOnResulutionOptionClickListener);
        Log.e(TAG, "onCreate: mExtendCameraCapture " + mExtendCameraCapture);
        if (mExtendCameraCapture) {
            //扩展摄像头方式
            UCloudRtcSdkEnv.setCaptureMode(
                    UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_EXTEND);
            FURenderer.setup(this);
            mCameraRenderer = new CameraRenderer(this, sdkEngine, new FURenderer.OnDebugListener() {
                @Override
                public void onFpsChanged(double fps, double callTime) {
                    Log.e(TAG, "onFpsChanged: fps " + String.format("%.2f", fps));
                }
            });
            FaceUnityView faceUnityView = findViewById(R.id.fu_view);
            faceUnityView.setVisibility(View.VISIBLE);
            faceUnityView.setModuleManager(mCameraRenderer.getFURenderer());
            sdkEngine.setCustomizedVideoParam(UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_EXTEND.extendParams(30,mCameraRenderer.mCameraHeight,mCameraRenderer.mCameraWidth,1500,2000,2000));
            UCloudRtcSdkEngine.onRGBCaptureResult(mCameraRenderer);
            mTextResolution.setVisibility(View.GONE);
            synchronized (extendByteBufferSync) {
                videoSourceData = sdkEngine.getNativeOpInterface().
                        createNativeByteBuffer(1280 * 720 * 4);
                videoSourceData.clear();
            }
            LifeCycleSensorManager lifeCycleSensorManager = new LifeCycleSensorManager(this, getLifecycle());
            lifeCycleSensorManager.setOnAccelerometerChangedListener(new LifeCycleSensorManager.OnAccelerometerChangedListener() {
                @Override
                public void onAccelerometerChanged(float x, float y, float z) {
                    if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                        if (Math.abs(x) > Math.abs(y)) {
                            mCameraRenderer.getFURenderer().onDeviceOrientationChanged(x > 0 ? 0 : 180);
                        } else {
                            mCameraRenderer.getFURenderer().onDeviceOrientationChanged(y > 0 ? 90 : 270);
                        }
                    }
                }
            });
        }
        else {
            UCloudRtcSdkEnv.setCaptureMode(
                    UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
        }
        if (mPublishMode == CommonUtils.AUTO_MODE) {
            mImgPreview.setVisibility(View.GONE);
            mTextPreview.setVisibility(View.GONE);
        } else {
            //手动发布时，按钮隐藏
            //mImgManualPub.setVisibility(View.VISIBLE);
            //mTextManualPub.setVisibility(View.VISIBLE);
            setIconStats(false);
        }
        if (!mCameraEnable && !mMicEnable) {
            mImgManualPubVideo.setVisibility(View.GONE);
            mTextManualPubVideo.setVisibility(View.GONE);
        }
        if (!mScreenEnable) {
            mImgManualPubScreen.setVisibility(View.GONE);
            mTextManualPubScreen.setVisibility(View.GONE);
        }
        mImgBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawer.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawer.openDrawer(Gravity.RIGHT);
                }
            }
        });

        mImgBtnSwitchCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
                if (mCameraRenderer != null) {
                    mCameraRenderer.switchCamera();
                }
            }
        });

        mTextResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });

        mImgBtnMuteMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muteMic();
            }
        });

        mImgBtnMuteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muteVideo();
            }
        });

        mImgBtnMuteSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muteSpeaker(!mSpeakerOn);
            }
        });

        mImgBtnMirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mirrorSwitch();
            }
        });

        mImgBtnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        mImgMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMix();
            }
        });

        mImgLocalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLocalRecord();
            }
        });

        mImgScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addScreenShotCallBack(mLocalVideoView);
            }
        });

        mImgRemoteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRemoteRecord();
            }
        });

        //手动发布
        //手动发布视频
        mImgManualPubVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mVideoIsPublished) {
                    sdkEngine.setStreamRole(UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH);
                    List<Integer> results = new ArrayList<>();
                    StringBuffer errorMessage = new StringBuffer();
                    // 重新刷新配置
                    refreshSettings();
                    if ( mCameraEnable || mMicEnable) {
                        if (!mVideoIsPublished) {
                            results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, mCameraEnable, mMicEnable).getErrorCode());
                        }
                    }
                    else {
                        errorMessage.append("Camera or Mic is disable!\n");
                    }

                    List<Integer> errorCodes = new ArrayList<>();
                    for (Integer result : results) {
                        if (result != 0)
                            errorCodes.add(result);
                    }
                    if (!errorCodes.isEmpty()) {
                        for (Integer errorCode : errorCodes) {
                            if (errorCode != NET_ERR_CODE_OK.ordinal())
                                errorMessage.append("UCLOUD_RTC_SDK_ERROR_CODE:" + errorCode + "\n");
                        }
                    }
                    if (errorMessage.length() > 0) {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, errorMessage.toString());
                    }
                    else {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "发布");
                    }
                }
                else {
                    sdkEngine.unPublish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
                }
            }
        });

        //手动发布屏幕
        mImgManualPubScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mScreenIsPublished) {
                    sdkEngine.setStreamRole(UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH);
                    List<Integer> results = new ArrayList<>();
                    StringBuffer errorMessage = new StringBuffer();
                    // 重新刷新配置
                    refreshSettings();
                    if (mScreenEnable && !mScreenIsPublished) {
                        if (isScreenCaptureSupport) {
                            results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN, true, false).getErrorCode());
                        }
                        else {
                            errorMessage.append("设备不支持屏幕捕捉\n");
                            //results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, true, true).getErrorCode());
                        }
                    }
                    else {
                        errorMessage.append("Screen is disable!\n");
                    }

                    List<Integer> errorCodes = new ArrayList<>();
                    for (Integer result : results) {
                        if (result != 0)
                            errorCodes.add(result);
                    }
                    if (!errorCodes.isEmpty()) {
                        for (Integer errorCode : errorCodes) {
                            if (errorCode != NET_ERR_CODE_OK.ordinal())
                                errorMessage.append("UCLOUD_RTC_SDK_ERROR_CODE:" + errorCode + "\n");
                        }
                    }
                    if (errorMessage.length() > 0) {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, errorMessage.toString());
                    }
                    else {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "发布");
                    }
                }
                else {
                    sdkEngine.unPublish(UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN);
                }
            }
        });

        //预览控制
        mImgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreview(!mIsPreview);
                mIsPreview = !mIsPreview;
                if (mIsPreview) {
                    mTextPreview.setText(R.string.stop_preview);
                    mLocalVideoView.setVisibility(View.VISIBLE);
                }
                else {
                    mTextPreview.setText(R.string.start_preview);
                    mLocalVideoView.setVisibility(View.INVISIBLE);
                }
            }
        });

        UCloudRtcSdkAuthInfo info = new UCloudRtcSdkAuthInfo();
        info.setAppId(mAppid);
        info.setToken(mRoomToken);
        info.setRoomId(mRoomid);
        info.setUId(mUserid);
        Log.d(TAG, " roomtoken = " + mRoomToken);
        initRecordManager();
        sdkEngine.joinChannel(info);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "on Stop");
        if((mVideoIsPublished || mScreenIsPublished) && !mLeaveRoomFlag){
            Intent service = new Intent(this, UCloudRtcForeGroundService.class);
            startService(service);
//            sdkEngine.controlAudio(false);
//            if (!mExtendCameraCapture) {
//                sdkEngine.controlLocalVideo(false);
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Intent service = new Intent(this, UCloudRtcForeGroundService.class);
        stopService(service);
//        sdkEngine.controlAudio(true);
//        if (!mExtendCameraCapture) {
//            sdkEngine.controlLocalVideo(true);
//        }
        synchronized (mSync) {
            if (mCameraRenderer != null) {
                mCameraRenderer.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        synchronized (mSync) {
            if (mCameraRenderer != null) {
                mCameraRenderer.onPause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "activity destory");
        super.onDestroy();
        releaseExtendCamera();
        System.gc();
    }

    private UCloudRtcSdkEventListener eventListener = new UCloudRtcSdkEventListener() {
        @Override
        public void onServerDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onServerDisconnect: ");
                    ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 服务器已断开");
                    stopTimeShow();
                    onMediaServerDisconnect();
                }
            });
        }

        @Override
        public void onJoinRoomResult(int code, String msg, String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 加入房间成功");
                        startTimeShow();
                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 加入房间失败 " +
                                code + " errmsg " + msg);
                        Intent intent = new Intent(UCloudRTCLiveTextureCustomActivity.this, ConnectActivity.class);
                        onMediaServerDisconnect();
                        startActivity(intent);
                        finish();
                    }

                }
            });
        }

        @Override
        public void onLeaveRoomResult(int code, String msg, String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeaveRoomFlag = true;
                    ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 离开房间 " +
                            code + " errmsg " + msg);
                    onMediaServerDisconnect();
                    finish();
                }
            });
        }

        @Override
        public void onRejoiningRoom(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "rejoining room");
                    ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 服务器重连中…… ");
                    stopTimeShow();
                }
            });
        }

        @Override
        public void onRejoinRoomResult(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "服务器重连成功");
                    startTimeShow();
                }
            });
        }

        @Override
        public void onLocalPublish(int code, String msg, UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {

                        int mediatype = info.getMediaType().ordinal();
                        mPublishMediaType = UCloudRtcSdkMediaType.matchValue(mediatype);
                        if(mLocalRender != null){
                            if (mediatype == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal()) {
                                mLocalRender.init();
                                mImgManualPubVideo.setImageResource(R.mipmap.stop);
                                mTextManualPubVideo.setText(R.string.pub_cancel_video);
                                if (!sdkEngine.isAudioOnlyMode()) {
                                    // UCloudRtcSdkSurfaceVideoView打开
                                    //mLocalVideoView.init(false);
                                    // Surfaceview打开
                                    //mLocalVideoView.setBackgroundColor(Color.TRANSPARENT);

                                    mLocalVideoView.setVisibility(View.VISIBLE);
                                    localViewWidth = mLocalVideoView.getMeasuredWidth();
                                    localViewHeight = mLocalVideoView.getMeasuredHeight();
                                    if (!mIsPreview) {
                                        if (mExtendCameraCapture) {
                                            sdkEngine.renderLocalView(info,mLocalRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                                        } else {
                                            sdkEngine.renderLocalView(info,mLocalRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                                        }
                                        //if (mPublishMode != CommonUtils.AUTO_MODE) {
//                                        setIconStats(true);
                                        //}
                                    }
                                    else {
//                                    setIconStats(true);
                                    }
                                    mVideoIsPublished = true;
                                    mLocalStreamInfo = info;
                                    mSwapStreamInfo = info;
                                    mLocalVideoView.setTag(mLocalStreamInfo);
//                                mLocalVideoView.setOnClickListener(mToggleScreenOnClickListener);
                                    mLocalVideoView.setOnClickListener(mScreenShotOnClickListener);
                                }
                            }
                            else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN.ordinal()) {
                                mScreenIsPublished = true;
                                mImgManualPubScreen.setImageResource(R.mipmap.stop);
                                mTextManualPubScreen.setText(R.string.pub_cancel_screen);
                                if (mScreenEnable && !mCameraEnable && !mMicEnable) {
                                    //sdkEngine.startPreview(info.getMediaType(), mLocalVideoView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL,null);
                                }
                            }
                            setIconStats(true);
                        }

                    }
                    else {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this,
                                "发布视频失败 " + code + " errmsg " + msg);
                    }

                }
            });
        }

        @Override
        public void onLocalUnPublish(int code, String msg, UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        if (info.getMediaType() == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO) {
                            if (mPublishMode == CommonUtils.AUTO_MODE) {
                                mImgManualPubVideo.setVisibility(View.GONE);
                                mTextManualPubVideo.setVisibility(View.GONE);
                            }
                            else {
                                mImgManualPubVideo.setImageResource(R.mipmap.publish);
                                mTextManualPubVideo.setText(R.string.pub_video);
                            }
                            if (mLocalVideoView != null) {
//                                localrenderview.refresh();
                            }
                            mVideoIsPublished = false;
                            mLocalVideoView.setVisibility(View.INVISIBLE);
                        } else if (info.getMediaType() == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                            mScreenIsPublished = false;
                            if (mPublishMode == CommonUtils.AUTO_MODE) {
                                mImgManualPubScreen.setVisibility(View.GONE);
                                mTextManualPubScreen.setVisibility(View.GONE);
                            }
                            else {
                                mImgManualPubScreen.setImageResource(R.mipmap.publish_screen);
                                mTextManualPubScreen.setText(R.string.pub_screen);
                            }
                            if (mScreenEnable && !mCameraEnable && !mMicEnable) {
//                                if (localrenderview != null) {
//                                    localrenderview.refresh();
//                                }
                            }
                        }
                        if (!mScreenIsPublished && !mVideoIsPublished) {
                            setIconStats(false);
                            setPreview(false);
                            mIsPreview = false;
                            mTextPreview.setText(R.string.start_preview);
                        }
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "取消发布成功");
                    }
                    else {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "取消发布失败 "
                                + code + " errmsg " + msg);
                    }
                }
            });
        }

        @Override
        public void onLocalUnPublishOnly(int i, String s, UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {

        }


        @Override
        public void onRemoteUserJoin(String uid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 用户 "
                            + uid + " 加入房间 ");
                }
            });
        }

        @Override
        public void onRemoteUserLeave(String uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "remote user " + uid + "leave ,reason: " + reason);
                    //onUserLeave(uid);
                    ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 用户 " +
                            uid + " 离开房间，离开原因： " + reason);
                }
            });
        }

        @Override
        public void onRemotePublish(UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //特殊情况下，譬如客户端在断网情况下离开房间，服务端可能还持有流，并没有超时，客户端就会收到自己的userid,
                    // 如果客户端是固定userid就可以过滤掉，如果不是，等待服务端超时也会删除流
                    Log.d(TAG, "onRemotePublish: " + info.getUId() + " me : " + mUserid);
                    if(!mUserid.equals(info.getUId())){
                        mSteamList.add(info);
                        if (!sdkEngine.isAutoSubscribe()) {
                            sdkEngine.subscribe(info);
                        } else {
                            //mSpinnerPopupWindowScribe.notifyUpdate();
                            //refreshStreamInfoText();
                        }
                    }
                }
            });
        }

        @Override
        public void onRemoteUnPublish(UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, " onRemoteUnPublish " + info.getMediaType() + " " + info.getUId());
                    ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 用户 " +
                            info.getUId() + " 取消媒体流 " + info.getMediaType());
                    String mkey = info.getUId() + info.getMediaType().toString();
                    sdkEngine.stopRemoteView(info);
                    if (mSwapStreamInfo != null && mSwapStreamInfo.getUId().equals(info.getUId()) && mSwapStreamInfo.getMediaType().toString().equals(info.getMediaType().toString())) {
//                        sdkEngine.stopRemoteView(mSwapStreamInfo);
                        int localIndex = mVideoAdapter.getPositionByKey(mUserid + mPublishMediaType.toString());
                        if (localIndex >= 0) {
                            Log.d(TAG, " onRemoteUnPublish localIndex " + localIndex);
                            mkey = mUserid + mPublishMediaType.toString();
                            sdkEngine.stopPreview(mPublishMediaType);
                            sdkEngine.renderLocalView(mLocalStreamInfo, mLocalRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                            mSwapStreamInfo = mLocalStreamInfo;
                        }
                    }
                    if (mVideoAdapter != null) {
                        mVideoAdapter.removeStreamView(mkey);
                    }
                }
            });
        }

        @Override
        public void onSubscribeResult(int code, String msg, UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        Log.d(TAG, " subscribe info: " + info);
                        URTCVideoViewInfo vinfo = new URTCVideoViewInfo();
                        vinfo.setUid(info.getUId());
                        vinfo.setMediaType(info.getMediaType());
                        vinfo.setEnableVideo(info.isHasVideo());
                        vinfo.setEnableAudio(info.isHasAudio());



                        String mkey = info.getUId() + info.getMediaType().toString();
                        vinfo.setKey(mkey);
                        //默认输出，和外部输出代码二选一
                        if (mVideoAdapter != null) {
                            vinfo.setStreamInfo(info);
                            mVideoAdapter.addStreamView(mkey, vinfo);
                        }

                        //textureview动态生成
//                        TextureView textureView = new TextureView(getApplicationContext());
//                        vinfo.setmRenderview(textureView);
//                        textureView.setTag(info);
//                        textureView.setId(R.id.video_view);
//                        if (vinfo != null && textureView != null) {
//                            sdkEngine.startRemoteView(info, textureView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL,null);
//                        }
                        //如果订阅成功就删除待订阅列表中的数据
                        //mSpinnerPopupWindowScribe.removeStreamInfoByUid(info.getUId());
                        //refreshStreamInfoText();
                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 订阅用户  " +
                                info.getUId() + " 流 " + info.getMediaType() + " 失败 " +
                                " code " + code + " msg " + msg);
                    }
                }
            });
        }

        @Override
        public void onUnSubscribeResult(int code, String msg, UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 取消订阅用户 " +
                            info.getUId() + " 类型 " + info.getMediaType());
                    if (mVideoAdapter != null) {
                        mVideoAdapter.removeStreamView(info.getUId() + info.getMediaType().toString());
                    }
                    //取消订阅又变成可订阅
                    //mSpinnerPopupWindowScribe.addStreamInfo(info, true);
                }
            });
        }

        @Override
        public void onLocalStreamMuteRsp(int code, String msg, UCloudRtcSdkMediaType mediatype, UCloudRtcSdkTrackType tracktype, boolean mute) {
            Log.d(TAG, " code " + code + " mediatype " + mediatype + " ttype " + tracktype + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        if (mediatype == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO) {
                            if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_AUDIO) {
                                onMuteMicResult(mute);
                            } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                                onMuteVideoResult(mute);
                            }
                        } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                            onMuteVideoResult(mute);
                        }
                    }
                }
            });
        }

        @Override
        public void onRemoteStreamMuteRsp(int code, String msg, String uid, UCloudRtcSdkMediaType mediatype, UCloudRtcSdkTrackType tracktype, boolean mute) {
            Log.d(TAG, " code " + code + " uid " + uid + " mediatype " + mediatype + " ttype " + tracktype + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        Log.d(TAG, " onRemoteStreamMuteRsp " + code + "msg " + msg);
                    }
                }
            });
        }

        @Override
        public void onRemoteTrackNotify(String uid, UCloudRtcSdkMediaType mediatype, UCloudRtcSdkTrackType tracktype, boolean mute) {
            Log.d(TAG, " uid " + uid + " mediatype " + mediatype + " ttype " + tracktype + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediatype == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO) {
                        String cmd = mute ? "关闭" : "打开";
                        if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_AUDIO) {
                            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 用户 " +
                                    uid + cmd + " 麦克风");
                        } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 用户 " +
                                    uid + cmd + " 摄像头");
                        }

                    } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                        String cmd = mute ? "关闭" : "打开";
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, " 用户 " +
                                uid + cmd + " 桌面流");
                    }
                }
            });
        }

        @Override
        public void onSendRTCStatus(UCloudRtcSdkStats uCloudRtcSdkStats) {

        }

        @Override
        public void onRemoteRTCStatus(UCloudRtcSdkStats uCloudRtcSdkStats) {

        }

        @Override
        public void onLocalAudioLevel(int volume) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //localprocess.setProgress(volume);
                    if (!mMuteMic) {
                        setVolume(volume);
                    }
                }
            });
        }

        @Override
        public void onRemoteAudioLevel(String uid, int volume) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mVideoAdapter != null) {
                        String mkey = uid + UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.toString();
                    }
                }
            });
        }

        @Override
        public void onKickoff(int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.longShow(UCloudRTCLiveTextureCustomActivity.this, " 被踢出会议 code " +
                            code);
                    Log.d(TAG, " user kickoff reason " + code);
                    Intent intent = new Intent(UCloudRTCLiveTextureCustomActivity.this, ConnectActivity.class);
                    onMediaServerDisconnect();
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onWarning(int warn) {

        }

        @Override
        public void onError(int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == UCloudRtcSdkErrorCode.NET_ERR_SDP_SWAP_FAIL.ordinal()) {
                        ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "sdp swap failed");
                    }
                }
            });
        }

        @Override
        public void onRecordStop(int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.longShow(UCloudRTCLiveTextureCustomActivity.this, "录制结束: " + (code == NET_ERR_CODE_OK.ordinal()?"成功":"失败: "+ code));
                    if(mIsRemoteRecording){
                        mIsRemoteRecording = false;
                        mImgRemoteRecord.setImageResource(R.mipmap.remote_record);
                        mTextRemoteRecord.setText(R.string.start_remote_record);
                    }
                }
            });
        }

        @Override
        public void onQueryMix(int code, String msg, int type, String mixId, String fileName) {
            Log.d(TAG, "onQueryMix: "+ code + " msg: "+ msg + " type: "+ type);
        }

        @Override
        public void onRecordStatusNotify(UCloudRtcSdkMediaServiceStatus status, int code, String msg, String userId, String roomId, String mixId, String fileName) {
            if (status == UCloudRtcSdkMediaServiceStatus.RECORD_STATUS_START) {
                String videoPath = "http://" + mBucket + "." + mRegion + ".ufileos.com/" + fileName;
                Log.d(TAG, "remote record path: " + videoPath + ".mp4");
                ToastUtils.longShow(UCloudRTCLiveTextureCustomActivity.this, "观看地址: " + videoPath);
                mIsRemoteRecording = true;
                mImgRemoteRecord.setImageResource(R.mipmap.stop);
                mTextRemoteRecord.setText(R.string.remote_recording);
                if (mAtomOpStart)
                    mAtomOpStart = false;
            } else if (status == UCloudRtcSdkMediaServiceStatus.RECORD_STATUS_STOP_REQUEST_SEND) {
                if (mIsRemoteRecording) {
                    mIsRemoteRecording = false;
                    mImgRemoteRecord.setImageResource(R.mipmap.remote_record);
                    mTextRemoteRecord.setText(R.string.start_remote_record);
                }
            } else {
                ToastUtils.longShow(UCloudRTCLiveTextureCustomActivity.this, "录制异常: 原因：" + code);
            }
        }

        @Override
        public void onRelayStatusNotify(UCloudRtcSdkMediaServiceStatus status, int code, String msg, String userId, String roomId, String mixId, String[] pushUrls) {
            if (status == UCloudRtcSdkMediaServiceStatus.RELAY_STATUS_START) {
                // ulive cdn watch address: http://rtchls.ugslb.com/rtclive/roomid.flv
                mIsMixing = true;
                mImgMix.setImageResource(R.mipmap.stop);
                mTextMix.setText(R.string.mixing);
                if (mAtomOpStart)
                    mAtomOpStart = false;
            } else if (status == UCloudRtcSdkMediaServiceStatus.RELAY_STATUS_STOP_REQUEST_SEND) {
                if (mIsMixing) {
                    mIsMixing = false;
                    mImgMix.setImageResource(R.mipmap.mix);
                    mTextMix.setText(R.string.start_mix);
                } else {
                    ToastUtils.longShow(UCloudRTCLiveTextureCustomActivity.this, "转推异常: 原因：" + code);
                }
            }
        }

        @Override
        public void onAddStreams(int code, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onAddStreams: "+ code + msg);
                }
            });
        }

        @Override
        public void onDelStreams(int code, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onDelStreams: "+ code + msg);
                }
            });
        }

        @Override
        public void onLogOffUsers(int code, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onLogOffUsers: "+ code + " msg: "+ msg);
                }
            });
        }

        @Override
        public void onMsgNotify(int code, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onMsgNotify: code: " + code + "msg: " + msg);
                }
            });
        }

        @Override
        public void onLogOffNotify(int cmdType, String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onLogOffUsers: "+ cmdType + " userId: "+ userId);
                }
            });
        }

        @Override
        public void onRecordStart(int code, String fileName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onRecordStart: " + code + " fileName: " + fileName);
                }
            });
        }

        @Override
        public void onServerBroadCastMsg(String uid, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onServerBroadCastMsg: uid: " + uid + "msg: " + msg);
                }
            });
        }

        @Override
        public void onAudioDeviceChanged(UCloudRtcSdkAudioDevice device) {
            defaultAudioDevice = device;
//            URTCLogUtils.d(TAG,"URTCAudioManager: room change device to "+ defaultAudioDevice);
            if (defaultAudioDevice == UCloudRtcSdkAudioDevice.UCLOUD_RTC_SDK_AUDIODEVICE_SPEAKER) {
                mImgBtnMuteSpeaker.setImageResource(R.mipmap.speaker);
                mSpeakerOn = true;
            } else {
                mSpeakerOn = false;
                mImgBtnMuteSpeaker.setImageResource(R.mipmap.speaker_off);
            }
        }

        @Override
        public void onPeerLostConnection(int type, UCloudRtcSdkStreamInfo info) {
            Log.d(TAG, "onPeerLostConnection: type: " + type + "info: " + info);
        }

        @Override
        public void onPeerReconnected(int i, UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {

        }

        @Override
        public void onNetWorkQuality(String userId, UCloudRtcSdkStreamType streamType, UCloudRtcSdkMediaType mediaType, UCloudRtcSdkNetWorkQuality quality) {
            Log.d(TAG, "onNetWorkQuality: userid: " + userId + "streamType: " + streamType + "mediatype : "+ mediaType + " quality: " + quality);
        }

        @Override
        public void onAudioFileFinish() {

        }

        @Override
        public void onFirstLocalVideoFrame() {

        }

        @Override
        public void onStartLocalRenderFailed(String s) {

        }
    };

    private RemoteVideoAdapter.RemoveRemoteStreamReceiver mRemoveRemoteStreamReceiver = new RemoteVideoAdapter.RemoveRemoteStreamReceiver() {
        @Override
        public void onRemoteStreamRemoved(boolean swaped) {
            if (swaped) {
                if (mClass == UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL) {
                    sdkEngine.stopPreview(mLocalStreamInfo.getMediaType());
                    sdkEngine.renderLocalView(mLocalStreamInfo, mLocalVideoView,null,null);
                } else if (mLocalVideoView.getTag(R.id.swap_info) != null) {
                    UCloudRtcSdkStreamInfo remoteStreamInfo = (UCloudRtcSdkStreamInfo) mLocalVideoView.getTag(R.id.swap_info);
                    sdkEngine.stopRemoteView(remoteStreamInfo);
                }
            }
        }
    };

    UCloudRtcRecordListener mLocalRecordListener = new UCloudRtcRecordListener() {
        @Override
        public void onLocalRecordStart(String path, int code,String msg) {
            Log.d(TAG, "onLocalRecordStart: " + path + " code: "+ code + " msg: " + msg);
        }

        @Override
        public void onLocalRecordStop(String path, long fileLength, int code) {
            Log.d(TAG, "onLocalRecordStop: " + path + "fileLength: "+ fileLength + "code: "+ code);
        }

        @Override
        public void onRecordStatusCallBack(long duration, long fileSize) {
            Log.d(TAG, "onRecordStatusCallBack duration: " + duration + " fileSize: "+ fileSize);
        }
    };

    private View.OnClickListener mToggleScreenOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleFullScreen();
        }
    };

    private View.OnClickListener mScreenShotOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "takeScreenShot: ");
            takeScreenShot(true,mLocalStreamInfo);
        }
    };

    private View.OnClickListener mSwapRemoteLocalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof TextureView) {
                UCloudRtcSdkStreamInfo clickStreamInfo = (UCloudRtcSdkStreamInfo) v.getTag();
                boolean swapLocal = mSwapStreamInfo.getUId().equals(mUserid);
                boolean clickLocal = clickStreamInfo.getUId().equals(mUserid);
                Log.d(TAG, "mSwapStreamInfo: " + mSwapStreamInfo + " clickInfo: " + clickStreamInfo);
                Log.d(TAG, "onClick swaplocal" + swapLocal + " clickLocal: " + clickLocal);
                if (swapLocal && !clickLocal) {
                    sdkEngine.stopRemoteView(clickStreamInfo);
                    sdkEngine.stopPreview(mSwapStreamInfo.getMediaType());
//                        sdkEngine.renderLocalView(mSwapStreamInfo, v,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                    UCloudRtcRenderTextureView remoteRender = (UCloudRtcRenderTextureView) v.getTag(R.id.render);
                    sdkEngine.renderLocalView(mSwapStreamInfo, remoteRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
//                        sdkEngine.startRemoteView(clickStreamInfo, mLocalVideoView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL,null);
                    sdkEngine.startRemoteView(clickStreamInfo, mLocalRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                } else if (!swapLocal && clickLocal) {
                    sdkEngine.stopRemoteView(mSwapStreamInfo);
                    sdkEngine.stopPreview(clickStreamInfo.getMediaType());
                    UCloudRtcRenderTextureView remoteRender = (UCloudRtcRenderTextureView) v.getTag(R.id.render);
                    sdkEngine.renderLocalView(clickStreamInfo, mLocalRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                    sdkEngine.startRemoteView(mSwapStreamInfo, remoteRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                } else if (!swapLocal && !clickLocal) {
                    sdkEngine.stopRemoteView(mSwapStreamInfo);
                    sdkEngine.stopRemoteView(clickStreamInfo);
                    sdkEngine.startRemoteView(clickStreamInfo, mLocalRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                    UCloudRtcRenderTextureView remoteRender = (UCloudRtcRenderTextureView) v.getTag(R.id.render);
                    sdkEngine.startRemoteView(mSwapStreamInfo, remoteRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                }
                v.setTag(mSwapStreamInfo);
                mVideoAdapter.updateSwapInfo(clickStreamInfo, mSwapStreamInfo);
                mSwapStreamInfo = clickStreamInfo;
            }
        }
    };

    private void switchCamera() {
        sdkEngine.switchCamera();
        ToastUtils.shortShow(this, "切换摄像头");
        mSwitchCamera = !mSwitchCamera;
    }

    private boolean muteMic() {
        sdkEngine.muteLocalMic(!mMuteMic);
        if (!mMuteMic) {
            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "关闭麦克风");
        } else {
            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "打开麦克风");
        }
        return false;
    }

    private boolean muteVideo() {
        if (mScreenEnable || mCameraEnable) {
            if (isScreenCaptureSupport && !mCameraEnable) {
                sdkEngine.muteLocalVideo(!mMuteVideo, UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN);
            } else {
                sdkEngine.muteLocalVideo(!mMuteVideo, UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
            }
        }
        if (!mMuteVideo) {
            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "关闭摄像头");
        } else {
            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "打开摄像头");
        }
        return false;
    }

    private void muteSpeaker(boolean enable) {
        if (mSpeakerOn) {
            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "关闭喇叭");
        } else {
            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this, "打开喇叭");
        }
        mSpeakerOn = !mSpeakerOn;
        sdkEngine.setSpeakerOn(enable);
        mImgBtnMuteSpeaker.setImageResource(enable ? R.mipmap.speaker : R.mipmap.speaker_off);
    }

    private void onMuteVideoResult(boolean mute) {
        mMuteVideo = mute;
        mImgBtnMuteVideo.setImageResource(mMuteVideo ? R.mipmap.camera_off :
                R.mipmap.camera);
        if (mLocalVideoView.getTag(R.id.swap_info) != null) {
            UCloudRtcSdkStreamInfo remoteInfo = (UCloudRtcSdkStreamInfo) mLocalVideoView.getTag(R.id.swap_info);
            String mkey = remoteInfo.getUId() + remoteInfo.getMediaType().toString();
            View view = mRemoteGridView.getChildAt(mVideoAdapter.getPositionByKey(mkey));
            if (mute) {
                view.setVisibility(View.INVISIBLE);
                view.setBackgroundColor(Color.BLACK);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (mute) {
//                localrenderview.refresh();
                mLocalVideoView.setVisibility(View.INVISIBLE);
            }
            else {
                mLocalVideoView.setVisibility(View.VISIBLE);
            }
        }

    }

    private void onMuteMicResult(boolean mute) {
        mMuteMic = mute;
        if (mMuteMic) {
            mImgSoundVolume.setVisibility(View.INVISIBLE);
        }
        else {
            mImgSoundVolume.setVisibility(View.VISIBLE);
        }
        mImgBtnMuteMic.setImageResource(mMuteMic ? R.mipmap.mic_off :
                R.mipmap.mic);
        mImgMicSts.setImageResource(mMuteMic ? R.mipmap.mic_disable :
                R.mipmap.mic_volume);
    }

    private void mirrorSwitch() {
        mMirror = !mMirror;
        UCloudRtcSdkEnv.setFrontCameraMirror(mMirror);
        mImgBtnMirror.setImageResource(mMirror ? R.mipmap.mirror_on :
                R.mipmap.mirror);
    }

    private void releaseExtendCamera() { // 释放扩展摄像头资源
        if (mUCloudRTCDataProvider != null) {
            mUCloudRTCDataProvider.releaseBuffer();
            mUCloudRTCDataProvider = null;
        }
        if (mCameraRenderer != null) {
            mCameraRenderer.releaseBuffer();
            mCameraRenderer = null;
        }
        if (UCloudRtcSdkEnv.getCaptureMode() == UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_EXTEND) {
            //这里回收一遍
            while (mQueueByteBuffer.size() != 0) {
                ByteBuffer videoData = mQueueByteBuffer.poll();
                if (videoData != null) {
                    Log.d("UCloudRTCLiveActivity", "videoData clear");
                    videoData.clear();
                    videoData = null;
                }
            }
        }
    }

    private void endCall() {
        sdkEngine.leaveChannel().ordinal();
//        Intent intent = new Intent(UCloudRTCLiveTextureActivity.this, ConnectActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        releaseExtendCamera();
//        onMediaServerDisconnect();
//        startActivity(intent);
//        finish();
    }
    private void onMediaServerDisconnect() {
        //mLocalVideoView.release();
        clearGridItem();
        UCloudRtcSdkEngine.destroy();
    }
    private void clearGridItem() {
        mVideoAdapter.clearAll();
        mVideoAdapter.notifyDataSetChanged();
    }
    private void startTimeShow() {
        timeShow.setBase(SystemClock.elapsedRealtime());
        timeShow.start();
    }

    private void stopTimeShow() {
        timeShow.stop();
    }

    public void toggleFullScreen() {
        if (!mLocalViewFullScreen) {
            setSystemUIVisible(false);
            //隐藏顶部标题和底部工具栏
            mTitleBar.setVisibility(View.GONE);
            mToolBar.setVisibility(View.GONE);
            StatusBarUtils.removeStatusView(this);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth, screenHeight + mToolBar.getHeight());
            params.setMargins(0, 0, 0, 0);
            mLocalVideoView.setLayoutParams(params);
            //抽屉随回显拉长
            DrawerLayout.LayoutParams dl_params = (DrawerLayout.LayoutParams) mDrawerMenu.getLayoutParams();
            dl_params.topMargin = 0;
            dl_params.bottomMargin = 0;
            //隐藏麦克风状态图标
            mImgSoundVolume.setVisibility(View.INVISIBLE);
            mImgMicSts.setVisibility(View.INVISIBLE);
            Log.d(TAG, "Switch full screen width: " + params.width + " height: " + params.height);
        }
        else {
            setSystemUIVisible(true);
            //退出全屏
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(localViewWidth, localViewHeight);
            params.setMargins(0, mTitleBar.getHeight(), 0, mToolBar.getHeight());
            mLocalVideoView.setLayoutParams(params);
            //显示顶部标题和底部工具栏
            mTitleBar.setVisibility(View.VISIBLE);
            mToolBar.setVisibility(View.VISIBLE);
            StatusBarUtils.addStatusView(this);
            //抽屉菜单还原
            DrawerLayout.LayoutParams dl_params = (DrawerLayout.LayoutParams) mDrawerMenu.getLayoutParams();
            dl_params.topMargin = mTitleBar.getHeight();
            dl_params.bottomMargin =  mToolBar.getHeight();
            //还原麦克风状态图标
            if (!mMuteMic) {
                mImgSoundVolume.setVisibility(View.VISIBLE);
            }
            mImgMicSts.setVisibility(View.VISIBLE);

            Log.d(TAG, "Quit full screen. width: " + params.width + " height: " + params.height);
        }
        mLocalViewFullScreen = !mLocalViewFullScreen;
    }

    private UCloudRTCScreenShot mUCloudRTCScreenShot = new UCloudRTCScreenShot() {
        @Override
        public void onReceiveRGBAData(ByteBuffer rgbBuffer, int width, int height) {
            final Bitmap bitmap = Bitmap.createBitmap(width * 1, height * 1, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(rgbBuffer);
            String name = "/mnt/sdcard/urtcscreen_"+System.currentTimeMillis() +".jpg";
            File file = new File(name);
            try {
                FileOutputStream out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    out.flush();
                    out.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "screen shoot : " + name);
            ToastUtils.shortShow(UCloudRTCLiveTextureCustomActivity.this,"screen shoot : " + name);
        }
    };

    private void addScreenShotCallBack(View view){
        if (view instanceof UCloudRtcSdkSurfaceVideoView) {
            ((UCloudRtcSdkSurfaceVideoView)view).setScreenShotBack(mUCloudRTCScreenShot);
        }
        else if(view instanceof UCloudRtcRenderView) {
            ((UCloudRtcRenderView)view).setScreenShotBack(mUCloudRTCScreenShot);
        }
        else {
            if(view == mLocalVideoView){
                if(view.getTag() != null && view.getTag() instanceof UCloudRtcSdkStreamInfo){
                    takeScreenShot(true,(UCloudRtcSdkStreamInfo)view.getTag());
                }
            }
        }
    }

    //初始化视频录制
    private void initRecordManager() {
        RtcRecordManager.init("");
        Log.d(TAG, "initRecordManager: cache path:" + RtcRecordManager.getVideoCachePath());
    }

    private void toggleLocalRecord(){
        if (!mIsLocalRecording) {
            Log.d(TAG, " start local record: ");
            //URTCRecordManager.getInstance().startRecord(UCloudRtcSdkRecordType.U_CLOUD_RTC_SDK_RECORD_TYPE_MP4,"mnt/sdcard/urtc/mp4/"+ System.currentTimeMillis()+".mp4",mLocalRecordListener,1000);
            mIsLocalRecording = true;
            mImgLocalRecord.setImageResource(R.mipmap.stop);
            mTextLocalRecord.setText(R.string.local_recording);
        }
        else {
            Log.d(TAG, " stop local record: ");
            //URTCRecordManager.getInstance().stopRecord();
            mIsLocalRecording = false;
            mImgLocalRecord.setImageResource(R.mipmap.record);
            mTextLocalRecord.setText(R.string.start_local_record);
        }
    }

    private void toggleRemoteRecord(){
        if (!mIsRemoteRecording) {
            Log.d(TAG, " start remote record: ");
            mAtomOpStart = true;
            UCloudRtcSdkMixProfile mixProfile = UCloudRtcSdkMixProfile.getInstance().assembleMixParamsBuilder()
                    .type(UCloudRtcSdkMixProfile.MIX_TYPE_RECORD)
                    //画面模式
                    .layout(UCloudRtcSdkMixProfile.LAYOUT_CLASS_ROOM_2)
                    //画面分辨率
                    .resolution(1280, 720)
                    //背景色
                    .bgColor(0, 0, 0)
                    //画面帧率
                    .frameRate(15)
                    //画面码率
                    .bitRate(1000)
                    //h264视频编码
                    .videoCodec(UCloudRtcSdkMixProfile.VIDEO_CODEC_H264)
                    //编码质量
                    .qualityLevel(UCloudRtcSdkMixProfile.QUALITY_H264_CB)
                    //音频编码
                    .audioCodec(UCloudRtcSdkMixProfile.AUDIO_CODEC_AAC)
                    //主讲人ID
                    .mainViewUserId(mUserid)
                    //主讲人媒体类型
                    .mainViewMediaType(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                    //加流方式手动
                    .addStreamMode(UCloudRtcSdkMixProfile.ADD_STREAM_MODE_MANUAL)
                    //添加流列表，也可以后续调用MIX_TYPE_UPDATE 动态添加
                    .addStream(mUserid,UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                    .build();
            sdkEngine.startRelay(mixProfile);

        }
        else if (!mAtomOpStart) {
            Log.d(TAG, " stop remote record: ");
            mAtomOpStart = true;
            sdkEngine.stopRecord();
        }
    }

    private void toggleMix(){
        if (!mIsMixing) {
            Log.d(TAG, " start mix: ");
            mAtomOpStart = true;
            UCloudRtcSdkMixProfile mixProfile = UCloudRtcSdkMixProfile.getInstance().assembleMixParamsBuilder()
                    .type(UCloudRtcSdkMixProfile.MIX_TYPE_RELAY)
                    //画面模式
                    .layout(UCloudRtcSdkMixProfile.LAYOUT_CLASS_ROOM_2)
                    //画面分辨率
                    .resolution(1280, 720)
                    //背景色
                    .bgColor(0, 0, 0)
                    //画面帧率
                    .frameRate(15)
                    //画面码率
                    .bitRate(1000)
                    //h264视频编码
                    .videoCodec(UCloudRtcSdkMixProfile.VIDEO_CODEC_H264)
                    //编码质量
                    .qualityLevel(UCloudRtcSdkMixProfile.QUALITY_H264_CB)
                    //音频编码
                    .audioCodec(UCloudRtcSdkMixProfile.AUDIO_CODEC_AAC)
                    //主讲人ID
                    .mainViewUserId(mUserid)
                    //主讲人媒体类型
                    .mainViewMediaType(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                    //加流方式手动
                    .addStreamMode(UCloudRtcSdkMixProfile.ADD_STREAM_MODE_MANUAL)
                    //添加流列表，也可以后续调用MIX_TYPE_UPDATE 动态添加
                    .addStream(mUserid,UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                    //设置转推cdn 的地址
                    .addPushUrl("rtmp://rtcpush.ugslb.com/rtclive/" + mRoomid)
                    .build();
            sdkEngine.startRelay(mixProfile);
        }
        else if(!mAtomOpStart) {
            Log.d(TAG, " stop mix: ");
            mAtomOpStart = true;
            sdkEngine.stopRelay(null);
        }
    }

    /**
     * 根据音量大小设置录音时的音量动画
     */
    private void setVolume(int voiceValue) {
        if (!mMuteMic) {
            if (voiceValue < 15) {
                mImgSoundVolume.setImageResource(R.mipmap.sound_volume_01);
            } else if (voiceValue > 15 && voiceValue < 30) {
                mImgSoundVolume.setImageResource(R.mipmap.sound_volume_02);
            } else if (voiceValue > 30 && voiceValue < 45) {
                mImgSoundVolume.setImageResource(R.mipmap.sound_volume_03);
            } else if (voiceValue > 45 && voiceValue < 60) {
                mImgSoundVolume.setImageResource(R.mipmap.sound_volume_04);
            } else if (voiceValue > 60 && voiceValue < 75) {
                mImgSoundVolume.setImageResource(R.mipmap.sound_volume_05);
            } else if (voiceValue > 75 && voiceValue < 90) {
                mImgSoundVolume.setImageResource(R.mipmap.sound_volume_06);
            } else if (voiceValue > 100) {
                mImgSoundVolume.setImageResource(R.mipmap.sound_volume_07);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    private void setSystemUIVisible(boolean show) {
        if (show) {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void showPopupWindow() {
        mResolutionPopupWindow.setAdapter(mAdapter);
        mResolutionPopupWindow.setWidth(mTextResolution.getWidth());
        mResolutionPopupWindow.showAsDropDown(mTextResolution);
    }

    private VideoProfilePopupWindow.OnSpinnerItemClickListener mOnResulutionOptionClickListener = new VideoProfilePopupWindow.OnSpinnerItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            mVideoProfileSelect = pos;
            sdkEngine.changePushResolution(UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect));
            mTextResolution.setText(mResolutionOption.get(mVideoProfileSelect));
            mResolutionPopupWindow.dismiss();
        }
    };

    private UVCCamera initUVCCamera(USBMonitor.UsbControlBlock ctrlBlock) {
        Log.d(TAG, "initUVCCamera-----mVideoProfileSelect:" + mVideoProfileSelect + " width:" + UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getWidth()
                + " height:" + UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getHeight());
        final UVCCamera camera = new UVCCamera();
        camera.open(ctrlBlock);
        camera.setPreviewSize(
                UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getWidth(),
                UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getHeight(),
                UVCCamera.FRAME_FORMAT_YUYV
        );

        //SurfaceTexture surface= mLocalVideoView.getSurfaceTexture();
        //UCloudRtcRenderView surface = mLocalVideoView.getSurfaceView();

        // Start preview to external GL texture
        // NOTE : this is necessary for callback passed to [UVCCamera.setFrameCallback]
        // to be triggered afterwards
        //camera.setPreviewTexture(surface);
        camera.startPreview();

        camera.setFrameCallback(new IFrameCallback() {
            @Override
            public void onFrame(ByteBuffer frame) {
                createFrameByteBuffer(frame);
            }
        },mUVCCameraFormat);
        return camera;
    }

    //外置数据输入监听
    private UCloudRTCDataProvider mUCloudRTCDataProvider = new UCloudRTCDataProvider() {
        private ByteBuffer cacheBuffer;
        private ByteBuffer videoSourceData;

        @Override
        public ByteBuffer provideRGBData(List<Integer> params) {
//            videoSourceData = mQueueByteBuffer.poll();
            if (videoSourceData == null) {
                //Log.d("UCloudRTCLiveActivity", "provideRGBData: " + null);
                return null;
            } else {
                //Log.d("UCloudRTCLiveActivity", "provideRGBData: ! = null");
                params.add(mURTCVideoFormat);
                params.add(UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getWidth());
                params.add(UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getHeight());
                if (cacheBuffer == null) {
                    cacheBuffer = sdkEngine.getNativeOpInterface().
                            createNativeByteBuffer(1280 * 720 * 4);
                    Log.d("UCloudRTCLiveActivity", "byteBuffer createNativeByteBuffer call ");
                    cacheBuffer.clear();
                } else {
                    cacheBuffer.rewind();
                }
                synchronized (extendByteBufferSync) {
                    cacheBuffer.put(videoSourceData);
                    videoSourceData.rewind();
                }
                cacheBuffer.flip();
                return cacheBuffer;
            }
        }

        @Override
        public void releaseBuffer() {
            synchronized (extendByteBufferSync) {
                if (videoSourceData != null) {
                    videoSourceData.clear();
                    sdkEngine.getNativeOpInterface().releaseNativeByteBuffer(videoSourceData);
                    videoSourceData = null;
                }
            }
            if (cacheBuffer != null) {
                cacheBuffer.clear();
                sdkEngine.getNativeOpInterface().releaseNativeByteBuffer(cacheBuffer);
                cacheBuffer = null;
            }
        }
    };

    //摄像数据输出监听
    private UCloudRTCDataReceiver mUCloudRTCDataReceiver = new UCloudRTCDataReceiver() {
        //private int limit = 0;
        private ByteBuffer cache;

        @Override
        public void onReceiveRGBAData(ByteBuffer rgbBuffer, int width, int height) {
            //Log.d("MainActivity", "onReceiveRGBAData!");

/*            final Bitmap bitmap = Bitmap.createBitmap(width * 1, height * 1, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(rgbBuffer);
            String name = "/mnt/sdcard/yuvrgba"+ limit+".jpg";
            if (limit++ < 5) {
                File file = new File(name);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }

        @Override
        public int getType() {
            return UCloudRTCDataReceiver.I420_TO_ABGR;
        }

        @Override
        public ByteBuffer getCacheBuffer() {
            if(cache == null){
                //根据需求来，设置最大的可能用到的buffersize，后续回调会复用这块内存
                int size = 4096*2160*4;
                cache = sdkEngine.getNativeOpInterface().
                        createNativeByteBuffer(4096*2160*4);
            }
            cache.clear();
            return cache;
        }

        @Override
        public void releaseBuffer() {
            if(cache != null)
                sdkEngine.getNativeOpInterface().releaseNativeByteBuffer(cache);
            cache = null;
        }
    };

    private void createFrameByteBuffer(ByteBuffer frame){
        try {
            if(frame != null){
                //add videoSource
                synchronized (extendByteBufferSync) {
                    if (videoSourceData != null) {
                        videoSourceData.clear();
                        videoSourceData.put(frame);
                        videoSourceData.flip();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateVideoFormat(int videoFormat){
        switch (videoFormat) {
            case CommonUtils.nv21_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_NV21;
                mURTCVideoFormat = UCloudRTCDataProvider.NV21;
                break;
            case CommonUtils.nv12_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_YUV420SP;
                mURTCVideoFormat = UCloudRTCDataProvider.NV12;
                break;
            case CommonUtils.i420_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_I420;
                mURTCVideoFormat = UCloudRTCDataProvider.I420;
                break;
            case CommonUtils.rgba_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_RGBX;
                mURTCVideoFormat = UCloudRTCDataProvider.RGBA_TO_I420;
                break;
            case CommonUtils.argb_format:
                //UVCCamera不支持输出argb格式，测试用rgbx格式，输出时颜色会有偏差
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_ARGB;
                mURTCVideoFormat = UCloudRTCDataProvider.ARGB_TO_I420;
                break;
            case CommonUtils.rgb24_format:
                //UVCCamera的RGB888与libyuv的数据有大小端区别，所以UVCCamera输出使用BGR888,保证颜色正确
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_BGR888;
                mURTCVideoFormat = UCloudRTCDataProvider.RGB24_TO_I420;
                break;
            case CommonUtils.rgb565_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_RGB565;
                mURTCVideoFormat = UCloudRTCDataProvider.RGB565_TO_I420;
                break;
        }
    }

    private void setPreview(boolean onOff) {
        if (onOff) {
            if (mExtendCameraCapture) {
                sdkEngine.startCameraPreview(
                        mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
            } else {
                sdkEngine.startCameraPreview(
                        mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
            }
        }
        else {
            sdkEngine.stopPreview(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
        }
    }

    private void setIconStats(boolean visible) {
        if (!visible) {
            //未发布时，按钮隐藏
            mImgPreview.setVisibility(View.VISIBLE);
            mTextPreview.setVisibility(View.VISIBLE);
            mImgMix.setVisibility(View.GONE);
            mTextMix.setVisibility(View.GONE);
            mImgLocalRecord.setVisibility(View.GONE);
            mTextLocalRecord.setVisibility(View.GONE);
            mImgScreenshot.setVisibility(View.GONE);
            mTextScreenshot.setVisibility(View.GONE);
            mImgRemoteRecord.setVisibility(View.GONE);
            mTextRemoteRecord.setVisibility(View.GONE);
            mImgBtnMuteMic.setVisibility(View.INVISIBLE);
            mImgBtnMuteVideo.setVisibility(View.INVISIBLE);
            mImgBtnMirror.setVisibility(View.INVISIBLE);
            mTextResolution.setVisibility(View.INVISIBLE);
        }
        else {
            mImgBtnMuteMic.setVisibility(View.VISIBLE);
            mImgBtnMuteVideo.setVisibility(View.VISIBLE);
            mTextResolution.setVisibility(View.VISIBLE);
            mImgBtnMirror.setVisibility(View.VISIBLE);
            mImgMix.setVisibility(View.VISIBLE);
            mTextMix.setVisibility(View.VISIBLE);
            mImgLocalRecord.setVisibility(View.VISIBLE);
            mTextLocalRecord.setVisibility(View.VISIBLE);
            mImgScreenshot.setVisibility(View.VISIBLE);
            mTextScreenshot.setVisibility(View.VISIBLE);
            mImgRemoteRecord.setVisibility(View.VISIBLE);
            mTextRemoteRecord.setVisibility(View.VISIBLE);
            mImgPreview.setVisibility(View.INVISIBLE);
            mTextPreview.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshSettings() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);

        mCameraEnable = preferences.getBoolean(CommonUtils.CAMERA_ENABLE, CommonUtils.CAMERA_ON);
        mMicEnable = preferences.getBoolean(CommonUtils.MIC_ENABLE, CommonUtils.MIC_ON);
        mScreenEnable = preferences.getBoolean(CommonUtils.SCREEN_ENABLE, CommonUtils.SCREEN_OFF);
        int classType = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL.ordinal());
        mClass = UCloudRtcSdkRoomType.valueOf(classType);
        mPublishMode = preferences.getInt(CommonUtils.PUBLISH_MODE, CommonUtils.AUTO_MODE);
        mScribeMode = preferences.getInt(CommonUtils.SUBSCRIBE_MODE, CommonUtils.AUTO_MODE);
        mExtendCameraCapture = preferences.getBoolean(CommonUtils.CAMERA_CAPTURE_MODE, false);
        mExtendVideoFormat = preferences.getInt(CommonUtils.EXTEND_CAMERA_VIDEO_FORMAT, CommonUtils.i420_format);
        updateVideoFormat(mExtendVideoFormat);


        //分辨率选择菜单
        String[] resolutions = getResources().getStringArray(R.array.videoResolutions);
        mResolutionOption.addAll(Arrays.asList(resolutions));

        Log.d(TAG, " Camera enable is: " + mCameraEnable + " Mic enable is: " + mMicEnable + " ScreenShare enable is: " + mScreenEnable);
        if (!mScreenEnable && !mCameraEnable && mMicEnable) {
            sdkEngine.setAudioOnlyMode(true);
        }
        else {
            sdkEngine.setAudioOnlyMode(false);
        }
        sdkEngine.configLocalCameraPublish(mCameraEnable);
        sdkEngine.configLocalAudioPublish(mMicEnable);
        if (isScreenCaptureSupport) {
            sdkEngine.configLocalScreenPublish(mScreenEnable);
        }
        else {
            sdkEngine.configLocalScreenPublish(false);
        }
    }

    @Override
    public View.OnClickListener provideSwapListener() {
        return mSwapRemoteLocalListener;
    }

    @Override
    public boolean isLocalStream(String uid) {
        return uid.equals(mUserid);
    }

    @Override
    public void stopRender(URTCVideoViewInfo info) {
        Log.d(TAG, "stop render: " + info.getRenderview());
        if (info != null && info.getRenderview() != null) {
            if (info.getStreamInfo().getUId().equals(mUserid)) {
                Log.d(TAG, "stop old list cache local render: " + info.getRenderview());
                sdkEngine.stopPreview(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, info.getRenderview());
            } else {
                Log.d(TAG, "stop old list cache remote render info: " + info.getStreamInfo());
                sdkEngine.stopRemoteView(info.getStreamInfo());
            }
        }
    }


    private void takeScreenShot(boolean isLocal,UCloudRtcSdkStreamInfo streamInfo){
        sdkEngine.takeSnapShot(isLocal,streamInfo,new UCloudRTCScreenShot() {
            @Override
            public void onReceiveRGBAData(ByteBuffer rgbBuffer, int width, int height) {
                Log.d(TAG, "onReceiveRGBAData: rgbBuffer: " + rgbBuffer + " width: " + width + " height: " + height);
                final Bitmap bitmap = Bitmap.createBitmap(width * 1, height * 1, Bitmap.Config.ARGB_8888);

                bitmap.copyPixelsFromBuffer(rgbBuffer);
                String name = "/mnt/sdcard/urtcscreen_" + System.currentTimeMillis() + "_local.jpg";
                File file = new File(name);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "screen shoot : " + name);
            }
        } );
    }
}

