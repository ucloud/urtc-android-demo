package com.urtcdemo.view;

import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;

public class URTCVideoViewInfo {
    private UCloudRtcSdkSurfaceVideoView mRenderview ;
    private String mUid ;
    private boolean mEanbleVideo ;
    private UCloudRtcSdkMediaType mMediatype ;

    public URTCVideoViewInfo(UCloudRtcSdkSurfaceVideoView view) {
        mRenderview = view ;
        mUid = "" ;
        mEanbleVideo = false ;
        mMediatype = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_NULL;
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

    public UCloudRtcSdkMediaType getmMediatype() {
        return mMediatype;
    }

    public void setmMediatype(UCloudRtcSdkMediaType mMediatype) {
        this.mMediatype = mMediatype;
    }

    public void release() {
        if (mRenderview != null) {
            mRenderview.refresh();
            mRenderview.release();
            mRenderview = null ;
        }
    }

}
