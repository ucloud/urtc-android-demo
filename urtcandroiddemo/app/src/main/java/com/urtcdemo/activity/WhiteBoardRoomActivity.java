package com.urtcdemo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.herewhite.sdk.AbstractRoomCallbacks;
import com.herewhite.sdk.Room;
import com.herewhite.sdk.RoomParams;
import com.herewhite.sdk.WhiteBroadView;
import com.herewhite.sdk.WhiteSdk;
import com.herewhite.sdk.WhiteSdkConfiguration;
import com.herewhite.sdk.domain.BroadcastState;
import com.herewhite.sdk.domain.DeviceType;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.RoomPhase;
import com.herewhite.sdk.domain.RoomState;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.SceneState;
import com.herewhite.sdk.domain.ViewMode;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkAudioDevice;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkAuthInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkErrorCode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRoomType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkScaleType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStats;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamRole;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkTrackType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkVideoProfile;
import com.ucloudrtclib.sdkengine.listener.UCloudRtcSdkEventListener;
import com.urtcdemo.R;
import com.urtcdemo.adpter.RemoteVideoAdapter;
import com.urtcdemo.listener.WhiteBoardEventListener;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.ToastUtils;
import com.urtcdemo.utils.UiHelper;
import com.urtcdemo.view.CustomerClickListener;
import com.urtcdemo.view.SteamScribePopupWindow;
import com.urtcdemo.view.URTCVideoViewInfo;
import com.urtcdemo.whiteboard.RemoteAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN;
import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO;


public class WhiteBoardRoomActivity extends AppCompatActivity {
    private static final String TAG = "WhiteBoardRoomActivity";
    private String mUserid = "test001";
    private String mRoomid = "urtc1";
    private String mRoomToken = "test token";
    private String mAppid = "";

    TextView title = null;
    UCloudRtcSdkSurfaceVideoView localrenderview = null;
    ProgressBar localprocess = null;

    final int COL_SIZE_P = 2;
    final int COL_SIZE_L = 6;
    private GridLayoutManager gridLayoutManager;
    private RemoteVideoAdapter mVideoAdapter;
    RecyclerView mRemoteGridView = null;

    UCloudRtcSdkEngine sdkEngine = null;
    private WhiteSdk whiteSdk;

//    ImageButton mPublish = null;
    ImageButton mHangup = null;
    ImageButton mShowHideVideo;
//    ImageButton mSwitchcam = null;
//    ImageButton mMuteMic = null;
//    ImageButton mLoudSpkeader = null;
//    ImageButton mMuteCam = null;
    private SteamScribePopupWindow mSpinnerPopupWindowScribe;
    private View mStreamSelect;
    private TextView mTextStream;
    int mCaptureMode;
    int mVideoProfile;
    @CommonUtils.PubScribeMode
    int mPublishMode;
    @CommonUtils.PubScribeMode
    int mScribeMode;
    UCloudRtcSdkStreamRole mRole;
    private int mWhiteRole;
    private String mWhiteUUId;
    UCloudRtcSdkRoomType mClass;
    boolean isScreenCaptureSupport;
    private List<UCloudRtcSdkStreamInfo> mSteamList;
    private UCloudRtcSdkStreamInfo mLocalStreamInfo;
    private boolean mRemoteVideoMute;
    private boolean mRemoteAudioMute;
    private String mLocalVideoKey;
    private String mLocalScreenKey;
    Chronometer timeshow;


