package com.urtcdemo.view;

import android.view.TextureView;

import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;

public class URTCTextureVideoViewInfo {
    private TextureView mRenderview ;
    private String mUid ;
    private boolean mEnableVideo ;
    private boolean mEnableAudio;
    private UCloudRtcSdkMediaType mMediatype ;
    private String key;

    public URTCTextureVideoViewInfo(TextureView view) {
        mRenderview = view ;
        mUid = "" ;
        mEnableVideo = false ;
        mMediatype = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_NULL;
    }

    public TextureView getRenderview() {
        return mRenderview;
    }

    public void setRenderview(TextureView mRenderview) {
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
        return mMediatype;
    }

    public void setMediaType(UCloudRtcSdkMediaType mMediatype) {
        this.mMediatype = mMediatype;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void release() {
        if (mRenderview != null) {
//            mRenderview.refresh();
//            mRenderview.release();
            mRenderview = null ;
        }
    }

}
