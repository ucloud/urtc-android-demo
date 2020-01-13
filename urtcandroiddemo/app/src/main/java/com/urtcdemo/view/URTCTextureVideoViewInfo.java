package com.urtcdemo.view;

import android.view.TextureView;

import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;

public class URTCTextureVideoViewInfo {
    private TextureView mRenderview ;
    private String mUid ;
    private boolean mEanbleVideo ;
    private boolean mEnableAudio;
    private UCloudRtcSdkMediaType mMediatype ;
    private String key;

    public URTCTextureVideoViewInfo(TextureView view) {
        mRenderview = view ;
        mUid = "" ;
        mEanbleVideo = false ;
        mMediatype = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_NULL;
    }

    public TextureView getmRenderview() {
        return mRenderview;
    }

    public void setmRenderview(TextureView mRenderview) {
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

    public void release() {
        if (mRenderview != null) {
//            mRenderview.refresh();
//            mRenderview.release();
            mRenderview = null ;
        }
    }

}
