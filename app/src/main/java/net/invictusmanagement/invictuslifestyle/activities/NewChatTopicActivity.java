package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.RecentTopicAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.enum_utils.ChatRequestStatus;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.ChatNewTopicRequest;
import net.invictusmanagement.invictuslifestyle.models.ChatTopic;
import net.invictusmanagement.invictuslifestyle.models.Topic;
import net.invictusmanagement.invictuslifestyle.models.UpdateTopicStatus;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.List;

import okhttp3.ResponseBody;

public class NewChatTopicActivity extends AppCompatActivity implements IRefreshableFragment {

    private Context _context;
    private RecyclerView recyclerView;
    private TextView tvNoDataFound;
    private EditText edtTitle;
    private AppCompatButton btnCreate;
    private RecentTopicAdapter adapter;
    private int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat_topic);

        initControls();
    }

    private void initControls() {
        _context = NewChatTopicActivity.this;
        if (getIntent().getExtras() != null) {
            userId = (int) getIntent().getLongExtra("ID", 0);
        }

        toolBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rvPaymentHistory);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        btnCreate = findViewById(R.id.btnCreateTopic);
        edtTitle = findViewById(R.id.edTitle);

        adapter = new RecentTopicAdapter(_context);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edtTitle.getText().toString())) {
                    edtTitle.setError(getString(R.string.error_field_required));
                    edtTitle.requestFocus();
                } else {
                    ChatNewTopicRequest chatNewTopicRequest = new ChatNewTopicRequest();
                    chatNewTopicRequest.topic = edtTitle.getText().toString().trim();
                    chatNewTopicRequest.description = "";
//                    chatNewTopicRequest.sender = HomeFragment.userName;
                    chatNewTopicRequest.locationId = Long.parseLong(HomeFragment.roomLocationId);
                    chatNewTopicRequest.adminUserId = Long.parseLong(HomeFragment.userId);
                    chatNewTopicRequest.applicationUserId = userId;
                    TabbedActivity.tabbedActivity.addNewAdminTopicRequest(chatNewTopicRequest);

                    Topic item = new Topic();
                    Intent intent = new Intent(NewChatTopicActivity.this, GeneralChatActivity.class);
                    item.locationId = Integer.parseInt(String.valueOf(chatNewTopicRequest.locationId));
                    item.topic = chatNewTopicRequest.topic;
                    item.description = chatNewTopicRequest.description;
                    item.applicationUserId = String.valueOf(userId);
                    item.adminUserId = HomeFragment.userId;
                    item.status = "2";
                    item.appStatus = "1";
                    intent.putExtra(GeneralChatActivity.TOPIC_JSON, new Gson().toJson(item));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Topics");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void refresh() {
        ProgressDialog.showProgress(_context);
        WebService.getInstance().getChatTopicList(userId, new RestCallBack<List<ChatTopic>>() {
            @Override
            public void onResponse(List<ChatTopic> response) {
                ProgressDialog.dismissProgress();
                setData(response);
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void setData(List<ChatTopic> chatTopics) {

        adapter.refresh(chatTopics);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        if (chatTopics.size() > 0) {
            tvNoDataFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            tvNoDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void callAPIForTopicStatusChange(ChatTopic item) {
        String newStatus = ChatRequestStatus.Close.value();
        if (item.status.equals(ChatRequestStatus.Close.value())) {
            newStatus = ChatRequestStatus.Open.value();
        }

        UpdateTopicStatus updateTopicStatus = new UpdateTopicStatus();
        updateTopicStatus.id = (int) item.id;
        updateTopicStatus.status = newStatus;
        updateTopicStatus.applicationUserId = Integer.parseInt(item.applicationUserId);

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Are you sure you want to update topic status?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        WebService.getInstance().updateTopicStatus(updateTopicStatus,
                                new RestEmptyCallBack<ResponseBody>() {
                                    @Override
                                    public void onResponse(ResponseBody response) {
                                        TabbedActivity.tabbedActivity.updateTopicStatus(updateTopicStatus);
                                        refresh();

                                    }

                                    @Override
                                    public void onFailure(WSException wse) {

                                    }
                                });
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

//        WebService.getInstance().updateTopicStatus(item, new RestEmptyCallBack<ResponseBody>() {
//            @Override
//            public void onResponse(ResponseBody response) {
//
//            }
//
//            @Override
//            public void onFailure(WSException wse) {
//
//            }
//        });

    }
}