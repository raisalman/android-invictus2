package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.RoundedLetterView;
import net.invictusmanagement.invictuslifestyle.interfaces.BusinessesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Business;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class BusinessesAdapter extends RecyclerView.Adapter<BusinessesAdapter.ViewHolder> {

    private final List<Business> _dataSource = new ArrayList<>();
    private final List<Business> _dataSourceTotal = new ArrayList<>();
    private final BusinessesListFragmentInteractionListener _listener;
    private Context _context;

    public BusinessesAdapter(Context context) {
        _context = context;
        _listener = (BusinessesListFragmentInteractionListener) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        holder.iconRoundedLetterView.setTitleText(holder.item.getName().substring(0, 1).toUpperCase());
        holder.nameTextView.setText(holder.item.getName());
        holder.addressTextView.setText((holder.item.getAddress1() +
                (TextUtils.isEmpty(holder.item.getAddress2()) ? "" : " " + holder.item.getAddress2())));
        holder.cszTextView.setText(holder.item.getCity() + ", "
                + holder.item.getState() + ". " + holder.item.getZip());
        holder.phoneTextView.setText(Utilities.formatPhone(holder.item.getPhone()));

        holder.moreImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(_context, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = null;
                        switch (item.getItemId()) {
                            case R.id.action_call:
                                intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + holder.item.getPhone()));
                                _context.startActivity(intent);
                                return true;

                            case R.id.action_map:
                                Uri uri = Uri.parse("geo:" + holder.item.getLatitude()
                                        + "," + holder.item.getLongitude() + "?z=18&q="
                                        + Uri.encode(holder.item.getName()));
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setPackage("com.google.android.apps.maps");
                                _context.startActivity(intent);
                                return true;
                        }
                        return false;
                    }
                });

                menu.inflate(R.menu.menu_business);
                menu.show();
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }


    public int totalItemCount() {
        return _dataSourceTotal.size();
    }

    public void refresh(List<Business> list, Boolean isAnytime) {
        if (list == null) return;
        _dataSource.clear();
        _dataSourceTotal.clear();
        _dataSourceTotal.addAll(list);
        if (!list.isEmpty()) {
            if (isAnytime) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isHasAnyTimeCoupons()) {
                        _dataSource.add(list.get(i));
                    }
                }
            } else {
                _dataSource.addAll(list);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final RoundedLetterView iconRoundedLetterView;
        public final TextView nameTextView;
        public final TextView addressTextView;
        public final TextView cszTextView;
        public final TextView phoneTextView;
        public final ImageButton moreImageButton;
        public Business item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            iconRoundedLetterView = (RoundedLetterView) view.findViewById(R.id.icon);
            nameTextView = (TextView) view.findViewById(R.id.name);
            addressTextView = (TextView) view.findViewById(R.id.address);
            cszTextView = (TextView) view.findViewById(R.id.csz);
            phoneTextView = (TextView) view.findViewById(R.id.phone);
            moreImageButton = (ImageButton) view.findViewById(R.id.moreButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameTextView.getText() + "'";
        }
    }
}
