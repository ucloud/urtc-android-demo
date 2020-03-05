package com.urtcdemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkScaleType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.ucloudrtclib.sdkengine.define.UcloudRtcRenderView;
import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.view.URTCVideoViewInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoteVideoAdapter extends RecyclerView.Adapter<RemoteVideoAdapter.ViewHolder> {
    public static final String TAG = " VideoGridAdapter ";
    private HashMap<String, URTCVideoViewInfo> mStreamViews = new HashMap<>();
    private HashMap<String, Boolean> mScreenState = new HashMap<>();
    private ArrayList<String> medialist = new ArrayList<>();
    protected final LayoutInflater mInflater;
    private Context mContext;
    private List<ViewHolder> mCacheHolder;
    private RemoveRemoteStreamReceiver mRemoveRemoteStreamReceiver;


    public RemoteVideoAdapter(Context context) {
        mContext = context;
        mInflater = ((Activity) context).getLayoutInflater();
        mCacheHolder = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.remote_video_view, parent, false);
        v.getLayoutParams().width = CommonUtils.mItemWidth;
        v.getLayoutParams().height = CommonUtils.mItemHeight;
        ViewHolder holder = new ViewHolder(v);
        if (mCacheHolder != null) {
            mCacheHolder.add(holder);
        } else {
            mCacheHolder = new ArrayList<>();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        FrameLayout holderView = (FrameLayout) holder.itemView;
        if (holderView != null) {
            if (holderView.getChildCount() != 0) {
                holderView.removeAllViews();
                holderView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        String mkey = medialist.get(position);
        URTCVideoViewInfo viewInfo = mStreamViews.get(mkey);
        if (viewInfo == null) {
            return;
        }
        if (holderView.getChildCount() == 0) {
            View videoView = viewInfo.getmRenderview();
            if (videoView != null) {
                ViewParent parent = videoView.getParent();
                if (parent != null) {
                    ((FrameLayout) parent).removeView(videoView);
                }
                if(videoView instanceof UCloudRtcSdkSurfaceVideoView )
                    ((UCloudRtcSdkSurfaceVideoView)videoView).setZOrderMediaOverlay(true);
                if(videoView instanceof UcloudRtcRenderView){
                    ((UcloudRtcRenderView)videoView).setZOrderMediaOverlay(true);
                    ((UcloudRtcRenderView)videoView).setScaleType(UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_ASPECT_FIT);
                }
                videoView.setTag(R.id.index, viewInfo);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                holderView.addView(videoView, layoutParams);
            } else {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                ImageView imageView = new ImageView(mContext);
                imageView.setBackground(mContext.getResources().getDrawable(R.drawable.user_default));
                holderView.setBackground(mContext.getResources().getDrawable(R.drawable.border));
                holderView.addView(imageView, layoutParams);
            }
        }
    }

    public void setRemoveRemoteStreamReceiver(RemoveRemoteStreamReceiver removeRemoteStreamReceiver) {
        mRemoveRemoteStreamReceiver = removeRemoteStreamReceiver;
    }

    public boolean checkState(String key) {
        return mScreenState.get(key);
    }

    public void reverseState(String key) {
        boolean reverse;
        if (mScreenState.containsKey(key)) {
            reverse = !mScreenState.get(key);
            mScreenState.put(key, reverse);
        }
    }

    public int getPositionByKey(String key){
       return medialist.indexOf(key);
    }
    public boolean checkCanSwap(String key) {
        if (mScreenState.containsKey(key) && mScreenState.get(key)) {
            //如果自己已经交换过就直接允许交换
            return true;
        } else {
            boolean otherHasSwaped = false;
            for (String ohterKey : mScreenState.keySet()) {
                if (!ohterKey.equals(key)) {
                    if (mScreenState.get(ohterKey)) {
                        //其它的已经有交换过的，那这次就不要交换
                        otherHasSwaped = true;
                        break;
                    }
                }
            }
            return !otherHasSwaped;
        }
    }

    public void addStreamView(String mkey, URTCVideoViewInfo videoView, UCloudRtcSdkStreamInfo streamInfo) {
        removeStreamView(mkey);
        if (!mStreamViews.containsKey(mkey)) {
            mStreamViews.put(mkey, videoView);
            medialist.add(mkey);
        }
        if (!mScreenState.containsKey(mkey)) {
            mScreenState.put(mkey, false);
        }
        notifyDataSetChanged();
    }

    public UCloudRtcSdkStreamInfo getStreamInfo(int position){
        UCloudRtcSdkStreamInfo streamInfo = null;
        if(medialist.size() > position && mStreamViews.size() > position){
            streamInfo = new UCloudRtcSdkStreamInfo();
            streamInfo.setMediaType(mStreamViews.get(medialist.get(position)).getmMediatype());
            streamInfo.setHasAudio(mStreamViews.get(medialist.get(position)).isEnableAudio());
            streamInfo.setHasVideo(mStreamViews.get(medialist.get(position)).ismEanbleVideo());
            streamInfo.setUid(mStreamViews.get(medialist.get(position)).getmUid());
        }
        return streamInfo;
    }

    public void removeStreamView(String mkey) {
        if (mStreamViews.containsKey(mkey)) {
            Log.d(TAG, " removeStreamView key: " + mkey);
            releaseVideoContainerRes(mkey);
            mStreamViews.remove(mkey);
            medialist.remove(mkey);
            Log.d(TAG, " remove finished ,mStreamViews size: " + mStreamViews.size() + "medialist size: " + medialist.size());
        }
        if (mScreenState.containsKey(mkey)) {
            Log.d(TAG, " mScreenState key: " + mkey);
            if (mScreenState.get(mkey)) {
                if (mRemoveRemoteStreamReceiver != null) {
                    mRemoveRemoteStreamReceiver.onRemoteStreamRemoved(true);
                }
            }
            mScreenState.remove(mkey);
            Log.d(TAG, " remove finished ,mScreenState size: " + mScreenState.size());
        }
        notifyDataSetChanged();
    }

    public void clearAll() {
        for (String streamId : mStreamViews.keySet()) {
            releaseVideoContainerRes(streamId);
        }
        medialist.clear();
        mStreamViews.clear();
        mScreenState.clear();

        if (mCacheHolder != null) {
            for (int i = 0; i < mCacheHolder.size(); i++) {
                FrameLayout holderView = (FrameLayout) mCacheHolder.get(i).itemView;
                if (holderView.getChildCount() != 0) {
                    holderView.removeAllViews();
                }
                holderView.removeAllViews();
            }
            mCacheHolder.clear();
        }
    }
    private void releaseVideoContainerRes(String mkey) {
        URTCVideoViewInfo viewInfo = mStreamViews.get(mkey);
        if (viewInfo != null) {
            viewInfo.release();
        }
    }

    @Override
    public int getItemCount() {
        return medialist.size();
    }

    public HashMap<String, URTCVideoViewInfo> getStreamViews() {
        return mStreamViews;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface RemoveRemoteStreamReceiver {
        void onRemoteStreamRemoved(boolean swaped);
    }
}
