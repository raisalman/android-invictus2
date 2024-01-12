package net.invictusmanagement.invictuslifestyle.activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;

public class SingleVideoImageActivity extends AppCompatActivity {

    BetterVideoPlayer player;
    ImageView imageView;
    MaintenanceRequesFiles maintenanceRequesFiles;
    ExoPlayer exoPlayer;
    PlayerView simpleExoPlayer;
    ImageView imgClose;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_video_image);
        getSupportActionBar().hide();

        initViews();
        getBundleData();

        TrackSelector trackSelector = new DefaultTrackSelector(SingleVideoImageActivity.this, new AdaptiveTrackSelection.Factory());
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        exoPlayer = new ExoPlayer.Builder(this).build();

        if (maintenanceRequesFiles.isImage) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnTouchListener(new ImageMatrixTouchHandler(imageView.getContext()));
            Glide.with(getApplicationContext()).load(maintenanceRequesFiles.maintenanceRequestImageSrc).apply(options).into(imageView);
        } else {
            simpleExoPlayer.setVisibility(View.VISIBLE);
            Log.d("SingleVideoPlay", "Video Uri-->" + maintenanceRequesFiles.maintenanceRequestImageSrc);
            Uri video = Uri.parse(maintenanceRequesFiles.maintenanceRequestImageSrc);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(SingleVideoImageActivity.this,
                    Util.getUserAgent(this, getString(R.string.app_name)));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaItem mediaItem = MediaItem.fromUri(maintenanceRequesFiles.maintenanceRequestImageSrc);
//            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            simpleExoPlayer.setPlayer(exoPlayer);
            simpleExoPlayer.setShowNextButton(false);
            simpleExoPlayer.setShowPreviousButton(false);
//            exoPlayer.prepare(mediaSource);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
            if (maintenanceRequesFiles.isFromAd) {
                exoPlayer.seekTo(maintenanceRequesFiles.duration);
            }

            /*player.setVisibility(View.VISIBLE);*/
            /*player.setSource(Uri.parse(maintenanceRequesFiles.maintenanceRequestImageSrc));*/
        }

    }

    private void initViews() {
        player = findViewById(R.id.player);
        simpleExoPlayer = findViewById(R.id.idExoPlayerVIew);
        imageView = findViewById(R.id.imageView);
        imgClose = findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maintenanceRequesFiles.isFromAd) {
//                    HomeFragment.newInstance().startAutoScrollViewPager();
                    MediaCenterActivity.getInstance().startAutoScrollViewPager();
                }
                finish();
            }
        });
    }

    private void getBundleData() {
        maintenanceRequesFiles = new Gson().fromJson(getIntent().getStringExtra("files"), new TypeToken<MaintenanceRequesFiles>() {
        }.getType());
    }


    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
        exoPlayer.pause();
    }

    @Override
    public void onBackPressed() {
        if (maintenanceRequesFiles.isFromAd) {
//            HomeFragment.newInstance().startAutoScrollViewPager();
            MediaCenterActivity.getInstance().startAutoScrollViewPager();
        }
        finish();
    }
}

