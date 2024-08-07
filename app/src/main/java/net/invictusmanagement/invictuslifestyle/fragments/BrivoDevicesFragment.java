package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.BrivoDevicesAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.AddBrivoUserDialog;
import net.invictusmanagement.invictuslifestyle.customviews.AddVendorDialog;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.customviews.ThermostatSettingDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.BrivoDevicesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddBrivoUserDialogClick;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddThermostatSettingDialogClick;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddVendorDialogClick;
import net.invictusmanagement.invictuslifestyle.models.AddBrivoSmartHomeUser;
import net.invictusmanagement.invictuslifestyle.models.BrivoDeviceData;
import net.invictusmanagement.invictuslifestyle.models.LoginBrivoSmartHomeUser;
import net.invictusmanagement.invictuslifestyle.models.ResponseListBrivoSmartHome;
import net.invictusmanagement.invictuslifestyle.models.ResponseLoginBrivoSmartHome;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;
import net.invictusmanagement.invictuslifestyle.webservice.WebServiceBrivoSmarthHome;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;

public class BrivoDevicesFragment extends Fragment implements SetOnAddBrivoUserDialogClick, SetOnAddThermostatSettingDialogClick {

    public static BrivoDevicesListFragmentInteractionListener _listener;
    public static RecyclerView _recyclerView;
    public static BrivoDevicesAdapter _adapter;
    public static SwipeRefreshLayout _swipeRefreshLayout;
    public static Context _context;
    public static TextView _feedback;
    SharedPreferences sharedPreferences;
    private static BrivoDevicesFragment instance;

    private AddBrivoUserDialog brivoUserDialog = null;
    private ThermostatSettingDialog thermostatSettingDialog = null;
    private boolean _isVisible = false;

    private String brivoSmartHomeLoginToken;

    public BrivoDevicesFragment() {
    }

    @SuppressWarnings("unused")
    public static BrivoDevicesFragment newInstance() {
        if (instance != null) return instance;
        return new BrivoDevicesFragment();
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

        View view = inflater.inflate(R.layout.fragment_brivo_devices, container, false);
        instance = this;

        Context context = getContext();
        if (context != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            Log.e("MyFragment", "Context is null, cannot initialize SharedPreferences.");
        }
        if (view instanceof SwipeRefreshLayout) {


            _feedback = view.findViewById(R.id.feedback);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
//            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context,10));
            _adapter = new BrivoDevicesAdapter(this, _listener, _context);
            _recyclerView.setAdapter(_adapter);

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh(true);
                }
            });
        }
