package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.CategoryBillBoardActivity;
import net.invictusmanagement.invictuslifestyle.activities.UserSelectionActivity;
import net.invictusmanagement.invictuslifestyle.models.CommunityNotificationList;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceCategories;
import net.invictusmanagement.invictuslifestyle.models.ResidentChat;

import java.util.ArrayList;
import java.util.List;

public class UserSelectionAdapter extends RecyclerView.Adapter<UserSelectionAdapter.ViewHolder> {

    private Context _context;
    private List<ResidentChat> _dataSource;

    public UserSelectionAdapter(Context context,
                                List<ResidentChat> transactionResponseList) {
        _context = context;
        _dataSource = transactionResponseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override

    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        holder.tvCategoryName.setText(holder.item.displayName);
        holder.chkSelected.setChecked(holder.item.isSelected);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.item.isSelected = !holder.item.isSelected;
                holder.chkSelected.setChecked(holder.item.isSelected);
            }
        });
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<ResidentChat> filterllist) {
        // below line is to add our filtered
        // list in our course array list.
        this._dataSource = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public List<ResidentChat> getList() {
        return this._dataSource;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvCategoryName;
        private ResidentChat item;
        private CheckBox chkSelected;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            chkSelected = view.findViewById(R.id.chkUser);
            tvCategoryName = view.findViewById(R.id.tvCategoryName);
        }
    }
}
