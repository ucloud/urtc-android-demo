package com.urtcdemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkSurfaceVideoView;
import com.urtcdemo.R;
import com.urtcdemo.listener.VideoViewEventListener;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.view.URTCVideoViewInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoGridRecyclerAdapter extends RecyclerView.Adapter<VideoGridRecyclerAdapter.ViewHolder> {
    public static final String TAG = " VideoGridRecyclerAdapter " ;
    private HashMap<String, URTCVideoViewInfo> mStreamViews = new HashMap<>();
    private ArrayList<String> medialist = new ArrayList<>();
    protected final LayoutInflater mInflater;
    protected VideoViewEventListener mListener;
    private Context mContext;
    private List<ViewHolder> mCacheHolder;

    public VideoGridRecyclerAdapter(Context context, VideoViewEventListener listener) {
        mContext = context ;
        mInflater = ((Activity) context).getLayoutInflater();
        mListener = listener;
        mCacheHolder = new ArrayList<>() ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.video_view, parent, false);
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
        RelativeLayout holderView = (RelativeLayout) holder.itemView;
        FrameLayout holderVideoView =  holderView.findViewById(R.id.viewcontainer) ;
        if (holderVideoView != null ) {
            if (holderVideoView.getChildCount() != 0) {
                holderVideoView.removeAllViews();
            }
        }

        String mkey = medialist.get(position);
        URTCVideoViewInfo viewInfo = mStreamViews.get(mkey);
        if (viewInfo == null) {
            return ;
        }
        if (holderVideoView.getChildCount() == 0) {
            UCloudRtcSdkSurfaceVideoView videoView = viewInfo.getmRenderview() ;
            ViewParent parent = (FrameLayout) videoView.getParent() ;
            if (parent != null) {
                ((FrameLayout)parent).removeView(videoView);
            }
            videoView.setZOrderMediaOverlay(true);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT) ;
            holderVideoView.addView(videoView, layoutParams );

            ImageButton muteplay = holder.getMuteRemotePlay() ;
            muteplay.setVisibility(View.VISIBLE);
            muteplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onMutePlay(viewInfo.getmUid(), !holder.isMutePlay());
                    }
                }
            });

            ImageButton mutevideo = holder.getMuteRemoteView() ;
            mutevideo.setVisibility(View.VISIBLE);
            mutevideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onMuteVideoView(viewInfo.getmUid(), !holder.isMuteRemoteView());
                    }
                }
            });
        }


    }

    public void addStreamView(String mkey, URTCVideoViewInfo videoView) {
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

    public void setRemoteVol(String mkey, int vol) {
        if ( mStreamViews.containsKey(mkey)) {
            int size = medialist.size() ;
            int pos = -1 ;
            for (int i=0; i<size; i++) {
                if (medialist.get(i).equals(mkey)) {
                    pos = i ;
                    break;
                }
            }
            //URTCLogUtils.d(VideoGridRecyclerAdapter.TAG, " setRemoteVol "+pos + vol) ;
//            if (pos>=0 && pos<mCacheHolder.size()) {
//                ViewHolder holder = mCacheHolder.get(pos) ;
//                holder.getVolProgress().setProgress(vol);
//            }
        }
    }

    public void onAudioMute(String mkey, boolean mute) {
        if ( mStreamViews.containsKey(mkey)) {
            int size = medialist.size() ;
            int pos = -1 ;
            for (int i=0; i<size; i++) {
                if (medialist.get(i).equals(mkey)) {
                    pos = i ;
                    break;
                }
            }

            if (pos>=0&& pos<mCacheHolder.size()) {
                ViewHolder holder = mCacheHolder.get(pos) ;
                holder.onAudioMute(mute);
            }
        }
    }

    public void onVideoMute(String mkey, boolean mute) {
        if ( mStreamViews.containsKey(mkey)) {
            URTCVideoViewInfo viewInfo = mStreamViews.get(mkey) ;
            int size = medialist.size() ;
            int pos = -1 ;
            for (int i=0; i<size; i++) {
                if (medialist.get(i).equals(mkey)) {
                    pos = i ;
                    break;
                }
            }
            if (pos>=0&& pos<mCacheHolder.size()) {
                ViewHolder holder = mCacheHolder.get(pos) ;
                if (viewInfo != null) {
                    holder.onVideoMute(mute, viewInfo.getmRenderview());
                }

            }
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
                RelativeLayout holderView = (RelativeLayout) mCacheHolder.get(i).itemView;
                FrameLayout holderVideoView =  holderView.findViewById(R.id.viewcontainer) ;
                if (holderVideoView.getChildCount() != 0) {
                    holderVideoView.removeAllViews();
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

        int size = medialist.size() ;
        int pos = -1 ;
        for (int i=0; i<size; i++) {
            if (medialist.get(i).equals(mkey)) {
                pos = i ;
                break;
            }
        }

        if (pos>0) {
            ViewHolder holder = mCacheHolder.get(pos) ;
            holder.getMuteRemotePlay().setVisibility(View.GONE);
            holder.getMuteRemoteView().setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return medialist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar volProgress ;
        private ImageButton muteRemoteView ;
        private ImageButton muteRemoteVol ;
        boolean mMutePlay = false ;
        boolean mMuteVideoView = false ;
        public ViewHolder(View itemView) {
            super(itemView);
            volProgress = itemView.findViewById(R.id.volprocess) ;
            muteRemoteView = itemView.findViewById(R.id.button_call_mute_video) ;
            muteRemoteVol = itemView.findViewById(R.id.button_call_mute_paly) ;
        }

        public void onAudioMute(boolean mute) {
            mMutePlay = mute ;
            if (mute) {
                muteRemoteVol.setBackground(mContext.getResources().getDrawable(R.mipmap.loudspeaker_disable));
            }else {
                muteRemoteVol.setBackground(mContext.getResources().getDrawable(R.mipmap.loudspeaker));
            }
        }


        public void onVideoMute(boolean mute, UCloudRtcSdkSurfaceVideoView view) {
            mMuteVideoView = mute ;
            if (mute) {
                muteRemoteView.setBackground(mContext.getResources().getDrawable(R.mipmap.video_close));
                view.refresh();
            }else {
                muteRemoteView.setBackground(mContext.getResources().getDrawable(R.mipmap.video_open));
            }
        }

        public ProgressBar getVolProgress() {
            return  volProgress ;
        }

        public ImageButton getMuteRemoteView() {
            return  muteRemoteView ;
        }

        public ImageButton getMuteRemotePlay() {
            return  muteRemoteVol ;
        }

        public boolean isMutePlay() {
            return mMutePlay ;
        }

        public boolean isMuteRemoteView() {
            return mMuteVideoView ;
        }
    }
}
