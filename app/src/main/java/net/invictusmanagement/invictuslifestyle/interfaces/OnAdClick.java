package net.invictusmanagement.invictuslifestyle.interfaces;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;

import net.invictusmanagement.invictuslifestyle.models.CouponsAdvertisement;

import java.util.List;

public interface OnAdClick {
    void onAdClick(List<CouponsAdvertisement> couponsAdvertisements, int position, PlayerView simpleExoPlayer, ExoPlayer exoPlayer, TrackSelector trackSelector, long duration);
}