    private UCloudRtcSdkSurfaceVideoView.RemoteOpTrigger mOnRemoteOpTrigger = new UCloudRtcSdkSurfaceVideoView.RemoteOpTrigger() {
        @Override
        public void onRemoteVideo(View v, UCloudRtcSdkSurfaceVideoView parent) {
            Log.d(TAG, "onRemoteVideo: ");
            if (parent.getTag(R.id.swap_info) != null) {
                UCloudRtcSdkStreamInfo swapStreamInfo = (UCloudRtcSdkStreamInfo) parent.getTag(R.id.swap_info);
                sdkEngine.muteRemoteVideo(swapStreamInfo.getUId(), !mRemoteVideoMute);
            } else if (parent.getTag() != null) {
                UCloudRtcSdkStreamInfo streamInfo = (UCloudRtcSdkStreamInfo) parent.getTag();
                if(mWhiteRole == CommonUtils.TEACHER_ROLE){
                    sdkEngine.muteLocalVideo( !mMuteCamBool,streamInfo.getMediaType());
                }else{
                    sdkEngine.muteRemoteVideo(streamInfo.getUId(), !mRemoteVideoMute);
                }
            }
        }

        @Override
        public void onRemoteAudio(View v, UCloudRtcSdkSurfaceVideoView parent) {
            if (parent.getTag(R.id.swap_info) != null) {
                UCloudRtcSdkStreamInfo swapStreamInfo = (UCloudRtcSdkStreamInfo) parent.getTag(R.id.swap_info);
                sdkEngine.muteRemoteAudio(swapStreamInfo.getUId(), !mRemoteAudioMute);
            } else if (parent.getTag() != null) {
                UCloudRtcSdkStreamInfo streamInfo = (UCloudRtcSdkStreamInfo) parent.getTag();
                if(mWhiteRole == CommonUtils.TEACHER_ROLE){
                    sdkEngine.muteLocalMic(!mMuteMicBool);
                }else{
                    sdkEngine.muteRemoteAudio(streamInfo.getUId(), !mRemoteAudioMute);
                }
            }
        }
    };

    private RemoteVideoAdapter.RemoveRemoteStreamReceiver mRemoveRemoteStreamReceiver = new RemoteVideoAdapter.RemoveRemoteStreamReceiver() {
        @Override
        public void onRemoteStreamRemoved(boolean swaped) {
            if (swaped) {
                if (mClass == UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL) {
                    sdkEngine.stopPreview(mLocalStreamInfo.getMediaType());
                    sdkEngine.startPreview(mLocalStreamInfo.getMediaType(), localrenderview);
                }else if(localrenderview.getTag(R.id.swap_info) != null){
                    UCloudRtcSdkStreamInfo remoteStreamInfo = (UCloudRtcSdkStreamInfo) localrenderview.getTag(R.id.swap_info);
                    sdkEngine.stopRemoteView(remoteStreamInfo);
                }
            }
        }
    };

    private void refreshStreamInfoText() {
        if (mSteamList == null || mSteamList.isEmpty()) {
            mTextStream.setText("当前没有流可以订阅");
        } else {
            mTextStream.setText(String.format("当前有%d路流可以订阅", mSteamList.size()));
        }
    }

