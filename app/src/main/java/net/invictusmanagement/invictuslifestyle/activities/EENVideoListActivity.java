package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.EENVideoListAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.CheckCameraAccess;
import net.invictusmanagement.invictuslifestyle.models.EENDeviceList;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EENVideoListActivity extends AppCompatActivity implements IRefreshableFragment {

    private Context _context;
    private RecyclerView rvPeekList;
    private TextView tvNoDataFound;
    private EENVideoListAdapter adapter;
    private ArrayList<EENDeviceList> eenDeviceLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_een_video_list);

        initControls();
    }

    private void initControls() {
        _context = EENVideoListActivity.this;
        toolBar();
        initView();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        refresh();
//    }

    private void initView() {
        rvPeekList = findViewById(R.id.rvPeekList);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);

        refresh();
    }


    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Payment History");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void refresh() {
        ProgressDialog.showProgress(_context);
//        WebServiceEEN.getInstance().eenDeviceList(new RestCallBack<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONArray array = new JSONArray(response);
//                    for (int i = 0; i < array.length(); i++) {
//                        String object = array.get(i).toString();
//                        String[] objectArray = object.split(",");
//                        EENDeviceList deviceList = new EENDeviceList();
//                        deviceList.name = objectArray[2].replace("\"", "");
//                        deviceList.id = objectArray[1].replace("\"", "");
//
//                        eenDeviceLists.add(deviceList);
//                    }
//                    eenDeviceLists.sort((o1, o2) -> o1.name.compareToIgnoreCase(o2.name));
//                    ProgressDialog.dismissProgress();
//                    setPaymentData();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(WSException wse) {
//                ProgressDialog.dismissProgress();
//                tvNoDataFound.setVisibility(View.VISIBLE);
//                rvPeekList.setVisibility(View.GONE);
//            }
//        });
        ProgressDialog.showProgress(this);
        WebService.getInstance().getCameraList(new RestCallBack<List<EENDeviceList>>() {
            @Override
            public void onResponse(List<EENDeviceList> response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    eenDeviceLists = (ArrayList<EENDeviceList>) response;
                    setPaymentData();
                } else {

                    tvNoDataFound.setVisibility(View.VISIBLE);
                    rvPeekList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                tvNoDataFound.setVisibility(View.VISIBLE);
                rvPeekList.setVisibility(View.GONE);
            }
        });
    }

    private void setPaymentData() {
        adapter = new EENVideoListAdapter(_context, eenDeviceLists);

        rvPeekList.setHasFixedSize(true);
        rvPeekList.setLayoutManager(new LinearLayoutManager(this));
        rvPeekList.addItemDecoration(new DividerItemDecoration(rvPeekList.getContext(),
                DividerItemDecoration.VERTICAL));
        rvPeekList.setAdapter(adapter);

        if (eenDeviceLists.size() > 0) {
            tvNoDataFound.setVisibility(View.GONE);
            rvPeekList.setVisibility(View.VISIBLE);
        } else {
            tvNoDataFound.setVisibility(View.VISIBLE);
            rvPeekList.setVisibility(View.GONE);
        }
    }

    public void onClick(EENDeviceList item) {
        //TODO Enable it
//        checkForCameraAccess(item);
        Intent intent = new Intent(EENVideoListActivity.this,
                EENVideoActivity.class);
        intent.putExtra("ID", item.devieceId);
        intent.putExtra("NAME", item.deviceName);
        intent.putExtra("CAMERA_OPERATOR", item.cameraOperator);
        startActivity(intent);
    }

    private void checkForCameraAccess(EENDeviceList item) {
        //Operator: ("EEN",value = "1"), ("AVA",value ="2")
        CheckCameraAccess model = new CheckCameraAccess(item.devieceId, String.valueOf(item.cameraOperator));
        WebService.getInstance().checkCameraAccess(model, new RestCallBack<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("error")) {
                            new AlertDialog
                                    .Builder(EENVideoListActivity.this)
                                    .setMessage(object.getString("error"))
                                    .setTitle("No Access")
                                    .setPositiveButton(getString(android.R.string.ok), (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (response.equalsIgnoreCase("true")) {
                            Intent intent = new Intent(EENVideoListActivity.this,
                                    EENVideoActivity.class);
                            intent.putExtra("ID", item.devieceId);
                            intent.putExtra("NAME", item.deviceName);
                            intent.putExtra("CAMERA_OPERATOR", item.cameraOperator);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }
}