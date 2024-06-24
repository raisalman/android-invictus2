package net.invictusmanagement.invictuslifestyle.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.brivo.sdk.BrivoSDKInitializationException;
import com.brivo.sdk.access.BrivoSDKAccess;
import com.brivo.sdk.ble.BrivoBLEErrorCodes;
import com.brivo.sdk.enums.AccessPointCommunicationState;
import com.brivo.sdk.model.AccessPointPath;
import com.brivo.sdk.model.BrivoError;
import com.brivo.sdk.onair.interfaces.IOnRedeemPassListener;
import com.brivo.sdk.onair.interfaces.IOnRetrieveSDKLocallyStoredPassesListener;
import com.brivo.sdk.onair.model.BrivoAccessPoint;
import com.brivo.sdk.onair.model.BrivoOnairPass;
import com.brivo.sdk.onair.model.BrivoSelectedAccessPoint;
import com.brivo.sdk.onair.model.BrivoSite;
import com.brivo.sdk.onair.model.BrivoTokens;
import com.brivo.sdk.onair.repository.BrivoSDKOnair;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.microsoft.signalr.TransportEnum;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.openpath.mobileaccesscore.OpenpathItem;
import com.openpath.mobileaccesscore.OpenpathLocationStatus;
import com.openpath.mobileaccesscore.OpenpathLockdownPlan;
import com.openpath.mobileaccesscore.OpenpathLogging;
import com.openpath.mobileaccesscore.OpenpathMobileAccessCore;
import com.openpath.mobileaccesscore.OpenpathOrderingItem;
import com.openpath.mobileaccesscore.OpenpathProvisionResponse;
import com.openpath.mobileaccesscore.OpenpathRequestResponse;
import com.openpath.mobileaccesscore.OpenpathResponse;
import com.openpath.mobileaccesscore.OpenpathSwitchUserResponse;
import com.openpath.mobileaccesscore.OpenpathSyncUserResponse;
import com.openpath.mobileaccesscore.OpenpathUnprovisionResponse;
import com.openpath.mobileaccesscore.OpenpathUserSettings;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.MRSImagesVideosDisplayAdapter;
import net.invictusmanagement.invictuslifestyle.adapters.SectionViewPagerAdapter;
import net.invictusmanagement.invictuslifestyle.asynctasks.HubConnectionTask;
import net.invictusmanagement.invictuslifestyle.asynctasks.HubConnectionTaskStop;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.enum_utils.AccessPointOperator;
import net.invictusmanagement.invictuslifestyle.enum_utils.AppStatus;
import net.invictusmanagement.invictuslifestyle.enum_utils.DeviceType;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.fragments.AccessPointsFragment;
import net.invictusmanagement.invictuslifestyle.fragments.AllCouponsFragment;
import net.invictusmanagement.invictuslifestyle.fragments.AllDigitalKeysFragment;
import net.invictusmanagement.invictuslifestyle.fragments.BillBoardFragment;
import net.invictusmanagement.invictuslifestyle.fragments.BrivoDevicesFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GeneralChatAdminFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GeneralChatFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GuestAccessPointsFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GuestDigitalKeysFragment;
import net.invictusmanagement.invictuslifestyle.fragments.HealthFragment;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.fragments.NotificationsFragment;
import net.invictusmanagement.invictuslifestyle.fragments.RentalToolFragment;
import net.invictusmanagement.invictuslifestyle.fragments.ServiceKeysFragment;
import net.invictusmanagement.invictuslifestyle.fragments.VoiceMailFragment;
import net.invictusmanagement.invictuslifestyle.helium_communication.LoginTask;
import net.invictusmanagement.invictuslifestyle.interfaces.AccessPointsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.BrivoDevicesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.DigitalKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.GuestDigitalKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.HealthVideoListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.MaintenanceRequestsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.MarketPostListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.NotificationsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.ServiceKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.ShowSingleMRSItem;
import net.invictusmanagement.invictuslifestyle.interfaces.VoiceMailFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.AccessCodeResponse;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.AccessPointAudit;
import net.invictusmanagement.invictuslifestyle.models.BrivoDeviceData;
import net.invictusmanagement.invictuslifestyle.models.BulletinBoard;
import net.invictusmanagement.invictuslifestyle.models.ChatInitiated;
import net.invictusmanagement.invictuslifestyle.models.ChatNewTopicRequest;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;
import net.invictusmanagement.invictuslifestyle.models.DigitalKeyRenew;
import net.invictusmanagement.invictuslifestyle.models.DigitalKeyUpdate;
import net.invictusmanagement.invictuslifestyle.models.ForceUpdateCheck;
import net.invictusmanagement.invictuslifestyle.models.GroupMassageSend;
import net.invictusmanagement.invictuslifestyle.models.GuestDigitalKey;
import net.invictusmanagement.invictuslifestyle.models.HealthVideo;
import net.invictusmanagement.invictuslifestyle.models.Helper;
import net.invictusmanagement.invictuslifestyle.models.MRSRating;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequest;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequestResponse;
import net.invictusmanagement.invictuslifestyle.models.Notification;
import net.invictusmanagement.invictuslifestyle.models.NotificationStatus;
import net.invictusmanagement.invictuslifestyle.models.OpenAccessPoint;
import net.invictusmanagement.invictuslifestyle.models.ServiceKey;
import net.invictusmanagement.invictuslifestyle.models.TopicStatusUpdate;
import net.invictusmanagement.invictuslifestyle.models.UpdateAppStatus;
import net.invictusmanagement.invictuslifestyle.models.UpdateChatCount;
import net.invictusmanagement.invictuslifestyle.models.UpdateTopicStatus;
import net.invictusmanagement.invictuslifestyle.models.UserDeviceId;
import net.invictusmanagement.invictuslifestyle.models.VoiceMail;
import net.invictusmanagement.invictuslifestyle.service.ChatRegistrationIntentService;
import net.invictusmanagement.invictuslifestyle.service.FirebaseService;
import net.invictusmanagement.invictuslifestyle.service.NotificationService;
import net.invictusmanagement.invictuslifestyle.utils.BrivoSampleConstants;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;
import net.invictusmanagement.invictuslifestyle.widgets.NewWidgetActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*import io.pdk.pdkmobilesdkandroid.PDKLibrary;
import io.pdk.pdkmobilesdkandroid.PDKLibraryRxWrapper;
import io.pdk.pdkmobilesdkandroid.model.error.BluetoothException;
import io.pdk.pdkmobilesdkandroid.model.error.DataSyncException;
import io.reactivex.Single;*/
import okhttp3.ResponseBody;

public class TabbedActivity extends BaseActivity implements AccessPointsListFragmentInteractionListener, NotificationsListFragmentInteractionListener, BrivoDevicesListFragmentInteractionListener, MaintenanceRequestsListFragmentInteractionListener, DigitalKeysListFragmentInteractionListener, ServiceKeysListFragmentInteractionListener, VoiceMailFragmentInteractionListener, HealthVideoListFragmentInteractionListener, MarketPostListFragmentInteractionListener, GuestDigitalKeysListFragmentInteractionListener, ShowSingleMRSItem, OpenpathMobileAccessCore.OpenpathEventHandler, DataClient.OnDataChangedListener, MessageClient.OnMessageReceivedListener, CapabilityClient.OnCapabilityChangedListener {

    private static String role;
    private String dataPathWearable = "/data_path";
    private SharedPreferences sharedPreferences;

    public static final String ENDPOINT = "https://api.openpath.com/";
    private static final String TAG = "TabbedActivity";
    public static String connectionID;
    public static String mobileChatConnectionID;
    public int _currentTabPosition = 0;
    public int _previousTabPosition = -1;
    public static Boolean isGuestUser, isWalkThroughEnable, isRWTHome, isGWTHome, isRWTCoupons, isGWTCoupons, isRWTAccessPoint, isRWTDigitalKey, isRWTServiceKey, isGWTDigitalKey, isRWTNotifictions, isGWTNotifictions, isRWTRenterTools, isRWTHealthAndWellness, isGWTHealthAndWellness, isRWTVoiceMail, isRWTBulletinBoard, isRWTAmenities, isRWTTopic;
    public static Boolean isHubConnected = false;
    public static Boolean isMobileHubConnected = false;
    public static Boolean hasRemoteUnlock = false;
    public static Boolean isVisible = false;
    public static Boolean isFirstTime = true;
    public static Boolean isOpenPathInit = false;
    public static Boolean isFirstTimeNotificationDot = true;
    public static TabbedActivity tabbedActivity;
    public static int viewPagerCount;
    public SectionViewPagerAdapter _sectionViewPagerAdapter;
    public static TabLayout _tabLayout;
    public static ViewPager2 _viewPager;
    public static boolean isFromNotification = false;
    public static boolean isQuickKey = false;
    public static boolean isAccessPoint = false;
    public static AlertDialog forceUpdateBuilder;
    public static AlertDialog surveyBuilder;
    public static AlertDialog ratingBuilder;
    public static List<ForceUpdateCheck> forceUpdateCheckList = new ArrayList<>();
    public static Handler handlerLogin;
    public static HandlerThread handlerThread;
    private final BroadcastReceiver handler = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("--Notification", "onReceive: Broadcast Fired");
            HomeFragment.newInstance().getNotificationCount();
            HomeFragment.newInstance().notificationStatus();
            new NotificationsFragment().refresh(false);
        }
    };
    public Boolean isScreenVisible = true;
    public HubConnection hubConnection;
    public HubConnection mobileHubConnection;
    ChatInitiated chatInitiated;
    TopicStatusUpdate topicStatusUpdate;
    private AccessPoint _accessPoint;
    private int _accessPointPosition;
    private AccessPointsFragment _accessPointFragment;
    private GuestAccessPointsFragment _guestAccessPointFragment;
    private boolean isAccessPointFailed = false;
