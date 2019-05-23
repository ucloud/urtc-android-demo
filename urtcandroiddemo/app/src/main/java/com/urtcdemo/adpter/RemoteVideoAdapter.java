package com.urtcdemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.mode.URTCVideoViewInfo;
import com.urtcdemo.listener.VideoViewEventListener;
import com.urtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoteVideoAdapter extends RecyclerView.Adapter<RemoteVideoAdapter.ViewHolder> {
    public static final String TAG = " VideoGridRecyclerAdapter " ;
    private HashMap<String, URTCVideoViewInfo> mStreamViews = new HashMap<>();
    private ArrayList<String> medialist = new ArrayList<>();
    protected final LayoutInflater mInflater;
    protected VideoViewEventListener mListener;
    private Context mContext;
    private List<ViewHolder> mCacheHolder;

    public RemoteVideoAdapter(Context context, VideoViewEventListener listener) {
        mContext = context ;
        mInflater = ((Activity) context).getLayoutInflater();
        mListener = listener;
        mCacheHolder = new ArrayList<>() ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.remote_video_view, parent, false);
        v.getLayoutParams().width = CommonUtils.mItemWidth;
        v.getLayoutParams().height = CommonUtils.mItemHeight;
        ViewHolder holder = new ViewHolder(v);
        if (mCacheHolder != null) {
            mCacheHolder.add(holder);
        }else {
            mCacheHolder = new ArrayList<>();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        FrameLayout holderView = (FrameLayout) holder.itemView;
        if (holderView != null ) {
            if (holderView.getChildCount() != 0) {
                holderView.removeAllViews();
                holderView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        String mkey = medialist.get(position);
        URTCVideoViewInfo viewInfo = mStreamViews.get(mkey);
        if (viewInfo == null) {
            return ;
        }
        if (holderView.getChildCount() == 0) {
            UCloudRtcSdkSurfaceVideoView videoView = viewInfo.getmRenderview() ;
            if (videoView != null) {
                ViewParent parent = videoView.getParent() ;
                if (parent != null) {
                    ((FrameLayout)parent).removeView(videoView);
                }
                videoView.setZOrderMediaOverlay(true);

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT) ;
                holderView.addView(videoView, layoutParams );
            }else {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT) ;
                ImageView imageView = new ImageView(mContext) ;
                imageView.setBackground( mContext.getResources().getDrawable(R.drawable.user_default) );
                holderView.setBackground(mContext.getResources().getDrawable(R.drawable.border) );
                holderView.addView(imageView, layoutParams );
            }
        }


    }

    public void addStreamView(String mkey, URTCVideoViewInfo videoView) {
        removeStreamView(mkey) ;

        if ( !mStreamViews.containsKey(mkey)) {
            mStreamViews.put(mkey, videoView);
            medialist.add(mkey);
            notifyDataSetChanged();
        }
    }

    public void removeStreamView(String mkey) {
        if (mStreamViews.containsKey(mkey))
        {
            releaseVideoContainerRes(mkey);
            mStreamViews.remove(mkey);
            medialist.remove(mkey);
            notifyDataSetChanged();
        }
    }

    public void clearAll() {
        for (String streamId : mStreamViews.keySet()) {
            releaseVideoContainerRes(streamId);
        }
        medialist.clear();
        mStreamViews.clear();

        if (mListener != null) {
            mListener = null;
        }

        if (mCacheHolder!=null){
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
    private void releaseVideoContainerRes(String mkey){
        URTCVideoViewInfo viewInfo = mStreamViews.get(mkey);
        if (viewInfo!=null) {
            viewInfo.release();
        }
    }

    @Override
    public int getItemCount() {
        return medialist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
