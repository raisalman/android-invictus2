package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.MaintenanceRequestActivity;
import net.invictusmanagement.invictuslifestyle.activities.NewCommunityNotificationActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.fragments.CommunityNotificationFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.CommunityNotificationFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.ServiceKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.CommunityNotificationList;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;
import okhttp3.ResponseBody;

public class CommunityNotificationAdapter extends RecyclerView.Adapter<CommunityNotificationAdapter.ViewHolder> {

    private final Context _context;
    private final List<CommunityNotificationList> _dataSource = new ArrayList<>();
    private final List<CommunityNotificationList> _dataSourceTotal = new ArrayList<>();
    private final CommunityNotificationFragmentInteractionListener _listener;
    private final CommunityNotificationFragment fragment;
    Boolean isRWTServiceKey;
    Boolean isActiveAll = true;
    private RelativeLayout rlCreateKey;

    public CommunityNotificationAdapter(Context context, CommunityNotificationFragmentInteractionListener listener,
                                        CommunityNotificationFragment serviceKeysFragment, Boolean isRWTServiceKey,
                                        RelativeLayout rlCreateKey) {
        _context = context;
        _listener = listener;
        this.fragment = serviceKeysFragment;
        this.isRWTServiceKey = isRWTServiceKey;
        this.rlCreateKey = rlCreateKey;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_notification, parent, false);
        return new ViewHolder(view);
    }

    private void walkThroughHighlight(View view1, View view2) {
        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_digital_key_info)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view2)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_digitalkey_add)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .show();

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
//        if (isActiveAll) {
//            if (isRWTServiceKey) {
//                if (position == 0) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (isRWTServiceKey)
//                                walkThroughHighlight(holder.llMain, rlCreateKey);
//                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
//                            sharedPreferences.edit().putBoolean("isRWTDigitalKey", false).apply();
//                            isRWTServiceKey = sharedPreferences.getBoolean("isRWTDigitalKey", true);
//                        }
//                    }, 500);
//                }
//            }
//        }


        holder.tvTitle.setText(holder.item.title);
        holder.tvMessage.setText(holder.item.message);
        int totalUsers = holder.item.applicationUserNotifications.size();
        if (totalUsers <= 1) {
            holder.tvRecipientType.setText("Individual");
        } else {
            holder.tvRecipientType.setText("Group");
        }

        int readCount = 0;
        for (int i = 0; i < totalUsers; i++) {
            if (holder.item.applicationUserNotifications.get(i).isRead) {
                readCount++;
            }
        }

        holder.tvRecipientRead.setText(readCount + "/" + totalUsers);

        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        holder.tvDate.setText(formatter.format(holder.item.createdUtc));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, fragment);
                }
            }
        });


        holder.llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(_context, holder.llMore);
                popupMenu.inflate(R.menu.menu_maintenance_key);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                AlertDialog.Builder alertDialog =
                                        new AlertDialog.Builder(_context);
                                alertDialog.setTitle("Delete Community Notification");
                                alertDialog.setMessage("Are you sure you want to delete community notification?");
                                alertDialog.setNegativeButton("Cancel", (dialog, which) -> {

                                });

                                alertDialog.setPositiveButton("Ok", (dialog, which) -> {
                                    callWebServiceToDelete(holder.item.id);
                                });
                                alertDialog.show();
                                return true;

                            case R.id.action_edit:
                                Intent intent = new Intent(_context, NewCommunityNotificationActivity.class);
                                intent.putExtra("DATA", holder.item);
                                _context.startActivity(intent);
                                return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    private void callWebServiceToDelete(long id) {
        WebService.getInstance().deleteCommunityNotification(id, new RestEmptyCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                if (response != null) {
                    Toast.makeText(_context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    if (_listener != null)
                        _listener.refreshList();
                }
            }

            @Override
            public void onFailure(WSException wse) {
                Toast.makeText(_context, "Item deletion failed", Toast.LENGTH_SHORT).show();
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

    public void refresh(List<CommunityNotificationList> list) {
        if (list == null) return;
        Date now = new Date();
        _dataSource.clear();
        _dataSourceTotal.clear();
        _dataSourceTotal.addAll(list);
        _dataSource.addAll(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        private TextView tvTitle;
        private TextView tvMessage;
        private TextView tvDate;
        private TextView tvRecipientType;
        private TextView tvRecipientRead;
        private final LinearLayout llMain;
        private final LinearLayout llMore;
        public CommunityNotificationList item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvTitle = view.findViewById(R.id.tvTitle);
            tvMessage = view.findViewById(R.id.tvMessage);
            tvDate = view.findViewById(R.id.tvCreated);
            tvRecipientRead = view.findViewById(R.id.tvRecipientRead);
            tvRecipientType = view.findViewById(R.id.tvRecipientType);
            llMain = view.findViewById(R.id.llMain);
            llMore = view.findViewById(R.id.moreButton);
        }
    }
}
