package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.CategoryBillBoardActivity;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceCategories;

import java.util.ArrayList;
import java.util.List;

public class CategoryBillBoardAdapter extends RecyclerView.Adapter<CategoryBillBoardAdapter.ViewHolder> {

    private Context _context;
    private List<MarketPlaceCategories> _dataSource;

    public CategoryBillBoardAdapter(Context context,
                                    List<MarketPlaceCategories> transactionResponseList) {
        _context = context;
        _dataSource = transactionResponseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_bill_board, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        holder.tvCategoryName.setText(holder.item.name);

        holder.tvCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CategoryBillBoardActivity)_context).onClick(holder.item);
            }
        });
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<MarketPlaceCategories> filterllist) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvCategoryName;
        private MarketPlaceCategories item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvCategoryName = view.findViewById(R.id.tvCategoryName);
        }
    }
}
