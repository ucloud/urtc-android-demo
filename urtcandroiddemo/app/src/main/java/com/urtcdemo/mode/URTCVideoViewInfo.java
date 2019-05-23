package com.urtcdemo.mode;

import com.urtclib.sdkengine.define.UCloudRtcSdkMediatype;
import com.urtclib.sdkengine.define.UCloudRtcSdkMode;
import com.urtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;
import com.urtclib.sdkengine.define.UCloudRtcSdkMediatype;
import com.urtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;

public class URTCVideoViewInfo {
    private UCloudRtcSdkSurfaceVideoView mRenderview ;
    private String mUid ;
    private boolean mEanbleVideo ;
    private UCloudRtcSdkMediatype mMediatype ;

    public URTCVideoViewInfo(UCloudRtcSdkSurfaceVideoView view) {
        mRenderview = view ;
        mUid = "" ;
        mEanbleVideo = false ;
        mMediatype = UCloudRtcSdkMediatype.URTC_SDK_MEDIA_TYPE_NULL ;
    }

    public UCloudRtcSdkSurfaceVideoView getmRenderview() {
        return mRenderview;
    }

    public void setmRenderview(UCloudRtcSdkSurfaceVideoView mRenderview) {
        this.mRenderview = mRenderview;
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

    public UCloudRtcSdkMediatype getmMediatype() {
        return mMediatype;
    }

    public void setmMediatype(UCloudRtcSdkMediatype mMediatype) {
        this.mMediatype = mMediatype;
    }

    public void release() {
        if (mRenderview != null) {
            mRenderview.refrush();
            mRenderview.release();
            mRenderview = null ;
        }
    }

}
