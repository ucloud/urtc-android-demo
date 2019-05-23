package com.urtcdemo.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.urtcdemo.R;
import com.urtcdemo.adpter.RemoteVideoAdapter;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.ToastUtils;
import com.urtcdemo.mode.URTCVideoViewInfo;
import com.urtclib.sdkengine.UCloudRtcSdkEnv;
import com.urtclib.sdkengine.UCloudRtcSdkEngine;
import com.urtclib.sdkengine.define.UCloudRtcSdkAudioDevice;
import com.urtclib.sdkengine.define.UCloudRtcSdkAuthInfo;
import com.urtclib.sdkengine.define.UCloudRtcSdkErrorCode;
import com.urtclib.sdkengine.define.UCloudRtcSdkMediatype;
import com.urtclib.sdkengine.listener.UCloudRtcSdkEventListener;
import com.urtclib.sdkengine.define.UCloudRtcSdkScaleType;
import com.urtclib.sdkengine.define.UCloudRtcSdkStats;
import com.urtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.urtclib.sdkengine.define.UCloudRtcSdkStreamRole;
import com.urtclib.sdkengine.define.UCloudRtcSdkTrackType;
import com.urtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView ;
import com.urtclib.sdkengine.define.UCloudRtcSdkVideoProfile;


public class RoomActivity extends AppCompatActivity {
    private static final String TAG = "RoomActivity" ;
    private String mUserid = "ucloud" ;
    private String mRoomid = "urtc" ;
    private String mRoomToken = "" ;
    private String mAppid = "" ;

    TextView title = null ;
    UCloudRtcSdkSurfaceVideoView localrenderview = null ;
    ProgressBar localprocess = null ;

    final int COL_SIZE_P = 3;
    final int COL_SIZE_L = 6;
    private GridLayoutManager gridLayoutManager;
    private RemoteVideoAdapter mVideoAdapter;
    RecyclerView mRemoteGridView = null;

    UCloudRtcSdkEngine sdkEngine = null ;

    ImageButton mHangup = null ;
    ImageButton mSwitchcam = null ;
    ImageButton mMuteMic = null ;
    ImageButton mLoudSpkeader = null ;
    ImageButton mMuteCam = null ;
    int mCaptureMode ;
    int mVideoProfile ;
    boolean isScreenCaptureSupport ;

    Chronometer timeshow ;