    UCloudRtcSdkEventListener eventListener = new UCloudRtcSdkEventListener() {
        @Override
        public void onServerDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 服务器已断开");
                    stopTimeShow();
                    onMediaServerDisconnect();
                }
            });
        }

        @Override
        public void onJoinRoomResult(int code, String msg, String roomid,String whiteId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 加入房间成功");
                        if(!TextUtils.isEmpty(whiteId) && mWhiteRole == CommonUtils.STUDENT_ROLE){
                            mWhiteUUId = whiteId;
                            getWhiteRoomToken();
                        }
                        startTimeShow();
                    } else {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 加入房间失败 " +
                                code + " errmsg " + msg);
                        Intent intent = new Intent(WhiteBoardRoomActivity.this, ConnectActivity.class);
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
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 离开房间 " +
                            code + " errmsg " + msg);
                    Intent intent = new Intent(WhiteBoardRoomActivity.this, ConnectActivity.class);
                    onMediaServerDisconnect();
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onRejoiningRoom(String roomid) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 服务器重连中…… ");
                    stopTimeShow();
                }
            });
        }

        @Override
        public void onRejoinRoomResult(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, "服务器重连成功");
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
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, "发布视频成功");
                        int mediatype = info.getMediaType().ordinal();
                        if (mediatype == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal()) {
                            URTCVideoViewInfo vinfo = new URTCVideoViewInfo(null);
                            Log.d(TAG, " public info video: " + info.getUId() + " hasvideo " + info.isHasVideo());
                            if (info.isHasVideo()) {
                                vinfo.setmRenderview(localrenderview);
                                localrenderview.setTag(info);
                                localrenderview.setId(R.id.video_view);
                            }
                            vinfo.setmUid(info.getUId());
                            vinfo.setmMediatype(info.getMediaType());
                            vinfo.setmEanbleVideo(info.isHasVideo());
                            String mkey = info.getUId() + info.getMediaType().toString();
                            mLocalVideoKey = mkey;
                            vinfo.setKey(mkey);
                            if (mVideoAdapter != null) {
                                mVideoAdapter.addStreamView(mkey, vinfo, info);
                            }
                            mLocalStreamInfo = info;
                            if (!sdkEngine.isAudioOnlyMode()) {
                                localrenderview.setBackgroundColor(Color.TRANSPARENT);
                                sdkEngine.startPreview(info.getMediaType(),
                                        localrenderview);
                                localrenderview.refreshRemoteOp(View.VISIBLE);
                            }
                        } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN.ordinal()) {
                            URTCVideoViewInfo vinfo = new URTCVideoViewInfo(null);
                            UCloudRtcSdkSurfaceVideoView videoView = null;
                            Log.d(TAG, " pub screen info: " + info.getUId() + " hasvideo " + info.isHasVideo());
                            if (info.isHasVideo()) {
                                videoView = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
                                videoView.init(false, new int[]{R.mipmap.video_open, R.mipmap.loudspeaker, R.mipmap.video_close, R.mipmap.loudspeaker_disable}, mOnRemoteOpTrigger, new int[]{R.id.remote_video, R.id.remote_audio});
                                videoView.setScalingType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
                                videoView.hideRemoteAudio();
                                vinfo.setmRenderview(videoView);
                                videoView.setTag(info);
                                videoView.setId(R.id.video_view);
                            }
                            vinfo.setmUid(info.getUId());
                            vinfo.setmMediatype(info.getMediaType());
                            vinfo.setmEanbleVideo(info.isHasVideo());
                            String mkey = info.getUId() + info.getMediaType().toString();
                            mLocalScreenKey = mkey;
                            vinfo.setKey(mkey);
                            if (mVideoAdapter != null) {
                                mVideoAdapter.addStreamView(mkey, vinfo, info);
                            }
                            if (vinfo != null && videoView != null) {
                                sdkEngine.startPreview(info.getMediaType(), videoView);
                                videoView.refreshRemoteOp(View.VISIBLE);
                            }
                        }

                    } else {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this,
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
                    if (info.getMediaType() == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO) {
                        if (localrenderview != null) {
                            localrenderview.refresh();
                        }
                        mLocalVideoKey = null;
                    } else if (info.getMediaType() == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                        if (mCaptureMode == CommonUtils.screen_capture_mode) {
                            if (localrenderview != null) {
                                localrenderview.refresh();
                            }
                        }
                        mLocalScreenKey = null;
                    }

                    if (code == 0) {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, "取消发布视频成功");
                    } else {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, "取消发布视频失败 "
                                + code + " errmsg " + msg);
                    }

                }
            });
        }

        @Override
        public void onRemoteUserJoin(String uid,String whiteid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 用户 "
                            + uid + " 加入房间 ");
                    if(!TextUtils.isEmpty(whiteid)){
                       mWhiteUUId = whiteid;
                       getWhiteRoomToken();
                    }
                }
            });
        }

        @Override
        public void onRemoteUserLeave(String uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "remote user " + uid + "leave ,reason: " + reason);
                    onUserLeave(uid);
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 用户 " +
                            uid + " 离开房间，离开原因： " + reason);
                }
            });
        }

        @Override
        public void onRemotePublish(UCloudRtcSdkStreamInfo info) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sdkEngine.isAutoSubscribe()) {
                        sdkEngine.subscribe(info);
                    } else {
                        mSteamList.add(info);
                        mSpinnerPopupWindowScribe.notifyUpdate();
                        refreshStreamInfoText();
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
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 用户 " +
                            info.getUId() + " 取消媒体流 " + info.getMediaType());
                    String mkey = info.getUId() + info.getMediaType().toString();
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
                        UCloudRtcSdkSurfaceVideoView videoView = null;
                        Log.d(TAG, " subscribe info: " + info.getUId() + " hasvideo " + info.isHasVideo());
                        if (info.isHasVideo()) {
                            videoView = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
                            videoView.init(false, new int[]{R.mipmap.video_open, R.mipmap.loudspeaker, R.mipmap.video_close, R.mipmap.loudspeaker_disable}, mOnRemoteOpTrigger, new int[]{R.id.remote_video, R.id.remote_audio});
                            videoView.setScalingType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
                            vinfo.setmRenderview(videoView);
                            videoView.setTag(info);
                            videoView.setId(R.id.video_view);
                        }
                        vinfo.setmUid(info.getUId());
                        vinfo.setmMediatype(info.getMediaType());
                        vinfo.setmEanbleVideo(info.isHasVideo());
                        String mkey = info.getUId() + info.getMediaType().toString();
                        vinfo.setKey(mkey);
                        if (mVideoAdapter != null) {
                            mVideoAdapter.addStreamView(mkey, vinfo, info);
                        }
                        if (vinfo != null && videoView != null) {
                            sdkEngine.startRemoteView(info, videoView);
                            videoView.refreshRemoteOp(View.VISIBLE);
                        }
                    } else {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 订阅用户  " +
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
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 取消订阅用户 " +
                            info.getUId() + " 类型 " + info.getMediaType());
                    if (mVideoAdapter != null) {
                        mVideoAdapter.removeStreamView(info.getUId() + info.getMediaType().toString());
                    }
                    //取消订阅又变成可订阅
                    mSpinnerPopupWindowScribe.addStreamInfo(info, true);
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
                                onMuteMicResult(mute,mLocalVideoKey);
                            } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                                onMuteCamResult(mute,mLocalVideoKey);
                            }
                        } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                            onMuteCamResult(mute,mLocalScreenKey);
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
                        String mkey = uid + mediatype.toString();
                        Log.d(TAG, " onRemoteStreamMuteRsp " + mkey + " " + mVideoAdapter);
                        if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_AUDIO) {
                            mRemoteAudioMute = mute;
                            refreshMuteAudio(mkey,mRemoteAudioMute);
//                            int position = mVideoAdapter.getPositionByKey(mkey);
//                            View view = mRemoteGridView.getChildAt(position);
//                            UCloudRtcSdkSurfaceVideoView videoView = view.findViewById(R.id.video_view);
//                            videoView.refreshRemoteAudio(mute);
                        } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                            mRemoteVideoMute = mute;
                            refreshMuteVideo(mkey,mRemoteVideoMute);
