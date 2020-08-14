package com.urtcdemo.utils;

/**
 * @author ciel
 * @create 2020/4/2
 * @Describe
 */
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Author: wangkai(wangkai@tv365.net)
 * Date: 2018-10-08
 * Time: 17:56
 * Description:
 */
public interface VideoListener extends IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener{
}
