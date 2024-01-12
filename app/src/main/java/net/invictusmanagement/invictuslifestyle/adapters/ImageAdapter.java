package net.invictusmanagement.invictuslifestyle.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.ImageVisibleOther;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceImage;

import java.util.List;

public class ImageAdapter extends PagerAdapter {
    Context context;
    private final List<MarketPlaceImage> GalImages;

    LayoutInflater mLayoutInflater;
    ImageVisibleOther imageVisibleOther;
    Boolean isZoomable, isClickable, isCenterCrop;

    public ImageAdapter(Context context, List<MarketPlaceImage> GalImages,
                        ImageVisibleOther imageVisibleOther, Boolean isCenterCrop,
                        Boolean isClickable, Boolean isZoomable) {
        this.context = context;
        this.isZoomable = isZoomable;
        this.isCenterCrop = isCenterCrop;
        this.isClickable = isClickable;
        this.imageVisibleOther = imageVisibleOther;
        this.GalImages = GalImages;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return GalImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        if (isZoomable) {
            imageView.setOnTouchListener(new ImageMatrixTouchHandler(imageView.getContext()));
        }
        Glide.with(context)
                .load(GalImages.get(position).marketPlaceImageUrl)
                .placeholder(R.drawable.img_service_list)
                .into(imageView);

        if (isClickable) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageVisibleOther.showImage(GalImages, position);
                }
            });
        } else {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }


        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
