package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.DeleteSingleMRSItem;
import net.invictusmanagement.invictuslifestyle.models.MRSItems;

import java.util.ArrayList;
import java.util.List;

public class MRSImagesVideosAdapter extends RecyclerView.Adapter<MRSImagesVideosAdapter.ViewHolder> {

    private final List<MRSItems> _dataSource = new ArrayList<>();
    private final List<String> _dataSourceString = new ArrayList<>();
    private final DeleteSingleMRSItem _listener;
    private Context context;
    private boolean canDelete = true;

    public MRSImagesVideosAdapter(Context context, List<MRSItems> _dataSource,
                                  DeleteSingleMRSItem _listener) {
//        this._dataSource = _dataSource;
        this._listener = _listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_product_images, parent, false);
        return new ViewHolder(view);
    }

    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);


        /*Glide.with(context).load(holder.item).centerCrop().placeholder(R.drawable.img_service_list).into(holder.imgProduct);*/

        /*File imgFile = holder.item.file;
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());*/
        if (holder.item.bitMap != null) {
            holder.imgProduct.setImageBitmap(holder.item.bitMap);
        } else if (holder.item.imageUrl != null) {
            Glide.with(context).load(holder.item.imageUrl).apply(options).into(holder.imgProduct);
        }

        if (canDelete)
            holder.rlDelete.setVisibility(View.VISIBLE);
        else
            holder.rlDelete.setVisibility(View.GONE);

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

    public void refresh(List<MRSItems> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
        notifyDataSetChanged();
    }

    public void setDelete(boolean b) {
        this.canDelete = b;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public MRSItems item;
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
