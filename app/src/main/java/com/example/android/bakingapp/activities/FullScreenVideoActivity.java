package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.example.android.bakingapp.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class FullScreenVideoActivity extends AppCompatActivity {
    public final static String VIDEO_URL="video_url";
    public final static String PLAYBACK_POSITION="playback_position";
    public final static String GET_PLAY_WHEN_READY="get_play_when_ready";

    //Ui elements
    private SimpleExoPlayerView simpleExoPlayerView;//the video
    //to create the Player and play the video in the SimpleExoPlayerView
    private SimpleExoPlayer simpleExoPlayer;

    private String stepVideoURL;
    private long playbackPosition;
    private boolean getPlayWhenReady;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        initializations();
        if(savedInstanceState!=null&&savedInstanceState.containsKey(VIDEO_URL)&&
                savedInstanceState.containsKey(PLAYBACK_POSITION)&&
                savedInstanceState.containsKey(GET_PLAY_WHEN_READY)){
            getDataFomBundle(savedInstanceState);
        }else{
        getDataFromIntent();}
    }

    private void getDataFomBundle(Bundle savedInstanceState) {
        stepVideoURL=savedInstanceState.getString(VIDEO_URL);
        playbackPosition=savedInstanceState.getLong(PLAYBACK_POSITION,0);
        getPlayWhenReady=savedInstanceState.getBoolean(GET_PLAY_WHEN_READY);
    }

    private void getDataFromIntent() {
        Intent intent=getIntent();
        stepVideoURL=intent.getStringExtra(VIDEO_URL);
        playbackPosition=intent.getLongExtra(PLAYBACK_POSITION,0);
        getPlayWhenReady=intent.getBooleanExtra(GET_PLAY_WHEN_READY,false);
    }

    private void initializations() {
        playbackPosition=0;
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.fullScreenVideoActivity_simpleExoPlayerView);
    }
    private void setVideo() {
        if (stepVideoURL != null && !stepVideoURL.equals("")) {
            simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.recipe_placeholder));
            initializePlayer();
        } else {
            simpleExoPlayerView.setVisibility(View.GONE);
        }
    }
    private void initializePlayer() {
        if (simpleExoPlayer == null) {
            createThePlayer();
            attachThePlayerToTheView();
            prepareThePlayer();
        }
    }
    private void createThePlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
    }

    private void attachThePlayerToTheView() {
        simpleExoPlayerView.setPlayer(simpleExoPlayer);
    }

    private void prepareThePlayer() {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "bakingApp"), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource that is responsable for loading the media and providing it to the player.
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(stepVideoURL),
                dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        simpleExoPlayer.prepare(videoSource);
        if (playbackPosition != 0) {
            //the video starts in the correct position
            simpleExoPlayer.seekTo(playbackPosition);
        }
        if(getPlayWhenReady){
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (stepVideoURL != null && !TextUtils.isEmpty(stepVideoURL)) {
            if(simpleExoPlayer!=null){
            playbackPosition=simpleExoPlayer.getCurrentPosition();
            getPlayWhenReady=simpleExoPlayer.getPlayWhenReady();}
            releaseThePlayer();
        }
    }
    /**
     * It's important to release the player when playback is completed because the player
     * holds system resources
     */
    private void releaseThePlayer() {
        simpleExoPlayer.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(GET_PLAY_WHEN_READY,simpleExoPlayer.getPlayWhenReady());
        outState.putLong(PLAYBACK_POSITION,simpleExoPlayer.getCurrentPosition());
        outState.putString(VIDEO_URL,stepVideoURL);
        simpleExoPlayer=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVideo();
    }

}
