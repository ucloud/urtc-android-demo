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

import com.cmcc.sdkengine.define.CMCCSurfaceViewRenderer;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.cmcc.sdkengine.CMCCRtcEngine;
import com.cmcc.sdkengine.CMCCEnvHelper;
import com.cmcc.sdkengine.define.CMCCAudioDevice;
import com.cmcc.sdkengine.define.CMCCAuthInfo;
import com.cmcc.sdkengine.define.CMCCCaptureMode;
import com.cmcc.sdkengine.define.CMCCErrorCode;
import com.cmcc.sdkengine.define.CMCCMediaServiceStatus;
import com.cmcc.sdkengine.define.CMCCMediaType;
import com.cmcc.sdkengine.define.CMCCMixProfile;
import com.cmcc.sdkengine.define.CMCCNetWorkQuality;
import com.cmcc.sdkengine.define.CMCCChannelProfile;
import com.cmcc.sdkengine.define.CMCCScaleType;
import com.cmcc.sdkengine.define.CMCCStreamStatus;
import com.cmcc.sdkengine.define.CMCCStreamInfo;
import com.cmcc.sdkengine.define.CMCCClientRole;
import com.cmcc.sdkengine.define.CMCCStreamType;
import com.cmcc.sdkengine.define.CMCCSurfaceViewGroup;
import com.cmcc.sdkengine.define.CMCCTrackType;
import com.cmcc.sdkengine.define.CMCCVideoProfile;
import com.cmcc.sdkengine.listener.ICMCCRecordListener;
import com.cmcc.sdkengine.listener.ICMCCRtcEngineEventHandler;
import com.cmcc.sdkengine.openinterface.CMCCDataProvider;
import com.cmcc.sdkengine.openinterface.CMCCDataReceiver;
import com.cmcc.sdkengine.openinterface.CMCCNotification;
import com.cmcc.sdkengine.openinterface.CMCCScreenShot;
import com.urtcdemo.R;
import com.urtcdemo.adpter.RemoteVideoAdapter;
import com.urtcdemo.service.ForeGroundService;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.StatusBarUtils;
import com.urtcdemo.utils.ToastUtils;
import com.urtcdemo.utils.VideoProfilePopupWindow;
import com.urtcdemo.view.URTCVideoViewInfo;

import org.webrtc.ucloud.record.URTCRecordManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.renderer.SurfaceViewGroup;

import static com.cmcc.sdkengine.define.CMCCErrorCode.NET_ERR_CODE_OK;
import static com.cmcc.sdkengine.define.CMCCMediaType.MEDIA_TYPE_VIDEO;

/**
 * @author ciel
 * @create 2020/7/2
 * @Describe
 */
public class RtcLiveActivity extends AppCompatActivity
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

    CMCCRtcEngine sdkEngine = null;
    private CMCCChannelProfile mClass;
    private CMCCStreamInfo mLocalStreamInfo;
    private CMCCAudioDevice defaultAudioDevice;
    // private List<UCloudRtcSdkStreamInfo> mSteamList;
    private List<String> mResolutionOption = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private CMCCSurfaceViewRenderer mLocalVideoView = null; //Surfaceview
    //private UCloudRtcSdkSurfaceVideoView mLocalVideoView = null; //UCloudRtcSdkSurfaceVideoView
    private CMCCSurfaceViewGroup mMuteView = null;
    private CMCCMediaType mPublishMediaType;

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
    private CMCCStreamInfo latestRemoteInfo;
    private CMCCStreamInfo mSwapStreamInfo;
    //外部摄像数据读取
    private ByteBuffer videoSourceData = null;
    private final Object extendByteBufferSync = new Object();
    private boolean mIsLocalMixingSound = false;
    private boolean mIsRemoteMixingSound = false;
    private boolean mIsPauseMixingSound = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate" + this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        sdkEngine = CMCCRtcEngine.create(eventListener);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;

        // 界面初始化
        setContentView(R.layout.activity_living);
        mVideoProfileSelect = preferences.getInt(CommonUtils.videoprofile, CommonUtils.videoprofilesel);
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
        mLocalVideoView.setVisibility(View.INVISIBLE);
