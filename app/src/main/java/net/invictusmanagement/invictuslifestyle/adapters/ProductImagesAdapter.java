package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.DeleteSingleProductImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.ViewHolder> {

    private final List<File> _dataSource = new ArrayList<>();
    private final List<String> _dataSourceString = new ArrayList<>();
    private final DeleteSingleProductImage _listener;
    private Context context;

    public ProductImagesAdapter(Context context, List<File> _dataSource, DeleteSingleProductImage _listener) {
        _dataSource = _dataSource;
        this._listener = _listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_product_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);


        /*Glide.with(context).load(holder.item).centerCrop().placeholder(R.drawable.img_service_list).into(holder.imgProduct);*/

        File imgFile = holder.item;
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        if (myBitmap != null) {
            holder.imgProduct.setImageBitmap(myBitmap);
        } else {
            if (_dataSourceString.get(position) != null) {
                Glide.with(context).load(_dataSourceString.get(position)).placeholder(R.drawable.img_service_list).into(holder.imgProduct);
            }

        }


        holder.rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.deleteImage(holder.getAdapterPosition(), holder.item);
            }
        });


    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<File> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
        notifyDataSetChanged();
    }

    public void refresh(List<File> list, List<String> urlString) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
        _dataSourceString.clear();
        _dataSourceString.addAll(urlString);
        notifyDataSetChanged();
    }

    private Bitmap getRotateImaged(String photoPath, Bitmap bitmap) throws IOException {

        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
        );

        Bitmap rotatedImage = null;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_NORMAL) {
            rotatedImage = bitmap;
        }
        return rotatedImage;

    }

    private Bitmap rotateImage(Bitmap source, Float angle) throws IOException {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public File item;
        public String itemString;
        private final ImageView imgProduct;
        private final RelativeLayout rlDelete;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imgProduct = view.findViewById(R.id.imgProduct);
            rlDelete = view.findViewById(R.id.rlDelete);
        }
    }

}
