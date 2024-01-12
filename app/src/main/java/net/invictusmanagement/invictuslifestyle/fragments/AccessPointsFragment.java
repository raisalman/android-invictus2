package net.invictusmanagement.invictuslifestyle.fragments;

import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.brivo.sdk.BrivoSDKInitializationException;
import com.brivo.sdk.access.BrivoSDKAccess;
import com.brivo.sdk.ble.BrivoBLEErrorCodes;
import com.brivo.sdk.enums.AccessPointCommunicationState;
import com.brivo.sdk.interfaces.IOnCommunicateWithAccessPointListener;
import com.brivo.sdk.localauthentication.BrivoSDKLocalAuthentication;
import com.brivo.sdk.model.BrivoError;
import com.brivo.sdk.model.BrivoResult;
import com.brivo.sdk.onair.interfaces.IOnRedeemPassListener;
import com.brivo.sdk.onair.interfaces.IOnRetrieveSDKLocallyStoredPassesListener;
import com.brivo.sdk.onair.model.BrivoOnairPass;
import com.brivo.sdk.onair.model.BrivoTokens;
import com.brivo.sdk.onair.repository.BrivoSDKOnair;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.AccessPointLocationAdapter;
import net.invictusmanagement.invictuslifestyle.adapters.AccessPointsAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.AccessPointsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.AccessPointsLocationListener;
import net.invictusmanagement.invictuslifestyle.interfaces.EmptyListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.AccessCodeResponse;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.AccessPointUpdate;
import net.invictusmanagement.invictuslifestyle.utils.BrivoSampleConstants;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;
import net.invictusmanagement.invictuslifestyle.widgets.NewWidgetActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.ResponseBody;

