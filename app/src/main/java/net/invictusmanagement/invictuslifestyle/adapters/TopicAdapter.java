package net.invictusmanagement.invictuslifestyle.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.enum_utils.ChatRequestStatus;
import net.invictusmanagement.invictuslifestyle.interfaces.TopicListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Topic;
import net.invictusmanagement.invictuslifestyle.utils.TopicTimeConversion;

import java.util.ArrayList;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    private final List<Topic> _dataSource = new ArrayList<>();
    private final TopicListFragmentInteractionListener _listener;
    private final Context context;
    private final TopicTimeConversion timeAgo2 = new TopicTimeConversion();
    boolean isRWTTopic;
    private RelativeLayout rlCreate;

    public TopicAdapter(Context context, TopicListFragmentInteractionListener listener,
                        boolean isRWTTopic, RelativeLayout rlCreate) {
        _listener = listener;
        this.context = context;
        this.isRWTTopic = isRWTTopic;
        this.rlCreate = rlCreate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_topic, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        if (isRWTTopic) {
            if (position == 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRWTTopic)
                            walkThroughHighlight(holder.viewMain, rlCreate);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                        sharedPreferences.edit().putBoolean("isRWTTopic", false).apply();
                        isRWTTopic = sharedPreferences.getBoolean("isRWTTopic", true);
                    }
                }, 500);
            }
        }

        if (holder.item.status.equals(ChatRequestStatus.Pending.value()) || holder.item.status.equals(ChatRequestStatus.Close.value())) {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(R.color.bg_un_read));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.getContext(), R.color.black_757575));
            holder.tvDescription.setTextColor(ContextCompat.getColor(holder.tvDescription.getContext(), R.color.black_757575));
        } else if (holder.item.status.equals(ChatRequestStatus.Open.value())) {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(android.R.color.white));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.getContext(), android.R.color.black));
            holder.tvDescription.setTextColor(ContextCompat.getColor(holder.tvDescription.getContext(), android.R.color.black));
        }

        holder.tvTitle.setText(holder.item.topic);

        if (holder.item.description.equals("")) {
            holder.tvDescription.setVisibility(View.INVISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
        }

        if (holder.item.residentUnreadCount > 0) {
            holder.textCount.setVisibility(View.VISIBLE);
            holder.textCount.setText(String.valueOf(holder.item.residentUnreadCount));
        } else {
            holder.textCount.setVisibility(View.INVISIBLE);
        }

        holder.tvDescription.setText(holder.item.description);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onListFragmentInteraction(holder.item);
            }
        });


        holder.tvLastMessageTime.setText(timeAgo2.covertTimeToText(holder.item.updatedUtc));

    }


    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<Topic> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvTitle;
        public final TextView textCount;
        public final TextView tvDescription;
        public final TextView tvLastMessageTime;
        public final LinearLayout llmain;
        public Topic item;
        public final GridLayout viewMain;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            llmain = view.findViewById(R.id.llmain);
            viewMain = view.findViewById(R.id.viewMain);
            tvTitle = view.findViewById(R.id.tvTitle);
            textCount = view.findViewById(R.id.textCount);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvLastMessageTime = view.findViewById(R.id.tvLastMessageTime);
        }
    }


    private void walkThroughHighlight(View view1, RelativeLayout rlCreate) {
        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_generalchat_info)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(rlCreate)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_generalchat)
                        .setTipViewRelativeDirection(Direction.TOP)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 0, 30))
                        .build())
                .show();


    }

}
