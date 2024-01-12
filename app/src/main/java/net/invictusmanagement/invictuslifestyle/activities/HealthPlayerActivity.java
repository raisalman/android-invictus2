package net.invictusmanagement.invictuslifestyle.activities;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.models.HealthVideo;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import org.jetbrains.annotations.NotNull;

public class HealthPlayerActivity extends BaseActivity implements BetterVideoCallback {

    BetterVideoPlayer player;
    ExoPlayer exoPlayer;
    PlayerView simpleExoPlayer;

    TextView _title, _description, _date;
    ImageView _favorite, _thumbnail;
    HealthVideo healthVideoItem;
    String videoDate, videoId;
    ConstraintLayout _clDetails, _clThumbnail;
    Boolean isFavourite = false;
    Boolean isLandscape = false;
    Boolean isVideoPlaying = false;
    String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_player);

        toolbar();
        initViews();
        initExoPlayer();
        getBundleData();
        setData();
        onCLickListeners();
    }

    private void initExoPlayer() {
        TrackSelector trackSelector = new DefaultTrackSelector(HealthPlayerActivity.this, new AdaptiveTrackSelection.Factory());
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        exoPlayer = new ExoPlayer.Builder(this).build();
    }

    private void onCLickListeners() {

        _clThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_clThumbnail.getVisibility() == View.VISIBLE) {
                    _clThumbnail.setVisibility(View.GONE);
                    /*player.setVisibility(View.VISIBLE);*/
                    if (healthVideoItem != null) {
                        healthVideoItem.id = Long.parseLong(videoId);
                        isVideoPlaying = true;
                        /*player.setSource(Uri.parse(healthVideoItem.healthVideoUrl));*/

                        simpleExoPlayer.setVisibility(View.VISIBLE);
                        Log.d("SingleVideoPlay", "Video Uri-->" + Uri.parse(healthVideoItem.healthVideoUrl));
                        Uri video = Uri.parse(healthVideoItem.healthVideoUrl);
                        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(HealthPlayerActivity.this,
                                Util.getUserAgent(HealthPlayerActivity.this, getString(R.string.app_name)));
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//                        MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
                        MediaItem mediaItem = MediaItem.fromUri(healthVideoItem.healthVideoUrl);
                        simpleExoPlayer.setPlayer(exoPlayer);
                        simpleExoPlayer.setShowNextButton(false);
                        simpleExoPlayer.setShowPreviousButton(false);
                        exoPlayer.setMediaItem(mediaItem);
                        exoPlayer.prepare();
//                        exoPlayer.prepare(mediaSource);
                        exoPlayer.setPlayWhenReady(true);
                        videoMarkAsViewed(healthVideoItem);
                    }
                    if (isLandscape) {
                        _clDetails.setVisibility(View.GONE);
                    }
                }
            }
        });


        _favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                healthVideoItem.id = Long.parseLong(videoId);
                if (isFavourite) {
                    videoAsUnFavorite(healthVideoItem);
                } else {
                    videoAsFavorite(healthVideoItem);
                }
            }
        });

    }


    private void setData() {
        try {
            if (healthVideoItem != null) {
                isFavourite = healthVideoItem.isFavorite;

                Glide.with(this)
                        .load(healthVideoItem.healthVideoThumbnailUrl)
                        .placeholder(R.drawable.exomedia_ic_pause_white)
                        .into(_thumbnail);
                _title.setText(healthVideoItem.title);
                _description.setText(healthVideoItem.description);

                if (healthVideoItem.isFavorite) {
                    _favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite));
                } else {
                    _favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unfavorite));
                }

                _date.setText(videoDate);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            healthVideoItem = (HealthVideo) getIntent().getExtras().getSerializable("HEALTHVIEDEOITEM");
            videoDate = (String) getIntent().getExtras().getString("VIDEODATE");
            videoId = (String) getIntent().getExtras().getString("VIDEOID");
        }
    }

    private void initViews() {
        player = findViewById(R.id.player);
        simpleExoPlayer = findViewById(R.id.idExoPlayerVIew);

        _title = (TextView) findViewById(R.id.tvTile);
        _description = (TextView) findViewById(R.id.tvDescription);
        _date = (TextView) findViewById(R.id.tvDate);
        _favorite = findViewById(R.id.imgFavorite);
        _thumbnail = findViewById(R.id.imgThumbnail);
        _clDetails = findViewById(R.id.clDetails);
        _clThumbnail = findViewById(R.id.clThumbnail);
    }

    private void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
        exoPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        player.start();
        exoPlayer.play();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //ADDU
            if (isVideoPlaying) {
                _clDetails.setVisibility(View.GONE);
            }
            isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //UBHU
            _clDetails.setVisibility(View.VISIBLE);
            isLandscape = false;
        }

    }

    public void videoAsFavorite(final HealthVideo item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().videoAsFavorite(item);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    _favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite));
                    isFavourite = true;
                }
            }
        }.execute();
    }

    public void videoAsUnFavorite(final HealthVideo item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().videoAsUnFavorite(item);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    _favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unfavorite));
                    isFavourite = false;
                }
            }
        }.execute();
    }

    public void videoMarkAsViewed(final HealthVideo item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().videoAsViewed(item);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {

            }
        }.execute();
    }

    @Override
    public void onStarted(BetterVideoPlayer player) {

    }

    @Override
    public void onPaused(BetterVideoPlayer player) {

    }

    @Override
    public void onPreparing(BetterVideoPlayer player) {

    }

    @Override
    public void onPrepared(BetterVideoPlayer player) {
        player.start();

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(BetterVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(BetterVideoPlayer player) {

    }

    @Override
    public void onToggleControls(BetterVideoPlayer player, boolean isShowing) {

    }
}