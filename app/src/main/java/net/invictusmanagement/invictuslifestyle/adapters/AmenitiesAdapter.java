package net.invictusmanagement.invictuslifestyle.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.interfaces.AmenitiesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Amenities;

import java.util.ArrayList;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.ViewHolder> {

    private final List<Amenities> _dataSource = new ArrayList<>();
    private final AmenitiesListFragmentInteractionListener _listener;
    private final Context context;
    boolean isRWTAmenities;
    private int adapterPosition = 0;

    public AmenitiesAdapter(Context context, AmenitiesListFragmentInteractionListener listener, boolean isRWTAmenities) {
        _listener = listener;
        this.context = context;
        this.isRWTAmenities = isRWTAmenities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_aminities, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        if (isRWTAmenities) {
            if (position == 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*walkThroughHighlight(holder.viewMain);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                        sharedPreferences.edit().putBoolean("isRWTAmenities", false).apply();
                        isRWTAmenities = sharedPreferences.getBoolean("isRWTAmenities", true);*/
                    }
                }, 500);
            }
        }

        holder.tvTitle.setText(holder.item.displayName);
        holder.tvTitleNickName.setText("(" + holder.item.amenitiesType.name + ")");
        holder.tvDescription.setText(holder.item.description);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onListFragmentInteraction(holder.item);
            }
        });

    }

    private void walkThroughHighlight(View view1) {
        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_amenities_info)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .show();


    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<Amenities> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvTitle;
        public final TextView tvTitleNickName;
        public final TextView tvDescription;
        public final LinearLayout llmain;
        public Amenities item;
        private GridLayout viewMain;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            llmain = (LinearLayout) view.findViewById(R.id.llmain);
            viewMain = (GridLayout) view.findViewById(R.id.viewMain);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvTitleNickName = (TextView) view.findViewById(R.id.tvTitleNickName);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        }
    }

}
