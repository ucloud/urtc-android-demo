package com.urtcdemo.view;

import android.view.View;

import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;

public class URTCVideoViewInfo {
    private View mRenderview ;
    private String mUid ;
    private boolean mEanbleVideo ;
    private boolean mEnableAudio;
    private UCloudRtcSdkMediaType mMediatype ;
    private String key;
    private UCloudRtcSdkStreamInfo mStreamInfo;
    public URTCVideoViewInfo(){
        mUid = "" ;
        mEanbleVideo = false ;
        mMediatype = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_NULL;
    }
    public URTCVideoViewInfo(UCloudRtcSdkSurfaceVideoView view) {
        mRenderview = view ;
        mUid = "" ;
        mEanbleVideo = false ;
        mMediatype = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_NULL;
    }

    public URTCVideoViewInfo(UCloudRtcSdkStreamInfo info) {
        mRenderview = null ;
        mUid = info.getUId() ;
        mEanbleVideo = info.isHasVideo() ;
        mEnableAudio = info.isHasAudio();
        mMediatype = info.getMediaType();
        mStreamInfo = info;
    }

    public View getmRenderview() {
        return mRenderview;
    }

    public void setmRenderview(View mRenderview) {
        this.mRenderview = mRenderview;
    }

    public boolean isEnableAudio() {
        return mEnableAudio;
    }

    public void setEnableAudio(boolean enableAudio) {
        mEnableAudio = enableAudio;
    }

    public boolean ismEanbleVideo() {
        return mEanbleVideo;
    }

    public void setmEanbleVideo(boolean mEanbleVideo) {
        this.mEanbleVideo = mEanbleVideo;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public UCloudRtcSdkMediaType getmMediatype() {
        return mMediatype;
    }

    public void setmMediatype(UCloudRtcSdkMediaType mMediatype) {
        this.mMediatype = mMediatype;
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

    public void release() {
        if (mRenderview != null) {
            if(mRenderview instanceof UCloudRtcSdkSurfaceVideoView){
                ((UCloudRtcSdkSurfaceVideoView)mRenderview).refresh();
                ((UCloudRtcSdkSurfaceVideoView)mRenderview).release();
            }
            mRenderview = null ;
        }
    }

}
