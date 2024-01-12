package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.MaintenanceRequestActivity;
import net.invictusmanagement.invictuslifestyle.activities.NewMaintenanceRequestActivity;
import net.invictusmanagement.invictuslifestyle.interfaces.MaintenanceRequestsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequest;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequestResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceRequestsAdapter extends RecyclerView.Adapter<MaintenanceRequestsAdapter.ViewHolder> {

    private String role;
    private Context _context;
    private List<MaintenanceRequestResponse> _dataSource = new ArrayList<>();
    private MaintenanceRequestsListFragmentInteractionListener _listener;
    private SharedPreferences sharedPreferences;

    public MaintenanceRequestsAdapter(Context context, MaintenanceRequestsListFragmentInteractionListener listener) {
        _context = context;
        _listener = listener;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        role = sharedPreferences.getString("userRole", "");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_maintenance_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        if (role.equals(_context.getString(R.string.role_property_manager))
                || role.equals(_context.getString(R.string.role_leasing_officer))
                || role.equals(_context.getString(R.string.role_vendor))
                || role.equals(_context.getString(R.string.role_facility))) {
            holder.moreButton.setVisibility(View.VISIBLE);
        } else {
            holder.moreButton.setVisibility(View.GONE);
        }

        holder.titleTextView.setText(holder.item.getTitle());
        holder.statusTextView.setText(holder.item.getStatus().toString());
        GradientDrawable drawable = (GradientDrawable) holder.iconBackgroundImageView.getDrawable();
        switch (holder.item.getStatus()) {
            case Active:
                holder.statusTextView.setText("Active");
                holder.iconImageView.setImageResource(R.drawable.ic_check_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.maintReqActive));
                break;
            case Requested:
                holder.statusTextView.setText("Requested");
                holder.iconImageView.setImageResource(R.drawable.ic_hourglass_empty_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.maintReqRequested));
                break;
            case Closed:
                holder.statusTextView.setText("Closed");
                holder.iconImageView.setImageResource(R.drawable.ic_block_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.maintReqClosed));
                break;
            case RequestedToClose:
                holder.statusTextView.setText("Requested To Close");
                holder.iconImageView.setImageResource(R.drawable.ic_block_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.maintReqClosed));
                break;
        }
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        holder.createdUtcTextView.setText(formatter.format(holder.item.createdUtc));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item);
                }
            }
        });

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(_context, holder.moreButton);
                if (role.equals(_context.getString(R.string.role_vendor)))
                    popup.inflate(R.menu.menu_service_key_edit);
                else
                    popup.inflate(R.menu.menu_maintenance_key);

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                AlertDialog.Builder alertDialog =
                                        new AlertDialog.Builder(_context);
                                alertDialog.setTitle("Delete Maintenance Request");
                                alertDialog.setMessage("Are you sure you want to delete maintenance request?");
                                alertDialog.setNegativeButton("Cancel", (dialog, which) -> {

                                });

                                alertDialog.setPositiveButton("Ok", (dialog, which) -> {
                                    ((MaintenanceRequestActivity) _context)
                                            .callAPIForDelete(holder.item.getId());
                                });
                                alertDialog.show();
                                return true;
                            case R.id.action_edit:
                                //handle menu2 click

                                if (role.equals(_context.getString(R.string.role_vendor))
                                        && (holder.item.getStatus().equals(MaintenanceRequest.Status.Closed) ||
                                        holder.item.getStatus().equals(MaintenanceRequest.Status.RequestedToClose))) {
                                    showErrorMessage();
                                } else {

                                    Intent intent = new Intent(_context,
                                            NewMaintenanceRequestActivity.class);
                                    intent.putExtra("MAINTENANCE_REQUEST", holder.item);
                                    _context.startActivity(intent);
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    private void showErrorMessage() {
        new AlertDialog.Builder(_context)
                .setTitle("Edit Request")
                .setMessage("Sorry, You can not edit Maintenance Request when status is Closed/Requested To Close")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.dismiss();
                    }
                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<MaintenanceRequestResponse> list) {
        if (list == null) return;
        _dataSource = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView iconBackgroundImageView;
        public final ImageView iconImageView;
        public final TextView titleTextView;
        public final TextView statusTextView;
        public final TextView createdUtcTextView;
        public MaintenanceRequestResponse item;
        public final LinearLayout moreButton;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            iconBackgroundImageView = view.findViewById(R.id.icon_background);
            iconImageView = view.findViewById(R.id.icon);
            titleTextView = view.findViewById(R.id.title);
            statusTextView = view.findViewById(R.id.status);
            createdUtcTextView = view.findViewById(R.id.createdUtc);
            moreButton = view.findViewById(R.id.moreButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText() + "'";
        }
    }
}
