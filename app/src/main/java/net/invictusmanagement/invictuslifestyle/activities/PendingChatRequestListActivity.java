package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.PendingChatRequestListAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.ChatInitiated;
import net.invictusmanagement.invictuslifestyle.models.ChatTopic;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

public class PendingChatRequestListActivity extends AppCompatActivity implements IRefreshableFragment {

    private Context _context;
    private RecyclerView rvList;
    private TextView tvNoDataFound;
    private PendingChatRequestListAdapter adapter;
    private ArrayList<ChatTopic> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_chat_list);

        initControls();
    }

    private void initControls() {
        _context = PendingChatRequestListActivity.this;
        toolBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initView() {
        rvList = findViewById(R.id.rvList);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);

//        refresh();
    }


    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Pending Chat Requests");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void refresh() {
        ProgressDialog.showProgress(_context);
        WebService.getInstance().getPendingChatRequests(new RestCallBack<List<ChatTopic>>() {
            @Override
            public void onResponse(List<ChatTopic> response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    list = (ArrayList<ChatTopic>) response;
                    setListData();
                } else {
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    rvList.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                tvNoDataFound.setVisibility(View.VISIBLE);
                rvList.setVisibility(View.GONE);
            }
        });
    }

    private void setListData() {
        adapter = new PendingChatRequestListAdapter(_context, list);

        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new DividerItemDecoration(rvList.getContext(),
                DividerItemDecoration.VERTICAL));
        rvList.setAdapter(adapter);

        if (list.size() > 0) {
            tvNoDataFound.setVisibility(View.GONE);
            rvList.setVisibility(View.VISIBLE);
        } else {
            tvNoDataFound.setVisibility(View.VISIBLE);
            rvList.setVisibility(View.GONE);
        }
    }

    public void onClick(ChatTopic item) {
        ChatInitiated requestData = new ChatInitiated();
        requestData.ApplicationUserId = Integer.parseInt(item.applicationUserId);
        requestData.LocationId = item.locationId;
        requestData.Topic = item.topic;
        requestData.Description = item.description;
        requestData.Status = item.status;
        requestData.AppStatus = item.appStatus;
        requestData.Deleted = item.deleted;
        requestData.IsAlreadyAccepted = item.isAlreadyAccepted;
        requestData.Id = item.id;
        requestData.CreatedUtc = item.createdUtc;
        requestData.Sender = item.sender;
        requestData.AdminUserId = Integer.parseInt(HomeFragment.userId);
        TabbedActivity.tabbedActivity.joinIndividualGroup(requestData);

        new Handler().postDelayed(() -> refresh(), 2000);

    }

}