//        mLocalVideoView.setZOrderMediaOverlay(false);
//        mLocalVideoView.setMirror(true);
        mDrawer = findViewById(R.id.drawer_layout);
        mDrawer.setScrimColor(0x00ffffff);
        mDrawerContent = findViewById(R.id.drawer_content);
        mDrawerContent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.d(TAG, "onViewAttachedToWindow: "+ v + " activity "+ RtcLiveActivity.this);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.d(TAG, "onViewDetachedFromWindow: "+ v);
            }
        });
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

        // 用户配置参数获取
        mUserid = getIntent().getStringExtra("user_id");
        mRoomid = getIntent().getStringExtra("room_id");
        mRoomToken = getIntent().getStringExtra("token");
        mAppid = getIntent().getStringExtra("app_id");

        isScreenCaptureSupport = CMCCEnvHelper.isSupportScreenCapture();
        mCameraEnable = preferences.getBoolean(CommonUtils.CAMERA_ENABLE, CommonUtils.CAMERA_ON);
        mMicEnable = preferences.getBoolean(CommonUtils.MIC_ENABLE, CommonUtils.MIC_ON);
        mScreenEnable = preferences.getBoolean(CommonUtils.SCREEN_ENABLE, CommonUtils.SCREEN_OFF);
        int classType = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, CMCCChannelProfile.CHANNEL_PROFILE_COMMUNICATION.ordinal());
        mClass = CMCCChannelProfile.valueOf(classType);
        mPublishMode = preferences.getInt(CommonUtils.PUBLISH_MODE, CommonUtils.AUTO_MODE);
        mScribeMode = preferences.getInt(CommonUtils.SUBSCRIBE_MODE, CommonUtils.AUTO_MODE);

        mIsPriDeploy = preferences.getBoolean(CommonUtils.PRIVATISATION_MODE, false);
        CMCCEnvHelper.setPrivateDeploy(mIsPriDeploy);
        mPriAddr = preferences.getString(CommonUtils.PRIVATISATION_ADDRESS, "");
        if (mIsPriDeploy) {
            CMCCEnvHelper.setPrivateDeployRoomURL(mPriAddr);
        }

        mExtendCameraCapture = preferences.getBoolean(CommonUtils.CAMERA_CAPTURE_MODE, false);
        mExtendVideoFormat = preferences.getInt(CommonUtils.EXTEND_CAMERA_VIDEO_FORMAT, CommonUtils.i420_format);
        updateVideoFormat(mExtendVideoFormat);
        // mSteamList = new ArrayList<>();

        //房间号
        mTextRoomId = findViewById(R.id.roomid_text);
        mTextRoomId.setText("房间号:" + mRoomid);
        mMirror = CMCCEnvHelper.isFrontCameraMirror();
        mImgBtnMirror.setImageResource(mMirror ? R.mipmap.mirror_on :
                R.mipmap.mirror);
        //分辨率选择菜单
        String[] resolutions = getResources().getStringArray(R.array.videoResolutions);
        mResolutionOption.addAll(Arrays.asList(resolutions));

        //用户ID
        mTextUserId = findViewById(R.id.userid_text);
        mTextUserId.setText("用户ID:" + mUserid);
        mTextUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeShow();
            }
        });

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
            if (mScreenEnable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CMCCRtcEngine.regScreenCaptureNotification(mScreenCaptureNotification);
            }
        } else {
            sdkEngine.configLocalScreenPublish(false);
            mImgManualPubScreen.setVisibility(View.GONE);
            mTextManualPubScreen.setVisibility(View.GONE);
        }
        defaultAudioDevice = sdkEngine.getDefaultAudioDevice();
        if (defaultAudioDevice == CMCCAudioDevice.AUDIO_DEVICE_SPEAKER) {
            mImgBtnMuteSpeaker.setImageResource(R.mipmap.speaker);
            mSpeakerOn = true;
        } else {
            mSpeakerOn = false;
            mImgBtnMuteSpeaker.setImageResource(R.mipmap.speaker_off);
        }
        sdkEngine.setClientRole(CMCCClientRole.CLIENT_ROLE_BROADCASTER);
        sdkEngine.setChannelProfile(mClass);
        sdkEngine.setAutoPublish(mPublishMode == CommonUtils.AUTO_MODE ? true : false);
        sdkEngine.setAutoSubscribe(mScribeMode == CommonUtils.AUTO_MODE ? true : false);
        sdkEngine.setVideoEncoderConfiguration(CMCCVideoProfile.matchValue(mVideoProfileSelect));
        sdkEngine.setScreenProfile(CMCCVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_1920_1080);

        synchronized (extendByteBufferSync) {
            videoSourceData = sdkEngine.getNativeOpInterface().
                    createNativeByteBuffer(1280 * 720 * 4);
            videoSourceData.clear();
        }
        //分辨率菜单显示
        mTextResolution.setText(mResolutionOption.get(mVideoProfileSelect));
        mAdapter = new ArrayAdapter<String>(this, R.layout.videoprofile_item, mResolutionOption);

        mResolutionPopupWindow = new VideoProfilePopupWindow(this);
        mResolutionPopupWindow.setOnSpinnerItemClickListener(mOnResulutionOptionClickListener);
        if (mExtendCameraCapture) {
            //扩展摄像头方式
            CMCCEnvHelper.setCaptureMode(
                    CMCCCaptureMode.CAPTURE_MODE_EXTEND);
            mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
            CMCCRtcEngine.onRGBCaptureResult(mCMCCDataProvider);
            mTextResolution.setVisibility(View.GONE);
            mImgBtnSwitchCam.setVisibility(View.GONE);
        } else {
            CMCCEnvHelper.setCaptureMode(
                    CMCCCaptureMode.CAPTURE_MODE_LOCAL);
        }
        if (mPublishMode == CommonUtils.AUTO_MODE) {
            mImgPreview.setVisibility(View.GONE);
            mTextPreview.setVisibility(View.GONE);
            mImgControlMixSound.setVisibility(View.GONE);
            mTextControlMixSound.setVisibility(View.GONE);
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
                    sdkEngine.setClientRole(CMCCClientRole.CLIENT_ROLE_BROADCASTER);
                    List<Integer> results = new ArrayList<>();
                    StringBuffer errorMessage = new StringBuffer();
                    // 重新刷新配置
                    refreshSettings();
                    if (mCameraEnable || mMicEnable) {
                        if (!mVideoIsPublished) {
                            results.add(sdkEngine.publish(MEDIA_TYPE_VIDEO, mCameraEnable, mMicEnable).getErrorCode());
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
                        ToastUtils.shortShow(RtcLiveActivity.this, errorMessage.toString());
                    } else {
                        ToastUtils.shortShow(RtcLiveActivity.this, "发布");
                    }
                } else {
                    sdkEngine.unPublish(MEDIA_TYPE_VIDEO);
                }
            }
        });

        //手动发布屏幕
        mImgManualPubScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mScreenIsPublished) {
                    sdkEngine.setClientRole(CMCCClientRole.CLIENT_ROLE_BROADCASTER);
                    List<Integer> results = new ArrayList<>();
                    StringBuffer errorMessage = new StringBuffer();
                    // 重新刷新配置
                    refreshSettings();
                    if (mScreenEnable && !mScreenIsPublished) {
                        if (isScreenCaptureSupport) {
                            results.add(sdkEngine.publish(CMCCMediaType.MEDIA_TYPE_SCREEN, true, false).getErrorCode());
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
                        ToastUtils.shortShow(RtcLiveActivity.this, errorMessage.toString());
                    } else {
                        ToastUtils.shortShow(RtcLiveActivity.this, "发布");
                    }
                } else {
                    sdkEngine.unPublish(CMCCMediaType.MEDIA_TYPE_SCREEN);
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

        CMCCAuthInfo info = new CMCCAuthInfo();
        info.setAppId(mAppid);
        info.setToken(mRoomToken);
        info.setRoomId(mRoomid);
        info.setUId(mUserid);
        Log.d(TAG, " roomtoken = " + mRoomToken + "appid : "+ mAppid + " userid :"+ mUserid);
        initRecordManager();
        // 加入房间
        if (sdkEngine.joinChannel(info) == CMCCErrorCode.NET_ERR_SECKEY_NULL
                || mAppid.length() == 0) {
            ToastUtils.shortShow(RtcLiveActivity.this, "加入房间失败，AppKey或AppId没有设置");
            endCall();
        }
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        synchronized (mSync) {
            if (mUSBMonitor != null) {
                mUSBMonitor.register();
            }
            if (mUVCCamera != null) {
                mUVCCamera.startPreview();
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
        synchronized (mSync) {
            if (mUVCCamera != null) {
                //mUVCCamera.stopPreview();
            }
            if (mUSBMonitor != null) {
                mUSBMonitor.unregister();
            }
        }
        Log.d(TAG, "on Stop");
        if (mVideoIsPublished || mScreenIsPublished) {
            if (!mLeaveRoomFlag) {
                Intent service = new Intent(this, ForeGroundService.class);
                startService(service);
            }
//            sdkEngine.disableAudio();
//            if (!mExtendCameraCapture) {
//                sdkEngine.enableLocalVideo(false);
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Intent service = new Intent(this, ForeGroundService.class);
        stopService(service);
//        sdkEngine.enableAudio();
//        if (!mExtendCameraCapture) {
//            sdkEngine.enableLocalVideo(true);
//        }
        synchronized (mSync) {
            if (mUSBMonitor != null) {
                mUSBMonitor.register();
            }
            if (mUVCCamera != null) {
                //mUVCCamera.startPreview();
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
        Intent service = new Intent(this, ForeGroundService.class);
        stopService(service);
//        endCall();
        releaseExtendCamera();
        //onMediaServerDisconnect();
        System.gc();
    }

    private ICMCCRtcEngineEventHandler eventListener = new ICMCCRtcEngineEventHandler() {
        @Override
        public void onServerDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onServerDisconnect: ");
                    ToastUtils.shortShow(RtcLiveActivity.this, " 服务器已断开");
                    stopTimeShow();
                    onMediaServerDisconnect();
                }
            });
        }

        @Override
        public void onJoinChannelSuccess(String joinChannel, String userId) {
            runOnUiThread(() -> {
                ToastUtils.shortShow(RtcLiveActivity.this, " 加入房间成功");
                startTimeShow();
            });
        }

        @Override
        public void onError(int error, String msg) {
            //to do leave room
//            runOnUiThread(() -> {
//                ToastUtils.shortShow(UCloudRTCLiveActivity.this, " 加入房间失败 " +
//                        error + " errmsg " + msg);
//                Intent intent = new Intent(UCloudRTCLiveActivity.this, ConnectActivity.class);
//                onMediaServerDisconnect();
//                startActivity(intent);
//                finish();
//            });
        }

        @Override
        public void onLeaveChannel(int code, String msg, String roomId) {
            // 离开房间回调结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "on leave Channel");
                    ToastUtils.shortShow(RtcLiveActivity.this, " 离开房间 " +
                            code + " errMsg " + msg);
//                    releaseExtendCamera();
//                    onMediaServerDisconnect();
//                    System.gc();
                }
            });
        }

        @Override
        public void onConnectionLost(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "rejoining room");
                    ToastUtils.shortShow(RtcLiveActivity.this, " 服务器重连中…… ");
                    stopTimeShow();
                }
            });
        }

        @Override
        public void onRejoinChannelSuccess(String roomId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RtcLiveActivity.this, "服务器重连成功");
                    startTimeShow();
                }
            });
        }

        @Override
        public void onLocalPublish(int code, String msg, CMCCStreamInfo info) {
            // 发布本地流回调结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        int mediatype = info.getMediaType().ordinal(); // 获取媒体类型（音视频流或桌面流）
                        mPublishMediaType = CMCCMediaType.matchValue(mediatype);
                        if (mediatype == MEDIA_TYPE_VIDEO.ordinal()) { // 音视频流
                            mImgManualPubVideo.setImageResource(R.mipmap.stop); // 修改界面图标
                            mTextManualPubVideo.setText(R.string.pub_cancel_video); // 修改界面文字
                            mVideoIsPublished = true;
                            if (!sdkEngine.isAudioOnlyMode()) {  // 非单音频流
                                // UCloudRtcSdkSurfaceVideoView打开
                                //mLocalVideoView.init(false);
                                // Surfaceview打开

                                // 回显view布局设置
                                mLocalVideoView.setBackgroundColor(Color.TRANSPARENT);
                                mLocalVideoView.setVisibility(View.VISIBLE);
                                // 获取当前横竖屏模式
                                if (RtcLiveActivity.this.getResources().getConfiguration().orientation
                                        == Configuration.ORIENTATION_LANDSCAPE) {
                                    Log.i("info", "landscape"); // 横屏
                                    localViewWidth_landscape = mLocalVideoView.getMeasuredWidth();
                                    localViewHeight_landscape = mLocalVideoView.getMeasuredHeight();
                                    localViewWidth_portrait = screenWidth;
                                    localViewHeight_portrait = screenHeight - mToolBar.getHeight() - mTitleBar.getHeight();
                                }
                                else if (RtcLiveActivity.this.getResources().getConfiguration().orientation
                                        == Configuration.ORIENTATION_PORTRAIT) {
                                    Log.i("info", "portrait"); // 竖屏
                                    localViewWidth_portrait = mLocalVideoView.getMeasuredWidth();
                                    localViewHeight_portrait = mLocalVideoView.getMeasuredHeight();
                                    localViewWidth_landscape = screenHeight;
                                    localViewHeight_landscape = screenWidth - mTitleBar.getHeight() - mToolBar.getHeight();
                                }

                                if (!mIsPreview) {
                                    if (mExtendCameraCapture) { // 扩展摄像头开启渲染
                                        sdkEngine.setupLocalVideo(info,
                                                mLocalVideoView, CMCCScaleType.SCALE_ASPECT_FIT, null);
                                    } else { // 自带摄像头开启渲染
                                        sdkEngine.setupLocalVideo(info,
                                                mLocalVideoView, CMCCScaleType.SCALE_ASPECT_FIT, null);
                                    }
                                    //if (mPublishMode != CommonUtils.AUTO_MODE) {
                                    // setIconStats(true);
                                    //}
                                } else {
                                    //setIconStats(true);
                                }
                                // 状态记录
                                mLocalStreamInfo = info;
                                mSwapStreamInfo = info;
                                mLocalVideoView.setTag(mLocalStreamInfo);
                                mLocalVideoView.setOnClickListener(mToggleScreenOnClickListener);
                            }
                        } else if (mediatype == CMCCMediaType.MEDIA_TYPE_SCREEN.ordinal()) { // 屏幕流
                            // 状态记录及界面更新
                            mScreenIsPublished = true;
                            mImgManualPubScreen.setImageResource(R.mipmap.stop);
                            mTextManualPubScreen.setText(R.string.pub_cancel_screen);
                            if (mScreenEnable) { // 屏幕流一般无需渲染
                                //sdkEngine.startPreview(info.getMediaType(), mLocalVideoView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL,null);
                            }
                        }
                        // 界面图标状态设置
                        setIconStats(true);
                    } else {
                        ToastUtils.shortShow(RtcLiveActivity.this,
                                "发布视频失败 " + code + " errmsg " + msg);
                    }

                }
            });
        }

        @Override
        public void onLocalUnPublish(int code, String msg, CMCCStreamInfo info) {
            // 取消发布回调结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        if (info.getMediaType() == MEDIA_TYPE_VIDEO) { // 音视频流
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
                            mLocalVideoView.setVisibility(View.INVISIBLE);
                            if (mIsLocalMixingSound) {
                                toggleMixingSound(false);
                            }
                            if (mIsRemoteMixingSound) {
                                toggleMixingSound(true);
                            }
                        } else if (info.getMediaType() == CMCCMediaType.MEDIA_TYPE_SCREEN) { //屏幕流
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
                            setIconStats(false);
                            setPreview(false);
                            mIsPreview = false;
                            mTextPreview.setText(R.string.start_preview);
                        }
                        ToastUtils.shortShow(RtcLiveActivity.this, "取消发布成功");
                    } else {
                        ToastUtils.shortShow(RtcLiveActivity.this, "取消发布失败 "
                                + code + " errmsg " + msg);
                    }
                }
            });
        }

        @Override
        public void onUserJoined(String uid) {
            // 远端用户加入房间
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RtcLiveActivity.this, " 用户 "
                            + uid + " 加入房间 ");
                }
            });
        }

        @Override
        public void onUserOffline(String uid) {
            // 远端用户离开房间
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "remote user " + uid );
                    //onUserLeave(uid);
                    ToastUtils.shortShow(RtcLiveActivity.this, " 用户 " +
                            uid );
                }
            });
        }

        @Override
        public void onRemotePublish(CMCCStreamInfo info) {
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
        public void onRemoteUnPublish(CMCCStreamInfo info) {
            // 远端用户取消发布
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, " onRemoteUnPublish " + info.getMediaType() + " " + info.getUId());
                    ToastUtils.shortShow(RtcLiveActivity.this, " 用户 " +
                            info.getUId() + " 取消媒体流 " + info.getMediaType()); // 界面提示信息
                    String mkey = info.getUId() + info.getMediaType().toString();
                    if(mSwapStreamInfo!= null && mSwapStreamInfo.getUId().equals(info.getUId()) && mSwapStreamInfo.getMediaType().toString().equals(info.getMediaType().toString())){
                        sdkEngine.stopRemoteView(mSwapStreamInfo); // 停止渲染远端视频流
                        int localIndex  = mVideoAdapter.getPositionByKey(mUserid + mPublishMediaType.toString());
                        if(localIndex >= 0){
                            Log.d(TAG," onRemoteUnPublish localIndex "+ localIndex);
                            mkey = mUserid + mPublishMediaType.toString();
                            sdkEngine.stopPreview(mPublishMediaType);
                            sdkEngine.setupLocalVideo(mLocalStreamInfo,mLocalVideoView, CMCCScaleType.SCALE_ASPECT_FILL,null);
                            mSwapStreamInfo = mLocalStreamInfo;
                        }
                    }else{
                        sdkEngine.stopRemoteView(info);
                    }
                    if (mVideoAdapter != null) {
                        mVideoAdapter.removeStreamView(mkey);
                    }

                    //mSpinnerPopupWindowScribe.removeStreamInfoByUid(info.getUId());
                    //refreshStreamInfoText();
                }
            });
        }

        @Override
        public void onSubscribeResult(int code, String msg, CMCCStreamInfo info) {
            // 订阅结果回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) { // 订阅成功
                        URTCVideoViewInfo vinfo = new URTCVideoViewInfo();
                        CMCCSurfaceViewGroup videoView = null;
                        // UCloudRtcSdkSurfaceVideoView videoViewCallBack = null; // 用于外部扩展输出

                        //UCloudRtcRenderView videoView = null;
                        Log.d(TAG, " subscribe info: " + info);
                        latestRemoteInfo = info;
                        if (info.isHasVideo()) { // 订阅流是否包含视频
//                            UCloudRtcSdkSurfaceVideoView 定义的viewgroup,URTCVideoViewInfo
                            videoView = new CMCCSurfaceViewGroup(getApplicationContext());
                            CMCCSurfaceViewRenderer surfaceViewRenderer = new CMCCSurfaceViewRenderer(getApplicationContext());
                            videoView.init(false, new int[]{R.mipmap.video_open, R.mipmap.loudspeaker, R.mipmap.video_close, R.mipmap.loudspeaker_disable, R.drawable.publish_layer}, mOnRemoteOpTrigger, new int[]{R.id.remote_video, R.id.remote_audio},surfaceViewRenderer);
                            // UCloudRtcRenderView
                            //videoView = new UCloudRtcRenderView(getApplicationContext());// 初始化渲染界面
                            //videoView.init();
                            videoView.setTag(info);
                            videoView.setId(R.id.video_view);
                            //外部扩展输出，和默认输出二选一
                            videoView.setFrameCallBack(mDataReceiver);
                            //videoViewCallBack.init(false);
                            //远端截图
                            //videoView.setOnClickListener(mScreenShotOnClickListener);
                            //设置交换
                            videoView.setOnClickListener(mSwapRemoteLocalListener);
                        }
                        vinfo.setmRenderview(videoView);
                        vinfo.setmUid(info.getUId());
                        vinfo.setmMediatype(info.getMediaType());
                        vinfo.setmEanbleVideo(info.isHasVideo());
                        vinfo.setEnableAudio(info.isHasAudio());
                        String mkey = info.getUId() + info.getMediaType().toString();
                        vinfo.setKey(mkey);
                        //默认输出，和外部输出代码二选一
                        if (mVideoAdapter != null) {
                            vinfo.setStreamInfo(info);
                            mVideoAdapter.addStreamView(mkey, vinfo, info);
                        }

                        if (videoView != null) {
                            sdkEngine.setupRemoteVideo(info, videoView, CMCCScaleType.SCALE_ASPECT_FIT, null); // 渲染订阅流
                            //videoView.refreshRemoteOp(View.VISIBLE);
                        }
                        //if (videoViewCallBack != null) {
                            // sdkEngine.startRemoteView(info, videoViewCallBack, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null); // 渲染订阅流，同时从回调输出
                        //}
                        //如果订阅成功就删除待订阅列表中的数据
                        //mSpinnerPopupWindowScribe.removeStreamInfoByUid(info.getUId());
                        //refreshStreamInfoText();
                    } else {
                        ToastUtils.shortShow(RtcLiveActivity.this, " 订阅用户  " +
                                info.getUId() + " 流 " + info.getMediaType() + " 失败 " +
                                " code " + code + " msg " + msg);
                    }
                }
            });
        }

        @Override
        public void onUnSubscribeResult(int code, String msg, CMCCStreamInfo info) {
            // 取消订阅结果回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RtcLiveActivity.this, " 取消订阅用户 " +
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
        public void onLocalStreamMuteRsp(int code, String msg, CMCCMediaType mediaType, CMCCTrackType trackType, boolean mute) {
            // 静音本地流回调
            Log.d(TAG, " code " + code + " mediatype " + mediaType + " ttype " + trackType + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) { // mute成功，更新界面
                        if (mediaType == MEDIA_TYPE_VIDEO) {
                            if (trackType == CMCCTrackType.TRACK_TYPE_AUDIO) {
                                onMuteMicResult(mute);
                            } else if (trackType == CMCCTrackType.TRACK_TYPE_VIDEO) {
                                onMuteVideoResult(mute);
                            }
                        } else if (mediaType == CMCCMediaType.MEDIA_TYPE_SCREEN) {
                            onMuteVideoResult(mute);
                        }
                    }
                }
            });
        }

        @Override
        public void onRemoteStreamMuteRsp(int code, String msg, String uid, CMCCMediaType mediatype, CMCCTrackType tracktype, boolean mute) {
            // 静音远端流回调
            Log.d(TAG, " code " + code + " uid " + uid + " mediatype " + mediatype + " ttype " + tracktype + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {// mute成功，更新界面
                        String mkey = uid + mediatype.toString();
                        Log.d(TAG, " onRemoteStreamMuteRsp " + mkey + " " + mVideoAdapter);
                        if (tracktype == CMCCTrackType.TRACK_TYPE_AUDIO) {
                            mRemoteAudioMute = mute;
                            if (mMuteView != null) {
                                mMuteView.refreshRemoteAudio(mute);
                            }
                        } else if (tracktype == CMCCTrackType.TRACK_TYPE_VIDEO) {
                            mRemoteVideoMute = mute;
                            if (mMuteView != null) {
                                mMuteView.refreshRemoteVideo(mute);
                            }
                        }

                    } else {
                        ToastUtils.shortShow(RtcLiveActivity.this, "mute " + mediatype + "failed with code: " + code);
                    }
                }
            });
        }

        @Override
        public void onRemoteTrackNotify(String uid, CMCCMediaType mediatype, CMCCTrackType tracktype, boolean mute) {
            // 远端流状态改变通知
            Log.d(TAG, " uid " + uid + " mediatype " + mediatype + " ttype " + tracktype + " mute " + mute);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 更新界面和界面提醒
                    if (mediatype == MEDIA_TYPE_VIDEO) {
                        String cmd = mute ? "关闭" : "打开";
                        if (tracktype == CMCCTrackType.TRACK_TYPE_AUDIO) {
                            ToastUtils.shortShow(RtcLiveActivity.this, " 用户 " +
                                    uid + cmd + " 麦克风");
                        } else if (tracktype == CMCCTrackType.TRACK_TYPE_VIDEO) {
                            ToastUtils.shortShow(RtcLiveActivity.this, " 用户 " +
                                    uid + cmd + " 摄像头");
                        }

                    } else if (mediatype == CMCCMediaType.MEDIA_TYPE_SCREEN) {
                        String cmd = mute ? "关闭" : "打开";
                        ToastUtils.shortShow(RtcLiveActivity.this, " 用户 " +
                                uid + cmd + " 桌面流");
                    }
                }
            });
        }

        @Override
        public void onSendStreamStatus(CMCCStreamStatus streamStatus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // localprocess.setProgress(volume);
                }
            });
        }

        @Override
        public void onRemoteStreamStatus(CMCCStreamStatus rtstats) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //localprocess.setProgress(volume);
                }
            });
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
                        String mkey = uid + MEDIA_TYPE_VIDEO.toString();
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
                    ToastUtils.longShow(RtcLiveActivity.this, " 被踢出会议 code " +
                            code);
                    Log.d(TAG, " user kickoff reason " + code);
                    Intent intent = new Intent(RtcLiveActivity.this, ConnectActivity.class);
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
                    if (error == CMCCErrorCode.NET_ERR_SDP_SWAP_FAIL.ordinal()) {
                        ToastUtils.shortShow(RtcLiveActivity.this, "sdp swap failed");
                    }
                }
            });
        }

        @Override
        public void onQueryMix(int code, String msg, int type, String mixId, String fileName) {
            Log.d(TAG, "onQueryMix: "+ code + " msg: "+ msg + " type: "+ type);
        }

        @Override
        public void onRecordStatusNotify(CMCCMediaServiceStatus status, int code, String msg, String userId, String roomId, String mixId, String fileName) {
            // 录制状态通知
            Log.d(TAG, "onRecordStatusNotify " + status + " code: " + code + " msg: " + msg + " userid " + userId + " roomid: " + roomId + " mixId: " + mixId + "fileName: " + fileName);
            if(status == CMCCMediaServiceStatus.RECORD_STATUS_START_REQUEST_SEND){ // 录制请求已送出
                Log.d(TAG, "开始录制请求已发送: ");
            }
            else if (status == CMCCMediaServiceStatus.RECORD_STATUS_START) { // 录制已经开始
                String videoPath = "http://" + mBucket + "." + mRegion + ".ufileos.com/" + fileName; // 录制观看地址
                Log.d(TAG, "remote record path: " + videoPath + ".mp4");
                // 界面提醒和更新
                ToastUtils.longShow(RtcLiveActivity.this, "观看地址: " + videoPath);
                mIsRemoteRecording = true;
                mImgRemoteRecord.setImageResource(R.mipmap.stop);
                mTextRemoteRecord.setText(R.string.remote_recording);
                if (mAtomOpStart)
                    mAtomOpStart = false;
            } else if (status == CMCCMediaServiceStatus.RECORD_STATUS_STOP_REQUEST_SEND) {
                if (mIsRemoteRecording) {
                    mIsRemoteRecording = false;
                    mImgRemoteRecord.setImageResource(R.mipmap.remote_record);
                    mTextRemoteRecord.setText(R.string.start_remote_record);
                }
            } else if (status == CMCCMediaServiceStatus.STATUS_UPDATE_REQUEST_SEND) {
                Log.d(TAG, "update 更新参数请求已发送: ");
            } else if (status == CMCCMediaServiceStatus.STATUS_UPDATE_ADD_STREAM_SUCCESS) {
                Log.d(TAG, "update 加流成功: ");
            } else {
                ToastUtils.longShow(RtcLiveActivity.this, "录制异常: 原因：" + code);
            }
        }

        @Override
        public void onRelayStatusNotify(CMCCMediaServiceStatus status, int code, String msg, String userId, String roomId, String mixId, String[] pushUrls) {
            // 转推状态通知
            Log.d(TAG, "onRelayStatusNotify " + status + " code: " + code + " msg: " + msg + " userid " + userId + " roomid: " + roomId + " mixId: " + mixId);
            if (pushUrls != null) {
                for (int i = 0; i < pushUrls.length; i++) {
                    Log.d(TAG, "onRelayStatusNotify: pushUrl " + pushUrls[i]); // 转推地址
                }
            }
            if(status == CMCCMediaServiceStatus.RELAY_STATUS_START_REQUEST_SEND){
                Log.d(TAG, "开始转推请求已发送: ");
            }
            else if (status == CMCCMediaServiceStatus.RELAY_STATUS_START) { // 开始转推
                // ulive cdn watch address: http://rtchls.ugslb.com/rtclive/roomid.flv
                // 界面更新和提醒
                mIsMixing = true;
                mImgMix.setImageResource(R.mipmap.stop);
                mTextMix.setText(R.string.mixing);
                if (mAtomOpStart)
                    mAtomOpStart = false;
            } else if (status == CMCCMediaServiceStatus.RELAY_STATUS_STOP_REQUEST_SEND) {
                if (mIsMixing) {
                    mIsMixing = false;
                    mImgMix.setImageResource(R.mipmap.mix);
                    mTextMix.setText(R.string.start_mix);
                }
            }
            else if (status == CMCCMediaServiceStatus.STATUS_UPDATE_REQUEST_SEND) {
                Log.d(TAG, "update 更新参数请求已发送: ");
            } else if (status == CMCCMediaServiceStatus.STATUS_UPDATE_ADD_STREAM_SUCCESS) {
                Log.d(TAG, "update 加流成功: ");
            } else {
                ToastUtils.longShow(RtcLiveActivity.this, "转推异常: 原因：" + code);
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
        public void onMessageNotify(int code, String msg) {
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
        public void onServerBroadcastMessage(String uid, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onServerBroadCastMsg: uid: " + uid + "msg: " + msg);
                }
            });
        }

        @Override
        public void onAudioRouteChanged(CMCCAudioDevice device) {
            // 播放声音设备切换
            defaultAudioDevice = device;
//            URTCLogUtils.d(TAG,"URTCAudioManager: room change device to "+ defaultAudioDevice);
            if (defaultAudioDevice == CMCCAudioDevice.AUDIO_DEVICE_SPEAKER) {
                mImgBtnMuteSpeaker.setImageResource(R.mipmap.speaker);
                mSpeakerOn = true;
            } else {
                mSpeakerOn = false;
                mImgBtnMuteSpeaker.setImageResource(R.mipmap.speaker_off);
            }
        }

        @Override
        public void onPeerLostConnection(int type, CMCCStreamInfo info) {
            Log.d(TAG, "onPeerLostConnection: type: " + type + "info: " + info);
        }

        @Override
        public void onNetWorkQuality(String userId, CMCCStreamType streamType, CMCCMediaType mediaType, CMCCNetWorkQuality quality) {
            // 网络质量通知
            Log.d(TAG, "onNetWorkQuality: userid: " + userId + "streamType: " + streamType + "mediatype : " + mediaType + " quality: " + quality);
        }

        @Override
        public void onAudioFileFinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onAudioFileFinish" );

                    if ( mIsLocalMixingSound){
                        // 本地混音中
                        mImgLocalMixSound.setImageResource(R.mipmap.local_mix_sound);
                        mTextLocalMixSound.setText(R.string.start_local_mix_sound);
                        if (mVideoIsPublished) {
                            mImgRemoteMixSound.setVisibility(View.VISIBLE);
                            mTextRemoteMixSound.setVisibility(View.VISIBLE);
                        }
                        mIsLocalMixingSound = false;
                    }
                    else if (mIsRemoteMixingSound) {
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

    private CMCCSurfaceViewGroup.RemoteOpTrigger mOnRemoteOpTrigger = new CMCCSurfaceViewGroup.RemoteOpTrigger() {
        @Override
        public void onRemoteVideo(View v, SurfaceViewGroup parent) {
            if (parent.getTag(R.id.swap_info) != null) {
                CMCCStreamInfo swapStreamInfo = (CMCCStreamInfo) parent.getTag(R.id.swap_info);
                sdkEngine.muteRemoteVideoStream(swapStreamInfo.getUId(), !mRemoteVideoMute);
            } else if (parent.getTag() != null) {
                CMCCStreamInfo streamInfo = (CMCCStreamInfo) parent.getTag();
                sdkEngine.muteRemoteVideoStream(streamInfo.getUId(), !mRemoteVideoMute);
            }
            mMuteView = (CMCCSurfaceViewGroup)parent;
        }

        @Override
        public void onRemoteAudio(View v, SurfaceViewGroup parent) {
            if (parent.getTag(R.id.swap_info) != null) {
                CMCCStreamInfo swapStreamInfo = (CMCCStreamInfo) parent.getTag(R.id.swap_info);
                sdkEngine.muteRemoteAudioStream(swapStreamInfo.getUId(), !mRemoteAudioMute);
            } else if (parent.getTag() != null) {
                CMCCStreamInfo streamInfo = (CMCCStreamInfo) parent.getTag();
                sdkEngine.muteRemoteAudioStream(streamInfo.getUId(), !mRemoteAudioMute);
            }
            mMuteView = (CMCCSurfaceViewGroup)parent;
        }
    };

    private RemoteVideoAdapter.RemoveRemoteStreamReceiver mRemoveRemoteStreamReceiver = new RemoteVideoAdapter.RemoveRemoteStreamReceiver() {
        @Override
        public void onRemoteStreamRemoved(boolean swaped) {
            if (swaped) {
                if (mClass == CMCCChannelProfile.CHANNEL_PROFILE_COMMUNICATION) {
                    sdkEngine.stopPreview(mLocalStreamInfo.getMediaType());
                    sdkEngine.setupLocalVideo(mLocalStreamInfo, mLocalVideoView, null, null);
                } else if (mLocalVideoView.getTag(R.id.swap_info) != null) {
                    CMCCStreamInfo remoteStreamInfo = (CMCCStreamInfo) mLocalVideoView.getTag(R.id.swap_info);
                    sdkEngine.stopRemoteView(remoteStreamInfo);
                }
            }
        }
    };

    ICMCCRecordListener mLocalRecordListener = new ICMCCRecordListener() {
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
            Log.v(TAG, "onAttach:");
            ToastUtils.shortShow(RtcLiveActivity.this, "USB摄像头已连接");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        if (mUSBMonitor != null) {
                            if (mUSBMonitor.getDeviceCount() > 0) {
                                mUSBMonitor.requestPermission(device);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            Log.v(TAG, "onConnect:");
            synchronized (mSync) {
                if (mUVCCamera != null) {
                    mUVCCamera.destroy();
                }
                isActive = isPreview = false;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        //final UVCCamera camera = initUVCCamera(ctrlBlock);
                        mUVCCamera = initUVCCamera(ctrlBlock);
                        isActive = true;
                        isPreview = true;
                    }
                }
            });
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            Log.v(TAG, "onDisconnect:");
            // XXX you should check whether the comming device equal to camera device that currently using
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        if (mUVCCamera != null) {
                            mUVCCamera.stopPreview();
                            mUVCCamera.close();
                            mUVCCamera.destroy();
/*                            if (mPreviewSurface != null) {
                                mPreviewSurface.release();
                                mPreviewSurface = null;
                            }*/
                            isActive = isPreview = false;
                        }
                    }
                }
            });
        }

        @Override
        public void onDetach(final UsbDevice device) {
            Log.v(TAG, "onDetach:");
            ToastUtils.shortShow(RtcLiveActivity.this, "USB摄像头被移除");
        }

        @Override
        public void onCancel(final UsbDevice device) {
        }
    };

    private View.OnClickListener mSwapRemoteLocalListener = new View.OnClickListener() { // 大小窗切换监听
        @Override
        public void onClick(View v) {
            if (v instanceof CMCCSurfaceViewGroup) {
                CMCCStreamInfo clickStreamInfo = (CMCCStreamInfo) v.getTag();
                boolean swapLocal = mSwapStreamInfo.getUId().equals(mUserid);
                boolean clickLocal = clickStreamInfo.getUId().equals(mUserid);
                Log.d(TAG, "mSwapStreamInfo: "+ mSwapStreamInfo + " clickInfo: " + clickStreamInfo);
                Log.d(TAG, "onClick swaplocal"+ swapLocal + " clickLocal: " + clickLocal);
                if(swapLocal && !clickLocal){
                    sdkEngine.stopRemoteView(clickStreamInfo);
                    sdkEngine.stopPreview(mSwapStreamInfo.getMediaType());
//                        sdkEngine.renderLocalView(mSwapStreamInfo, v,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                    CMCCSurfaceViewRenderer remoteRender = (CMCCSurfaceViewRenderer)v.getTag(R.id.render);
                    sdkEngine.setupLocalVideo(mSwapStreamInfo, remoteRender, CMCCScaleType.SCALE_ASPECT_FILL, null);
//                        sdkEngine.startRemoteView(clickStreamInfo, mLocalVideoView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL,null);
                    sdkEngine.setupRemoteVideo(clickStreamInfo, mLocalVideoView, CMCCScaleType.SCALE_ASPECT_FILL,null);
                        ((CMCCSurfaceViewGroup) v).refreshRemoteOp(View.INVISIBLE);
                }else if(!swapLocal && clickLocal){
                    sdkEngine.stopRemoteView(mSwapStreamInfo);
                    sdkEngine.stopPreview(clickStreamInfo.getMediaType());
                    CMCCSurfaceViewRenderer remoteRender = (CMCCSurfaceViewRenderer)v.getTag(R.id.render);
                    sdkEngine.setupLocalVideo(clickStreamInfo, mLocalVideoView, CMCCScaleType.SCALE_ASPECT_FILL,null);
                    sdkEngine.setupRemoteVideo(mSwapStreamInfo, remoteRender, CMCCScaleType.SCALE_ASPECT_FILL,null);
                        ((CMCCSurfaceViewGroup) v).refreshRemoteOp(View.VISIBLE);
                }else if(!swapLocal && !clickLocal){
                    sdkEngine.stopRemoteView(mSwapStreamInfo);
                    sdkEngine.stopRemoteView(clickStreamInfo);
                    sdkEngine.setupRemoteVideo(clickStreamInfo, mLocalVideoView, CMCCScaleType.SCALE_ASPECT_FILL,null);
                    CMCCSurfaceViewRenderer remoteRender = (CMCCSurfaceViewRenderer)v.getTag(R.id.render);
                    sdkEngine.setupRemoteVideo(mSwapStreamInfo, remoteRender, CMCCScaleType.SCALE_ASPECT_FILL,null);
                    }
                v.setTag(mSwapStreamInfo);
                mVideoAdapter.updateSwapInfo(clickStreamInfo,mSwapStreamInfo);
                mSwapStreamInfo = clickStreamInfo;
            }
        }
    };

    private void switchCamera() { // 前后置摄像头切换
        sdkEngine.switchCamera();
        ToastUtils.shortShow(this, "切换摄像头");
        mSwitchCamera = !mSwitchCamera;
    }

    private boolean muteMic() { // 关闭打开本端麦克风
        sdkEngine.muteLocalAudioStream(!mMuteMic);
        if (!mMuteMic) {
            ToastUtils.shortShow(RtcLiveActivity.this, "关闭麦克风");
        } else {
            ToastUtils.shortShow(RtcLiveActivity.this, "打开麦克风");
        }
        return false;
    }

    public static int testPic = 0;
    public static int testLimit = 2;

    private boolean muteVideo() { // 关闭打开本端视频
        if (mScreenEnable || mCameraEnable) {
            if (isScreenCaptureSupport && !mCameraEnable) {
                sdkEngine.muteLocalVideoStream(!mMuteVideo, CMCCMediaType.MEDIA_TYPE_SCREEN);
            } else {
                sdkEngine.muteLocalVideoStream(!mMuteVideo, MEDIA_TYPE_VIDEO);
            }
        }
        if (!mMuteVideo) {
            ToastUtils.shortShow(RtcLiveActivity.this, "关闭摄像头");
        } else {
            ToastUtils.shortShow(RtcLiveActivity.this, "打开摄像头");
        }
        return false;
    }

    private void muteSpeaker(boolean enable) { //喇叭听筒切换
        if (mSpeakerOn) {
            ToastUtils.shortShow(RtcLiveActivity.this, "关闭喇叭");
        } else {
            ToastUtils.shortShow(RtcLiveActivity.this, "打开喇叭");
        }
        mSpeakerOn = !mSpeakerOn;
        sdkEngine.setEnableSpeakerphone(enable);
        mImgBtnMuteSpeaker.setImageResource(enable ? R.mipmap.speaker : R.mipmap.speaker_off);
    }

    private void onMuteVideoResult(boolean mute) {
        mMuteVideo = mute;
        mImgBtnMuteVideo.setImageResource(mMuteVideo ? R.mipmap.camera_off :
                R.mipmap.camera);
        if (mLocalVideoView.getTag(R.id.swap_info) != null) {
            CMCCStreamInfo remoteInfo = (CMCCStreamInfo) mLocalVideoView.getTag(R.id.swap_info);
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
        CMCCEnvHelper.setFrontCameraMirror(mMirror);
        mImgBtnMirror.setImageResource(mMirror ? R.mipmap.mirror_on :
                R.mipmap.mirror);
    }

    private void endCall() { // 离开频道
        sdkEngine.leaveChannel().ordinal();
        mLeaveRoomFlag = true;
//        Intent intent = new Intent(UCloudRTCLiveActivity.this, ConnectActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        releaseExtendCamera();
//        onMediaServerDisconnect();
//        startActivity(intent);
        finish();
        Log.d(TAG, "endCall: finish called");
    }

    private void onMediaServerDisconnect() {
        //mLocalVideoView.release();
        clearGridItem();
        Log.d(TAG, "onMediaServerDisconnect: destroy start");
        CMCCRtcEngine.destroy();
        Log.d(TAG, "onMediaServerDisconnect: destroy finish");
    }

    private void clearGridItem() {
        mVideoAdapter.clearAll();
        mVideoAdapter.notifyDataSetChanged();
    }

    private void releaseExtendCamera() { // 释放扩展摄像头资源
        synchronized (mSync) {
            isActive = isPreview = false;
            if (mUVCCamera != null) {
                mUVCCamera.stopPreview();
                mUVCCamera.close();
                mUVCCamera = null;
            }
            if (mUSBMonitor != null) {
                mUSBMonitor.destroy();
                mUSBMonitor = null;
            }
        }
//        mVideoAdapter.setRemoveRemoteStreamReceiver(null);
        if (mCMCCDataProvider != null) {
            mCMCCDataProvider.releaseBuffer();
            mCMCCDataProvider = null;
        }
        if (mDataReceiver != null) {
            mDataReceiver.releaseBuffer();
            mDataReceiver = null;
        }
    }

    private void startTimeShow() {
        timeShow.setBase(SystemClock.elapsedRealtime());
        timeShow.start();
    }

    private void stopTimeShow() {
        timeShow.stop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            int tempScreen = 0;
            FrameLayout.LayoutParams params = null;
            // 呼唤全屏参数
            tempScreen = screenHeight;
            screenHeight = screenWidth;
            screenWidth = tempScreen;

            if (mLocalViewFullScreen) {
                if (mLocalVideoView.getScaleType() == CMCCScaleType.SCALE_ASPECT_FIT.ordinal()) {
                    mLocalVideoView.resetSurface();
                }
                else {
                    params = new FrameLayout.LayoutParams(screenWidth, screenHeight + mToolBar.getHeight());
                    params.setMargins(0, 0, 0, 0);
                    mLocalVideoView.setLayoutParams(params);
                }
            } else {
                if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (mLocalVideoView.getScaleType() == CMCCScaleType.SCALE_ASPECT_FIT.ordinal()) {
                        mLocalVideoView.resetSurface();
                    }
                    else {
                        params = new FrameLayout.LayoutParams(localViewWidth_portrait, localViewHeight_portrait);
                        params.setMargins(0, mTitleBar.getHeight(), 0, mToolBar.getHeight());
                        mLocalVideoView.setLayoutParams(params);
                        Log.d(TAG, "PORTRAIT screen. localViewWidth: " + localViewWidth_portrait + " localViewHeight: " + localViewHeight_portrait);
                    }
                }
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (mLocalVideoView.getScaleType() == CMCCScaleType.SCALE_ASPECT_FIT.ordinal()) {
                        mLocalVideoView.resetSurface();
                    }
                    else {
                        params = new FrameLayout.LayoutParams(localViewWidth_landscape, localViewHeight_landscape);
                        params.setMargins(0, mTitleBar.getHeight(), 0, mToolBar.getHeight());
                        mLocalVideoView.setLayoutParams(params);
                        Log.d(TAG, "LANDSCAPE screen. localViewWidth: " + localViewWidth_landscape + " localViewHeight: " + localViewHeight_landscape);
                    }
                }
            }
            }
        }, 50);
