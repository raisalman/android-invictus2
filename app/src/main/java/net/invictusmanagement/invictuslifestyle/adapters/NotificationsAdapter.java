package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.customviews.RoundedLetterView;
import net.invictusmanagement.invictuslifestyle.interfaces.NotificationsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.MarkNotificationRead;
import net.invictusmanagement.invictuslifestyle.models.Notification;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final List<Notification> _dataSource = new ArrayList<>();
    private final NotificationsListFragmentInteractionListener _listener;
    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();
    private Context context;
    private boolean isRWTNotifictions;

    public NotificationsAdapter(NotificationsListFragmentInteractionListener listener, Context context, boolean isRWTNotifictions) {
        _listener = listener;
        this.context = context;
        this.isRWTNotifictions = isRWTNotifictions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notification, parent, false);
        return new ViewHolder(view);
    }

    private void walkThroughHighlight(View view1) {
        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_notification_info)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .show();


    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        if (isRWTNotifictions) {
            if (position == 0 && TabbedActivity.tabbedActivity._currentTabPosition
                    == ((TabbedActivity) context)._sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        walkThroughHighlight(holder.viewMain);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                        if (TabbedActivity.isGuestUser) {
                            sharedPreferences.edit().putBoolean("isGWTNotifictions", false).apply();
                            isRWTNotifictions = sharedPreferences.getBoolean("isGWTNotifictions", true);
                        } else {
                            sharedPreferences.edit().putBoolean("isRWTNotifictions", false).apply();
                            isRWTNotifictions = sharedPreferences.getBoolean("isRWTNotifictions", true);
                        }

                    }
                }, 500);
            }
        }

        if (holder.item.isRead) {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(R.color.bg_read));
            holder.createdUtcTextView.setTextColor(ContextCompat.getColor(holder.createdUtcTextView.getContext(), R.color.black_757575));
            holder.titleTextView.setTextColor(ContextCompat.getColor(holder.titleTextView.getContext(), R.color.black_757575));
        } else {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(R.color.bg_un_read));
            holder.createdUtcTextView.setTextColor(ContextCompat.getColor(holder.createdUtcTextView.getContext(), android.R.color.black));
            holder.titleTextView.setTextColor(ContextCompat.getColor(holder.titleTextView.getContext(), android.R.color.black));
        }

        holder.iconRoundedLetterView.setTitleText(holder.item.title.substring(0, 1).toUpperCase());
        holder.titleTextView.setText(holder.item.title);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        holder.createdUtcTextView.setText(formatter.format(holder.item.createdUtc));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markNotificationAsRead(holder.item);
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, false);
                }
            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, true);
                }
            }
        });

        if (holder.item.imageUrl == null) {
            holder.llProfile.setVisibility(View.GONE);
        } else {
            holder.llProfile.setVisibility(View.VISIBLE);
            Glide.with(context).load(holder.item.imageUrl).apply(options).into(holder.imgProfile);
        }
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }


    public void refresh(List<Notification> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
    }

    public void markNotificationAsRead(final Notification item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().readNotification(new MarkNotificationRead(item.notificationId));
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {

            }
        }.execute();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final RoundedLetterView iconRoundedLetterView;
        public final TextView titleTextView;
        public final TextView createdUtcTextView;
        public final GridLayout viewMain;
        public LinearLayout llProfile;
        public ImageView imgProfile;
        public Notification item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            iconRoundedLetterView = (RoundedLetterView) view.findViewById(R.id.icon);
            titleTextView = (TextView) view.findViewById(R.id.title);
            viewMain = (GridLayout) view.findViewById(R.id.viewMain);
            createdUtcTextView = (TextView) view.findViewById(R.id.createdUtc);
            llProfile = (LinearLayout) view.findViewById(R.id.llProfile);
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText() + "'";
        }
    }
}
