package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.CategoryBillBoardActivity;
import net.invictusmanagement.invictuslifestyle.activities.VendorSelectionActivity;
import net.invictusmanagement.invictuslifestyle.models.ResidentChat;
import net.invictusmanagement.invictuslifestyle.models.Vendors;

import java.util.ArrayList;
import java.util.List;

public class VendorSelectionAdapter extends RecyclerView.Adapter<VendorSelectionAdapter.ViewHolder> {

    private Context _context;
    private List<Vendors> _dataSource;

    public VendorSelectionAdapter(Context context,
                                  List<Vendors> transactionResponseList) {
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
        holder.tvCategoryName.setText(holder.item.name);
        holder.chkSelected.setVisibility(View.GONE);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((VendorSelectionActivity) _context).onClick(holder.item);
            }
        });

        //MUlti Selection
//        holder.chkSelected.setVisibility(View.GONE);
//        holder.chkSelected.setChecked(holder.item.isSelected);
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.item.isSelected = !holder.item.isSelected;
//                holder.chkSelected.setChecked(holder.item.isSelected);
//            }
//        });
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<Vendors> filterllist) {
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

    public List<Vendors> getList() {
        return this._dataSource;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvCategoryName;
        private Vendors item;
        private CheckBox chkSelected;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            chkSelected = view.findViewById(R.id.chkUser);
            tvCategoryName = view.findViewById(R.id.tvCategoryName);
        }
    }
}
