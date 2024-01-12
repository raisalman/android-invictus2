package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.EENVideoListActivity;
import net.invictusmanagement.invictuslifestyle.interfaces.AccessPointsLocationListener;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;

import java.util.ArrayList;
import java.util.List;

public class AccessPointLocationAdapter extends RecyclerView.Adapter<AccessPointLocationAdapter.ViewHolder> {

    private Context _context;
    private List<AccessPoint> _dataSource;
    private AccessPointsLocationListener listener;

    public AccessPointLocationAdapter(Context context,
                                      List<AccessPoint> transactionResponseList, AccessPointsLocationListener listener) {
        _context = context;
        _dataSource = transactionResponseList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_een_video_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        holder.tvCameraName.setText(holder.item.getLocationName());

        holder.tvCameraName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLocationSelected(holder.item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvCameraName;
        private AccessPoint item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvCameraName = view.findViewById(R.id.tvCameraName);
        }
    }
}
