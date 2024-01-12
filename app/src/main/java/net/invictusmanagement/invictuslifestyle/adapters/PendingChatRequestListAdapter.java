package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.PendingChatRequestListActivity;
import net.invictusmanagement.invictuslifestyle.models.ChatTopic;
import net.invictusmanagement.invictuslifestyle.utils.TopicTimeConversion;

import java.util.ArrayList;

public class PendingChatRequestListAdapter extends RecyclerView.Adapter<PendingChatRequestListAdapter.ViewHolder> {

    private Context _context;
    private ArrayList<ChatTopic> _dataSource;
    private final TopicTimeConversion timeAgo2 = new TopicTimeConversion();

    public PendingChatRequestListAdapter(Context context,
                                         ArrayList<ChatTopic> transactionResponseList) {
        _context = context;
        _dataSource = transactionResponseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        holder.tvTopicName.setText(holder.item.topic);
        holder.tvDescription.setText(holder.item.description);
        holder.tvDate.setText(timeAgo2.covertTimeToText(holder.item.updatedUtc));

        holder.tvAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PendingChatRequestListActivity) _context).onClick(holder.item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvTopicName;
        private TextView tvDescription;
        private TextView tvAcceptRequest;
        private TextView tvDate;
        private ChatTopic item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvTopicName = view.findViewById(R.id.tvTopicName);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvDate = view.findViewById(R.id.tvDate);
            tvAcceptRequest = view.findViewById(R.id.tvAcceptRequest);
        }
    }
}
