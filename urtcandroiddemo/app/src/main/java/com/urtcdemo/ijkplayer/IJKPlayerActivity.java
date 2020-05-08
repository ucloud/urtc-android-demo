package com.urtcdemo.ijkplayer;
import com.urtcdemo.R;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TableLayout;

import com.urtcdemo.ijkplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IJKPlayerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    VideoAdapter adapter;
    IjkVideoView videoView, videoView2;
    TableLayout hud_view;
    Toolbar toolbar;
    String url="rtsp://admin:12345678q@145.255.18.220:554/ISAPI/Streaming/channels/101";
    String url2="rtsp://admin:12345678q@145.255.18.220:554/ISAPI/Streaming/channels/401";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijkplayer);
       // videoView=(IjkVideoView) findViewById(R.id.video_view);
       // hud_view=(TableLayout) findViewById(R.id.hud_view);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler);
        adapter = new VideoAdapter(this, DataUtil.getVideoListData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        videoView.release(false);

        videoView2.pause();
        videoView2.release(false);
    }


}
