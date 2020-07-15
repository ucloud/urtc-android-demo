package com.urtcdemo.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkAudioDevice;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkAuthInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkCaptureMode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkErrorCode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMixProfile;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkNetWorkQuality;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRecordProfile;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRecordType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRoomType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkScaleType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStats;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamRole;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkTrackType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkVideoProfile;
import com.ucloudrtclib.sdkengine.define.UcloudRtcCameraMixConfig;
import com.ucloudrtclib.sdkengine.listener.UCloudRtcRecordListener;
import com.ucloudrtclib.sdkengine.listener.UCloudRtcSdkEventListener;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCDataProvider;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCDataReceiver;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCFirstFrameRendered;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCScreenShot;
import com.urtcdemo.R;
import com.urtcdemo.adpter.RemoteVideoAdapter;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.utils.ToastUtils;
import com.urtcdemo.utils.UiHelper;
import com.urtcdemo.utils.VideoListener;
import com.urtcdemo.utils.VideoPlayer;
import com.urtcdemo.view.CustomerClickListener;
import com.urtcdemo.view.SteamScribePopupWindow;
import com.urtcdemo.view.URTCVideoViewInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.ucloud.record.MediaRecorderBase;
import org.webrtc.ucloud.record.URTCRecordManager;
import org.webrtc.ucloud.record.model.MediaObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import tv.danmaku.ijk.media.player.IMediaPlayer;

import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkErrorCode.NET_ERR_CODE_OK;
import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN;
import static com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO;
import static com.urtcdemo.activity.RoomMixerActivity.BtnOp.OP_MIX;

//import com.ucloudrtclib.sdkengine.define.UcloudRtcSdkRecordProfile;
//import com.ucloudrtclib.sdkengine.openinterface.UcloudRTCSceenShot;


public class RoomMixerActivity extends AppCompatActivity implements VideoListener {
    private static final String TAG = "RoomMixerActivity";

    private String mUserid = "test001";
    private String mRoomid = "urtc1";
    private String mRoomToken = "test token";
    private String mAppid = "";
    private String mBucket = "urtc-test";
    private String mRegion = "cn-bj";
    private boolean mIsRecording = false;
    private boolean mIsMixing = false;
    private boolean mAtomOpStart = false;
    private boolean mIsPublished = false;
    private boolean mMixAddOrDel = true;

    TextView title = null;
    //    UCloudRtcSdkSurfaceVideoView localrenderview = null;
    UCloudRtcRenderView localrenderview = null;
    UCloudRtcRenderView remoteRenderView = null;
    UCloudRtcRenderView mRtspView1 = null;
    UCloudRtcRenderView mRtspView2 = null;
    UCloudRtcRenderView mRtspView3 = null;
    UCloudRtcRenderView mRtspView4 = null;
    UCloudRtcRenderView mHdmiView = null;
    ProgressBar localprocess = null;

    final int COL_SIZE_P = 3;
    final int COL_SIZE_L = 6;
    private GridLayoutManager gridLayoutManager;
    private RemoteVideoAdapter mVideoAdapter;
    RecyclerView mRemoteGridView = null;
    UCloudRtcSdkEngine sdkEngine = null;
    ImageButton mPublish = null;
    ImageButton mHangup = null;
    ImageButton mSwitchcam = null;
    ImageButton mMuteMic = null;
    ImageButton mLoudSpkeader = null;
    ImageButton mMuteCam = null;
    TextView mOpBtn = null;
    TextView mAddDelBtn = null;
    TextView mSynBtn = null;
    TextView mSwitchBtn = null;
    CheckBox mCheckBoxMirror = null;
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
    UCloudRtcSdkRoomType mClass;
    boolean isScreenCaptureSupport;
    private List<UCloudRtcSdkStreamInfo> mSteamList;
    private UCloudRtcSdkStreamInfo mLocalStreamInfo;
    private boolean mRemoteVideoMute;
    private boolean mRemoteAudioMute;
    private UCloudRtcSdkSurfaceVideoView mMuteView = null;
    Chronometer timeshow;
    private int mPictureFlag = 0;
    private boolean mPFlag = false;
    private ArrayBlockingQueue<RGBSourceData> mQueue = new ArrayBlockingQueue(2);
    // 定义一个nv21 的
    private ArrayBlockingQueue<NVSourceData> mQueueNV = new ArrayBlockingQueue(2);
    private Thread mCreateImgThread;
    private Timer mTimerCreateImg = new Timer("createPicture");
    private boolean startCreateImg = true;
    private AtomicInteger memoryCount = new AtomicInteger(0);
    private List<String> userIds = new ArrayList<>();
    private boolean mLocalRecordStart = false;
    private UCloudRtcSdkMediaType mPublishMediaType;
    private VideoPlayer mVideoPlayer;
    private UCloudRtcRenderView mRemoteRenderView;
    private boolean bigVolume = true;
    private FrameLayout testT, testB;
    private AppCompatSeekBar mSeekBar;
    private UcloudRtcCameraMixConfig mCameraMixConfig;
    private boolean synFlag = false;
    private boolean changeRTSPFlag = false;