    UCloudRtcSdkEventListener eventListener = new UCloudRtcSdkEventListener() {
        @Override
        public void onServerDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RoomActivity.this, " 服务器已断开");
                    stopTimeShow();
                    onMediaServerDisconnect() ;
                }
            });
        }

        @Override
        public void onJoinRoomResult(int code, String msg, String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        ToastUtils.shortShow(RoomActivity.this, " 加入房间成功");
                        startTimeShow();
                    }else {
                        ToastUtils.shortShow(RoomActivity.this, " 加入房间失败 "+
                                code +" errmsg "+ msg);
                    }

                }
            });
        }

        @Override
        public void onLeaveRoomResult(int code, String msg, String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RoomActivity.this, " 离开房间 "+
                            code +" errmsg "+ msg);
                    Intent intent = new Intent(RoomActivity.this, ConnectActivity.class);
                    onMediaServerDisconnect() ;
                    startActivity(intent) ;
                    finish();
                }
            });
        }

        @Override
        public void onRejoiningRoom(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RoomActivity.this, " 服务器重连中…… ");
                    stopTimeShow();
                }
            });
        }

        @Override
        public void onRejoinRoomResult(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RoomActivity.this, "服务器重连成功");
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
                        ToastUtils.shortShow(RoomActivity.this, "发布视频成功");
                        int mediatype = info.getmMediatype().ordinal() ;
                        if (mediatype == UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO.ordinal()) {
                            if (!sdkEngine.isAudioOnlyMode()) {
                                localrenderview.setBackgroundColor(Color.TRANSPARENT);
                                sdkEngine.startPreview(info.getmMediatype(),
                                        localrenderview);
                            }

                        } else if (mediatype == UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_SCREEN.ordinal()) {
                            if (mCaptureMode == CommonUtils.screen_capture_mode) {
                                sdkEngine.startPreview(info.getmMediatype(), localrenderview) ;
                            }
                        }

                    }else {
                        ToastUtils.shortShow(RoomActivity.this,
                                "发布视频失败 "+code +" errmsg "+ msg);
                    }

                }
            });
        }

        @Override
        public void onLocalUnPublish(int code, String msg, UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (info.getmMediatype() == UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO) {
                        if (localrenderview != null) {
                            localrenderview.refrush();
                        }
                    }else if (info.getmMediatype() == UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_SCREEN){
                        if (mCaptureMode == CommonUtils.screen_capture_mode) {
                            if (localrenderview != null) {
                                localrenderview.refrush();
                            }
                        }
                    }

                    if (code == 0) {
                        ToastUtils.shortShow(RoomActivity.this, "取消发布视频成功");
                    }else {
                        ToastUtils.shortShow(RoomActivity.this, "取消发布视频失败 "
                        +code +" errmsg "+ msg);
                    }

                }
            });
        }

        @Override
        public void onRemoteUserJoin(String uid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RoomActivity.this, " 用户 "
                            + uid +" 加入房间 ");
                }
            }) ;

        }

        @Override
        public void onRemoteUserLeave(String uid, int reson) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "uid "+ uid) ;
                    onUserLeave(uid) ;
                    ToastUtils.shortShow(RoomActivity.this, " 用户 "+
                            uid +" 离开房间，离开原因： "+ reson);
                }
            }) ;
        }

        @Override
        public void onRemotePublish(UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!sdkEngine.isAutoPublish()) {
                        sdkEngine.subscribe(info) ;
                    }
                }
            }) ;
        }

        @Override
        public void onRemoteUnPublish(UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, " onRemoteUnPublish "+ info.getmMediatype() +" "+info.getmUid()) ;
                    ToastUtils.shortShow(RoomActivity.this, " 用户 "+
                            info.getmUid() +" 取消媒体流 "+ info.getmMediatype());
                    String mkey = info.getmUid()+ info.getmMediatype().toString() ;

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
                        URTCVideoViewInfo vinfo = new URTCVideoViewInfo(null);
                        UCloudRtcSdkSurfaceVideoView videoView = null ;
                        Log.d(TAG, " info. "+info.getmUid() + " hasvideo"+ info.ismHasvideo() );
                        if (info.ismHasvideo()) {
                            videoView = new UCloudRtcSdkSurfaceVideoView(getApplicationContext()) ;
                            videoView.init();
                            videoView.setScalingType(UCloudRtcSdkScaleType.URTC_SCALE_ASPECT_FIT);
                            vinfo.setmRenderview(videoView);
                        }
                        vinfo.setmUid(info.getmUid());
                        vinfo.setmMediatype(info.getmMediatype());
                        vinfo.setmEanbleVideo(info.ismHasvideo());
                        String mkey = info.getmUid() + info.getmMediatype().toString();
                        if (mVideoAdapter != null ) {
                            mVideoAdapter.addStreamView(mkey, vinfo);
                        }
                        if (vinfo != null && videoView != null) {
                            sdkEngine.startRemoteView(info, videoView);
                        }

                    } else {
                        ToastUtils.shortShow(RoomActivity.this, " 订阅用户  " +
                                info.getmUid() + " 流 " + info.getmMediatype() + " 失败 " +
                                " code " + code + " msg " + msg);
                    }
                }
            }) ;
        }

        @Override
        public void onUnSubscribeResult(int code, String msg, UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RoomActivity.this, " 取消订阅用户 "+
                            info.getmUid() + " 类型 "+ info.getmMediatype());
                    if (mVideoAdapter != null) {
                        mVideoAdapter.removeStreamView(info.getmUid()+info.getmMediatype().toString());
                    }
                }
            });
        }

        @Override
        public void onLocalStreamMuteRsp(int code, String msg, UCloudRtcSdkMediatype mediatype, UCloudRtcSdkTrackType tracktype, boolean mute) {
            Log.d(TAG, " code " + code + " mediatype "+ mediatype + " ttype "+ tracktype + " mute "+ mute) ;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        if (mediatype == UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO) {
                            if (tracktype == UCloudRtcSdkTrackType.URTC_SDK_TRACK_TYPE_AUDIO) {
                                onMuteMicResult(mute);
                            }else if (tracktype == UCloudRtcSdkTrackType.URTC_SDK_TRACK_TYPE_VIDEO){
                                onMuteCamResult(mute);
                            }
                        }else if (mediatype == UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_SCREEN) {
                            onMuteCamResult(mute);
                        }
                    }
                }
            });
        }

        @Override
        public void onRemoteStreamMuteRsp(int code, String msg, String uid, UCloudRtcSdkMediatype mediatype, UCloudRtcSdkTrackType tracktype, boolean mute) {
            Log.d(TAG, " code " + code + " uid "+ uid+ " mediatype "+ mediatype + " ttype "+ tracktype + " mute "+ mute) ;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        String mkey = uid+mediatype.toString() ;
                        Log.d(TAG, " onRemoteStreamMuteRsp " + mkey +" "+ mVideoAdapter) ;
                        if (tracktype == UCloudRtcSdkTrackType.URTC_SDK_TRACK_TYPE_AUDIO) {
                            if (mVideoAdapter != null) {
                            }
                        }else if (tracktype == UCloudRtcSdkTrackType.URTC_SDK_TRACK_TYPE_VIDEO) {
                            if (mVideoAdapter != null) {
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onRemoteTrackNotify(String uid, UCloudRtcSdkMediatype mediatype, UCloudRtcSdkTrackType tracktype, boolean mute) {
            Log.d(TAG, " uid "+ uid+ " mediatype "+ mediatype + " ttype "+ tracktype + " mute "+ mute) ;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediatype == UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO) {
                        String cmd = mute? "关闭":"打开" ;
                        if (tracktype == UCloudRtcSdkTrackType.URTC_SDK_TRACK_TYPE_AUDIO) {
                            ToastUtils.shortShow(RoomActivity.this, " 用户 "+
                                    uid +cmd+" 麦克风");
                        }else if (tracktype == UCloudRtcSdkTrackType.URTC_SDK_TRACK_TYPE_VIDEO) {
                            ToastUtils.shortShow(RoomActivity.this, " 用户 "+
                                    uid +cmd+" 摄像头");
                        }

                    }else if (mediatype == UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_SCREEN) {
                        String cmd = mute? "关闭":"打开" ;
                        ToastUtils.shortShow(RoomActivity.this, " 用户 "+
                                uid +cmd+" 桌面流");
                    }
                }
            });
        }

        @Override
        public void onSendRTCStats(UCloudRtcSdkStats rtstats) {

        }

        @Override
        public void onRemoteRTCStats(UCloudRtcSdkStats rtstats) {

        }

        @Override
        public void onLocalAudioLevel(int volume) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    localprocess.setProgress(volume);
                }
            });
        }

        @Override
        public void onRemoteAudioLevel(String uid, int volume) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                      if (mVideoAdapter != null) {
                          String mkey = uid+UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO.toString() ;
                      }
                }
            });
        }

        @Override
        public void onKickoff(int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.longShow(RoomActivity.this, " 被踢出会议 code "+
                            code);
                    Log.d(TAG, " user kickoff reason "+ code) ;
                    Intent intent = new Intent(RoomActivity.this, ConnectActivity.class);
                    onMediaServerDisconnect();
                    startActivity(intent) ;
                    finish();
                }
            });
        }

        @Override
        public void onWarning(int warn) {

        }

        @Override
        public void onError(int error) {

        }
    };

    private void onUserLeave(String uid) {
        if (mVideoAdapter != null) {
            mVideoAdapter.removeStreamView(uid+UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO);
            mVideoAdapter.removeStreamView(uid+UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_SCREEN);
        }
    }

    private void onMediaServerDisconnect() {
        localrenderview.release();
        clearGridItem() ;
        UCloudRtcSdkEngine.destory();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_room);
        timeshow = findViewById(R.id.timer) ;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        mCaptureMode = preferences.getInt(CommonUtils.capture_mode, CommonUtils.camera_capture_mode) ;
        mVideoProfile = preferences.getInt(CommonUtils.videoprofile, CommonUtils.videoprofilesel) ;
        mRemoteGridView = findViewById(R.id.remoteGridView);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, COL_SIZE_L);
        }else {
            gridLayoutManager = new GridLayoutManager(this, COL_SIZE_P);
        }
        mRemoteGridView.setLayoutManager(gridLayoutManager);
        mVideoAdapter = new RemoteVideoAdapter(this, null);
        mRemoteGridView.setAdapter(mVideoAdapter);
        sdkEngine = UCloudRtcSdkEngine.createEngnine(eventListener) ;

        mUserid = getIntent().getStringExtra("user_id");
        mRoomid = getIntent().getStringExtra("room_id");
