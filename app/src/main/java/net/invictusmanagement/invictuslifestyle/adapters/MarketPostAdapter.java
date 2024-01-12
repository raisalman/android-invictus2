package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.fragments.BillBoardFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.MarketPostListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.BulletinBoard;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.ArrayList;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class MarketPostAdapter extends RecyclerView.Adapter<MarketPostAdapter.ViewHolder> {

    private final List<BulletinBoard> _dataSource = new ArrayList<>();
    private final List<BulletinBoard> _dataSourceTotal = new ArrayList<>();
    private final MarketPostListFragmentInteractionListener _listener;
    private final BillBoardFragment billBoardFragment;
    private final Context context;
    private Boolean isMineList = false;
    boolean isRWTBulletinBoard;
    private RelativeLayout rlCreate;

    public MarketPostAdapter(Context context, BillBoardFragment billBoardFragment,
                             MarketPostListFragmentInteractionListener listener,
                             boolean isRWTBulletinBoard,
                             RelativeLayout rlCreate) {
        _listener = listener;
        this.rlCreate = rlCreate;
        this.context = context;
        this.billBoardFragment = billBoardFragment;
        this.isRWTBulletinBoard = isRWTBulletinBoard;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_marketplace, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        if (position == 0) {
            new Handler().postDelayed(() -> {
                if (isRWTBulletinBoard)
                    walkThroughHighlight(holder.clMain, rlCreate);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                sharedPreferences.edit().putBoolean("isRWTBulletinBoard", false).apply();
                isRWTBulletinBoard = sharedPreferences.getBoolean("isRWTBulletinBoard", true);
            }, 500);
        }
        if (isRWTBulletinBoard) {
        }

        if (isMineList) {
            if (holder.item.isApproved) {
                holder.llPostApproval.setVisibility(View.GONE);
                holder.tvPostApproval.setText("Approved");
            } else {
                holder.llPostApproval.setVisibility(View.VISIBLE);
                holder.llPostApproval.setGravity(Gravity.CENTER);
                holder.llPostApproval.setVisibility(View.VISIBLE);
                holder.tvPostApproval.setText("Pending");
            }

        } else {
            holder.llPostApproval.setVisibility(View.GONE);
        }

        if (holder.item.isService) {
            if (holder.item.isClosed) {
                holder.imgFavorite.setVisibility(View.GONE);
            } else {
                holder.imgFavorite.setVisibility(View.VISIBLE);
            }
        } else {
            if (holder.item.isSoldOut) {
                holder.imgFavorite.setVisibility(View.GONE);
            } else {
                holder.imgFavorite.setVisibility(View.VISIBLE);
            }
        }
        if (holder.item.isFavorite) {
            holder.imgFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
        } else {
            holder.imgFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unfavorite));
        }

        holder.tvPostTitle.setText(holder.item.title);
        int price = (int) Math.floor(Double.parseDouble(holder.item.price));
        holder.tvProductServicePrice.setText("$" + price);
        holder.tvDescription.setText(holder.item.description);
        if (holder.item.isMyPost) {
            if (holder.item.isService) {
                if (holder.item.isClosed) {
                    holder.imgEdit.setVisibility(View.GONE);
                } else {
                    holder.imgEdit.setVisibility(View.VISIBLE);
                }
            } else {
                if (holder.item.isSoldOut) {
                    holder.imgEdit.setVisibility(View.GONE);
                } else {
                    holder.imgEdit.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.imgEdit.setVisibility(View.GONE);
        }

        if (holder.item.isService) {
            holder.tvPostTitle.setMaxLines(2);
            holder.tvPostTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.llProductCondition.setVisibility(View.GONE);
            holder.tvPostDate.setVisibility(View.VISIBLE);
            holder.tvPerSerice.setVisibility(View.VISIBLE);
            if (holder.item.isHourPrice) {
                holder.tvPerSerice.setText("per hour");
            } else {
                holder.tvPerSerice.setText("per service");
            }
            if (holder.item.availableDate != null) {
                Utilities.converDateWithFormatter(holder.item.availableDate, holder.tvPostDate, "yyyy-MM-dd'T'HH:mm:ss", "EEEE, MMMM dd, yyyy");
            } else {
                holder.tvPostDate.setText("");
            }
        } else {
            holder.tvPostTitle.setMaxLines(1);
            holder.tvPostTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.tvPostDate.setVisibility(View.GONE);
            holder.tvPerSerice.setVisibility(View.GONE);
            if (holder.item.condition.length() > 0) {
                holder.llProductCondition.setVisibility(View.VISIBLE);
                holder.llProductCondition.setGravity(Gravity.CENTER);
            } else {
                holder.llProductCondition.setVisibility(View.GONE);
            }
            holder.tvProductCondition.setText(holder.item.condition);
        }

        holder.llPostDefine.setVisibility(View.VISIBLE);
        holder.llPostDefine.setGravity(Gravity.CENTER);
        if (holder.item.isService) {
            holder.tvPostDefine.setText("Service");
        } else {
            holder.tvPostDefine.setText("Sell");
        }
        if (holder.item.marketPlaceImages.size() > 0) {
            Glide.with(context)
                    .load(holder.item.marketPlaceImages.get(0).marketPlaceImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.img_service_list)
                    .into(holder.imgPost);
        } else {
            holder.imgPost.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_service_list));
        }

        holder.clMain.setOnClickListener(v -> {
            if (null != _listener) {
                _listener.onListFragmentInteraction(holder.item);
            }
        });
        holder.imgEdit.setOnClickListener(v -> {
            Utilities.addHaptic(v);
            _listener.onListFragmentForEditInteraction(holder.item);
        });

        holder.imgFavorite.setOnClickListener(v -> {
            if (holder.item.isFavorite) {
                postAsUnFavorite(holder.item);
            } else {
                postAsFavorite(holder.item);
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

    public void refresh(List<BulletinBoard> list, Boolean isMine, Boolean isFavourite) {
        if (list == null) return;
        _dataSource.clear();
        _dataSourceTotal.clear();
        _dataSourceTotal.addAll(list);
        isMineList = isMine;
        if (!list.isEmpty()) {
            if (isFavourite) {
                if (isMine) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isMyPost && list.get(i).isFavorite) {
                            _dataSource.add(list.get(i));
                        }
                    }
                } else {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).isService) {
                            if (!list.get(j).isClosed && list.get(j).isFavorite && list.get(j).isApproved) {
                                _dataSource.add(list.get(j));
                            }
                        } else {
                            if (!list.get(j).isSoldOut && list.get(j).isFavorite && list.get(j).isApproved) {
                                _dataSource.add(list.get(j));
                            }

                        }

                    }

                }
            } else {
                if (isMine) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isMyPost) {
                            _dataSource.add(list.get(i));
                        }
                    }
                } else {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).isService) {
                            if (!list.get(j).isClosed && list.get(j).isApproved) {
                                _dataSource.add(list.get(j));
                            }
                        } else {
                            if (!list.get(j).isSoldOut && list.get(j).isApproved) {
                                _dataSource.add(list.get(j));
                            }

                        }

                    }

                }
            }
        }
    }

    private void walkThroughHighlight(View view1, View view2) {
        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_billboard_info)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view2)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_billboard_add_post)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .show();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public BulletinBoard item;
        private final ConstraintLayout clMain;
        private final ImageView imgPost, imgEdit, imgFavorite;
        private final TextView tvPostTitle;
        private final TextView tvProductCondition;
        private final TextView tvDescription;
        private final TextView tvPostDate;
        private final TextView tvPostApproval;
        private final TextView tvProductServicePrice;
        private final TextView tvPerSerice;
        private final TextView tvPostDefine;
        private final LinearLayout llProductCondition;
        private final LinearLayout llPostDefine;
        private final LinearLayout llPostApproval;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            clMain = view.findViewById(R.id.clMain);
            tvPostDefine = view.findViewById(R.id.tvPostDefine);
            llPostApproval = view.findViewById(R.id.llPostApproval);
            tvPostApproval = view.findViewById(R.id.tvPostApproval);
            imgFavorite = view.findViewById(R.id.imgFavorite);
            llPostDefine = view.findViewById(R.id.llPostDefine);
            imgEdit = view.findViewById(R.id.imgEdit);
            imgPost = view.findViewById(R.id.imgPost);
            tvPostTitle = view.findViewById(R.id.tvPostTitle);
            llProductCondition = view.findViewById(R.id.llProductCondition);
            tvProductCondition = view.findViewById(R.id.tvProductCondition);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvPostDate = view.findViewById(R.id.tvPostDate);
            tvProductServicePrice = view.findViewById(R.id.tvProductServicePrice);
            tvPerSerice = view.findViewById(R.id.tvPerSerice);

        }
    }


    public void postAsFavorite(final BulletinBoard item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().markPostAsFav(Integer.valueOf(String.valueOf(item.id)));
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    billBoardFragment.refresh();
                    notifyDataSetChanged();
                }
            }
        }.execute();
    }

    public void postAsUnFavorite(final BulletinBoard item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().markPostAsUnFav(Integer.valueOf(String.valueOf(item.id)));
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    billBoardFragment.refresh();
                    notifyDataSetChanged();
                }
            }
        }.execute();
    }


}
