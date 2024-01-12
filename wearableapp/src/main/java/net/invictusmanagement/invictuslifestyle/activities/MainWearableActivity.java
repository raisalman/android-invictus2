package net.invictusmanagement.invictuslifestyle.activities;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.brivo.sdk.BrivoSDKInitializationException;
import com.brivo.sdk.access.BrivoSDKAccess;
import com.brivo.sdk.ble.BrivoBLEErrorCodes;
import com.brivo.sdk.enums.AccessPointCommunicationState;
import com.brivo.sdk.interfaces.IOnCommunicateWithAccessPointListener;
import com.brivo.sdk.model.BrivoError;
import com.brivo.sdk.model.BrivoResult;
import com.brivo.sdk.onair.interfaces.IOnRedeemPassListener;
import com.brivo.sdk.onair.interfaces.IOnRetrieveSDKLocallyStoredPassesListener;
import com.brivo.sdk.onair.model.BrivoOnairPass;
import com.brivo.sdk.onair.model.BrivoTokens;
import com.brivo.sdk.onair.repository.BrivoSDKOnair;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.AccessPointsAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.databinding.ActivityMainBinding;
import net.invictusmanagement.invictuslifestyle.enum_utils.AccessPointOperator;
import net.invictusmanagement.invictuslifestyle.enum_utils.DeviceType;
import net.invictusmanagement.invictuslifestyle.interfaces.AccessPointsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.AccessCodeResponse;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.AccessPointAudit;
import net.invictusmanagement.invictuslifestyle.models.OpenAccessPoint;
import net.invictusmanagement.invictuslifestyle.utils.BrivoSampleConstants;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.RestClient;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.ResponseBody;

