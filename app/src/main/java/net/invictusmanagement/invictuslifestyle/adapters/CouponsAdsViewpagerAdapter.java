package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.interfaces.OnAdClick;
import net.invictusmanagement.invictuslifestyle.interfaces.OnAdClickImage;
import net.invictusmanagement.invictuslifestyle.models.CouponsAdvertisement;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CouponsAdsViewpagerAdapter extends RecyclerView.Adapter<CouponsAdsViewpagerAdapter.ViewHolder> {

    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();
    Context context;
    List<CouponsAdvertisement> couponsAdvertisements;
    List<CouponsAdvertisement> couponsAdvertisements2;
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            couponsAdvertisements = new ArrayList<>();
            couponsAdvertisements.addAll(couponsAdvertisements2);
            notifyDataSetChanged();
        }
    };
    OnAdClick onAdClick;
    OnAdClickImage onAdClickImage;
    ViewPager2 addViewPager;
    DotsIndicator dots_indicator;

    public CouponsAdsViewpagerAdapter(Context context, List<CouponsAdvertisement> couponsAdvertisements, ViewPager2 addViewPager, DotsIndicator dots_indicator, OnAdClick onAdClick, OnAdClickImage onAdClickImage) {
        this.dots_indicator = dots_indicator;
        this.addViewPager = addViewPager;
        this.onAdClick = onAdClick;
        this.onAdClickImage = onAdClickImage;
        this.context = context;
        this.couponsAdvertisements = couponsAdvertisements;
        this.couponsAdvertisements2 = couponsAdvertisements;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_coupons_add, parent, false);
        return new ViewHolder(view);
    }

    public ExoPlayer getExoPlayer() {
        return new ViewHolder(addViewPager.getRootView()).exoPlayer;
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull CouponsAdsViewpagerAdapter.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        holder.item = couponsAdvertisements.get(position);
        if (holder.item != null) {
            holder.releasePlayer();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = couponsAdvertisements.get(position);
        holder.mediaPlay.setVisibility(View.GONE);


        if (holder.item.isImage) {
            holder.imgAd.setVisibility(View.VISIBLE);
            holder.simpleExoPlayer.setVisibility(View.GONE);
            holder.mediaPlay.setVisibility(View.GONE);
            Glide.with(context).load(couponsAdvertisements.get(position).advertiseFileSrc + ".jpg").apply(options).into(holder.imgAd);
        } else {
            holder.simpleExoPlayer.setVisibility(View.VISIBLE);
            holder.imgAd.setVisibility(View.GONE);
            holder.mediaPlay.setVisibility(View.GONE);
            holder.releasePlayer();
            holder.intiPlayer(couponsAdvertisements.get(position).advertiseFileSrc + ".mp4");
        }

        holder.imgVideoFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!couponsAdvertisements.get(position).isImage) {
                    holder.exoPlayer.pause();
                    onAdClick.onAdClick(couponsAdvertisements, position, holder.simpleExoPlayer, holder.exoPlayer, holder.trackSelector, holder.exoPlayer.getCurrentPosition());
                    /*holder.mediaPlay.setVisibility(View.VISIBLE);*/
                }

            }
        });

        holder.imgVolumeController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!couponsAdvertisements.get(position).isImage)
                    if (holder.exoPlayer.getAudioComponent().getVolume() == 0f) {
                        holder.exoPlayer.getAudioComponent().setVolume(1f);
                        holder.imgVolumeController.setImageDrawable(ContextCompat.getDrawable(TabbedActivity.tabbedActivity, R.drawable.ic_volume_up));
                    } else {
                        holder.exoPlayer.getAudioComponent().setVolume(0f);
                        holder.imgVolumeController.setImageDrawable(ContextCompat.getDrawable(TabbedActivity.tabbedActivity, R.drawable.ic_volume_off));
                    }

            }
        });

        if (couponsAdvertisements.get(position).isImage) {
            holder.mediaController.setVisibility(View.GONE);
        } else {
            holder.mediaController.setVisibility(View.VISIBLE);
        }
        holder.clMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponsAdvertisements.get(position).isImage)
                    onAdClickImage.onAdClickImage(couponsAdvertisements, position);
            }
        });
        holder.mediaPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.exoPlayer.play();
                holder.mediaPlay.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return couponsAdvertisements.size();
    }

    public int totalItemCount() {
        return couponsAdvertisements.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public ExoPlayer exoPlayer;
        ConstraintLayout clMain;
        ImageView imgAd;
        ImageView imgVolumeController;
        ImageView imgVideoFullScreen;
        ImageView imgVideoPlay;
        LinearLayout mediaController;
        LinearLayout mediaPlay;
        PlayerView simpleExoPlayer;
        CouponsAdvertisement item;
        TrackSelector trackSelector;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            trackSelector = new DefaultTrackSelector(TabbedActivity.tabbedActivity, new AdaptiveTrackSelection.Factory());
            exoPlayer = new ExoPlayer.Builder(context).build();
            imgAd = (ImageView) view.findViewById(R.id.imgAd);
            imgVolumeController = (ImageView) view.findViewById(R.id.imgVolumeController);
            imgVideoFullScreen = (ImageView) view.findViewById(R.id.imgVideoFullScreen);
            imgVideoPlay = (ImageView) view.findViewById(R.id.imgVideoPlay);
            mediaController = (LinearLayout) view.findViewById(R.id.mediaController);
            mediaPlay = (LinearLayout) view.findViewById(R.id.mediaPlay);
            simpleExoPlayer = (PlayerView) view.findViewById(R.id.idExoPlayerVIew);
            clMain = (ConstraintLayout) view.findViewById(R.id.clMain);
        }

        private void intiPlayer(String url) {
            try {
                trackSelector = new DefaultTrackSelector(TabbedActivity.tabbedActivity, new AdaptiveTrackSelection.Factory());
//                exoPlayer = ExoPlayerFactory.newSimpleInstance(TabbedActivity.tabbedActivity, trackSelector);
                MediaItem mediaSource = MediaItem.fromUri(url);
                Log.d("SingleVideoPlay", "Video Uri-->" + url);
                Uri video = Uri.parse(url);
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(TabbedActivity.tabbedActivity,
                        Util.getUserAgent(TabbedActivity.tabbedActivity, TabbedActivity.tabbedActivity.getString(R.string.app_name)));
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//                MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
                MediaItem mediaItem = MediaItem.fromUri(url);
                simpleExoPlayer.setPlayer(exoPlayer);
                simpleExoPlayer.setShowNextButton(false);
                simpleExoPlayer.setShowPreviousButton(false);
                simpleExoPlayer.hideController();
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                imgVolumeController.setImageDrawable(ContextCompat.getDrawable(TabbedActivity.tabbedActivity, R.drawable.ic_volume_off));
                exoPlayer.setVolume(0f);
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.setPlayWhenReady(true);
                simpleExoPlayer.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        simpleExoPlayer.getPlayer().play();
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        simpleExoPlayer.getPlayer().pause();

                    }
                });
            } catch (Exception e) {
                Log.e("MainAcvtivity", " exoplayer error " + e.toString());
            }
        }


        private boolean isPlaying() {
            return exoPlayer != null
                    && exoPlayer.getPlaybackState() != Player.STATE_ENDED
                    && exoPlayer.getPlaybackState() != Player.STATE_IDLE
                    && exoPlayer.getPlayWhenReady();
        }

        private void releasePlayer() {
            if (exoPlayer != null) {
                /*playbackPosition = exoPlayer.getCurrentPosition();
                currentWindow = exoPlayer.getCurrentWindowIndex();
                playWhenReady = exoPlayer.getPlayWhenReady();*/
                exoPlayer.release();
                exoPlayer = null;
            }
        }

    }
}
