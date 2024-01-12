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
import net.invictusmanagement.invictuslifestyle.adapters.GuestDigitalKeysAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.GuestDigitalKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.GuestDigitalKey;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.List;

public class GuestDigitalKeysFragment extends Fragment implements IRefreshableFragment {

    public static GuestDigitalKeysAdapter _adapter;
    private List<GuestDigitalKey> guestDigitalKeys;
    private Boolean isDataAvailable = false;
    private Boolean isActive = true;
    private GuestDigitalKeysListFragmentInteractionListener _listener;
    private RecyclerView _recyclerView;
    public static Boolean isGWTDigitalKey = false;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private TextView _feedback, tvNoActive;
    private TextView _tvAll, _tvActive;
    private ImageView _switch;
    private ConstraintLayout clSwitch;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);

    public GuestDigitalKeysFragment() {
    }

    @SuppressWarnings("unused")
    public static GuestDigitalKeysFragment newInstance() {
        return new GuestDigitalKeysFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_guest_digital_keys, container, false);
        if (view instanceof SwipeRefreshLayout) {

            isGWTDigitalKey = sharedPreferences.getBoolean("isGWTDigitalKey", true);
            tvNoActive = view.findViewById(R.id.tvNoActive);
            _feedback = view.findViewById(R.id.feedback);
            clSwitch = view.findViewById(R.id.clSwitch);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new GuestDigitalKeysAdapter(_context, _listener, this, isGWTDigitalKey);
            _recyclerView.setAdapter(_adapter);

            _tvAll = view.findViewById(R.id.tvAll);
            _tvActive = view.findViewById(R.id.tvActive);

            _tvActive.setTypeface(Typeface.DEFAULT_BOLD);
            _tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));

            _switch = view.findViewById(R.id.filterSwitch);
            _switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchVisibility(!isActive);
                    refreshAdapter();
                }
            });

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.DigitalKey.value());
                    refresh();
                }
            });
            refresh();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
        if (_context instanceof GuestDigitalKeysListFragmentInteractionListener) {
            _listener = (GuestDigitalKeysListFragmentInteractionListener) _context;
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

    private void switchVisibility(Boolean isUnActive) {
        if (isUnActive) {
            _switch.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_deactive_switch));
            _tvActive.setTypeface(Typeface.DEFAULT_BOLD);
            _tvAll.setTypeface(Typeface.DEFAULT);

            _tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            _tvAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSwitchTextNotSelected));

            isActive = true;
        } else {
            _switch.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_switch));
            _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
            _tvActive.setTypeface(Typeface.DEFAULT);

            _tvAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            _tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSwitchTextNotSelected));

            isActive = false;
        }

    }

    public void refreshAdapter() {
        if (isDataAvailable) {
            _adapter.refresh(guestDigitalKeys, isActive);
            _adapter.notifyDataSetChanged();
            if (_adapter.getItemCount() > 0) {
                tvNoActive.setVisibility(View.GONE);
            } else {
                tvNoActive.setVisibility(View.VISIBLE);
            }
        }

    }


    public void setErrorView() {
        Utilities.showHide(_context, _feedback, _adapter.totalItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.totalItemCount() > 0);
        Utilities.showHide(_context, clSwitch, _adapter.totalItemCount() > 0);
        isDataAvailable = _adapter.totalItemCount() > 0;
        if (_adapter.totalItemCount() > 0) {
            if (_adapter.getItemCount() > 0) {
                tvNoActive.setVisibility(View.GONE);
            } else {
                tvNoActive.setVisibility(View.VISIBLE);
            }
        }
    }

    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    guestDigitalKeys = MobileDataProvider.getInstance().getDigitalKeysForGuest();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                _adapter.refresh(guestDigitalKeys, isActive);
                _adapter.notifyDataSetChanged();
                _swipeRefreshLayout.setRefreshing(false);
                setErrorView();
                if (!success)
                    Toast.makeText(getActivity(), "Unable to refresh digital keys. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }
}
