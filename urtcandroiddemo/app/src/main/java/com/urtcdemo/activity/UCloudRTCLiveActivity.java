package com.urtcdemo.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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

import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
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
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCFirstFrameRendered;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCNotification;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCScreenShot;
import com.urtcdemo.R;
import com.urtcdemo.adpter.RemoteVideoAdapter;
import com.urtcdemo.service.UCloudRtcForeGroundService;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.EDensityUtils;
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

import core.renderer.SurfaceViewGroup;

import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkErrorCode.NET_ERR_CODE_OK;
import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO;
import static com.urtcdemo.utils.CommonUtils.BUCKET;
import static com.urtcdemo.utils.CommonUtils.REGION;

/**
 * @author ciel
 * @create 2020/7/2
 * @Describe
 */
public class UCloudRTCLiveActivity extends BaseActivity
        implements TextureView.SurfaceTextureListener, CameraDialog.CameraDialogParent {
    private static final String TAG = "UCloudRTCLiveActivity";
    private final String mBucket = "urtc-test";
    private final String mRegion = "cn-bj";
    private final int COL_SIZE_P = 3;
    private final int COL_SIZE_L = 6;

    private String mUserid = "test001";
    private String mRoomid = "urtc1";
    private String mRoomToken = "test token";
    private String mAppid = "";
    private String mPriAddr = "";

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
    private boolean mIsPriDeploy = false;
    private boolean mJoinChannelFlag = true;
    @CommonUtils.PubScribeMode
    private int mPublishMode;
    @CommonUtils.PubScribeMode
    private int mScribeMode;
    private int mVideoProfileSelect;
    private int localViewWidth_portrait;
    private int localViewHeight_portrait;
    private int localViewWidth_landscape;
    private int localViewHeight_landscape;
    private int screenWidth;
    private int screenHeight;
    private boolean mExtendCameraCapture;
    private int mExtendVideoFormat;
    private int mUVCCameraFormat;
    private int mURTCVideoFormat;
    private UCloudRtcSdkVideoProfile videoProfile;
    private boolean mVideoHwAcc = false;

    private UCloudRtcSdkEngine sdkEngine = null;
    private UCloudRtcSdkRoomType mClass;
    private UCloudRtcSdkStreamInfo mLocalStreamInfo;
    private UCloudRtcSdkAudioDevice defaultAudioDevice;
    // private List<UCloudRtcSdkStreamInfo> mSteamList;
    private List<String> mResolutionOption = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private UCloudRtcRenderView mLocalVideoView = null; //Surfaceview
    //private UCloudRtcSdkSurfaceVideoView mLocalVideoView = null; //UCloudRtcSdkSurfaceVideoView
    private UCloudRtcSdkSurfaceVideoView mMuteView = null;
    private UCloudRtcSdkMediaType mPublishMediaType;

    private GridLayoutManager gridLayoutManager;
    private RemoteVideoAdapter mVideoAdapter;
    private RecyclerView mRemoteGridView = null;
    private DrawerLayout mDrawer;
    private ViewGroup mDrawerContent;
    private FrameLayout mDrawerMenu;
    private LinearLayout mTitleBar;
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
    private ImageView mImgLocalMixSound;
    private TextView mTextLocalMixSound;
    private ImageView mImgRemoteMixSound;
    private TextView mTextRemoteMixSound;
    private ImageView mImgControlMixSound;
    private TextView mTextControlMixSound;
    private TextView mTextRoomId;
    private TextView mTextUserId;
    private ImageView mImgPreview;
    private TextView mTextPreview;
    private ImageView mImgJoin;
    private TextView mTextJoin;
    private ImageView mImgLeave;
    private TextView mTextLeave;
    private TextView mTextResolution;
    private VideoProfilePopupWindow mResolutionPopupWindow;
    //音量图片
    private ImageView mImgSoundVolume = null;
    private ImageView mImgMicSts = null;
    //UVCCamera
    private USBMonitor mUSBMonitor = null;
    private UVCCamera mUVCCamera = null;
    private final Object mSync = new Object();
    private boolean isActive, isPreview;
    private boolean mLeaveRoomFlag;
    private UCloudRtcSdkStreamInfo latestRemoteInfo;
    private UCloudRtcSdkStreamInfo mSwapStreamInfo;
    //外部摄像数据读取
    private ByteBuffer videoSourceData = null;
    private final Object extendByteBufferSync = new Object();
    private boolean mIsLocalMixingSound = false;
    private boolean mIsRemoteMixingSound = false;
    private boolean mIsPauseMixingSound = false;

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
//        EDensityUtils.setDensity(getApplication(),this);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        mVideoHwAcc = preferences.getBoolean(CommonUtils.VIDEO_HW_ACC, CommonUtils.HARDWARE_ACC);
        UCloudRtcSdkEnv.setVideoHardWareAcceleration(mVideoHwAcc);
        sdkEngine = UCloudRtcSdkEngine.createEngine(eventListener);
        sdkEngine.controlDetect(true);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;

        // 界面初始化
        setContentView(R.layout.activity_living);
        mVideoProfileSelect = preferences.getInt(CommonUtils.videoprofile, CommonUtils.videoprofilesel);
        videoProfile = UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect);
        videoProfile.updateParams();
        mRemoteGridView = findViewById(R.id.remoteGridView);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, COL_SIZE_L);
        } else {
            gridLayoutManager = new GridLayoutManager(this, COL_SIZE_P);
        }
        mRemoteGridView.setLayoutManager(gridLayoutManager);
        mVideoAdapter = new RemoteVideoAdapter(this);
//        mVideoAdapter.setRemoveRemoteStreamReceiver(mRemoveRemoteStreamReceiver);
        mRemoteGridView.setAdapter(mVideoAdapter);
        mLocalVideoView = findViewById(R.id.localvideoview);
        //Surfaceview 打开注释
        mLocalVideoView.init();
