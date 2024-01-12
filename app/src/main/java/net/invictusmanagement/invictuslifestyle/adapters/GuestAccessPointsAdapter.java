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
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.customviews.SlideView;
import net.invictusmanagement.invictuslifestyle.fragments.GuestAccessPointsFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.AccessPointsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.OnFinishListener;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.AccessPointFavUnFavRequest;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;
import okhttp3.ResponseBody;

public class GuestAccessPointsAdapter extends RecyclerView.Adapter<GuestAccessPointsAdapter.ViewHolder> {

    private final Context _context;
    private final List<AccessPoint> _dataSource = new ArrayList<>();
    private final List<AccessPoint> _dataSourceTotal = new ArrayList<>();
    private final AccessPointsListFragmentInteractionListener _listener;
    private final GuestAccessPointsFragment accessPointsFragment;
    private boolean isRWTAccessPoint;
    private boolean isFavouriteAll = false;
    private Boolean isGuestUser;

    public GuestAccessPointsAdapter(Context context, GuestAccessPointsFragment accessPointsFragment,
                                    AccessPointsListFragmentInteractionListener listener,
                                    Boolean isRWTAccessPoint, Boolean isGuestUser) {
        _context = context;
        _listener = listener;
        this.accessPointsFragment = accessPointsFragment;
        this.isRWTAccessPoint = isRWTAccessPoint;
        this.isGuestUser = isGuestUser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fragment_accesspoint, parent, false);
        return new ViewHolder(view);
    }

    private void walkThroughHighlight(View view1) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Lighter.with(TabbedActivity.tabbedActivity)
                        .addHighlight(new LighterParameter.Builder()
                                .setHighlightedView(view1)
                                .setLighterShape(new RectShape())
                                .setTipLayoutId(R.layout.layout_access_points_info)
                                .setTipViewRelativeDirection(Direction.BOTTOM)
                                .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                                .build())
                        .show();
            }
        }, 500);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        if (isRWTAccessPoint) {
            if (position == 0 && TabbedActivity.tabbedActivity._currentTabPosition
                    == ((TabbedActivity)_context)._sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS) {
                if (!isFavouriteAll) {
                    walkThroughHighlight(holder.glMain);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                    sharedPreferences.edit().putBoolean("isRWTAccessPoint", false).apply();
                    isRWTAccessPoint = sharedPreferences.getBoolean("isRWTAccessPoint", true);
                }
            }
        }

        GradientDrawable drawable = (GradientDrawable) holder.iconBackgroundImageView.getDrawable();
        drawable.setColor(ContextCompat.getColor(_context, R.color.colorPrimary));
        holder.iconImageView.setImageResource(holder.item.getType() == AccessPoint.Type.Pedestrian ? R.drawable.ic_directions_walk_white_24dp : R.drawable.ic_directions_car_white_24dp);
        holder.nameTextView.setText(holder.item.getName());
        holder.typeTextView.setText(holder.item.getType().toString());

        if (!isGuestUser) {
            holder._favorite.setVisibility(View.VISIBLE);
        } else {
            holder._favorite.setVisibility(View.GONE);
        }

        if (holder.item.unlockingStatus == 0) {
            holder.slideView.setBackground(_context
                    .getDrawable(R.drawable.rounded_bottom_corner_gray));
        } else if (holder.item.unlockingStatus == 1) {
            holder.slideView.setBackground(_context
                    .getDrawable(R.drawable.rounded_bottom_corner_green));
            resetItem(holder.item, position);
        } else if (holder.item.unlockingStatus == 2) {
            holder.slideView.setBackground(_context
                    .getDrawable(R.drawable.rounded_bottom_corner_red));
            resetItem(holder.item, position);
        } else {
            holder.slideView.setBackground(_context
                    .getDrawable(R.drawable.rounded_bottom_corner_yellow));
        }


        holder.slideView.setOnFinishListener(new OnFinishListener() {
            @Override
            public void onFinish() {
                Utilities.addHaptic(holder.slideView);
                holder.item.unlockingStatus = 3;
                holder.slideView.reset();
                notifyItemChanged(position);
                _listener.onGuestSlideUnlockTapped(position, holder.item, accessPointsFragment);
            }
        });

    }

    private void resetItem(AccessPoint item, int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                item.unlockingStatus = 0;
                notifyItemChanged(position);
            }
        }, 4000);
    }

    private void accessPointAsUnFavoriteUnFav(AccessPoint item, String operator) {
        AccessPointFavUnFavRequest model = new AccessPointFavUnFavRequest();
        model.setUserAccessPointId(item.id);
        model.setFavorite(!item.getFavorite());
        model.setOperator(operator);
        WebService.getInstance().markAccessPointFavUnFav(model,
                new RestEmptyCallBack<ResponseBody>() {
                    @Override
                    public void onResponse(ResponseBody response) {
                        notifyDataSetChanged();
                        accessPointsFragment.refresh();
                    }

                    @Override
                    public void onFailure(WSException wse) {
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


    public void refresh(List<AccessPoint> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSourceTotal.clear();
        _dataSourceTotal.addAll(list);
        if (!list.isEmpty()) {
            List<AccessPoint> favList = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getFavorite() != null)
                    if (list.get(i).getFavorite()) {
                        favList.add(list.get(i));
                    }
            }

            List<AccessPoint> union = new ArrayList<>(list);
            union.addAll(favList);

            List<AccessPoint> intersection = new ArrayList<>(list);
            intersection.retainAll(favList);

            union.removeAll(intersection);

            _dataSource.addAll(favList);
            _dataSource.addAll(union);

            /*_dataSource.addAll(list);*/
        }
    }

    public void setItem(int position, int status) {
        _dataSource.get(position).unlockingStatus = status;
        notifyItemChanged(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView iconBackgroundImageView;
        public final ImageView iconImageView;
        public final TextView nameTextView;
        public final TextView typeTextView;
        public final ImageView _favorite;
        public final LinearLayout glMain;
        public AccessPoint item;
        public final SlideView slideView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            iconBackgroundImageView = view.findViewById(R.id.icon_background);
            iconImageView = view.findViewById(R.id.icon);
            nameTextView = view.findViewById(R.id.name);
            typeTextView = view.findViewById(R.id.type);
            _favorite = view.findViewById(R.id.imgFavorite);
            glMain = view.findViewById(R.id.glMain);
            slideView = view.findViewById(R.id.slide_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameTextView.getText() + "'";
        }
    }
}
