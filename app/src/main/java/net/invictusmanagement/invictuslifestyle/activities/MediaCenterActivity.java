package net.invictusmanagement.invictuslifestyle.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.gson.Gson;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.CouponsAdsViewpagerAdapter;
import net.invictusmanagement.invictuslifestyle.interfaces.OnAdClick;
import net.invictusmanagement.invictuslifestyle.interfaces.OnAdClickImage;
import net.invictusmanagement.invictuslifestyle.models.CouponsAdvertisement;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

public class MediaCenterActivity extends AppCompatActivity implements OnAdClick, OnAdClickImage {

    private CouponsAdsViewpagerAdapter couponsAddViewPager;
    private List<CouponsAdvertisement> couponsAdvertisementArrayList = new ArrayList<>();
    private Handler scrollHandler = new Handler(Looper.getMainLooper());
    private ViewPager2 addViewPager;
    private DotsIndicator dots_indicator;

    private ExoPlayer exoPlayerAdd;
    private boolean isAddImage = true;

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            addViewPager.setCurrentItem(addViewPager.getCurrentItem() + 1);
        }
    };
    public Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            addViewPager.setCurrentItem(0);
            couponsAddViewPager.notifyDataSetChanged();
        }
    };
    private ImageView imgClose;
    private static MediaCenterActivity instance;

    public static MediaCenterActivity getInstance() {
        return instance;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_center);

        instance = this;
        dots_indicator = findViewById(R.id.dots_indicator);
        addViewPager = findViewById(R.id.addViewPager);
        imgClose = findViewById(R.id.imgClose);

        imgClose.setOnClickListener(v -> finish());

        callAdvertisements();
    }

    public void callAdvertisements() {
        WebService.getInstance().getAdvertisements(new RestCallBack<List<CouponsAdvertisement>>() {
            @Override
            public void onResponse(List<CouponsAdvertisement> response) {
                couponsAdvertisementArrayList = response;
                if (couponsAdvertisementArrayList.size() > 0)
                    setViewPagerData();
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void setViewPagerData() {
        addViewPager.setVisibility(View.VISIBLE);
        dots_indicator.setVisibility(View.VISIBLE);
        couponsAddViewPager = new CouponsAdsViewpagerAdapter(MediaCenterActivity.this,
                couponsAdvertisementArrayList, addViewPager, dots_indicator,
                this, this);
        addViewPager.setAdapter(couponsAddViewPager);
        dots_indicator.setViewPager2(addViewPager);

        addViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position != (couponsAdvertisementArrayList.size() - 1)) {
                    if (couponsAdvertisementArrayList.get(position + 1).isImage) {
                        scrollHandler.removeCallbacks(runnable);
                        scrollHandler.removeCallbacks(runnable2);
                        scrollHandler.postDelayed(runnable, 10000);
                    } else {
                        scrollHandler.removeCallbacks(runnable);
                        scrollHandler.removeCallbacks(runnable2);
                        scrollHandler.postDelayed(runnable, 5000);
                    }
                } else {
                    if (couponsAdvertisementArrayList.get(0).isImage) {
                        scrollHandler.removeCallbacks(runnable);
                        scrollHandler.removeCallbacks(runnable2);
                        scrollHandler.postDelayed(runnable2, 10000);
                    } else {
                        scrollHandler.removeCallbacks(runnable);
                        scrollHandler.removeCallbacks(runnable2);
                        scrollHandler.postDelayed(runnable2, 5000);
                    }
                }
            }
        });
        if (couponsAddViewPager != null) {
            addViewPager.setCurrentItem(0);
            couponsAddViewPager.notifyDataSetChanged();
        }
    }

    @Override
    public void onAdClick(List<CouponsAdvertisement> couponsAdvertisements, int position,
                          PlayerView simpleExoPlayer, ExoPlayer exoPlayer,
                          TrackSelector trackSelector, long duration) {

        MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
        if (!couponsAdvertisements.get(position).isImage) {
            isAddImage = false;
            exoPlayerAdd = exoPlayer;
            stopAutoScrollViewPager();
            maintenanceRequesFiles.isImage = false;
            maintenanceRequesFiles.isFromAd = true;
            maintenanceRequesFiles.duration = duration;
            maintenanceRequesFiles.maintenanceRequestImageSrc = couponsAdvertisements.get(position).advertiseFileSrc + ".mp4";
            Intent i = new Intent(TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
            i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
            startActivity(i);
        }
    }

    @Override
    public void onAdClickImage(List<CouponsAdvertisement> couponsAdvertisements, int position) {
        MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
        if (couponsAdvertisements.get(position).isImage) {
            isAddImage = true;
            stopAutoScrollViewPager();
            maintenanceRequesFiles.isImage = true;
            maintenanceRequesFiles.isFromAd = true;
            maintenanceRequesFiles.maintenanceRequestImageSrc = couponsAdvertisements.get(position).advertiseFileSrc + ".jpg";
            Intent i = new Intent(TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
            i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
            startActivity(i);
        }
    }

    public void stopAutoScrollViewPager() {
        if (addViewPager != null) {
            scrollHandler.removeCallbacks(runnable);
            scrollHandler.removeCallbacks(runnable2);
        }
    }

    public void startAutoScrollViewPager() {
        if (addViewPager != null) {
            if (addViewPager.getCurrentItem() == (couponsAdvertisementArrayList.size() - 1)) {
                if (couponsAdvertisementArrayList.get(0).isImage) {
                    scrollHandler.postDelayed(runnable2, 10000);
                } else {
                    scrollHandler.postDelayed(runnable2, 5000);
                }
            } else {
                scrollHandler.postDelayed(runnable, 5000);
            }

            if (exoPlayerAdd != null) {
                if (!isAddImage)
                    exoPlayerAdd.play();
            }
        }
    }
}
