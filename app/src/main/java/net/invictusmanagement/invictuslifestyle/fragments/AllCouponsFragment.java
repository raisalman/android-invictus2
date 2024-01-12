package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.BusinessTypesAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.BusinessTypesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.BusinessType;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.List;

public class AllCouponsFragment extends Fragment implements IRefreshableFragment {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private BusinessTypesListFragmentInteractionListener _listener;
    private RecyclerView _recyclerView;
    public BusinessTypesAdapter _adapter;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private TextView _feedback;
    private TextView _tvLoyaltyPoints;
    private Double totalLoyaltyPoint;
    private boolean isRWTCoupons;

    public AllCouponsFragment() {
    }

    @SuppressWarnings("unused")
    public static AllCouponsFragment newInstance() {
        return new AllCouponsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_coupons, viewGroup, false);
        if (view instanceof SwipeRefreshLayout) {

            if (TabbedActivity.isGuestUser) {
                isRWTCoupons = sharedPreferences.getBoolean("isGWTCoupons", true);
            } else {
                isRWTCoupons = sharedPreferences.getBoolean("isRWTCoupons", true);
            }
            _feedback = view.findViewById(R.id.feedback);
            _recyclerView = view.findViewById(R.id.list);
            _tvLoyaltyPoints = view.findViewById(R.id.tvLoyaltyPoints);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new BusinessTypesAdapter(_context, isRWTCoupons);
            _recyclerView.setAdapter(_adapter);
            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Coupons.value());
                    BusinessTypesFragment.newInstance().getLoyaltyPoint();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }


    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getBusinessTypes(new RestCallBack<List<BusinessType>>() {
            @Override
            public void onResponse(List<BusinessType> response) {
                _swipeRefreshLayout.setRefreshing(false);
                if (response != null) {
                    _adapter.refresh(response);
                    _adapter.notifyDataSetChanged();
                    Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
                    Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_load_coupon_categories),
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(WSException wse) {
                Toast.makeText(getActivity(), getString(R.string.error_load_coupon_categories),
                        Toast.LENGTH_LONG).show();
                _swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}