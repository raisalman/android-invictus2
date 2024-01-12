package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.fragments.HealthFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.HealthVideoListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.HealthVideo;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

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

public class HealthAdapter extends RecyclerView.Adapter<HealthAdapter.ViewHolder> {

    private final List<HealthVideo> _dataSource = new ArrayList<>();
    private final List<HealthVideo> _dataSourceTotal = new ArrayList<>();
    private final HealthVideoListFragmentInteractionListener _listener;
    private final HealthFragment healthFragment;
    private final Context context;
    Boolean isRWTHealthAndWellness;

    public HealthAdapter(Context context, HealthVideoListFragmentInteractionListener listener, HealthFragment healthFragment, Boolean isRWTHealthAndWellness) {
        _listener = listener;
        this.healthFragment = healthFragment;
        this.context = context;
        this.isRWTHealthAndWellness = isRWTHealthAndWellness;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_health_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item = _dataSource.get(position);

        if (isRWTHealthAndWellness) {
            if (position == 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        walkThroughHighlight(holder.viewMain);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                        if (TabbedActivity.isGuestUser) {
                            sharedPreferences.edit().putBoolean("isGWTHealthAndWellness", false).apply();
                            isRWTHealthAndWellness = sharedPreferences.getBoolean("isGWTHealthAndWellness", true);
                        } else {
                            sharedPreferences.edit().putBoolean("isRWTHealthAndWellness", false).apply();
                            isRWTHealthAndWellness = sharedPreferences.getBoolean("isRWTHealthAndWellness", true);
                        }

                    }
                }, 500);
            }
        }

        DateFormat formatter = SimpleDateFormat.getDateInstance(DateFormat.FULL);
        String date = formatter.format(holder.item.createdUtc);
        holder.createdUtcTextView.setText(date);
        holder.titleTextView.setText(holder.item.title);

        if (holder.item.isFavorite) {
            holder._favorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
        } else {
            holder._favorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unfavorite));
        }

        Glide.with(context)
                .load(holder.item.healthVideoThumbnailUrl)
                .centerCrop()
                .placeholder(R.drawable.exomedia_ic_pause_white)
                .into(holder._thumbnail);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.watchVideo(holder.item, holder.item.id, date);
                }
            }
        });

        holder._favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.item.isFavorite) {
                    videoAsUnFavorite(holder.item);
                } else {
                    videoAsFavorite(holder.item);
                }
            }
        });


    }

    private void walkThroughHighlight(View view1) {
        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_health_wellness_info)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                /*.addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view2)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_billboard_add_post)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 0, 30))
                        .build())*/
                .show();


    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public int totalItemCount() {
        return _dataSourceTotal.size();
    }

    public void refresh(List<HealthVideo> list, Boolean isFavourite) {
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
        public final View view, viewMain;
        public final TextView titleTextView;
        public final TextView createdUtcTextView;
        public final LinearLayout llMain;
        public final ImageView _favorite, _thumbnail;
        public HealthVideo item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            viewMain = view.findViewById(R.id.viewMain);
            _favorite = view.findViewById(R.id.imgFavorite);
            _thumbnail = view.findViewById(R.id.videoThumbnail);

            titleTextView = view.findViewById(R.id.title);
            createdUtcTextView = view.findViewById(R.id.createdUtc);
            llMain = view.findViewById(R.id.llMain);
        }

    }

    public void videoAsFavorite(final HealthVideo item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().videoAsFavorite(item);
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
                    healthFragment.refresh();
                }
            }
        }.execute();
    }

    public void videoAsUnFavorite(final HealthVideo item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().videoAsUnFavorite(item);
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
                    healthFragment.refresh();
                }
            }
        }.execute();
    }


    /*@SuppressLint("SimpleDateFormat")
    fun changeDateFormat(
        dateToConvert: String,
        currentDateFormat: String,
        desiredDateFormat: String
    ): String {
        try {
            if (dateToConvert.isNotEmpty()) {
                val formatter = SimpleDateFormat(currentDateFormat)
                val date = formatter.parse(dateToConvert) as Date
                val newFormat = SimpleDateFormat(desiredDateFormat)
                return newFormat.format(date)
            }
        } catch (e: Exception) {
            showLog(e.localizedMessage)
        }
        return ""
    }
*/

    public String changeDateFormat(String dateToConvert, String currentDateFormat, String desiredDateFormat) {
        try {
            if (!dateToConvert.isEmpty()) {
                SimpleDateFormat formatter = new SimpleDateFormat(currentDateFormat);
                Date date = formatter.parse(dateToConvert);
                SimpleDateFormat newFormate = new SimpleDateFormat(desiredDateFormat);
                return newFormate.format(date);

            }
        } catch (Exception e) {
            Log.d("--date", "chnageDateFormat: " + e.getLocalizedMessage());
        }
        return "";
    }

}
