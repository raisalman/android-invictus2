package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.models.GuestEntryDoor;

import java.util.ArrayList;
import java.util.List;

public class GuestEntryDoorsAdapter extends RecyclerView.Adapter<GuestEntryDoorsAdapter.ViewHolder> {

    private Context _context;
    private List<GuestEntryDoor> _dataSource;

    public GuestEntryDoorsAdapter(Context context,
                                  List<GuestEntryDoor> guestEntryDoors) {
        _context = context;
        _dataSource = guestEntryDoors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guest_entry_door, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        holder.chkDoor.setText(holder.item.getName());

        holder.chkDoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.item.setSelected(isChecked);
            }
        });
    }

    public ArrayList<GuestEntryDoor> getSelectedEntry() {
        ArrayList<GuestEntryDoor> entryDoors = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            if (_dataSource.get(i).isSelected()) {
                entryDoors.add(_dataSource.get(i));
            }
        }
        return entryDoors;
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private CheckBox chkDoor;
        private GuestEntryDoor item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            chkDoor = view.findViewById(R.id.chkDoor);
        }
    }
}