//                            int position = mVideoAdapter.getPositionByKey(mkey);
//                            View view = mRemoteGridView.getChildAt(position);
//                            UCloudRtcSdkSurfaceVideoView videoView = view.findViewById(R.id.video_view);
//                            videoView.refreshRemoteVideo(mute);
                        }

                    } else {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, "mute " + mediatype + "failed with code: " + code);
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
                            ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 用户 " +
                                    uid + cmd + " 麦克风");
                        } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                            ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 用户 " +
                                    uid + cmd + " 摄像头");
                        }

                    } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                        String cmd = mute ? "关闭" : "打开";
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, " 用户 " +
                                uid + cmd + " 桌面流");
                    }
                }
            });
        }

        @Override
        public void onSendRTCStats(UCloudRtcSdkStats rtstats) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // localprocess.setProgress(volume);
                }
            });
        }

        @Override
        public void onRemoteRTCStats(UCloudRtcSdkStats rtstats) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //localprocess.setProgress(volume);
                }
            });
        }

        @Override
        public void onLocalAudioLevel(int volume) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    localprocess.setProgress(volume);
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
                    ToastUtils.longShow(WhiteBoardRoomActivity.this, " 被踢出会议 code " +
                            code);
                    Log.d(TAG, " user kickoff reason " + code);
                    Intent intent = new Intent(WhiteBoardRoomActivity.this, ConnectActivity.class);
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
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this, "sdp swap failed");
                    }
                }
            });
        }

        @Override
        public void onRecordStart(String msg) {

        }

        @Override
        public void onRecordStop(String msg) {

        }
    };
    private int mSelectPos;

    private void refreshMuteAudio(String key,boolean mute){
        int position = mVideoAdapter.getPositionByKey(key);
        View view = mRemoteGridView.getChildAt(position);
        UCloudRtcSdkSurfaceVideoView videoView = view.findViewById(R.id.video_view);
        videoView.refreshRemoteAudio(mute);
    }

    private void refreshMuteVideo(String key,boolean mute){
        int position = mVideoAdapter.getPositionByKey(key);
        View view = mRemoteGridView.getChildAt(position);
        UCloudRtcSdkSurfaceVideoView videoView = view.findViewById(R.id.video_view);
        videoView.refreshRemoteVideo(mute);
    }



    private void onUserLeave(String uid) {
        if (mVideoAdapter != null) {
            mVideoAdapter.removeStreamView(uid + UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
            mVideoAdapter.removeStreamView(uid + UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN);
        }
    }

    private void onMediaServerDisconnect() {
        localrenderview.release();
        clearGridItem();
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
        setContentView(R.layout.activity_white_board_room);
        timeshow = findViewById(R.id.timer);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        mCaptureMode = preferences.getInt(CommonUtils.capture_mode, CommonUtils.camera_capture_mode);
        mVideoProfile = preferences.getInt(CommonUtils.videoprofile, CommonUtils.videoprofilesel);
        mRemoteGridView = findViewById(R.id.remoteGridView);
        gridLayoutManager = new GridLayoutManager(this, COL_SIZE_P);
        mRemoteGridView.setLayoutManager(gridLayoutManager);
        mVideoAdapter = new RemoteVideoAdapter(this);
        mVideoAdapter.setVideoSize(UiHelper.dipToPx(this,115));
        mVideoAdapter.setRemoveRemoteStreamReceiver(mRemoveRemoteStreamReceiver);
        mRemoteGridView.setAdapter(mVideoAdapter);
        sdkEngine = UCloudRtcSdkEngine.createEngine(eventListener);
        mUserid = getIntent().getStringExtra("user_id");
        mRoomid = getIntent().getStringExtra("room_id");
        mRoomToken = getIntent().getStringExtra("token");
        mAppid = getIntent().getStringExtra("app_id");

        mHangup = findViewById(R.id.button_call_disconnect);
        mShowHideVideo = findViewById(R.id.btn_op_recycler);
        mStreamSelect = findViewById(R.id.stream_select);
        mTextStream = findViewById(R.id.stream_text_view);
        refreshStreamInfoText();
        mTextStream.setOnClickListener(new CustomerClickListener() {
            @Override
            protected void onSingleClick() {
                showPopupWindow();
            }

            @Override
            protected void onFastClick() {

            }
        });
        mHangup.setOnClickListener(v -> callHangUp());
        mShowHideVideo.setOnClickListener(v -> {

            float alpha = mRemoteGridView.getAlpha() == 1.0f ? 0.0f: 1.0f;
            mRemoteGridView.animate().alpha(alpha).setDuration(200).setListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    if(alpha == 0f){
                        mRemoteGridView.setVisibility(View.GONE);
                        mShowHideVideo.setBackgroundResource(R.drawable.show);
                    }else{
                        mShowHideVideo.setBackgroundResource(R.drawable.hide);
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    mRemoteGridView.setVisibility(View.VISIBLE);
                }
            }).start();
        });
