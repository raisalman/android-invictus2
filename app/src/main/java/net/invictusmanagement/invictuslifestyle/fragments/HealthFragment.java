package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.HealthAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.HealthVideoListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.HealthVideo;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.List;

public class HealthFragment extends Fragment implements IRefreshableFragment {

    public static HealthAdapter _adapter;
    private HealthVideoListFragmentInteractionListener _listener;
    private ConstraintLayout _clSwitch;
    private RecyclerView _recyclerView;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private TextView _feedback, _tvAll, _tvFavourites, _tvNoFavourite;
    private ImageView _switch;
    private Boolean isFavourite = false;
    private List<HealthVideo> healthVideoList;
    private Boolean isDataAvailable = false;
    private Boolean isRWTHealthAndWellness = false;
    private static HealthFragment instance;

    public HealthFragment() {
    }

    @SuppressWarnings("unused")
    public static HealthFragment newInstance() {
        if (instance != null)
            return instance;
        return new HealthFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_health, container, false);
        instance = this;
        if (view instanceof SwipeRefreshLayout) {

            if (TabbedActivity.isGuestUser) {
                isRWTHealthAndWellness = sharedPreferences.getBoolean("isGWTHealthAndWellness", true);
            } else {
                isRWTHealthAndWellness = sharedPreferences.getBoolean("isRWTHealthAndWellness", true);
            }
            _feedback = (TextView) view.findViewById(R.id.feedback);
            _tvAll = (TextView) view.findViewById(R.id.tvAll);
            _tvFavourites = (TextView) view.findViewById(R.id.tvFavourites);
            _tvNoFavourite = (TextView) view.findViewById(R.id.tvNoFavourite);
            _recyclerView = (RecyclerView) view.findViewById(R.id.list);
            _clSwitch = (ConstraintLayout) view.findViewById(R.id.clSwitch);
            _switch = (ImageView) view.findViewById(R.id.favSwitch);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new HealthAdapter(getContext(), _listener, this, isRWTHealthAndWellness);
            _recyclerView.setAdapter(_adapter);

            _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
            _tvAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            isFavourite = false;

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.HealthVideo.value());
                    refresh();
                }
            });

            _switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchVisibility(!isFavourite);
                    if (isDataAvailable) {
                        _adapter.refresh(healthVideoList, isFavourite);
                        _adapter.notifyDataSetChanged();
                        if (_adapter.getItemCount() > 0) {
                            _tvNoFavourite.setVisibility(View.GONE);
                        } else {
                            _tvNoFavourite.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });
            refresh();
        }
        return view;
    }

    private void switchVisibility(Boolean isUnFavourite) {
        if (isUnFavourite) {
            _switch.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_switch));
            _tvFavourites.setTypeface(Typeface.DEFAULT_BOLD);
            _tvAll.setTypeface(Typeface.DEFAULT);

            _tvFavourites.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            _tvAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSwitchTextNotSelected));

            isFavourite = true;
        } else {
            _switch.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_deactive_switch));
            _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
            _tvFavourites.setTypeface(Typeface.DEFAULT);

            _tvAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            _tvFavourites.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSwitchTextNotSelected));

            isFavourite = false;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
        if (_context instanceof HealthVideoListFragmentInteractionListener) {
            _listener = (HealthVideoListFragmentInteractionListener) _context;
        } else {
            throw new RuntimeException(_context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    public void refresh() {
        if (_swipeRefreshLayout == null)
            return;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    healthVideoList = MobileDataProvider.getInstance().getHealthVideos();
                    _adapter.refresh(MobileDataProvider.getInstance().getHealthVideos(), isFavourite);
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                _adapter.notifyDataSetChanged();
                _swipeRefreshLayout.setRefreshing(false);
                setErrorView();
                if (success != null) {
                    if (!success)
                        Toast.makeText(getActivity(), "Unable to refresh health and wellness. Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();
    }

    public void setErrorView() {
        Utilities.showHide(_context, _feedback, _adapter.totalItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.totalItemCount() > 0);
        Utilities.showHide(_context, _clSwitch, _adapter.totalItemCount() > 0);
        isDataAvailable = _adapter.totalItemCount() > 0;
        if (_adapter.totalItemCount() > 0) {
            if (_adapter.getItemCount() > 0) {
                _tvNoFavourite.setVisibility(View.GONE);
            } else {
                _tvNoFavourite.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("--OnResume", "onResume: ");
        refresh();
    }
}
