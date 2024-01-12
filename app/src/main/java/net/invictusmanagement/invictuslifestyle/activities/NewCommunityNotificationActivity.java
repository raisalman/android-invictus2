package net.invictusmanagement.invictuslifestyle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.models.CommunityNotificationList;
import net.invictusmanagement.invictuslifestyle.models.Feedback;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequestResponse;
import net.invictusmanagement.invictuslifestyle.models.UserUpdate;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import okhttp3.ResponseBody;

public class NewCommunityNotificationActivity extends BaseActivity {

    private EditText etMessage;
    private EditText etTitle;
    private LinearLayout llSelectRecipient;
    private TextView tvSelectRecipient;
    private String[] selectedRecipientIds;
    private Toolbar toolbar;
    private boolean isEdit = false;
    private CommunityNotificationList editCommunityNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_community_notification);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getSerializable("DATA") != null) {
                isEdit = true;
                editCommunityNotification = (CommunityNotificationList) getIntent()
                        .getSerializableExtra("DATA");
            }
        }

        toolBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etMessage = findViewById(R.id.etMessage);
        etTitle = findViewById(R.id.etTitle);
        tvSelectRecipient = findViewById(R.id.tvSelectRecipient);
        llSelectRecipient = findViewById(R.id.llSelectRecipient);

        llSelectRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewCommunityNotificationActivity.this,
                        UserSelectionActivity.class);
                activityForResultLauncher.launch(intent);
            }
        });

        tvSelectRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewCommunityNotificationActivity.this,
                        UserSelectionActivity.class);
                activityForResultLauncher.launch(intent);
            }
        });

        if (isEdit) {
            etMessage.setText(editCommunityNotification.message);
            etTitle.setText(editCommunityNotification.title);

            llSelectRecipient.setVisibility(View.GONE);
        }
    }


    private void toolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (isEdit) {
            toolbar.setTitle("Edit Community Notification");
        }
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

    ActivityResultLauncher<Intent> activityForResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            tvSelectRecipient.setText("");
                            selectedRecipientIds = data.getExtras().getStringArray("ID");
                            Log.e("selected from Intent IDs >> ", selectedRecipientIds + "");
                            String[] names = data.getExtras().getStringArray("NAME");
                            Log.e("selected from Intent Names >> ", names + "");
                            String selectedName = String.join(", ", names);
                            tvSelectRecipient.setText(selectedName);
                        }
                    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(NewCommunityNotificationActivity.this);
                return true;

            case R.id.action_send:
                item.setEnabled(false);
                boolean cancel = false;
                View focusView = null;

                etTitle.setError(null);
                etMessage.setError(null);

                if (TextUtils.isEmpty(etTitle.getText().toString())) {
                    etTitle.setError(getString(R.string.error_field_required));
                    focusView = etTitle;
                    cancel = true;
                } else if (TextUtils.isEmpty(etMessage.getText().toString())) {
                    etMessage.setError(getString(R.string.error_field_required));
                    focusView = etMessage;
                    cancel = true;
                }

                if (!cancel) {
                    CommunityNotificationList notification = new CommunityNotificationList();
                    notification.title = etTitle.getText().toString();
                    notification.message = etMessage.getText().toString();

                    Log.e("SelectedRecipientIds >> ", selectedRecipientIds + "");
                    if (tvSelectRecipient.getText().toString().trim().length() > 0 && !isEdit
                            && selectedRecipientIds != null) {
                        int[] ids = new int[selectedRecipientIds.length];
                        for (int i = 0; i < selectedRecipientIds.length; i++) {
                            ids[i] = Integer.parseInt(selectedRecipientIds[i]);
                        }
                        Log.e("SelectedRecipientIds int>> ", ids + "");
                        notification.selectedRecipientIds = ids;
                    }
                    if (!isEdit) {
                        callAPItoCreate(notification);
                    } else {
                        notification.id = editCommunityNotification.id;
                        callAPItoEdit(notification);
                    }
                } else {
                    focusView.requestFocus();
                    item.setEnabled(true);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callAPItoEdit(CommunityNotificationList notification) {

        WebService.getInstance().updateCommunityNotification(notification, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                Toast.makeText(NewCommunityNotificationActivity.this,
                        "Community Notification updated successfully",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(WSException wse) {
                Toast.makeText(NewCommunityNotificationActivity.this,
                        "Failed to update Community Notification",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void callAPItoCreate(CommunityNotificationList notification) {

        WebService.getInstance().createCommunityNotification(notification, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                Toast.makeText(NewCommunityNotificationActivity.this,
                        "Community Notification created successfully",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(WSException wse) {
                Toast.makeText(NewCommunityNotificationActivity.this,
                        "Failed to create Community Notification",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
