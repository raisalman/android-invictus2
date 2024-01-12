package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.SlideView;
import net.invictusmanagement.invictuslifestyle.interfaces.AccessPointsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.OnFinishListener;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;

import java.util.List;

public class AccessPointsAdapter extends RecyclerView.Adapter<AccessPointsAdapter.ViewHolder> {

    private Context _context;
    private List<AccessPoint> _dataSource;
    private AccessPointsListFragmentInteractionListener listener;

    public AccessPointsAdapter(Context context,
                               List<AccessPoint> accessPointList,
                               AccessPointsListFragmentInteractionListener listener) {
        _context = context;
        _dataSource = accessPointList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_access_point, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item = _dataSource.get(position);

        GradientDrawable drawable = (GradientDrawable) holder.iconBackgroundImageView.getDrawable();
        drawable.setColor(ContextCompat.getColor(_context, R.color.colorPrimary));
        holder.iconImageView.setImageResource(holder.item.getType() == AccessPoint.Type.Pedestrian ? R.drawable.ic_directions_walk_white_24dp : R.drawable.ic_directions_car_white_24dp);
        holder.nameTextView.setText(holder.item.getName());
        holder.typeTextView.setText(holder.item.getType().toString());

        if (holder.item.unlockingStatus == 0) {
            holder.slideView.setBackground(_context
                    .getDrawable(R.drawable.rounded_bottom_corner_gray));
        } else if (holder.item.unlockingStatus == 1) {
            holder.slideView.setBackground(_context
                    .getDrawable(R.drawable.rounded_bottom_corner_green));
            resetItem(holder.item, position);
        } else if (holder.item.unlockingStatus == 2) {
            holder.slideView.setBackground(_context
                    .getDrawable(R.drawable.rounded_bottom_corner_red));
            resetItem(holder.item, position);
        } else {
            holder.slideView.setBackground(_context
                    .getDrawable(R.drawable.rounded_bottom_corner_yellow));
        }


        holder.slideView.setOnFinishListener(new OnFinishListener() {
            @Override
            public void onFinish() {
                addHaptic(holder.slideView);
                holder.item.unlockingStatus = 3;
                holder.slideView.reset();
                notifyItemChanged(position);

                listener.onSlideUnlockTapped(position, holder.item);
            }
        });

    }

    private void addHaptic(SlideView slideView) {
        slideView.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
        );
    }

    private void resetItem(final AccessPoint item, final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                item.unlockingStatus = 0;
                notifyItemChanged(position);
            }
        }, 4000);
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void setItem(int position, int status) {
        _dataSource.get(position).unlockingStatus = status;
        notifyItemChanged(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView iconBackgroundImageView;
        public final ImageView iconImageView;
        public final TextView nameTextView;
        public final TextView typeTextView;
        public final LinearLayout glMain;
        public AccessPoint item;
        public final SlideView slideView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            iconBackgroundImageView = view.findViewById(R.id.icon_background);
            iconImageView = view.findViewById(R.id.icon);
            nameTextView = view.findViewById(R.id.name);
            typeTextView = view.findViewById(R.id.type);
            glMain = view.findViewById(R.id.glMain);
            slideView = view.findViewById(R.id.slide_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameTextView.getText() + "'";
        }
    }
}
