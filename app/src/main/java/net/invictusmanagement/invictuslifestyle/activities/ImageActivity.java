package net.invictusmanagement.invictuslifestyle.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.ImageAdapter;
import net.invictusmanagement.invictuslifestyle.interfaces.ImageVisibleOther;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceImage;

import java.util.List;

public class ImageActivity extends AppCompatActivity implements ImageVisibleOther {

    private TextView tvImageNumber;
    private List<MarketPlaceImage> galImages;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ViewPager imageViewPager = findViewById(R.id.imageViewPager);
        ImageView imgClose = findViewById(R.id.imgClose);
        tvImageNumber = findViewById(R.id.tvImageNumber);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getIntent().getExtras() != null) {
            int selectedItem = getIntent().getExtras().getInt("selectedItem");
            galImages = new Gson().fromJson(getIntent().getStringExtra("imageList"), new TypeToken<List<MarketPlaceImage>>() {
            }.getType());
            ImageAdapter imageAdapter = new ImageAdapter(ImageActivity.this, galImages, this, false, false, true);
            imageViewPager.setAdapter(imageAdapter);
            imageViewPager.setCurrentItem(selectedItem);
            tvImageNumber.setText(selectedItem + 1 + "/" + galImages.size());
            imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    tvImageNumber.setText(position + 1 + "/" + galImages.size());
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }

    }

    @Override
    public void showImage(List<MarketPlaceImage> galImages, int position) {

    }
}