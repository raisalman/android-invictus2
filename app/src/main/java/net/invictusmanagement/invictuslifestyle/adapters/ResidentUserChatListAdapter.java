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
import net.invictusmanagement.invictuslifestyle.interfaces.ResidentChatListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.ResidentChat;

import java.util.ArrayList;
import java.util.List;

public class ResidentUserChatListAdapter extends RecyclerView.Adapter<ResidentUserChatListAdapter.ViewHolder> {

    private final List<ResidentChat> _dataSource = new ArrayList<>();
    private final Context context;
    private final ResidentChatListFragmentInteractionListener _listener;

    public ResidentUserChatListAdapter(Context context, ResidentChatListFragmentInteractionListener listener) {
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

        holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(android.R.color.white));
        holder.tvTitle.setTextColor(ContextCompat.getColor(holder.tvTitle.getContext(), android.R.color.black));
        holder.tvDescription.setTextColor(ContextCompat.getColor(holder.tvDescription.getContext(), android.R.color.black));

        holder.tvTitle.setText(holder.item.displayName);
        holder.textCount.setVisibility(View.GONE);
        holder.tvDescription.setVisibility(View.GONE);
        holder.tvLastMessageTime.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(v -> _listener.onListFragmentInteraction(holder.item));

    }


    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<ResidentChat> list) {
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
        public ResidentChat item;
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
