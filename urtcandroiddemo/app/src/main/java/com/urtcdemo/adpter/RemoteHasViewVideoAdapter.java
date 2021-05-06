package com.urtcdemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cmcc.sdkengine.CMCCRtcEngine;
import com.cmcc.sdkengine.define.CMCCTextureViewRenderer;
import com.cmcc.sdkengine.define.CMCCStreamInfo;
import com.cmcc.sdkengine.openinterface.CMCCFirstFrameRendered;
import com.urtcdemo.R;
import com.urtcdemo.utils.CommonUtils;
import com.urtcdemo.view.URTCVideoViewInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RemoteHasViewVideoAdapter extends RecyclerView.Adapter<RemoteHasViewVideoAdapter.ViewHolder> {
    public static final String TAG = " RemoteVideoAdapter ";
    private HashMap<String, URTCVideoViewInfo> mStreamViews = new HashMap<>();
    private Set<Object> cacheRender = new HashSet<>();
    private ArrayList<String> medialist = new ArrayList<>();
    protected final LayoutInflater mInflater;
    private Context mContext;
    private List<ViewHolder> mCacheHolder;
    private SwapInterface mSwapInterface;
    private CMCCRtcEngine mSdkEngine;


    public RemoteHasViewVideoAdapter(Context context, CMCCRtcEngine sdkEngine, SwapInterface provider) {
        mContext = context;
        mSdkEngine = sdkEngine;
        mSwapInterface = provider;
        mInflater = ((Activity) context).getLayoutInflater();
        mCacheHolder = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, TAG + "onCreateViewHolder");
        View v = mInflater.inflate(R.layout.remote_video_view_texture, parent, false);
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
//        holder.setIsRecyclable(false);
        Log.d(TAG, TAG + "onBindViewHolder + " + position);
        FrameLayout holderView = (FrameLayout) holder.itemView;
//        if (holderView != null) {
//            if (holderView.getChildCount() != 0) {
//                holderView.removeAllViews();
//                holderView.setBackgroundColor(Color.TRANSPARENT);
//            }
//        }
        String mkey = medialist.get(position);
        URTCVideoViewInfo viewInfo = mStreamViews.get(mkey);
        if (viewInfo == null) {
            return;
        }

        TextureView videoView = holderView.findViewById(R.id.texture_view);

        if (videoView != null) {

//            ViewParent parent = videoView.getParent();
//            if (parent != null) {
//                ((FrameLayout) parent).removeView(videoView);
//            }
            videoView.setTag(R.id.index, viewInfo);

            boolean isLocal = false;
            if(mSwapInterface != null){
                videoView.setOnClickListener(mSwapInterface.provideSwapListener());
                isLocal = mSwapInterface.isLocalStream(viewInfo.getmUid());
            }else{
                videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //screen shot
                        Log.d(TAG, "onClick: take snapShop: " + viewInfo.getStreamInfo());
                        mSdkEngine.takeSnapShot(false,viewInfo.getStreamInfo(), (rgbBuffer, width, height) -> {
                            Log.d(TAG, "onReceiveRGBAData: rgbBuffer: " + rgbBuffer + " width: " + width + " height: " + height);
                            final Bitmap bitmap = Bitmap.createBitmap(width * 1, height * 1, Bitmap.Config.ARGB_8888);

                            bitmap.copyPixelsFromBuffer(rgbBuffer);
                            String name = "/mnt/sdcard/urtcscreen_" + System.currentTimeMillis() + "_remote.jpg";
                            File file = new File(name);
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                                    out.flush();
                                    out.close();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "screen shoot : " + name);
                        });


                        //view render mode change
//                    mSdkEngine.setRenderViewMode(false, viewInfo.getStreamInfo(), UCloudRtcSdkScaleType.UCLOUD_RTC_SDK_SCALE_FILL);
                    }
                });
            }
            CMCCTextureViewRenderer render = null;
            Object viewInfoRender = viewInfo.getmRenderview();
            Log.d(TAG, "onBindViewHolder: video info render "+ viewInfo.getmRenderview());
            if(videoView.getTag(R.id.render)!= null){
                render = (CMCCTextureViewRenderer)videoView.getTag(R.id.render);
                Log.d(TAG, "onBindViewHolder: view "+ videoView +" has render "+ render);
            }else{
                render = new CMCCTextureViewRenderer(videoView);
                render.init();
                videoView.setTag(R.id.render,render);
                Log.d(TAG, "onBindViewHolder: new render "+ render);
            }

            if(!render.equals(viewInfoRender)){
                Log.d(TAG, "onBindViewHolder: new render old info "+ videoView.getTag());
                Log.d(TAG, "onBindViewHolder: new info " + viewInfo.getStreamInfo() + " old render : "+ viewInfo.getmRenderview());
                mSwapInterface.stopRender(viewInfo);
            }
            videoView.setTag(viewInfo.getStreamInfo());
            viewInfo.setmRenderview(render);
            for (Object releaseCacheRender : cacheRender) {
                if(releaseCacheRender.equals(render)){
                    Log.d(TAG, "onBindViewHolder: reinit render "+ releaseCacheRender);
                    render.init();
                    break;
                }
            }
            if(isLocal){
                mSdkEngine.setupLocalVideo(viewInfo.getStreamInfo(), render, null, new CMCCFirstFrameRendered(){
                    @Override
                    public void onFirstFrameRender(CMCCStreamInfo CMCCStreamInfo, View view) {
                        Log.d(TAG, "onlocal first frame render: " + "view: " + view);
                    }
                });
            }else{
                mSdkEngine.setupRemoteVideo(viewInfo.getStreamInfo(), render, null, new CMCCFirstFrameRendered(){

                    @Override
                    public void onFirstFrameRender(CMCCStreamInfo CMCCStreamInfo, View view) {
                        Log.d(TAG, "onRemoteFirstFrameRender: " + "view: " + view);
                    }
                });
            }

        } else {
            holderView.setBackground(mContext.getResources().getDrawable(R.drawable.border));
        }
    }

    public int getPositionByKey(String key) {
        return medialist.indexOf(key);
    }

    public void addStreamView(String mkey, URTCVideoViewInfo videoViewInfo) {
//        removeStreamView(mkey);
        if (!mStreamViews.containsKey(mkey)) {
            mStreamViews.put(mkey, videoViewInfo);
            medialist.add(mkey);
        }
//
//        notifyItemInserted(medialist.size() - 1);
        notifyDataSetChanged();
    }

    public void updateSwapInfo(CMCCStreamInfo clickInfo, CMCCStreamInfo swapInfo){
        String clickKey = clickInfo.getUId() + clickInfo.getMediaType().toString();
        String swapKey = swapInfo.getUId() + swapInfo.getMediaType().toString();
        URTCVideoViewInfo oldBean = mStreamViews.remove(clickKey);
        URTCVideoViewInfo newBean = new URTCVideoViewInfo(swapInfo);
        Log.d(TAG, "updateSwapInfo: old bean render: "+ oldBean.getmRenderview());
        newBean.setmRenderview(oldBean.getmRenderview());
        mStreamViews.put(swapKey,newBean);

        int clickIndex = medialist.indexOf(clickKey);
        Log.d(TAG, "updateSwapInfo: old medialist index: "+ medialist.indexOf(clickKey));
        medialist.set(clickIndex,swapKey);
    }

