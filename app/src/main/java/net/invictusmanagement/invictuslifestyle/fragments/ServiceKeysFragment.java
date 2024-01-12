package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.NewServiceKeyActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.ServiceKeysAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.ServiceKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.ServiceKey;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.List;

public class ServiceKeysFragment extends Fragment implements IRefreshableFragment {

    public static Boolean isRWTServiceKey = false;
    public static ServiceKeysListFragmentInteractionListener _listener;
    public static RecyclerView _recyclerView;
    public static ServiceKeysAdapter _adapter;
    public static SwipeRefreshLayout _swipeRefreshLayout;
    public static Context _context;
    public static TextView _feedback, tvNoActive;
    public static FloatingActionButton fab;
    public static TextView txtCreateKey;
    public static RelativeLayout rlCreate;
    public static List<ServiceKey> serviceKeys;
    public static Boolean isDataAvailable = false;
    private TextView _tvAll, _tvActive;
    private ImageView _switch;
    public static Boolean isActive = true;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);

    public ServiceKeysFragment() {
    }

    @SuppressWarnings("unused")
    public static ServiceKeysFragment newInstance() {
        return new ServiceKeysFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_service_keys, container, false);
        if (view instanceof SwipeRefreshLayout) {

            isRWTServiceKey = sharedPreferences.getBoolean("isRWTServiceKey", true);
            txtCreateKey = view.findViewById(R.id.txtCreateKey);
            rlCreate = view.findViewById(R.id.rlCreate);
            rlCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(),
                            NewServiceKeyActivity.class), ((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_SERVICE_KEYS);
                }
            });

            txtCreateKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(),
                            NewServiceKeyActivity.class), ((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_SERVICE_KEYS);
                }
            });

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

            tvNoActive = view.findViewById(R.id.tvNoActive);
            _feedback = view.findViewById(R.id.feedback);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new ServiceKeysAdapter(_context, _listener, this,
                    isRWTServiceKey, fab, rlCreate);
            _recyclerView.setAdapter(_adapter);

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
            refresh();
        }

        return view;
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
        if (_context instanceof ServiceKeysListFragmentInteractionListener) {
            _listener = (ServiceKeysListFragmentInteractionListener) _context;
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

    public void refreshAdapter() {
        if (isDataAvailable) {
            Boolean isActive = ServiceKeysFragment.isActive;
            _adapter.refresh(serviceKeys, isActive);
            _adapter.notifyDataSetChanged();
            if (_adapter.getItemCount() > 0) {
                tvNoActive.setVisibility(View.GONE);
            } else {
                tvNoActive.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callGetServiceKeyAPI() {
        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getServiceKey(new RestCallBack<List<ServiceKey>>() {
            @Override
            public void onResponse(List<ServiceKey> response) {
                _swipeRefreshLayout.setRefreshing(false);
                if (response != null) {
                    serviceKeys = response;
                    _adapter.refresh(serviceKeys, ServiceKeysFragment.isActive);
                    _adapter.notifyDataSetChanged();
                    _swipeRefreshLayout.setRefreshing(false);
                    setErrorView();
                } else {
                    Utilities.showLongToast(_context,
                            getString(R.string.error_service_key_refresh));
                }
            }

            @Override
            public void onFailure(WSException wse) {
                _swipeRefreshLayout.setRefreshing(false);
                Utilities.showLongToast(_context,
                        getString(R.string.error_service_key_refresh));
            }
        });
    }

    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        callGetServiceKeyAPI();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void setErrorView() {
        Utilities.showHide(_context, _feedback, _adapter.totalItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.totalItemCount() > 0);
        isDataAvailable = _adapter.totalItemCount() > 0;
        if (_adapter.totalItemCount() > 0) {
            if (_adapter.getItemCount() > 0) {
                tvNoActive.setVisibility(View.GONE);
            } else {
                tvNoActive.setVisibility(View.VISIBLE);
            }
        }
    }
}