    /**
     * SDK视频录制对象
     */
    private MediaRecorderBase mMediaRecorder;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }

    enum BtnOp {
        OP_LOCAL_RECORD,
        OP_REMOTE_RECORD,
        OP_SEND_MSG,
        OP_LOCAL_RESAMPLE,
        OP_MIX,
        OP_MIX_MANUAL
    }

    class RGBSourceData {
        Bitmap srcData;
        int width;
        int height;
        int type;

        public RGBSourceData(Bitmap srcData, int width, int height, int type) {
            this.srcData = srcData;
            this.width = width;
            this.height = height;
            this.type = type;
        }

        public Bitmap getSrcData() {
            return srcData;
        }

        public void setSrcData(Bitmap srcData) {
            this.srcData = srcData;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getType() {
            return type;
        }
    }

    class NVSourceData {
        ByteBuffer srcData;
        int width;
        int height;
        int type;

        public NVSourceData(ByteBuffer srcData, int width, int height, int type) {
            this.srcData = srcData;
            this.width = width;
            this.height = height;
            this.type = type;
        }

        public ByteBuffer getSrcData() {
            return srcData;
        }

        public void setSrcData(ByteBuffer srcData) {
            this.srcData = srcData;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getType() {
            return type;
        }
    }

    private UCloudRTCDataProvider mUCloudRTCDataProvider = new UCloudRTCDataProvider() {
        private ByteBuffer cacheBuffer;
        private RGBSourceData rgbSourceData;
        private NVSourceData nvSourceData;

        @Override
        public ByteBuffer provideRGBData(List<Integer> params) {
            rgbSourceData = mQueue.poll();
            if (rgbSourceData == null) {
                Log.d(TAG, "provideRGBData: " + null);
                return null;
            } else {
                Log.d(TAG, "provideRGBData: ! = null");
                params.add(rgbSourceData.getType());
                params.add(rgbSourceData.getWidth());
                params.add(rgbSourceData.getHeight());
                if (cacheBuffer == null) {
                    cacheBuffer = sdkEngine.getNativeOpInterface().
                            createNativeByteBuffer(2560 * 720 * 4);
                } else {
                    cacheBuffer.clear();
                }
                cacheBuffer.limit(rgbSourceData.getWidth() * rgbSourceData.getHeight() * 4);
                rgbSourceData.getSrcData().copyPixelsToBuffer(cacheBuffer);
                recycleBitmap(rgbSourceData.getSrcData());
                rgbSourceData.srcData = null;
                rgbSourceData = null;
                Log.d("YUVCapture", "provideRGBData finish" + Thread.currentThread());
                cacheBuffer.position(0);
                return cacheBuffer;
            }

            //NV系列 数据处理
//            nvSourceData = mQueueNV.poll();
//            if(nvSourceData == null){
//                return null;
//            }else{
//                params.add(nvSourceData.getType());
//                params.add(nvSourceData.getWidth());
//                params.add(nvSourceData.getHeight());
//                if(cacheBuffer == null){
//                    cacheBuffer = sdkEngine.getNativeOpInterface().
//                            createNativeByteBuffer(4096*2160*4);
//                }else{
//                    cacheBuffer.clear();
//                }
//                cacheBuffer.put(nvSourceData.getSrcData());
//                return cacheBuffer;
//            }
        }

        public void releaseBuffer() {
            if (rgbSourceData != null && !rgbSourceData.getSrcData().isRecycled()) {
                rgbSourceData.getSrcData().recycle();
                rgbSourceData.srcData = null;
                rgbSourceData = null;
            }
            if (cacheBuffer != null) {
                sdkEngine.getNativeOpInterface().realeaseNativeByteBuffer(cacheBuffer);
            }
        }
    };

    private UCloudRTCDataReceiver mUCloudRTCDataReceiver = new UCloudRTCDataReceiver() {
        private int limit = 0;
        private ByteBuffer cache;

        @Override
        public void onReceiveRGBAData(ByteBuffer rgbBuffer, int width, int height) {
            final Bitmap bitmap = Bitmap.createBitmap(width * 1, height * 1, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(rgbBuffer);
            String name = "/mnt/sdcard/yuvrgba" + limit + ".jpg";
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
            }
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
                sdkEngine.getNativeOpInterface().realeaseNativeByteBuffer(cache);
            cache = null;
        }
    };

    private UCloudRTCFirstFrameRendered mFirstFrameRendered = new UCloudRTCFirstFrameRendered() {
        @Override
        public void onFirstFrameRender(UCloudRtcSdkStreamInfo info, View view) {

        }
    };

    private View.OnClickListener mScreenShotOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addScreenShotCallBack(v);
        }
    };

    private View.OnClickListener mLocalChangeRenderMode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            localrenderview.setScalingType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
        }
    };

    private UCloudRtcSdkSurfaceVideoView.RemoteOpTrigger mOnRemoteOpTrigger = new UCloudRtcSdkSurfaceVideoView.RemoteOpTrigger() {
        @Override
        public void onRemoteVideo(View v, UCloudRtcSdkSurfaceVideoView parent) {
            if (parent.getTag(R.id.swap_info) != null) {
                UCloudRtcSdkStreamInfo swapStreamInfo = (UCloudRtcSdkStreamInfo) parent.getTag(R.id.swap_info);
                sdkEngine.muteRemoteVideo(swapStreamInfo.getUId(), !mRemoteVideoMute);
            } else if (parent.getTag() != null) {
                UCloudRtcSdkStreamInfo streamInfo = (UCloudRtcSdkStreamInfo) parent.getTag();
                sdkEngine.muteRemoteVideo(streamInfo.getUId(), !mRemoteVideoMute);
            }
            mMuteView = parent;
        }

        @Override
        public void onRemoteAudio(View v, UCloudRtcSdkSurfaceVideoView parent) {
            if (parent.getTag(R.id.swap_info) != null) {
                UCloudRtcSdkStreamInfo swapStreamInfo = (UCloudRtcSdkStreamInfo) parent.getTag(R.id.swap_info);
                sdkEngine.muteRemoteAudio(swapStreamInfo.getUId(), !mRemoteAudioMute);
            } else if (parent.getTag() != null) {
                UCloudRtcSdkStreamInfo streamInfo = (UCloudRtcSdkStreamInfo) parent.getTag();
                sdkEngine.muteRemoteAudio(streamInfo.getUId(), !mRemoteAudioMute);
            }
            mMuteView = parent;
        }
    };

    private RemoteVideoAdapter.RemoveRemoteStreamReceiver mRemoveRemoteStreamReceiver = new RemoteVideoAdapter.RemoveRemoteStreamReceiver() {
        @Override
        public void onRemoteStreamRemoved(boolean swaped) {
            if (swaped) {
                if (mClass == UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL) {
                    sdkEngine.stopPreview(mLocalStreamInfo.getMediaType());
                    sdkEngine.startPreview(mLocalStreamInfo.getMediaType(), localrenderview, null, null);
                } else if (localrenderview.getTag(R.id.swap_info) != null) {
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

    UCloudRtcSdkEventListener eventListener = new UCloudRtcSdkEventListener() {
        @Override
        public void onServerDisconnect() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onServerDisconnect: ");
                    ToastUtils.shortShow(RoomMixerActivity.this, " 服务器已断开");
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
                        sdkEngine.renderRemoteMixStream(mRemoteRenderView);
                        ToastUtils.shortShow(RoomMixerActivity.this, " 加入房间成功");
//                        mOpBtn.setVisibility(View.VISIBLE);
                        startTimeShow();
                    } else {
                        ToastUtils.shortShow(RoomMixerActivity.this, " 加入房间失败 " +
                                code + " errmsg " + msg);
                        Intent intent = new Intent(RoomMixerActivity.this, ConnectActivity.class);
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
                    ToastUtils.shortShow(RoomMixerActivity.this, " 离开房间 " +
                            code + " errmsg " + msg);
//                    Intent intent = new Intent(RoomActivity.this, ConnectActivity.class);
                    onMediaServerDisconnect();
                    System.gc();
//                    startActivity(intent);
//                    finish();
                }
            });
        }

        @Override
        public void onRejoiningRoom(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "rejoining room");
                    ToastUtils.shortShow(RoomMixerActivity.this, " 服务器重连中…… ");
                    stopTimeShow();
                }
            });
        }

        @Override
        public void onRejoinRoomResult(String roomid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.shortShow(RoomMixerActivity.this, "服务器重连成功");
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
//                        ToastUtils.shortShow(RoomActivity.this, "发布视频成功");
                        mPublish.setImageResource(R.drawable.unpublish);
                        mIsPublished = true;
                        int mediatype = info.getMediaType().ordinal();
                        mPublishMediaType = UCloudRtcSdkMediaType.matchValue(mediatype);
                        if (mediatype == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal()) {
                            if (!sdkEngine.isAudioOnlyMode()) {
                                localrenderview.setVisibility(View.VISIBLE);
                                localrenderview.setBackgroundColor(Color.TRANSPARENT);
//                                localrenderview.setScalingType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
                                sdkEngine.startPreview(info.getMediaType(),
                                        localrenderview, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);

//                                UCloudRtcRenderView renderView = new UCloudRtcRenderView(RoomActivity.this);
//                                FrameLayout frameLayout = findViewById(R.id.local_parent);
//                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(150,150);
//                                frameLayout.addView(renderView,0,layoutParams);
//                                renderView.init();
//                                sdkEngine.startPreview(info.getMediaType(),
//                                        renderView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL,null);
                                mLocalStreamInfo = info;
                                localrenderview.setTag(mLocalStreamInfo);
//                                localrenderview.refreshRemoteOp(View.INVISIBLE);
                                localrenderview.setOnClickListener(mScreenShotOnClickListener);
//                                localrenderview.setOnClickListener(mLocalChangeRenderMode);
                            }

                        } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN.ordinal()) {
                            if (mCaptureMode == CommonUtils.screen_capture_mode) {
//                                localrenderview.setVisibility(View.VISIBLE);
//                                sdkEngine.startPreview(info.getMediaType(), localrenderview, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FILL, null);
                            }
                        }

                    } else {
                        ToastUtils.shortShow(RoomMixerActivity.this,
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
                        mIsPublished = false;
                        if (info.getMediaType() == UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO) {
                            if (localrenderview != null) {
//                                localrenderview.refresh();
                            }
                            UCloudRtcSdkEnv.setCaptureMode(UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
                        } else if (info.getMediaType() == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                            if (mCaptureMode == CommonUtils.screen_capture_mode) {
//                                if (localrenderview != null) {
//                                    localrenderview.refresh();
//                                }
                            }
                        }
                        ToastUtils.shortShow(RoomMixerActivity.this, "取消发布视频成功");
                    } else {
                        ToastUtils.shortShow(RoomMixerActivity.this, "取消发布视频失败 "
                                + code + " errmsg " + msg);
                    }
                }
            });
        }

        @Override
        public void onRemoteUserJoin(String uid) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ToastUtils.shortShow(RoomMixerActivity.this, " 用户 "
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
                    onUserLeave(uid);
                    ToastUtils.shortShow(RoomMixerActivity.this, " 用户 " +
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
                    if (!mUserid.equals(info.getUId())) {
                        mSteamList.add(info);
                        if (!sdkEngine.isAutoSubscribe()) {
                            sdkEngine.subscribe(info);
                        } else {
                            mSpinnerPopupWindowScribe.notifyUpdate();
                            refreshStreamInfoText();
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
                    ToastUtils.shortShow(RoomMixerActivity.this, " 用户 " +
                            info.getUId() + " 取消媒体流 " + info.getMediaType());
                    String mkey = info.getUId() + info.getMediaType().toString();
                    if (mVideoAdapter != null) {
                        mVideoAdapter.removeStreamView(mkey);
                    }

                    mSpinnerPopupWindowScribe.removeStreamInfoByUid(info.getUId());
                    refreshStreamInfoText();
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
//                        UCloudRtcRenderView videoView = null;
                        UCloudRtcSdkSurfaceVideoView videoView = null;
                        Log.d(TAG, " subscribe info: " + info);
                        if (info.isHasVideo()) {
                            //外部扩展输出，和默认输出二选一
//                            UCloudRtcSdkSurfaceVideoView videoViewCallBack = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
//                            videoViewCallBack.setFrameCallBack(mUcloudRTCDataReceiver);
//                            videoViewCallBack.init(false);
//                            sdkEngine.startRemoteView(info, videoViewCallBack);

//                             UCloudRtcSdkSurfaceVideoView 定义的viewgroup,内含UcloudRtcRenderView
                            videoView = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
                            videoView.init(false, new int[]{R.mipmap.video_open, R.mipmap.loudspeaker, R.mipmap.video_close, R.mipmap.loudspeaker_disable, R.drawable.publish_layer}, mOnRemoteOpTrigger, new int[]{R.id.remote_video, R.id.remote_audio});
                            videoView.setTag(info);
                            videoView.setId(R.id.video_view);
                            //设置交换
//                            videoView.setOnClickListener(mSwapRemoteLocalListener);
//                            //远端截图
                            videoView.setOnClickListener(mScreenShotOnClickListener);

                            //自定义的surfaceview
//                            videoView = new UCloudRtcRenderView(getApplicationContext());
//                            videoView.init();
//                            videoView.setTag(info);
//                            videoView.setOnClickListener(mScreenShotOnClickListener);
//                            mRemoteRenderView = new UCloudRtcRenderView(getApplicationContext());
//                            mRemoteRenderView.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
//                            testT.addView(mRemoteRenderView);
//                            mRemoteRenderView.init();
//                            sdkEngine.startRemoteView(info, mRemoteRenderView,UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT,null);
//                            mRemoteRenderView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    testT.removeAllViews();
//                                    mRemoteRenderView.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
//                                    testB.addView(mRemoteRenderView);
//                                    mRemoteRenderView.resetSurface();
//                                }
//                            });
//                            mRemoteRenderView.setTag(info);
//                            mRemoteRenderView.setOnClickListener(mScreenShotOnClickListener);
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
                            mVideoAdapter.addStreamView(mkey, vinfo, info);
                        }

                        if (vinfo != null && videoView != null) {
                            sdkEngine.startRemoteView(info, videoView, UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT, null);
//                            videoView.refreshRemoteOp(View.VISIBLE);
                        }
                        //如果订阅成功就删除待订阅列表中的数据
                        mSpinnerPopupWindowScribe.removeStreamInfoByUid(info.getUId());
                        refreshStreamInfoText();
                    } else {
                        ToastUtils.shortShow(RoomMixerActivity.this, " 订阅用户  " +
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
                    Log.d(TAG, "onUnSubscribeResult: "+ info);
                    ToastUtils.shortShow(RoomMixerActivity.this, " 取消订阅用户 " +
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
                                onMuteMicResult(mute);
                            } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                                onMuteCamResult(mute);
                            }
                        } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                            onMuteCamResult(mute);
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
                            if (mMuteView != null) {
                                mMuteView.refreshRemoteAudio(mute);
                            }
//                            if (mMuteView == localrenderview) {
//                                int position = mVideoAdapter.getPositionByKey(mkey);
//                                View view = mRemoteGridView.getChildAt(position);
//                                UCloudRtcSdkSurfaceVideoView videoView = view.findViewById(R.id.video_view);
//                                videoView.refreshRemoteAudio(mute);
//                            } else {
//                                localrenderview.refreshRemoteAudio(mute);
//                            }
                        } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                            mRemoteVideoMute = mute;
                            if (mMuteView != null) {
                                mMuteView.refreshRemoteVideo(mute);
                            }
//                            if (mMuteView == localrenderview) {
//                                int position = mVideoAdapter.getPositionByKey(mkey);
//                                View view = mRemoteGridView.getChildAt(position);
//                                UCloudRtcSdkSurfaceVideoView videoView = view.findViewById(R.id.video_view);
//                                videoView.refreshRemoteVideo(mute);
//                            } else {
//                                localrenderview.refreshRemoteVideo(mute);
//                            }
                        }

                    } else {
                        ToastUtils.shortShow(RoomMixerActivity.this, "mute " + mediatype + "failed with code: " + code);
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
                            ToastUtils.shortShow(RoomMixerActivity.this, " 用户 " +
                                    uid + cmd + " 麦克风");
                        } else if (tracktype == UCloudRtcSdkTrackType.UCLOUD_RTC_SDK_TRACK_TYPE_VIDEO) {
                            ToastUtils.shortShow(RoomMixerActivity.this, " 用户 " +
                                    uid + cmd + " 摄像头");
                        }

                    } else if (mediatype == UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN) {
                        String cmd = mute ? "关闭" : "打开";
                        ToastUtils.shortShow(RoomMixerActivity.this, " 用户 " +
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
                    ToastUtils.longShow(RoomMixerActivity.this, " 被踢出会议 code " +
                            code);
                    Log.d(TAG, " user kickoff reason " + code);
                    Intent intent = new Intent(RoomMixerActivity.this, ConnectActivity.class);
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
                        ToastUtils.shortShow(RoomMixerActivity.this, "sdp swap failed");
                    }
                }
            });
        }

        @Override
        public void onRecordStart(int code, String fileName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == NET_ERR_CODE_OK.ordinal()) {
                        String videoPath = "http://" + mBucket + "." + mRegion + ".ufileos.com/" + fileName;
                        Log.d(TAG, "remote record path: " + videoPath + ".mp4");
                        ToastUtils.longShow(RoomMixerActivity.this, "观看地址: " + videoPath);
                        mIsRecording = true;
                        mOpBtn.setText("stop record");
                        if (mAtomOpStart)
                            mAtomOpStart = false;
                    } else {
                        ToastUtils.longShow(RoomMixerActivity.this, "录制开始失败: 原因：" + code);
                    }
                }
            });
        }

        @Override
        public void onRecordStop(int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.longShow(RoomMixerActivity.this, "录制结束: " + (code == NET_ERR_CODE_OK.ordinal() ? "成功" : "失败: " + code));
                    if (mIsRecording) {
                        mIsRecording = false;
                        mOpBtn.setText("start record");
                    }
                }
            });
        }

        @Override
        public void onMixStart(int code, String msg, String fileName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "code : " + code + "msg: " + msg + "fileName: " + fileName);
                    if (code == NET_ERR_CODE_OK.ordinal()) {
                        Log.d(TAG, "onMixStart: " + fileName);
                        mIsMixing = true;
                        mOpBtn.setText("stop mix");
                        if (mAtomOpStart)
                            mAtomOpStart = false;
                    }
                }
            });
        }

        @Override
        public void onMixStop(int code, String msg, String pushUrls) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onMixStop: " + code + "msg: " + msg + " pushUrl: " + pushUrls);
                    if (mIsMixing) {
                        mIsMixing = false;
                        mOpBtn.setText("mix");
                    }
                }
            });
        }

        @Override
        public void onAddStreams(int code, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onAddStreams: " + code + msg);
                }
            });
        }

        @Override
        public void onDelStreams(int code, String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onDelStreams: " + code + msg);
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
                mLoudSpkeader.setImageResource(R.mipmap.loudspeaker);
                mSpeakerOn = true;
            } else {
                mSpeakerOn = false;
                mLoudSpkeader.setImageResource(R.mipmap.loudspeaker_disable);
            }
        }

        @Override
        public void onPeerLostConnection(int type, UCloudRtcSdkStreamInfo info) {
            Log.d(TAG, "onPeerLostConnection: type: " + type + "info: " + info);
        }

        @Override
        public void onNetWorkQuality(String userId, UCloudRtcSdkStreamType streamType, UCloudRtcSdkMediaType mediaType, UCloudRtcSdkNetWorkQuality quality) {
//            Log.d(TAG, "onNetWorkQuality: userid: " + userId + "streamType: " + streamType + "mediatype : "+ mediaType + " quality: " + quality);
        }

        @Override
        public void onLocalMixRenderReady(List<UcloudRtcCameraMixConfig.CameraInfo> infos) {
            Log.d(TAG, "onLocalMixRenderReady: " + infos.size());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < infos.size(); i++) {
                        UcloudRtcCameraMixConfig.CameraInfo info = infos.get(i);
                        String cameraName = info.cameraName;
                        Log.d(TAG, "cameraName: " + cameraName + "url: " + info.url);
                        if (cameraName.contains("rtsp")) {
//                            if (info.url.contains("ch4")) {
//                                if (mRtspView1 == null) {
//                                    mRtspView1 = findViewById(R.id.FirstRTSPView);
//                                    mRtspView1.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                    mRtspView1.init();
//                                    ArrayList<SurfaceView> renders = new ArrayList();
//                                    renders.add(mRtspView1);
//                                    sdkEngine.renderMixStream(cameraName, renders);
//                                    Log.d(TAG, "bound1");
//                                } else if (mRtspView4 == null) {
//                                    mRtspView4 = findViewById(R.id.FourthRTSPView);
//                                    mRtspView4.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                    mRtspView4.init();
//                                    ArrayList<SurfaceView> renders = new ArrayList();
//                                    renders.add(mRtspView4);
//                                    sdkEngine.renderMixStream(cameraName, renders);
//                                    Log.d(TAG, "bound4");
//                                }
//
//
//                            }
                            if (info.url.contains("ch1")) {
//                                mRtspView1 = findViewById(R.id.FirstRTSPView);
//                                mRtspView1.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                mRtspView1.init();
//                                ArrayList<SurfaceView> renders = new ArrayList();
//                                renders.add(mRtspView1);
//                                sdkEngine.renderMixStream(cameraName, renders);
//                                Log.d(TAG, "bound1");
                            }
//                            else if (info.url.contains("ch3")) {
//                                if (mRtspView2 == null) {
//                                    mRtspView2 = findViewById(R.id.SecondRTSPView);
//                                    mRtspView2.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                    mRtspView2.init();
//                                    ArrayList<SurfaceView> renders = new ArrayList();
//                                    renders.add(mRtspView2);
//                                    sdkEngine.renderMixStream(cameraName, renders);
//                                    Log.d(TAG, "bound2");
//                                } else if (mRtspView3 == null) {
//                                    mRtspView3 = findViewById(R.id.ThirdRTSPView);
//                                    mRtspView3.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                    mRtspView3.init();
//                                    ArrayList<SurfaceView> renders = new ArrayList();
//                                    renders.add(mRtspView3);
//                                    sdkEngine.renderMixStream(cameraName, renders);
//                                    Log.d(TAG, "bound3");
//                                }
//                            }
////                            else if(info.url.contains("ch4") ){
////                                mRtspView4 = findViewById(R.id.FourthRTSPView);
////                                mRtspView4.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
////                                mRtspView4.init();
////                                ArrayList<SurfaceViewRenderer> renders = new ArrayList();
////                                renders.add(mRtspView4);
////                                sdkEngine.renderMixStream(cameraName,renders);
////                                Log.d(TAG, "bound4");
////                            }
                        }
                        else {
                            mHdmiView.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
                            mHdmiView.init();
                            ArrayList<SurfaceView> renders = new ArrayList();
                            renders.add(mHdmiView);
////                            renders.add(localrenderview);
                            sdkEngine.renderMixStream(cameraName, renders);
//
                            Log.d(TAG, "bound hdmi");
                        }
                    }