//    public UCloudRtcSdkStreamInfo getStreamInfo(int position) {
//        UCloudRtcSdkStreamInfo streamInfo = null;
//        if (medialist.size() > position && mStreamViews.size() > position) {
//            streamInfo = new UCloudRtcSdkStreamInfo();
//            streamInfo.setMediaType(mStreamViews.get(medialist.get(position)).getmMediatype());
//            streamInfo.setHasAudio(mStreamViews.get(medialist.get(position)).isEnableAudio());
//            streamInfo.setHasVideo(mStreamViews.get(medialist.get(position)).ismEanbleVideo());
//            streamInfo.setUid(mStreamViews.get(medialist.get(position)).getmUid());
//        }
//        return streamInfo;
//    }

    public void removeStreamView(String mkey) {
        if (mStreamViews.containsKey(mkey)) {
            Log.d(TAG, " removeStreamView key: " + mkey);
            releaseVideoContainerRes(mkey);
            mStreamViews.remove(mkey);
            medialist.remove(mkey);
//            notifyItemRemoved(index);
//            notifyItemRangeChanged(index, getItemCount());
//            notifyItemRemoved(index);
            Log.d(TAG, " remove finished ,mStreamViews size: " + mStreamViews.size() + "medialist size: " + medialist.size());
        }
        notifyDataSetChanged();
    }

    public void clearAll() {
        for (String streamId : mStreamViews.keySet()) {
//            releaseVideoContainerRes(streamId);
        }
        medialist.clear();
        mStreamViews.clear();

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
            Object release = viewInfo.release();
            Log.d(TAG, "releaseVideoContainerRes: release cache " + release);
            cacheRender.add(release);
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

    public interface SwapInterface{
        View.OnClickListener provideSwapListener();

        boolean isLocalStream(String uid);

        void stopRender(URTCVideoViewInfo info);
    }
}
