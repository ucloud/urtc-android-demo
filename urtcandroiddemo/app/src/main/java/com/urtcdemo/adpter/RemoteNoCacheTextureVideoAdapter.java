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

import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderTextureView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCFirstFrameRendered;
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

public class RemoteNoCacheTextureVideoAdapter extends RecyclerView.Adapter<RemoteNoCacheTextureVideoAdapter.ViewHolder> {
    public static final String TAG = " NoCacheTextureAdapter ";
    private HashMap<String, URTCVideoViewInfo> mStreamViews = new HashMap<>();
    private Set<Object> cacheRender = new HashSet<>();
    private ArrayList<String> medialist = new ArrayList<>();
    protected final LayoutInflater mInflater;
    private Context mContext;
    private List<ViewHolder> mCacheHolder;
    private SwapInterface mSwapInterface;
    private UCloudRtcSdkEngine mSdkEngine;


    public RemoteNoCacheTextureVideoAdapter(Context context, UCloudRtcSdkEngine sdkEngine, SwapInterface provider) {
        mContext = context;
        mSdkEngine = sdkEngine;
        mSwapInterface = provider;
        mInflater = ((Activity) context).getLayoutInflater();
        mCacheHolder = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, TAG + "onCreateViewHolder");
        View v = mInflater.inflate(R.layout.remote_empty_texture, parent, false);
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
        String mkey = medialist.get(position);
        URTCVideoViewInfo viewInfo = mStreamViews.get(mkey);
        holder.setIsRecyclable(false);
        Log.d(TAG, TAG + "onBindViewHolder + " + position);
        FrameLayout holderView = (FrameLayout) holder.itemView;
        if (holderView != null) {
            if (holderView.getChildCount() != 0) {
                View view = holderView.getChildAt(0);
                if(view instanceof TextureView){
                    if(view.getTag(R.id.render)!= null) {
                        UCloudRtcRenderTextureView render = (UCloudRtcRenderTextureView)view.getTag(R.id.render);
                        if(view.getTag()!= null && view.getTag() instanceof UCloudRtcSdkStreamInfo){
                            URTCVideoViewInfo oldInfo = new URTCVideoViewInfo();
                            oldInfo.setRenderview(render);
                            oldInfo.setStreamInfo((UCloudRtcSdkStreamInfo)view.getTag());
                            Log.d(TAG, "onBindViewHolder: clean old cache view ,it's info " + view.getTag() + " render" + render);
                            mSwapInterface.stopRender(oldInfo);
                        }
                        render.release();
                    }
                }
                holderView.removeAllViews();
            }
        }

        if (viewInfo == null) {
            return;
        }
        TextureView videoView = new TextureView(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        holderView.addView(videoView, layoutParams);
        UCloudRtcRenderTextureView render = new UCloudRtcRenderTextureView(videoView);
        render.init();
        videoView.setTag(R.id.render,render);
        if (videoView != null) {

//            ViewParent parent = videoView.getParent();
//            if (parent != null) {
//                ((FrameLayout) parent).removeView(videoView);
//            }
            videoView.setTag(R.id.index, viewInfo);
            videoView.setTag(viewInfo.getStreamInfo());
            boolean isLocal = false;
            if(mSwapInterface != null){
                videoView.setOnClickListener(mSwapInterface.provideSwapListener());
                isLocal = mSwapInterface.isLocalStream(viewInfo.getUid());
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
//
            viewInfo.setRenderview(render);
            if(isLocal){
                mSdkEngine.renderLocalView(viewInfo.getStreamInfo(), render, null, new UCloudRTCFirstFrameRendered(){
                    @Override
                    public void onFirstFrameRender(UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo, View view) {
                        Log.d(TAG, "onlocal first frame render: " + "view: " + view);
                    }
                });
            }else{
                mSdkEngine.startRemoteView(viewInfo.getStreamInfo(), render, null, new UCloudRTCFirstFrameRendered(){

                    @Override
                    public void onFirstFrameRender(UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo, View view) {
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

    public void updateSwapInfo(UCloudRtcSdkStreamInfo clickInfo,UCloudRtcSdkStreamInfo swapInfo){
        String clickKey = clickInfo.getUId() + clickInfo.getMediaType().toString();
        String swapKey = swapInfo.getUId() + swapInfo.getMediaType().toString();
        URTCVideoViewInfo oldBean = mStreamViews.remove(clickKey);
        URTCVideoViewInfo newBean = new URTCVideoViewInfo(swapInfo);
        Log.d(TAG, "updateSwapInfo: old bean render: "+ oldBean.getRenderview());
        newBean.setRenderview(oldBean.getRenderview());
        mStreamViews.put(swapKey,newBean);

        int clickIndex = medialist.indexOf(clickKey);
        Log.d(TAG, "updateSwapInfo: old medialist index: "+ medialist.indexOf(clickKey));
        medialist.set(clickIndex,swapKey);
    }

//    public UCloudRtcSdkStreamInfo getStreamInfo(int position) {
//        UCloudRtcSdkStreamInfo streamInfo = null;
//        if (medialist.size() > position && mStreamViews.size() > position) {
//            streamInfo = new UCloudRtcSdkStreamInfo();
//            streamInfo.setMediaType(mStreamViews.get(medialist.get(position)).getMediaType());
//            streamInfo.setHasAudio(mStreamViews.get(medialist.get(position)).isEnableAudio());
//            streamInfo.setHasVideo(mStreamViews.get(medialist.get(position)).isEnableVideo());
//            streamInfo.setUid(mStreamViews.get(medialist.get(position)).getUid());
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