//                    mRemoteRenderView = findViewById(R.id.RemoteMixView);
//                    mRemoteRenderView.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                    mRemoteRenderView.init();
//                    sdkEngine.renderRemoteNestedMixStream(mRemoteRenderView);
                }
            });

        }

        @Override
        public void onLocalMixSyn(List<UcloudRtcCameraMixConfig.SynInfo> infos) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onLocalMixSyn: " + infos.size());
                    for (int i = 0; i < infos.size(); i++) {
                        UcloudRtcCameraMixConfig.SynInfo info = infos.get(i);
                        String cameraName = info.cameraName;
//                        if (info.addOrRemove) {
//                            if (cameraName.contains("rtsp")) {
//                                if (info.url.contains("ch1")) {
//                                    mRtspView1 = findViewById(R.id.FirstRTSPView);
//                                    mRtspView1.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                    mRtspView1.init();
//                                    ArrayList<SurfaceView> renders = new ArrayList();
//                                    renders.add(mRtspView1);
//                                    sdkEngine.renderMixStream(cameraName, renders);
//                                    Log.d(TAG, "bound1");
//                                } else if (info.url.contains("ch3")) {
//                                    if (mRtspView2 == null) {
//                                        mRtspView2 = findViewById(R.id.SecondRTSPView);
//                                        mRtspView2.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                        mRtspView2.init();
//                                        ArrayList<SurfaceView> renders = new ArrayList();
//                                        renders.add(mRtspView2);
//                                        sdkEngine.renderMixStream(cameraName, renders);
//                                        Log.d(TAG, "bound2");
//                                    } else if (mRtspView3 == null) {
//                                        mRtspView3 = findViewById(R.id.ThirdRTSPView);
//                                        mRtspView3.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                        mRtspView3.init();
//                                        ArrayList<SurfaceView> renders = new ArrayList();
//                                        renders.add(mRtspView3);
//                                        sdkEngine.renderMixStream(cameraName, renders);
//                                        Log.d(TAG, "bound3");
//                                    }
//                                } else if (info.url.contains("ch4")) {
//                                    mRtspView4 = findViewById(R.id.FourthRTSPView);
//                                    mRtspView4.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
//                                    mRtspView4.init();
//                                    ArrayList<SurfaceView> renders = new ArrayList();
//                                    renders.add(mRtspView4);
//                                    sdkEngine.renderMixStream(cameraName, renders);
//                                    Log.d(TAG, "bound4");
//                                }
//                            } else {
////                                mHdmiView.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
////                                mHdmiView.init();
//                                ArrayList<SurfaceView> renders = new ArrayList();
//                                renders.add(mHdmiView);
//                                sdkEngine.renderMixStream(cameraName, renders);
//                                Log.d(TAG, "bound hdmi");
//                            }
//                        } else {
//                            // stop preview
//                        }
                    }
                }
            });
        }
    };
    private int mSelectPos;

    private void onUserLeave(String uid) {
//        if (mVideoAdapter != null) {
//            mVideoAdapter.removeStreamView(uid + UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO);
//            mVideoAdapter.removeStreamView(uid + UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN);
//        }
    }

    private void onMediaServerDisconnect() {
        localrenderview.release();
        clearGridItem();
//        UCloudRtcSdkEngine.destory();
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
        setContentView(R.layout.activity_mix_room);
        testT = findViewById(R.id.test_t);
        testB = findViewById(R.id.test_bottom);
        mSeekBar = findViewById(R.id.seek_volume);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sdkEngine.adjustRecordVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        mVideoPlayer = findViewById(R.id.playView);
//        mVideoPlayer.setVideoListener(this);
//        mVideoPlayer.setPath("http://video.zhihuishu.com/zhs_yufa_150820/aidedteaching/COURSE_FOLDER/202002/47dd76d15b5348839fcfa78b104e886e_64.mp3");
//        try {
//            mVideoPlayer.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        timeshow = findViewById(R.id.timer);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        mCaptureMode = preferences.getInt(CommonUtils.capture_mode, CommonUtils.camera_capture_mode);
        mVideoProfile = preferences.getInt(CommonUtils.videoprofile, CommonUtils.videoprofilesel);
        mRemoteGridView = findViewById(R.id.remoteGridView);
//        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            gridLayoutManager = new GridLayoutManager(this, COL_SIZE_L);
//        } else {
//            gridLayoutManager = new GridLayoutManager(this, COL_SIZE_P);
//        }
        gridLayoutManager = new GridLayoutManager(this, 2);
        mRemoteGridView.setLayoutManager(gridLayoutManager);
        mVideoAdapter = new RemoteVideoAdapter(this);
        mVideoAdapter.setRemoveRemoteStreamReceiver(mRemoveRemoteStreamReceiver);
        mRemoteGridView.setAdapter(mVideoAdapter);
        sdkEngine = UCloudRtcSdkEngine.createEngine(eventListener);
//        sdkEngine = UCloudRtcApplication.getInstance().createRtcEngine(eventListener);
        mUserid = getIntent().getStringExtra("user_id");
        mRoomid = getIntent().getStringExtra("room_id");
        mRoomToken = getIntent().getStringExtra("token");
        mAppid = getIntent().getStringExtra("app_id");
        mHangup = findViewById(R.id.button_call_disconnect);
        mSwitchcam = findViewById(R.id.button_call_switch_camera);
        mMuteMic = findViewById(R.id.button_call_toggle_mic);
        mLoudSpkeader = findViewById(R.id.button_call_loundspeaker);
        mMuteCam = findViewById(R.id.button_call_toggle_cam);
        mStreamSelect = findViewById(R.id.stream_select);
        mTextStream = findViewById(R.id.stream_text_view);
        refreshStreamInfoText();
        mOpBtn = findViewById(R.id.opBtn);
        //user can chose the suitable type
//        mOpBtn.setTag(OP_SEND_MSG);
//        mOpBtn.setText("sendmsg");
//        mOpBtn.setTag(OP_LOCAL_RECORD);
//        mOpBtn.setText("lrecord");
//        mOpBtn.setTag(OP_REMOTE_RECORD);
//        mOpBtn.setText("record");
        mOpBtn.setTag(OP_MIX);
        mOpBtn.setText("mix");
//        mOpBtn.setTag(OP_MIX_MANUAL);
//        mOpBtn.setText("mix_manual");
        mAddDelBtn = findViewById(R.id.addDelBtn);
        mAddDelBtn.setText("add_st");
        mAddDelBtn.setVisibility(View.GONE);
        mSynBtn = findViewById(R.id.syn);
        mSwitchBtn = findViewById(R.id.swap);
        mCheckBoxMirror = findViewById(R.id.cb_mirror);
        mCheckBoxMirror.setChecked(UCloudRtcSdkEnv.isFrontCameraMirror());
        mCheckBoxMirror.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UCloudRtcSdkEnv.setFrontCameraMirror(isChecked);
            }
        });
        mOpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnOp btnOp = (BtnOp) mOpBtn.getTag();
                switch (btnOp) {
                    case OP_SEND_MSG:
                        sdkEngine.messageNotify("hi");
                        break;
                    case OP_LOCAL_RECORD:
                        if (!mLocalRecordStart) {
                            Log.d(TAG, " start local record: ");
//                        URTCRecordManager.getInstance().startRecord(UCloudRtcSdkRecordType.U_CLOUD_RTC_SDK_RECORD_TYPE_MP4,System.currentTimeMillis()+"",mLocalRecordListener,1000);
                            URTCRecordManager.getInstance().startRecord(UCloudRtcSdkRecordType.U_CLOUD_RTC_SDK_RECORD_TYPE_MP4, "mnt/sdcard/urtc/mp4/" + System.currentTimeMillis() + ".mp4", mLocalRecordListener, 1000);
                            mLocalRecordStart = true;
                        } else {
                            Log.d(TAG, " stop local record: ");
                            URTCRecordManager.getInstance().stopRecord();
                            mLocalRecordStart = false;
                        }
                        break;
                    case OP_REMOTE_RECORD:
                        if (!mIsRecording) {
                            mAtomOpStart = true;
//                如果主窗口是当前用户
                UCloudRtcSdkRecordProfile recordProfile = UCloudRtcSdkRecordProfile.getInstance().assembleRecordBuilder()
                        .recordType(UCloudRtcSdkRecordProfile.RECORD_TYPE_VIDEO)
                        .mainViewMediaType(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                        .VideoProfile(UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_640_480.ordinal())
                        .Average(UCloudRtcSdkRecordProfile.RECORD_UNEVEN)
                        .WaterType(UCloudRtcSdkRecordProfile.RECORD_WATER_TYPE_IMG)
                        .WaterPosition(UCloudRtcSdkRecordProfile.RECORD_WATER_POS_LEFTTOP)
                        .WarterUrl("http://urtc-living-test.cn-bj.ufileos.com/test.png")
                        .Template(UCloudRtcSdkRecordProfile.RECORD_TEMPLET_9)
                        .build();
//                sdkEngine.startRecord(recordProfile);
//                            UCloudRtcSdkRecordProfile recordAudioProfile = UCloudRtcSdkRecordProfile.getInstance().assembleRecordBuilder()
//                                    .recordType(UCloudRtcSdkRecordProfile.RECORD_TYPE_AUDIO)
//                                    .mainViewMediaType(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
//                                    .build();
//                            sdkEngine.startRecord(recordAudioProfile);

                            //如果主窗口不是当前推流用户，而是被订阅的用户
//                UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo = mVideoAdapter.getStreamInfo(0);
//                if(uCloudRtcSdkStreamInfo != null){
//                    UcloudRtcSdkRecordProfile recordProfile = UcloudRtcSdkRecordProfile.getInstance().assembleRecordBuilder()
//                            .recordType(UcloudRtcSdkRecordProfile.RECORD_TYPE_VIDEO)
//                            .mainViewUserId(uCloudRtcSdkStreamInfo.getUId())
//                            .mainViewMediaType(uCloudRtcSdkStreamInfo.getMediaType().ordinal())
//                            .VideoProfile(UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_640_480.ordinal())
//                            .Average(UcloudRtcSdkRecordProfile.RECORD_UNEVEN)
//                            .WaterType(UcloudRtcSdkRecordProfile.RECORD_WATER_TYPE_IMG)
//                            .WaterPosition(UcloudRtcSdkRecordProfile.RECORD_WATER_POS_LEFTTOP)
//                            .WarterUrl("http://urtc-living-test.cn-bj.ufileos.com/test.png")
//                            .Template(UcloudRtcSdkRecordProfile.RECORD_TEMPLET_9)
//                            .build();
//                    sdkEngine.startRecord(recordProfile);
//                }
                        } else if (!mAtomOpStart) {
                            mAtomOpStart = true;
                            sdkEngine.stopRecord();
                        }
                        break;
                    case OP_MIX:
                        if (!mIsMixing) {
                            mAtomOpStart = true;
                            //默认mix类型是3 MIX_TYPE_BOTH
                            JSONArray pushURL = new JSONArray();
//                        pushURL.put("rtmp://push.urtc.com.cn/" + mAppid + "/"+ mUserid);
//                        pushURL.put("rtmp://push.urtc.com.cn/live/URtc-h4r1txxy123131");
                            pushURL.put("rtmp://rtcpush.ugslb.com/rtclive/" + mRoomid);
                            UCloudRtcSdkMixProfile mixProfile = UCloudRtcSdkMixProfile.getInstance().assembleMixParamsBuilder()
                                    .pushUrl(pushURL)
                                    .layout(UCloudRtcSdkMixProfile.LAYOUT_AVERAGE)
                                    .resolution(1920,1080)
                                    .mainViewUserId(mUserid)
                                    .mainViewMediaType(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                                    .addStreamMode(UCloudRtcSdkMixProfile.ADD_STREAM_MODE_AUTO)
//                                    .mimeType(UCloudRtcSdkMixProfile.MIME_TYPE_AUDIO)
                                    .build();
                            sdkEngine.startMix(mixProfile);
                        } else if (!mAtomOpStart) {
                            mAtomOpStart = true;
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put("");
                            sdkEngine.stopMix(UCloudRtcSdkMixProfile.MIX_TYPE_BOTH, jsonArray);
                        }
                        break;
                    case OP_MIX_MANUAL:
                        if (!mIsMixing) {
                            mAtomOpStart = true;
                            //如果主窗口是当前用户
                            JSONArray pushURL = new JSONArray();
//                        pushURL.put("rtmp://push.urtc.com.cn/" + mAppid + "/"+ mUserid);
//                        pushURL.put("rtmp://push.urtc.com.cn/live/URtc-h4r1txxy123131");
                            pushURL.put("rtmp://rtcpush.ugslb.com/rtclive/" + mRoomid);
                            JSONArray streams = new JSONArray();
                            JSONObject local = new JSONObject();
                            try {
                                local.put("user_id", mUserid);
                                local.put("media_type", UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal());
                                streams.put(local);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            UCloudRtcSdkMixProfile mixProfile = UCloudRtcSdkMixProfile.getInstance().assembleMixParamsBuilder()
                                    .pushUrl(pushURL)
                                    .streams(streams)
                                    .mainViewUserId(mUserid)
                                    .mainViewMediaType(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO.ordinal())
                                    .addStreamMode(UCloudRtcSdkMixProfile.ADD_STREAM_MODE_MANUAL)
                                    .build();
                            sdkEngine.startMix(mixProfile);
                        } else if (!mAtomOpStart) {
                            mAtomOpStart = true;
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put("");
                            sdkEngine.stopMix(UCloudRtcSdkMixProfile.MIX_TYPE_BOTH, jsonArray);
                        }
//                    mVideoPlayer.start();
                        break;
                }
            }
        });
        //动态增加流或者删除混流
        mAddDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMixAddOrDel) {
                    mMixAddOrDel = false;
                    mAddDelBtn.setText("del_st");
                    UCloudRtcSdkStreamInfo info = mVideoAdapter.getStreamInfo(0);
                    Log.d(TAG, "add stream: " + info);
                    JSONArray streams = new JSONArray();
                    JSONObject remote = new JSONObject();
                    try {
                        remote.put("user_id", info.getUId());
                        remote.put("media_type", info.getMediaType().ordinal());
                        streams.put(remote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sdkEngine.addMixStream(streams);
                } else {
                    mMixAddOrDel = true;
                    mAddDelBtn.setText("add_st");
                    UCloudRtcSdkStreamInfo info = mVideoAdapter.getStreamInfo(0);
                    Log.d(TAG, "del stream: " + info);
                    JSONArray streams = new JSONArray();
                    JSONObject remote = new JSONObject();
                    try {
                        remote.put("user_id", info.getUId());
                        remote.put("media_type", info.getMediaType().ordinal());
                        streams.put(remote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sdkEngine.delMixStream(streams);
                }
            }
        });

        mTextStream.setOnClickListener(new CustomerClickListener() {
            @Override
            protected void onSingleClick() {
                showPopupWindow();
            }

            @Override
            protected void onFastClick() {

            }
        });
        mSteamList = new ArrayList<>();
        mSpinnerPopupWindowScribe = new SteamScribePopupWindow(this, mSteamList);
        mSpinnerPopupWindowScribe.setAnimationStyle(0);
        mSpinnerPopupWindowScribe.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
            }
        });
        ((SteamScribePopupWindow) mSpinnerPopupWindowScribe).setmOnSubScribeListener(mOnSubscribeListener);
        //手动发布
        mPublish = findViewById(R.id.button_call_pub);
        mPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPublished = false;
                if (!mIsPublished) {
                    sdkEngine.setStreamRole(UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH);
                    List<Integer> results = new ArrayList<>();
                    StringBuffer errorMessage = new StringBuffer();
                    switch (mCaptureMode) {
                        //音频
                        case CommonUtils.audio_capture_mode:
                            results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, false, true).getErrorCode());
                            break;
                        //视频
                        case CommonUtils.camera_capture_mode:
                            results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, true, true,false).getErrorCode());
                            break;
                        //屏幕捕捉
                        case CommonUtils.screen_capture_mode:
                            if (isScreenCaptureSupport) {
                                if(changeRTSPFlag){
                                    UcloudRtcCameraMixConfig.RTSP_URL = "rtsp://192.168.161.148:554/ch3";
                                    sdkEngine.changeRTSPUrl();
                                }else{
                                    UcloudRtcCameraMixConfig.RTSP_URL = "rtsp://192.168.161.148:554/ch1";
                                    results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN, true, true).getErrorCode());
                                }

                                changeRTSPFlag = !changeRTSPFlag;
                            } else {
                                errorMessage.append("设备不支持屏幕捕捉\n");
                                results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, true, true).getErrorCode());
                            }
                            break;
                        //音频+屏幕捕捉
                        case CommonUtils.screen_Audio_mode:
                            if (isScreenCaptureSupport) {
                                //推一路桌面一路音频,桌面流不需要带音频
                                results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN, false, false).getErrorCode());
                                results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, false, true).getErrorCode());
                            } else {
                                results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, false, true).getErrorCode());
                            }
                            break;
                        //视频+屏幕捕捉
                        case CommonUtils.multi_capture_mode:
                            if (isScreenCaptureSupport) {
                                results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN, true, true).getErrorCode());
                                results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, true, true).getErrorCode());
                            } else {
                                results.add(sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, true, true).getErrorCode());
                            }
                            break;
                    }
                    Log.d(TAG, "onClick: publish");