//    private PDKLibraryRxWrapper sdk;

    public static void showNotificationDialog(String title, String body) {

        AlertDialog.Builder dialog = new AlertDialog.Builder((Context) TabbedActivity.tabbedActivity).setTitle(title).setMessage(body).setPositiveButton(android.R.string.yes, (dialog1, which) -> {
            // Continue with delete operation
            dialog1.dismiss();
        });
        TabbedActivity.tabbedActivity.runOnUiThread(() -> dialog.show());
    }


    public void refreshAdapter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isGuestUser = sharedPreferences.getBoolean("isGuestUser", false);

        role = sharedPreferences.getString("userRole", "");

        _sectionViewPagerAdapter = new SectionViewPagerAdapter(this);
        _viewPager.setAdapter(_sectionViewPagerAdapter);
        new TabLayoutMediator(_tabLayout, _viewPager, (tab, position) -> {

            if (role.equalsIgnoreCase(getString(R.string.role_vendor))) {
                tab.setText(_sectionViewPagerAdapter.getVendorTabs()[position]);
            } else if (role.equalsIgnoreCase(getString(R.string.role_leasing_officer)) || role.equalsIgnoreCase(getString(R.string.role_property_manager))) {
                tab.setText(_sectionViewPagerAdapter.getAdminTabsName().get(position));
            } else if (role.equals(getString(R.string.role_resident))) {
                tab.setText(_sectionViewPagerAdapter.getResidentTabsName().get(position));
            } else if (role.equals(getString(R.string.role_facility))) {
                tab.setText(_sectionViewPagerAdapter.getFacilityTabsName().get(position));
            } else {
                tab.setText(_sectionViewPagerAdapter.getGuestTabs()[position]);
            }
        }).attach();

        tabCustomizationForDot(context);

    }


    public void chatNotificationDot(boolean chat) {
        TabLayout.Tab tab = _tabLayout.getTabAt(_sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT);
        if (tab != null) {
            TextView tabDot = tab.getCustomView().findViewById(R.id.tabDot);
            Utilities.showInvisible(tabbedActivity, tabDot, chat);
        }
    }


    public void setGuestTabDot(NotificationStatus notificationStatus, Context context) {
        for (int i = 1; i < _sectionViewPagerAdapter.getItemCount(); i++) {
            TabLayout.Tab tab = _tabLayout.getTabAt(i);
            TextView tabDot = tab.getCustomView().findViewById(R.id.tabDot);
            if (i == 1) {
                Utilities.showHide(context, tabDot, notificationStatus.getAccessPoints());
            } else if (i == 2) {
                Utilities.showHide(context, tabDot, notificationStatus.getCoupons());
            } else if (i == 3) {
                Utilities.showHide(context, tabDot, notificationStatus.getDigitalKey());
            } else if (i == 5) {
                Utilities.showHide(context, tabDot, notificationStatus.getHealthVideo());
            }
        }
    }

    public void setResidentTabDot(NotificationStatus notificationStatus, Context context) {
        for (int i = 1; i < _sectionViewPagerAdapter.getItemCount(); i++) {
            TabLayout.Tab tab = _tabLayout.getTabAt(i);
            TextView tabDot = tab.getCustomView().findViewById(R.id.tabDot);
            if (i == 1) {
                Utilities.showInvisible(tabbedActivity, tabDot, notificationStatus.getAccessPoints());
            } else if (i == 2) {
                Utilities.showInvisible(tabbedActivity, tabDot, notificationStatus.getCoupons());
            } else if (i == 3) {
                Utilities.showInvisible(tabbedActivity, tabDot, notificationStatus.getDigitalKey());
            } else if (i == 5) {
                Utilities.showInvisible(tabbedActivity, tabDot, notificationStatus.getMainRequests());
            } else if (i == 6) {
                Utilities.showInvisible(tabbedActivity, tabDot, notificationStatus.getHealthVideo());
            } else if (i == 7) {
                Utilities.showInvisible(tabbedActivity, tabDot, notificationStatus.getVoiceMail());
            } else if (i == 8) {
                if (sharedPreferences.getBoolean("allowBulletinBoard", true)) {
                    Utilities.showInvisible(tabbedActivity, tabDot, notificationStatus.getBulletinBoard());
                }
            } else if (i == 9) {
                if (sharedPreferences.getBoolean("allowGeneralChat", true)) {
                    Utilities.showInvisible(tabbedActivity, tabDot, notificationStatus.getChat());
                }
            }
        }
    }

    private void tabCustomizationForDot(Context context) {
        for (int i = 0; i < _sectionViewPagerAdapter.getItemCount(); i++) {
            TabLayout.Tab tab = _tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.layout_tabwithdot);
            TextView tabText = tab.getCustomView().findViewById(R.id.tabTitle);
            if (isGuestUser) {
                tabText.setText(_sectionViewPagerAdapter.getGuestTabs()[i]);
            } else if (role.equals(context.getString(R.string.role_vendor))) {
                tabText.setText(_sectionViewPagerAdapter.getVendorTabs()[i]);
            } else if (role.equals(context.getString(R.string.role_property_manager)) || role.equals(context.getString(R.string.role_leasing_officer))) {
                tabText.setText(_sectionViewPagerAdapter.getAdminTabsName().get(i));
            } else if (role.equals(context.getString(R.string.role_facility))) {
                tabText.setText(_sectionViewPagerAdapter.getFacilityTabsName().get(i));
            } else {
                tabText.setText(_sectionViewPagerAdapter.getResidentTabsName().get(i));
            }
        }
    }

    public static void responseForceUpdate(String response) {
        forceUpdateCheckList = new Gson().fromJson(response, new TypeToken<List<ForceUpdateCheck>>() {
        }.getType());
    }

    public static void openPathLogin(final String email, final String password, final String organizationId, final String userId, final String mfaCode) {
        /*Toast.makeText(TabbedActivity.tabbedActivity,isFirstTime.toString(),Toast.LENGTH_LONG).show();*/
        if (isFirstTime) {
            handlerThread = new HandlerThread("helium api", Process.THREAD_PRIORITY_BACKGROUND);
            handlerThread.start();
            handlerLogin = new Handler(handlerThread.getLooper());
            login(email, password, organizationId, userId, mfaCode);
        }

    }

    public static void login(final String email, final String password, final String organizationId, final String userId, final String mfaCode) {
        /*ProgressDialog.showProgress(tabbedActivity);*/
        handlerLogin.post(new LoginTask(ENDPOINT, email, password, organizationId, userId, mfaCode));
    }

    public static void logOutOpenPath() {
        OpenpathMobileAccessCore.getInstance().unprovision(OpenpathMobileAccessCore.getInstance().getUserOpal());
    }

    public void initHubConnection() {
        System.out.println("initHubConnection");
        hubConnection = HubConnectionBuilder.create(BuildConfig._chatHubBaseUrl).withTransport(TransportEnum.LONG_POLLING).build();
        //The message here is the information sent to us by the server.
        receiveFromServer();
        new HubConnectionTask(hubConnection, true).execute(hubConnection);

        hubConnection.onClosed(exception -> {
            isHubConnected = false;
            if (isVisible) initHubConnection();
        });

    }

    public void initMobileChatHubConnection() {
        mobileHubConnection = HubConnectionBuilder.create(BuildConfig._chatMobileHubBaseUrl).withTransport(TransportEnum.LONG_POLLING).build();
        //The message here is the information sent to us by the server.
        receiveFromMobileServer();
        new HubConnectionTask(mobileHubConnection, false).execute(mobileHubConnection);

        mobileHubConnection.onClosed(exception -> {
            isMobileHubConnected = false;
            if (isVisible) initMobileChatHubConnection();
        });
    }

    public boolean checkConnectionActive() {
        if (hubConnection != null)
            return hubConnection.getConnectionState() == HubConnectionState.CONNECTED;
        else return false;
    }

    public boolean checkMobileHubConnectionActive() {
        if (mobileHubConnection != null)
            return mobileHubConnection.getConnectionState() == HubConnectionState.CONNECTED;
        else return false;
    }

    public void joinGroup(String groupName) {
        System.out.println("JoinGroup: " + groupName);
        if (checkConnectionActive()) hubConnection.send("JoinGroup", groupName);

    }

    public void joinMobileHUbGroup(String groupName) {
        System.out.println("Register1: " + groupName);
        if (checkMobileHubConnectionActive()) {
            System.out.println("Register2: " + groupName);
            mobileHubConnection.send("Register", Long.parseLong(groupName));
        }

    }

    public void joinResidentAllGroup(String applicationUserId) {
        System.out.println("JoinResidentAllGroup: " + applicationUserId);
        if (checkConnectionActive()) hubConnection.send("JoinResidentAllGroup", applicationUserId);

    }

    public void updateAppStatus(String ApplicationUserId, int AppStatus) {
        if (ApplicationUserId != null) if (!ApplicationUserId.equals("")) {
            UpdateAppStatus updateAppStatus = new UpdateAppStatus();
            updateAppStatus.ApplicationUserId = ApplicationUserId;
            updateAppStatus.AppStatus = AppStatus;
            System.out.println("UpdateAppStatus: " + new Gson().toJson(updateAppStatus));
            if (checkConnectionActive())
                hubConnection.send("UpdateAppStatus", new Gson().toJson(updateAppStatus));
        }
    }

    public void updateChatCount(int ChatRequestId, int MessageCount, int IsAdmin) {
        UpdateChatCount updateChatCount = new UpdateChatCount();
        updateChatCount.ChatRequestId = ChatRequestId;
        updateChatCount.MessageCount = MessageCount;
        if (role.equals(getString(R.string.role_property_manager)) || role.equals(getString(R.string.role_leasing_officer))) {
            updateChatCount.IsAdmin = 1;
        } else {
            updateChatCount.IsAdmin = 0;
        }
        System.out.println("UpdateChatCount: " + new Gson().toJson(updateChatCount));
        if (checkConnectionActive())
            hubConnection.send("UpdateChatCount", new Gson().toJson(updateChatCount));

    }

    public void joinIndividualGroup(ChatInitiated updateTopicStatus) {
        System.out.println("JoinIndividualGroup: " + new Gson().toJson(updateTopicStatus));
        if (checkConnectionActive())
            hubConnection.send("JoinIndividualGroup", new Gson().toJson(updateTopicStatus));
    }

    public void updateTopicStatus(UpdateTopicStatus updateTopicStatus) {
        System.out.println("UpdateTopicStatus: " + new Gson().toJson(updateTopicStatus));
        if (checkConnectionActive())
            hubConnection.send("UpdateTopicStatus", new Gson().toJson(updateTopicStatus));
    }

    public void addNewTopicRequest(ChatNewTopicRequest chatNewTopicRequest) {
        System.out.println("ChatRequestToOfficer: " + new Gson().toJson(chatNewTopicRequest));
        if (checkConnectionActive())
            hubConnection.send("ChatRequestToOfficer", new Gson().toJson(chatNewTopicRequest));

    }

    public void addNewAdminTopicRequest(ChatNewTopicRequest chatNewTopicRequest) {
        System.out.println("ChatRequestToResident: " + new Gson().toJson(chatNewTopicRequest));
        if (checkConnectionActive())
            hubConnection.send("ChatRequestToResident", new Gson().toJson(chatNewTopicRequest));

    }

    public void sendMessageToGroup(GroupMassageSend groupMassageSend) {
        System.out.println("SendMessageToGroup: " + new Gson().toJson(groupMassageSend));
        if (checkConnectionActive())
            hubConnection.send("SendMessageToGroup", new Gson().toJson(groupMassageSend));

    }

    public void sendImageMessageToGroup(String groupMassageSend) {
        System.out.println("SendImageMessageToGroup: " + groupMassageSend);
        if (checkConnectionActive())
            hubConnection.send("SendImageMessageToGroup", groupMassageSend);

    }

    public void leaveGroup(String roomLocationId) {
        System.out.println("LeaveGroup: " + roomLocationId);
        if (checkConnectionActive()) hubConnection.send("LeaveGroup", roomLocationId);

    }

    public void receiveFromServer() {

        hubConnection.on("Disconnected", (message) -> {
            System.out.println("Disconnected: " + message);
            initHubConnection();
        }, String.class);

        hubConnection.on("Connected", (message) -> {
            System.out.println("Connected: " + message);
            connectionID = message;
        }, String.class);

        hubConnection.on("ChatInitiated", (message) -> {
            System.out.println("ChatInitiated: " + message);
            checkAndSetDataForChatInit(message, true);
        }, String.class);

        hubConnection.on("TopicStatusUpdated", (message) -> {
            System.out.println("TopicStatusUpdated: " + message);
            checkAndSetDataForStatusUpdate(message, false);
        }, String.class);

        hubConnection.on("MessageShown", (message) -> {
            System.out.println("MessageShown: " + message);
            if (Integer.parseInt(message) == GeneralChatActivity.generalChatActivity.chatRequestId)
                GeneralChatActivity.generalChatActivity.makeAllRead(message);
        }, String.class);

        hubConnection.on("AdminChatRequested", (message) -> {
            System.out.println("AdminChatRequested: " + message);
            //Receive data after Admin chat created
            ChatInitiated requestData = new GsonBuilder().registerTypeAdapter(Date.class, new MobileDataProvider.DateDeserializer()).create().fromJson(message, new TypeToken<ChatInitiated>() {
            }.getType());


            if (GeneralChatActivity.generalChatActivity.chatRequestId == 0) {
                GeneralChatActivity.generalChatActivity.callMessageListAPI(requestData);
            }

        }, String.class);

        hubConnection.on("RecidentRequestforChat", (message) -> {
            System.out.println("RecidentRequestforChat: " + message);

            ChatInitiated requestData = new GsonBuilder().registerTypeAdapter(Date.class, new MobileDataProvider.DateDeserializer()).create().fromJson(message, new TypeToken<ChatInitiated>() {
            }.getType());

            showAlertDialog(requestData);

        }, String.class);

        hubConnection.on("ReceiveGroupMessage", (message) -> {
            System.out.println("ReceiveGroupMessage: " + message);
            if (GeneralChatActivity.active)
                GeneralChatActivity.generalChatActivity.receivedGroupMessageNewServer(message);


            if (HomeFragment.newInstance() != null) {
                HomeFragment.newInstance().chatHomeDot(true);
            }

            if (role.equals(getString(R.string.role_property_manager)) || role.equals(getString(R.string.role_leasing_officer))) {
                if (GeneralChatAdminFragment.active && !GeneralChatActivity.active) {
                    if (_currentTabPosition == _sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT) {
                        HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Chat.value());
                    } else {
                        chatNotificationDot(true);
                    }
                } else {
                    showNotificationDialog("Chat", "New Chat Message Received");
                    chatNotificationDot(true);
                }
            } else {
                if (GeneralChatFragment.active && !GeneralChatActivity.active) {
                    if (_currentTabPosition == _sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT) {
                        HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Chat.value());
                    } else {
                        chatNotificationDot(true);
                    }
                    GeneralChatFragment.newInstance().getTopicList(false);
                } else {
                    showNotificationDialog("Chat", "New Chat Message Received");
                    chatNotificationDot(true);
                }
            }
        }, String.class);

        hubConnection.on("ErrorInGroupMsg", (message) -> {
            System.out.println("ErrorInGroupMsg: " + message);
        }, String.class);

    }

    public void receiveFromMobileServer() {

        mobileHubConnection.on("Disconnected", (message) -> {
            System.out.println("Disconnected: " + message);
            initMobileChatHubConnection();
        }, String.class);

        mobileHubConnection.on("Connected", (message) -> {
            System.out.println("Mobile Connected: " + message);
            mobileChatConnectionID = message;
            if (HomeFragment.newInstance().roomLocationId.length() > 0) {
                joinMobileHUbGroup(HomeFragment.newInstance().roomLocationId);
            }
        }, String.class);

        mobileHubConnection.on("CheckUserDeviceId", (message) -> {
            System.out.println("CheckUserDeviceId: " + message);

            UserDeviceId userDeviceDetails = new GsonBuilder().registerTypeAdapter(Date.class, new MobileDataProvider.DateDeserializer()).create().fromJson(message, new TypeToken<UserDeviceId>() {
            }.getType());

            if (userDeviceDetails.applicationUserId.equals(HomeFragment.userId)) {
                String deviceId = Utilities.getDeviceID(TabbedActivity.this);
                if (!userDeviceDetails.deviceId.equalsIgnoreCase(deviceId)) {
                    logoutFromApp(true);
                }
            }
        }, String.class);
    }

    public void logoutFromApp(boolean showMessage) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) this);
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent((Context) this, LoginActivity.class);
        intent.putExtra("FromLogout", true);
        intent.putExtra("showMessage", showMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        logOutOpenPath();
        finishAffinity();
        if (showMessage)
            Toast.makeText((Context) this, "You have been logged into another device", Toast.LENGTH_LONG).show();
    }

    private void showAlertDialog(ChatInitiated requestData) {
        String msgtext = requestData.Sender + " want to chat with you on below topic." + " Please click on Join button to continue.\n\nTopic: " + requestData.Topic;
        if (!requestData.Description.equals("")) {
            msgtext = msgtext + "\nDescription: " + requestData.Description;
        }

        String finalMsgtext = msgtext;
        runOnUiThread(() -> new AlertDialog.Builder((Context) TabbedActivity.this).setCancelable(false).setMessage(finalMsgtext).setPositiveButton("Join", (arg0, arg1) -> {

            requestData.AdminUserId = Integer.parseInt(HomeFragment.userId);
            joinIndividualGroup(requestData);
        }).setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss()).create().show());
    }

    private void checkAndSetDataForChatInit(String message, boolean b) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    chatInitiated = new GsonBuilder().registerTypeAdapter(Date.class, new MobileDataProvider.DateDeserializer()).create().fromJson(message, new TypeToken<ChatInitiated>() {
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
                    int id = chatInitiated.ApplicationUserId;
                    if (chatInitiated.IsAlreadyAccepted == 0) {
                        if (role.equals(getString(R.string.role_property_manager)) || role.equals(getString(R.string.role_leasing_officer))) {
                            id = chatInitiated.AdminUserId;
                        }

                        if (id == Integer.parseInt(HomeFragment.userId)) {
                            if (GeneralChatActivity.active)
                                GeneralChatActivity.generalChatActivity.chatInitiated(chatInitiated, b);
                        }
                    }
                }
            }
        }.execute();
    }

    private void checkAndSetDataForStatusUpdate(String message, boolean b) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    topicStatusUpdate = new GsonBuilder().registerTypeAdapter(Date.class, new MobileDataProvider.DateDeserializer()).create().fromJson(message, new TypeToken<TopicStatusUpdate>() {
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
                    if (topicStatusUpdate.ApplicationUserId == Integer.parseInt(HomeFragment.userId)) {
                        if (GeneralChatActivity.active)
                            GeneralChatActivity.generalChatActivity.statusUpdated(topicStatusUpdate, b);
                    }
                }
            }
        }.execute();
    }

    private void updateNotificationStatus(TabLayout.Tab tab) {
        if (isGuestUser) {
            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.AccessPoints.value());
            }
            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Coupons.value());
            }
            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.DigitalKey.value());
            }

            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_HEALTH) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.HealthVideo.value());
            }
        } else {

            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.AccessPoints.value());
            }

            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Coupons.value());
            }

            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_HEALTH) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.HealthVideo.value());
            }

            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_BILLBOARD) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.BulletinBoard.value());
            }

            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_VOICE_MAIL) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.VoiceMail.value());
            }
            if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT) {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Chat.value());
            }
        }

    }

    // Start the  service
    public void startNewService() {
        Intent intent = new Intent((Context) this, NotificationService.class);
        intent.putExtra("string", "string_value");
        this.startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isGuestUser) {
            getMenuInflater().inflate(R.menu.menu_without_setting_tabbed, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent((Context) TabbedActivity.this, SettingsActivity.class));
                return true;

            case R.id.action_feedback:
                startActivity(new Intent((Context) TabbedActivity.this, NewFeedbackActivity.class));
                return true;

            case R.id.action_refresh:
                Log.e("current Tab >> ", " >>" + _viewPager.getCurrentItem());
                ((IRefreshableFragment) getSupportFragmentManager().findFragmentByTag("f" + _viewPager.getCurrentItem())).refresh();
                return true;

            case R.id.action_about:
                Utilities.showAboutDialog(this);
                return true;

            case R.id.action_logout:
                new AlertDialog.Builder((Context) TabbedActivity.this).setCancelable(true).setMessage("Are you sure you want to logout from the App?").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logoutFromApp(false);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tabbed);

