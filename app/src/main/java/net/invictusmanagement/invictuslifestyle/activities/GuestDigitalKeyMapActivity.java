package net.invictusmanagement.invictuslifestyle.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.ViewMapsPagerAdapter;

public class GuestDigitalKeyMapActivity extends AppCompatActivity {

    private ImageView imgMap, imgClose;
    private ViewPager viewPager;
    private LinearLayout llPagerDots;
    private String[] imageMaps;

    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();
    private ImageView[] ivArrayDotsPager;
    private ViewMapsPagerAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_digital_key_map);

        imgMap = findViewById(R.id.imgMap);
        imgClose = findViewById(R.id.imgClose);
        viewPager = findViewById(R.id.view_pager);
        llPagerDots = findViewById(R.id.pager_dots);
        imgMap.setOnTouchListener(new ImageMatrixTouchHandler(imgMap.getContext()));
        imageMaps = getIntent().getExtras().getStringArray("imageMaps");
//        Glide.with(GuestDigitalKeyMapActivity.this)
//                .load(getIntent().getExtras().getString("imageMap")).apply(options)
//                .into(imgMap);

        adapter = new ViewMapsPagerAdapter(this, imageMaps);
        viewPager.setAdapter(adapter);

        setupPagerIndicatorDots();

        ivArrayDotsPager[0].setImageResource(R.drawable.viewpager_selected_dot_blue);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (ImageView imageView : ivArrayDotsPager) {
                    imageView.setImageResource(R.drawable.viewpager_un_selected_dot_blue);
                }
                ivArrayDotsPager[position].setImageResource(R.drawable.viewpager_selected_dot_blue);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupPagerIndicatorDots() {
        ivArrayDotsPager = new ImageView[imageMaps.length];
        for (int i = 0; i < ivArrayDotsPager.length; i++) {
            ivArrayDotsPager[i] = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            ivArrayDotsPager[i].setLayoutParams(params);
            ivArrayDotsPager[i].setImageResource(R.drawable.viewpager_un_selected_dot_blue);
            //ivArrayDotsPager[i].setAlpha(0.4f);
            ivArrayDotsPager[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(1);
                }
            });
            llPagerDots.addView(ivArrayDotsPager[i]);
            llPagerDots.bringToFront();
        }
    }
}