//            List<Integer> errorCodes = results.stream()
//                    .filter(result -> result != 0)
//                    .collect(Collectors.toList());
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
                    if (errorMessage.length() > 0)
                        ToastUtils.shortShow(RoomMixerActivity.this, errorMessage.toString());
                    else {
                        ToastUtils.shortShow(RoomMixerActivity.this, "发布");
                    }
                } else {
                    sdkEngine.unPublish(mPublishMediaType);
                }
            }
        });
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
                onToggleMic();
            }
        });

        mLoudSpkeader.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 onLoudSpeaker(!mSpeakerOn);
                                             }
                                         }
        );

        mMuteCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleCamera();
            }
        });

        title = findViewById(R.id.text_room);
        title.setText("roomid: " + mRoomid);
        //title.setText("roomid: "+mRoomid+"\nuid: "+ mUserid);

        localrenderview = findViewById(R.id.LocalMixView);
//        localrenderview.init(true, new int[]{R.mipmap.video_open, R.mipmap.loudspeaker, R.mipmap.video_close, R.mipmap.loudspeaker_disable, R.drawable.publish_layer}, mOnRemoteOpTrigger, new int[]{R.id.remote_video, R.id.remote_audio});
//        localrenderview.init(true);
        localrenderview.init();
        localrenderview.setZOrderMediaOverlay(false);
        localrenderview.setMirror(false);
        remoteRenderView = findViewById(R.id.RemoteMixView);
        mHdmiView = findViewById(R.id.HDMIView);
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
                sdkEngine.setAudioOnlyMode(false);
                sdkEngine.configLocalCameraPublish(true);
                sdkEngine.configLocalAudioPublish(true);
                sdkEngine.configLocalScreenPublish(false);
                break;
            case CommonUtils.screen_capture_mode:
                sdkEngine.setAudioOnlyMode(false);
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
                sdkEngine.setAudioOnlyMode(false);
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
                sdkEngine.setAudioOnlyMode(false);
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

        defaultAudioDevice = sdkEngine.getDefaultAudioDevice();
