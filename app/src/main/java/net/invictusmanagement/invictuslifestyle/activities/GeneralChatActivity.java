package net.invictusmanagement.invictuslifestyle.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.invictusmanagement.invictuslifestyle.ForbiddenException;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.ChatAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.EndlessRecyclerViewScrollListener;
import net.invictusmanagement.invictuslifestyle.customviews.FileUtils;
import net.invictusmanagement.invictuslifestyle.customviews.PickFileFromDevice;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.enum_utils.AppStatus;
import net.invictusmanagement.invictuslifestyle.enum_utils.ChatRequestStatus;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.ChatItemClick;
import net.invictusmanagement.invictuslifestyle.models.ChatInitiated;
import net.invictusmanagement.invictuslifestyle.models.ChatMessageList;
import net.invictusmanagement.invictuslifestyle.models.GroupMassageSend;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.models.MessageData;
import net.invictusmanagement.invictuslifestyle.models.Topic;
import net.invictusmanagement.invictuslifestyle.models.TopicStatusUpdate;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class GeneralChatActivity extends AppCompatActivity implements ChatItemClick {

    public static final String TOPIC_JSON = "net.invictusmanagement.invictusmobile.topic";
    public static GeneralChatActivity generalChatActivity;
    static boolean active = false;
    public EditText editMessage;
    public RecyclerView rvMessages;
    public ImageButton btnSend, btnAttach;
    public List<MessageData> messageViewModels = new ArrayList<>();
    public GroupMassageSend groupMassageReceive;
    ChatInitiated chatInitiated;
    TextView tvToolBarTitle, tvStatus;
    private MaterialProgressBar loader;
    private ChatAdapter chatAdapter;
    private int messageIdMain = 0;
    private int lastMessageIdMain = 0;
    public static int chatRequestId = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Topic topic;
    private TextView tvWaitingForApproval, tvStatusText;
    private View viewDot;
    private PickFileFromDevice pickFileFromDevice;
    private boolean isFirstTime = true;
    private boolean isForImage = false;
    private SharedPreferences sharedPreferences;
    private String role;

    public void responseMessageChat(String successModel) {
        messageViewModels = new GsonBuilder().registerTypeAdapter(Date.class, new MobileDataProvider.DateDeserializer()).create().fromJson(successModel, new TypeToken<List<MessageData>>() {
        }.getType());
    }


    public void startActivityResult(Intent pictureIntent, int requestCode) {
        startActivityForResult(pictureIntent, requestCode);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        TabbedActivity.tabbedActivity.updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Active.value()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("GeneralChatActivity: OnResumeCalled");
        if (!isForImage)
            if (!isFirstTime) {
                if (topic.status.equals(ChatRequestStatus.Close.value()) ||
                        topic.status.equals(ChatRequestStatus.Open.value())) {
                    chatAdapter.clearDate(new ArrayList<>());
                    scrollListener.resetState();
                    messageIdMain = 0;
                    ChatMessageList chatMessageList = new ChatMessageList();
                    chatMessageList.chatRequestId = topic.id;
                    getPastChatList(chatMessageList, true);
                    if (topic.status.equals(ChatRequestStatus.Open.value())) {
                        TabbedActivity.tabbedActivity.updateChatCount(Integer.parseInt(String.valueOf(topic.id)), 0, 0);
                    }
                }
            }
        TabbedActivity.tabbedActivity.updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Active.value()));
        isForImage = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstTime = false;
        TabbedActivity.tabbedActivity.updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Offline.value()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_chat);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        role = sharedPreferences.getString("userRole", "");
        initViews();

    }

    private void initViews() {
        generalChatActivity = this;
        pickFileFromDevice = new PickFileFromDevice(generalChatActivity, generalChatActivity);
        tvStatusText = findViewById(R.id.tvStatusText);
        viewDot = findViewById(R.id.viewDot);
        loader = findViewById(R.id.loader);
        tvWaitingForApproval = findViewById(R.id.tvWaitingForApproval);
        rvMessages = findViewById(R.id.rvMessages);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        btnAttach = findViewById(R.id.btnAttach);
        initRecyclerView();
        initControls();
        getBundleData();
        /*toolBar();*/
    }


    private void initControls() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMessage.getText().toString().trim().length() > 0) {
                    if (TabbedActivity.isHubConnected) {
                        sendMessageToGroup(editMessage.getText().toString().trim());
                        editMessage.setText("");
                        /*Utilities.hideKeyboard(GeneralChatActivity.this);*/
                    } else {
                        Toast.makeText(TabbedActivity.tabbedActivity, "Please check after some time", Toast.LENGTH_LONG).show();
                    }

                } else {
                    editMessage.setError(getString(R.string.error_field_required));
                    editMessage.requestFocus();
                }
            }
        });
        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(GeneralChatActivity.this);
                checkPermissions();
            }
        });

    }


    private void checkPermissions() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()) {
                    uploadDocument();
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(getApplicationContext(), "Please accept all permissions from settings.", Toast.LENGTH_LONG).show();
                    openSetting();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                if (!permissions.isEmpty()) {
                    token.continuePermissionRequest();
                }
            }
        }).onSameThread().check();
    }


    private void openSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        String path = pickFileFromDevice.getImagePath();
        /*Bitmap myBitmap = BitmapFactory.decodeFile(path);
        getRotateImaged(path,myBitmap)*/
        if (requestCode == PickFileFromDevice.CAPTURE_IMAGE && path != null && resultCode != 0) {
            /*new File(path)*/
            uploadChatImage(new File(path));
        } else if (requestCode == PickFileFromDevice.PICK_IMAGE && data != null) {
            File file = FileUtils.getFile(this, data.getData());
            if (file != null) {
                if (((file.length() / 1024) / 1024) < 11) {
                    /*new File(Utilities.compressedFile(file))*/
                    uploadChatImage(file);
                } else {
                    Toast.makeText(this, "File must me less then or equal to 10 MB", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void uploadChatImage(File file) {
        try {
            ProgressDialog.showProgress(GeneralChatActivity.this);
            MobileDataProvider.getInstance().chatImage(getApplicationContext(), file, 278, topic.id, GeneralChatActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ForbiddenException e) {
            e.printStackTrace();
        }
    }


    private void uploadDocument() {
        Dialog dialog = new Dialog(this, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_product_upload);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.color.transparent));
        dialog.show();
        dialog.findViewById(R.id.uploadDocumentUsingCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isForImage = true;
                pickFileFromDevice.showCameraIntent(PickFileFromDevice.CAPTURE_IMAGE);
            }
        });

        dialog.findViewById(R.id.uploadDocumentUsingFilePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isForImage = true;
                pickFileFromDevice.showFileChooser(PickFileFromDevice.PICK_IMAGE);
            }
        });

        dialog.findViewById(R.id.dialogDocUploadCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    public void statusUpdated(TopicStatusUpdate chat, boolean b) {
        if (topic.id == Long.parseLong(chat.ChatRequestId)) {
            topic.status = chat.StatusId;
            topic.appStatus = "2";
            checkStatus(topic, b);
        }

    }

    public void chatInitiated(ChatInitiated chat, boolean b) {
        topic = new Topic();
        topic.appStatus = "2";
        topic.status = chat.Status;
        topic.topic = chat.Topic;
        topic.description = chat.Description;
        topic.id = chat.Id;
        topic.applicationUserId = String.valueOf(HomeFragment.userId);
        topic.adminUserId = String.valueOf(chat.AdminUserId);
        checkStatus(topic, b);
    }

    private void initRecyclerView() {
        chatAdapter = new ChatAdapter(GeneralChatActivity.this, GeneralChatActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GeneralChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        /*linearLayoutManager.setStackFromEnd(true);*/
        rvMessages.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (messageIdMain != 0 && lastMessageIdMain != messageIdMain) {
                    lastMessageIdMain = messageIdMain;
                    ChatMessageList chatMessageList = new ChatMessageList();
                    chatMessageList.chatRequestId = topic.id;
                    chatMessageList.messageId = messageIdMain;

                    getPastChatList(chatMessageList, false);
                }

            }
        };
        rvMessages.addOnScrollListener(scrollListener);
        rvMessages.setAdapter(chatAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if (topic.id != 0)
            TabbedActivity.tabbedActivity.leaveGroup(String.valueOf(topic.id));*/
    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            topic = new Gson().fromJson(getIntent().getStringExtra(TOPIC_JSON), new TypeToken<Topic>() {
            }.getType());

            checkStatus(topic, true);
        }
    }

    private void checkStatus(Topic topic, boolean b) {
        if (topic.status.equals(ChatRequestStatus.Pending.value())) {
            tvWaitingForApproval.setVisibility(View.VISIBLE);
            rvMessages.setVisibility(View.GONE);
        } else {
            tvWaitingForApproval.setVisibility(View.GONE);
            rvMessages.setVisibility(View.VISIBLE);
        }

        if (topic.status.equals(ChatRequestStatus.Close.value()) || topic.status.equals(ChatRequestStatus.Pending.value())) {
            editMessage.setEnabled(false);
            btnSend.setEnabled(false);
            btnAttach.setEnabled(false);
        } else if (topic.status.equals(ChatRequestStatus.Open.value())) {
            editMessage.setEnabled(true);
            btnSend.setEnabled(true);
            btnAttach.setEnabled(true);
            /*TabbedActivity.tabbedActivity.joinGroup(String.valueOf(topic.id));*/
        }

        if (topic.status.equals(ChatRequestStatus.Close.value()) || topic.status.equals(ChatRequestStatus.Open.value())) {
            ChatMessageList chatMessageList = new ChatMessageList();
            chatMessageList.chatRequestId = topic.id;
            chatRequestId = Integer.parseInt(String.valueOf(topic.id));
            /*chatMessageList.messageId = 0;*/
            if (b) {
                getPastChatList(chatMessageList, true);
            }
        }

        if (topic.status.equals(ChatRequestStatus.Open.value())) {
            TabbedActivity.tabbedActivity.updateChatCount(Integer.parseInt(String.valueOf(topic.id)), 0, 0);
        }

        updateStatus(topic.status);
        toolBar();
    }

    private void updateStatus(String status) {
        if (status.equals(ChatRequestStatus.Close.value())) {
            viewDot.setBackground(ContextCompat.getDrawable(GeneralChatActivity.this, R.drawable.dot_closed));
            tvStatusText.setText("CLOSED");
            tvStatusText.setTextColor(ContextCompat.getColor(GeneralChatActivity.this, R.color.color_red));
        } else if (status.equals(ChatRequestStatus.Pending.value())) {
            viewDot.setBackground(ContextCompat.getDrawable(GeneralChatActivity.this, R.drawable.dot_pending));
            tvStatusText.setText("PENDING");
            tvStatusText.setTextColor(ContextCompat.getColor(GeneralChatActivity.this, R.color.color_yellow));
        } else if (status.equals(ChatRequestStatus.Open.value())) {
            viewDot.setBackground(ContextCompat.getDrawable(GeneralChatActivity.this, R.drawable.dot_active));
            tvStatusText.setText("OPEN");
            tvStatusText.setTextColor(ContextCompat.getColor(GeneralChatActivity.this, R.color.color_green));
        }

    }

    private void toolBar() {

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvToolBarTitle = findViewById(R.id.tvToolBarTitle);
        tvStatus = findViewById(R.id.tvStatus);
        tvToolBarTitle.setText(topic.topic.trim());
        if (topic.appStatus.equals(AppStatus.Active.value())) {
            tvStatus.setText("(Online)");
        } else {
            tvStatus.setText("(Offline)");
        }

    }

    private void sendMessageToGroup(String message) {
        Utilities.hideKeyboard(TabbedActivity.tabbedActivity);
        GroupMassageSend groupMassageSend = new GroupMassageSend();
        groupMassageSend.ChatRequestId = String.valueOf(topic.id);
        groupMassageSend.Message = message;
        groupMassageSend.Sender = HomeFragment.userName;
        groupMassageSend.SenderId = HomeFragment.userId;
        if (role.equals(getString(R.string.role_leasing_officer)) ||
                role.equals(getString(R.string.role_property_manager))) {
            groupMassageSend.ReceiverId = topic.applicationUserId;
        } else {
            groupMassageSend.ReceiverId = topic.adminUserId;
        }
        groupMassageSend.MessageType = 1;

        MessageData messageViewModel = new MessageData();
        messageViewModel.isMyMessage = true;
        messageViewModel.residentName = groupMassageSend.Sender;
        messageViewModel.message = groupMassageSend.Message;
        messageViewModel.messageType = groupMassageSend.MessageType;
        messageViewModel.isRead = false;
        messageViewModel.createdUtc = Calendar.getInstance().getTime();
        chatAdapter.add(messageViewModel);

        rvMessages.smoothScrollToPosition(0);
        TabbedActivity.tabbedActivity.sendMessageToGroup(groupMassageSend);

    }

    private void sendImageMessageToGroup(GroupMassageSend groupMassageSendOriginal, String originalJson) {
        Utilities.hideKeyboard(TabbedActivity.tabbedActivity);
        GroupMassageSend groupMassageSend = new GroupMassageSend();
        groupMassageSend.ChatRequestId = String.valueOf(topic.id);
        groupMassageSend.Message = originalJson;
        groupMassageSend.Sender = HomeFragment.userName;
        groupMassageSend.SenderId = HomeFragment.userId;
        groupMassageSend.ReceiverId = topic.adminUserId;
        groupMassageSend.MessageType = 2;

        MessageData messageViewModel = new MessageData();
        messageViewModel.isMyMessage = true;
        messageViewModel.isLocalImage = false;
        messageViewModel.isReceivedImage = true;
        messageViewModel.isRead = false;
        messageViewModel.residentName = groupMassageSend.Sender;
        messageViewModel.message = groupMassageSendOriginal.Message;
        messageViewModel.messageType = groupMassageSend.MessageType;
        messageViewModel.createdUtc = groupMassageSendOriginal.CreatedUtc;
        chatAdapter.add(messageViewModel);

        rvMessages.smoothScrollToPosition(0);
        TabbedActivity.tabbedActivity.sendImageMessageToGroup(originalJson);
    }

    public void getPastChatList(ChatMessageList chatMessageList, boolean isOneTime) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                /*_swipeRefreshLayout.setRefreshing(true);*/
                if (loader.getVisibility() != View.VISIBLE)
                    loader.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().getChatMessageList(chatMessageList);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    loader.setVisibility(View.GONE);
                    if (messageViewModels.size() > 0) {
                        messageIdMain = Integer.parseInt(String.valueOf(messageViewModels.get(messageViewModels.size() - 1).chatMessageId));
                    }
                    /*Collections.reverse(messageViewModels);*/
                    chatAdapter.refresh(messageViewModels);
                    if (isOneTime)
                        rvMessages.smoothScrollToPosition(0);
                    /*rvMessages.scrollToPosition(chatAdapter.getItemCount() - 1);*/
                } else {
                    loader.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    public void makeAllRead(String message) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return true;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    if (Integer.parseInt(message) == topic.id) {
                        chatAdapter.markAllRead();
                    }
                    /*rvMessages.smoothScrollToPosition(0);*/
                }
            }
        }.execute();


    }

    public void receivedGroupMessageNewServer(String customMessage) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    groupMassageReceive = new GsonBuilder().registerTypeAdapter(Date.class, new MobileDataProvider.DateDeserializer()).create().fromJson(customMessage, new TypeToken<GroupMassageSend>() {
                    }.getType());

                    return true;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    if (Integer.valueOf(groupMassageReceive.ChatRequestId) == topic.id && !groupMassageReceive.Sender.equals(HomeFragment.userName)) {
                        MessageData messageViewModel = new MessageData();
                        messageViewModel.isMyMessage = false;
                        messageViewModel.isLocalImage = false;
                        messageViewModel.isReceivedImage = true;
                        messageViewModel.sender = groupMassageReceive.Sender;
                        messageViewModel.message = groupMassageReceive.Message;
                        messageViewModel.messageType = groupMassageReceive.MessageType;
                        messageViewModel.attachmentUrl = groupMassageReceive.attachmentUrl;
                        messageViewModel.createdUtc = Calendar.getInstance().getTime();
                        chatAdapter.add(messageViewModel);

                        rvMessages.smoothScrollToPosition(0);
                        TabbedActivity.tabbedActivity.updateChatCount(Integer.parseInt(String.valueOf(topic.id)), 0, 0);
                    }
                    /*rvMessages.smoothScrollToPosition(0);*/
                }
            }
        }.execute();
    }

    public void successFullyUploaded(boolean successful, String string, GroupMassageSend groupMassageSend, String originalJson) {
        ProgressDialog.dismissProgress();
        if (successful)
            sendImageMessageToGroup(groupMassageSend, originalJson);
        else
            Toast.makeText(GeneralChatActivity.this, string, Toast.LENGTH_LONG).show();
    }

    public void failToUpload(String string) {
        ProgressDialog.dismissProgress();
        Toast.makeText(GeneralChatActivity.this, string, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onChatClick(MessageData messageData) {
        if (messageData.messageType == 2) {
            isForImage = true;
            String thumbnailUrl = "";
            String originalUrl = "";
            if (messageData.isReceivedImage) {
                thumbnailUrl = messageData.message;
                if (thumbnailUrl.contains("thumbnail")) {
                    originalUrl = thumbnailUrl.replace("thumbnail", "original");
                } else {
                    originalUrl = thumbnailUrl;
                }
            } else {
                String str = "";
                String imgId = "";
                thumbnailUrl = messageData.attachmentUrl;

                if (messageData.message != null) {
                    str = messageData.message;
                }

                if (str.contains(",")) {
                    imgId = str.substring(0, str.indexOf(","));
                } else {
                    imgId = str;
                }

                if (thumbnailUrl.contains("thumbnail")) {
                    thumbnailUrl = thumbnailUrl.replace("thumbnail", "original");
                }

                originalUrl = thumbnailUrl + imgId + ".jpg";
            }

            MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
            maintenanceRequesFiles.isImage = true;
            maintenanceRequesFiles.maintenanceRequestImageSrc = originalUrl;
            Intent i = new Intent(GeneralChatActivity.this, SingleVideoImageActivity.class);
            i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
            startActivity(i);
        }
    }

    public void callMessageListAPI(ChatInitiated requestData) {
        topic.status = requestData.Status;
        topic.id = requestData.Id;
        if (topic.status.equals(ChatRequestStatus.Close.value()) || topic.status.equals(ChatRequestStatus.Open.value())) {
            ChatMessageList chatMessageList = new ChatMessageList();
            chatMessageList.chatRequestId = topic.id;
            chatRequestId = Integer.parseInt(String.valueOf(topic.id));
            getPastChatList(chatMessageList, true);
        }
    }
}