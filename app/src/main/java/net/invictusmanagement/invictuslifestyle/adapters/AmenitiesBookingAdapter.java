package net.invictusmanagement.invictuslifestyle.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.AmenitiesBookingActivity;
import net.invictusmanagement.invictuslifestyle.interfaces.AmenitiesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.AmenitiesBooking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AmenitiesBookingAdapter extends RecyclerView.Adapter<AmenitiesBookingAdapter.ViewHolder> {

    private final List<AmenitiesBooking> _dataSource = new ArrayList<>();
    private final AmenitiesListFragmentInteractionListener _listener;
    private final Context context;
    boolean isRWTAmenities;
    private int adapterPosition = 0;

    public AmenitiesBookingAdapter(Context context, AmenitiesListFragmentInteractionListener listener, boolean isRWTAmenities) {
        _listener = listener;
        this.context = context;
        this.isRWTAmenities = isRWTAmenities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_aminities_booking, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);

        holder.tvTitle.setText(holder.item.amenities.displayName);
        holder.tvFromDate.setText("From: " + formatter.format(holder.item.bookFrom));
        holder.tvToDate.setText("To: " + formatter.format(holder.item.bookTo));
        holder.tvNumberPerson.setText("Number of Persons: " + holder.item.bookingPersonCount);
        holder.tvDescription.setText(holder.item.amenities.description);

        if (holder.item.isApproved) {
            holder.tvReqStatus.setText("Reject \nRequest");
        } else {
            holder.tvReqStatus.setText("Accept \nRequest");
        }


        holder.tvReqStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AmenitiesBookingActivity) context).callAPIApproveRejectReq(holder.item);
            }
        });

        GradientDrawable drawable = (GradientDrawable) holder.iconBackground.getDrawable();
        if (holder.item.isApproved) {
            holder.tvReqStatus.setText("Reject \nRequest");
            holder.tvReqStatus.setBackground(ContextCompat.getDrawable(context,
                    R.drawable.layout_bg_post_condition_red));
            holder.tvReqStatus.setTextColor(ContextCompat.getColor(context, R.color.red_E21533));
            holder.icon.setImageResource(R.drawable.ic_check_white_24dp);
            drawable.setColor(ContextCompat.getColor(context, R.color.maintReqActive));
        } else {
            holder.tvReqStatus.setText("Accept \nRequest");
            holder.tvReqStatus.setBackground(ContextCompat.getDrawable(context,
                    R.drawable.layout_bg_post_condition));
            holder.tvReqStatus.setTextColor(ContextCompat.getColor(context, R.color.blue_013B97));
            holder.icon.setImageResource(R.drawable.ic_block_white_24dp);
            drawable.setColor(ContextCompat.getColor(context, R.color.maintReqClosed));
        }
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<AmenitiesBooking> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvTitle;
        public final TextView tvDescription;
        public final TextView tvFromDate;
        public final TextView tvToDate;
        public final TextView tvReqStatus;
        public final TextView tvNumberPerson;
        public final ImageView icon;
        public final ImageView iconBackground;
        public AmenitiesBooking item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvFromDate = view.findViewById(R.id.tvFromDate);
            tvToDate = view.findViewById(R.id.tvToDate);
            tvReqStatus = view.findViewById(R.id.tvReqStatus);
            tvNumberPerson = view.findViewById(R.id.tvNumberPerson);

            icon = view.findViewById(R.id.icon);
            iconBackground = view.findViewById(R.id.icon_background);
        }
    }

}