//        URTCLogUtils.d(TAG,"URTCAudioManager audio device room with: "+defaultAudioDevice);
        if (defaultAudioDevice == UCloudRtcSdkAudioDevice.UCLOUD_RTC_SDK_AUDIODEVICE_SPEAKER) {
            mLoudSpkeader.setImageResource(R.mipmap.loudspeaker);
            mSpeakerOn = true;
        } else {
            mSpeakerOn = false;
            mLoudSpkeader.setImageResource(R.mipmap.loudspeaker_disable);
        }
        int role = preferences.getInt(CommonUtils.SDK_STREAM_ROLE, UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH.ordinal());
        mRole = UCloudRtcSdkStreamRole.valueOf(role);
        sdkEngine.setStreamRole(mRole);
        int classType = preferences.getInt(CommonUtils.SDK_CLASS_TYPE, UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL.ordinal());
        mClass = UCloudRtcSdkRoomType.valueOf(classType);
        sdkEngine.setClassType(mClass);
        mPublishMode = preferences.getInt(CommonUtils.PUBLISH_MODE, CommonUtils.AUTO_MODE);
        sdkEngine.setAutoPublish(mPublishMode == CommonUtils.AUTO_MODE ? true : false);
        mScribeMode = preferences.getInt(CommonUtils.SCRIBE_MODE, CommonUtils.AUTO_MODE);
        if (mScribeMode == CommonUtils.AUTO_MODE) {
            mStreamSelect.setVisibility(View.GONE);
        } else {
            mStreamSelect.setVisibility(View.VISIBLE);
        }
        sdkEngine.setAutoSubscribe(mScribeMode == CommonUtils.AUTO_MODE ? true : false);
        //设置sdk 外部扩展模式及其采集的帧率，同时sdk内部会自动调整初始码率和最小码率
        //扩展模式只支持720p的分辨率及以下，若要自定义更高分辨率，请联系Ucloud商务定制，否则sdk会抛出异常，终止运行。
        sdkEngine.setVideoProfile(UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_EXTEND.extendParams(30,640,480));
        sdkEngine.setVideoProfile(UCloudRtcSdkVideoProfile.matchValue(mVideoProfile));
        initButtonSize();
        UCloudRtcSdkAuthInfo info = new UCloudRtcSdkAuthInfo();
        info.setAppId(mAppid);
        info.setToken(mRoomToken);
        info.setRoomId(mRoomid);
        info.setUId(mUserid);
        Log.d(TAG, " roomtoken = " + mRoomToken);
