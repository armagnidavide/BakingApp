package com.example.android.bakingapp.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.activities.FullScreenVideoActivity;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;


public class StepDetailFragment extends Fragment {

    //constants to retrieve data from the  Bundle
    public static final String STEP_DESCRIPTION = "step_description";
    public static final String STEP_VIDEO_URL = "step_video_url";
    public static final String STEP_IMAGE_URL = "step_image_url";
    public static final String PLAYBACK_POSITION = "playback_position";
    public static final String GET_PLAY_WHEN_READY = "get_play_when_ready";

    //Ui elements
    private TextView txtVwStepDescription;//the description
    private ImageView imgVwStepImage;//the image
    private SimpleExoPlayerView simpleExoPlayerView;//the video

    //to create the Player and play the video in the SimpleExoPlayerView
    private SimpleExoPlayer simpleExoPlayer;

    //step's details
    private String stepDescription;
    private String stepVideoURL;
    private String stepImageURL;
    private long playbackPosition = 0;
    private boolean getPlayWhenReady;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        getDataFromBundle(arguments);
    }

    /**
     * get Step's details from the Bundle
     *
     * @param arguments the Bundle of the current Fragment
     */
    private void getDataFromBundle(Bundle arguments) {
        if (arguments.containsKey(STEP_DESCRIPTION) &&
                arguments.containsKey(STEP_VIDEO_URL) &&
                arguments.containsKey(STEP_IMAGE_URL)) {
            stepDescription = arguments.getString(STEP_DESCRIPTION);
            stepVideoURL = arguments.getString(STEP_VIDEO_URL);
            stepImageURL = arguments.getString(STEP_IMAGE_URL);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_detail_, container, false);
        if (savedInstanceState != null && savedInstanceState.containsKey(PLAYBACK_POSITION) && savedInstanceState.containsKey(GET_PLAY_WHEN_READY)) {
            getPlayWhenReady = savedInstanceState.getBoolean(GET_PLAY_WHEN_READY);
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
            stepVideoURL=savedInstanceState.getString(STEP_VIDEO_URL);

        }
        initializations(rootView);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && getPlayWhenReady) {
            startFullScreenActivity(playbackPosition, getPlayWhenReady);
        }
        return rootView;
    }


    private void initializations(View rootView) {
        simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.stepDetailFragment_simpleExoPlayerView);
        txtVwStepDescription = (TextView) rootView.findViewById(R.id.stepDetailFragment_txtVw_recipe_description);
        imgVwStepImage = (ImageView) rootView.findViewById(R.id.stepDetailFragment_imgVw_step_image);
    }

    private void setVideo() {
        if (stepVideoURL != null && !stepVideoURL.equals("")) {
            simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.recipe_placeholder));
            initializePlayer();
        } else {
            simpleExoPlayerView.setVisibility(View.GONE);
        }
    }

    private void setImage() {
        if (stepImageURL != null) {
            //Render image using Picasso library
            if (!TextUtils.isEmpty(stepImageURL)) {
                Picasso.with(this.getContext()).load(stepImageURL)
                        .error(R.drawable.recipe_placeholder)
                        .placeholder(R.drawable.recipe_placeholder)
                        .into(imgVwStepImage);
            } else {
                imgVwStepImage.setVisibility(View.GONE);
            }
        }
    }

    private void setDescription() {
        if (stepDescription != null) {
            txtVwStepDescription.setText(stepDescription);
        }

    }




    private void startFullScreenActivity(long playbackPosition, boolean getPlayWhenReady) {
        Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
        intent.putExtra(FullScreenVideoActivity.VIDEO_URL, stepVideoURL);
        intent.putExtra(FullScreenVideoActivity.PLAYBACK_POSITION, playbackPosition);
        intent.putExtra(FullScreenVideoActivity.GET_PLAY_WHEN_READY, getPlayWhenReady);
        startActivity(intent);
    }


    private void initializePlayer() {
        if (simpleExoPlayer == null) {
            createThePlayer();
            attachThePlayerToTheView();
            prepareThePlayer();
        }
    }

    private void createThePlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());
    }

    private void attachThePlayerToTheView() {
        simpleExoPlayerView.setPlayer(simpleExoPlayer);
    }

    private void prepareThePlayer() {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "bakingApp"), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource that is responsable for loading the media and providing it to the player.
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(stepVideoURL),
                dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        simpleExoPlayer.prepare(videoSource);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    startFullScreenActivity(simpleExoPlayer.getCurrentPosition(), simpleExoPlayer.getPlayWhenReady());
                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });

        if (playbackPosition != 0) {
            simpleExoPlayer.seekTo(playbackPosition);
            simpleExoPlayer.setPlayWhenReady(true);

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (stepVideoURL != null && !stepVideoURL.equals("")) {
            if(simpleExoPlayer!=null){
                getPlayWhenReady=simpleExoPlayer.getPlayWhenReady();
                playbackPosition=simpleExoPlayer.getCurrentPosition();

            }
            releaseThePlayer();
        }
    }



    /**
     * It's important to release the player when playback is completed because the player
     * holds system resources
     */
    private void releaseThePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(simpleExoPlayer!=null){
            playbackPosition=simpleExoPlayer.getCurrentPosition();
            getPlayWhenReady=simpleExoPlayer.getPlayWhenReady();
            outState.putLong(PLAYBACK_POSITION,simpleExoPlayer.getCurrentPosition() );
            outState.putBoolean(GET_PLAY_WHEN_READY, simpleExoPlayer.getPlayWhenReady());
            outState.putString(STEP_VIDEO_URL,stepVideoURL);
            simpleExoPlayer=null;
        }else{
            outState.putLong(PLAYBACK_POSITION,playbackPosition );
        outState.putBoolean(GET_PLAY_WHEN_READY, getPlayWhenReady);}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
            setVideo();
            setImage();
            setDescription();


    }
}