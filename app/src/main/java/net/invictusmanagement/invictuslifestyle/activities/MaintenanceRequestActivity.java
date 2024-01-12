package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.MRSImagesVideosDisplayAdapter;
import net.invictusmanagement.invictuslifestyle.adapters.MaintenanceRequestsAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.BottomMRSDialog;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.MaintenanceRequestsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnBottomDialogButtonClick;
import net.invictusmanagement.invictuslifestyle.interfaces.ShowSingleMRSItem;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequest;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequestResponse;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class MaintenanceRequestActivity extends AppCompatActivity implements IRefreshableFragment,
        MaintenanceRequestsListFragmentInteractionListener, ShowSingleMRSItem,
        SetOnBottomDialogButtonClick {

    private RecyclerView _recyclerView;
    private RelativeLayout rlCreate;
    private MaintenanceRequestsAdapter _adapter;
    private List<MaintenanceRequestResponse> maintenanceList = new ArrayList<>();
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private TextView _feedback, tvFilterType;
    private ConstraintLayout _clSwitch;
    private BottomMRSDialog dialog = null;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maitanance_request);

        initControls();
    }

    private void initControls() {
        _context = MaintenanceRequestActivity.this;
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        role = sharedPreferences.getString("userRole", "");
        toolBar();
        initView();
        refresh();
    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initView() {

        MobileDataProvider.isMRSEnable = false;
        tvFilterType = findViewById(R.id.tvFilterType);
        ImageView imgFilter = findViewById(R.id.imgFilter);
        _clSwitch = findViewById(R.id.clSwitch);
        dialog = new BottomMRSDialog(this, role);
        tvFilterType.setText("All");


        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    if (!dialog.isHidden()) {
                        dialog.show(getSupportFragmentManager(), "dialogFilter");
                        dialog.setCancelable(false);
                    } else {
                        dialog.dismiss();
                    }
                }
            }
        });

        _feedback = findViewById(R.id.feedback);
        _recyclerView = findViewById(R.id.list);
        _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
        _adapter = new MaintenanceRequestsAdapter(_context, this);
        _recyclerView.setAdapter(_adapter);
        rlCreate = findViewById(R.id.rlCreate);
        rlCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.addHaptic(v);
                startActivity(new Intent(MaintenanceRequestActivity.this, NewMaintenanceRequestActivity.class));
            }
        });
        if (role.equals(getString(R.string.role_vendor)) || role.equals(getString(R.string.role_facility))) {
            rlCreate.setVisibility(View.GONE);
        } else {
            rlCreate.setVisibility(View.VISIBLE);
        }

        _swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.MainRequests.value());
                refresh();
            }
        });
    }


    @Override
    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        tvFilterType.setText("All");
        HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.MainRequests.value());
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    maintenanceList = MobileDataProvider.getInstance().getMaintenanceRequests();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                _adapter.refresh(maintenanceList);
                _adapter.notifyDataSetChanged();
                _swipeRefreshLayout.setRefreshing(false);