//        UCloudRtcSdkEnv.setRTSPURL("rtsp://192.168.161.148:554/ch1");
        mCameraMixConfig = new UcloudRtcCameraMixConfig();
        mCameraMixConfig.HDMI_ENCODE = true;
        mCameraMixConfig.mixMode = UcloudRtcCameraMixConfig.MixMode.MIX_RTSP_HDMI;
//        mCameraMixConfig.mixMode = UcloudRtcCameraMixConfig.MixMode.MiX_RTSP_RTSP;
        String rtspurl = preferences.getString(CommonUtils.RTSP_URL_KEY, "rtsp://192.168.161.148:554/ch1");
//        if (rtspurl.length() == 0) {
//            mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo("rtsp://192.168.161.148:554/ch1", false));
//        }else {
//            mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo(rtspurl, false));
//        }
        UcloudRtcCameraMixConfig.RTSP_URL = rtspurl;
//        mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo("rtsp://192.168.1.13/ch1", false));
//        mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo("rtsp://192.168.161.148:554/ch4", true));
//        mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo("rtsp://192.168.161.148:554/ch3", false));
//        mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo("rtsp://192.168.161.148:554/ch4", false));
        UCloudRtcSdkEnv.setMixConfig(mCameraMixConfig);
        UCloudRtcSdkEnv.setCaptureMode(
                UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
//        UCloudRtcSdkEnv.setCaptureMode(
//                UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_MIX);
        mSynBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!synFlag) {
                    mCameraMixConfig.mixMode = UcloudRtcCameraMixConfig.MixMode.MIX_RTSP_HDMI;
                    mCameraMixConfig.HDMI_ENCODE = false;
                    mCameraMixConfig.rtspURLs.clear();
                    mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo("rtsp://192.168.161.148:554/ch1", false));
