package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.RoundedLetterView;
import net.invictusmanagement.invictuslifestyle.fragments.BusinessCouponsFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.PromotionsFavListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.PromotionFav;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PromotionsFragmentAdapter extends RecyclerView.Adapter<PromotionsFragmentAdapter.ViewHolder> {

    private final List<PromotionFav> _dataSource = new ArrayList<>();
    private final List<PromotionFav> _dataSourceTotal = new ArrayList<>();
    private final PromotionsFavListFragmentInteractionListener _listener;
    private final BusinessCouponsFragment businessCouponsFragment;
    private final Context context;
    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();

    public PromotionsFragmentAdapter(Context context, BusinessCouponsFragment businessCouponsFragment, PromotionsFavListFragmentInteractionListener listener) {
        _listener = listener;
        this.context = context;
        this.businessCouponsFragment = businessCouponsFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_promotion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        if (holder.item.isFavorite) {
            holder._favorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
        } else {
            holder._favorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unfavorite));
        }

        holder.iconRoundedLetterView.setTitleText(holder.item.name.substring(0, 1).toUpperCase());
        holder.nameTextView.setText(holder.item.name);
        holder.tvBusinessName.setText(holder.item.businessName);
        DateFormat formatter = SimpleDateFormat.getDateInstance(DateFormat.FULL);
        holder.toUtcTextView.setText(formatter.format(holder.item.toUtc));
        holder.descriptionTextView.setText(holder.item.description);

        if (holder.item.isAnytimeCoupon) {
            holder.llAnytime.setVisibility(View.VISIBLE);
            holder.llToUtc.setVisibility(View.GONE);
        } else {
            holder.llAnytime.setVisibility(View.GONE);
            holder.llToUtc.setVisibility(View.VISIBLE);
        }

        holder.tvRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, false);
                }
            }
        });
        holder.llRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item, false);
                }
            }
        });

        holder._favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.item.isFavorite) {
                    couponsAsUnFavorite(holder.item);
                } else {
                    couponsAsFavorite(holder.item);
                }
            }
        });

        if (holder.item.promotionAdvertises != null)
            if (holder.item.promotionAdvertises.size() > 0) {
                holder.clThumbnail.setVisibility(View.VISIBLE);
                holder.imgPlayIcon.setVisibility(View.VISIBLE);
                holder.imgAd.setVisibility(View.VISIBLE);
                holder.iconRoundedLetterView.setVisibility(View.GONE);

                Glide.with(context).load(holder.item.promotionAdvertises
                        .get(0).getAdvertiseFileSrc() + ".jpg").apply(options).into(holder.imgAd);
                if (holder.item.promotionAdvertises.get(0).isImage()) {
                    holder.imgPlayIcon.setVisibility(View.GONE);
                } else {
                    holder.imgPlayIcon.setVisibility(View.VISIBLE);
                }

                holder.clThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != _listener) {
                            _listener.onListFragmentInteraction(holder.item, true);
                        }
                    }
                });

            } else {
                holder.clThumbnail.setVisibility(View.GONE);
                holder.imgPlayIcon.setVisibility(View.GONE);
                holder.imgAd.setVisibility(View.GONE);
                holder.iconRoundedLetterView.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public int totalItemCount() {
        return _dataSourceTotal.size();
    }

    public void refresh(List<PromotionFav> list, Boolean isFavourite) {
        if (list == null) return;
        _dataSource.clear();
        _dataSourceTotal.clear();
        _dataSourceTotal.addAll(list);
        if (!list.isEmpty()) {
            if (isFavourite) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isFavorite) {
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
        public final TextView toUtcTextView;
        public final TextView tvRedeem;
        public final TextView tvBusinessName;
        public final TextView descriptionTextView;
        public final ConstraintLayout clThumbnail;
        public ImageView _favorite, imgAd, imgPlayIcon;
        public PromotionFav item;
        private LinearLayout llRedeem, llToUtc, llAnytime;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            iconRoundedLetterView = (RoundedLetterView) view.findViewById(R.id.icon);
            nameTextView = (TextView) view.findViewById(R.id.name);
            toUtcTextView = (TextView) view.findViewById(R.id.toUtc);
            tvRedeem = (TextView) view.findViewById(R.id.tvRedeem);
            tvBusinessName = (TextView) view.findViewById(R.id.businessName);
            descriptionTextView = (TextView) view.findViewById(R.id.description);
            _favorite = (ImageView) view.findViewById(R.id.imgFavorite);
            imgAd = (ImageView) view.findViewById(R.id.imgAd);
            imgPlayIcon = (ImageView) view.findViewById(R.id.imgPlayIcon);
            llRedeem = (LinearLayout) view.findViewById(R.id.llRedeem);
            llToUtc = (LinearLayout) view.findViewById(R.id.llToUtc);
            llAnytime = (LinearLayout) view.findViewById(R.id.llAnytime);
            clThumbnail = (ConstraintLayout) view.findViewById(R.id.clThumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameTextView.getText() + "'";
        }
    }


    public void couponsAsFavorite(final PromotionFav item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().couponsAsFavorite(item.id);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    notifyDataSetChanged();
                    businessCouponsFragment.refresh();
                }
            }
        }.execute();
    }

    public void couponsAsUnFavorite(final PromotionFav item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().couponsAsUnFavorite(item.id);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    notifyDataSetChanged();
                    businessCouponsFragment.refresh();
                }
            }
        }.execute();
    }
}