public class AccessPointsFragment extends Fragment implements IRefreshableFragment, EmptyListener,
        AccessPointsLocationListener {

    public static AccessPointsAdapter _adapter;
    private static TextView _tvAll;
    private AccessPointsListFragmentInteractionListener _listener;
    private RecyclerView _recyclerView;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private ConstraintLayout _clSwitch;
    private static TextView _tvFavourites;
    private static ImageView _switch;
    private static Boolean isFavourite = false;
    private SharedPreferences sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private TextView _feedback;
    private List<AccessPoint> accessPoints = new ArrayList<>();
    private Boolean isDataAvailable = false;
    private Boolean isRWTAccessPoint = false;
    private boolean isFirstTimeList = true;
    private String role = "";
    //Vendor specific change
    private AccessPointLocationAdapter _adapterLocation;
    private ImageView ivBack;
    private LinearLayout llLocation;
    private RelativeLayout rlBackAccess;
    private RecyclerView rvLocations;
    private boolean isGuestUser;


    public AccessPointsFragment() {
    }

    @SuppressWarnings("unused")
    public static AccessPointsFragment newInstance() {
        return new AccessPointsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void refreshView() {
        if (HomeFragment.isFavAccessPoints) {
            HomeFragment.isFavAccessPoints = false;
            isFavourite = true;
        } else {
            isFavourite = false;
        }

        if (_tvFavourites != null && _tvAll != null) {
            if (isFavourite) {
                _tvFavourites.setTypeface(Typeface.DEFAULT_BOLD);
                _tvFavourites.setTextColor(ContextCompat.getColor(TabbedActivity.tabbedActivity, R.color.colorPrimary));
            } else {
                _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
                _tvAll.setTextColor(ContextCompat.getColor(TabbedActivity.tabbedActivity, R.color.colorPrimary));
            }
            switchVisibility(isFavourite);
        }
    }

    private static void switchVisibility(Boolean isUnFavourite) {
        if (isUnFavourite) {
            _switch.setImageDrawable(ContextCompat.getDrawable(TabbedActivity.tabbedActivity, R.drawable.ic_active_switch));
            _tvFavourites.setTypeface(Typeface.DEFAULT_BOLD);
            _tvAll.setTypeface(Typeface.DEFAULT);

            _tvFavourites.setTextColor(ContextCompat.getColor(TabbedActivity.tabbedActivity, R.color.colorPrimary));
            _tvAll.setTextColor(ContextCompat.getColor(TabbedActivity.tabbedActivity, R.color.colorSwitchTextNotSelected));

            isFavourite = true;
        } else {
            _switch.setImageDrawable(ContextCompat.getDrawable(TabbedActivity.tabbedActivity, R.drawable.ic_deactive_switch));
            _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
            _tvFavourites.setTypeface(Typeface.DEFAULT);

            _tvAll.setTextColor(ContextCompat.getColor(TabbedActivity.tabbedActivity, R.color.colorPrimary));
            _tvFavourites.setTextColor(ContextCompat.getColor(TabbedActivity.tabbedActivity, R.color.colorSwitchTextNotSelected));

            isFavourite = false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accesspoints_list, container, false);
        initBrivoInstance();
        if (view instanceof SwipeRefreshLayout) {

            isRWTAccessPoint = sharedPreferences.getBoolean("isRWTAccessPoint", true);
            role = sharedPreferences.getString("userRole", "");
            isGuestUser = sharedPreferences.getBoolean("isGuestUser", false);
            if (role.equals(getString(R.string.role_vendor)) ||
                    role.equals(getString(R.string.role_facility))) {
                isGuestUser = true;
            }
            _feedback = view.findViewById(R.id.feedback);
            _tvAll = view.findViewById(R.id.tvAll);
            _tvFavourites = view.findViewById(R.id.tvFavourites);
            _clSwitch = view.findViewById(R.id.clSwitch);
            _switch = view.findViewById(R.id.favSwitch);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(false);
            _adapter = new AccessPointsAdapter(_context,
                    this, _listener, isRWTAccessPoint,
                    isGuestUser, this);
            _recyclerView.setAdapter(_adapter);

            llLocation = view.findViewById(R.id.llLocation);
            rlBackAccess = view.findViewById(R.id.llBackAccess);
            ivBack = view.findViewById(R.id.ivBack);
            rvLocations = view.findViewById(R.id.rvlocationList);
            rvLocations.setHasFixedSize(false);

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

            _switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchVisibility(!isFavourite);
                    if (isDataAvailable) {
                        _adapter.refresh(accessPoints, isFavourite);
                        _adapter.notifyDataSetChanged();
                    }
                }
            });

            if (role.equals(getString(R.string.role_vendor))) {
                llLocation.setVisibility(View.VISIBLE);
                rlBackAccess.setVisibility(View.GONE);
                _recyclerView.setVisibility(View.GONE);
            } else {
                llLocation.setVisibility(View.GONE);
                rlBackAccess.setVisibility(View.GONE);
            }

            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llLocation.setVisibility(View.VISIBLE);
                    rlBackAccess.setVisibility(View.GONE);
                    _recyclerView.setVisibility(View.GONE);
                }
            });
            refresh();
        }

        try {
            BrivoSDKLocalAuthentication.getInstance().init(getActivity(),
                    "2FA Required",
                    "Please authenticate with your device biometrics " +
                            "before unlocking the door",
                    "Cancel",
                    "");
        } catch (BrivoSDKInitializationException e) {
            e.printStackTrace();
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
                            BrivoSampleConstants.BRIVO_TOKEN = pass
                                    .getBrivoOnairPassCredentials().getTokens();
                            BrivoSampleConstants.PASS_ID = pass.getPassId();
                        }

                        @Override
                        public void onFailed(@NonNull BrivoError error) {
                            Log.d("redeempass", "onFailed: "+error.getMessage());
                        }
                    });
        } catch (BrivoSDKInitializationException e) {
            Log.d("redeemPass", "brivoRedeemPass: "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void initBrivoInstance() {
        try {
            BrivoSDKOnair.getInstance().retrieveSDKLocallyStoredPasses(new IOnRetrieveSDKLocallyStoredPassesListener() {
                @Override
                public void onSuccess(LinkedHashMap<String, BrivoOnairPass> passes) {
                    //Manage retrieved passes
                    if (passes!=null && !passes.isEmpty()) {
                        Log.e("BrivoSDKOnair", "passess >> " + passes);
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
                        } else if (BrivoSampleConstants.BRIVO_TOKEN != null) {
                            brivoRefreshPass();
                        }else{
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

    private void brivoRefreshPass() {
        BrivoTokens token = BrivoSampleConstants.BRIVO_TOKEN;
        try {
            if (BrivoSDKOnair.getInstance() != null && token != null) {
                BrivoSDKOnair.getInstance().refreshPass(token, new IOnRedeemPassListener() {
                    @Override
                    public void onSuccess(BrivoOnairPass pass) {
                        //Manage refreshed pass
                        if (pass != null) {
                            Log.e("BrivoSDKOnair > ", "refreshPass > " + pass.getPassId());
                            BrivoSampleConstants.PASS_ID = pass.getPassId();
                            BrivoSampleConstants.BRIVO_TOKEN = pass.getBrivoOnairPassCredentials().getTokens();
                        } else {
                            callAPIForGetAccessCode();
                        }
                    }

                    @Override
                    public void onFailed(BrivoError error) {
                        //Handle refresh pass error case
                        Log.e("BrivoSDKOnair > ", "refreshPass > " + error.getMessage());
                        callAPIForGetAccessCode();
                    }
                });
            }
        } catch (BrivoSDKInitializationException e) {
            //Handle BrivoSDK initialization exception
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
        refreshView();
    }

    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getAccessPoints(isGuestUser,
                new RestCallBack<List<AccessPoint>>() {
                    @Override
                    public void onResponse(List<AccessPoint> response) {
                        if (response != null) {
                            accessPoints = response;
                            accessPoints.sort((o1, o2) -> {
                                if (o1.getDisplayOrder() < o2.getDisplayOrder()) {// less than
                                    return -1;
                                } else {
                                    return 0;
                                }
                            });

                            _adapter.refresh(accessPoints, isFavourite);
                            _adapter.notifyDataSetChanged();

                            _recyclerView.setOnDragListener(_adapter.getDragInstance());

                            _swipeRefreshLayout.setRefreshing(false);
                            isDataAvailable = response.size() > 0;
                            setErrorView();

                            Intent intent2 = new Intent(TabbedActivity.tabbedActivity, NewWidgetActivity.class);
                            intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                            int[] ids = AppWidgetManager.getInstance(TabbedActivity.tabbedActivity)
                                    .getAppWidgetIds(new ComponentName(TabbedActivity.tabbedActivity,
                                            NewWidgetActivity.class));
                            intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                            TabbedActivity.tabbedActivity.sendBroadcast(intent2);

                            if (isFirstTimeList) {
                                isFirstTimeList = false;
                                stopDropEvent();
                            }

                            if (getContext() != null) {
                                if (role.equals(getContext().getString(R.string.role_vendor))) {

                                    _recyclerView.setVisibility(View.GONE);
                                    rlBackAccess.setVisibility(View.GONE);
                                    if (accessPoints.size() > 0) {
                                        List<AccessPoint> accessPointLocation = new ArrayList<>();
                                        for (int i = 0; i < accessPoints.size(); i++) {
                                            if (accessPointLocation.size() == 0) {
                                                accessPointLocation.add(accessPoints.get(i));
                                            } else {
                                                for (int j = 0; j < accessPointLocation.size(); j++) {
                                                    if (accessPointLocation.get(j).getLocationId() !=
                                                            accessPoints.get(i).getLocationId()) {
                                                        accessPointLocation.add(accessPoints.get(i));
                                                        break;
                                                    }
                                                }
                                            }
                                        }


                                        _adapterLocation = new AccessPointLocationAdapter(_context, accessPointLocation,
                                                AccessPointsFragment.this);

                                        rvLocations.setLayoutManager(new LinearLayoutManager(_context));
                                        rvLocations.addItemDecoration(new DividerItemDecoration(rvLocations.getContext(),
                                                DividerItemDecoration.VERTICAL));
                                        rvLocations.setAdapter(_adapterLocation);
                                        llLocation.setVisibility(View.VISIBLE);
                                        rvLocations.setVisibility(View.VISIBLE);
                                    } else {
                                        rvLocations.setVisibility(View.GONE);
                                        llLocation.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                        _swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        isDataAvailable = false;
                        _swipeRefreshLayout.setRefreshing(false);
                        _feedback.setVisibility(View.VISIBLE);
                        String currentString = wse.getServerMessage();
                        if (currentString.startsWith("301")) {
                            accessPoints.clear();
                            _adapter.notifyDataSetChanged();
                            String[] separated = currentString.split("301");
                            _feedback.setText(separated[1].trim());
                        } else {
                            Toast.makeText(getActivity(),
                                    getString(R.string.error_fetching_access_point),
                                    Toast.LENGTH_LONG).show();
                            _feedback.setText(getString(R.string.no_access_point_with_swipe_down_to_refresh));
                        }
                        setErrorView();
                    }
                }
        );
    }


    public void setErrorView() {
        if (!role.equals(_context.getString(R.string.role_vendor)))
            Utilities.showHide(_context, _recyclerView, _adapter.totalItemCount() > 0);
        if (!isGuestUser) {
            Utilities.showHide(_context, _clSwitch, (_adapter.totalItemCount() > 0));
        } else {
            Utilities.showHide(_context, _clSwitch, false);
        }
    }

    public void notifyData(int position, int status, AccessPoint accessPoint) {
        _adapter.setItem(position, status);
    }

    public void notifyData(int status, String accessPointId) {
        for (int i = 0; i < accessPoints.size(); i++) {
            if (accessPoints.get(i).id == Long.parseLong(accessPointId)) {
                _adapter.setItem(i, status);

//                if (status == 1)
//                    Toast.makeText(getContext(), accessPoints.get(i).getName() + " was successfully opened.",
//                            Toast.LENGTH_LONG).show();
//                else if (status == 2)
//                    Toast.makeText(getContext(), "Failed to open door " + accessPoints.get(i).getName(),
//                            Toast.LENGTH_LONG).show();

                break;
            }
        }
    }

    @Override
    public void setEmptyListTop(boolean visibility) {
    }

    @Override
    public void setEmptyListBottom(boolean visibility) {
        if (isGuestUser) {
            _recyclerView.setVisibility(visibility ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void stopDropEvent() {
        int displayOrder = -1;
        ArrayList<AccessPointUpdate> accessPointUpdates = new ArrayList<>();

        for (int i = 0; i < accessPoints.size(); i++) {
            displayOrder++;
            accessPoints.get(i).setDisplayOrder(displayOrder);

            AccessPointUpdate update = new AccessPointUpdate();
            update.setAccessPointId(accessPoints.get(i).id);
            update.id = accessPoints.get(i).getUserAccessPointId();
            update.setFavorite(accessPoints.get(i).getFavorite());
            update.setApplicationUserId(HomeFragment.userId);
            update.setOperator(accessPoints.get(i).getOperator());
            update.setDisplayOrder(displayOrder);
            accessPointUpdates.add(update);
        }
        callAPIUpdateDisplayOrder(accessPointUpdates);
    }

    private void callAPIUpdateDisplayOrder(ArrayList<AccessPointUpdate> accessPointUpdates) {
        WebService.getInstance().updateAccessPoints(accessPointUpdates, new RestEmptyCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                Intent intent2 = new Intent(TabbedActivity.tabbedActivity, NewWidgetActivity.class);
                intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = AppWidgetManager.getInstance(TabbedActivity.tabbedActivity)
                        .getAppWidgetIds(new ComponentName(TabbedActivity.tabbedActivity,
                                NewWidgetActivity.class));
                intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                TabbedActivity.tabbedActivity.sendBroadcast(intent2);
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    @Override
    public void onLocationSelected(AccessPoint item, int position) {
        List<AccessPoint> selectedAccessPoint = new ArrayList<>();
        for (int i = 0; i < accessPoints.size(); i++) {
            if (accessPoints.get(i).getLocationId() == item.getLocationId()) {
                selectedAccessPoint.add(accessPoints.get(i));
            }
        }

        _adapter.refresh(selectedAccessPoint, false);
        _adapter.notifyDataSetChanged();

        llLocation.setVisibility(View.GONE);
        rlBackAccess.setVisibility(View.VISIBLE);
        _recyclerView.setVisibility(View.VISIBLE);
    }
}