//        registerWithNotificationHubs();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabbedActivity = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) TabbedActivity.this);
        OpenpathMobileAccessCore.getInstance().init(getApplication(), TabbedActivity.this);
        isGuestUser = sharedPreferences.getBoolean("isGuestUser", false);
        sharedPreferences.edit().putBoolean("isFirstTime", false).apply();
        isWalkThroughEnable = sharedPreferences.getBoolean("isWalkThroughEnable", false);

        forceUpdateCheck();
//        checkPermission();
        askPermissionOpenPath();
        if (!isGuestUser) {
            if (isScreenVisible) {
                initHubConnection();
                initMobileChatHubConnection();
                setDataForWearable();
            }
        }

        Intent intent2 = new Intent((Context) this, NewWidgetActivity.class);
        intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance((Context) this).getAppWidgetIds(new ComponentName((Context) this, NewWidgetActivity.class));
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        this.sendBroadcast(intent2);


        getWalkThroughData(sharedPreferences);

        if (getIntent().getExtras() != null) {
            isFromNotification = getIntent().getExtras().getBoolean("isFromNotification");
            isQuickKey = getIntent().getExtras().getBoolean("isQuickKey");
            isAccessPoint = getIntent().getExtras().getBoolean("isAccessPoint");
        }

        role = sharedPreferences.getString("userRole", "");

        _tabLayout = findViewById(R.id.tabs);
        _viewPager = findViewById(R.id.container);
        _viewPager.setUserInputEnabled(false);

        _sectionViewPagerAdapter = new SectionViewPagerAdapter(this);
        _viewPager.setAdapter(_sectionViewPagerAdapter);

        new TabLayoutMediator(_tabLayout, _viewPager, (tab, position) -> {
            if (role.equalsIgnoreCase(getString(R.string.role_vendor))) {
                tab.setText(_sectionViewPagerAdapter.getVendorTabs()[position]);
            } else if (role.equalsIgnoreCase(getString(R.string.role_leasing_officer)) || role.equalsIgnoreCase(getString(R.string.role_property_manager))) {
                tab.setText(_sectionViewPagerAdapter.getAdminTabsName().get(position));
            } else if (role.equals(getString(R.string.role_resident))) {
                tab.setText(_sectionViewPagerAdapter.getResidentTabsName().get(position));
            } else if (role.equals(getString(R.string.role_facility))) {
                tab.setText(_sectionViewPagerAdapter.getFacilityTabsName().get(position));
            } else {
                tab.setText(_sectionViewPagerAdapter.getGuestTabs()[position]);
            }
        }).attach();

        new Handler().postDelayed(() -> _sectionViewPagerAdapter.setOnSelectView(_tabLayout.getTabAt(0).parent, 0), 300);

//        tabCustomizationForCount();
        tabCustomizationForDot(this);
        if (_currentTabPosition == 0) {
            if (isWalkThroughEnable) openWalkThroughDialog(_currentTabPosition, sharedPreferences);
        }
        //hide chat tab temporary
        /*((ViewGroup) _tabLayout.getChildAt(0)).getChildAt(FRAGMENT_POSITION_CHAT).setVisibility(View.GONE);*/
        _tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                _currentTabPosition = tab.getPosition();
                _viewPager.setCurrentItem(_currentTabPosition);
                viewPagerCount = _viewPager.getCurrentItem();
                if (isWalkThroughEnable)
                    openWalkThroughDialog(_currentTabPosition, sharedPreferences);

                _sectionViewPagerAdapter.setOnSelectView(tab.parent, tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //refresh api call when tab selected
                if (tab.parent.getSelectedTabPosition() == 0) {
                    HomeFragment.newInstance().getNotificationCount();
                }
                HomeFragment.newInstance().notificationStatus();


                //refresh api call when tab selected
                if (tab.parent.getSelectedTabPosition() == _sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS) {
                    NotificationsFragment.newInstance().refresh(false);
                }

                if (_currentTabPosition >= 0)
                    _sectionViewPagerAdapter.setUnSelectView(_tabLayout.getTabAt(_currentTabPosition).parent, _currentTabPosition);
                updateNotificationStatus(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        MobileDataProvider.getInstance().setOnForbiddenListener(() -> {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) TabbedActivity.this);
            sharedPreferences.edit().remove("activationCode").apply();
            sharedPreferences.edit().remove("authenticationCookie").apply();
            MobileDataProvider.getInstance().setAuthenticationCookie(null);
        });

        FirebaseService.createChannelAndHandleNotifications(getApplicationContext());
        if (Utilities.checkPlayServices(this)) {
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(handler, new IntentFilter("notificationReceived"));
            startNewService();
            startService(new Intent((Context) TabbedActivity.this, ChatRegistrationIntentService.class));

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    try {
                        registrationId(task.getResult());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });

        }   // Google play services available?

        if (isFromNotification) {
            new Handler().postDelayed(() -> _viewPager.setCurrentItem(_sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS), 500);

        }
        if (isAccessPoint) {
            if (!isGuestUser) new Handler().postDelayed(() -> {
                HomeFragment.isFavAccessPoints = true;
                _viewPager.setCurrentItem(_sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS);
                AccessPointsFragment.refreshView();
            }, 500);

        }
        if (isQuickKey) {
            if (!isGuestUser) new Handler().postDelayed(() -> {
                HomeFragment.isQuickKey = true;
                _viewPager.setCurrentItem(_sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS);
            }, 500);

        }
        brivoRefreshPass(false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Check for Android 14
            askFullScreenIntentPermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void askFullScreenIntentPermission() {
        PackageManager packageManager = getPackageManager();
        boolean hasFsiPermission = packageManager.checkPermission(
                Manifest.permission.USE_FULL_SCREEN_INTENT, getPackageName())
                == PackageManager.PERMISSION_GRANTED;

        if (!hasFsiPermission) {
            // User doesn't have FSI permission, navigate to settings
            navigateToManageFsiSettings();
        }
    }

    private void navigateToManageFsiSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Check for Android 14
            // Use the original `ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT`
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    /**
     * Register Hub with fcm token and activation code for Message/Voice Hub
     *
     * @param FCM_token fcm token
     */
    private void registrationId(String FCM_token) {
        String regID;
        String resultString;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) this);