//
//        mSwitchcam.setOnClickListener(v -> switchCamera());
//
//        mMuteMic.setOnClickListener(v -> onToggleMic());
//
//        mLoudSpkeader.setOnClickListener(v -> onLoudSpeaker(!mSpeakerOn));
//
//        mMuteCam.setOnClickListener(v -> onToggleCamera());
//
//        title = findViewById(R.id.titlebar);
//        title.setText("roomid: " + mRoomid);
        //title.setText("roomid: "+mRoomid+"\nuid: "+ mUserid);

        localrenderview = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
        localrenderview.init(false, new int[]{R.mipmap.video_open, R.mipmap.loudspeaker, R.mipmap.video_close, R.mipmap.loudspeaker_disable}, mOnRemoteOpTrigger, new int[]{R.id.remote_video, R.id.remote_audio});
        localrenderview.setScalingType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
        localrenderview.setZOrderMediaOverlay(false);
        localrenderview.setMirror(false);
        localprocess = findViewById(R.id.processlocal);
        isScreenCaptureSupport = UCloudRtcSdkEnv.isSuportScreenCapture();
        Log.d(TAG, " mCaptureMode " + mCaptureMode);
        switch (mCaptureMode) {
            case CommonUtils.audio_capture_mode:
                sdkEngine.setAudioOnlyMode(true);
                sdkEngine.configLocalCameraPublish(false);
                sdkEngine.configLocalAudioPublish(true);
                sdkEngine.configLocalScreenPublish(false);
                break;
            case CommonUtils.camera_capture_mode:
                sdkEngine.configLocalCameraPublish(true);
                sdkEngine.configLocalAudioPublish(true);
                sdkEngine.configLocalScreenPublish(false);
                break;
            case CommonUtils.screen_capture_mode:
                if (isScreenCaptureSupport) {
                    sdkEngine.configLocalScreenPublish(true);
                    sdkEngine.configLocalCameraPublish(false);
                    sdkEngine.configLocalAudioPublish(false);
                } else {
                    sdkEngine.configLocalCameraPublish(true);
                    sdkEngine.configLocalAudioPublish(true);
                    sdkEngine.configLocalScreenPublish(false);
                }
                break;
            case CommonUtils.screen_Audio_mode:
                if (isScreenCaptureSupport) {
                    sdkEngine.configLocalScreenPublish(true);
                    sdkEngine.configLocalCameraPublish(false);
                    sdkEngine.configLocalAudioPublish(true);
                } else {
                    sdkEngine.configLocalScreenPublish(false);
                    sdkEngine.configLocalCameraPublish(false);
                    sdkEngine.configLocalAudioPublish(true);
                }
                break;
            case CommonUtils.multi_capture_mode:
                if (isScreenCaptureSupport) {
                    sdkEngine.configLocalScreenPublish(true);
                    sdkEngine.configLocalCameraPublish(true);
                    sdkEngine.configLocalAudioPublish(true);
                } else {
                    sdkEngine.configLocalScreenPublish(false);
                    sdkEngine.configLocalCameraPublish(true);
                    sdkEngine.configLocalAudioPublish(true);
                }
                break;
        }

        sdkEngine.setAudioDevice(UCloudRtcSdkAudioDevice.UCLOUD_RTC_SDK_AUDIODEVICE_NONE);
        defaultAudioDevice = sdkEngine.getDefaultAudioDevice();
        if (defaultAudioDevice == UCloudRtcSdkAudioDevice.UCLOUD_RTC_SDK_AUDIODEVICE_SPEAKER) {
//            mLoudSpkeader.setImageResource(R.mipmap.loudspeaker);
            mSpeakerOn = true;
        } else {
            mSpeakerOn = false;
//            mLoudSpkeader.setImageResource(R.mipmap.loudspeaker_disable);
        }
        int classType = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL.ordinal());
        mClass = UCloudRtcSdkRoomType.valueOf(classType);
        sdkEngine.setClassType(mClass);
        mWhiteRole = preferences.getInt(CommonUtils.WHITE_ROLE, CommonUtils.TEACHER_ROLE);
        checkSDKRole();
        sdkEngine.setAutoPublish(true);
        sdkEngine.setAutoSubscribe(true);
        sdkEngine.setVideoProfile(UCloudRtcSdkVideoProfile.matchValue(mVideoProfile));
        //白板
        WhiteBroadView whiteBroadView = findViewById(R.id.whiteboard);
        WhiteSdkConfiguration configuration = new WhiteSdkConfiguration(DeviceType.touch, 10, 0.1);
        whiteSdk = new WhiteSdk(whiteBroadView, this, configuration);
        if(!TextUtils.isEmpty(mRoomid)){
            if(mWhiteRole == CommonUtils.TEACHER_ROLE){
                String logInfo = preferences.getString(CommonUtils.WHITE_LOG_INFO,"");
                if(!TextUtils.isEmpty(logInfo)){
                    try {
                        JSONObject jsLog = new JSONObject(logInfo);
                        if(jsLog.has(mRoomid)){
                            mWhiteUUId = jsLog.getString(mRoomid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(!TextUtils.isEmpty(mWhiteUUId)){
                    joinRTCRoom(mWhiteUUId);
                    getWhiteRoomToken();
                }else{
                    createWhiteRoom(mRoomid + "_white");
                }
            }else{
                joinRTCRoom("");
            }
        }
    }

    private void joinRTCRoom(String whiteUUId){
        UCloudRtcSdkAuthInfo info = new UCloudRtcSdkAuthInfo();
        info.setAppId(mAppid);
        info.setToken(mRoomToken);
        info.setRoomId(mRoomid);
        info.setUId(mUserid);
        if(!TextUtils.isEmpty(whiteUUId)){
            info.setWhiteId(whiteUUId);
        }
        Log.d(TAG, " roomtoken = " + mRoomToken);
        sdkEngine.joinChannel(info);
    }

    private void createWhiteRoom(String roomName){
        RemoteAPI.instance.createRoom("test room", new RemoteAPI.Callback() {

            @Override
            public void success(final String uuid, final String roomToken) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinRTCRoom(uuid);
                        joinWhiteRoom(uuid,roomToken);
                        try {
                            SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                                    Context.MODE_PRIVATE);
                            JSONObject roomUUIdMap ;
                            if(preferences.contains(CommonUtils.WHITE_LOG_INFO)){
                                roomUUIdMap = new JSONObject(preferences.getString(CommonUtils.WHITE_LOG_INFO,""));
                            }else{
                                roomUUIdMap = new JSONObject();
                            }
                            roomUUIdMap.put(mRoomid, uuid);
                            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name),
                                    Context.MODE_PRIVATE).edit();
                            editor.putString(CommonUtils.WHITE_LOG_INFO,roomUUIdMap.toString());
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void fail(final String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this,"创建白板房间失败");
                    }
                });
            }
        });
    }

    private void getWhiteRoomToken(){
        RemoteAPI.instance.getRoom(mWhiteUUId, new RemoteAPI.Callback() {
            @Override
            public void success(final String uuid, final String roomToken) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinWhiteRoom(uuid, roomToken);
                    }
                });
            }

            @Override
            public void fail(final String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this,"加入白板房间失败: "+errorMessage);
                    }
                });
            }
        });

    }

    private void joinWhiteRoom(String uuid, String roomToken) {
        RoomParams roomParams = new RoomParams(uuid, roomToken);
        whiteSdk.joinRoom(roomParams, new AbstractRoomCallbacks() {

            @Override
            public void onPhaseChanged(final RoomPhase phase) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        onRoomPhaseChange(phase);
                    }
                });
            }

            @Override
            public void onRoomStateChanged(RoomState modifyState) {
                MemberState memberState = modifyState.getMemberState();
                BroadcastState broadcastState = modifyState.getBroadcastState();
                final SceneState sceneState = modifyState.getSceneState();

                if (memberState != null) {
                    final String applianceName = memberState.getCurrentApplianceName();
                    final int[] sdkColor = memberState.getStrokeColor();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            appliancesTooBar.setState(applianceName, sdkColor);
                        }
                    });
                }
                if (broadcastState != null) {
                    final ViewMode viewMode = broadcastState.getMode();
                    final boolean hasBroadcaster = broadcastState.getBroadcasterInformation() != null;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            broadcastManager.setState(viewMode, hasBroadcaster);
                        }
                    });
                }
                if (sceneState != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            slidesTable.setSceneState(sceneState);
                        }
                    });
                }
            }
        }, new Promise<Room>() {

            @Override
            public void then(final Room room) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        setupRoom(room);
                    }
                });
            }

            @Override
            public void catchEx(final SDKError sdkError) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.shortShow(WhiteBoardRoomActivity.this,sdkError.getMessage());
                    }
                });
            }
        });

    }


    private void checkSDKRole(){
        if(mWhiteRole == CommonUtils.TEACHER_ROLE){
            if(mClass == UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL){
                mRole = UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH;
            }else{
                mRole = UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_PUB;
            }
        }else{
            if(mClass == UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL){
                mRole = UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH;
            }else{
                mRole = UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_SUB;
            }
        }
        sdkEngine.setStreamRole(mRole);
    }

