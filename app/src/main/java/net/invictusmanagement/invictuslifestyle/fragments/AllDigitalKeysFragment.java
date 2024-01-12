package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import net.invictusmanagement.invictuslifestyle.activities.NewDigitalKeyTabActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.DigitalKeysAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.interfaces.DigitalKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.List;

public class AllDigitalKeysFragment extends Fragment implements IRefreshableFragment {

    public static Boolean isRWTDigitalKey = false;
    public static DigitalKeysListFragmentInteractionListener _listener;
    public static RecyclerView _recyclerView;
    public static DigitalKeysAdapter _adapter;
    public static SwipeRefreshLayout _swipeRefreshLayout;
    public static Context _context;
    public static TextView _feedback, tvNoActive;
    public static FloatingActionButton fab;
    public static TextView txtCreateKey;
    public static RelativeLayout rlCreate;
    public static List<DigitalKey> digitalKeys;
    public static Boolean isDataAvailable = false;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private static AllDigitalKeysFragment instance;

    public AllDigitalKeysFragment() {
    }

    @SuppressWarnings("unused")
    public static AllDigitalKeysFragment newInstance() {
        if(instance != null)
            return instance;
        return new AllDigitalKeysFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (HomeFragment.isQuickKey) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(new Intent(getContext(), NewDigitalKeyTabActivity.class),
                            ((TabbedActivity) getActivity())._sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS);
                }
            }, 500);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_digital_keys, container, false);
        instance = this;
        if (view instanceof SwipeRefreshLayout) {

            isRWTDigitalKey = sharedPreferences.getBoolean("isRWTDigitalKey", true);
            txtCreateKey = view.findViewById(R.id.txtCreateKey);
            rlCreate = view.findViewById(R.id.rlCreate);
            rlCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(),
                                    NewDigitalKeyTabActivity.class),
                            ((TabbedActivity) getActivity())._sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS);
                }
            });

            txtCreateKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(),
                                    NewDigitalKeyTabActivity.class),
                            ((TabbedActivity) getActivity())._sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS);
                }
            });

            tvNoActive = view.findViewById(R.id.tvNoActive);
            _feedback = view.findViewById(R.id.feedback);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new DigitalKeysAdapter(_context, _listener, this,
                    isRWTDigitalKey, fab, rlCreate);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
        if (_context instanceof DigitalKeysListFragmentInteractionListener) {
            _listener = (DigitalKeysListFragmentInteractionListener) _context;
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
            Boolean isActive = DigitalKeysFragment.isActive;
            _adapter.refresh(digitalKeys, isActive);
            _adapter.notifyDataSetChanged();
            if (_adapter.getItemCount() > 0) {
                tvNoActive.setVisibility(View.GONE);
            } else {
                tvNoActive.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callGetDigitalKeyAPI() {
        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getDigitalKey(new RestCallBack<List<DigitalKey>>() {
            @Override
            public void onResponse(List<DigitalKey> response) {
                _swipeRefreshLayout.setRefreshing(false);
                if (response != null) {
                    digitalKeys = response;
                    _adapter.refresh(digitalKeys, DigitalKeysFragment.isActive);
                    _adapter.notifyDataSetChanged();
                    _swipeRefreshLayout.setRefreshing(false);
                    setErrorView();
                } else {
                    Utilities.showLongToast(_context,
                            getString(R.string.error_digital_key_refresh));
                }
            }

            @Override
            public void onFailure(WSException wse) {
                _swipeRefreshLayout.setRefreshing(false);
                Utilities.showLongToast(_context,
                        getString(R.string.error_digital_key_refresh));
            }
        });
    }

    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        callGetDigitalKeyAPI();
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