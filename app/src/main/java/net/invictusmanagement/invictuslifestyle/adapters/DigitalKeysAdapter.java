package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.fragments.AllDigitalKeysFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.DigitalKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class DigitalKeysAdapter extends RecyclerView.Adapter<DigitalKeysAdapter.ViewHolder> {

    private final Context _context;
    private final List<DigitalKey> _dataSource = new ArrayList<>();
    private final List<DigitalKey> _dataSourceTotal = new ArrayList<>();
    private final DigitalKeysListFragmentInteractionListener _listener;
    private final AllDigitalKeysFragment digitalKeysFragment;
    Boolean isRWTDigitalKey;
    Boolean isActiveAll = true;
    FloatingActionButton fab;
    private RelativeLayout rlCreateKey;

    public DigitalKeysAdapter(Context context, DigitalKeysListFragmentInteractionListener listener,
                              AllDigitalKeysFragment digitalKeysFragment, Boolean isRWTDigitalKey,
                              FloatingActionButton fab, RelativeLayout rlCreateKey) {
        _context = context;
        _listener = listener;
        this.digitalKeysFragment = digitalKeysFragment;
        this.isRWTDigitalKey = isRWTDigitalKey;
        this.fab = fab;
        this.rlCreateKey = rlCreateKey;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_digitalkey, parent, false);
        return new ViewHolder(view);
    }

    private void walkThroughHighlight(View view1, View view2) {
        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_digital_key_info)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view2)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_digitalkey_add)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .show();

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        holder.llMore.setVisibility(View.GONE);

        if (isActiveAll) {
            if (isRWTDigitalKey) {
                if (position == 0 ) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isRWTDigitalKey)
                                walkThroughHighlight(holder.llMain, rlCreateKey);
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                            sharedPreferences.edit().putBoolean("isRWTDigitalKey", false).apply();
                            isRWTDigitalKey = sharedPreferences.getBoolean("isRWTDigitalKey", true);
                        }
                    }, 500);
                }
            }
        }


        try {
            Long l = Long.parseLong(holder.item.getRecipient());
            holder.recipientTextView.setText(Utilities.formatPhone(holder.item.getRecipient()));
        } catch (NumberFormatException ex) {
            holder.recipientTextView.setText(holder.item.getRecipient());
        }

        Date now = new Date();
        GradientDrawable drawable = (GradientDrawable) holder.iconBackgroundImageView.getDrawable();
        holder.tvRenew.setVisibility(View.GONE);
        if (holder.item.isRevoked()) {
            holder.statusTextView.setText("Revoked");
            holder.iconImageView.setImageResource(R.drawable.ic_block_white_24dp);
            drawable.setColor(ContextCompat.getColor(_context, R.color.digitalKeyExpired));
        } else {
            if (now.after(holder.item.getFromUtc()) && now.before(holder.item.getToUtc())) {
                holder.statusTextView.setText("Valid");
                holder.iconImageView.setImageResource(R.drawable.ic_check_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.digitalKeyValid));
            } else if (now.before(holder.item.getFromUtc()) && now.before(holder.item.getToUtc())) {
                holder.statusTextView.setText("Upcoming");
                holder.iconImageView.setImageResource(R.drawable.ic_hourglass_empty_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.digitalKeyUpcoming));
            } else {
                holder.statusTextView.setText("Expired");
                holder.iconImageView.setImageResource(R.drawable.ic_block_white_24dp);
                drawable.setColor(ContextCompat.getColor(_context, R.color.digitalKeyExpired));
                if (!holder.item.isQuickKey()) {
                    holder.tvRenew.setVisibility(View.VISIBLE);
                }
            }
        }   // revoked?

        if (!holder.item.isRevoked()
                && !holder.statusTextView.getText().toString().equals("Expired")) {
            holder.tvRevoke.setVisibility(View.VISIBLE);
        } else {
            holder.tvRevoke.setVisibility(View.GONE);
        }

        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        holder.fromTextView.setText(formatter.format(holder.item.getFromUtc()));
        holder.toTextView.setText(formatter.format(holder.item.getToUtc()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, digitalKeysFragment);
                }
            }
        });

        holder.tvRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.addHaptic(holder.tvRenew);
                _listener.onRenewClicked(holder.item, digitalKeysFragment);
            }
        });

        holder.tvRevoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.addHaptic(holder.tvRevoke);
                _listener.onRevokeClicked(holder.item, digitalKeysFragment);
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

    public void refresh(List<DigitalKey> list, Boolean isActive) {
        if (list == null) return;
        Date now = new Date();
        _dataSource.clear();
        _dataSourceTotal.clear();
        _dataSourceTotal.addAll(list);
        isActiveAll = isActive;
        if (isActive) {
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).isRevoked()) {
                    if (now.after(list.get(i).getFromUtc()) && now.before(list.get(i).getToUtc())) {
                        _dataSource.add(list.get(i));
                    }
                }
            }
        } else {
            _dataSource.addAll(list);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView iconBackgroundImageView;
        public final ImageView iconImageView;
        public final TextView recipientTextView;
        public final TextView statusTextView;
        public final TextView fromTextView;
        public final TextView toTextView;
        public final TextView tvRenew;
        public final TextView tvRevoke;
        private final LinearLayout llMain;
        private final LinearLayout llMore;
        public DigitalKey item;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            llMain = view.findViewById(R.id.llMain);
            iconBackgroundImageView = view.findViewById(R.id.icon_background);
            iconImageView = view.findViewById(R.id.icon);
            recipientTextView = view.findViewById(R.id.recipient);
            statusTextView = view.findViewById(R.id.status);
            fromTextView = view.findViewById(R.id.from);
            toTextView = view.findViewById(R.id.to);
            tvRenew = view.findViewById(R.id.tvRenew);
            tvRevoke = view.findViewById(R.id.tvRevoke);
            llMore = view.findViewById(R.id.moreButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + recipientTextView.getText() + "'";
        }
    }
}
