package com.xiaoxuan.exoplayerdemo;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.xiaoxuan.exoplayerdemo.test2.MyDataSourceFactory;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                initPlayer();
            }
        }, 2000);
    }
    
    private void initPlayer()
    {
        PlayerView playerView = findViewById(R.id.player_view);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        MyDataSourceFactory myDataSourceFactory =
            new MyDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayerTest-master"), bandwidthMeter);
        MediaSource videoSource = new ProgressiveMediaSource.Factory(myDataSourceFactory)
            .createMediaSource(Uri.parse("/android_asset/test.ld6"));
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        playerView.setPlayer(player);
        playerView.setControllerShowTimeoutMs(0);
        playerView.setControllerAutoShow(false);
        player.prepare(videoSource);
    }
}