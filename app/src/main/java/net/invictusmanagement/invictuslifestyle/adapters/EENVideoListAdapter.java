package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.EENVideoListActivity;
import net.invictusmanagement.invictuslifestyle.models.EENDeviceList;

import java.util.ArrayList;

public class EENVideoListAdapter extends RecyclerView.Adapter<EENVideoListAdapter.ViewHolder> {

    private Context _context;
    private ArrayList<EENDeviceList> _dataSource;

    public EENVideoListAdapter(Context context,
                               ArrayList<EENDeviceList> transactionResponseList) {
        _context = context;
        _dataSource = transactionResponseList;
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
        holder.tvCameraName.setText(holder.item.deviceName);

        holder.tvCameraName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EENVideoListActivity) _context).onClick(holder.item);
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
        private EENDeviceList item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvCameraName = view.findViewById(R.id.tvCameraName);
        }
    }
}
