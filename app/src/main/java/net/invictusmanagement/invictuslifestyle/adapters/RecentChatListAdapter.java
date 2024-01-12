package net.invictusmanagement.invictuslifestyle.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.enum_utils.ChatRequestStatus;
import net.invictusmanagement.invictuslifestyle.interfaces.RecentChatListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.RecentChat;
import net.invictusmanagement.invictuslifestyle.utils.TopicTimeConversion;

import java.util.ArrayList;
import java.util.List;

public class RecentChatListAdapter extends RecyclerView.Adapter<RecentChatListAdapter.ViewHolder> {

    private final List<RecentChat> _dataSource = new ArrayList<>();
    private final Context context;
    private final TopicTimeConversion timeAgo2 = new TopicTimeConversion();
    private int adapterPosition = 0;
    private final RecentChatListFragmentInteractionListener _listener;

    public RecentChatListAdapter(Context context, RecentChatListFragmentInteractionListener listener) {
        this.context = context;
        _listener = listener;
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

        if (holder.item.appStatus.equals(ChatRequestStatus.Pending.value()) || holder.item.appStatus.equals(ChatRequestStatus.Close.value())) {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(R.color.bg_un_read));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.getContext(), R.color.black_757575));
            holder.tvDescription.setTextColor(ContextCompat.getColor(holder.tvDescription.getContext(), R.color.black_757575));
        } else if (holder.item.appStatus.equals(ChatRequestStatus.Open.value())) {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(android.R.color.white));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.getContext(), android.R.color.black));
            holder.tvDescription.setTextColor(ContextCompat.getColor(holder.tvDescription.getContext(), android.R.color.black));
        }

        holder.tvTitle.setText(holder.item.displayName);

        if (holder.item.adminUnreadCount > 0) {
            holder.textCount.setVisibility(View.VISIBLE);
            holder.textCount.setText(String.valueOf(holder.item.adminUnreadCount));
        } else {
            holder.textCount.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onListFragmentInteraction(holder.item);
            }
        });


        holder.tvLastMessageTime.setText(timeAgo2.covertTimeToText(holder.item.appStatusUpdatedDate));

    }


    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<RecentChat> list) {
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
        public RecentChat item;
        private GridLayout viewMain;

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
}
