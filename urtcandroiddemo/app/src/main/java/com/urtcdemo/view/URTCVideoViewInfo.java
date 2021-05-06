package com.urtcdemo.view;

import com.cmcc.sdkengine.define.CMCCTextureViewRenderer;
import com.cmcc.sdkengine.define.CMCCSurfaceViewRenderer;
import com.cmcc.sdkengine.define.CMCCMediaType;
import com.cmcc.sdkengine.define.CMCCStreamInfo;
import com.cmcc.sdkengine.define.CMCCSurfaceViewGroup;

public class URTCVideoViewInfo {
    private Object mRenderview ;
    private String mUid ;
    private boolean mEanbleVideo ;
    private boolean mEnableAudio;
    private CMCCMediaType mMediatype ;
    private String key;
    private CMCCStreamInfo mStreamInfo;
    public URTCVideoViewInfo(){
        mUid = "" ;
        mEanbleVideo = false ;
        mMediatype = CMCCMediaType.MEDIA_TYPE_NULL;
    }
    public URTCVideoViewInfo(CMCCSurfaceViewGroup view) {
        mRenderview = view ;
        mUid = "" ;
        mEanbleVideo = false ;
        mMediatype = CMCCMediaType.MEDIA_TYPE_NULL;
    }

    public URTCVideoViewInfo(CMCCStreamInfo info) {
        mRenderview = null ;
        mUid = info.getUId() ;
        mEanbleVideo = info.isHasVideo() ;
        mEnableAudio = info.isHasAudio();
        mMediatype = info.getMediaType();
        mStreamInfo = info;
    }

    public Object getmRenderview() {
        return mRenderview;
    }

    public void setmRenderview(Object mRenderview) {
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

    public CMCCMediaType getmMediatype() {
        return mMediatype;
    }

    public void setmMediatype(CMCCMediaType mMediatype) {
        this.mMediatype = mMediatype;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setStreamInfo(CMCCStreamInfo streamInfo) {
        mStreamInfo = streamInfo;
    }

    public CMCCStreamInfo getStreamInfo() {
        return mStreamInfo;
    }

    public Object release() {
        if (mRenderview != null) {
            if(mRenderview instanceof CMCCSurfaceViewGroup){
                ((CMCCSurfaceViewGroup)mRenderview).refresh();
                ((CMCCSurfaceViewGroup)mRenderview).release();
            }else if(mRenderview instanceof CMCCSurfaceViewRenderer){
                ((CMCCSurfaceViewRenderer)mRenderview).release();
            }
            else if(mRenderview instanceof CMCCTextureViewRenderer){
                ((CMCCTextureViewRenderer)mRenderview).release();
            }
//            mRenderview = null ;

        }
        return mRenderview;
    }

}
