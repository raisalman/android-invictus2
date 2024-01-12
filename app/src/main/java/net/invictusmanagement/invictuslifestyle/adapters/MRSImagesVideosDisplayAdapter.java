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
import net.invictusmanagement.invictuslifestyle.interfaces.ShowSingleMRSItem;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;

import java.util.ArrayList;
import java.util.List;

public class MRSImagesVideosDisplayAdapter extends RecyclerView.Adapter<MRSImagesVideosDisplayAdapter.ViewHolder> {

    private final List<MaintenanceRequesFiles> _dataSource = new ArrayList<>();
    private final List<String> _dataSourceString = new ArrayList<>();
    private final ShowSingleMRSItem _listener;
    private Context context;

    public MRSImagesVideosDisplayAdapter(Context context, List<MaintenanceRequesFiles> _dataSource, ShowSingleMRSItem _listener) {
        _dataSource = _dataSource;
        this._listener = _listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_prior_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        if (holder.item.isImage) {
            holder.imgPlayIcon.setVisibility(View.GONE);
            Glide.with(context).load(holder.item.maintenanceRequestImageSrc).apply(options).into(holder.imgProduct);
        } else {
            String str = holder.item.maintenanceRequestImageSrc;
            int index = str.lastIndexOf('/');
            String videoThumb = str.substring(0, index + 1) + holder.item.id + "_thumb.jpg";

            holder.imgPlayIcon.setVisibility(View.VISIBLE);
            Glide.with(context).load(videoThumb).apply(options).into(holder.imgProduct);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.showVideoImage(holder.getAdapterPosition(), holder.item);
            }
        });


    }

    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<MaintenanceRequesFiles> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public MaintenanceRequesFiles item;
        public String itemString;
        private final ImageView imgProduct;
        private final ImageView imgPlayIcon;
        private final RelativeLayout rlDelete;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imgProduct = view.findViewById(R.id.imgProduct);
            imgPlayIcon = view.findViewById(R.id.imgPlayIcon);
            rlDelete = view.findViewById(R.id.rlDelete);
        }
    }

}