//        mLocalVideoView.setVisibility(View.INVISIBLE);
//        mLocalVideoView.setZOrderMediaOverlay(false);
//        mLocalVideoView.setMirror(true);
//        mLocalVideoView.setZOrderOnTop(true);
//        mLocalVideoView.setBackgroundColor(Color.TRANSPARENT);
        mDrawer = findViewById(R.id.drawer_layout);
        mDrawer.setScrimColor(0x00ffffff);
        mDrawerContent = findViewById(R.id.drawer_content);
        mDrawerMenu = findViewById(R.id.menu_drawer);
        timeShow = findViewById(R.id.timer);
        mDrawerContent.setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, 0);
        mTitleBar = findViewById(R.id.title_bar);
        mToolBar = findViewById(R.id.tool_bar);
        mImgBtnMore = findViewById(R.id.img_btn_more);
        mImgBtnSwitchCam = findViewById(R.id.img_btn_switch_camera);
        mImgBtnMuteMic = findViewById(R.id.img_btn_toggle_mic);
        mImgBtnMuteVideo = findViewById(R.id.img_btn_toggle_video);
        mImgBtnMuteSpeaker = findViewById(R.id.img_btn_speaker);
        mImgBtnMirror = findViewById(R.id.img_btn_mirror);
        mImgBtnEndCall = findViewById(R.id.img_btn_endcall);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.color_7F04A5EB));
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
        mImgLocalMixSound = findViewById(R.id.local_mix_pic);
        mTextLocalMixSound = findViewById(R.id.local_mix_text);
        mImgRemoteMixSound = findViewById(R.id.remote_mix_pic);
        mTextRemoteMixSound = findViewById(R.id.remote_mix_text);
        mImgControlMixSound = findViewById(R.id.control_mix_pic);
        mTextControlMixSound = findViewById(R.id.control_mix_text);
        mImgJoin = findViewById(R.id.join_room_pic);
        mTextJoin = findViewById(R.id.join_room_text);
        mImgLeave = findViewById(R.id.leave_room_pic);
        mTextLeave = findViewById(R.id.leave_room_text);

        // 用户配置参数获取
        mUserid = getIntent().getStringExtra("user_id");
        mRoomid = getIntent().getStringExtra("room_id");
        mRoomToken = getIntent().getStringExtra("token");
        mAppid = getIntent().getStringExtra("app_id");
        mJoinChannelFlag = getIntent().getBooleanExtra("join_channel", true);

        isScreenCaptureSupport = UCloudRtcSdkEnv.isSupportScreenCapture();
        mCameraEnable = preferences.getBoolean(CommonUtils.CAMERA_ENABLE, CommonUtils.CAMERA_ON);
        mMicEnable = preferences.getBoolean(CommonUtils.MIC_ENABLE, CommonUtils.MIC_ON);
        mScreenEnable = preferences.getBoolean(CommonUtils.SCREEN_ENABLE, CommonUtils.SCREEN_OFF);
        int classType = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL.ordinal());
        mClass = UCloudRtcSdkRoomType.valueOf(classType);
        mPublishMode = preferences.getInt(CommonUtils.PUBLISH_MODE, CommonUtils.AUTO_MODE);
        mScribeMode = preferences.getInt(CommonUtils.SUBSCRIBE_MODE, CommonUtils.AUTO_MODE);

        mIsPriDeploy = preferences.getBoolean(CommonUtils.PRIVATISATION_MODE, false);
        UCloudRtcSdkEnv.setPrivateDeploy(mIsPriDeploy);
        mPriAddr = preferences.getString(CommonUtils.PRIVATISATION_ADDRESS, "");
        if (mIsPriDeploy) {
            UCloudRtcSdkEnv.setPrivateDeployRoomURL(mPriAddr);
        }

        mExtendCameraCapture = preferences.getBoolean(CommonUtils.CAMERA_CAPTURE_MODE, false);
        mExtendVideoFormat = preferences.getInt(CommonUtils.EXTEND_CAMERA_VIDEO_FORMAT, CommonUtils.i420_format);
        updateVideoFormat(mExtendVideoFormat);
        // mSteamList = new ArrayList<>();

        //房间号
        mTextRoomId = findViewById(R.id.roomid_text);
        mTextRoomId.setText("房间号:" + mRoomid);
        mMirror = UCloudRtcSdkEnv.isFrontCameraMirror();
        mImgBtnMirror.setImageResource(mMirror ? R.mipmap.mirror_on :
                R.mipmap.mirror);
        //分辨率选择菜单
        String[] resolutions = getResources().getStringArray(R.array.videoResolutions);
        mResolutionOption.addAll(Arrays.asList(resolutions));

        //用户ID
        mTextUserId = findViewById(R.id.userid_text);
        mTextUserId.setText("用户ID:" + mUserid);

        Log.d(TAG, " Camera enable is: " + mCameraEnable + " Mic enable is: " + mMicEnable + " ScreenShare enable is: " + mScreenEnable);
        if (!mScreenEnable && !mCameraEnable && mMicEnable) {
            sdkEngine.setAudioOnlyMode(true);
        } else {
            sdkEngine.setAudioOnlyMode(false);
        }
        sdkEngine.configLocalCameraPublish(mCameraEnable);
        sdkEngine.configLocalAudioPublish(mMicEnable);
        if (isScreenCaptureSupport) {
            sdkEngine.configLocalScreenPublish(mScreenEnable);
            if (mScreenEnable && Build.VERSION.SDK_INT >= 28) {
                UCloudRtcSdkEngine.regScreenCaptureNotification(mScreenCaptureNotification);
            }
        } else {
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
        sdkEngine.setCustomizedVideoParam(UCloudRtcSdkVideoProfile.matchValue(0).extendParams(15,1920,1080));
//        sdkEngine.setVideoProfile(UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect));
//        sdkEngine.setScreenProfile(UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_1280_720);

        synchronized (extendByteBufferSync) {
            videoSourceData = ByteBuffer.allocate(1920 * 1080 * 4);
            videoSourceData.clear();
        }
        //分辨率菜单显示
        mTextResolution.setText(mResolutionOption.get(mVideoProfileSelect));
        mAdapter = new ArrayAdapter<String>(this, R.layout.videoprofile_item, mResolutionOption);

        mResolutionPopupWindow = new VideoProfilePopupWindow(this);
        mResolutionPopupWindow.setOnSpinnerItemClickListener(mOnResulutionOptionClickListener);
        if (mExtendCameraCapture) {
            //扩展摄像头方式
            UCloudRtcSdkEnv.setCaptureMode(
                    UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_EXTEND);
            try {
                mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
                final List<DeviceFilter> filters = DeviceFilter.getDeviceFilters(this, R.xml.device_filter);
                mUSBMonitor.setDeviceFilter(filters);
            } catch (IllegalStateException e) {
                if (sdkEngine != null) {
                    UCloudRtcSdkEngine.destroy();
                    sdkEngine = null;
                }
                finish();
            }
            UCloudRtcSdkEngine.onRGBCaptureResult(mUCloudRTCDataProvider);
            mTextResolution.setVisibility(View.GONE);
            mImgBtnSwitchCam.setVisibility(View.GONE);
        } else {
            UCloudRtcSdkEnv.setCaptureMode(
                    UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
        }

        if (mJoinChannelFlag) {
            if (mPublishMode == CommonUtils.AUTO_MODE) {
                setIconStats(userStatus.PUBLISH_VIDEO_AND_PREVIEW);
            } else {
                setIconStats(userStatus.JOIN_ROOM_NOT_PREVIEW);
            }
        } else {
            setIconStats(userStatus.PREVIEW_NOT_JOIN_ROOM);
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
//                update(UCloudRtcSdkMixProfile.MIX_TYPE_UPDATE);
//                sdkEngine.queryMix();
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
                    if (mCameraEnable || mMicEnable) {
                        if (!mVideoIsPublished) {
                            results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, mCameraEnable, mMicEnable).getErrorCode());
                        }
                    } else {
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
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, errorMessage.toString());
                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, "发布");
                    }
                } else {
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
                            results.add(sdkEngine.publish(UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN, true, false).getErrorCode());
                        } else {
                            errorMessage.append("设备不支持屏幕捕捉\n");
                            //results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, true, true).getErrorCode());
                        }
                    } else {
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
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, errorMessage.toString());
                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, "发布");
                    }
                } else {
                    sdkEngine.unPublish(UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN);
                }
            }
        });

        //本地预览控制
        mImgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreview(!mIsPreview);
                mIsPreview = !mIsPreview;
                if (mIsPreview) {
                    mTextPreview.setText(R.string.stop_preview);
                    mLocalVideoView.setVisibility(View.VISIBLE);
                } else {
                    mTextPreview.setText(R.string.start_preview);
                    mLocalVideoView.setVisibility(View.INVISIBLE);
                }
            }
        });

        mImgLocalMixSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMixingSound(false);
            }
        });

        mImgRemoteMixSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMixingSound(true);
            }
        });

        mImgControlMixSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControlMixingSound();
            }
        });

        //加入房间
        mImgJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UCloudRtcSdkAuthInfo info = new UCloudRtcSdkAuthInfo();
                info.setAppId(mAppid);
                info.setToken(mRoomToken);
                info.setRoomId(mRoomid);
                info.setUId(mUserid);
                Log.d(TAG, " roomtoken = " + mRoomToken + "appid : " + mAppid + " userid :" + mUserid);
                // 加入房间
                if (sdkEngine.joinChannel(info) == UCloudRtcSdkErrorCode.NET_ERR_SECKEY_NULL
                        || mAppid.length() == 0) {
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, "加入房间失败，AppKey或AppId没有设置");
                    endCall();
                }
            }
        });

        //离开房间
        mImgLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeaveRoomFlag = true;
                if (sdkEngine != null) {
                    sdkEngine.leaveChannel().ordinal();
                }
            }
        });

        initRecordManager();

        if (mJoinChannelFlag) {
            UCloudRtcSdkAuthInfo info = new UCloudRtcSdkAuthInfo();
            info.setAppId(mAppid);
            info.setToken(mRoomToken);
            info.setRoomId(mRoomid);
            info.setUId(mUserid);
            Log.d(TAG, " roomtoken = " + mRoomToken + "appid : " + mAppid + " userid :" + mUserid);
//            sdkEngine.setCustomizedVideoParam(UCloudRtcSdkVideoProfile.matchValue(UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_480_360.ordinal())
//                    .extendParams(15,1280,720));
//            sdkEngine.cropPushResolution(640,280);
            // 加入房间
            if (sdkEngine.joinChannel(info) == UCloudRtcSdkErrorCode.NET_ERR_SECKEY_NULL
                    || mAppid.length() == 0) {
                ToastUtils.shortShow(UCloudRTCLiveActivity.this, "加入房间失败，AppKey或AppId没有设置");
                endCall();
            }
        } else {
            setPreview(true);
            mIsPreview = true;
            mTextPreview.setText(R.string.stop_preview);
//            mLocalVideoView.setVisibility(View.VISIBLE);
        }
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        sdkEngine.cropPushResolution(640,360);
    }

    @Override
    protected void onStart() {
        super.onStart();
        synchronized (mSync) {
            if (mUSBMonitor != null) {
                mUSBMonitor.register();
            }
        }
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
        if (sdkEngine != null) {
//            sdkEngine.controlAudio(false);
            if (!mExtendCameraCapture) {
//                sdkEngine.controlLocalVideo(false);
            }
        }
        if (mVideoIsPublished || mScreenIsPublished) {
            if (!mLeaveRoomFlag) {
                Intent service = new Intent(this, UCloudRtcForeGroundService.class);
                startService(service);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Intent service = new Intent(this, UCloudRtcForeGroundService.class);
        stopService(service);
        if (sdkEngine != null) {
            if (!mExtendCameraCapture) {
//            sdkEngine.controlLocalVideo(true);
//          sdkEngine.controlAudio(true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "activity destory");
        super.onDestroy();
        Intent service = new Intent(this, UCloudRtcForeGroundService.class);
        stopService(service);
        mUCloudRTCDataProvider = null;
        System.gc();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent:keyCode--" + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            endCall();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private UCloudRtcSdkEventListener eventListener = new UCloudRtcSdkEventListener() {
        @Override
        public void onPeerReconnected(int type, UCloudRtcSdkStreamInfo info) {

        }

        @Override
        public void onFirstLocalVideoFrame() {

        }

        @Override
        public void onStartLocalRenderFailed(String reason) {

        }

        @Override
        public void onServerDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onServerDisconnect: ");
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 服务器已断开");
                    stopTimeShow();
                    onMediaServerDisconnect();
                }
            });
        }

        @Override
        public void onJoinRoomResult(int code, String msg, String roomid) {
            // 加入房间回调结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) { // 成功
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 加入房间成功");
                        startTimeShow();
                        DisplayMetrics metric = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metric);
                        int width = metric.widthPixels;  // 屏幕宽度（像素）
                        int height = metric.heightPixels;  // 屏幕高度（像素）
                        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5，小米4的是3.0）
                        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240,小米4的是480）
                        Log.d(TAG, "metric.widthPixels: " + metric.widthPixels + "metric.heightPixels: " + metric.heightPixels +
                                "metric.density: " + metric.density + " metric.densityDpi: " + metric.densityDpi);
                        if (mIsPreview) {
                            setIconStats(userStatus.PREVIEW_JOIN_ROOM_NOT_PUBLISH);
                        } else {
                            setIconStats(userStatus.JOIN_ROOM_NOT_PREVIEW);
                        }
                    } else { // 失败
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 加入房间失败 " +
                                code + " errmsg " + msg);
                        Intent intent = new Intent(UCloudRTCLiveActivity.this, ConnectActivity.class);
                        onMediaServerDisconnect();
                        startActivity(intent);
                        finish();
                    }

                }
            });
        }

        @Override
        public void onLeaveRoomResult(int code, String msg, String roomid) {
            // 离开房间回调结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 离开房间 " +
                            code + " errmsg " + msg);
                    setIconStats(userStatus.INIT);
//                    mRemoteGridView.setVisibility(View.INVISIBLE);
//                    mLocalVideoView.setVisibility(View.INVISIBLE);
                    mSwapStreamInfo = null;