//        initViews();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (_adapter.getDataSource().isEmpty()) {
            initViews();
        }

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (brivoUserDialog != null && !brivoUserDialog.isHidden()) {
//            brivoUserDialog.dismiss();
//        }
//    }

    public void initViews() {
        brivoUserDialog = new AddBrivoUserDialog(this);
        thermostatSettingDialog = new ThermostatSettingDialog(this);
        if (sharedPreferences.getString("bshUserName", "").isEmpty() && sharedPreferences.getString("bshPassword", "").isEmpty()) {
            if (brivoUserDialog != null) {
                if (!brivoUserDialog.isHidden()) {
                    brivoUserDialog.show(getActivity().getSupportFragmentManager(), "addBrivoUserDialog");
                    brivoUserDialog.setCancelable(false);
                } else {
                    brivoUserDialog.dismiss();
                }

            }

        } else {
            refresh(true);
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
        if (_context instanceof BrivoDevicesListFragmentInteractionListener) {
            _listener = (BrivoDevicesListFragmentInteractionListener) _context;
        } else {
            throw new RuntimeException(_context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    public void refresh(Boolean isProgress) {
        if (_swipeRefreshLayout == null || _adapter == null) return;


        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (isProgress) _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    callAPIForLogin();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (!success)
                    Toast.makeText(_context, "Unable to refresh brivo devices. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }


    private void callAPIForLogin() {
        LoginBrivoSmartHomeUser loginBrivoSmartHomeUser = new LoginBrivoSmartHomeUser();
        loginBrivoSmartHomeUser.username = sharedPreferences.getString("bshUserName", "");
        loginBrivoSmartHomeUser.password = sharedPreferences.getString("bshPassword", "");
//        loginBrivoSmartHomeUser.password = "kjskh";
        WebServiceBrivoSmarthHome.getInstance().loginBrivoSmartHome(loginBrivoSmartHomeUser, new RestCallBack<ResponseLoginBrivoSmartHome>() {
            @Override
            public void onResponse(ResponseLoginBrivoSmartHome response) {
                if (response != null) {
                    sharedPreferences.edit().putString("bshUserName", loginBrivoSmartHomeUser.username).apply();
                    sharedPreferences.edit().putString("bshPassword", loginBrivoSmartHomeUser.password).apply();
                    brivoSmartHomeLoginToken = response.token;
                    callAPIForGetDeviceList(response.token);
                } else
                    Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(WSException wse) {
                if (brivoUserDialog != null) {
                    //added getActivity null check here
                    if (!(brivoUserDialog.isHidden())) {
                        if(getActivity() != null) {
                            brivoUserDialog.show(getActivity().getSupportFragmentManager(), "addBrivoUserDialog");
                            brivoUserDialog.setCancelable(false);
                        }
                    } else {
                        brivoUserDialog.dismiss();
                    }

                }

                _swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), wse.getServerMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callAPIForGetDeviceList(String token) {
        WebServiceBrivoSmarthHome.getInstance().getBrivoSmartHomeDevices(token, new RestCallBack<ResponseListBrivoSmartHome>() {
            @Override
            public void onResponse(ResponseListBrivoSmartHome response) {
                if (response != null) {
                    List<BrivoDeviceData> brivoDeviceDataList = response.getResults();
                    List<BrivoDeviceData> filteredList = brivoDeviceDataList.stream()
                            .filter(brivoDeviceData -> !brivoDeviceData.getId().contains("~brivo"))
                            .collect(Collectors.toList());

                    _adapter.refresh(filteredList);
                    _adapter.notifyDataSetChanged();
                    _swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
                    _swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(WSException wse) {
                _swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onItemClick(int position, BrivoDeviceData brivoDeviceData) {
        if (brivoDeviceData.getType().equals("lock") || brivoDeviceData.getType().equals("garage_door")) {
            if (brivoDeviceData.getState().equals("secured")) {
                unlockSmartHomeAPI(brivoDeviceData, position);
            } else {
                lockSmartHomeAPI(brivoDeviceData, position);
            }
        } else if (brivoDeviceData.getType().equals("switch")) {
            if (brivoDeviceData.getState().equals("on")) {
                turnOffSmartHomeSwitch(brivoDeviceData, position);
            } else {
                turnOnSmartHomeSwitch(brivoDeviceData, position);
            }
        } else if (brivoDeviceData.getType().equals("thermostat")) {
            if (thermostatSettingDialog != null) {
                if (!thermostatSettingDialog.isHidden()) {
                    thermostatSettingDialog.setBrivoDeviceData(brivoDeviceData, brivoSmartHomeLoginToken);
                    thermostatSettingDialog.show(getActivity().getSupportFragmentManager(), "addBrivoUserDialog");
                    thermostatSettingDialog.setCancelable(false);
                } else {
                    thermostatSettingDialog.dismiss();
                }

            }

        } else {
            Toast.makeText(_context, brivoDeviceData.getName(), Toast.LENGTH_SHORT).show();
        }
    }


    private void lockSmartHomeAPI(BrivoDeviceData data, int position) {
        ProgressDialog.showProgress(requireContext());
        WebServiceBrivoSmarthHome.getInstance().lockSmartHomeDoor(data.getId(), brivoSmartHomeLoginToken, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    data.setState("secured");
                    _adapter.refreshItem(data, position);
                    _adapter.notifyItemChanged(position);
                } else
                    Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void unlockSmartHomeAPI(BrivoDeviceData data, int position) {
        ProgressDialog.showProgress(requireContext());
        WebServiceBrivoSmarthHome.getInstance().unlockSmartHomeDoor(data.getId(), brivoSmartHomeLoginToken, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    data.setState("unsecured");
                    _adapter.refreshItem(data, position);
                    _adapter.notifyItemChanged(position);
                } else
                    Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
//                Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), wse.getServerMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void turnOffSmartHomeSwitch(BrivoDeviceData data, int position) {
        ProgressDialog.showProgress(requireContext());
        WebServiceBrivoSmarthHome.getInstance().turnOffSmartHomeSwitch(data.getId(), brivoSmartHomeLoginToken, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    data.setState("off");
                    _adapter.refreshItem(data, position);
                    _adapter.notifyItemChanged(position);
                } else
                    Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void turnOnSmartHomeSwitch(BrivoDeviceData data, int position) {
        ProgressDialog.showProgress(requireContext());
        WebServiceBrivoSmarthHome.getInstance().turnOnSmartHomeSwitch(data.getId(), brivoSmartHomeLoginToken, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    data.setState("on");
                    _adapter.refreshItem(data, position);
                    _adapter.notifyItemChanged(position);
                } else
                    Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onAddThermostatSettingClicked() {
        Toast.makeText(_context, "Data saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddBrivoUserClicked(String bshUsername, String bshPassword) {
        sharedPreferences.edit().putString("bshUserName", bshUsername).apply();
        sharedPreferences.edit().putString("bshPassword", bshPassword).apply();
        refresh(true);
    }
}
