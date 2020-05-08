package com.urtcdemo.ijkplayer;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.urtcdemo.R;
import com.urtcdemo.ijkplayer.media.AndroidMediaController;
import com.urtcdemo.ijkplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by XiaoJianjun on 2017/5/21.
 */

public class VideoViewHolder extends RecyclerView.ViewHolder {

    public AndroidMediaController mController;
    public IjkVideoView mVideoPlayer;
    public TableLayout mHudView;

    public VideoViewHolder(View itemView) {
        super(itemView);
        mVideoPlayer = itemView.findViewById(R.id.video_view);
        mHudView = itemView.findViewById(R.id.hud_view);
        // 将列表中的每个视频设置为默认16:9的比例
        ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();
        params.width = itemView.getResources().getDisplayMetrics().widthPixels; // 宽度为屏幕宽度
        params.height = (int) (params.width * 9f / 16f);    // 高度为宽度的9/16
        mVideoPlayer.setLayoutParams(params);
    }

    public void setController(AndroidMediaController controller) {
        mController = controller;
    }

    public void bindData(Video video) {
        mVideoPlayer.setVideoURI(Uri.parse(video.getVideoUrl()));
        mVideoPlayer.setHudView(mHudView);
        mVideoPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mController.setAnchorView(mVideoPlayer);
                mVideoPlayer.setMediaController(mController);
                mVideoPlayer.start();
            }
        });
    }
}