//        initializePDKLibrary(FCM_token);

        String activationCode = sharedPreferences.getString("activationCode", null);
        regID = sharedPreferences.getString("registrationID", null);
        if (regID == null) {
            NotificationHub hub = new NotificationHub(BuildConfig.HubName, BuildConfig.MsgHubListenConnectionString, (Context) this);
            registerHub(hub, activationCode, regID, sharedPreferences, FCM_token);
        } else if (!(sharedPreferences.getString("FCMtoken", "")).equals(FCM_token)) {
            // Check to see if the token has been compromised and needs refreshing.
            NotificationHub hub = new NotificationHub(BuildConfig.HubName, BuildConfig.MsgHubListenConnectionString, (Context) this);
            /*regID = hub.register(FCM_token,activationCode).getRegistrationId();*/
            registerHub(hub, activationCode, regID, sharedPreferences, FCM_token);
        } else {
            resultString = "Previously Registered Successfully - RegId : " + regID;
            Log.d(TAG, resultString + " >> FCM" + FCM_token);
        }

        String pdkActivationCode = sharedPreferences.getString("PDK_ACTIVATION_CODE", null);
        if (pdkActivationCode == null) {
//            getActivationCode();
        }
    }

/*    private void initializePDKLibrary(String fcm_token) {
        sdk = new PDKLibraryRxWrapper((Context) this, new PDKLibrary.StatusCallback() {
            @Override
            public void syncDataError(@NonNull DataSyncException e) {
                Log.e("PDK", e.getMessage());
            }

            @Override
            public void bleError(@NonNull List<BluetoothException> list) {
                Log.e("PDK", "bleError");
            }
        }, (requestor, permissions) -> {
            HashMap<String, Boolean> map = new HashMap<>();
            permissions.forEach(s -> map.put(s, true));
            return Single.just(map);
        });
        sdk.init(fcm_token);
    }*/

/*    @SuppressLint("CheckResult")
    private void redeemCredentials(String activationCode) {
        if (activationCode != null) {
            Log.e("PDK Activation Code", activationCode);
            sdk.redeemCredential(activationCode).subscribe(listResult -> {
                if (listResult.isSuccess()) {
                    sharedPreferences.edit().putString("PDK_ACTIVATION_CODE", activationCode).apply();
                }
                Log.d("PDK", "redeemCredential isSuccess " + listResult.isSuccess() + " " + listResult.getOrNull() + ", errors: " + listResult.exceptionOrNull());
            });
        }
    }*/

  /*  private void getActivationCode() {
        WebService.getInstance().getPDKActivationCode(new RestCallBack<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (response.length() > 0) {
                        new Handler().postDelayed(() -> redeemCredentials(response), 5000);
                    }
                }
            }

            @Override
            public void onFailure(WSException wse) {
                Log.e("onFailure", "getActivationCode Failed");
            }
        });
    }*/

    /**
     * Registering hub in background else will throw main thread exception
     */
    public void registerHub(NotificationHub hub, String activationCode, String regid, SharedPreferences sharedPreferences, String FCM_token) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                String registrationId = hub.register(FCM_token, activationCode).getRegistrationId();
                //Background work here
                handler.post(() -> {
                    Log.d(TAG, "New NH Registration Successfully - RegId : " + registrationId);
                    sharedPreferences.edit().putString("registrationID", registrationId).apply();
                    sharedPreferences.edit().putString("FCMtoken", registrationId).apply();
                });
            } catch (Exception e) {
                Log.e(Utilities.TAG, "Exception >> " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void getWalkThroughData(SharedPreferences sharedPreferences) {
        isRWTHome = sharedPreferences.getBoolean("isRWTHome", true);
        isGWTHome = sharedPreferences.getBoolean("isGWTHome", true);
        isRWTCoupons = sharedPreferences.getBoolean("isRWTCoupons", true);
        isGWTCoupons = sharedPreferences.getBoolean("isGWTCoupons", true);
        isRWTAccessPoint = sharedPreferences.getBoolean("isRWTAccessPoint", true);
        isRWTDigitalKey = sharedPreferences.getBoolean("isRWTDigitalKey", true);
        isRWTServiceKey = sharedPreferences.getBoolean("isRWTServiceKey", true);
        isGWTDigitalKey = sharedPreferences.getBoolean("isGWTDigitalKey", true);
        isRWTNotifictions = sharedPreferences.getBoolean("isRWTNotifictions", true);
        isGWTNotifictions = sharedPreferences.getBoolean("isGWTNotifictions", true);
        isRWTRenterTools = sharedPreferences.getBoolean("isRWTRenterTools", true);
        isRWTHealthAndWellness = sharedPreferences.getBoolean("isRWTHealthAndWellness", true);
        isGWTHealthAndWellness = sharedPreferences.getBoolean("isGWTHealthAndWellness", true);
        isRWTVoiceMail = sharedPreferences.getBoolean("isRWTVoiceMail", true);
        isRWTBulletinBoard = sharedPreferences.getBoolean("isRWTBulletinBoard", true);
        isRWTAmenities = sharedPreferences.getBoolean("isRWTAmenities", true);
        isRWTTopic = sharedPreferences.getBoolean("isRWTTopic", true);
    }

    private void openWalkThroughDialog(int position, SharedPreferences sharedPreferences) {
        if (isGuestUser) {
            if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS) {
                if (isGWTCoupons) if (AllCouponsFragment.newInstance()._adapter != null)
                    AllCouponsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS) {
                if (isGWTDigitalKey) if (GuestDigitalKeysFragment.newInstance()._adapter != null)
                    GuestDigitalKeysFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS) {
                if (isGWTNotifictions) if (NotificationsFragment.newInstance()._adapter != null)
                    NotificationsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_HEALTH) {
                if (isGWTHealthAndWellness) if (HealthFragment.newInstance()._adapter != null)
                    HealthFragment.newInstance()._adapter.notifyDataSetChanged();
            }
        } else if (role.equals(TabbedActivity.tabbedActivity.getString(R.string.role_leasing_officer)) || role.equals(TabbedActivity.tabbedActivity.getString(R.string.role_property_manager))) {

            if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS) {
                if (isRWTAccessPoint) if (AccessPointsFragment.newInstance()._adapter != null)
                    AccessPointsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_SERVICE_KEYS) {
                if (isRWTServiceKey) if (ServiceKeysFragment.newInstance()._adapter != null)
                    ServiceKeysFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS) {
                if (isRWTCoupons) if (AllCouponsFragment.newInstance()._adapter != null)
                    AllCouponsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS) {
                if (isRWTDigitalKey) if (AllDigitalKeysFragment.newInstance()._adapter != null)
                    AllDigitalKeysFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS) {
                if (isRWTNotifictions) if (NotificationsFragment.newInstance()._adapter != null)
                    NotificationsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_RENTAL_TOOL) {
                if (isRWTRenterTools) RentalToolFragment.newInstance().displayWalkThrough();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_HEALTH) {
                if (isRWTHealthAndWellness) if (HealthFragment.newInstance()._adapter != null)
                    HealthFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_VOICE_MAIL) {
                if (isRWTVoiceMail) if (VoiceMailFragment.newInstance()._adapter != null)
                    VoiceMailFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_BILLBOARD) {
                if (isRWTBulletinBoard) if (BillBoardFragment.newInstance()._adapter != null)
                    BillBoardFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT) {
                if (isRWTTopic) if (GeneralChatAdminFragment.newInstance().viewpager != null)
                    if (GeneralChatAdminFragment.newInstance().viewpager.getAdapter() != null)
                        GeneralChatAdminFragment.newInstance().viewpager.getAdapter().notifyDataSetChanged();
            }

        } else if (role.equals(TabbedActivity.tabbedActivity.getString(R.string.role_vendor))) {
            if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS) {
                if (isRWTAccessPoint) if (AccessPointsFragment.newInstance()._adapter != null)
                    AccessPointsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS) {
                if (isGWTCoupons) if (AllCouponsFragment.newInstance()._adapter != null)
                    AllCouponsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS) {
                if (isGWTDigitalKey) if (GuestDigitalKeysFragment.newInstance()._adapter != null)
                    GuestDigitalKeysFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS) {
                if (isGWTNotifictions) if (NotificationsFragment.newInstance()._adapter != null)
                    NotificationsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_VOICE_MAIL) {
                if (isRWTVoiceMail) if (VoiceMailFragment.newInstance()._adapter != null)
                    VoiceMailFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_BILLBOARD) {
                if (isRWTBulletinBoard) if (BillBoardFragment.newInstance()._adapter != null)
                    BillBoardFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT) {
                if (isRWTTopic) if (GeneralChatFragment.newInstance()._adapter != null)
                    GeneralChatFragment.newInstance()._adapter.notifyDataSetChanged();
            }
        } else {
            if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS) {
                if (isRWTAccessPoint) if (AccessPointsFragment.newInstance()._adapter != null)
                    AccessPointsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS) {
                if (isRWTCoupons) if (AllCouponsFragment.newInstance()._adapter != null)
                    AllCouponsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS) {
                if (isRWTDigitalKey) if (AllDigitalKeysFragment.newInstance()._adapter != null)
                    AllDigitalKeysFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS) {
                if (isRWTNotifictions) if (NotificationsFragment.newInstance()._adapter != null)
                    NotificationsFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_RENTAL_TOOL) {
                if (isRWTRenterTools) RentalToolFragment.newInstance().displayWalkThrough();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_HEALTH) {
                if (isRWTHealthAndWellness) if (HealthFragment.newInstance()._adapter != null)
                    HealthFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_VOICE_MAIL) {
                if (isRWTVoiceMail) if (VoiceMailFragment.newInstance()._adapter != null)
                    VoiceMailFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_BILLBOARD) {
                if (isRWTBulletinBoard) if (BillBoardFragment.newInstance()._adapter != null)
                    BillBoardFragment.newInstance()._adapter.notifyDataSetChanged();
            } else if (position == _sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT) {
                if (isRWTTopic) if (GeneralChatFragment.newInstance()._adapter != null)
                    GeneralChatFragment.newInstance()._adapter.notifyDataSetChanged();
            }

        }
        getWalkThroughData(sharedPreferences);
    }

    @Override
    public void onRevokeClicked(DigitalKey item, AllDigitalKeysFragment digitalKeysFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        builder.setMessage("Are you sure you want to revoke " + item.getRecipient() + " digital key?").setTitle("Revoke Digital Key");
        builder.setNegativeButton("No", (dialog, id) -> {
        });
        builder.setPositiveButton("Yes", (dialog, id) -> {
            DigitalKeyUpdate model = new DigitalKeyUpdate();
            model.id = item.id;
            model.isRevoked = true;

            WebService.getInstance().revokeDigitalKey(model, new RestEmptyCallBack<ResponseBody>() {
                @Override
                public void onResponse(ResponseBody response) {
                    digitalKeysFragment.refresh();
                    Toast.makeText((Context) TabbedActivity.this, item.getRecipient() + " digital key was successfully revoked.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(WSException wse) {
                    Toast.makeText((Context) TabbedActivity.this, item.getRecipient() + " digital key failed to be revoked." + " Please try again later.", Toast.LENGTH_LONG).show();
                }
            });
        });
        builder.create().show();
    }

    @Override
    public void onRenewClicked(DigitalKey item, AllDigitalKeysFragment digitalKeysFragment) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog((Context) this);
        bottomSheetDialog.setContentView(R.layout.bottom_renew_layout);

        TextView tv3Days = bottomSheetDialog.findViewById(R.id.tv3Days);
        TextView tv5Days = bottomSheetDialog.findViewById(R.id.tv5Days);
        TextView tv7Days = bottomSheetDialog.findViewById(R.id.tv7Days);
        TextView tvCancel = bottomSheetDialog.findViewById(R.id.tvCancel);

        tv3Days.setOnClickListener(view -> {
            callAPIForRenewKey(item, 3, digitalKeysFragment);
            bottomSheetDialog.dismiss();
        });

        tv5Days.setOnClickListener(view -> {
            callAPIForRenewKey(item, 5, digitalKeysFragment);
            bottomSheetDialog.dismiss();
        });

        tv7Days.setOnClickListener(view -> {
            callAPIForRenewKey(item, 7, digitalKeysFragment);
            bottomSheetDialog.dismiss();
        });

        tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }


    @Override
    public void onListFragmentInteraction(final Notification item, boolean isForImage) {
        if (isForImage) {
            MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
            maintenanceRequesFiles.isImage = true;
            maintenanceRequesFiles.maintenanceRequestImageSrc = item.imageUrl;
            Intent i = new Intent((Context) TabbedActivity.this, SingleVideoImageActivity.class);
            i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
            startActivity(i);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_notification, null);
            builder.setView(view).setTitle(item.title);

            DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
            ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(item.createdUtc));
            ((TextView) view.findViewById(R.id.message)).setText(item.message);

            NotificationsFragment.newInstance().refresh(false);

            builder.setPositiveButton("Dismiss", (dialog, id) -> {
            });
            builder.setCancelable(false).create().show();
        }

    }

    @Override
    public void onListFragmentInteraction(final MaintenanceRequestResponse item) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_maintenance_request, null);
        builder.setView(view).setTitle(item.getTitle());

        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(item.createdUtc));
        ((TextView) view.findViewById(R.id.status)).setText(item.getStatus().toString());
        ((TextView) view.findViewById(R.id.description)).setText(item.getDescription());

        if (item.getNeedPermission()) {
            ((TextView) view.findViewById(R.id.permission)).setText("No, follow up");
        } else {
            ((TextView) view.findViewById(R.id.permission)).setText("Ok to enter");
        }


        view.findViewById(R.id.closedUtcLabel).setVisibility(item.getStatus() == MaintenanceRequest.Status.Closed ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.closedUtc).setVisibility(item.getStatus() == MaintenanceRequest.Status.Closed ? View.VISIBLE : View.GONE);
        if (item.getStatus() == MaintenanceRequest.Status.Closed)
            ((TextView) view.findViewById(R.id.closedUtc)).setText(formatter.format(item.getClosedUtc()));

        //recyclerview set
        if (item.getMaintenanceRequestFiles().size() > 0) {
            view.findViewById(R.id.tvUploadImaged).setVisibility(View.VISIBLE);
            view.findViewById(R.id.nsImages).setVisibility(View.VISIBLE);
            MRSImagesVideosDisplayAdapter mrsImagesVideosDisplayAdapter = new MRSImagesVideosDisplayAdapter((Context) TabbedActivity.this, item.getMaintenanceRequestFiles(), this);
            ((RecyclerView) view.findViewById(R.id.rvPriorImages)).setAdapter(mrsImagesVideosDisplayAdapter);
            mrsImagesVideosDisplayAdapter.refresh(item.getMaintenanceRequestFiles());
        }


        builder.setPositiveButton("Dismiss", (dialog, id) -> {
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == _sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS) && resultCode > 0) {
            ((IRefreshableFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + _viewPager.getCurrentItem())).refresh();
        }
    }

    public void setSelectedTab(int index) {
        _tabLayout.getTabAt(index).select();
    }

    @Override
    public void onListFragmentInteraction(VoiceMail item) {

    }

    @Override
    public void watchVideo(final VoiceMail item) {
        Dexter.withActivity((Activity) TabbedActivity.this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    Intent i = new Intent((Context) TabbedActivity.this, PlayerActivity.class);
                    i.putExtra("URL", item.videoName);
                    startActivity(i);
                }

                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    showSettingsDialog(report);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();
    }

    private void showSettingsDialog(MultiplePermissionsReport report) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        builder.setTitle("Need Permissions");