//                    clearGridItem();
//                    setPreview(false);
//                    mIsPreview = false;
//                    System.gc();
                }
            });
        }

        @Override
        public void onRejoiningRoom(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "rejoining room");
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 服务器重连中…… ");
                    stopTimeShow();
                }
            });
        }

        @Override
        public void onRejoinRoomResult(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, "服务器重连成功");
                    startTimeShow();
                }
            });
        }

        @Override
        public void onLocalPublish(int code, String msg, UCloudRtcSdkStreamInfo info) {
            // 发布本地流回调结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        int mediatype = info.getMediaType().ordinal(); // 获取媒体类型（音视频流或桌面流）
                        mPublishMediaType = UCloudRtcSdkMediaType.matchValue(mediatype);
                        if (mediatype == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal()) { // 音视频流
                            mImgManualPubVideo.setImageResource(R.mipmap.stop); // 修改界面图标
                            mTextManualPubVideo.setText(R.string.pub_cancel_video); // 修改界面文字
                            mVideoIsPublished = true;
                            if (!sdkEngine.isAudioOnlyMode()) {  // 非单音频流
                                // UCloudRtcSdkSurfaceVideoView打开
                                //mLocalVideoView.init(false);
                                // Surfaceview打开

                                // 回显view布局设置

//                                mLocalVideoView.setVisibility(View.VISIBLE);
                                // 获取当前横竖屏模式
                                if (UCloudRTCLiveActivity.this.getResources().getConfiguration().orientation
                                        == Configuration.ORIENTATION_LANDSCAPE) {
                                    Log.i("info", "landscape"); // 横屏
                                    localViewWidth_landscape = mLocalVideoView.getMeasuredWidth();
                                    localViewHeight_landscape = mLocalVideoView.getMeasuredHeight();
                                    localViewWidth_portrait = screenWidth;
                                    localViewHeight_portrait = screenHeight - mToolBar.getHeight() - mTitleBar.getHeight();
                                } else if (UCloudRTCLiveActivity.this.getResources().getConfiguration().orientation
                                        == Configuration.ORIENTATION_PORTRAIT) {
                                    Log.i("info", "portrait"); // 竖屏
                                    localViewWidth_portrait = mLocalVideoView.getMeasuredWidth();
                                    localViewHeight_portrait = mLocalVideoView.getMeasuredHeight();
                                    localViewWidth_landscape = screenHeight;
                                    localViewHeight_landscape = screenWidth - mTitleBar.getHeight() - mToolBar.getHeight();
                                }
                                if (mSwapStreamInfo != null) {
                                    String mkey = mSwapStreamInfo.getUId() + mSwapStreamInfo.getMediaType().toString();
                                    sdkEngine.stopRemoteView(mSwapStreamInfo, mLocalVideoView); // 大窗停止渲染远端视频流

                                    // 创建小窗，把原大窗订阅转移到小窗
                                    UCloudRtcSdkSurfaceVideoView videoView = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
                                    UCloudRtcRenderView surfaceViewRenderer = new UCloudRtcRenderView(getApplicationContext());
                                    videoView.init(false, new int[]{R.mipmap.video_open, R.mipmap.loudspeaker, R.mipmap.video_close, R.mipmap.loudspeaker_disable, R.drawable.publish_layer}, mOnRemoteOpTrigger, new int[]{R.id.remote_video, R.id.remote_audio}, surfaceViewRenderer);
                                    videoView.setTag(mSwapStreamInfo);
                                    videoView.setId(R.id.video_view);
                                    URTCVideoViewInfo vinfo = new URTCVideoViewInfo();
                                    vinfo.setRenderview(videoView);
                                    vinfo.setUid(mSwapStreamInfo.getUId());
                                    vinfo.setMediaType(mSwapStreamInfo.getMediaType());
                                    vinfo.setEnableVideo(mSwapStreamInfo.isHasVideo());
                                    vinfo.setEnableAudio(mSwapStreamInfo.isHasAudio());
                                    vinfo.setKey(mkey);
                                    //设置交换
                                    videoView.setOnClickListener(mSwapRemoteLocalListener);
                                    vinfo.setStreamInfo(mSwapStreamInfo);
                                    mVideoAdapter.addStreamView(mkey, vinfo, mSwapStreamInfo);
                                    sdkEngine.startRemoteView(mSwapStreamInfo, videoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null); // 渲染订阅流到小窗
                                    mRemoteGridView.setVisibility(View.VISIBLE);
                                }

                                if (!mIsPreview) {
                                    sdkEngine.renderLocalView(info,
                                            mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                                    mIsPreview = true;
                                }
                                // 状态记录
                                mLocalStreamInfo = info;
                                mSwapStreamInfo = info;
                                mLocalVideoView.setTag(mLocalStreamInfo);
                                mLocalVideoView.setOnClickListener(mToggleScreenOnClickListener);
                                setIconStats(userStatus.PUBLISH_VIDEO_AND_PREVIEW);

                            }
                        } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN.ordinal()) { // 屏幕流
                            // 状态记录及界面更新
                            mScreenIsPublished = true;
                            mImgManualPubScreen.setImageResource(R.mipmap.stop);
                            mTextManualPubScreen.setText(R.string.pub_cancel_screen);
                            setIconStats(userStatus.PUBLISH_SCREEN_AND_PREVIEW);
                            if (mScreenEnable) { // 屏幕流一般无需渲染
                                //sdkEngine.startPreview(info.getMediaType(), mLocalVideoView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL,null);
                            }
                        }
                        // 界面图标状态设置
                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this,
                                "发布视频失败 " + code + " errmsg " + msg);
                    }

                }
            });
        }

        @Override
        public void onLocalUnPublish(int code, String msg, UCloudRtcSdkStreamInfo info) {
            // 取消发布回调结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        if (info.getMediaType() == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO) { // 音视频流
                            // 界面更新
                            if (mPublishMode == CommonUtils.AUTO_MODE) {
                                mImgManualPubVideo.setVisibility(View.GONE);
                                mTextManualPubVideo.setVisibility(View.GONE);
                            } else {
                                mImgManualPubVideo.setImageResource(R.mipmap.publish);
                                mTextManualPubVideo.setText(R.string.pub_video);
                            }
                            if (mLocalVideoView != null) {
//                                localrenderview.refresh();
                            }
                            mVideoIsPublished = false;
                            if (mSwapStreamInfo != null) {
                                if (mSwapStreamInfo.getUId().equals(mLocalStreamInfo.getUId()) && mSwapStreamInfo.getMediaType().toString().equals(mLocalStreamInfo.getMediaType().toString())) {
                                    if (mVideoAdapter.getItemCount() != 0) {
                                        // 渲染远端流到大窗
                                        UCloudRtcSdkStreamInfo firstInfo = mVideoAdapter.getStreamInfo(0);
                                        sdkEngine.startRemoteView(firstInfo, mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                                        mSwapStreamInfo = firstInfo;
                                        mVideoAdapter.removeStreamView(firstInfo.getUId() + firstInfo.getMediaType().toString());
                                    } else {
                                        mLocalVideoView.setVisibility(View.INVISIBLE);
                                        mSwapStreamInfo = null;
                                    }
                                } else {
                                    mVideoAdapter.removeStreamView(mLocalStreamInfo.getUId() + mLocalStreamInfo.getMediaType().toString()); // 渲染流移除
                                }
                            } else {
                                mLocalVideoView.setVisibility(View.INVISIBLE);
                                mTextPreview.setText(R.string.start_preview);
                            }

                            if (mIsLocalMixingSound) {
                                toggleMixingSound(false);
                            }
                            if (mIsRemoteMixingSound) {
                                toggleMixingSound(true);
                            }
                            setPreview(false);
                            mIsPreview = false;

                        } else if (info.getMediaType() == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) { //屏幕流
                            mScreenIsPublished = false;
                            if (mPublishMode == CommonUtils.AUTO_MODE) {
                                mImgManualPubScreen.setVisibility(View.GONE);
                                mTextManualPubScreen.setVisibility(View.GONE);
                            } else {
                                mImgManualPubScreen.setImageResource(R.mipmap.publish_screen);
                                mTextManualPubScreen.setText(R.string.pub_screen);
                            }
                            if (mScreenEnable && !mCameraEnable && !mMicEnable) {
//                                if (localrenderview != null) {
//                                    localrenderview.refresh();
//                                }
                            }
                            if (mIsLocalMixingSound) {
                                toggleMixingSound(false);
                            }
                        }
                        if (!mScreenIsPublished && !mVideoIsPublished) {
                            if (mIsPreview) {
                                setIconStats(userStatus.PREVIEW_JOIN_ROOM_NOT_PUBLISH);
                            } else {
                                if (mVideoAdapter.getItemCount() == 0 && mSwapStreamInfo == null) {
                                    mRemoteGridView.setVisibility(View.INVISIBLE);
                                    setIconStats(userStatus.JOIN_ROOM_NOT_PREVIEW);
                                } else {
                                    setIconStats(userStatus.JOIN_ROOM_AND_SUBSCRIBE);
                                }
                            }
                        }
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, "取消发布成功");
                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, "取消发布失败 "
                                + code + " errmsg " + msg);
                    }
                }
            });
        }

        @Override
        public void onLocalUnPublishOnly(int code, String msg, UCloudRtcSdkStreamInfo info) {
            // 取消发布回调结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        if (info.getMediaType() == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO) { // 音视频流
                            // 界面更新
                            if (mPublishMode == CommonUtils.AUTO_MODE) {
                                mImgManualPubVideo.setVisibility(View.GONE);
                                mTextManualPubVideo.setVisibility(View.GONE);
                            } else {
                                mImgManualPubVideo.setImageResource(R.mipmap.publish);
                                mTextManualPubVideo.setText(R.string.pub_video);
                            }
                            if (mLocalVideoView != null) {
//                                localrenderview.refresh();
                            }
                            mVideoIsPublished = false;
                            if (mIsLocalMixingSound) {
                                toggleMixingSound(false);
                            }
                            if (mIsRemoteMixingSound) {
                                toggleMixingSound(true);
                            }
                        } else if (info.getMediaType() == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) { //屏幕流
                            mScreenIsPublished = false;
                            if (mPublishMode == CommonUtils.AUTO_MODE) {
                                mImgManualPubScreen.setVisibility(View.GONE);
                                mTextManualPubScreen.setVisibility(View.GONE);
                            } else {
                                mImgManualPubScreen.setImageResource(R.mipmap.publish_screen);
                                mTextManualPubScreen.setText(R.string.pub_screen);
                            }
                            if (mScreenEnable && !mCameraEnable && !mMicEnable) {
//                                if (localrenderview != null) {
//                                    localrenderview.refresh();
//                                }
                            }
                            if (mIsLocalMixingSound) {
                                toggleMixingSound(false);
                            }
                        }
                        if (!mScreenIsPublished && !mVideoIsPublished) {
//                            setIconStats(false);
                            setIconStats(userStatus.PREVIEW_JOIN_ROOM_NOT_PUBLISH);
                        }
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, "取消发布成功");
                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, "取消发布失败 "
                                + code + " errmsg " + msg);
                    }
                }
            });
        }

        @Override
        public void onRemoteUserJoin(String uid) {
            // 远端用户加入房间
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 用户 "
                            + uid + " 加入房间 ");
                }
            });
        }

        @Override
        public void onRemoteUserLeave(String uid, int reason) {
            // 远端用户离开房间
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "remote user " + uid + "leave ,reason: " + reason);
                    //onUserLeave(uid);
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 用户 " +
                            uid + " 离开房间，离开原因： " + reason);
                }
            });
        }

        @Override
        public void onRemotePublish(UCloudRtcSdkStreamInfo info) {
            // 远端用户发布流
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //特殊情况下，譬如客户端在断网情况下离开房间，服务端可能还持有流，并没有超时，客户端就会收到自己的userid,
                    // 如果客户端是固定userid就可以过滤掉，如果不是，等待服务端超时也会删除流
                    Log.d(TAG, "onRemotePublish: " + info.getUId() + " me : " + mUserid);
                    if (!mUserid.equals(info.getUId())) {
                        // mSteamList.add(info);
                        if (!sdkEngine.isAutoSubscribe()) { // 非自动订阅模式情况下调用订阅接口
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
            // 远端用户取消发布
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, " onRemoteUnPublish " + info.getMediaType() + " " + info.getUId());
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 用户 " +
                            info.getUId() + " 取消媒体流 " + info.getMediaType()); // 界面提示信息
                    String mkey = info.getUId() + info.getMediaType().toString();
                    if (mSwapStreamInfo != null && mSwapStreamInfo.getUId().equals(info.getUId()) && mSwapStreamInfo.getMediaType().toString().equals(info.getMediaType().toString())) {
                        sdkEngine.stopRemoteView(mSwapStreamInfo); // 停止渲染远端视频流
                        if (mVideoAdapter.getItemCount() != 0) {
                            int localIndex = mVideoAdapter.getPositionByKey(mUserid + mPublishMediaType.toString());
                            if (localIndex >= 0) {
                                Log.d(TAG, " onRemoteUnPublish localIndex " + localIndex);
                                mkey = mUserid + mPublishMediaType.toString();
                                sdkEngine.stopPreview(mPublishMediaType);
                                sdkEngine.renderLocalView(mLocalStreamInfo, mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                                mSwapStreamInfo = mLocalStreamInfo;
                            }
                        } else {
                            mSwapStreamInfo = null;
                            mLocalVideoView.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        sdkEngine.stopRemoteView(info);
                    }
                    if (mVideoAdapter != null) {
                        mVideoAdapter.removeStreamView(mkey);
                    }
                    if (mVideoAdapter.getItemCount() < 1) {
                        // 隐藏远端回显
                        mRemoteGridView.setVisibility(View.INVISIBLE);
                    }
                    //mSpinnerPopupWindowScribe.removeStreamInfoByUid(info.getUId());
                    //refreshStreamInfoText();
                }
            });
        }

        @Override
        public void onSubscribeResult(int code, String msg, UCloudRtcSdkStreamInfo info) {
            // 订阅结果回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) { // 订阅成功
                        URTCVideoViewInfo vinfo = new URTCVideoViewInfo();
                        UCloudRtcSdkSurfaceVideoView videoView = null;
                        // UCloudRtcSdkSurfaceVideoView videoViewCallBack = null; // 用于外部扩展输出

                        //UCloudRtcRenderView videoView = null;
                        Log.d(TAG, " subscribe info: " + info);
                        latestRemoteInfo = info;
                        if (info.isHasVideo()) { // 订阅流是否包含视频
//                            UCloudRtcSdkSurfaceVideoView 定义的viewgroup,URTCVideoViewInfo
                            videoView = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
                            UCloudRtcRenderView surfaceViewRenderer = new UCloudRtcRenderView(getApplicationContext());
                            videoView.init(false, new int[]{R.mipmap.video_open, R.mipmap.loudspeaker, R.mipmap.video_close, R.mipmap.loudspeaker_disable, R.drawable.publish_layer}, mOnRemoteOpTrigger, new int[]{R.id.remote_video, R.id.remote_audio}, surfaceViewRenderer);
                            // UCloudRtcRenderView
                            //videoView = new UCloudRtcRenderView(getApplicationContext());// 初始化渲染界面
                            //videoView.init();
                            videoView.setTag(info);
                            videoView.setId(R.id.video_view);
                            //外部扩展输出，和默认输出二选一
                            //videoViewCallBack = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
                            //videoViewCallBack.setFrameCallBack(mUCloudRTCDataReceiver);
                            //videoViewCallBack.init(false);
                            //远端截图
                            //videoView.setOnClickListener(mScreenShotOnClickListener);
                            //设置交换
                            videoView.setOnClickListener(mSwapRemoteLocalListener);
                            //测试远端大屏
//                            mLocalVideoView.setBackgroundColor(Color.TRANSPARENT);
//                            mLocalVideoView.setVisibility(View.VISIBLE);
//                            sdkEngine.startRemoteView(info, mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null); // 渲染订阅流
                        }
                        vinfo.setRenderview(videoView);
                        vinfo.setUid(info.getUId());
                        vinfo.setMediaType(info.getMediaType());
                        vinfo.setEnableVideo(info.isHasVideo());
                        vinfo.setEnableAudio(info.isHasAudio());
                        String mkey = info.getUId() + info.getMediaType().toString();
                        vinfo.setKey(mkey);

                        if (videoView != null) {
                            if (!mVideoIsPublished && mSwapStreamInfo == null && !mIsPreview) {
                                sdkEngine.startRemoteView(info, mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null); // 大窗渲染
                                mLocalVideoView.setBackgroundColor(Color.TRANSPARENT);
                                mLocalVideoView.setVisibility(View.VISIBLE);
                                mSwapStreamInfo = info;
                            } else {
                                if (mVideoAdapter != null) {
                                    vinfo.setStreamInfo(info);
                                    mVideoAdapter.addStreamView(mkey, vinfo, info);
                                    sdkEngine.startRemoteView(info, videoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null); // 渲染订阅流
                                }
                            }
                            if (mVideoAdapter.getItemCount() == 0) {
                                // 隐藏远端回显
                                mRemoteGridView.setVisibility(View.INVISIBLE);
                            } else {
                                mRemoteGridView.setVisibility(View.VISIBLE);
                            }
                        }
                        setIconStats(userStatus.JOIN_ROOM_AND_SUBSCRIBE);
                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 订阅用户  " +
                                info.getUId() + " 流 " + info.getMediaType() + " 失败 " +
                                " code " + code + " msg " + msg);
                    }
                }
            });
        }

        @Override
        public void onUnSubscribeResult(int code, String msg, UCloudRtcSdkStreamInfo info) {
            // 取消订阅结果回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 取消订阅用户 " +
                            info.getUId() + " 类型 " + info.getMediaType());
                    if (mVideoAdapter != null) {
                        mVideoAdapter.removeStreamView(info.getUId() + info.getMediaType().toString()); // 远端渲染流移除
                    }
                    //取消订阅又变成可订阅
                    //mSpinnerPopupWindowScribe.addStreamInfo(info, true);
                }
            });
        }

        @Override
        public void onLocalStreamMuteRsp(int code, String msg, UCloudRtcSdkMediaType mediatype, UCloudRtcSdkTrackType tracktype, boolean mute) {
            // 静音本地流回调
            Log.d(TAG, " code " + code + " mediatype " + mediatype + " ttype " + tracktype + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) { // mute成功，更新界面
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
            // 静音远端流回调
            Log.d(TAG, " code " + code + " uid " + uid + " mediatype " + mediatype + " ttype " + tracktype + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {// mute成功，更新界面
                        String mkey = uid + mediatype.toString();
                        Log.d(TAG, " onRemoteStreamMuteRsp " + mkey + " " + mVideoAdapter);
                        if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_AUDIO) {
                            mRemoteAudioMute = mute;
                            if (mMuteView != null) {
                                mMuteView.refreshRemoteAudio(mute);
                            }
                        } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                            mRemoteVideoMute = mute;
                            if (mMuteView != null) {
                                mMuteView.refreshRemoteVideo(mute);
                            }
                        }

                    } else {
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, "mute " + mediatype + "failed with code: " + code);
                    }
                }
            });
        }

        @Override
        public void onRemoteTrackNotify(String uid, UCloudRtcSdkMediaType mediatype, UCloudRtcSdkTrackType tracktype, boolean mute) {
            // 远端流状态改变通知
            Log.d(TAG, " uid " + uid + " mediatype " + mediatype + " ttype " + tracktype + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 更新界面和界面提醒
                    if (mediatype == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO) {
                        String cmd = mute ? "关闭" : "打开";
                        if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_AUDIO) {
                            ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 用户 " +
                                    uid + cmd + " 麦克风");
                        } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                            ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 用户 " +
                                    uid + cmd + " 摄像头");
                        }

                    } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                        String cmd = mute ? "关闭" : "打开";
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 用户 " +
                                uid + cmd + " 桌面流");
                    }
                }
            });
        }

        @Override
        public void onSendRTCStatus(UCloudRtcSdkStats rtstats) {

        }

        @Override
        public void onRemoteRTCStatus(UCloudRtcSdkStats rtstats) {

        }

        @Override
        public void onLocalAudioLevel(int volume) {
            // 本端声音调整回调
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
            // 远端声音调整回调
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
            // 被踢出房间通知
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.longShow(UCloudRTCLiveActivity.this, " 被踢出会议 code " +
                            code);
                    Log.d(TAG, " user kickoff reason " + code);
                    Intent intent = new Intent(UCloudRTCLiveActivity.this, ConnectActivity.class);
                    releaseExtendCamera();
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
                        ToastUtils.shortShow(UCloudRTCLiveActivity.this, "sdp swap failed");
                    }
                }
            });
        }

        @Override
        public void onRecordStop(int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.longShow(UCloudRTCLiveActivity.this, "录制结束: " + (code == NET_ERR_CODE_OK.ordinal() ? "成功" : "失败: " + code));
                }
            });
        }

        @Override
        public void onQueryMix(int code, String msg, int type, String mixId, String fileName) {
            Log.d(TAG, "onQueryMix: " + code + " msg: " + msg + " type: " + type);
        }

        @Override
        public void onRecordStatusNotify(UCloudRtcSdkMediaServiceStatus status, int code, String msg, String userId, String roomId, String mixId, String fileName) {
            // 录制状态通知
            Log.d(TAG, "onRecordStatusNotify " + status + " code: " + code + " msg: " + msg + " userid " + userId + " roomid: " + roomId + " mixId: " + mixId + "fileName: " + fileName);
            if (status == UCloudRtcSdkMediaServiceStatus.RECORD_STATUS_START_REQUEST_SEND) { // 录制请求已送出
                Log.d(TAG, "开始录制请求已发送: ");
            } else if (status == UCloudRtcSdkMediaServiceStatus.RECORD_STATUS_START) { // 录制已经开始
                String videoPath = "http://" + mBucket + "." + mRegion + ".ufileos.com/" + fileName; // 录制观看地址
                Log.d(TAG, "remote record path: " + videoPath + ".mp4");
                // 界面提醒和更新
                ToastUtils.longShow(UCloudRTCLiveActivity.this, "观看地址: " + videoPath);
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
            } else if (status == UCloudRtcSdkMediaServiceStatus.STATUS_UPDATE_REQUEST_SEND) {
                Log.d(TAG, "update 更新参数请求已发送: ");
            } else if (status == UCloudRtcSdkMediaServiceStatus.STATUS_UPDATE_ADD_STREAM_SUCCESS) {
                Log.d(TAG, "update 加流成功: ");
            } else {
                ToastUtils.longShow(UCloudRTCLiveActivity.this, "录制异常: 原因：" + code);
            }
        }

        @Override
        public void onRelayStatusNotify(UCloudRtcSdkMediaServiceStatus status, int code, String msg, String userId, String roomId, String mixId, String[] pushUrls) {
            // 转推状态通知
            Log.d(TAG, "onRelayStatusNotify " + status + " code: " + code + " msg: " + msg + " userid " + userId + " roomid: " + roomId + " mixId: " + mixId);
            if (pushUrls != null) {
                for (int i = 0; i < pushUrls.length; i++) {
                    Log.d(TAG, "onRelayStatusNotify: pushUrl " + pushUrls[i]); // 转推地址
                }
            }
            if (status == UCloudRtcSdkMediaServiceStatus.RELAY_STATUS_START_REQUEST_SEND) {
                Log.d(TAG, "开始转推请求已发送: ");
            } else if (status == UCloudRtcSdkMediaServiceStatus.RELAY_STATUS_START) { // 开始转推
                // ulive cdn watch address: http://rtchls.ugslb.com/rtclive/roomid.flv
                // 界面更新和提醒
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
                }
            } else if (status == UCloudRtcSdkMediaServiceStatus.STATUS_UPDATE_REQUEST_SEND) {
                Log.d(TAG, "update 更新参数请求已发送: ");
            } else if (status == UCloudRtcSdkMediaServiceStatus.STATUS_UPDATE_ADD_STREAM_SUCCESS) {
                Log.d(TAG, "update 加流成功: ");
            } else {
                ToastUtils.longShow(UCloudRTCLiveActivity.this, "转推异常: 原因：" + code);
            }
        }

        @Override
        public void onAddStreams(int code, String msg) {
            // 加流回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onAddStreams: " + code + msg);
                }
            });
        }

        @Override
        public void onDelStreams(int code, String msg) {
            // 减流回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onDelStreams: " + code + msg);
                }
            });
        }

        @Override
        public void onLogOffUsers(int code, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onLogOffUsers: " + code + " msg: " + msg);
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
                    Log.d(TAG, "onLogOffUsers: " + cmdType + " userId: " + userId);
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
            // 播放声音设备切换
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
        public void onNetWorkQuality(String userId, UCloudRtcSdkStreamType streamType, UCloudRtcSdkMediaType mediaType, UCloudRtcSdkNetWorkQuality quality) {
            // 网络质量通知
            Log.d(TAG, "onNetWorkQuality: userid: " + userId + "streamType: " + streamType + "mediatype : " + mediaType + " quality: " + quality);
        }

        @Override
        public void onAudioFileFinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onAudioFileFinish");

                    if (mIsLocalMixingSound) {
                        // 本地混音中
                        mImgLocalMixSound.setImageResource(R.mipmap.local_mix_sound);
                        mTextLocalMixSound.setText(R.string.start_local_mix_sound);
                        if (mVideoIsPublished) {
                            mImgRemoteMixSound.setVisibility(View.VISIBLE);
                            mTextRemoteMixSound.setVisibility(View.VISIBLE);
                        }
                        mIsLocalMixingSound = false;
                    } else if (mIsRemoteMixingSound) {
                        // 远端混音中
                        mImgLocalMixSound.setVisibility(View.VISIBLE);
                        mTextLocalMixSound.setVisibility(View.VISIBLE);
                        mImgRemoteMixSound.setImageResource(R.mipmap.remote_mix_sound);
                        mTextRemoteMixSound.setText(R.string.start_remote_mix_sound);
                        mIsRemoteMixingSound = false;
                    }
                    mIsPauseMixingSound = false;
                    mImgControlMixSound.setImageResource(R.mipmap.pause);
                    mTextControlMixSound.setText(R.string.pause_mixing_sound);
                    mImgControlMixSound.setVisibility(View.GONE);
                    mTextControlMixSound.setVisibility(View.GONE);
                }
            });
        }
    };

    private UCloudRtcSdkSurfaceVideoView.RemoteOpTrigger mOnRemoteOpTrigger = new UCloudRtcSdkSurfaceVideoView.RemoteOpTrigger() {
        @Override
        public void onRemoteVideo(View v, SurfaceViewGroup parent) {
            if (parent.getTag(R.id.swap_info) != null) {
                UCloudRtcSdkStreamInfo swapStreamInfo = (UCloudRtcSdkStreamInfo) parent.getTag(R.id.swap_info);
                sdkEngine.muteRemoteVideo(swapStreamInfo.getUId(), !mRemoteVideoMute);
            } else if (parent.getTag() != null) {
                UCloudRtcSdkStreamInfo streamInfo = (UCloudRtcSdkStreamInfo) parent.getTag();
                sdkEngine.muteRemoteVideo(streamInfo.getUId(), !mRemoteVideoMute);
            }
            mMuteView = (UCloudRtcSdkSurfaceVideoView) parent;
        }

        @Override
        public void onRemoteAudio(View v, SurfaceViewGroup parent) {
            if (parent.getTag(R.id.swap_info) != null) {
                UCloudRtcSdkStreamInfo swapStreamInfo = (UCloudRtcSdkStreamInfo) parent.getTag(R.id.swap_info);
                sdkEngine.muteRemoteAudio(swapStreamInfo.getUId(), !mRemoteAudioMute);
            } else if (parent.getTag() != null) {
                UCloudRtcSdkStreamInfo streamInfo = (UCloudRtcSdkStreamInfo) parent.getTag();
                sdkEngine.muteRemoteAudio(streamInfo.getUId(), !mRemoteAudioMute);
            }
            mMuteView = (UCloudRtcSdkSurfaceVideoView) parent;
        }
    };

    private RemoteVideoAdapter.RemoveRemoteStreamReceiver mRemoveRemoteStreamReceiver = new RemoteVideoAdapter.RemoveRemoteStreamReceiver() {
        @Override
        public void onRemoteStreamRemoved(boolean swaped) {
            if (swaped) {
                if (mClass == UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL) {
                    sdkEngine.stopPreview(mLocalStreamInfo.getMediaType());
                    sdkEngine.renderLocalView(mLocalStreamInfo, mLocalVideoView, null, null);
                } else if (mLocalVideoView.getTag(R.id.swap_info) != null) {
                    UCloudRtcSdkStreamInfo remoteStreamInfo = (UCloudRtcSdkStreamInfo) mLocalVideoView.getTag(R.id.swap_info);
                    sdkEngine.stopRemoteView(remoteStreamInfo);
                }
            }
        }
    };

    UCloudRtcRecordListener mLocalRecordListener = new UCloudRtcRecordListener() {
        @Override
        public void onLocalRecordStart(String path, int code, String msg) {
            Log.d(TAG, "onLocalRecordStart: " + path + " code: " + code + " msg: " + msg);
        }

        @Override
        public void onLocalRecordStop(String path, long fileLength, int code) {
            Log.d(TAG, "onLocalRecordStop: " + path + "fileLength: " + fileLength + "code: " + code);
        }

        @Override
        public void onRecordStatusCallBack(long duration, long fileSize) {
            Log.d(TAG, "onRecordStatusCallBack duration: " + duration + " fileSize: " + fileSize);
        }
    };

    private View.OnClickListener mToggleScreenOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 回显画面全屏切换
            toggleFullScreen();
        }
    };

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() { // 外接usb监听接口
        @Override
        public void onAttach(final UsbDevice device) {
            Log.v(TAG, "onAttach current device:" + device);
            Log.v(TAG, "device class is:" + device.getDeviceClass());
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        if (mUSBMonitor != null) {
                            Log.v(TAG, "onAttach: mUSBMonitor.getDeviceCount(): " + mUSBMonitor.getDeviceCount());
                            if (mUSBMonitor.getDeviceCount() > 0) {
                                mUSBMonitor.requestPermission(device);
                            }
                        }
                    }
                }
            }, 0);
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            Log.v(TAG, "onConnect:");
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        if (mUVCCamera != null) {
                            mUVCCamera.destroy();
                            mUVCCamera = null;
                        }
                        isActive = isPreview = false;

                        mUVCCamera = initUVCCamera(ctrlBlock);
                        if (mUVCCamera != null) {
                            isActive = true;
                            isPreview = true;
                        } else {
                            Log.d(TAG, "USB Camera初始化失败!:");
                        }

                    }
                }
            }, 0);
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            Log.v(TAG, "onDisconnect:");
            // XXX you should check whether the comming device equal to camera device that currently using
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        if (mUVCCamera != null) {
                            mUVCCamera.close();
                            isActive = isPreview = false;
                        }
                    }
                }
            }, 0);
        }

        @Override
        public void onDetach(final UsbDevice device) {
            Log.v(TAG, "onDetach: USB摄像头被移除");
        }

        @Override
        public void onCancel(final UsbDevice device) {
        }
    };

    private View.OnClickListener mSwapRemoteLocalListener = new View.OnClickListener() { // 大小窗切换监听
        @Override
        public void onClick(View v) {
            if (v instanceof UCloudRtcSdkSurfaceVideoView) {
                UCloudRtcSdkStreamInfo clickStreamInfo = (UCloudRtcSdkStreamInfo) v.getTag();
                boolean swapLocal = mSwapStreamInfo.getUId().equals(mUserid);
                boolean clickLocal = clickStreamInfo.getUId().equals(mUserid);
                Log.d(TAG, "mSwapStreamInfo: " + mSwapStreamInfo + " clickInfo: " + clickStreamInfo);
                Log.d(TAG, "onClick swaplocal" + swapLocal + " clickLocal: " + clickLocal);
                if (swapLocal && !clickLocal) {
                    sdkEngine.stopRemoteView(clickStreamInfo);
                    sdkEngine.stopPreview(mSwapStreamInfo.getMediaType());
//                        sdkEngine.renderLocalView(mSwapStreamInfo, v,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                    UCloudRtcRenderView remoteRender = (UCloudRtcRenderView) v.getTag(R.id.render);
                    sdkEngine.renderLocalView(mSwapStreamInfo, remoteRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
//                        sdkEngine.startRemoteView(clickStreamInfo, mLocalVideoView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL,null);
                    sdkEngine.startRemoteView(clickStreamInfo, mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                    ((UCloudRtcSdkSurfaceVideoView) v).refreshRemoteOp(View.INVISIBLE);
                } else if (!swapLocal && clickLocal) {
                    sdkEngine.stopRemoteView(mSwapStreamInfo);
                    sdkEngine.stopPreview(clickStreamInfo.getMediaType());
                    UCloudRtcRenderView remoteRender = (UCloudRtcRenderView) v.getTag(R.id.render);
                    sdkEngine.renderLocalView(clickStreamInfo, mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                    sdkEngine.startRemoteView(mSwapStreamInfo, remoteRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                    ((UCloudRtcSdkSurfaceVideoView) v).refreshRemoteOp(View.VISIBLE);
                } else if (!swapLocal && !clickLocal) {
                    sdkEngine.stopRemoteView(mSwapStreamInfo);
                    sdkEngine.stopRemoteView(clickStreamInfo);
                    sdkEngine.startRemoteView(clickStreamInfo, mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                    UCloudRtcRenderView remoteRender = (UCloudRtcRenderView) v.getTag(R.id.render);
                    sdkEngine.startRemoteView(mSwapStreamInfo, remoteRender, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
                }
                v.setTag(mSwapStreamInfo);
                mVideoAdapter.updateSwapInfo(clickStreamInfo, mSwapStreamInfo);
                mSwapStreamInfo = clickStreamInfo;
            }
        }
    };

    private void switchCamera() { // 前后置摄像头切换
        sdkEngine.switchCameraSkipSameSide();
        ToastUtils.shortShow(this, "切换摄像头");
        mSwitchCamera = !mSwitchCamera;
    }

    private boolean muteMic() { // 关闭打开本端麦克风
        sdkEngine.muteLocalMic(!mMuteMic);
        if (!mMuteMic) {
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "关闭麦克风");
        } else {
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "打开麦克风");
        }
        return false;
    }

    private boolean muteVideo() { // 关闭打开本端视频
        if (mScreenEnable || mCameraEnable) {
            if (isScreenCaptureSupport && !mCameraEnable) {
                sdkEngine.muteLocalVideo(!mMuteVideo, UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN);
            } else {
//                sdkEngine.muteLocalVideo(!mMuteVideo, UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO)
                sdkEngine.controlLocalVideo(mMuteVideo);
                mMuteVideo = !mMuteVideo;
            }
        }
        if (!mMuteVideo) {
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "关闭摄像头");
        } else {
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "打开摄像头");
        }
        return false;
    }

    int type = 0;
    private void muteSpeaker(boolean enable) { //喇叭听筒切换
        if (mSpeakerOn) {
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "关闭喇叭");
        } else {
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "打开喇叭");
        }
        mSpeakerOn = !mSpeakerOn;
        sdkEngine.setSpeakerOn(enable);
        mImgBtnMuteSpeaker.setImageResource(enable ? R.mipmap.speaker : R.mipmap.speaker_off);

        type++;
        type = type %3;
        sdkEngine.setDetectSolution(type);
    }

    private void onMuteVideoResult(boolean mute) {
//        mMuteVideo = mute;
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
        } else {
            if (mute) {
//                localrenderview.refresh();
                mLocalVideoView.setVisibility(View.INVISIBLE);
            } else {
                mLocalVideoView.setVisibility(View.VISIBLE);
            }
        }

    }

    private void onMuteMicResult(boolean mute) {
        mMuteMic = mute;
        if (mMuteMic) {
            mImgSoundVolume.setVisibility(View.INVISIBLE);
        } else {
            mImgSoundVolume.setVisibility(View.VISIBLE);
        }
        mImgBtnMuteMic.setImageResource(mMuteMic ? R.mipmap.mic_off :
                R.mipmap.mic);
        mImgMicSts.setImageResource(mMuteMic ? R.mipmap.mic_disable :
                R.mipmap.mic_volume);
    }

    private void mirrorSwitch() { // 前置摄像头镜像切换
        mMirror = !mMirror;
//        UCloudRtcSdkEnv.setFrontCameraMirror(mMirror);
//        if(mMirror) {
//            sdkEngine.updateDesensitizationState(UCloudRtcDesensitizationState.ALL_DESENSITIZATION);
//        }else{
//            sdkEngine.updateDesensitizationState(UCloudRtcDesensitizationState.NONE);
//        }
        mImgBtnMirror.setImageResource(mMirror ? R.mipmap.mirror_on :
                R.mipmap.mirror);
    }

    private void endCall() { // 离开频道
        synchronized (mSync) {
            mLeaveRoomFlag = true;
            if (sdkEngine != null) {
                int result = sdkEngine.leaveChannel().ordinal();
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        releaseExtendCamera();
                    }
                }, 0);
                UCloudRtcSdkEngine.destroy();
                sdkEngine = null;
            }
            finish();
