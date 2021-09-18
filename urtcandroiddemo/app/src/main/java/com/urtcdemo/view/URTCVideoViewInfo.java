package com.urtcdemo.view;

import android.view.View;

import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderTextureView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;

public class URTCVideoViewInfo {
    private Object mRenderview ;
    private String mUid ;
    private boolean mEnableVideo ;
    private boolean mEnableAudio;
    private boolean mMuteVideo;
    private boolean mMuteAudio;
    private boolean mSmall;
    private UCloudRtcSdkMediaType mMediaType ;
    private String key;
    private UCloudRtcSdkStreamInfo mStreamInfo;
    public URTCVideoViewInfo(){
        mUid = "" ;
        mEnableVideo = false ;
        mMediaType = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_NULL;
    }
    public URTCVideoViewInfo(UCloudRtcSdkSurfaceVideoView view) {
        mRenderview = view ;
        mUid = "" ;
        mEnableVideo = false ;
        mMediaType = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_NULL;
    }

    public URTCVideoViewInfo(UCloudRtcSdkStreamInfo info) {
        mRenderview = null ;
        mUid = info.getUId() ;
        mEnableVideo = info.isHasVideo() ;
        mEnableAudio = info.isHasAudio();
        mMediaType = info.getMediaType();
        mStreamInfo = info;
        mMuteAudio = info.isMuteAudio();
        mMuteVideo = info.isMuteAudio();
    }

    public Object getRenderview() {
        return mRenderview;
    }

    public void setRenderview(Object mRenderview) {
        this.mRenderview = mRenderview;
    }

    public boolean isEnableAudio() {
        return mEnableAudio;
    }

    public void setEnableAudio(boolean enableAudio) {
        mEnableAudio = enableAudio;
    }

    public boolean isEnableVideo() {
        return mEnableVideo;
    }

    public void setEnableVideo(boolean mEnableVideo) {
        this.mEnableVideo = mEnableVideo;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public UCloudRtcSdkMediaType getMediaType() {
        return mMediaType;
    }

    public void setMediaType(UCloudRtcSdkMediaType mMediaType) {
        this.mMediaType = mMediaType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setStreamInfo(UCloudRtcSdkStreamInfo streamInfo) {
        mStreamInfo = streamInfo;
    }

    public UCloudRtcSdkStreamInfo getStreamInfo() {
        return mStreamInfo;
    }

    public boolean isMuteVideo() {
        return mMuteVideo;
    }

    public void setMuteVideo(boolean muteVideo) {
        mMuteVideo = muteVideo;
    }

    public boolean isMuteAudio() {
        return mMuteAudio;
    }

    public void setMuteAudio(boolean muteAudio) {
        mMuteAudio = muteAudio;
    }

    public boolean isSmall() {
        return mSmall;
    }

    public void setSmall(boolean small) {
        mSmall = small;
    }

    public Object release() {
        if (mRenderview != null) {
            if(mRenderview instanceof UCloudRtcSdkSurfaceVideoView){
                ((UCloudRtcSdkSurfaceVideoView)mRenderview).refresh();
                ((UCloudRtcSdkSurfaceVideoView)mRenderview).release();
            }else if(mRenderview instanceof UCloudRtcRenderView){
                ((UCloudRtcRenderView)mRenderview).release();
            }
            else if(mRenderview instanceof UCloudRtcRenderTextureView){
                ((UCloudRtcRenderTextureView)mRenderview).release();
            }
//            mRenderview = null ;

        }
        return mRenderview;
    }

}
