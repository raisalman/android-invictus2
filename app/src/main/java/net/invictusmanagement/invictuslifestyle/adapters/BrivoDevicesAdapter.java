package net.invictusmanagement.invictuslifestyle.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.policy.Resource;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.fragments.BrivoDevicesFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.BrivoDevicesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.NotificationsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.BrivoDeviceData;

import java.util.ArrayList;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class BrivoDevicesAdapter extends RecyclerView.Adapter<BrivoDevicesAdapter.ViewHolder> {

    private final List<BrivoDeviceData> _dataSource = new ArrayList<>();
    private final BrivoDevicesListFragmentInteractionListener _listener;
    public RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.loader_animation).diskCacheStrategy(DiskCacheStrategy.ALL).priority(Priority.HIGH).dontAnimate().dontTransform();
    private Context context;
    private BrivoDevicesFragment brivoDevicesFragment;

    public BrivoDevicesAdapter(BrivoDevicesFragment brivoDevicesFragment, BrivoDevicesListFragmentInteractionListener listener, Context context) {
        this.brivoDevicesFragment = brivoDevicesFragment;
        _listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brivo_devices, parent, false);
        return new ViewHolder(view);
    }

/*
    private void walkThroughHighlight(View view1) {
        Lighter.with(TabbedActivity.tabbedActivity).addHighlight(new LighterParameter.Builder().setHighlightedView(view1).setLighterShape(new RectShape()).setTipLayoutId(R.layout.layout_notification_info).setTipViewRelativeDirection(Direction.BOTTOM).setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0)).build()).show();


    }
*/

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.item = _dataSource.get(position);

        holder.tvItemName.setText(_dataSource.get(position).getName());
        if (_dataSource.get(position).getType().equals("temp_flood_sensor")) {
            int temp = Math.round(Float.parseFloat(_dataSource.get(position).getTemperature().toString()));
            holder.tvDetails.setText("Temperature : " + temp);
            holder.tvItemTemp.setText(temp+"");
        } else {
            holder.tvDetails.setText(_dataSource.get(position).getState());
        }


        if (_dataSource.get(position).getType().equals("switch")) {
            holder.frameSwitch.setVisibility(View.VISIBLE);
            holder.tvItemTemp.setVisibility(View.GONE);
            holder.imgItem.setVisibility(View.GONE);


//            set value
            if (_dataSource.get(position).getState().equals("on")) {
                holder.switchTemp.setChecked(true);
            } else {
                holder.switchTemp.setChecked(false);
            }


        } else if (_dataSource.get(position).getType().equals("temp_flood_sensor")) {
            holder.frameSwitch.setVisibility(View.GONE);
            holder.tvItemTemp.setVisibility(View.VISIBLE);
            holder.imgItem.setVisibility(View.GONE);


        } else if (_dataSource.get(position).getType().equals("thermostat")) {
            holder.frameSwitch.setVisibility(View.GONE);
            holder.tvItemTemp.setVisibility(View.GONE);
            holder.imgItem.setVisibility(View.VISIBLE);

            holder.imgItem.setImageResource(R.drawable.ic_thermometar);

        } else if (_dataSource.get(position).getType().equals("window_door_sensor")) {
            holder.frameSwitch.setVisibility(View.GONE);
            holder.tvItemTemp.setVisibility(View.GONE);
            holder.imgItem.setVisibility(View.VISIBLE);

            holder.imgItem.setImageResource(R.drawable.ic_window_door);

        } else {
            holder.frameSwitch.setVisibility(View.GONE);
            holder.tvItemTemp.setVisibility(View.GONE);
            holder.imgItem.setVisibility(View.VISIBLE);

            if (_dataSource.get(position).getState().equals("secured")) {
                holder.imgItem.setImageResource(R.drawable.ic_lock);
            } else {
                holder.imgItem.setImageResource(R.drawable.ic_lock_open);
            }

        }

        holder.imgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, brivoDevicesFragment, position);
                }
            }
        });

        holder.tvItemTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, brivoDevicesFragment, position);
                }
            }
        });


        holder.switchTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, brivoDevicesFragment, position);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }


    public void refresh(List<BrivoDeviceData> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
    }

    public void refreshItem(BrivoDeviceData item, int position) {
        if (item == null) return;
        _dataSource.set(position, item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvItemName, tvDetails, tvItemTemp;
        public BrivoDeviceData item;
        public AppCompatImageView imgItem;
        public SwitchCompat switchTemp;
        public FrameLayout frameSwitch;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvItemName = (TextView) view.findViewById(R.id.tvItemName);
            tvItemTemp = (TextView) view.findViewById(R.id.tvItemTemp);
            tvDetails = (TextView) view.findViewById(R.id.tvDetails);
            imgItem = (AppCompatImageView) view.findViewById(R.id.imgItem);
            switchTemp = (SwitchCompat) view.findViewById(R.id.switchTemp);
            frameSwitch = (FrameLayout) view.findViewById(R.id.frameSwitch);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvItemName.getText() + "'";
        }
    }
}