//        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        StringBuilder permission = new StringBuilder();
        for (int i = 0; i < report.getDeniedPermissionResponses().size(); i++) {
            permission.append(report.getDeniedPermissionResponses().get(i).getPermissionName()).append(", ");
        }

        builder.setMessage("This app needs permissions to use this feature. You can grant them in app settings." + "\nPermission Name(s) :" + permission);
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openBlueToothSettings() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    private void navigateToPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void watchVideo(HealthVideo item, long id, String createdUtc) {
        /*Health and Wellness Video view*/
        Intent i = new Intent((Context) TabbedActivity.this, HealthPlayerActivity.class);
        i.putExtra("HEALTHVIEDEOITEM", item);
        i.putExtra("VIDEOID", String.valueOf(id));
        i.putExtra("VIDEODATE", String.valueOf(createdUtc));
        startActivity(i);
    }

    @Override
    public void onListFragmentInteraction(BulletinBoard item) {
        Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
        intent.putExtra("GeneralDetails", new Gson().toJson(item));
        startActivity(intent);
    }

    @Override
    public void onListFragmentForEditInteraction(BulletinBoard item) {
        Intent intent = new Intent();
        int requestCode;
        if (item.isService) {
            intent = new Intent(getApplicationContext(), ServiceActivity.class);
            requestCode = 1;
        } else {
            intent = new Intent(getApplicationContext(), SellActivity.class);
            requestCode = 2;
        }
        intent.putExtra("forEdit", new Gson().toJson(item));

        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onListFragmentInteraction(GuestDigitalKey item, GuestDigitalKeysFragment guestDigitalKeysFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_digital_key, null);

        builder.setView(view).setTitle(item.recipient);

        String status = "Expired";
        if (item.isRevoked) status = "Revoked";
        else {
            Date now = new Date();
            if (now.after(item.fromUtc) && now.before(item.toUtc)) status = "Valid";
            else if (now.before(item.fromUtc) && now.before(item.toUtc)) status = "Upcoming";
        }
        ((TextView) view.findViewById(R.id.status)).setText(status);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(item.createdUtc));
        ((TextView) view.findViewById(R.id.fromUtc)).setText(formatter.format(item.fromUtc));
        ((TextView) view.findViewById(R.id.toUtc)).setText(formatter.format(item.toUtc));
        view.findViewById(R.id.notes_label).setVisibility(TextUtils.isEmpty(item.notes) ? View.GONE : View.VISIBLE);
        ((TextView) view.findViewById(R.id.notes)).setText(item.notes);
        view.findViewById(R.id.tvKeyTitle).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tvKey).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.tvKey)).setText(item.key);
        ImageView ivQRCode = view.findViewById(R.id.ivQrCode);
        if (item.qrCodeSrc != null) {
            ivQRCode.setVisibility(View.VISIBLE);
        } else {
            ivQRCode.setVisibility(View.GONE);
        }

        ivQRCode.setOnClickListener(v -> {
            MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
            maintenanceRequesFiles.isImage = true;
            maintenanceRequesFiles.isFromAd = false;
            maintenanceRequesFiles.maintenanceRequestImageSrc = item.qrCodeSrc;
            Intent i = new Intent((Context) TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
            i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
            startActivity(i);
        });

        if (status.equals("Valid")) {
            if (item.mapUrl.length() > 0) builder.setNegativeButton("View Map", (dialog, id) -> {
                Intent intent = new Intent((Context) TabbedActivity.this, GuestDigitalKeyMapActivity.class);
                intent.putExtra("imageMap", item.mapUrl);
                intent.putExtra("imageMaps", item.mapUrls);
                startActivity(intent);
            });
        }

        builder.setPositiveButton("Dismiss", (dialog, id) -> {
        });

        builder.setNeutralButton("Download Pass", (dialog, which) -> {
            callAPIPrintPass(item.id);
        });
        builder.create().show();
    }

    private void callAPIPrintPass(long id) {
        ProgressDialog.showProgress(this);
        WebService.getInstance().getParkingPass(id, new RestCallBack<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replace("data:image/jpeg;base64,", "");
                byte[] imageAsBytes = Base64.decode(response.getBytes(), 0);
                Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//                createPDF(bmp, id);
                saveImage(bmp, id);
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
            }
        });
    }

    private void saveImage(Bitmap bmp, long id) {
        try {
            //Create Path to save Image
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Invictus"); //Creates app specific folder
            path.mkdirs();
            File imageFile = new File(path, "Guest_pass_" + id + ".png"); // Imagename.png
            FileOutputStream out = new FileOutputStream(imageFile);

            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            Toast.makeText((Context) this, "Pass Downloaded!", Toast.LENGTH_LONG).show();
            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/png");
            MediaScannerConnection.scanFile((Context) this, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);

                    ProgressDialog.dismissProgress();
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share Image"));
                }
            });
        } catch (IOException e) {
            ProgressDialog.dismissProgress();
            e.printStackTrace();
        }

    }

    private void createPDF(Bitmap bitmap, long id) {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Invictus".toString();
//        final File file = new File(path, "GUEST_PASS_" + id + ".pdf");
        new Thread(() -> {
            PdfDocument document = new PdfDocument();
            //  int height = 842;
            //int width = 595;
            int height = 1010;
            int width = 714;
            int reqH, reqW;
            reqW = width;

            reqH = width * bitmap.getHeight() / bitmap.getWidth();
            Log.e("reqH", "=" + reqH);
            if (reqH > height) {
                reqH = height;
                reqW = height * bitmap.getWidth() / bitmap.getHeight();
                Log.e("reqW", "=" + reqW);
                //   bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
            }
            // Compress image by decreasing quality
            // ByteArrayOutputStream out = new ByteArrayOutputStream();
            //  bitmap.compress(Bitmap.CompressFormat.WEBP, 50, out);
            //    bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            //bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
            //Create an A4 sized page 595 x 842 in Postscript points.
            //PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(reqW, reqH, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            Log.e("PDF", "pdf = " + bitmap.getWidth() + "x" + bitmap.getHeight());
            canvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);
            FileOutputStream fos;
            File directory = new File(path);
            directory.mkdirs();
            File file = new File(directory, "GUEST_PASS_" + id + ".pdf");
            try {
//                fos = new FileOutputStream(file);
                fos = new FileOutputStream(file);
                document.writeTo(fos);
                document.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                Toast.makeText((Context) TabbedActivity.this, "PDF Created", Toast.LENGTH_LONG).show();
                ProgressDialog.dismissProgress();

                Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.setDataAndType(uri, "application/pdf");
                try {
                    startActivity(pdfOpenintent);
                } catch (ActivityNotFoundException e) {

                }
            });
        }).start();
    }

    @Override
    public void onListFragmentInteraction(final DigitalKey item, AllDigitalKeysFragment digitalKeysFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_digital_key, null);

        builder.setView(view).setTitle(item.getRecipient());

        String status = "Expired";
        if (item.isRevoked()) status = "Revoked";
        else {
            Date now = new Date();
            if (now.after(item.getFromUtc()) && now.before(item.getToUtc())) status = "Valid";
            else if (now.before(item.getFromUtc()) && now.before(item.getToUtc()))
                status = "Upcoming";
        }

        TextView tvStatus = view.findViewById(R.id.status);
        tvStatus.setText(status);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(item.createdUtc));
        ((TextView) view.findViewById(R.id.fromUtc)).setText(formatter.format(item.getFromUtc()));
        ((TextView) view.findViewById(R.id.toUtc)).setText(formatter.format(item.getToUtc()));
        view.findViewById(R.id.notes_label).setVisibility(TextUtils.isEmpty(item.getNotes()) ? View.GONE : View.VISIBLE);
        ((TextView) view.findViewById(R.id.notes)).setText(item.getNotes());
        if (item.isQuickKey()) {
            if (status.equals("Valid")) {
                view.findViewById(R.id.tvKeyTitle).setVisibility(View.VISIBLE);
                view.findViewById(R.id.tvKey).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ivQrCode).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.tvKey)).setText(item.getKey());
                builder.setNeutralButton("COPY", (dialog, which) -> {
                    Utilities.addHaptic(tvStatus);
                    ClipboardManager clipboard = (ClipboardManager) TabbedActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    long diff = item.getToUtc().getTime() - item.getFromUtc().getTime();
                    int hours = (int) (diff / (1000 * 60 * 60));
                    String keyText = "Delivery Quick Key is " + item.getKey() + "\nFind " + HomeFragment.userName + " in the DIRECTORY " + "of the kiosk and enter this key. Key expires in 90 mins.";
//                    String keyText = HomeFragment.userName + " sent you this quick key:\n"
//                            + item.getKey() + " \n" +
//                            "Find " + HomeFragment.userName + " using the directory button of the" +
//                            " kiosk to enter this key. \n"
//                            + "Key expires in " + hours + " hours ("
//                            + convertTime(item.getToUtc()) + ")";
                    ClipData clip = ClipData.newPlainText("Key", keyText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText((Context) TabbedActivity.this, "Text copied. Enter this quick key in my DIRECTORY PROFILE on the kiosk.", Toast.LENGTH_LONG).show();
                });
            }
        }

        builder.setPositiveButton("Dismiss", (dialog, id) -> {
        });


        if (!item.isQuickKey()) {
            builder.setNeutralButton("Download Pass", (dialog, which) -> {
                callAPIPrintPass(item.id);
            });
        }
        builder.create().show();
    }

    private String convertTime(Date datePasssed) {
        /*"10:30 PM"*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm a");
        return displayFormat.format(datePasssed);
    }

    private String convertDate(Date datePasssed) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        return displayFormat.format(datePasssed);
    }

    public void updateCount(int count) {
        TabLayout.Tab tab = _tabLayout.getTabAt(_sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS);
        if (tab != null) {
            TextView tabDot = tab.getCustomView().findViewById(R.id.tabDot);
            if (count > 0) {
                tabDot.setVisibility(View.VISIBLE);
            } else {
                tabDot.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        OpenpathMobileAccessCore.getInstance().onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted location permission
                // Now check if android version >= 11, if >= 11 check for Background Location Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Background Location Permission is granted so do your work here
                    } else {
                        // Ask for Background Location Permission
                        askPermissionForBackgroundUsage();
                    }
                }
            } else {
                // User denied location permission
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted for Background Location Permission.
            } else {
                // User declined for Background Location Permission.
            }
        }
        OpenpathMobileAccessCore.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }


    private int LOCATION_PERMISSION_CODE = 1;
    private int BACKGROUND_LOCATION_PERMISSION_CODE = 2;

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Background Location Permission is granted so do your work here
                } else {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage();
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission();
        }
    }

    private void askForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder((Context) this).setTitle("Permission Needed!").setMessage("Location Permission Needed!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(TabbedActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Permission is denied by the user
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private void askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new AlertDialog.Builder((Context) this).setTitle("Permission Needed!").setMessage("Invictus LifeStyle collects Background Location for unlocking door even when app is closed or not in use," + " tap \"Allow all time in the next screen\"").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(TabbedActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_CODE);
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User declined for Background Location Permission.
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_CODE);
        }
    }


    private void askPermissionOpenPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext((Activity) this).withPermissions(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        //nothing
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        boolean permission = false;
                        for (int i = 0; i < report.getDeniedPermissionResponses().size(); i++) {
                            if (report.getDeniedPermissionResponses().get(i).getPermissionName().equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                permission = true;
                            }
                        }
                        if (!permission) {
                            showSettingsDialog(report);
                        }
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).onSameThread().check();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Dexter.withContext((Activity) this).withPermissions(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        //nothing
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        boolean permission = false;
                        for (int i = 0; i < report.getDeniedPermissionResponses().size(); i++) {
                            if (report.getDeniedPermissionResponses().get(i).getPermissionName().equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                permission = true;
                            }
                        }
                        if (!permission) {
                            showSettingsDialog(report);
                        }
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).onSameThread().check();
        } else {
            Dexter.withContext((Activity) this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        //nothing
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog(report);
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).onSameThread().check();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Offline.value()));
        isVisible = false;
        isScreenVisible = false;
        try {
            Wearable.getDataClient((Context) this).removeListener(this);
            Wearable.getMessageClient((Context) this).removeListener(this);
            Wearable.getCapabilityClient((Context) this).removeListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Wearable.getDataClient((Context) this).addListener(this);
            Wearable.getCapabilityClient((Context) this).addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
            Wearable.getMessageClient((Context) this).addListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Helper.isAppRunning(TabbedActivity.this, "net.invictusmanagement.invictuslifestyle")) {
            updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Active.value()));
        } else {
            updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Offline.value()));
        }
        isVisible = true;
        isScreenVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
        isScreenVisible = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Active.value()));
        isVisible = true;
        isScreenVisible = true;
    }


    @Override
    public void showVideoImage(int position, MaintenanceRequesFiles item) {
        Intent i = new Intent((Context) TabbedActivity.this, SingleVideoImageActivity.class);
        i.putExtra("files", new Gson().toJson(item));
        startActivity(i);
    }

    public void forceUpdateCheck() {
        ForceUpdateCheck forceUpdateCheck = new ForceUpdateCheck();
        forceUpdateCheck.isAndroid = true;
        forceUpdateCheck.appVersion = "";

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                /*_swipeRefreshLayout.setRefreshing(true);*/
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().forceUpdateCheck(forceUpdateCheck);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    if (forceUpdateCheckList.size() != 0) {
                        boolean isForceUpdate = forceUpdateCheckList.get(0).forceUpdate;
                        boolean notifyForUpdate = forceUpdateCheckList.get(0).notifyForUpdate;
                        String updateLink = forceUpdateCheckList.get(0).appUpdateLink;
                        String apiAppVersion = forceUpdateCheckList.get(0).appVersion;
                        String appVersion = "";
                        try {
                            appVersion = TabbedActivity.tabbedActivity.getPackageManager().getPackageInfo(TabbedActivity.tabbedActivity.getPackageName(), 0).versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        boolean checkForUpdate = Double.parseDouble(apiAppVersion) > Double.parseDouble(appVersion);
                        /*boolean checkForUpdate = !apiAppVersion.equals(appVersion);*/
                        if (isForceUpdate) {
                            if (checkForUpdate) {
                                AlertDialogForceUpdate(updateLink);
                            }
                        } else {
                            if (notifyForUpdate) {
                                if (checkForUpdate) {
                                    AlertDialogNotifyUpdate(updateLink);
                                }
                            }
                        }
                    }
                }
            }
        }.execute();
    }

    private void AlertDialogForceUpdate(String upateLink) {
        if (!isFinishing()) {
            forceUpdateBuilder = new AlertDialog.Builder((Context) TabbedActivity.this).setCancelable(false).setTitle("It's time to update!").setMessage("We've revamped Invictus to make it easier to use! Tap below to update now.").setCancelable(false).setPositiveButton("Update", (arg0, arg1) -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(upateLink));
                startActivity(i);
                finishAffinity();
            }).create();
            forceUpdateBuilder.show();
        }
    }

    private void AlertDialogNotifyUpdate(String updateLink) {
        if (!isFinishing()) {
            forceUpdateBuilder = new AlertDialog.Builder((Context) TabbedActivity.this).setCancelable(false).setTitle("New update available!").setMessage("There's a new version of Invictus! Would you like to update?").setCancelable(true).setPositiveButton("Update", (arg0, arg1) -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(updateLink));
                startActivity(i);
            }).setNegativeButton("Later", (dialog, which) -> {
                //dismiss dialog
            }).create();
            forceUpdateBuilder.show();
        }

    }

    public void AlertDialogSurvey() {
        if (!isFinishing()) {
            surveyBuilder = new AlertDialog.Builder((Context) TabbedActivity.this).setCancelable(false).setTitle("New survey available!").setMessage("There's a new survey available! Would you like to take?").setCancelable(true).setPositiveButton("Take it!", (arg0, arg1) -> {
                surveyBuilder.dismiss();
                Intent i = new Intent((Context) TabbedActivity.this, ChooseSurveyActivity.class);
                startActivity(i);
            }).setNegativeButton("Answer Later", (dialog, which) -> {
                //dismiss dialog
                surveyBuilder.dismiss();
            }).create();
            if (forceUpdateBuilder != null) {
                if (!forceUpdateBuilder.isShowing()) surveyBuilder.show();
            } else {
                surveyBuilder.show();
            }
        }
    }

    public void AlertDialogRating(int id, String title) {
        if (!isFinishing()) {
            LayoutInflater inflater = (TabbedActivity.this).getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_rate_maintenance, null);
            ratingBuilder = new AlertDialog.Builder((Context) TabbedActivity.this).setCancelable(false).setView(view).setCancelable(true).create();

            TextView tvTitle = view.findViewById(R.id.tvTitle);
            EditText edDescription = view.findViewById(R.id.edReview);
            RatingBar ratingBar = view.findViewById(R.id.ratingBarMRS);

            tvTitle.setText(title);
            view.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
                //submit form
                if (ratingBar.getRating() <= 0) {
                    Utilities.hideKeyboard(TabbedActivity.tabbedActivity);
                    Toast.makeText((Context) TabbedActivity.tabbedActivity, "Please give star rating to maintenance request service", Toast.LENGTH_LONG).show();
                } else {
                    callApiForSubmitReview(String.valueOf(id), ratingBar.getRating(), edDescription.getText().toString());
                }
            });

            view.findViewById(R.id.btnCancel).setOnClickListener(v -> {
                //dismiss dialog
                callApiForSubmitReview(String.valueOf(id), 0, "");
//                    ratingBuilder.dismiss();
            });

            if (forceUpdateBuilder != null) {
                if (!forceUpdateBuilder.isShowing()) ratingBuilder.show();
            } else {
                ratingBuilder.show();
            }
        }
    }

    private void callApiForSubmitReview(String id, float rating, String s) {
        MRSRating model = new MRSRating(id, rating, s);
        WebService.getInstance().updateRatingMRS(model, new RestEmptyCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                Utilities.hideKeyboard(TabbedActivity.tabbedActivity);
                Toast.makeText((Context) TabbedActivity.tabbedActivity, "Rating submitted successfully.", Toast.LENGTH_LONG).show();

                ratingBuilder.dismiss();
            }

            @Override
            public void onFailure(WSException wse) {
                Utilities.hideKeyboard(TabbedActivity.tabbedActivity);
                Toast.makeText((Context) TabbedActivity.tabbedActivity, "Failed to submit ratings", Toast.LENGTH_LONG).show();

                ratingBuilder.dismiss();
            }
        });
    }

    public void openPathEntryOpen(long id) {
        if (isOpenPathInit) {
            if (hasRemoteUnlock) {
                OpenpathMobileAccessCore.getInstance().unlock("entry", Integer.parseInt(String.valueOf(id)), (int) System.currentTimeMillis(), 5000);
            } else {
                Toast.makeText((Context) TabbedActivity.tabbedActivity, "Please allow remote unlock from openpath portal to access this.", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText((Context) TabbedActivity.tabbedActivity, "Please try again after some time.", Toast.LENGTH_LONG).show();
        }

    }

//    private void waitForSlowConnection(final CancellationSignal cancellationSignal) {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                cancellationSignal.cancel();
//                unlockFailure(true);
//            }
//        }, 30000);
//    }

    @Override
    public void onSlideUnlockTapped(final int position, final AccessPoint item, final AccessPointsFragment accessPointsFragment) {
//        ProgressDialog.showProgress(TabbedActivity.this);
        _accessPoint = item;
        _accessPointPosition = position;
        _accessPointFragment = accessPointsFragment;
        _guestAccessPointFragment = null;
        String accessPointId = String.valueOf(item.id);
        if (item.getOperator() == Utilities.OPERATOR_BRIVO) {
            unlockBrivoDoor();
        } else if (item.getOperator() == Utilities.OPERATOR_PDK && !isGuestUser) {
//            unlockPDKDoor();
        } else if (item.getOperator() == Utilities.OPERATOR_OPENPATH && !isGuestUser) {
            unlockOpenPathDoor();
        } else {
            unlockAccessPoint(item, position);
        }

    }

    private void unlockOpenPathDoor() {
        String accessPointId = String.valueOf(_accessPoint.id);
        if (isOpenPathInit) {
            if (hasRemoteUnlock) {
                OpenpathMobileAccessCore.getInstance().unlock("entry", Integer.parseInt(accessPointId), (int) System.currentTimeMillis(), 5000);
            } else {
                unlockFailure(true, accessPointId);
//                        ProgressDialog.dismissProgress();
                Toast.makeText((Context) TabbedActivity.tabbedActivity, "Please allow remote unlock from openpath portal to access this.", Toast.LENGTH_LONG).show();
            }

        } else {
            unlockFailure(true, accessPointId);
            ProgressDialog.dismissProgress();
            Toast.makeText((Context) TabbedActivity.tabbedActivity, "Please try again after some time.", Toast.LENGTH_LONG).show();
        }
    }

//    private void unlockPDKDoor() {
//
//        String accessPointId = String.valueOf(_accessPoint.id);
//        if (sdk == null) return;
//
//        sdk.open(_accessPoint.getPdkPanelId(), _accessPoint.id).subscribe(unitResult -> {
//            Log.d("PDK", "open the door result " + unitResult.isSuccess() + " " + unitResult.getOrNull() + ", errors: " + unitResult.exceptionOrNull());
//            if (unitResult.isSuccess()) {
//                unlockSuccessMethod(true, accessPointId);
//            } else {
//                unlockFailure(true, accessPointId);
//            }
//        });
//    }

    @Override
    public void onGuestSlideUnlockTapped(int position, AccessPoint item, GuestAccessPointsFragment accessPointsFragment) {
        _accessPoint = item;
        _accessPointPosition = position;
        _accessPointFragment = null;
        _guestAccessPointFragment = accessPointsFragment;
        if (item.getOperator() == Utilities.OPERATOR_BRIVO) {
            unlockBrivoDoor();
        } else {
            unlockAccessPoint(item, position);
        }
    }

    @Override
    public void onListFragmentInteraction(final AccessPoint item, final int position, final AccessPointsFragment accessPointsFragment) {
    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    private void unlockBrivoDoor() {
        if (!isBluetoothEnabled()) {
            showDialogToEnableBluetooth();
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showDialogToEnableLocation();
        } else {

            String accessPointId = String.valueOf(_accessPoint.id);
            String passId = BrivoSampleConstants.PASS_ID;
            try {

                BrivoSDKOnair.getInstance().retrieveSDKLocallyStoredPasses(new IOnRetrieveSDKLocallyStoredPassesListener() {
                    @Override
                    public void onSuccess(@Nullable LinkedHashMap<String, BrivoOnairPass> linkedHashMap) {
                        for (BrivoOnairPass passes : linkedHashMap.values()) {
                            if (passes.getPassId().equals(passId)) {
                                for (BrivoSite sites : passes.getSites()) {
                                    for (BrivoAccessPoint accessPoints : sites.getAccessPoints()) {
                                        if (accessPoints != null) {
                                            if (accessPoints.getId().equals(accessPointId)) {
                                                AccessPointPath path = new AccessPointPath(accessPointId, String.valueOf(sites.getId()), passes.getPassId());

                                                String readerId = null;
                                                if (accessPoints.getBluetoothReader() != null) {
                                                    if (accessPoints.getBluetoothReader().getReaderUid() != null) {
                                                        readerId = accessPoints.getBluetoothReader().getReaderUid();
                                                    }
                                                }

                                                BrivoSelectedAccessPoint accessPoint = new BrivoSelectedAccessPoint(path, accessPoints.getDoorType(), passes.getBrivoOnairPassCredentials(), accessPoints.isTwoFactorEnabled(), 0, readerId, passes.getBleCredential(), "");
                                                unlockBrivoDoor(accessPoint);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailed(@NonNull BrivoError brivoError) {
                        isAccessPointFailed = true;
                        unlockFailure(true, accessPointId);
                    }
                });
            } catch (BrivoSDKInitializationException e) {
                e.printStackTrace();
                unlockFailure(true, accessPointId);
            }
        }
    }

    private void unlockBrivoDoor(BrivoSelectedAccessPoint accessPoint) {

        //Pass null for SDK to handle cancellation timeout
        //else provide cancellation signal with custom handling from code end
//                CancellationSignal signal = new CancellationSignal();
//                waitForSlowConnection(signal);
        String accessPointId = accessPoint.getAccessPointPath().getAccessPointId();
        try {
            BrivoSDKAccess.getInstance().unlockAccessPoint(accessPoint, null, result -> {
                if (result.getCommunicationState() == AccessPointCommunicationState.SUCCESS) {
                    isAccessPointFailed = false;
                    unlockSuccessMethod(true, accessPointId);
                } else if (result.getCommunicationState() == AccessPointCommunicationState.FAILED) {
                    if (result.getError() != null) {
                        if (result.getError().getCode() == BrivoBLEErrorCodes.BLE_LOCATION_DISABLED_ON_DEVICE) {
                            Toast.makeText((Context) TabbedActivity.this, "Location is disable on you device, Please enable it.", Toast.LENGTH_LONG).show();
                        }
                        if (!isAccessPointFailed && (result.getError().getCode() == 403 || result.getError().getCode() == -1003)) {
                            isAccessPointFailed = true;
                            brivoRefreshPass(true);
                        } else {
                            isAccessPointFailed = false;
                            unlockFailure(true, accessPointId);
                        }
                    } else {
                        isAccessPointFailed = false;
                        unlockFailure(true, accessPointId);
                    }

                }
            });
        } catch (BrivoSDKInitializationException e) {
            e.printStackTrace();
            isAccessPointFailed = true;
            unlockFailure(true, accessPointId);
        }
    }

    private void showDialogToEnableBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        builder.setTitle("Enable Bluetooth");
        builder.setMessage("Please enable Bluetooth.");
        builder.setPositiveButton("Turn On", (dialog, which) -> {
            dialog.cancel();
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.enable();
            new Handler().postDelayed(() -> unlockBrivoDoor(), 2000);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDialogToEnableLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        builder.setTitle("Enable Location");
        builder.setMessage("We will require location permission. Please grant it");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            navigateToPermissionSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void brivoRefreshPass(boolean unlock) {
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
                            if (unlock) unlockBrivoDoor();
                        } else {
                            callAPIForGetAccessCode(unlock);
                        }
                    }

                    @Override
                    public void onFailed(BrivoError error) {
                        Log.e("BrivoSDKOnair > ", "refreshPass > " + error.getMessage());
                        callAPIForGetAccessCode(unlock);
                    }
                });
            }
        } catch (BrivoSDKInitializationException e) {
            callAPIForGetAccessCode(unlock);
        }
    }

    private void callAPIForGetAccessCode(boolean unlock) {
        ProgressDialog.showProgress((Context) TabbedActivity.this);
        WebService.getInstance().getAccessCode(new RestCallBack<AccessCodeResponse>() {
            @Override
            public void onResponse(AccessCodeResponse response) {
                ProgressDialog.dismissProgress();
                if (response != null) brivoRedeemPass(response, unlock);
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
            }
        });
    }

    private void brivoRedeemPass(AccessCodeResponse codeResponse, boolean unlock) {
        try {
            BrivoSDKOnair.getInstance().redeemPass(codeResponse.getReferenceId(), codeResponse.getAccessCode(), new IOnRedeemPassListener() {
                @Override
                public void onSuccess(BrivoOnairPass pass) {
                    //Manage pass
                    BrivoSampleConstants.BRIVO_TOKEN = pass.getBrivoOnairPassCredentials().getTokens();
                    BrivoSampleConstants.PASS_ID = pass.getPassId();
                    if (unlock) unlockBrivoDoor();
                }

                @Override
                public void onFailed(BrivoError error) {
                    //Handle redeem pass error case
                    if (unlock) unlockFailure(true);
                    Log.e("BrivoSDKOnair > ", "redeem Fail >>" + error.getMessage());
                }
            });
        } catch (BrivoSDKInitializationException e) {
            //Handle BrivoSDK initialization exception
            Log.e("BrivoSDKOnair > ", "catch >> " + e.getMessage());
            if (unlock) unlockFailure(true);
        }
    }

    private void unlockFailure(boolean callAudit) {
        tabbedActivity.runOnUiThread(() -> {

            Toast.makeText((Context) TabbedActivity.this, "Failed to open door " + _accessPoint.getName(), Toast.LENGTH_LONG).show();
            if (_accessPointFragment != null) {
                _accessPointFragment.notifyData(_accessPointPosition, 2, _accessPoint);
            } else if (_guestAccessPointFragment != null) {
                _guestAccessPointFragment.notifyData(_accessPointPosition, 2);
            }
            if (callAudit) callAPIForAudit(_accessPoint, false);
        });
    }


    private void unlockFailure(boolean callAudit, String accessPointId) {
        tabbedActivity.runOnUiThread(() -> {

            Toast.makeText((Context) TabbedActivity.this, "Failed to open door " + _accessPoint.getName(), Toast.LENGTH_LONG).show();
            if (_accessPointFragment != null) {
                _accessPointFragment.notifyData(2, accessPointId);
            } else if (_guestAccessPointFragment != null) {
                _guestAccessPointFragment.notifyData(_accessPointPosition, 2);
            }
            if (callAudit) callAPIForAudit(_accessPoint, false);
        });
    }

    private void unlockSuccessMethod(boolean callAudit) {
        tabbedActivity.runOnUiThread(() -> {
            Toast.makeText((Context) TabbedActivity.this, _accessPoint.getName() + " was successfully opened.", Toast.LENGTH_LONG).show();
            if (_accessPointFragment != null) {
                _accessPointFragment.notifyData(_accessPointPosition, 1, _accessPoint);
            } else if (_guestAccessPointFragment != null) {
                _guestAccessPointFragment.notifyData(_accessPointPosition, 1);
            }
            if (callAudit) callAPIForAudit(_accessPoint, true);
        });
    }

    private void unlockSuccessMethod(boolean callAudit, String accessPointId) {
        tabbedActivity.runOnUiThread(() -> {
            Toast.makeText((Context) TabbedActivity.this, _accessPoint.getName() + " was successfully opened.", Toast.LENGTH_LONG).show();
            if (_accessPointFragment != null) {
                _accessPointFragment.notifyData(1, accessPointId);
            } else if (_guestAccessPointFragment != null) {
                _guestAccessPointFragment.notifyData(1, accessPointId);
            }
            if (callAudit) callAPIForAudit(_accessPoint, true);
        });
    }

    private void unlockAccessPoint(AccessPoint item, int position ) {
        String accessPointId = String.valueOf(item.id);
        new AsyncTask<AccessPoint, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(AccessPoint... params) {
                try {
                    AccessPoint item = params[0];
                    OpenAccessPoint model = new OpenAccessPoint();
                    model.id = item.id;
                    model.isSilent = true;
                    model.isVideoAccess = false;
                    model.entryName = params[0].getName();
                    model.deviceType = DeviceType.Mobile.value();
                    if (_guestAccessPointFragment != null) model.guestAccess = true;
                    else if (isGuestUser) model.guestAccess = true;
                    else model.guestAccess = false;
                    switch (item.getOperator()) {
                        case Utilities.OPERATOR_OPENPATH:
                            model.operator = AccessPointOperator.OpenPath.value();
                            break;
                        case Utilities.OPERATOR_BRIVO:
                            model.operator = AccessPointOperator.Brivo.value();
                            break;
                        case Utilities.OPERATOR_PDK:
                            model.operator = AccessPointOperator.PDK.value();
                            break;
                        case Utilities.OPERATOR_INVICTUS:
                            model.operator = AccessPointOperator.Invictus.value();
                            break;
                    }
                    MobileDataProvider.getInstance().openAccessPoint(model);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    unlockSuccessMethod(false, accessPointId);
                    Toast.makeText((Context) TabbedActivity.this, item.getName() + " was successfully opened.", Toast.LENGTH_LONG).show();
                } else {
                    unlockFailure(false, accessPointId);
                    Toast.makeText((Context) TabbedActivity.this, "Opening the " + item.getName() + " access point failed.  Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(item);
    }

    private void callAPIForRenewKey(DigitalKey item, int days, AllDigitalKeysFragment digitalKeysFragment) {
        new AsyncTask<DigitalKey, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ProgressDialog.showProgress(TabbedActivity.this);
            }

            @Override
            protected String doInBackground(DigitalKey... params) {
                try {
                    DigitalKeyRenew model = new DigitalKeyRenew();
                    model.keyId = item.id;
                    model.isDeActivated = false;
                    model.renewalDays = days;
                    return MobileDataProvider.getInstance().digitalKeyRenew(model);
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String success) {
                ProgressDialog.dismissProgress();
                if (success != null) {
                    Toast.makeText((Context) TabbedActivity.this, "Digital Key renewed successfully", Toast.LENGTH_LONG).show();
                    digitalKeysFragment.refresh();
                } else {
                    Toast.makeText((Context) TabbedActivity.this, "Something went wrong while renewing Digital Key", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(item);
    }

    private void callAPIForAudit(AccessPoint item, boolean isValid) {
        new AsyncTask<AccessPoint, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(AccessPoint... params) {
                try {
                    AccessPointAudit model = new AccessPointAudit();
                    model.accessPointId = item.id;
                    model.isValid = isValid;
                    model.entryName = item.getName();
                    MobileDataProvider.getInstance().accessPointForAudit(model);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Log.e("Audit >> ", "success");
//                    Toast.makeText(TabbedActivity.this, item.name + " was successfully opened.", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("Audit >> ", "failure");
//                    Toast.makeText(TabbedActivity.this, "Opening the " + item.name + " access point failed.  Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*destoryOpenPath();*/
        updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Offline.value()));
        new HubConnectionTaskStop(hubConnection, true).execute(hubConnection);
        new HubConnectionTaskStop(mobileHubConnection, false).execute(mobileHubConnection);
        /*hubConnection.stop();*/
        hubConnection = null;
        if (forceUpdateBuilder != null) {
            if (forceUpdateBuilder.isShowing()) {
                forceUpdateBuilder.dismiss();
            }
        }
        if (surveyBuilder != null) {
            if (surveyBuilder.isShowing()) {
                surveyBuilder.dismiss();
            }
        }
    }

    public void setSwitchUser(String opal) {
        OpenpathMobileAccessCore.getInstance().switchUser(opal);
    }

    //open path call back
    @Override
    public void onProvisionResponse(OpenpathProvisionResponse response) {
        if (response.hasError()) {
            System.out.println(response.error.code + " " + response.error.message);
        } else {
            runOnUiThread(() -> setSwitchUser(response.user.opal));
        }
    }

    @Override
    public void onUnprovisionResponse(OpenpathUnprovisionResponse openpathUnprovisionResponse) {

    }

    @Override
    public void onSwitchUserResponse(OpenpathSwitchUserResponse openpathSwitchUserResponse) {
        OpenpathMobileAccessCore.getInstance().syncUser();
    }

    @Override
    public void onSyncUserResponse(OpenpathSyncUserResponse openpathSyncUserResponse) {
        new Handler().postDelayed(() -> isOpenPathInit = true, 1500);
        isFirstTime = false;
    }

    @Override
    public void onUnlockResponse(OpenpathRequestResponse openpathRequestResponse) {
        OpenpathLogging.d(openpathRequestResponse.toString());
        if (openpathRequestResponse.statusCode == 200) {
            runOnUiThread(() -> unlockSuccessMethod(true));
        } else {
            runOnUiThread(() -> {
                unlockFailure(true);
                Toast.makeText((Context) TabbedActivity.tabbedActivity, "Entry fail to open, " + openpathRequestResponse.description, Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public void onUserSettingsSet(OpenpathUserSettings openpathUserSettings) {
        hasRemoteUnlock = openpathUserSettings.hasRemoteUnlock;
        OpenpathLogging.d("got user settings");
    }

    @Override
    public void onItemsSet(ArrayList<OpenpathItem> arrayList, ArrayList<OpenpathOrderingItem> arrayList1) {

    }

    @Override
    public void onItemsUpdated(ArrayList<OpenpathItem> arrayList) {

    }

    @Override
    public void onInit() {

    }

    @Override
    public void onLocationStatusChanged(OpenpathLocationStatus openpathLocationStatus) {

    }

    @Override
    public void onBatteryOptimizationStatusChanged(boolean b) {

    }

    @Override
    public void onBluetoothStatusChanged(boolean b, boolean b1) {

    }

    @Override
    public void onInternetStatusChanged(boolean b) {

    }

    @Override
    public void onFeedbackResponse(OpenpathResponse openpathResponse) {
        OpenpathLogging.d("onFeedbackResponse " + openpathResponse.toString());
    }

    @Override
    public void onRevertResponse(OpenpathRequestResponse openpathRequestResponse) {

    }

    @Override
    public void onOverrideResponse(OpenpathRequestResponse openpathRequestResponse) {

    }

    @Override
    public void onTriggerLockdownPlanResponse(OpenpathRequestResponse openpathRequestResponse) {

    }

    @Override
    public void onRevertLockdownPlanResponse(OpenpathRequestResponse openpathRequestResponse) {

    }

    @Override
    public void onNotificationClicked(String s, int i) {

    }

    @Override
    public void onBluetoothError(int i, String s) {

    }

    @Override
    public void onLockdownPlansSet(ArrayList<OpenpathLockdownPlan> arrayList) {

    }

    @Override
    public void onEvent(JSONObject jsonObject) {

    }

    @Override
    public void onListFragmentInteraction(ServiceKey item, ServiceKeysFragment serviceKeysFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_digital_key, null);

        builder.setView(view).setTitle(item.getRecipient());

        String status = "Expired";
        if (item.isRevoked()) status = "Revoked";
        else {
            Date now = new Date();
            if (now.after(item.getFromUtc()) && now.before(item.getToUtc())) status = "Valid";
            else if (now.before(item.getFromUtc()) && now.before(item.getToUtc())) status = "Upcoming";
        }
        ((TextView) view.findViewById(R.id.status)).setText(status);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(item.createdUtc));
        ((TextView) view.findViewById(R.id.fromUtc)).setText(formatter.format(item.getFromUtc()));
        if (item.isNoEndDate()) {
            ((TextView) view.findViewById(R.id.toUtc)).setText(formatter.format(item.getToUtc()));
        } else {
            ((TextView) view.findViewById(R.id.toUtc)).setText("Always Access");
        }
        view.findViewById(R.id.notes_label).setVisibility(TextUtils.isEmpty(item.getNotes()) ? View.GONE : View.VISIBLE);
        ((TextView) view.findViewById(R.id.notes)).setText(item.getNotes());
        view.findViewById(R.id.tvKeyTitle).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tvKey).setVisibility(View.VISIBLE);
        view.findViewById(R.id.ivQrCode).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tvKey)).setText(item.getKey());

        if (status.equals("Valid")) {
            if (item.getMapUrls() != null) {
                if (item.getMapUrls().length > 0) builder.setNegativeButton("View Map", (dialog, id) -> {
                    Intent intent = new Intent((Context) TabbedActivity.this, GuestDigitalKeyMapActivity.class);
                    intent.putExtra("imageMap", item.getMapUrl());
                    intent.putExtra("imageMaps", item.getMapUrls());
                    startActivity(intent);
                });
            }
        }

        builder.setPositiveButton("Dismiss", (dialog, id) -> {
        });
        builder.create().show();
    }

    @Override
    public void onRevokeClicked(ServiceKey item, ServiceKeysFragment serviceKeysFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.this);
        builder.setMessage("Are you sure you want to revoke " + item.getRecipient() + " service key?").setTitle("Revoke Service Key");
        builder.setNegativeButton("No", (dialog, id) -> {
        });
        builder.setPositiveButton("Yes", (dialog, id) -> {
            DigitalKeyUpdate model = new DigitalKeyUpdate();
            model.id = item.getId();
            model.isRevoked = true;

            WebService.getInstance().revokeServiceKey(model, new RestEmptyCallBack<ResponseBody>() {
                @Override
                public void onResponse(ResponseBody response) {
                    serviceKeysFragment.refresh();
                    Toast.makeText((Context) TabbedActivity.this, item.getRecipient() + " service key was successfully revoked.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(WSException wse) {
                    Toast.makeText((Context) TabbedActivity.this, item.getRecipient() + " service key failed to be revoked." + " Please try again later.", Toast.LENGTH_LONG).show();
                }
            });
        });
        builder.create().show();
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged: Phone" + dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (dataPathWearable.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String message = dataMapItem.getDataMap().getString("message");
                    Log.v(TAG, "Wear activity received message: " + message);
                    setDataForWearable();
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
        try {
            Log.e("TAG_MESSAGE_RECEIVED", "onMessageReceived event received");
            String s1 = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String messageEventPath = messageEvent.getPath();

            Log.e("TAG_MESSAGE_RECEIVED", "onMessageReceived() A message from watch was received:" + messageEvent.getRequestId() + " " + messageEventPath + " " + s1);
        } catch (Exception e) {
            Log.e("Exception >>", e.getMessage());
        }
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    /**
     * Sends the data.  Since it specify a client, everyone who is listening to the path, will
     * get the data.
     */
    private void setDataForWearable() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) TabbedActivity.this);
        PutDataMapRequest dataMap = PutDataMapRequest.create(dataPathWearable);
        dataMap.getDataMap().putString("message", "message 111");
        dataMap.getDataMap().putString("cookies", sharedPreferences.getString("authenticationCookie", null));
        dataMap.getDataMap().putString("login", "login");
        dataMap.getDataMap().putString("email", sharedPreferences.getString("email", null));
        dataMap.getDataMap().putString("activationCode", sharedPreferences.getString("activationCode", null));
        dataMap.getDataMap().putLong("userId", sharedPreferences.getLong("userId", 0));
        sendData(dataMap);
    }

    private void sendData(PutDataMapRequest dataMap) {
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();

        Task<DataItem> dataItemTask = Wearable.getDataClient((Activity) this).putDataItem(request);
        dataItemTask.addOnSuccessListener(dataItem -> Log.d(TAG, "Sending message was successful: " + dataItem)).addOnFailureListener(e -> Log.e(TAG, "Sending message failed: " + e));
    }

    @Override
    public void onListFragmentInteraction(BrivoDeviceData item, BrivoDevicesFragment brivoDevicesFragment, int position) {
        if (brivoDevicesFragment != null) {
            brivoDevicesFragment.onItemClick(position, item);
        }

    }
}