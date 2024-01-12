package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.BusinessesActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.customviews.RoundedLetterView;
import net.invictusmanagement.invictuslifestyle.models.BusinessType;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class BusinessTypesAdapter extends RecyclerView.Adapter<BusinessTypesAdapter.ViewHolder> {

    private List<BusinessType> _dataSource = new ArrayList<>();
    boolean isRWTCoupons;
    private Context _context;

    public BusinessTypesAdapter(Context context, boolean isRWTCoupons) {
        _context = context;
        this.isRWTCoupons = isRWTCoupons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_business_type, parent, false);
        return new ViewHolder(view);
    }


    private void walkThroughHighlight(View view1) {
        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_coupons_info)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .show();


    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        if (isRWTCoupons) {
            if (position == 0 && TabbedActivity.tabbedActivity._currentTabPosition
                    == ((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        walkThroughHighlight(holder.glMain);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                        if (TabbedActivity.isGuestUser) {
                            sharedPreferences.edit().putBoolean("isGWTCoupons", false).apply();
                            isRWTCoupons = sharedPreferences.getBoolean("isGWTCoupons", true);
                        } else {

                            sharedPreferences.edit().putBoolean("isRWTCoupons", false).apply();
                            isRWTCoupons = sharedPreferences.getBoolean("isRWTCoupons", true);
                        }
                    }
                }, 500);
            }
        }

        holder.iconRoundedLetterView.setTitleText(holder.item.getName().substring(0, 1).toUpperCase());
        holder.nameTextView.setText(holder.item.getName());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, BusinessesActivity.class);
                intent.putExtra(Utilities.EXTRA_BUSINESS_TYPE_JSON, new Gson().toJson(holder.item));
                _context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<BusinessType> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final RoundedLetterView iconRoundedLetterView;
        public final TextView nameTextView;
        public BusinessType item;
        private GridLayout glMain;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            glMain = (GridLayout) view.findViewById(R.id.glMain);
            iconRoundedLetterView = (RoundedLetterView) view.findViewById(R.id.icon);
            nameTextView = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameTextView.getText() + "'";
        }
    }
}
