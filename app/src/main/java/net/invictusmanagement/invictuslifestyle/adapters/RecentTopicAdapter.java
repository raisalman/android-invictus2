package net.invictusmanagement.invictuslifestyle.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.GeneralChatActivity;
import net.invictusmanagement.invictuslifestyle.activities.NewChatTopicActivity;
import net.invictusmanagement.invictuslifestyle.activities.RecentTopicListActivity;
import net.invictusmanagement.invictuslifestyle.enum_utils.ChatRequestStatus;
import net.invictusmanagement.invictuslifestyle.models.ChatTopic;
import net.invictusmanagement.invictuslifestyle.models.Topic;

import java.util.ArrayList;
import java.util.List;

public class RecentTopicAdapter extends RecyclerView.Adapter<RecentTopicAdapter.ViewHolder> {

    private final List<ChatTopic> _dataSource = new ArrayList<>();
    private final Context context;

    public RecentTopicAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_resident_topic, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        if (holder.item.status.equals(ChatRequestStatus.Pending.value()) || holder.item.status.equals(ChatRequestStatus.Close.value())) {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(R.color.bg_un_read));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.getContext(), R.color.black_757575));
        } else if (holder.item.status.equals(ChatRequestStatus.Open.value())) {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(android.R.color.white));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.getContext(), android.R.color.black));
        }

        holder.txtTopicStatus.setPaintFlags(holder.txtTopicStatus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (holder.item.status.equals(ChatRequestStatus.Close.value())) {
            holder.txtTopicStatus.setText("Reopen");
        } else {
            holder.txtTopicStatus.setText("Close");
        }

        holder.tvTitle.setText(holder.item.topic);

        if (holder.item.adminUnreadCount > 0) {
            holder.textCount.setVisibility(View.VISIBLE);
            holder.textCount.setText(String.valueOf(holder.item.adminUnreadCount));
        } else {
            holder.textCount.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Topic topic = convertTopic(holder.item);
                Intent intent = new Intent(context, GeneralChatActivity.class);
                intent.putExtra(GeneralChatActivity.TOPIC_JSON, new Gson().toJson(topic));
                context.startActivity(intent);
            }
        });

        holder.txtTopicStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof RecentTopicListActivity) {
                    ((RecentTopicListActivity) context).callAPIForTopicStatusChange(holder.item);
                } else if (context instanceof NewChatTopicActivity) {
                    ((NewChatTopicActivity) context).callAPIForTopicStatusChange(holder.item);
                }
            }
        });
    }

    private Topic convertTopic(ChatTopic chatTopic) {
        Topic topic = new Topic();
        topic.id = chatTopic.id;
        topic.locationId = chatTopic.locationId;
        topic.topic = chatTopic.topic;
        topic.description = chatTopic.description;
        topic.status = chatTopic.status;
        topic.appStatus = chatTopic.appStatus;
        topic.applicationUserId = chatTopic.applicationUserId;
        topic.adminUserId = chatTopic.adminUserId;
        topic.residentUnreadCount = chatTopic.residentUnreadCount;
        topic.adminUnreadCount = chatTopic.adminUnreadCount;
        topic.updatedUtc = chatTopic.updatedUtc;
        return topic;
    }


    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<ChatTopic> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvTitle;
        public final TextView textCount;
        public final LinearLayout llmain;
        public ChatTopic item;
        private GridLayout viewMain;
        private TextView txtTopicStatus;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            llmain = view.findViewById(R.id.llmain);
            viewMain = view.findViewById(R.id.viewMain);
            tvTitle = view.findViewById(R.id.tvTitle);
            textCount = view.findViewById(R.id.textCount);
            txtTopicStatus = view.findViewById(R.id.txtTopicStatus);
        }
    }
}