//            sdkEngine.leaveChannelNonStopLocalPreview();
//            sdkEngine.controlLocalVideo(false);
//            mMuteVideo = true;
        }
        Log.d(TAG, "endCall: finish called");

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

    private void releaseExtendCamera() { // 释放扩展摄像头资源
        synchronized (mSync) {
            isActive = isPreview = false;
            if (mUVCCamera != null) {
                mUVCCamera.destroy();
                mUVCCamera = null;
            }
            if (mUSBMonitor != null) {
                mUSBMonitor.destroy();
                mUSBMonitor = null;
            }
        }
//        mVideoAdapter.setRemoveRemoteStreamReceiver(null);
        if (mUCloudRTCDataReceiver != null) {
            mUCloudRTCDataReceiver.releaseBuffer();
            mUCloudRTCDataReceiver = null;
        }
    }

    private void startTimeShow() {
        timeShow.setBase(SystemClock.elapsedRealtime());
        timeShow.start();
    }

    private void stopTimeShow() {
        timeShow.stop();
    }

    public void toggleFullScreen() {
        FrameLayout.LayoutParams params = null;

        if (!mLocalViewFullScreen) {
            setSystemUIVisible(false);
            //隐藏顶部标题和底部工具栏
            mTitleBar.setVisibility(View.GONE);
            mToolBar.setVisibility(View.GONE);
            StatusBarUtils.removeStatusView(this);

            if (mLocalVideoView.getScaleType() == UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT.ordinal()) {
                mLocalVideoView.resetSurface();
            } else {
                params = new FrameLayout.LayoutParams(screenWidth, screenHeight + mToolBar.getHeight());
                params.setMargins(0, 0, 0, 0);
                mLocalVideoView.setLayoutParams(params);
                Log.d(TAG, "Switch full screen in ASPECT_FILL width: " + params.width + " height: " + params.height);
            }
            //抽屉随回显拉长
            DrawerLayout.LayoutParams dl_params = (DrawerLayout.LayoutParams) mDrawerMenu.getLayoutParams();
            dl_params.topMargin = 0;
            dl_params.bottomMargin = 0;
            //隐藏麦克风状态图标
            mImgSoundVolume.setVisibility(View.INVISIBLE);
            mImgMicSts.setVisibility(View.INVISIBLE);
        } else {
            setSystemUIVisible(true);
            //FrameLayout.LayoutParams params = null;
            //退出全屏
            // 获取当前横竖屏模式
            if (UCloudRTCLiveActivity.this.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                params = new FrameLayout.LayoutParams(localViewWidth_landscape, localViewHeight_landscape);
            } else {
                params = new FrameLayout.LayoutParams(localViewWidth_portrait, localViewHeight_portrait);
            }
            if (mLocalVideoView.getScaleType() == UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT.ordinal()) {
                mLocalVideoView.resetSurface();
            } else {
                params.setMargins(0, mTitleBar.getHeight(), 0, mToolBar.getHeight());
                mLocalVideoView.setLayoutParams(params);
            }
            //显示顶部标题和底部工具栏
            mTitleBar.setVisibility(View.VISIBLE);
            mToolBar.setVisibility(View.VISIBLE);
            StatusBarUtils.addStatusView(this);
            //抽屉菜单还原
            DrawerLayout.LayoutParams dl_params = (DrawerLayout.LayoutParams) mDrawerMenu.getLayoutParams();
            dl_params.topMargin = mTitleBar.getHeight();
            dl_params.bottomMargin = mToolBar.getHeight();
            //还原麦克风状态图标
            if (!mMuteMic) {
                mImgSoundVolume.setVisibility(View.VISIBLE);
            }
            mImgMicSts.setVisibility(View.VISIBLE);

            Log.d(TAG, "Quit full screen. width: " + params.width + " height: " + params.height);
        }
        mLocalViewFullScreen = !mLocalViewFullScreen;
    }

    private UCloudRTCScreenShot mUCloudRTCScreenShot = new UCloudRTCScreenShot() { // 本地截图
        @Override
        public void onReceiveRGBAData(ByteBuffer rgbBuffer, int width, int height) {
            final Bitmap bitmap = Bitmap.createBitmap(width * 1, height * 1, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(rgbBuffer);
            String name = "/mnt/sdcard/urtcscreen_" + System.currentTimeMillis() + ".jpg";
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
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "screen shoot : " + name);
        }
    };

    private void addScreenShotCallBack(View view) {
        if (view instanceof UCloudRtcSdkSurfaceVideoView) {
            ((UCloudRtcSdkSurfaceVideoView) view).setScreenShotBack(mUCloudRTCScreenShot);
        } else if (view instanceof UCloudRtcRenderView) {
            ((UCloudRtcRenderView) view).setScreenShotBack(mUCloudRTCScreenShot);
        }
    }

    //初始化视频录制
    private void initRecordManager() {
        RtcRecordManager.init("");
        Log.d(TAG, "initRecordManager: cache path:" + RtcRecordManager.getVideoCachePath());
    }

    private void toggleLocalRecord() { // 本地录制界面更新（未实现）
        if (!mIsLocalRecording) {
            Log.d(TAG, " start local record: ");
            //URTCRecordManager.getInstance().startRecord(UCloudRtcSdkRecordType.U_CLOUD_RTC_SDK_RECORD_TYPE_MP4,"mnt/sdcard/urtc/mp4/"+ System.currentTimeMillis()+".mp4",mLocalRecordListener,1000);
            mIsLocalRecording = true;
            mImgLocalRecord.setImageResource(R.mipmap.stop);
            mTextLocalRecord.setText(R.string.local_recording);
        } else {
            Log.d(TAG, " stop local record: ");
            //URTCRecordManager.getInstance().stopRecord();
            mIsLocalRecording = false;
            mImgLocalRecord.setImageResource(R.mipmap.record);
            mTextLocalRecord.setText(R.string.start_local_record);
        }
    }

    private void toggleRemoteRecord() { // 远端录制
        if (!mIsRemoteRecording) {
            Log.d(TAG, " start remote record: ");
            mAtomOpStart = true;
            // 生成录制配置
            UCloudRtcSdkMixProfile mixProfile = UCloudRtcSdkMixProfile.getInstance().assembleRecordMixParamsBuilder()
                    .type(UCloudRtcSdkMixProfile.MIX_TYPE_RECORD)
                    //画面模式
                    .layout(UCloudRtcSdkMixProfile.LAYOUT_AVERAGE_1)
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
                    .addStreamMode(UCloudRtcSdkMixProfile.ADD_STREAM_MODE_AUTO)
                    //添加流列表，也可以后续调用MIX_TYPE_UPDATE 动态添加
                    .addStream(mUserid, UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                    //录像存储区域
                    .region(REGION)
                    //录像存储桶
                    .Bucket(BUCKET)
                    .build();
            sdkEngine.startRecord(mixProfile); // 开始录制
        } else if (!mAtomOpStart) {
            Log.d(TAG, " stop remote record: ");
            mAtomOpStart = true;
            sdkEngine.stopRecord(); // 停止录制
        }
    }

    private void toggleMix() { // 转推
        if (!mIsMixing) {
            Log.d(TAG, " start mix: ");
            mAtomOpStart = true;
            // 生成转推配置
            UCloudRtcSdkMixProfile mixProfile = UCloudRtcSdkMixProfile.getInstance().assembleUpdateMixParamsBuilder()
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
                    .addStream(mUserid, UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                    //设置转推cdn 的地址
                    .addPushUrl("rtmp://rtcpush.ugslb.com/rtclive/" + mRoomid)
                    //关键用户
                    .keyUser(mUserid)
                    //流上限
                    .layoutUserLimit(2)
                    //房间没流多久结束任务
                    .taskTimeOut(70)
                    .build();
            sdkEngine.updateMixConfig(mixProfile); // 开始转推
        } else if (!mAtomOpStart) {
            Log.d(TAG, " stop mix: ");
            mAtomOpStart = true;
            sdkEngine.stopRelay(null); // 停止转推
        }
    }

    private void update(int type) {
        Log.d(TAG, " start update: ");
        UCloudRtcSdkMixProfile mixProfile = UCloudRtcSdkMixProfile.getInstance().assembleMixParamsBuilder()
                .type(type)
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
                .addStream(mUserid, UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                .addStream(latestRemoteInfo.getUId(), latestRemoteInfo.getMediaType().ordinal())
                //设置转推cdn 的地址
                .addPushUrl("rtmp://rtcpush.ugslb.com/rtclive/" + mRoomid)
                //关键用户
                .keyUser(mUserid)
                //流上限
                .layoutUserLimit(2)
                //房间没流多久结束任务
                .taskTimeOut(70)
                .build();
        sdkEngine.updateMixConfig(mixProfile);
    }

    // 播放本地或远端混音
    private void toggleMixingSound(boolean isRemotePlay) {
        if (!isRemotePlay) {
            // 本地混音
            if (!mIsLocalMixingSound) {
                if (!sdkEngine.startPlayAudioFile(
                        //"/sdcard/light.mp3",
                        sdkEngine.copyAssetsFileToSdcard("water.mp3"),
                        false, false)) {
                    return;
                } else {
                    mImgLocalMixSound.setImageResource(R.mipmap.stop);
                    mTextLocalMixSound.setText(R.string.local_sound_mixing);
                    mImgRemoteMixSound.setVisibility(View.GONE);
                    mTextRemoteMixSound.setVisibility(View.GONE);
                    mImgControlMixSound.setVisibility(View.VISIBLE);
                    mTextControlMixSound.setVisibility(View.VISIBLE);
                }
            } else {
                sdkEngine.stopPlayAudioFile();
                mIsPauseMixingSound = false;
                mImgLocalMixSound.setImageResource(R.mipmap.local_mix_sound);
                mTextLocalMixSound.setText(R.string.start_local_mix_sound);
                if (mVideoIsPublished) {
                    mImgRemoteMixSound.setVisibility(View.VISIBLE);
                    mTextRemoteMixSound.setVisibility(View.VISIBLE);
                }
                mImgControlMixSound.setImageResource(R.mipmap.pause);
                mTextControlMixSound.setText(R.string.pause_mixing_sound);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
            }
            mIsLocalMixingSound = !mIsLocalMixingSound;
        } else if (mVideoIsPublished && isRemotePlay) {
            // 本地+远端混音
            if (!mIsRemoteMixingSound) {
                if (!sdkEngine.startPlayAudioFile(
                        //"/sdcard/light.mp3",
                        sdkEngine.copyAssetsFileToSdcard("water.mp3"),
                        true, false)) {
                    return;
                } else {
                    mImgRemoteMixSound.setImageResource(R.mipmap.stop);
                    mTextRemoteMixSound.setText(R.string.remote_sound_mixing);
                    mImgLocalMixSound.setVisibility(View.GONE);
                    mTextLocalMixSound.setVisibility(View.GONE);
                    mImgControlMixSound.setVisibility(View.VISIBLE);
                    mTextControlMixSound.setVisibility(View.VISIBLE);
                }
            } else {
                sdkEngine.stopPlayAudioFile();
                mIsPauseMixingSound = false;
                mImgRemoteMixSound.setImageResource(R.mipmap.remote_mix_sound);
                mTextRemoteMixSound.setText(R.string.start_remote_mix_sound);
                mImgLocalMixSound.setVisibility(View.VISIBLE);
                mTextLocalMixSound.setVisibility(View.VISIBLE);
                mImgControlMixSound.setImageResource(R.mipmap.pause);
                mTextControlMixSound.setText(R.string.pause_mixing_sound);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
            }
            mIsRemoteMixingSound = !mIsRemoteMixingSound;
        } else {
            Log.e(TAG, " Wrong mixing status.");
        }
    }

    private void toggleControlMixingSound() { // 混音控制
        if (mIsLocalMixingSound || mIsRemoteMixingSound) {
            if (mIsPauseMixingSound) {
                sdkEngine.resumeAudioFile();
                mImgControlMixSound.setImageResource(R.mipmap.pause);
                mTextControlMixSound.setText(R.string.pause_mixing_sound);
            } else {
                sdkEngine.pauseAudioFile();
                mImgControlMixSound.setImageResource(R.mipmap.play);
                mTextControlMixSound.setText(R.string.resume_mixing_sound);
            }
            mIsPauseMixingSound = !mIsPauseMixingSound;
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
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    private UVCCamera initUVCCamera(USBMonitor.UsbControlBlock ctrlBlock) { // usb外接摄像头初始化
        Log.d(TAG, "initUVCCamera-----mVideoProfileSelect:" + mVideoProfileSelect + " width:" + videoProfile.getWidth()
                + " height:" + videoProfile.getHeight());
        final UVCCamera camera = new UVCCamera();
        int result = camera.open(ctrlBlock);
        if (result != 0) {
            Log.d(TAG, "USB Camera open failed.");
            return null;
        }
        try {
            camera.setPreviewSize(
                    videoProfile.getWidth(),
                    videoProfile.getHeight(),
                    UVCCamera.FRAME_FORMAT_YUYV
            );
        } catch (final IllegalArgumentException e) {
            Log.d(TAG, "preview size is not support");
            Log.d(TAG, "USB Camera当前分辨率"
                    + videoProfile.getWidth() + "*" + videoProfile.getHeight() + "不支持，请重新配置");
            camera.destroy();
            return null;
        }


        //SurfaceTexture surface= mLocalVideoView.getSurfaceTexture();
        //UCloudRtcRenderView surface = mLocalVideoView.getSurfaceView();

        // Start preview to external GL texture
        // NOTE : this is necessary for callback passed to [UVCCamera.setFrameCallback]
        // to be triggered afterwards
        //camera.setPreviewTexture(surface);
        result = camera.startPreview();
        if (result != 0) {
            Log.d(TAG, "startPreview failed!");
            camera.destroy();
            return null;
        }

        camera.setFrameCallback(new IFrameCallback() {
            @Override
            public void onFrame(ByteBuffer frame) {
/*                Log.d("UCloudRTCLiveActivity", "onFrame byteBuffer, frame.position: " + frame.position()
                                + " frame.limit: " + frame.limit());*/
                createFrameByteBuffer(frame);
            }
        }, mUVCCameraFormat);
        return camera;
    }

    //外置数据输入监听
    private UCloudRTCDataProvider mUCloudRTCDataProvider = new UCloudRTCDataProvider() {
        private ByteBuffer cacheBuffer;

        @Override
        public ByteBuffer provideRGBData(List<Integer> params) {
            if (videoSourceData == null) {
                Log.d("UCloudRTCLiveActivity", "provideRGBData byteBuffer data is null");
                return null;
            } else {
                params.add(mURTCVideoFormat);
                params.add(videoProfile.getWidth());
                params.add(videoProfile.getHeight());
                if (cacheBuffer == null) {
                    cacheBuffer = sdkEngine.getNativeOpInterface().createNativeByteBuffer(1920 * 1080 * 4);
                    Log.d("UCloudRTCLiveActivity", "byteBuffer allocate call ");
                }
                cacheBuffer.clear();
                synchronized (extendByteBufferSync) {
                    cacheBuffer.put(videoSourceData);
                }

                cacheBuffer.flip();

                return cacheBuffer;
            }
        }

        @Override
        public void releaseBuffer() { // 释放资源
            Log.d("UCloudRTCLiveActivity", "releaseBuffer");
            if (videoSourceData != null) {
                videoSourceData.clear();
                videoSourceData = null;
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
            Log.d("UCloudRTCLiveActivity", "onReceiveRGBAData!");

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
            if (cache == null) {
                //根据需求来，设置最大的可能用到的buffersize，后续回调会复用这块内存
                int size = 4096 * 2160 * 4;
                cache = sdkEngine.getNativeOpInterface().
                        createNativeByteBuffer(4096 * 2160 * 4);
            }
            cache.clear();
            return cache;
        }

        @Override
        public void releaseBuffer() {
            if (cache != null)
                sdkEngine.getNativeOpInterface().releaseNativeByteBuffer(cache);
            cache = null;
        }
    };

    //自定义前台通知
    private UCloudRTCNotification mScreenCaptureNotification = new UCloudRTCNotification() {
        @Override
        public Notification createNotificationChannel() {
            Notification.Builder builder = new Notification.Builder(getApplicationContext()); //获取一个Notification构造器
            Intent nfIntent = new Intent(getApplicationContext(), UCloudRTCLiveActivity.class); //点击后跳转的界面，可以设置跳转数据

            builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, nfIntent, 0)) // 设置PendingIntent
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                    //.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("screen capturing") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            //以下是对Android 8.0的适配
            //普通notification适配
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId("notification_id");
            }
            //前台服务notification适配
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = builder.build(); // 获取构建好的Notification
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

            return notification;
        }
    };

    private void createFrameByteBuffer(ByteBuffer frame) { // 获取外接视频数据
        try {
            if (frame != null) {
                synchronized (extendByteBufferSync) {
                    if (videoSourceData != null) {
                        videoSourceData.clear();
                        videoSourceData.put(frame);
                        videoSourceData.flip();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // FIXME
                }
            });
        }
    }

    private void updateVideoFormat(int videoFormat) { // 设置外接视频格式
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

    private void setPreview(boolean onOff) { //预览窗口开关
        if (onOff) {
            sdkEngine.startCameraPreview(
                    mLocalVideoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, new UCloudRTCFirstFrameRendered() {
                        @Override
                        public void onFirstFrameRender(UCloudRtcSdkStreamInfo info, View view) {
                            Log.d(TAG, "onFirstFrameRender: local");
                            View mask = findViewById(R.id.mask);
                            runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  Log.d(TAG, "onFirstFrameRender: remove mask");
                                                  mask.setVisibility(View.GONE);
                                              }
                                          }
                            );

                        }
                    });
        } else {
            sdkEngine.stopPreview(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
        }
    }

    private void setIconStats(boolean visible) { // 图标状态切换
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
            mImgRemoteMixSound.setVisibility(View.GONE);
            mTextRemoteMixSound.setVisibility(View.GONE);
            mImgControlMixSound.setVisibility(View.GONE);
            mTextControlMixSound.setVisibility(View.GONE);
        } else {
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
            mImgPreview.setVisibility(View.GONE);
            mTextPreview.setVisibility(View.GONE);
            mImgRemoteMixSound.setVisibility(View.VISIBLE);
            mTextRemoteMixSound.setVisibility(View.VISIBLE);
        }
    }

    public enum userStatus {
        INIT,
        JOIN_ROOM_NOT_PREVIEW,
        PREVIEW_NOT_JOIN_ROOM,
        PREVIEW_JOIN_ROOM_NOT_PUBLISH,
        PUBLISH_NOT_PREVIEW,
        PUBLISH_VIDEO_AND_PREVIEW,
        PUBLISH_SCREEN_AND_PREVIEW,
        JOIN_ROOM_AND_SUBSCRIBE;

        public static userStatus matchValue(int value) {
            for (userStatus en : values()) {
                if (en.ordinal() == value) {
                    return en;
                }
            }
            return userStatus.INIT;
        }
    }

    private void setIconStats(userStatus status) { // 图标状态切换
        switch (status) {
            case JOIN_ROOM_NOT_PREVIEW:
                mImgJoin.setVisibility(View.GONE);
                mTextJoin.setVisibility(View.GONE);
                mImgLeave.setVisibility(View.VISIBLE);
                mTextLeave.setVisibility(View.VISIBLE);
                mImgPreview.setVisibility(View.VISIBLE);
                mTextPreview.setText(R.string.start_preview);
                mTextPreview.setVisibility(View.VISIBLE);
                if (mPublishMode == CommonUtils.MANUAL_MODE) {
                    mImgManualPubVideo.setImageResource(R.mipmap.publish); // 修改界面图标
                    mTextManualPubVideo.setText(R.string.pub_video); // 修改界面文字
                    mImgManualPubVideo.setVisibility(View.VISIBLE);
                    mTextManualPubVideo.setVisibility(View.VISIBLE);
                    if (mScreenEnable) {
                        mImgManualPubScreen.setImageResource(R.mipmap.publish_screen); // 修改界面图标
                        mTextManualPubScreen.setText(R.string.pub_screen); // 修改界面文字
                        mImgManualPubScreen.setVisibility(View.VISIBLE);
                        mTextManualPubScreen.setVisibility(View.VISIBLE);
                    }
                } else {
                    mImgManualPubVideo.setVisibility(View.GONE);
                    mTextManualPubVideo.setVisibility(View.GONE);
                    mImgManualPubScreen.setVisibility(View.GONE);
                    mTextManualPubScreen.setVisibility(View.GONE);
                }
                mImgMix.setVisibility(View.GONE);
                mTextMix.setVisibility(View.GONE);
                mImgLocalRecord.setVisibility(View.GONE);
                mTextLocalRecord.setVisibility(View.GONE);
                mImgScreenshot.setVisibility(View.GONE);
                mTextScreenshot.setVisibility(View.GONE);
                mImgRemoteRecord.setVisibility(View.GONE);
                mTextRemoteRecord.setVisibility(View.GONE);
                mImgRemoteMixSound.setVisibility(View.GONE);
                mTextRemoteMixSound.setVisibility(View.GONE);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
                mImgBtnMuteMic.setVisibility(View.INVISIBLE);
                mImgBtnMuteVideo.setVisibility(View.INVISIBLE);
                mImgBtnMirror.setVisibility(View.INVISIBLE);
                mTextResolution.setVisibility(View.INVISIBLE);
                break;
            case PREVIEW_NOT_JOIN_ROOM:
                mImgJoin.setVisibility(View.VISIBLE);
                mTextJoin.setVisibility(View.VISIBLE);
                mImgLeave.setVisibility(View.GONE);
                mTextLeave.setVisibility(View.GONE);
                mImgPreview.setVisibility(View.VISIBLE);
                mTextPreview.setText(R.string.stop_preview);
                mTextPreview.setVisibility(View.VISIBLE);
                mImgManualPubScreen.setVisibility(View.GONE);
                mTextManualPubScreen.setVisibility(View.GONE);
                mImgManualPubVideo.setVisibility(View.GONE);
                mTextManualPubVideo.setVisibility(View.GONE);
                mImgManualPubScreen.setVisibility(View.GONE);
                mTextManualPubScreen.setVisibility(View.GONE);
                mImgMix.setVisibility(View.GONE);
                mTextMix.setVisibility(View.GONE);
                mImgLocalRecord.setVisibility(View.GONE);
                mTextLocalRecord.setVisibility(View.GONE);
                mImgScreenshot.setVisibility(View.GONE);
                mTextScreenshot.setVisibility(View.GONE);
                mImgRemoteRecord.setVisibility(View.GONE);
                mTextRemoteRecord.setVisibility(View.GONE);
                mImgRemoteMixSound.setVisibility(View.GONE);
                mTextRemoteMixSound.setVisibility(View.GONE);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
                mImgBtnMuteMic.setVisibility(View.INVISIBLE);
                mImgBtnMuteVideo.setVisibility(View.INVISIBLE);
                mImgBtnMirror.setVisibility(View.INVISIBLE);
                mTextResolution.setVisibility(View.INVISIBLE);
                break;
            case PREVIEW_JOIN_ROOM_NOT_PUBLISH:
                mImgJoin.setVisibility(View.GONE);
                mTextJoin.setVisibility(View.GONE);
                mImgLeave.setVisibility(View.VISIBLE);
                mTextLeave.setVisibility(View.VISIBLE);
                mImgPreview.setVisibility(View.VISIBLE);
                mTextPreview.setText(R.string.stop_preview);
                mTextPreview.setVisibility(View.VISIBLE);
                if (mPublishMode == CommonUtils.MANUAL_MODE) {
                    mImgManualPubVideo.setImageResource(R.mipmap.publish); // 修改界面图标
                    mTextManualPubVideo.setText(R.string.pub_video); // 修改界面文字
                    mImgManualPubVideo.setVisibility(View.VISIBLE);
                    mTextManualPubVideo.setVisibility(View.VISIBLE);
                    if (mScreenEnable) {
                        mImgManualPubScreen.setImageResource(R.mipmap.publish_screen); // 修改界面图标
                        mTextManualPubScreen.setText(R.string.pub_screen); // 修改界面文字
                        mImgManualPubScreen.setVisibility(View.VISIBLE);
                        mTextManualPubScreen.setVisibility(View.VISIBLE);
                    }
                } else {
                    mImgManualPubVideo.setVisibility(View.GONE);
                    mTextManualPubVideo.setVisibility(View.GONE);
                    mImgManualPubScreen.setVisibility(View.GONE);
                    mTextManualPubScreen.setVisibility(View.GONE);
                }
                mImgMix.setVisibility(View.GONE);
                mTextMix.setVisibility(View.GONE);
                mImgLocalRecord.setVisibility(View.GONE);
                mTextLocalRecord.setVisibility(View.GONE);
                mImgScreenshot.setVisibility(View.GONE);
                mTextScreenshot.setVisibility(View.GONE);
                mImgRemoteRecord.setVisibility(View.GONE);
                mTextRemoteRecord.setVisibility(View.GONE);
                mImgRemoteMixSound.setVisibility(View.GONE);
                mTextRemoteMixSound.setVisibility(View.GONE);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
                mImgBtnMuteMic.setVisibility(View.INVISIBLE);
                mImgBtnMuteVideo.setVisibility(View.INVISIBLE);
                mImgBtnMirror.setVisibility(View.INVISIBLE);
                mTextResolution.setVisibility(View.INVISIBLE);
                break;
            case PUBLISH_NOT_PREVIEW:
                mImgJoin.setVisibility(View.GONE);
                mTextJoin.setVisibility(View.GONE);
                mImgLeave.setVisibility(View.VISIBLE);
                mTextLeave.setVisibility(View.VISIBLE);
                mImgPreview.setVisibility(View.VISIBLE);
                mTextPreview.setText(R.string.start_preview);
                mTextPreview.setVisibility(View.VISIBLE);
                mImgManualPubVideo.setImageResource(R.mipmap.stop); // 修改界面图标
                mTextManualPubVideo.setText(R.string.pub_cancel_video); // 修改界面文字
                mImgManualPubVideo.setVisibility(View.VISIBLE);
                mTextManualPubVideo.setVisibility(View.VISIBLE);
//                if (mScreenEnable) {
//                    mImgManualPubScreen.setImageResource(R.mipmap.stop); // 修改界面图标
//                    mTextManualPubScreen.setText(R.string.pub_cancel_screen); // 修改界面文字
//                    mImgManualPubScreen.setVisibility(View.VISIBLE);
//                    mTextManualPubScreen.setVisibility(View.VISIBLE);
//                }
                mImgMix.setVisibility(View.GONE);
                mTextMix.setVisibility(View.GONE);
                mImgLocalRecord.setVisibility(View.GONE);
                mTextLocalRecord.setVisibility(View.GONE);
                mImgScreenshot.setVisibility(View.GONE);
                mTextScreenshot.setVisibility(View.GONE);
                mImgRemoteRecord.setVisibility(View.GONE);
                mTextRemoteRecord.setVisibility(View.GONE);
                mImgRemoteMixSound.setVisibility(View.VISIBLE);
                mTextRemoteMixSound.setVisibility(View.VISIBLE);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
                mImgBtnMuteMic.setVisibility(View.INVISIBLE);
                mImgBtnMuteVideo.setVisibility(View.INVISIBLE);
                mImgBtnMirror.setVisibility(View.INVISIBLE);
                mTextResolution.setVisibility(View.INVISIBLE);
                break;
            case PUBLISH_VIDEO_AND_PREVIEW:
                mImgJoin.setVisibility(View.GONE);
                mTextJoin.setVisibility(View.GONE);
                mImgLeave.setVisibility(View.GONE);
                mTextLeave.setVisibility(View.GONE);
                mImgPreview.setVisibility(View.GONE);
                mTextPreview.setText(R.string.stop_preview);
                mTextPreview.setVisibility(View.GONE);
                mImgManualPubVideo.setImageResource(R.mipmap.stop); // 修改界面图标
                mTextManualPubVideo.setText(R.string.pub_cancel_video); // 修改界面文字
                mImgManualPubVideo.setVisibility(View.VISIBLE);
                mTextManualPubVideo.setVisibility(View.VISIBLE);
                mImgMix.setVisibility(View.VISIBLE);
                mTextMix.setVisibility(View.VISIBLE);
                mImgLocalRecord.setVisibility(View.VISIBLE);
                mTextLocalRecord.setVisibility(View.VISIBLE);
                mImgScreenshot.setVisibility(View.VISIBLE);
                mTextScreenshot.setVisibility(View.VISIBLE);
                mImgRemoteRecord.setVisibility(View.VISIBLE);
                mTextRemoteRecord.setVisibility(View.VISIBLE);
                mImgRemoteMixSound.setVisibility(View.VISIBLE);
                mTextRemoteMixSound.setVisibility(View.VISIBLE);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
                mImgBtnMuteMic.setVisibility(View.VISIBLE);
                mImgBtnMuteVideo.setVisibility(View.VISIBLE);
                mImgBtnMirror.setVisibility(View.VISIBLE);
                mTextResolution.setVisibility(View.VISIBLE);
                break;
            case PUBLISH_SCREEN_AND_PREVIEW:
                mImgJoin.setVisibility(View.GONE);
                mTextJoin.setVisibility(View.GONE);
                mImgLeave.setVisibility(View.GONE);
                mTextLeave.setVisibility(View.GONE);
                mImgPreview.setVisibility(View.GONE);
                mTextPreview.setText(R.string.stop_preview);
                mTextPreview.setVisibility(View.GONE);
                mImgManualPubScreen.setImageResource(R.mipmap.stop); // 修改界面图标
                mTextManualPubScreen.setText(R.string.pub_cancel_screen); // 修改界面文字
                mImgManualPubScreen.setVisibility(View.VISIBLE);
                mTextManualPubScreen.setVisibility(View.VISIBLE);
                mImgMix.setVisibility(View.VISIBLE);
                mTextMix.setVisibility(View.VISIBLE);
                mImgLocalRecord.setVisibility(View.VISIBLE);
                mTextLocalRecord.setVisibility(View.VISIBLE);
                mImgScreenshot.setVisibility(View.VISIBLE);
                mTextScreenshot.setVisibility(View.VISIBLE);
                mImgRemoteRecord.setVisibility(View.VISIBLE);
                mTextRemoteRecord.setVisibility(View.VISIBLE);
                mImgRemoteMixSound.setVisibility(View.VISIBLE);
                mTextRemoteMixSound.setVisibility(View.VISIBLE);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
                mImgBtnMuteMic.setVisibility(View.VISIBLE);
                mImgBtnMuteVideo.setVisibility(View.VISIBLE);
                mImgBtnMirror.setVisibility(View.VISIBLE);
                mTextResolution.setVisibility(View.VISIBLE);
                break;
            case JOIN_ROOM_AND_SUBSCRIBE:
                if (!mVideoIsPublished && !mScreenIsPublished) {
                    mImgJoin.setVisibility(View.GONE);
                    mTextJoin.setVisibility(View.GONE);
                    mImgLeave.setVisibility(View.VISIBLE);
                    mTextLeave.setVisibility(View.VISIBLE);
                    mImgPreview.setVisibility(View.GONE);
                    mTextPreview.setVisibility(View.GONE);
                    if (mPublishMode == CommonUtils.MANUAL_MODE) {
                        mImgManualPubVideo.setImageResource(R.mipmap.publish); // 修改界面图标
                        mTextManualPubVideo.setText(R.string.pub_video); // 修改界面文字
                        mImgManualPubVideo.setVisibility(View.VISIBLE);
                        mTextManualPubVideo.setVisibility(View.VISIBLE);
                        if (mScreenEnable) {
                            mImgManualPubScreen.setImageResource(R.mipmap.publish_screen); // 修改界面图标
                            mTextManualPubScreen.setText(R.string.pub_screen); // 修改界面文字
                            mImgManualPubScreen.setVisibility(View.VISIBLE);
                            mTextManualPubScreen.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mImgManualPubVideo.setVisibility(View.GONE);
                        mTextManualPubVideo.setVisibility(View.GONE);
                        mImgManualPubScreen.setVisibility(View.GONE);
                        mTextManualPubScreen.setVisibility(View.GONE);
                    }
                    mImgMix.setVisibility(View.GONE);
                    mTextMix.setVisibility(View.GONE);
                    mImgLocalRecord.setVisibility(View.GONE);
                    mTextLocalRecord.setVisibility(View.GONE);
                    mImgScreenshot.setVisibility(View.GONE);
                    mTextScreenshot.setVisibility(View.GONE);
                    mImgRemoteRecord.setVisibility(View.GONE);
                    mTextRemoteRecord.setVisibility(View.GONE);
                    mImgRemoteMixSound.setVisibility(View.GONE);
                    mTextRemoteMixSound.setVisibility(View.GONE);
                    mImgControlMixSound.setVisibility(View.GONE);
                    mTextControlMixSound.setVisibility(View.GONE);
                    mImgBtnMuteMic.setVisibility(View.INVISIBLE);
                    mImgBtnMuteVideo.setVisibility(View.INVISIBLE);
                    mImgBtnMirror.setVisibility(View.INVISIBLE);
                    mTextResolution.setVisibility(View.INVISIBLE);
                }
                break;
            case INIT:
            default:
                mImgJoin.setVisibility(View.VISIBLE);
                mTextJoin.setVisibility(View.VISIBLE);
                mImgLeave.setVisibility(View.GONE);
                mTextLeave.setVisibility(View.GONE);
                mImgPreview.setVisibility(View.VISIBLE);
                mTextPreview.setText(R.string.start_preview);
                mTextPreview.setVisibility(View.VISIBLE);
                mImgManualPubVideo.setVisibility(View.GONE);
                mTextManualPubVideo.setVisibility(View.GONE);
                mImgManualPubScreen.setVisibility(View.GONE);
                mTextManualPubScreen.setVisibility(View.GONE);
                mImgMix.setVisibility(View.GONE);
                mTextMix.setVisibility(View.GONE);
                mImgLocalRecord.setVisibility(View.GONE);
                mTextLocalRecord.setVisibility(View.GONE);
                mImgScreenshot.setVisibility(View.GONE);
                mTextScreenshot.setVisibility(View.GONE);
                mImgRemoteRecord.setVisibility(View.GONE);
                mTextRemoteRecord.setVisibility(View.GONE);
                mImgRemoteMixSound.setVisibility(View.GONE);
                mTextRemoteMixSound.setVisibility(View.GONE);
                mImgControlMixSound.setVisibility(View.GONE);
                mTextControlMixSound.setVisibility(View.GONE);
                mImgBtnMuteMic.setVisibility(View.INVISIBLE);
                mImgBtnMuteVideo.setVisibility(View.INVISIBLE);
                mImgBtnMirror.setVisibility(View.INVISIBLE);
                mTextResolution.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void refreshSettings() { // 配置刷新
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
        } else {
            sdkEngine.setAudioOnlyMode(false);
        }
        sdkEngine.configLocalCameraPublish(mCameraEnable);
        sdkEngine.configLocalAudioPublish(mMicEnable);
        if (isScreenCaptureSupport) {
            sdkEngine.configLocalScreenPublish(mScreenEnable);
        } else {
            sdkEngine.configLocalScreenPublish(false);
        }
    }
}