//        mRoomToken = getIntent().getStringExtra("token") ;
//        mAppid = getIntent().getStringExtra("app_id") ;
        mAppid = preferences.getString(CommonUtils.APPID_KEY, CommonUtils.APP_ID) ;

        mHangup = findViewById(R.id.button_call_disconnect) ;
        mSwitchcam = findViewById(R.id.button_call_switch_camera) ;
        mMuteMic = findViewById(R.id.button_call_toggle_mic) ;
        mLoudSpkeader = findViewById(R.id.button_call_loundspeaker) ;
        mMuteCam = findViewById(R.id.button_call_toggle_cam) ;

        mHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callHangUp();
            }
        });

        mSwitchcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        mMuteMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleMic() ;
            }
        });

        mLoudSpkeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoudSpeaker(!mSpeakerOn);
            }
        });

        mMuteCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleCamera() ;
            }
        });

        title =  findViewById(R.id.titlebar) ;
        title.setText("roomid: "+mRoomid+"\nuid: "+ mUserid);

        localrenderview = findViewById(R.id.localview) ;
        localrenderview.init();
        localrenderview.setScalingType(UCloudRtcSdkScaleType.URTC_SCALE_ASPECT_FIT);
        localrenderview.setZOrderMediaOverlay(false);
        localrenderview.setMirror(false);

        localprocess =  findViewById(R.id.processlocal) ;
        isScreenCaptureSupport = UCloudRtcSdkEnv.isSuportScreenCapture() ;
        Log.d(TAG, " mCaptureMode " + mCaptureMode) ;
        switch (mCaptureMode) {
            case CommonUtils.audio_capture_mode :
                sdkEngine.setAudioOnlyMode(true) ;
                sdkEngine.configLocalCameraPublish(false) ;
                sdkEngine.configLocalAudioPublish(true) ;
                sdkEngine.configLocalScreenPublish(false) ;
                break;
            case CommonUtils.camera_capture_mode :
                sdkEngine.configLocalCameraPublish(true) ;
                sdkEngine.configLocalAudioPublish(true) ;
                sdkEngine.configLocalScreenPublish(false) ;
                break;
            case CommonUtils.screen_capture_mode :
                if (isScreenCaptureSupport) {
                    sdkEngine.configLocalScreenPublish(true) ;
                    sdkEngine.configLocalCameraPublish(false) ;
                    sdkEngine.configLocalAudioPublish(false) ;
                }else {
                    sdkEngine.configLocalCameraPublish(true) ;
                    sdkEngine.configLocalAudioPublish(true) ;
                    sdkEngine.configLocalScreenPublish(false) ;
                }
                break;
            case CommonUtils.screen_Audio_mode:
                if (isScreenCaptureSupport) {
                    sdkEngine.configLocalScreenPublish(true) ;
                    sdkEngine.configLocalCameraPublish(false) ;
                    sdkEngine.configLocalAudioPublish(true) ;
                }else {
                    sdkEngine.configLocalScreenPublish(false) ;
                    sdkEngine.configLocalCameraPublish(false) ;
                    sdkEngine.configLocalAudioPublish(true) ;
                }
                break;
            case CommonUtils.multi_capture_mode:
                if (isScreenCaptureSupport) {
                    sdkEngine.configLocalScreenPublish(true) ;
                    sdkEngine.configLocalCameraPublish(true) ;
                    sdkEngine.configLocalAudioPublish(true) ;
                }else {
                    sdkEngine.configLocalScreenPublish(false) ;
                    sdkEngine.configLocalCameraPublish(true) ;
                    sdkEngine.configLocalAudioPublish(true) ;
                }
                break;

        }

        sdkEngine.setAudioDevice(UCloudRtcSdkAudioDevice.URTC_SDK_AUDIODEVICE_NONE);
        defaultAudioDevice = sdkEngine.getDefaultAudioDevice() ;
        if (defaultAudioDevice == UCloudRtcSdkAudioDevice.URTC_SDK_AUDIODEVICE_SPEAKER) {
            mLoudSpkeader.setImageResource(R.mipmap.loudspeaker);
            mSpeakerOn = true ;
        }else {
            mSpeakerOn = false ;
            mLoudSpkeader.setImageResource(R.mipmap.loudspeaker_disable);
        }
        sdkEngine.setStreamRole(UCloudRtcSdkStreamRole.URTC_SDK_STREAM_ROLE_BOTH);
        sdkEngine.setAutoPublish(true) ;
        sdkEngine.setAutoSubscribe(true) ;
        sdkEngine.setVideoProfile(UCloudRtcSdkVideoProfile.matchValue(mVideoProfile)) ;

        UCloudRtcSdkAuthInfo info = new UCloudRtcSdkAuthInfo() ;
        info.setmAppId(mAppid);
        info.setmToken(mRoomToken);
        info.setmRoomid(mRoomid);
        info.setmUid(mUserid);
        int ret = sdkEngine.joinChannel(info) ;
        if (ret != UCloudRtcSdkErrorCode.NET_ERR_CODE_OK.ordinal()) {
            ToastUtils.shortShow(getApplicationContext(), "加入房间失败 errorcode "+ ret);
            Intent intent = new Intent(RoomActivity.this, ConnectActivity.class);
            onMediaServerDisconnect();
            startActivity(intent) ;
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "activity destory") ;
        super.onDestroy();
        localrenderview.release();
        clearGridItem() ;
        UCloudRtcSdkEngine.destory();
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    private void callHangUp() {
       int ret = sdkEngine.leaveChannel() ;
       if (ret != UCloudRtcSdkErrorCode.NET_ERR_CODE_OK.ordinal()) {
           Intent intent = new Intent(RoomActivity.this, ConnectActivity.class);
           onMediaServerDisconnect();
           startActivity(intent) ;
           finish();
       }
    }

    boolean mSwitchCam = false ;
    private void switchCamera() {
        sdkEngine.switchCamera() ;
        mSwitchcam.setImageResource(mSwitchCam? R.mipmap.camera_switch_front :
                R.mipmap.camera_switch_end);
        mSwitchCam = !mSwitchCam ;
    }

    boolean mMuteMicBool = false ;
    private boolean onToggleMic() {
        sdkEngine.muteLocalMic(!mMuteMicBool) ;
        return false;
    }

    boolean mMuteCamBool = false ;
    private boolean onToggleCamera() {
        if (mCaptureMode == CommonUtils.camera_capture_mode) {
            sdkEngine.muteLocalVideo(!mMuteCamBool, UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO) ;
        }else if (mCaptureMode == CommonUtils.screen_capture_mode) {
            if (isScreenCaptureSupport) {
                sdkEngine.muteLocalVideo(!mMuteCamBool, UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_SCREEN) ;
            }else {
                sdkEngine.muteLocalVideo(!mMuteCamBool, UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO) ;
            }
        }else if (mCaptureMode == CommonUtils.multi_capture_mode) {
            sdkEngine.muteLocalVideo(!mMuteCamBool, UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_VIDEO) ;
        }

        return false;
    }

    private void onMuteCamResult(boolean mute) {
        mMuteCamBool = mute ;
        mMuteCam.setImageResource(mute ? R.mipmap.video_close : R.mipmap.video_open);
        if (mute) {
            localrenderview.refrush();
            localrenderview.setVisibility(View.INVISIBLE);
        }else {
            localrenderview.setVisibility(View.VISIBLE);
        }
    }

    private void onMuteMicResult(boolean mute) {
        mMuteMicBool = mute ;
        mMuteMic.setImageResource(mute ? R.mipmap.microphone_disable : R.mipmap.microphone);
    }

    boolean mSpeakerOn = true ;
    UCloudRtcSdkAudioDevice defaultAudioDevice ;
    private void onLoudSpeaker(boolean enable) {
        mSpeakerOn = !mSpeakerOn ;
        sdkEngine.setSpeakerOn(enable);
        mLoudSpkeader.setImageResource(enable ? R.mipmap.loudspeaker : R.mipmap.loudspeaker_disable);
    }

    private void clearGridItem() {
        mVideoAdapter.clearAll();
        mVideoAdapter.notifyDataSetChanged();
    }

    private void startTimeShow() {
        timeshow.setBase(SystemClock.elapsedRealtime());
        timeshow.start();
    }

    private void stopTimeShow() {
        timeshow.stop();
    }

}