//    SteamScribePopupWindow.OnSpinnerItemClickListener mOnSubscribe = new SteamScribePopupWindow.OnSpinnerItemClickListener() {
//        @Override
//        public void onItemClick(int pos) {
//            mSelectPos = pos;
//            mTextStream.setText(pos);
//            mSpinnerPopupWindowScribe.dismiss();
//        }
//    };

    //手动订阅
    SteamScribePopupWindow.OnSubscribeListener mOnSubscribeListener = new SteamScribePopupWindow.OnSubscribeListener() {
        @Override
        public void onSubscribe(List<UCloudRtcSdkStreamInfo> dataInfo) {
            for (UCloudRtcSdkStreamInfo streamInfo : dataInfo) {
                UCloudRtcSdkErrorCode result = sdkEngine.subscribe(streamInfo);
                if (result.ordinal() != UCloudRtcSdkErrorCode.NET_ERR_CODE_OK.ordinal()) {
                    ToastUtils.shortShow(WhiteBoardRoomActivity.this, "UCLOUD_RTC_SDK_ERROR_CODE:" + result.getErrorCode());
                }
            }
            mSpinnerPopupWindowScribe.dismiss();
        }
    };

    private void showPopupWindow() {
        if (!mSpinnerPopupWindowScribe.isShowing()) {
            mSpinnerPopupWindowScribe.setWidth(mTextStream.getWidth());
            mSpinnerPopupWindowScribe.showAsDropDown(mTextStream);
        }
    }

    private void setButtonSize(View button, int buttonSize) {
        button.getLayoutParams().width = buttonSize;
        button.getLayoutParams().height = buttonSize;
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
        Log.d(TAG, "activity destory");
        super.onDestroy();
        localrenderview.release();
        clearGridItem();
        mVideoAdapter.setRemoveRemoteStreamReceiver(null);
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
        int ret = sdkEngine.leaveChannel().ordinal();
        if (ret != UCloudRtcSdkErrorCode.NET_ERR_CODE_OK.ordinal()) {
            Intent intent = new Intent(WhiteBoardRoomActivity.this, ConnectActivity.class);
            onMediaServerDisconnect();
            startActivity(intent);
            finish();
        }
    }

    boolean mSwitchCam = false;

    private void switchCamera() {
        sdkEngine.switchCamera();
//        mSwitchcam.setImageResource(mSwitchCam ? R.mipmap.camera_switch_front :
//                R.mipmap.camera_switch_end);
        mSwitchCam = !mSwitchCam;
    }

    boolean mMuteMicBool = false;

    private boolean onToggleMic() {
        sdkEngine.muteLocalMic(!mMuteMicBool);
        return false;
    }

    boolean mMuteCamBool = false;

    private boolean onToggleCamera() {
        if (mCaptureMode == CommonUtils.camera_capture_mode) {
            sdkEngine.muteLocalVideo(!mMuteCamBool, UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
        } else if (mCaptureMode == CommonUtils.screen_capture_mode) {
            if (isScreenCaptureSupport) {
                sdkEngine.muteLocalVideo(!mMuteCamBool, UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN);
            } else {
                sdkEngine.muteLocalVideo(!mMuteCamBool, UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
            }
        } else if (mCaptureMode == CommonUtils.multi_capture_mode) {
            sdkEngine.muteLocalVideo(!mMuteCamBool, UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
        }

        return false;
    }

    private void onMuteCamResult(boolean mute,String key) {
        mMuteCamBool = mute;
        if(mute){
            localrenderview.refresh();
        }
        refreshMuteVideo(key,mMuteCamBool);
//        mMuteCam.setImageResource(mute ? R.mipmap.video_close : R.mipmap.video_open);
//        if (mute) {
//            localrenderview.refresh();
//            localrenderview.setVisibility(View.INVISIBLE);
//        } else {
//            localrenderview.setVisibility(View.VISIBLE);
//        }
    }

    private void onMuteMicResult(boolean mute,String key) {
        mMuteMicBool = mute;
        refreshMuteAudio(key,mMuteMicBool);
    }

    boolean mSpeakerOn = true;
    UCloudRtcSdkAudioDevice defaultAudioDevice;

    private void onLoudSpeaker(boolean enable) {
        mSpeakerOn = !mSpeakerOn;
        sdkEngine.setSpeakerOn(enable);
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

    public interface Callback {
        void success(String uuid, String roomToken);
        void fail(String errorMessage);
    }

}