//                    mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo("rtsp://192.168.1.13/ch1", false));
//                    mCameraMixConfig.rtspURLs.get(0).mix = true;
                } else {
                    mCameraMixConfig.mixMode = UcloudRtcCameraMixConfig.MixMode.MIX_RTSP_HDMI;
                    mCameraMixConfig.HDMI_ENCODE = true;
                    mCameraMixConfig.rtspURLs.clear();
//                    mCameraMixConfig.rtspURLs.add(new UcloudRtcCameraMixConfig.RtspCameraInfo("rtsp://192.168.161.148:554/ch4", false));
//                    mCameraMixConfig.rtspURLs.get(0).mix = true;
                }
                synFlag = !synFlag;
                sdkEngine.synMixConfig(mCameraMixConfig);
            }
        });

        mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdkEngine.switchMixView();
            }
        });
//        //普通摄像头捕获方式，与扩展模式二选一
//        UCloudRtcSdkEnv.setCaptureMode(
//                UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
        //rgb数据捕获，与普通捕获模式二选一
//        UCloudRtcSdkEnv.setCaptureMode(
//                UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_EXTEND);
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                try{
//                    RGBSourceData sourceData;
//                    Bitmap bitmap = null;
//                    int type;
//                    if(mPFlag){
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                        bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.img1_640,options);
//                        type = UcloudRTCDataProvider.RGBA_TO_I420;
//                    }
//                    else{
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                        bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.img_640,options);
//                        type = UcloudRTCDataProvider.RGBA_TO_I420;
//                    }
//                    mPFlag = !mPFlag;
////                            if(++mPictureFlag >50)
////                                mPictureFlag = 0;
//                    if(bitmap != null){
//                        sourceData = new RGBSourceData(bitmap,bitmap.getWidth(),bitmap.getHeight(),type);
//                        //add rgbdata
//                        mQueue.put(sourceData);
////                                Log.d(TAG, "create bitmap: " + bitmap + "count :" + memoryCount.incrementAndGet());
//                    }
////                            }
////                        }
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        };

        Runnable imgTask = new Runnable() {
            @Override
            public void run() {
                while (startCreateImg) {
                    try {
                        RGBSourceData sourceData;
                        Bitmap bitmap = null;
                        int type;
                        if (mPFlag) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_1080_1, options);
                            type = UCloudRTCDataProvider.RGBA_TO_I420;
                        } else {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_1080_2, options);
                            type = UCloudRTCDataProvider.RGBA_TO_I420;
                        }
                        mPFlag = !mPFlag;
//                            if(++mPictureFlag >50)
//                                mPictureFlag = 0;
                        if (bitmap != null) {
                            sourceData = new RGBSourceData(bitmap, bitmap.getWidth(), bitmap.getHeight(), type);
                            //add rgbdata
                            mQueue.put(sourceData);
//                                Log.d(TAG, "create bitmap: " + bitmap + "count :" + memoryCount.incrementAndGet());
                        }
//                            }
//                        }

                        Thread.sleep((int) (Math.random() * 20));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //可以添加nv21 的数据,请根据实际情况拿到bytebuffer的数据,图像宽高
//                        try {
//                            ByteBuffer byteBuffer = null;
//                            NVSourceData nvSourceData = new NVSourceData(byteBuffer,1280,720,UcloudRTCDataProvider.NV21);
//                            mQueueNV.put(nvSourceData);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                }
                //这里在回收一遍 防止队列不阻塞了在destroy以后又产生了bitmap没回收
                while (mQueue.size() != 0) {
                    RGBSourceData rgbSourceData = mQueue.poll();
                    if (rgbSourceData != null) {
                        recycleBitmap(rgbSourceData.getSrcData());
                        rgbSourceData.srcData = null;
                        rgbSourceData = null;
                    }
                }
            }
        };

        if (UCloudRtcSdkEnv.getCaptureMode() == UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_EXTEND &&
                (mRole == UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH ||
                        mRole == UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_PUB)) {

            mCreateImgThread = new Thread(imgTask);
            mCreateImgThread.setName("create picture");
            mCreateImgThread.start();
//            mTimerCreateImg.scheduleAtFixedRate(timerTask,0,10);
            UCloudRtcSdkEngine.onRGBCaptureResult(mUCloudRTCDataProvider);
        }
        mRemoteRenderView = findViewById(R.id.RemoteMixView);
        mRemoteRenderView.setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
        mRemoteRenderView.init();
        sdkEngine.joinChannel(info);
        initRecordManager();
    }

    private void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
