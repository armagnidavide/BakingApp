package com.example.android.bakingapp;


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
import com.squareup.picasso.Picasso;


public class StepDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String STEP_DESCRIPTION = "step_description";
    public static final String STEP_VIDEO_URL = "step_video_url";
    public static final String STEP_IMAGE_URL = "step_image_url";

    private TextView txtVwStepDescription;
    private ImageView imgVwStepImage;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer simpleExoPlayer;
    private String stepDescription;
    private String stepVideoURL;
    private String stepImageURL;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        getDataFromBundle(arguments);
    }


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
        initializations(rootView);
        setVideo();
        setImage();
        setDescription();
        return rootView;
    }

    private void initializations(View rootView) {
        simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.simpleExoPlayerView);
        txtVwStepDescription = (TextView) rootView.findViewById(R.id.txtVw_recipe_step_detail_description);
        imgVwStepImage = (ImageView) rootView.findViewById(R.id.imgVw_recipe_step_image);
    }

    private void setVideo() {
        simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.recipe_placeholder));
        if (stepVideoURL != null && !stepVideoURL.equals("")) {
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


    private void initializePlayer() {
        if (simpleExoPlayer == null) {
            creatingThePlayer();
            attachingPlayerToTheView();
            preparingThePlayer();
        }
    }

    private void creatingThePlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());
    }

    private void attachingPlayerToTheView() {
        simpleExoPlayerView.setPlayer(simpleExoPlayer);

    }

    private void preparingThePlayer() {
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
        //the video start when it's ready
        simpleExoPlayer.setPlayWhenReady(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (stepVideoURL != null && !stepVideoURL.equals("")) {
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

}