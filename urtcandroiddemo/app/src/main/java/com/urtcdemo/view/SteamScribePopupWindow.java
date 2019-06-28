package com.urtcdemo.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.urtcdemo.R;
import com.urtcdemo.adpter.SelectAdapter;
import com.urtclib.sdkengine.define.UCloudRtcSdkStreamInfo;

import java.util.List;

public class SteamScribePopupWindow extends PopupWindow {

    private Context mContext;
    private List<UCloudRtcSdkStreamInfo> mSteamList;
    RecyclerView mRecyclerview;
    Button mBtnScrbie;
    LinearLayoutManager layoutManager;
    SelectAdapter mAdapter;
    private OnSubscribeListener mOnSubScribeListener;

    public interface OnSubscribeListener {
        void onSubscribe(List<UCloudRtcSdkStreamInfo> selectStream);
    }

    public SteamScribePopupWindow(Context context,List<UCloudRtcSdkStreamInfo> streamInfos) {
        super(context);
        mContext = context;
        mSteamList = streamInfos;
        init();
    }

    public void setmOnSubScribeListener(OnSubscribeListener mOnSubScribeListener) {
        this.mOnSubScribeListener = mOnSubScribeListener;
    }

    public void notifyUpdate(){
        mAdapter.notifyDataSetChanged();
    }

    public void addStreamInfo(UCloudRtcSdkStreamInfo info,boolean notify){
        mAdapter.addStreamInfo(info,notify);
    }

    public UCloudRtcSdkStreamInfo findStreamInfoByUid(String uid){
       return mAdapter.findStreamInfoByUId(uid);
    }

    public void removeStreamInfoByUid(String uid){
        mAdapter.removeStreamInfoByUId(uid);
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.stream_scribe_popup_window, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.editTextBackground)));
        mRecyclerview =  view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(mContext);
        mRecyclerview.setLayoutManager(layoutManager);
        mAdapter = new SelectAdapter(mSteamList);
        mRecyclerview.setAdapter(mAdapter);
        mBtnScrbie = view.findViewById(R.id.btn_scribe);
        mBtnScrbie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<UCloudRtcSdkStreamInfo> data = mAdapter.getSelectedItem();
                if(mOnSubScribeListener !=null ){
                    mOnSubScribeListener.onSubscribe(data);
                }
            }
        });
    }

//    public static void fitPopupWindowOverStatusBar(PopupWindow mPopupWindow, boolean needFullScreen) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            try {
//                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
//                mLayoutInScreen.setAccessible(needFullScreen);
//                mLayoutInScreen.set(mPopupWindow, needFullScreen);
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