//            Log.d(TAG, "recycleBitmap: " + bitmap + "count: "+ (memoryCount.decrementAndGet()));
        }
    }

    private UCloudRTCScreenShot mUCloudRTCScreenShot = new UCloudRTCScreenShot() {
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
            ToastUtils.shortShow(RoomMixerActivity.this, "screen shoot : " + name);
        }
    };

    private void addScreenShotCallBack(View view) {
        if (view instanceof UCloudRtcSdkSurfaceVideoView) {
            ((UCloudRtcSdkSurfaceVideoView) view).setScreenShotBack(mUCloudRTCScreenShot);
        } else if (view instanceof UCloudRtcRenderView) {
            ((UCloudRtcRenderView) view).setScreenShotBack(mUCloudRTCScreenShot);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
//        boolean hasSwap = false;
//        for (String key : mVideoAdapter.getStreamViews().keySet()) {
//        for (String key : mVideoAdapter.getStreamViews().keySet()) {
//            URTCVideoViewInfo info = mVideoAdapter.getStreamViews().get(key);
//            View videoView = info.getmRenderview();
//            UCloudRtcSdkStreamInfo videoViewStreamInfo = (UCloudRtcSdkStreamInfo) videoView.getTag();
//            UCloudRtcSdkStreamInfo videoViewSwapStreamInfo = (UCloudRtcSdkStreamInfo) videoView.getTag(R.id.swap_info);
//            if (videoView != null && videoViewStreamInfo != null) {
//                if (videoViewSwapStreamInfo != null) {
//                    //恢复交换后的小窗本地视频
//                    sdkEngine.startPreview(mLocalStreamInfo.getMediaType(), videoView,null,null);
//                    //恢复交换后的大窗远程视频
//                    sdkEngine.startRemoteView(videoViewStreamInfo, localrenderview,null,null);
//                    hasSwap = true;
//                } else {
//                    sdkEngine.startRemoteView(videoViewStreamInfo, videoView,null,null);
//                }
//            }
//        }
//        if (!hasSwap) {
//            sdkEngine.startPreview(mLocalStreamInfo.getMediaType(), localrenderview,null,null);
////            sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, true, true);
//        } if (!hasSwap) {
//            sdkEngine.startPreview(mLocalStreamInfo.getMediaType(), localrenderview,null,null);
////            sdkEngine.publish(UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO, true, true);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "on Stop");
        if (mIsPublished) {
//            Intent service = new Intent(this, UCloudRtcForeGroundService.class);
//            startService(service);
            sdkEngine.controlAudio(false);
            sdkEngine.controlLocalVideo(false);
        }

//        for (String key : mVideoAdapter.getStreamViews().keySet()) {
//            URTCVideoViewInfo info = mVideoAdapter.getStreamViews().get(key);
//            View videoView = info.getmRenderview();
//            UCloudRtcSdkStreamInfo videoViewStreamInfo = (UCloudRtcSdkStreamInfo) videoView.getTag();
//            if (videoView != null && videoViewStreamInfo != null) {
//                sdkEngine.stopRemoteView(videoViewStreamInfo);
//            }
//        }
//        if (mLocalStreamInfo != null)
//            sdkEngine.stopPreview(mLocalStreamInfo.getMediaType());
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
                if (result.ordinal() != NET_ERR_CODE_OK.ordinal()) {
                    ToastUtils.shortShow(RoomMixerActivity.this, "UCLOUD_RTC_SDK_ERROR_CODE:" + result.getErrorCode());
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

    private void initButtonSize() {
        int screenWidth = UiHelper.getScreenPixWidth(this);
        int leftRightMargin = UiHelper.dipToPx(this, 30 * 2);
        int gap = UiHelper.dipToPx(this, 8);
        int buttonSize;
        if (mPublishMode == CommonUtils.AUTO_MODE) {
            buttonSize = (screenWidth - leftRightMargin - gap * 4) / 5;
            mPublish.setVisibility(View.GONE);
        } else {
            buttonSize = (screenWidth - leftRightMargin - gap * 5) / 6;
            mPublish.setVisibility(View.VISIBLE);
            setButtonSize(mPublish, buttonSize);
        }
        buttonSize = 150;
        setButtonSize(mHangup, buttonSize);
        setButtonSize(mLoudSpkeader, buttonSize);
        setButtonSize(mSwitchcam, buttonSize);
        setButtonSize(mMuteCam, buttonSize);
        setButtonSize(mMuteMic, buttonSize);
    }

    private void setButtonSize(View button, int buttonSize) {
        button.getLayoutParams().width = buttonSize;
        button.getLayoutParams().height = buttonSize;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent service = new Intent(this, UCloudRtcForeGroundService.class);
//        stopService(service);
        sdkEngine.controlAudio(true);
        sdkEngine.controlLocalVideo(true);
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
        mUCloudRTCDataProvider.releaseBuffer();
        mUCloudRTCDataProvider = null;
        mUCloudRTCDataReceiver.releaseBuffer();
        mUCloudRTCDataReceiver = null;
        if (UCloudRtcSdkEnv.getCaptureMode() == UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_EXTEND &&
                (mRole == UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH ||
                        mRole == UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_PUB)) {
            startCreateImg = false;
            //这里回收一遍
            while (mQueue.size() != 0) {
                RGBSourceData rgbSourceData = mQueue.poll();
                if (rgbSourceData != null) {
                    recycleBitmap(rgbSourceData.getSrcData());
                    rgbSourceData.srcData = null;
                    rgbSourceData = null;
                }

            }
        }
        UCloudRtcSdkEngine.destory();
//        if(mVideoPlayer != null ){
//            mVideoPlayer.stop();
//        }
        System.gc();
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
//        if (ret != NET_ERR_CODE_OK.ordinal()) {
        Intent intent = new Intent(RoomMixerActivity.this, ConnectActivity.class);
        onMediaServerDisconnect();
        startActivity(intent);
        finish();
//        }
    }

    boolean mSwitchCam = false;

    private void switchCamera() {
        sdkEngine.switchCamera();
        ToastUtils.shortShow(this, "切换摄像头");
//        mSwitchcam.setImageResource(mSwitchCam ? R.mipmap.camera_switch_front :
//                R.mipmap.camera_switch_end);
        mSwitchCam = !mSwitchCam;
    }

    boolean mMuteMicBool = false;

    private boolean onToggleMic() {
        sdkEngine.muteLocalMic(!mMuteMicBool);
        if (!mMuteMicBool) {
            ToastUtils.shortShow(RoomMixerActivity.this, "关闭麦克风");
        } else {
            ToastUtils.shortShow(RoomMixerActivity.this, "打开麦克风");
        }
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
        if (!mMuteCamBool) {
            ToastUtils.shortShow(RoomMixerActivity.this, "关闭摄像头");
        } else {
            ToastUtils.shortShow(RoomMixerActivity.this, "打开摄像头");
        }
        return false;
    }

    private void onMuteCamResult(boolean mute) {
        mMuteCamBool = mute;
        mMuteCam.setImageResource(mute ? R.mipmap.video_close : R.mipmap.video_open);
        if (localrenderview.getTag(R.id.swap_info) != null) {
            UCloudRtcSdkStreamInfo remoteInfo = (UCloudRtcSdkStreamInfo) localrenderview.getTag(R.id.swap_info);
            String mkey = remoteInfo.getUId() + remoteInfo.getMediaType().toString();
            View view = mRemoteGridView.getChildAt(mVideoAdapter.getPositionByKey(mkey));
            if (mute) {
                view.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            if (mute) {
//                localrenderview.refresh();
                localrenderview.setVisibility(View.INVISIBLE);
            } else {
                localrenderview.setVisibility(View.VISIBLE);
            }
        }

    }

    private void onMuteMicResult(boolean mute) {
        mMuteMicBool = mute;
        mMuteMic.setImageResource(mute ? R.mipmap.microphone_disable : R.mipmap.microphone);
    }

    boolean mSpeakerOn = true;
    UCloudRtcSdkAudioDevice defaultAudioDevice;

    private void onLoudSpeaker(boolean enable) {
        if (mSpeakerOn) {
            ToastUtils.shortShow(RoomMixerActivity.this, "关闭喇叭");
        } else {
            ToastUtils.shortShow(RoomMixerActivity.this, "打开喇叭");
        }
        mSpeakerOn = !mSpeakerOn;
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

    //初始化视频
    public static void initRecordManager() {
        // 设置拍摄视频缓存路径
//        File dcim = Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        URTCRecordManager.init("");
        Log.d(TAG, "initRecordManager: cache path:" + URTCRecordManager.getVideoCachePath());
    }


}
