package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.SingleVideoImageActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.interfaces.InviteKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.GuestDigitalKey;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InviteKeysAdapter extends RecyclerView.Adapter<InviteKeysAdapter.ViewHolder> {

    private final List<GuestDigitalKey> _dataSource = new ArrayList<>();
    private final List<GuestDigitalKey> _dataSourceTotal = new ArrayList<>();
    private final InviteKeysListFragmentInteractionListener _listener;
    private Context _context;

    public InviteKeysAdapter(Context context, InviteKeysListFragmentInteractionListener listener) {
        _context = context;
        _listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_digitalkey, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        try {
            Long l = Long.parseLong(holder.item.recipient);
            holder.recipientTextView.setText(Utilities.formatPhone(holder.item.recipient));
        } catch (NumberFormatException ex) {
            holder.recipientTextView.setText(holder.item.recipient);
        }

        Date now = new Date();
        GradientDrawable drawable = (GradientDrawable) holder.iconBackgroundImageView.getDrawable();
        if (holder.item.isRevoked) {
            holder.statusTextView.setText("Revoked");
            holder.iconImageView.setImageResource(R.drawable.ic_block_white_24dp);
            drawable.setColor(ContextCompat.getColor(_context, R.color.digitalKeyExpired));
        } else {
            if (now.after(holder.item.fromUtc) && now.before(holder.item.toUtc)) {
                holder.statusTextView.setText("Valid");
                holder.iconImageView.setImageResource(R.drawable.ic_check_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.digitalKeyValid));
            } else if (now.before(holder.item.fromUtc) && now.before(holder.item.toUtc)) {
                holder.statusTextView.setText("Upcoming");
                holder.iconImageView.setImageResource(R.drawable.ic_hourglass_empty_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.digitalKeyUpcoming));
            } else {
                holder.statusTextView.setText("Expired");
                holder.iconImageView.setImageResource(R.drawable.ic_block_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.digitalKeyExpired));
            }
        }   // revoked?

        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        holder.fromTextView.setText(formatter.format(holder.item.fromUtc));
        holder.toTextView.setText(formatter.format(holder.item.toUtc));

        if (holder.item.qrCodeSrc != null) {
            holder.ivQRCode.setVisibility(View.VISIBLE);
        } else {
            holder.ivQRCode.setVisibility(View.GONE);
        }

        holder.ivQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
                maintenanceRequesFiles.isImage = true;
                maintenanceRequesFiles.isFromAd = false;
                maintenanceRequesFiles.maintenanceRequestImageSrc = holder.item.qrCodeSrc;
                Intent i = new Intent(TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
                i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
                _context.startActivity(i);
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }


    public int totalItemCount() {
        return _dataSourceTotal.size();
    }

    public void refresh(List<GuestDigitalKey> list, Boolean isActive) {
        if (list == null) return;
        Date now = new Date();
        _dataSource.clear();
        _dataSourceTotal.clear();
        _dataSourceTotal.addAll(list);
        if (isActive) {
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).isRevoked) {
                    if (now.after(list.get(i).fromUtc) && now.before(list.get(i).toUtc)) {
                        _dataSource.add(list.get(i));
                    }
                }
            }
        } else {
            _dataSource.addAll(list);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView iconBackgroundImageView;
        public final ImageView iconImageView;
        public final TextView recipientTextView;
        public final TextView statusTextView;
        public final TextView fromTextView;
        public final TextView toTextView;
        public final ImageView ivQRCode;
        public GuestDigitalKey item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            iconBackgroundImageView = (ImageView) view.findViewById(R.id.icon_background);
            iconImageView = (ImageView) view.findViewById(R.id.icon);
            recipientTextView = (TextView) view.findViewById(R.id.recipient);
            statusTextView = (TextView) view.findViewById(R.id.status);
            fromTextView = (TextView) view.findViewById(R.id.from);
            toTextView = (TextView) view.findViewById(R.id.to);
            ivQRCode = (ImageView) view.findViewById(R.id.ivQrCode);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + recipientTextView.getText() + "'";
        }
    }
}