//                Utilities.showHide(MaintenanceRequestActivity.this, rlCreate, MobileDataProvider.isMRSEnable);
                if (!success) {
                    _feedback.setText(MobileDataProvider.mrsString);
                    Utilities.showHide(_context, _feedback, true);
                    Utilities.showHide(_context, _recyclerView, false);
                } else {
                    _feedback.setText("No maintenance requests available.\nSwipe down to refresh.");
                    Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
                    Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
                }
                /*Toast.makeText(getActivity(), "Unable to refresh maintenance requests. Please try again later.", Toast.LENGTH_LONG).show();*/
            }
        }.execute();
    }

    public void callAPIForDelete(long id) {
        WebService.getInstance().deleteMaintenanceReq(id, new RestEmptyCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                if (response != null) {
                    Toast.makeText(_context, "Maintenance request deleted successfully", Toast.LENGTH_SHORT).show();
                    refresh();
                }
            }

            @Override
            public void onFailure(WSException wse) {
                Toast.makeText(_context, "Maintenance Request deletion failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }


    @Override
    public void onListFragmentInteraction(MaintenanceRequestResponse item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_maintenance_request, null);
        builder.setView(view)
                .setTitle(item.getTitle());

        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(item.createdUtc));
        ((TextView) view.findViewById(R.id.status)).setText(item.getStatus().toString());
        ((TextView) view.findViewById(R.id.description)).setText(item.getDescription());

        if (item.getNeedPermission()) {
            ((TextView) view.findViewById(R.id.permission)).setText("No, follow up");
        } else {
            ((TextView) view.findViewById(R.id.permission)).setText("Ok to enter");
        }


        view.findViewById(R.id.closedUtcLabel).setVisibility(item.getStatus()
                == MaintenanceRequest.Status.Closed ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.closedUtc).setVisibility(item.getStatus()
                == MaintenanceRequest.Status.Closed ? View.VISIBLE : View.GONE);
        if (item.getStatus() == MaintenanceRequest.Status.Closed)
            ((TextView) view.findViewById(R.id.closedUtc))
                    .setText(formatter.format(item.getClosedUtc()));

        //recyclerview set
        ArrayList<MaintenanceRequesFiles> mrsItemsArrayList = new ArrayList();
        ArrayList<MaintenanceRequesFiles> mrsAfterItemsArrayList = new ArrayList();

        for (int i = 0; i < item.getMaintenanceRequestFiles().size(); i++) {

            if (item.getMaintenanceRequestFiles().get(i).isBeforeSolve) {
                mrsItemsArrayList.add(item.getMaintenanceRequestFiles().get(i));
            } else {
                mrsAfterItemsArrayList.add(item.getMaintenanceRequestFiles().get(i));
            }
        }

        if (mrsItemsArrayList.size() > 0) {
            view.findViewById(R.id.tvUploadImaged).setVisibility(View.VISIBLE);
            view.findViewById(R.id.nsImages).setVisibility(View.VISIBLE);
            MRSImagesVideosDisplayAdapter mrsImagesVideosDisplayAdapter =
                    new MRSImagesVideosDisplayAdapter(_context,
                            mrsItemsArrayList, this);
            ((RecyclerView) view.findViewById(R.id.rvPriorImages)).setAdapter(mrsImagesVideosDisplayAdapter);
            mrsImagesVideosDisplayAdapter.refresh(mrsItemsArrayList);
        }

        if (mrsAfterItemsArrayList.size() > 0) {
            view.findViewById(R.id.tvUploadImageAfter).setVisibility(View.VISIBLE);
            view.findViewById(R.id.nsImagesAfter).setVisibility(View.VISIBLE);
            MRSImagesVideosDisplayAdapter mrsImagesVideosDisplayAdapter =
                    new MRSImagesVideosDisplayAdapter(_context,
                            mrsAfterItemsArrayList, this);
            ((RecyclerView) view.findViewById(R.id.rvAfterImages)).setAdapter(mrsImagesVideosDisplayAdapter);
            mrsImagesVideosDisplayAdapter.refresh(mrsAfterItemsArrayList);
        }


        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }

    @Override
    public void showVideoImage(int position, MaintenanceRequesFiles item) {
        Intent i = new Intent(_context, SingleVideoImageActivity.class);
        i.putExtra("files", new Gson().toJson(item));
        startActivity(i);
    }

    @Override
    public void setFilter(int number) {
        if (number == 1) {
            //all
            tvFilterType.setText("All");
            List<MaintenanceRequestResponse> filterList = new ArrayList<>();
            filterList.addAll(maintenanceList);

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();

            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No maintenance requests available.\nSwipe down to refresh.");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }
        } else if (number == 2) {
            //mine
            tvFilterType.setText("Requested");

            List<MaintenanceRequestResponse> filterList = new ArrayList<>();
            for (int i = 0; i < maintenanceList.size(); i++) {
                if (maintenanceList.get(i).getStatus() == MaintenanceRequest.Status.Requested) {
                    filterList.add(maintenanceList.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();


            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No pending maintenance request available");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }

        } else if (number == 3) {
            //fav
            tvFilterType.setText("Active");

            List<MaintenanceRequestResponse> filterList = new ArrayList<>();
            for (int i = 0; i < maintenanceList.size(); i++) {
                if (maintenanceList.get(i).getStatus() == MaintenanceRequest.Status.Active) {
                    filterList.add(maintenanceList.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();


            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No active maintenance request available");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }

        } else if (number == 4) {
            //fav
            tvFilterType.setText("Close");

            List<MaintenanceRequestResponse> filterList = new ArrayList<>();
            for (int i = 0; i < maintenanceList.size(); i++) {
                if (maintenanceList.get(i).getStatus() == MaintenanceRequest.Status.Closed) {
                    filterList.add(maintenanceList.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();

            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No closed maintenance request available");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }
        } else if (number == 5) {
            //fav
            tvFilterType.setText("Requested To Close");

            List<MaintenanceRequestResponse> filterList = new ArrayList<>();
            for (int i = 0; i < maintenanceList.size(); i++) {
                if (maintenanceList.get(i).getStatus() == MaintenanceRequest.Status.RequestedToClose) {
                    filterList.add(maintenanceList.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();

            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No requested to close maintenance request available");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }
        } else if (number == 6) {
            //fav
            tvFilterType.setText("Not Solved");

            List<MaintenanceRequestResponse> filterList = new ArrayList<>();
            for (int i = 0; i < maintenanceList.size(); i++) {
                if (maintenanceList.get(i).getStatus() == MaintenanceRequest.Status.NotSolve) {
                    filterList.add(maintenanceList.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();

            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No Not Solved maintenance request available");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }
        }
    }
}