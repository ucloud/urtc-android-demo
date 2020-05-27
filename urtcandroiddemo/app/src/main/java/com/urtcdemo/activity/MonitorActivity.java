package com.urtcdemo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderView;
import com.urtcdemo.R;

public class MonitorActivity extends AppCompatActivity {
    private UCloudRtcRenderView localScreen = null;
    private UCloudRtcRenderView remoteScreen = null;
    private UCloudRtcRenderView firstRTSPScreen = null;
    private UCloudRtcRenderView secondRTSPScreen = null;
    private UCloudRtcRenderView thirdRTSPScreen = null;
    private UCloudRtcRenderView fourthRTSPScreen = null;
    private UCloudRtcRenderView hdmiScreen = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        localScreen = (UCloudRtcRenderView)findViewById(R.id.LocalMixView);
        remoteScreen = (UCloudRtcRenderView)findViewById(R.id.RemoteMixView);
        firstRTSPScreen = (UCloudRtcRenderView)findViewById(R.id.FirstRTSPView);
        secondRTSPScreen = (UCloudRtcRenderView)findViewById(R.id.SecondRTSPView);
        thirdRTSPScreen = (UCloudRtcRenderView)findViewById(R.id.ThirdRTSPView);
        fourthRTSPScreen = (UCloudRtcRenderView)findViewById(R.id.FourthRTSPView);
        hdmiScreen = (UCloudRtcRenderView)findViewById(R.id.HDMIView);
        localScreen.setBackgroundColor(Color.parseColor("#FF0000"));
        remoteScreen.setBackgroundColor(Color.parseColor("#FF6600"));
        firstRTSPScreen.setBackgroundColor(Color.parseColor("#FFFF00"));
        secondRTSPScreen.setBackgroundColor(Color.parseColor("#99FF00"));
        thirdRTSPScreen.setBackgroundColor(Color.parseColor("#33FFFF"));
        fourthRTSPScreen.setBackgroundColor(Color.parseColor("#3366FF"));
        hdmiScreen.setBackgroundColor(Color.parseColor("#330099"));
    }
}
