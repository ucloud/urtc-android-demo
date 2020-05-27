package com.urtcdemo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.urtcdemo.R;
import com.urtcdemo.view.MonitorCamView;

public class MonitorActivity extends AppCompatActivity {
    private MonitorCamView localScreen = null;
    private MonitorCamView remoteScreen = null;
    private MonitorCamView firstRTSPScreen = null;
    private MonitorCamView secondRTSPScreen = null;
    private MonitorCamView thirdRTSPScreen = null;
    private MonitorCamView fourthRTSPScreen = null;
    private MonitorCamView hdmiScreen = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        localScreen = (MonitorCamView)findViewById(R.id.LocalScreenView);
        remoteScreen = (MonitorCamView)findViewById(R.id.RemoteScreenView);
        firstRTSPScreen = (MonitorCamView)findViewById(R.id.FirstRTSPView);
        secondRTSPScreen = (MonitorCamView)findViewById(R.id.SecondRTSPView);
        thirdRTSPScreen = (MonitorCamView)findViewById(R.id.ThirdRTSPView);
        fourthRTSPScreen = (MonitorCamView)findViewById(R.id.FourthRTSPView);
        hdmiScreen = (MonitorCamView)findViewById(R.id.HDMIView);
        localScreen.setBackgroundColor(Color.parseColor("#FF0000"));
        remoteScreen.setBackgroundColor(Color.parseColor("#FF6600"));
        firstRTSPScreen.setBackgroundColor(Color.parseColor("#FFFF00"));
        secondRTSPScreen.setBackgroundColor(Color.parseColor("#99FF00"));
        thirdRTSPScreen.setBackgroundColor(Color.parseColor("#33FFFF"));
        fourthRTSPScreen.setBackgroundColor(Color.parseColor("#3366FF"));
        hdmiScreen.setBackgroundColor(Color.parseColor("#330099"));
    }
}