/*        int tempScreen = 0;
        FrameLayout.LayoutParams params = null;
        // 呼唤全屏参数
        tempScreen = screenHeight;
        screenHeight = screenWidth;
        screenWidth = tempScreen;

        if (mLocalViewFullScreen) {
            params = new FrameLayout.LayoutParams(screenWidth, screenHeight + mToolBar.getHeight());
            params.setMargins(0, 0, 0, 0);
            mLocalVideoView.setLayoutParams(params);
        } else {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                params = new FrameLayout.LayoutParams(localViewWidth_portrait, localViewHeight_portrait);
                params.setMargins(0, mTitleBar.getHeight(), 0, mToolBar.getHeight());
                mLocalVideoView.setLayoutParams(params);
                Log.d(TAG, "PORTRAIT screen. localViewWidth: " + localViewWidth_portrait + " localViewHeight: " + localViewHeight_portrait);
            }
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                params = new FrameLayout.LayoutParams(localViewWidth_landscape, localViewHeight_landscape);
                params.setMargins(0, mTitleBar.getHeight(), 0, mToolBar.getHeight());
                mLocalVideoView.setLayoutParams(params);
                Log.d(TAG, "LANDSCAPE screen. localViewWidth: " + localViewWidth_landscape + " localViewHeight: " + localViewHeight_landscape);
            }
        }*/
    }

    public void toggleFullScreen() {
        FrameLayout.LayoutParams params = null;

        if (!mLocalViewFullScreen) {
            setSystemUIVisible(false);
            //隐藏顶部标题和底部工具栏
            mTitleBar.setVisibility(View.GONE);
            mToolBar.setVisibility(View.GONE);
            StatusBarUtils.removeStatusView(this);

            if (mLocalVideoView.getScaleType() == CMCCScaleType.SCALE_ASPECT_FIT.ordinal()) {
                mLocalVideoView.resetSurface();
            }
            else {
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
            if (RtcLiveActivity.this.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                params = new FrameLayout.LayoutParams(localViewWidth_landscape, localViewHeight_landscape);
            }
            else {
                params = new FrameLayout.LayoutParams(localViewWidth_portrait, localViewHeight_portrait);
            }
            if (mLocalVideoView.getScaleType() == CMCCScaleType.SCALE_ASPECT_FIT.ordinal()) {
                mLocalVideoView.resetSurface();
            }
            else {
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

    private CMCCScreenShot mCMCCScreenShot = new CMCCScreenShot() { // 本地截图
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
            ToastUtils.shortShow(RtcLiveActivity.this, "screen shoot : " + name);
        }
    };

    private void addScreenShotCallBack(View view) {
        if (view instanceof CMCCSurfaceViewGroup) {
            ((CMCCSurfaceViewGroup) view).setScreenShotBack(mCMCCScreenShot);
        } else if (view instanceof CMCCSurfaceViewRenderer) {
            ((CMCCSurfaceViewRenderer) view).setScreenShotBack(mCMCCScreenShot);
        }
    }

    //初始化视频录制
    private void initRecordManager() {
        URTCRecordManager.init("");
        Log.d(TAG, "initRecordManager: cache path:" + URTCRecordManager.getVideoCachePath());
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
            CMCCMixProfile mixProfile = (CMCCMixProfile)CMCCMixProfile.getInstance().assembleRecordMixParamsBuilder()
                    .type(CMCCMixProfile.MIX_TYPE_RECORD)
                    //画面模式
                    .layout(CMCCMixProfile.LAYOUT_AVERAGE_1)
                    //画面分辨率
                    .resolution(1280, 720)
                    //背景色
                    .bgColor(0, 0, 0)
                    //画面帧率
                    .frameRate(15)
                    //画面码率
                    .bitRate(1000)
                    //h264视频编码
                    .videoCodec(CMCCMixProfile.VIDEO_CODEC_H264)
                    //编码质量
                    .qualityLevel(CMCCMixProfile.QUALITY_H264_CB)
                    //音频编码
                    .audioCodec(CMCCMixProfile.AUDIO_CODEC_AAC)
                    //主讲人ID
                    .mainViewUserId(mUserid)
                    //主讲人媒体类型
                    .mainViewMediaType(MEDIA_TYPE_VIDEO.ordinal())
                    //加流方式手动
                    .addStreamMode(CMCCMixProfile.ADD_STREAM_MODE_AUTO)
                    //添加流列表，也可以后续调用MIX_TYPE_UPDATE 动态添加
                    .addStream(mUserid, MEDIA_TYPE_VIDEO.ordinal())
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
            CMCCMixProfile mixProfile = (CMCCMixProfile)CMCCMixProfile.getInstance().assembleUpdateMixParamsBuilder()
                    .type(CMCCMixProfile.MIX_TYPE_RELAY)
                    //画面模式
                    .layout(CMCCMixProfile.LAYOUT_CLASS_ROOM_2)
                    //画面分辨率
                    .resolution(1280, 720)
                    //背景色
                    .bgColor(0, 0, 0)
                    //画面帧率
                    .frameRate(15)
                    //画面码率
                    .bitRate(1000)
                    //h264视频编码
                    .videoCodec(CMCCMixProfile.VIDEO_CODEC_H264)
                    //编码质量
                    .qualityLevel(CMCCMixProfile.QUALITY_H264_CB)
                    //音频编码
                    .audioCodec(CMCCMixProfile.AUDIO_CODEC_AAC)
                    //主讲人ID
                    .mainViewUserId(mUserid)
                    //主讲人媒体类型
                    .mainViewMediaType(MEDIA_TYPE_VIDEO.ordinal())
                    //加流方式手动
                    .addStreamMode(CMCCMixProfile.ADD_STREAM_MODE_MANUAL)
                    //添加流列表，也可以后续调用MIX_TYPE_UPDATE 动态添加
                    .addStream(mUserid, MEDIA_TYPE_VIDEO.ordinal())
                    //设置转推cdn 的地址
                    .addPushUrl("rtmp://rtcpush.ugslb.com/rtclive/" + mRoomid)
                    //关键用户
                    .keyUser(mUserid)
                    //流上限
                    .layoutUserLimit(2)
                    //房间没流多久结束任务
                    .taskTimeOut(70)
                    .build();
            sdkEngine.startRelay(mixProfile); // 开始转推
        } else if (!mAtomOpStart) {
            Log.d(TAG, " stop mix: ");
            mAtomOpStart = true;
            sdkEngine.stopRelay(null); // 停止转推
        }
    }

    private void update(int type) {
        Log.d(TAG, " start update: ");
        CMCCMixProfile mixProfile = (CMCCMixProfile)CMCCMixProfile.getInstance().assembleMixParamsBuilder()
                .type(type)
                //画面模式
                .layout(CMCCMixProfile.LAYOUT_CLASS_ROOM_2)
                //画面分辨率
                .resolution(1280, 720)
                //背景色
                .bgColor(0, 0, 0)
                //画面帧率
                .frameRate(15)
                //画面码率
                .bitRate(1000)
                //h264视频编码
                .videoCodec(CMCCMixProfile.VIDEO_CODEC_H264)
                //编码质量
                .qualityLevel(CMCCMixProfile.QUALITY_H264_CB)
                //音频编码
                .audioCodec(CMCCMixProfile.AUDIO_CODEC_AAC)
                //主讲人ID
                .mainViewUserId(mUserid)
                //主讲人媒体类型
                .mainViewMediaType(MEDIA_TYPE_VIDEO.ordinal())
                //加流方式手动
                .addStreamMode(CMCCMixProfile.ADD_STREAM_MODE_MANUAL)
                //添加流列表，也可以后续调用MIX_TYPE_UPDATE 动态添加
                .addStream(mUserid, MEDIA_TYPE_VIDEO.ordinal())
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
                }
                else {
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
        }
        else if (mVideoIsPublished && isRemotePlay){
            // 本地+远端混音
            if (!mIsRemoteMixingSound) {
                if (!sdkEngine.startPlayAudioFile(
                        //"/sdcard/light.mp3",
                        sdkEngine.copyAssetsFileToSdcard("water.mp3"),
                        true, false)) {
                    return;
                }
                else {
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
        }
        else {
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
            sdkEngine.changePushResolution(CMCCVideoProfile.matchValue(mVideoProfileSelect));
            mTextResolution.setText(mResolutionOption.get(mVideoProfileSelect));
            mResolutionPopupWindow.dismiss();
        }
    };

    private UVCCamera initUVCCamera(USBMonitor.UsbControlBlock ctrlBlock) { // usb外接摄像头初始化
        Log.d(TAG, "initUVCCamera-----mVideoProfileSelect:" + mVideoProfileSelect + " width:" + CMCCVideoProfile.matchValue(mVideoProfileSelect).getWidth()
                + " height:" + CMCCVideoProfile.matchValue(mVideoProfileSelect).getHeight());
        final UVCCamera camera = new UVCCamera();
        camera.open(ctrlBlock);
        camera.setPreviewSize(
                CMCCVideoProfile.matchValue(mVideoProfileSelect).getWidth(),
                CMCCVideoProfile.matchValue(mVideoProfileSelect).getHeight(),
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
/*                Log.d("UCloudRTCLiveActivity", "onFrame byteBuffer, frame.position: " + frame.position()
                                + " frame.limit: " + frame.limit());*/
                createFrameByteBuffer(frame);
            }
        }, mUVCCameraFormat);
        return camera;
    }

    //外置数据输入监听
    private CMCCDataProvider mCMCCDataProvider = new CMCCDataProvider() {
        private ByteBuffer cacheBuffer;

        @Override
        public ByteBuffer provideRGBData(List<Integer> params) {
            if (videoSourceData == null ) {
                Log.d("UCloudRTCLiveActivity", "provideRGBData byteBuffer data is null");
                return null;
            } else {
                //Log.d("UCloudRTCLiveActivity", "provideRGBData: ! = null");
/*                Log.d("UCloudRTCLiveActivity", "provideRGBData byteBuffer, videoSourceData.position: " + videoSourceData.position()
                        + " videoSourceData.limit: " + videoSourceData.limit());*/
                params.add(mURTCVideoFormat);
                params.add(CMCCVideoProfile.matchValue(mVideoProfileSelect).getWidth());
                params.add(CMCCVideoProfile.matchValue(mVideoProfileSelect).getHeight());
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

                //cacheBuffer.position(0);
                cacheBuffer.flip();

                return cacheBuffer;
            }
        }

        @Override
        public void releaseBuffer() { // 释放资源
            Log.d("UCloudRTCLiveActivity", "releaseBuffer");
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
    private CMCCDataReceiver mDataReceiver = new CMCCDataReceiver() {
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
            return CMCCDataReceiver.I420_TO_ABGR;
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
    private CMCCNotification mScreenCaptureNotification = new CMCCNotification() {
        @Override
        public Notification createNotificationChannel() {
            Notification.Builder builder = new Notification.Builder(getApplicationContext()); //获取一个Notification构造器
            Intent nfIntent = new Intent(getApplicationContext(), RtcLiveActivity.class); //点击后跳转的界面，可以设置跳转数据

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
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
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
                mURTCVideoFormat = CMCCDataProvider.NV21;
                break;
            case CommonUtils.nv12_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_YUV420SP;
                mURTCVideoFormat = CMCCDataProvider.NV12;
                break;
            case CommonUtils.i420_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_I420;
                mURTCVideoFormat = CMCCDataProvider.I420;
                break;
            case CommonUtils.rgba_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_RGBX;
                mURTCVideoFormat = CMCCDataProvider.RGBA_TO_I420;
                break;
            case CommonUtils.argb_format:
                //UVCCamera不支持输出argb格式，测试用rgbx格式，输出时颜色会有偏差
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_ARGB;
                mURTCVideoFormat = CMCCDataProvider.ARGB_TO_I420;
                break;
            case CommonUtils.rgb24_format:
                //UVCCamera的RGB888与libyuv的数据有大小端区别，所以UVCCamera输出使用BGR888,保证颜色正确
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_BGR888;
                mURTCVideoFormat = CMCCDataProvider.RGB24_TO_I420;
                break;
            case CommonUtils.rgb565_format:
                mUVCCameraFormat = UVCCamera.PIXEL_FORMAT_RGB565;
                mURTCVideoFormat = CMCCDataProvider.RGB565_TO_I420;
                break;
        }
    }

    private void setPreview(boolean onOff) { //预览窗口开关
        if (onOff) {
            if (mExtendCameraCapture) {
                sdkEngine.startPreview(
                        mLocalVideoView, CMCCScaleType.SCALE_ASPECT_FIT, null);
            } else {
                sdkEngine.startPreview(mLocalVideoView, CMCCScaleType.SCALE_ASPECT_FIT, null);
            }
        } else {
            sdkEngine.stopPreview(MEDIA_TYPE_VIDEO);
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

    private void refreshSettings() { // 配置刷新
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);

        mCameraEnable = preferences.getBoolean(CommonUtils.CAMERA_ENABLE, CommonUtils.CAMERA_ON);
        mMicEnable = preferences.getBoolean(CommonUtils.MIC_ENABLE, CommonUtils.MIC_ON);
        mScreenEnable = preferences.getBoolean(CommonUtils.SCREEN_ENABLE, CommonUtils.SCREEN_OFF);
        int classType = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, CMCCChannelProfile.CHANNEL_PROFILE_COMMUNICATION.ordinal());
        mClass = CMCCChannelProfile.valueOf(classType);
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