public class MainWearableActivity extends Activity implements
        AccessPointsListFragmentInteractionListener,
        DataClient.OnDataChangedListener, MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {

    private TextView mTextView;
    private LinearLayout llLogin, llAccessPoints;
    private ActivityMainBinding binding;
    private WearableRecyclerView wearableRecyclerView;
    private AccessPointsAdapter adapter;
    private List<AccessPoint> accessPointList;
    private String datapath = "/data_path";
    private static final String TAG = "WearMainActivity";
    private SharedPreferences sharedPreferences;
    private int _accessPointPosition;
    private AccessPoint _accessPoint;
    private boolean isAccessPointFailed = false;
    private static Boolean isFirstTime = true;
    public static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MainWearableActivity.this);

        mTextView = binding.txtAccessPoint;
        llLogin = binding.llLogin;
        llAccessPoints = binding.llAccessPoint;
        wearableRecyclerView = binding.recyclerView;
        wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this));

        initBrivoInstance();
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            String cookies = sharedPreferences.getString("authenticationCookie", null);
            if (cookies != null) {
                binding.llLogin.setVisibility(View.GONE);
                binding.llAccessPoint.setVisibility(View.VISIBLE);
                RestClient.getInstance().setAuthenticationCookie(cookies);
                callWebServiceForGetAccessPoints();

            } else {
                binding.llLogin.setVisibility(View.VISIBLE);
                binding.llAccessPoint.setVisibility(View.GONE);
            }
        }
    }

    private void callWebServiceForGetAccessPoints() {
        ProgressDialog.showProgress(this);
        WebService.getInstance().getAccessPoints(new RestCallBack<List<AccessPoint>>() {
            @Override
            public void onResponse(List<AccessPoint> response) {
                if (response != null) {
                    ProgressDialog.dismissProgress();
                    accessPointList = response;
                    setAdapter();
                } else {
                    ProgressDialog.dismissProgress();
                }
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
            }
        });
    }

    private void setAdapter() {
        adapter = new AccessPointsAdapter(this, accessPointList, this);
        wearableRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged: " + dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (datapath.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String message = dataMapItem.getDataMap().getString("message");
                    String cookies = dataMapItem.getDataMap().getString("cookies");
                    String email = dataMapItem.getDataMap().getString("email");
                    String activationCode = dataMapItem.getDataMap().getString("activationCode");
                    long userId = dataMapItem.getDataMap().getLong("userId");

                    String unlockingMessage = dataMapItem.getDataMap().getString("unlock");
                    if (unlockingMessage != null) {
                        int position = dataMapItem.getDataMap().getInt("itemPosition");
                        boolean status = dataMapItem.getDataMap().getBoolean("status");
                        String statusMessage = dataMapItem.getDataMap().getString("statusMessage");
                        Log.d(TAG, "onDataChanged: unlockingMessage " + unlockingMessage + " >> " + status);
                        Log.d(TAG, "onDataChanged: statusMessage " + statusMessage);
                        if (status) {
                            adapter.setItem(position, 1);
                            Toast.makeText(this, " was successfully opened.",
                                    Toast.LENGTH_LONG).show();
                        } else if (statusMessage != null) {
                            adapter.setItem(position, 2);
                            Toast.makeText(this, statusMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else if (cookies != null) {
                        llLogin.setVisibility(View.GONE);
                        llAccessPoints.setVisibility(View.VISIBLE);
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                        sharedPreferences.edit().putString("authenticationCookie",
                                cookies).apply();
                        sharedPreferences.edit().putString("email", email).apply();
                        sharedPreferences.edit().putLong("userId", userId).apply();
                        RestClient.getInstance().setAuthenticationCookie(cookies);
                        callWebServiceForGetAccessPoints();
                    }
                } else {
                    Log.e(TAG, "Unrecognized path: " + path);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.v(TAG, "Data deleted : " + event.getDataItem().toString());
            } else {
                Log.e(TAG, "Unknown data event Type = " + event.getType());
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    public boolean isBluetoothEnabled() {
        //TODO uncomment while release
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
//        return true;
    }

    @Override
    public void onSlideUnlockTapped(int position, AccessPoint item) {
        if (item.getOperator() == Utilities.OPERATOR_BRIVO) {
            unlockBrivoDoor(item, position);
        } else {
            unlockAccessPoint(item, position);
        }
    }


    private void unlockFailure(AccessPoint item, int position) {
        Toast.makeText(this, "Failed to open door " + item.getName(),
                Toast.LENGTH_LONG).show();
        adapter.setItem(position, 2);
        callAPIForAudit(item, false);
    }

    private void unlockSuccessMethod(AccessPoint item, int position) {
        Toast.makeText(this, item.getName() + " was successfully opened.",
                Toast.LENGTH_LONG).show();
        adapter.setItem(position, 1);
        callAPIForAudit(item, true);
    }

    private void waitForSlowConnection(final CancellationSignal cancellationSignal) {
        final Handler handler = new Handler();
        handler.postDelayed(cancellationSignal::cancel, 30000);
    }

    private void initBrivoInstance() {
        try {
            BrivoSDKOnair.getInstance().retrieveSDKLocallyStoredPasses(new IOnRetrieveSDKLocallyStoredPassesListener() {
                @Override
                public void onSuccess(LinkedHashMap<String, BrivoOnairPass> passes) {
                    //Manage retrieved passes
                    if (passes != null) {
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


    private void unlockBrivoDoor(AccessPoint item, int position) {
        _accessPoint = item;
        _accessPointPosition = position;

        if (!isBluetoothEnabled()) {
            showDialogToEnableBluetooth();
        } else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showDialogToEnableLocation();
        } else {
            try {
                String accessPointId = String.valueOf(item.id);
                String passId = BrivoSampleConstants.PASS_ID;
                CancellationSignal signal = new CancellationSignal();
                waitForSlowConnection(signal);

                BrivoSDKAccess.getInstance().unlockAccessPoint(passId, accessPointId, signal, new IOnCommunicateWithAccessPointListener() {
                    @Override
                    public void onResult(@NonNull BrivoResult result) {
                        if (result.getCommunicationState() ==
                                AccessPointCommunicationState.SUCCESS) {
                            isAccessPointFailed = false;
                            unlockSuccessMethod(item, position);
                        } else if (result.getCommunicationState() ==
                                AccessPointCommunicationState.FAILED) {
                            if (result.getError() != null) {
                                if (result.getError().getCode() ==
                                        BrivoBLEErrorCodes.BLE_LOCATION_DISABLED_ON_DEVICE) {
                                    Toast.makeText(MainWearableActivity.this,
                                            "Location is disable on you device, Please enable it.",
                                            Toast.LENGTH_LONG).show();
                                }
                                Log.e("Brivo Error >> ", result.getError().getCode()
                                        + " >> " + result.getError().getMessage());
                                if (!isAccessPointFailed &&
                                        (result.getError().getCode() == 403 || result.getError().getCode() == -1003
                                                || result.getError().getCode() == -1004)) {
                                    isAccessPointFailed = true;
                                    brivoRefreshPass();
                                } else {
                                    isAccessPointFailed = false;
                                    unlockFailure(item, position);
                                }
                            } else {
                                isAccessPointFailed = false;
                                unlockFailure(item, position);
                            }
                        }
                    }
                });
            } catch (BrivoSDKInitializationException e) {
                e.printStackTrace();
                unlockFailure(item, position);
            }
        }
    }

    private void callAPIForAudit(AccessPoint item, boolean isValid) {
        AccessPointAudit model = new AccessPointAudit();
        model.setAccessPointId(item.id);

        model.setValid(isValid);
        model.setEntryName(item.getName());
        WebService.getInstance().postAccessPointAudit(model,
                new RestEmptyCallBack<ResponseBody>() {
                    @Override
                    public void onResponse(ResponseBody response) {

                    }

                    @Override
                    public void onFailure(WSException wse) {
                    }
                });
    }


    private void showDialogToEnableBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Bluetooth");
        builder.setMessage("Please enable Bluetooth.");
        builder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.enable();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unlockBrivoDoor(_accessPoint, _accessPointPosition);
                    }
                }, 2000);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void showDialogToEnableLocation() {
        Dialog dialog = new Dialog(this);
        View myLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        TextView positiveButton = myLayout.findViewById(R.id.btn_settings);
        positiveButton.setOnClickListener(
                v -> {
                    /* Your action on positive button clicked. */
                    dialog.cancel();
                    navigateToPermissionSettings();
                }
        );

        TextView negativeButton = myLayout.findViewById(R.id.btn_cancel);
        negativeButton.setOnClickListener(
                v -> {
                    /* Your action on negative button clicked. */
                    dialog.cancel();
                }
        );

        dialog.setContentView(myLayout);
        dialog.show();
    }

    private void navigateToPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void brivoRefreshPass() {
        BrivoTokens token = BrivoSampleConstants.BRIVO_TOKEN;
        if (token != null) {
            try {
                BrivoSDKOnair.getInstance().refreshPass(token, new IOnRedeemPassListener() {
                    @Override
                    public void onSuccess(BrivoOnairPass pass) {
                        //Manage refreshed pass
                        if (pass != null) {
                            Log.e("BrivoSDKOnair > ", "refreshPass > " + pass.getPassId());
                            BrivoSampleConstants.PASS_ID = pass.getPassId();
                            BrivoSampleConstants.BRIVO_TOKEN = pass.getBrivoOnairPassCredentials().getTokens();
                            unlockBrivoDoor(_accessPoint, _accessPointPosition);
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
            } catch (BrivoSDKInitializationException e) {
                //Handle BrivoSDK initialization exception
                callAPIForGetAccessCode();
            }
        } else {
            callAPIForGetAccessCode();
        }
    }

    private void callAPIForGetAccessCode() {
        ProgressDialog.showProgress(MainWearableActivity.this);
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
                            unlockBrivoDoor(_accessPoint, _accessPointPosition);
                        }

                        @Override
                        public void onFailed(BrivoError error) {
                            //Handle redeem pass error case
                            unlockFailure(_accessPoint, _accessPointPosition);
                        }
                    });
        } catch (BrivoSDKInitializationException e) {
            unlockFailure(_accessPoint, _accessPointPosition);
        }
    }

    private void unlockAccessPoint(AccessPoint item, int position) {
        OpenAccessPoint model = new OpenAccessPoint();
        model.setId(item.id);
        model.setSilent(true);
        model.setVideoAccess(false);
        model.setDeviceType(DeviceType.Watch.value());
        model.setEntryName(item.getName());
        switch (item.getOperator()) {
            case Utilities.OPERATOR_OPENPATH:
                model.setOperator(AccessPointOperator.OpenPath.value());
                break;
            case Utilities.OPERATOR_BRIVO:
                model.setOperator(AccessPointOperator.Brivo.value());
                break;
            case Utilities.OPERATOR_PDK:
                model.setOperator(AccessPointOperator.PDK.value());
                break;
        }
        WebService.getInstance().openAccessPoints(model,
                new RestEmptyCallBack<ResponseBody>() {
                    @Override
                    public void onResponse(ResponseBody response) {
                        if (response != null) {
                            unlockSuccessMethod(item, position);
                            Toast.makeText(MainWearableActivity.this, item.getName() + " was successfully opened.", Toast.LENGTH_LONG).show();
                        } else {
                            unlockFailure(item, position);
                            Toast.makeText(MainWearableActivity.this, "Opening the " + item.getName() + " access point failed.  Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        unlockFailure(item, position);
                        Toast.makeText(MainWearableActivity.this, "Opening the " + item.getName() + " access point failed.  Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Wearable.getDataClient(this).addListener(this);
            Wearable.getCapabilityClient(this).addListener(this,
                    Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
            Wearable.getMessageClient(this).addListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Wearable.getDataClient(this).removeListener(this);
            Wearable.getMessageClient(this).removeListener(this);
            Wearable.getCapabilityClient(this).removeListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}