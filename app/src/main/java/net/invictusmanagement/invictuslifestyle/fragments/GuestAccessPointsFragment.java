package net.invictusmanagement.invictuslifestyle.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.brivo.sdk.BrivoSDKInitializationException;
import com.brivo.sdk.model.BrivoError;
import com.brivo.sdk.onair.interfaces.IOnRedeemPassListener;
import com.brivo.sdk.onair.interfaces.IOnRetrieveSDKLocallyStoredPassesListener;
import com.brivo.sdk.onair.model.BrivoOnairPass;
import com.brivo.sdk.onair.repository.BrivoSDKOnair;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.GuestAccessPointsAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.AccessPointsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.AccessCodeResponse;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.utils.BrivoSampleConstants;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;
import net.invictusmanagement.invictuslifestyle.widgets.NewWidgetActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GuestAccessPointsFragment extends Fragment implements IRefreshableFragment {

    public static GuestAccessPointsAdapter _adapter;
    private static TextView _tvAll;
    private AccessPointsListFragmentInteractionListener _listener;
    private RecyclerView _recyclerView;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private SharedPreferences sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private TextView _feedback;
    private TextView _tvNoFavourite;
    private List<AccessPoint> accessPoints = new ArrayList<>();
    private Boolean isDataAvailable = false;

    public GuestAccessPointsFragment() {
    }

    @SuppressWarnings("unused")
    public static GuestAccessPointsFragment newInstance() {
        return new GuestAccessPointsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accesspoints_list, container, false);
        initBrivoInstance();
        if (view instanceof SwipeRefreshLayout) {
            _feedback = view.findViewById(R.id.feedback);
            _tvAll = view.findViewById(R.id.tvAll);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
            _tvNoFavourite = view.findViewById(R.id.tvNoFavourite);
            _adapter = new GuestAccessPointsAdapter(_context,
                    this, _listener, false, true);
            _recyclerView.setAdapter(_adapter);
            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setEnabled(true);
            _swipeRefreshLayout.setRefreshing(true);
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.AccessPoints.value());
                    refresh();
                }
            });
            refresh();
        }
        return view;
    }

    private void callAPIForGetAccessCode() {
        ProgressDialog.showProgress(_context);
        WebService.getInstance().getAccessCode(new RestCallBack<AccessCodeResponse>() {
            @Override
            public void onResponse(AccessCodeResponse response) {
                ProgressDialog.dismissProgress();
                if (response != null)
                    brivoRedeemPass(response);
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
            }
        });
    }

    private void brivoRedeemPass(AccessCodeResponse codeResponse) {
        try {
            BrivoSDKOnair.getInstance().redeemPass(codeResponse.getReferenceId(),
                    codeResponse.getAccessCode(), new IOnRedeemPassListener() {
                        @Override
                        public void onSuccess(BrivoOnairPass pass) {
                            //Manage pass
                            BrivoSampleConstants.BRIVO_TOKEN = pass.getBrivoOnairPassCredentials().getTokens();
                            BrivoSampleConstants.PASS_ID = pass.getPassId();
                        }

                        @Override
                        public void onFailed(@NonNull BrivoError error) {
                        }
                    });
        } catch (BrivoSDKInitializationException e) {
            e.printStackTrace();
        }
    }

    private void initBrivoInstance() {
        try {
            BrivoSDKOnair.getInstance().retrieveSDKLocallyStoredPasses(new IOnRetrieveSDKLocallyStoredPassesListener() {
                @Override
                public void onSuccess(LinkedHashMap<String, BrivoOnairPass> passes) {
                    //Manage retrieved passes
                    if (passes != null) {
                        if (passes.keySet().toArray().length > 0) {
                            String passId = (String) passes.keySet().toArray()[0];
                            if (passId != null) {
                                BrivoSampleConstants.PASS_ID = passId;
                                if (passes.get(passId) != null) {
                                    BrivoSampleConstants.BRIVO_TOKEN = passes.get(passId).getBrivoOnairPassCredentials().getTokens();
                                } else {
                                    callAPIForGetAccessCode();
                                }
                            } else {
                                callAPIForGetAccessCode();
                            }
                        } else {
                            callAPIForGetAccessCode();
                        }
                    } else {
                        callAPIForGetAccessCode();
                    }
                }

                @Override
                public void onFailed(@NonNull BrivoError error) {
                    //Handle error
                    callAPIForGetAccessCode();
                }
            });
        } catch (BrivoSDKInitializationException e) {
            e.printStackTrace();
            callAPIForGetAccessCode();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        _context = context;
        if (_context instanceof AccessPointsListFragmentInteractionListener) {
            _listener = (AccessPointsListFragmentInteractionListener) _context;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getAccessPoints(true,
                new RestCallBack<List<AccessPoint>>() {
                    @Override
                    public void onResponse(List<AccessPoint> response) {
                        _swipeRefreshLayout.setRefreshing(false);
                        accessPoints = response;

                        if (accessPoints != null) {
                            _adapter.refresh(accessPoints);
                            _adapter.notifyDataSetChanged();
                            _swipeRefreshLayout.setRefreshing(false);
                            setErrorView();

                            Intent intent2 = new Intent(TabbedActivity.tabbedActivity, NewWidgetActivity.class);
                            intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                            int[] ids = AppWidgetManager.getInstance(TabbedActivity.tabbedActivity)
                                    .getAppWidgetIds(new ComponentName(TabbedActivity.tabbedActivity,
                                            NewWidgetActivity.class));
                            intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                            TabbedActivity.tabbedActivity.sendBroadcast(intent2);
                        }
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        setErrorView();
                        _swipeRefreshLayout.setRefreshing(false);
                        String currentString = wse.getServerMessage();
                        if (currentString.startsWith("301")) {
                            String[] separated = currentString.split("301");
                            _feedback.setText(separated[1].trim());
                        } else {
                            Toast.makeText(getActivity(),
                                    getString(R.string.error_fetching_access_point),
                                    Toast.LENGTH_LONG).show();
                            _feedback.setText(getString(R.string.no_access_point_with_swipe_down_to_refresh));
                        }
                    }
                }
        );
    }


    public void setErrorView() {
        Utilities.showHide(_context, _feedback, _adapter.totalItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.totalItemCount() > 0);
        isDataAvailable = _adapter.totalItemCount() > 0;
    }

    public void notifyData(int position, int status) {
        _adapter.setItem(position, status);
    }

    public void notifyData(int status, String accessPointId) {
        for (int i = 0; i < accessPoints.size(); i++) {
            if (accessPoints.get(i).id == Long.parseLong(accessPointId)) {
                _adapter.setItem(i, status);
                break;
            }
        }
    }
}
