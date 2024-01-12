package net.invictusmanagement.invictuslifestyle.fragments;

import static net.invictusmanagement.invictuslifestyle.activities.TabbedActivity.openPathLogin;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.brivo.sdk.BrivoSDKInitializationException;
import com.brivo.sdk.access.BrivoSDKAccess;
import com.brivo.sdk.ble.BrivoBLEErrorCodes;
import com.brivo.sdk.enums.AccessPointCommunicationState;
import com.brivo.sdk.interfaces.IOnCommunicateWithAccessPointListener;
import com.brivo.sdk.model.BrivoError;
import com.brivo.sdk.model.BrivoResult;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import net.invictusmanagement.invictuslifestyle.MyApplication;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.ChooseSurveyActivity;
import net.invictusmanagement.invictuslifestyle.activities.LoginActivity;
import net.invictusmanagement.invictuslifestyle.activities.MediaCenterActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.enum_utils.AppStatus;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.CouponsAdvertisement;
import net.invictusmanagement.invictuslifestyle.models.EENAuthenticate;
import net.invictusmanagement.invictuslifestyle.models.EENAuthorise;
import net.invictusmanagement.invictuslifestyle.models.Login;
import net.invictusmanagement.invictuslifestyle.models.NotificationCount;
import net.invictusmanagement.invictuslifestyle.models.NotificationStatus;
import net.invictusmanagement.invictuslifestyle.models.User;
import net.invictusmanagement.invictuslifestyle.models.UserDeviceId;
import net.invictusmanagement.invictuslifestyle.models.UserStatus;
import net.invictusmanagement.invictuslifestyle.utils.BrivoSampleConstants;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;
import net.invictusmanagement.invictuslifestyle.webservice.WebServiceEEN;
import net.invictusmanagement.invictuslifestyle.widgets.NewWidgetActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;
import okhttp3.ResponseBody;

public class HomeFragment extends Fragment implements IRefreshableFragment {

    public static SwipeRefreshLayout _swipeRefreshLayout;
    public static ConstraintLayout swipeRefresh;
    public static Context _context;
    public static TextView _welcomeTextView, tvCount_2, tvCount;
    public static TextView tabDotAccesspoint;
    public static TextView tabDotServiceKey;
    public static TextView tabDotBrivoDevice;
    public static TextView tabDotAccesspoint2;
    public static TextView tabDotDigitalKey;
    public static TextView tabDotDigitalKey_2;
    public static TextView tabDotCoupons;
    public static TextView tabDotCoupons_2;
    public static TextView tabDotRentalTool;
    public static TextView tabDotHealth;
    public static TextView tabDotHealth_2;
    public static TextView tabDotBillBoard;
    public static TextView tabDotVoiceMail;
    public static TextView tabDotChat;
    public static TextView tabDotChat2;
    public static TextView tabDotNotification;
    public static TextView tabDotNotification_2;
    public static MediaPlayer _mediaPlayer;
    public static Boolean isGuestUser;
    public static Boolean isOpenPathInit = false;
    public static Boolean isSurveyAvailable = false;
    public static Boolean isQuickKey = false;
    public static Boolean isFavAccessPoints = false;
    public static UserStatus userStatus;
    public static NotificationCount notificationCount;
    public static String userName;
    public static String userId = "";
    public static String roomLocationId = "";
    public static ScrollView svMain;
    public SurfaceView surfaceView;
    public LinearLayout ll_survay;
    private LinearLayout llServiceKey;
    private LinearLayout llBrivoDevices;
    private ImageView imgTicker;
    private String mediaCenterLogoUrl = null;
    private boolean isRWTHome, isGWTHome;
    private Handler scrollHandler = new Handler(Looper.getMainLooper());
    private ExoPlayer exoPlayer;
    private StyledPlayerView idExoPlayerVIew;
    private static HomeFragment instance;
    private SharedPreferences sharedPreferences;
    private LinearLayout tvMagicDoor;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {

        if (instance == null) {
            instance = new HomeFragment();
        }
        return instance;
    }

    public void chatHomeDot(boolean chat) {
        if (tabDotChat != null)
            Utilities.showInvisible(_context, tabDotChat, chat);
        if (tabDotChat2 != null)
            Utilities.showInvisible(_context, tabDotChat2, chat);
    }

    @Override
    public void onStart() {
        _mediaPlayer = MediaPlayer.create(_context, R.raw.intro);
        _mediaPlayer.setLooping(true);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _mediaPlayer = MediaPlayer.create(_context, R.raw.intro);
        _mediaPlayer.setLooping(true);
    }

    private void walkThroughHighlightGuest(View view, View view3, View view5, View view7, View view10) {

        Lighter.with((Activity) TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_tab_access)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view3)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_tab_coupons)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view5)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_tab_digitalkey)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view7)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_tab_notification)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view10)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_tab_guest_healthwellness)
                        .setTipViewRelativeDirection(Direction.TOP)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 0, 30))
                        .build())

                .show();
    }

    private void showHighLight(View view, View view1, View view2, View view3, View view4,
                               View view5, View view6, View view7, View view8,
                               View view9, View view10, View view11, View view12,
                               View view13, View view14) {
        if (TabbedActivity.isGuestUser) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    svMain.scrollTo(0, svMain.getBottom());
                    walkThroughHighlightGuest(view1, view3, view5, view7, view10);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) TabbedActivity.tabbedActivity);
                    sharedPreferences.edit().putBoolean("isGWTHome", false).apply();
                    isGWTHome = sharedPreferences.getBoolean("isGWTHome", true);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    svMain.scrollTo(0, svMain.getBottom());
                    walkThroughHighlightResident(view, view2, view4, view6, view8, view9, view11, view12, view13, view14);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) TabbedActivity.tabbedActivity);
                    sharedPreferences.edit().putBoolean("isRWTHome", false).apply();
                    isRWTHome = sharedPreferences.getBoolean("isRWTHome", true);
                }
            }, 500);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onDestroy() {
        if (_mediaPlayer != null) {
            if (_mediaPlayer.isPlaying())
                _mediaPlayer.stop();
            _mediaPlayer.reset();
            _mediaPlayer.release();
            _mediaPlayer = null;
        }
        super.onDestroy();
    }

    private void walkThroughHighlightResident(View ap, View cp, View dk, View nt, View rt,
                                              View hw, View vm, View bb, View am, View gc) {

        Lighter lighter = Lighter.with((Activity) TabbedActivity.tabbedActivity);
        lighter.addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(ap)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_tab_access)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(cp)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_tab_coupons)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build());
        if (dk.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(dk)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_tab_digitalkey)
                    .setTipViewRelativeDirection(Direction.BOTTOM)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                    .build());
        }
        if (nt.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(nt)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_tab_notification)
                    .setTipViewRelativeDirection(Direction.BOTTOM)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                    .build());
        }
        if (rt.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(rt)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_tab_rentertool)
                    .setTipViewRelativeDirection(Direction.BOTTOM)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                    .build());
        }
        if (hw.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(hw)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_tab_healthwellness)
                    .setTipViewRelativeDirection(Direction.TOP)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0, 0, 30))
                    .build());
        }
        if (bb.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(bb)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_tab_bb)
                    .setTipViewRelativeDirection(Direction.TOP)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0, 0, 30))
                    .build());
        }
        if (gc.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(gc)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_tab_generalchat)
                    .setTipViewRelativeDirection(Direction.TOP)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0, 0, 30))
                    .build());
        }
        if (vm.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(vm)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_tab_voicemail)
                    .setTipViewRelativeDirection(Direction.TOP)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0, 0, 30))
                    .build());
        }
        lighter.show();

    }

    public void getNotificationCount() {
        WebService.getInstance().getNotificationCount(new RestCallBack<NotificationCount>() {
            @Override
            public void onResponse(NotificationCount response) {
                notificationCount = response;
                if (notificationCount != null) {
                    if (getActivity() != null)
                        ((TabbedActivity) getActivity())
                                .updateCount(notificationCount.getUnreadNotificationCount());

                    if (notificationCount.getUnreadNotificationCount() > 0) {
                        if (isGuestUser) {
                            tabDotNotification_2.setVisibility(View.VISIBLE);
                        } else {
                            tabDotNotification.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (isGuestUser) {
                            tabDotNotification_2.setVisibility(View.INVISIBLE);
                        } else {
                            tabDotNotification.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    public void checkIsUserActive() {
        WebService.getInstance().checkIsUserActive(new RestCallBack<UserStatus>() {
            @Override
            public void onResponse(UserStatus response) {
                userStatus = response;

                if (!userStatus.getActive()) {
                    sharedPreferences.edit().putString("authenticationCookie", null).apply();
                    sharedPreferences.edit().putBoolean("isFirstTime", false).apply();
                    Intent intent = new Intent((Context) TabbedActivity.tabbedActivity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    TabbedActivity.logOutOpenPath();
                    TabbedActivity.tabbedActivity.finishAffinity();
                    Toast.makeText(getContext(), "Account Deactivated", Toast.LENGTH_LONG).show();
                }

                if (userStatus.getActive()) {
                    if (isGuestUser) {
                        if (!userStatus.getRole().equals(getString(R.string.role_guest)) &&
                                !userStatus.getRole().equals(getString(R.string.role_prospect))) {
                            Toast.makeText(_context, "Your role is changed, you are now Resident", Toast.LENGTH_LONG).show();

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
                            sharedPreferences.edit().putBoolean("isGuestUser", false).apply();
                            TabbedActivity.logOutOpenPath();
                            ((TabbedActivity) getActivity()).refreshAdapter(getActivity());
                            getActivity().finish();
                            startActivity(new Intent((Context) TabbedActivity.tabbedActivity, TabbedActivity.class));
                            TabbedActivity.tabbedActivity.recreate();
                        }
                    } else {
                        if (!userStatus.getRole().equals(getString(R.string.role_resident))
                                && !userStatus.getRole().equals(getString(R.string.role_leasing_officer))
                                && !userStatus.getRole().equals(getString(R.string.role_property_manager))
                                && !userStatus.getRole().equals(getString(R.string.role_vendor))
                                && !userStatus.getRole().equals(getString(R.string.role_facility))) {
                            Toast.makeText(_context, "Your role is changed, you are now Guest", Toast.LENGTH_LONG).show();

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
                            sharedPreferences.edit().putBoolean("isGuestUser", true).apply();
                            TabbedActivity.logOutOpenPath();
                            ((TabbedActivity) getActivity()).refreshAdapter(getActivity());
                            getActivity().finish();
                            startActivity(new Intent((Context) TabbedActivity.tabbedActivity, TabbedActivity.class));
                            TabbedActivity.tabbedActivity.recreate();
                        }
                    }
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        instance = this;
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        if (view instanceof SwipeRefreshLayout) {

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            isGuestUser = sharedPreferences.getBoolean("isGuestUser", false);
            isRWTHome = sharedPreferences.getBoolean("isRWTHome", true);
            isGWTHome = sharedPreferences.getBoolean("isGWTHome", true);

            if (isGuestUser) {
                view.findViewById(R.id.llOriginal).setVisibility(View.GONE);
                view.findViewById(R.id.llSecoundary).setVisibility(View.VISIBLE);
            }

            imgTicker = view.findViewById(R.id.imgTicker);
            imgTicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.addHaptic(v);
                    startActivity(new Intent(_context, MediaCenterActivity.class));
                }
            });

            callAdvertisements();

            Button btnUpdate = view.findViewById(R.id.btnUpdate);
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedPreferences.getBoolean("isLoggedIn", false)) {
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
                    } else {
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                    }

                    Intent intent = new Intent((Context) TabbedActivity.tabbedActivity, NewWidgetActivity.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    int[] ids = AppWidgetManager.getInstance((Context) TabbedActivity.tabbedActivity)
                            .getAppWidgetIds(new ComponentName((Context) TabbedActivity.tabbedActivity,
                                    NewWidgetActivity.class));
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    TabbedActivity.tabbedActivity.sendBroadcast(intent);
                }
            });

            idExoPlayerVIew = view.findViewById(R.id.idExoPlayerVIew);
            svMain = view.findViewById(R.id.svMain);
            tabDotAccesspoint = view.findViewById(R.id.tabDotAccesspoint);
            tabDotAccesspoint2 = view.findViewById(R.id.tabDotAccesspoint2);
            tabDotDigitalKey = view.findViewById(R.id.tabDotDigitalKey);
            tabDotDigitalKey_2 = view.findViewById(R.id.tabDotDigitalKey_2);
            tabDotCoupons = view.findViewById(R.id.tabDotCoupons);
            tabDotCoupons_2 = view.findViewById(R.id.tabDotCoupons_2);
            tabDotRentalTool = view.findViewById(R.id.tabDotRentalTool);
            /*tabDotMaintReq = view.findViewById(R.id.tabDotMaintReq);*/
            tabDotHealth = view.findViewById(R.id.tabDotHealth);
            tabDotHealth_2 = view.findViewById(R.id.tabDotHealth_2);
            tabDotBillBoard = view.findViewById(R.id.tabDotBillBoard);
            tabDotVoiceMail = view.findViewById(R.id.tabDotVoiceMail);
            tabDotChat = view.findViewById(R.id.tabDotChat);
            tabDotChat2 = view.findViewById(R.id.tabDotChat2);
            tabDotNotification = view.findViewById(R.id.tabDotNotification);
            tabDotNotification_2 = view.findViewById(R.id.tabDotNotification_2);
            llServiceKey = view.findViewById(R.id.ll_service_keys);
            llBrivoDevices = view.findViewById(R.id.ll_brivo_devices);
            tabDotServiceKey = view.findViewById(R.id.tabDotServiceKey);
            tabDotBrivoDevice = view.findViewById(R.id.tabDotBrivoDevices);

            tvMagicDoor = view.findViewById(R.id.tvMagicDoor);
            if (!sharedPreferences.getBoolean("enableBrivoIntegration", false)) {
                tvMagicDoor.setVisibility(View.GONE);
            } else {
                tvMagicDoor.setVisibility(View.VISIBLE);
            }
            tvMagicDoor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unlockNearByBrivoDoor();
                }
            });

            /*swipeRefresh = (ConstraintLayout) view;*/
            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
            _welcomeTextView = view.findViewById(R.id.welcome);

            String role = sharedPreferences.getString("userRole", "");
            if (role.equals(getString(R.string.role_leasing_officer))
                    || role.equals(getString(R.string.role_property_manager))) {
                llServiceKey.setVisibility(View.VISIBLE);
                llBrivoDevices.setVisibility(View.GONE);
            } else {
                llServiceKey.setVisibility(View.GONE);
                llBrivoDevices.setVisibility(View.GONE);
            }

            if (sharedPreferences.getBoolean("allowGeneralChat", true)) {
                view.findViewById(R.id.ll_chat).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.ll_chat).setVisibility(View.GONE);
            }

            if (sharedPreferences.getBoolean("allowBulletinBoard", true)) {
                view.findViewById(R.id.ll_bill_board).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.ll_bill_board).setVisibility(View.GONE);
            }

            if (role.equalsIgnoreCase(getString(R.string.role_vendor))) {
                view.findViewById(R.id.ll_bill_board).setVisibility(View.GONE);
                view.findViewById(R.id.ll_voice_mail).setVisibility(View.GONE);
                view.findViewById(R.id.ll_chat).setVisibility(View.GONE);

                view.findViewById(R.id.ll_chat2).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ll_chat2).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else if (role.equals(getString(R.string.role_facility))) {
                view.findViewById(R.id.ll_digital_key).setVisibility(View.GONE);
                view.findViewById(R.id.ll_bill_board).setVisibility(View.GONE);
                view.findViewById(R.id.ll_voice_mail).setVisibility(View.GONE);
                view.findViewById(R.id.ll_chat).setVisibility(View.GONE);
                view.findViewById(R.id.ll_health).setVisibility(View.GONE);

                view.findViewById(R.id.ll_cmn_notifications).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ll_service_keys2).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ll_chat2).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ll_chat2).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                view.findViewById(R.id.ll_coupons).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }

            surfaceView = view.findViewById(R.id.media);
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    _mediaPlayer.setDisplay(holder);
                    _mediaPlayer.start();
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                }
            });

            view.findViewById(R.id.ll_access_points).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFavAccessPoints = false;
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS);
                }
            });
            view.findViewById(R.id.ll_access_points2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFavAccessPoints = false;
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_ACCESS_POINTS);
                }
            });
            view.findViewById(R.id.ll_service_keys2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_SERVICE_KEYS);
                }
            });
            view.findViewById(R.id.ll_service_keys).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_SERVICE_KEYS);
                }
            });

            llBrivoDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_BRIVO_DEVICES);
                }
            });

            view.findViewById(R.id.ll_cmn_notifications).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_COMMUNITY_NOTIFICATIONS);
                }
            });
            view.findViewById(R.id.ll_coupons).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS);
                }
            });
            view.findViewById(R.id.ll_coupons_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_PROMOTIONS);
                }
            });
            view.findViewById(R.id.ll_service_keys).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context)
                            .setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_SERVICE_KEYS);
                }
            });
            view.findViewById(R.id.ll_digital_key).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isQuickKey = false;
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS);
                }
            });
            view.findViewById(R.id.ll_digital_key_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_DIGITAL_KEYS);
                }
            });
            /*view.findViewById(R.id.ll_quick_key).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isQuickKey = true;
                    ((TabbedActivity) _context).setSelectedTab(TabbedActivity.FRAGMENT_POSITION_DIGITAL_KEYS);
                }
            });*/
            view.findViewById(R.id.ll_notification).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS);
                }
            });
            view.findViewById(R.id.ll_notification_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_NOTIFICATIONS);
                }
            });
            view.findViewById(R.id.ll_rental_tool).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_RENTAL_TOOL);
                }
            });
           /* view.findViewById(R.id.ll_maintenance_reqs).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(TabbedActivity.FRAGMENT_POSITION_MAINTENANCE_REQUESTS);
                }
            });*/
            view.findViewById(R.id.ll_health).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_HEALTH);
                }
            });
            view.findViewById(R.id.ll_health_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_HEALTH);
                }
            });
            /*view.findViewById(R.id.ll_insurance).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(TabbedActivity.FRAGMENT_POSITION_INSURANCE);
                }
            });*/
            view.findViewById(R.id.ll_voice_mail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_VOICE_MAIL);
                }
            });
            view.findViewById(R.id.ll_bill_board).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_BILLBOARD);
                }
            });
            /*view.findViewById(R.id.ll_amenities).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(TabbedActivity.FRAGMENT_POSITION_AMENITIES);
                }
            });*/
            view.findViewById(R.id.ll_chat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT);
                }
            });
            view.findViewById(R.id.ll_chat2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TabbedActivity) _context).setSelectedTab(((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_CHAT);
                }
            });
            ll_survay = view.findViewById(R.id.ll_survay);
            ll_survay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(_context, ChooseSurveyActivity.class);
                    startActivity(i);
                }
            });
            /*getPastChatList(messageIdMain, true);*/
            if (isGuestUser) {
                if (isGWTHome) {
                    showHighLight(view.findViewById(R.id.ll_access_points),
                            view.findViewById(R.id.ll_access_points2),
                            view.findViewById(R.id.ll_coupons),
                            view.findViewById(R.id.ll_coupons_2),
                            view.findViewById(R.id.ll_digital_key),
                            view.findViewById(R.id.ll_digital_key_2),
                            view.findViewById(R.id.ll_notification),
                            view.findViewById(R.id.ll_notification_2),
                            view.findViewById(R.id.ll_rental_tool),
                            view.findViewById(R.id.ll_health),
                            view.findViewById(R.id.ll_health_2),
                            view.findViewById(R.id.ll_voice_mail),
                            view.findViewById(R.id.ll_bill_board),
                            view,
                            view.findViewById(R.id.ll_chat));
                }
            } else {
                if (isRWTHome) {
                    showHighLight(view.findViewById(R.id.ll_access_points),
                            view.findViewById(R.id.ll_access_points2),
                            view.findViewById(R.id.ll_coupons),
                            view.findViewById(R.id.ll_coupons_2),
                            view.findViewById(R.id.ll_digital_key),
                            view.findViewById(R.id.ll_digital_key_2),
                            view.findViewById(R.id.ll_notification),
                            view.findViewById(R.id.ll_notification_2),
                            view.findViewById(R.id.ll_rental_tool),
                            view.findViewById(R.id.ll_health),
                            view.findViewById(R.id.ll_health_2),
                            view.findViewById(R.id.ll_voice_mail),
                            view.findViewById(R.id.ll_bill_board),
                            view,
                            (role.equals(getString(R.string.role_vendor)) ||
                                    role.equals(getString(R.string.role_facility))) ?
                                    view.findViewById(R.id.ll_chat2) :
                                    view.findViewById(R.id.ll_chat));
                }
            }

            refresh();
            updateNotificationOneTime();
//            if (!sharedPreferences.getBoolean("IsSavedDATA", false)) {
            saveUserId();
//            }
        }
        return view;
    }


    private void guestHomeDotUpdate(NotificationStatus notificationStatus) {
        Utilities.showInvisible(_context, tabDotAccesspoint2, notificationStatus.getAccessPoints());
        Utilities.showInvisible(_context, tabDotCoupons_2, notificationStatus.getCoupons());
        Utilities.showInvisible(_context, tabDotDigitalKey_2, notificationStatus.getDigitalKey());
        Utilities.showInvisible(_context, tabDotHealth_2, notificationStatus.getHealthVideo());
    }

    public void refresh() {
        if (_swipeRefreshLayout == null)
            return;

        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getUserData(new RestCallBack<User>() {
            @Override
            public void onResponse(User user) {
                _swipeRefreshLayout.setRefreshing(false);
                if (user != null) {
                    mediaCenterLogoUrl = user.getMediaCenterLogoUrl();
                    if (mediaCenterLogoUrl != null)
                        if (!mediaCenterLogoUrl.equals(""))
                            setMediaCenterLogo();
                    isOpenPathInit = user.isEnableOpenPathIntegration();
                    isSurveyAvailable = user.isHasSurvey();
                    userName = user.getDisplayName();
                    userId = user.getId();
                    roomLocationId = user.getLocationId();

                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(_context);
                    sharedPreferences.edit()
                            .putBoolean("enableBrivoIntegration", user.isEnableBrivoIntegration()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enablePDKIntegration", user.isEnablePDKIntegration()).apply();

                    if (user.isEnableBrivoIntegration()) {
                        BrivoSampleConstants.CLIENT_ID = user.getBrivoDoorAccessClientId();
                        BrivoSampleConstants.CLIENT_SECRET = user.getBrivoDoorAccessClientSecret();
                        ((MyApplication) getActivity().getApplication()).initializeBrivoSDK();
                    }
                    sharedPreferences.edit()
                            .putString("peekId", user.getEenUserName()).apply();
                    sharedPreferences.edit()
                            .putString("peekPassword", user.getEenPassword()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enableEENIntegration", user.isEnableEENIntegration()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enableAVAIntegration", user.enableAVAIntegration).apply();
                    sharedPreferences.edit()
                            .putString("avaUserName", user.avaUserName).apply();
                    sharedPreferences.edit()
                            .putString("avaPassword", user.avaPassword).apply();
                    sharedPreferences.edit()
                            .putString("avaServerName", user.avaServerName).apply();
                    sharedPreferences.edit()
                            .putBoolean("isHapticOn", user.isHapticOn()).apply();
                    sharedPreferences.edit()
                            .putBoolean("isPushSilent", user.isPushSilent()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enableOpenPathIntegration", user.isEnableOpenPathIntegration()).apply();
                    sharedPreferences.edit()
                            .putBoolean("allowMaintenanceRequest", user.isAllowMaintenanceRequest()).apply();
                    sharedPreferences.edit()
                            .putBoolean("allowAmenitiesBooking", user.isAllowAmenitiesBooking()).apply();
                    sharedPreferences.edit()
                            .putBoolean("allowBulletinBoard", user.isAllowBulletinBoard()).apply();
                    sharedPreferences.edit()
                            .putBoolean("allowInsuranceRequest", user.isAllowInsuranceRequest()).apply();
                    sharedPreferences.edit()
                            .putBoolean("allowGeneralChat", user.isAllowGeneralChat()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enableRentPayment", user.isEnableRentPayment()).apply();
                    sharedPreferences.edit()
                            .putBoolean("hasExtraIntegration", user.isHasExtraIntegration()).apply();
                    sharedPreferences.edit()
                            .putString("bshUserName", user.bshUserName).apply();
                    sharedPreferences.edit()
                            .putString("bshPassword", user.bshPassword).apply();
                    sharedPreferences.edit()
                            .putInt("bshUserId", user.bshUserId).apply();

                    TabbedActivity.tabbedActivity.updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Active.value()));
                    /*TabbedActivity.tabbedActivity.joinResidentAllGroup(HomeFragment.userId);*/
                    _welcomeTextView.setText("Welcome " + user.getDisplayName() + "!");
                    if (!isGuestUser) {
                        if (user.getEmail() != null && user.getId() != null) {
                            if (isOpenPathInit) {
                                if (user.openPathEmail != null && user.openPathCredential != null &&
                                        user.openPathOrganizationId != null && user.openPathUserId != null) {
                                    if (user.openPathEmail.length() > 0 && user.openPathCredential.length() > 0
                                            && user.openPathOrganizationId.length() > 0
                                            && user.openPathUserId.length() > 0) {
                                        openPathLogin(user.openPathEmail, user.openPathCredential,
                                                user.openPathOrganizationId, user.openPathUserId, "");
                                    }
                                }
//                                if (user.getOpenPathCredential() != null) {
//                                    openPathLogin(user.getEmail(), user.getOpenPathCredential(), "");
//                                }
//                                openPathLogin(user.getEmail(), "invictus@" + user.getId(), "");
                            } else {
                                TabbedActivity.logOutOpenPath();
                            }
                        }
                    }
                    if (!isGuestUser) {
                        ll_survay.setVisibility(View.GONE);
                        if (user.isHasSurvey()) {
                            TabbedActivity.tabbedActivity.AlertDialogSurvey();
                        }
                    }
                }
            }

            @Override
            public void onFailure(WSException wse) {
                _swipeRefreshLayout.setRefreshing(false);

            }
        });
//        callAdvertisements();
        checkDeviceDetails();
        getNotificationCount();
        checkIsUserActive();
        notificationStatus();
        getEENAuthentication();
        checkMaintenanceRating();
    }

    private void saveUserId() {
        Login login = new Login();
        login.deviceId = Utilities.getDeviceID(_context);
        login.deviceName = Utilities.getDeviceName();
        login.email = sharedPreferences.getString("email", "");
        login.applicationUserId = sharedPreferences.getLong("userId", 0);
        WebService.getInstance().saveUserId(login, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                if (response != null) {
                    sharedPreferences.edit().putBoolean("IsSavedDATA", true).apply();
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void checkDeviceDetails() {
        WebService.getInstance().getUserDeviceId(new RestCallBack<UserDeviceId>() {
            @Override
            public void onResponse(UserDeviceId response) {
                if (response != null) {
                    String deviceId = Utilities.getDeviceID(getContext());
                    if (!response.deviceId.equalsIgnoreCase(deviceId)) {
                        ((TabbedActivity) getActivity()).logoutFromApp(true);
                    }
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void checkMaintenanceRating() {
        WebService.getInstance().checkMaintenanceRating(new RestCallBack<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("id")) {
                            TabbedActivity.tabbedActivity.AlertDialogRating(object.getInt("id"),
                                    object.getString("title"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void getEENAuthentication() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) TabbedActivity.tabbedActivity);
        if (!sharedPreferences.getBoolean("enableEENIntegration", false)) {
            EENAuthenticate authenticate = new EENAuthenticate();
            authenticate.username = sharedPreferences.getString("peekId", "");
            authenticate.password = sharedPreferences.getString("peekPassword", "");
            WebServiceEEN.getInstance().eenAuthenticate(authenticate, new RestCallBack<EENAuthorise>() {
                @Override
                public void onResponse(EENAuthorise response) {
                    if (response != null)
                        getEENAuthorise(response);
                }

                @Override
                public void onFailure(WSException wse) {

                }
            });
        }
    }

    private void getEENAuthorise(EENAuthorise response) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) TabbedActivity.tabbedActivity);
        WebServiceEEN.getInstance().eenAuthorise(response, new RestEmptyCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                if (response != null) {
                    sharedPreferences.edit().putBoolean("peekAuthentication", true).apply();
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void setMediaCenterLogo() {
        idExoPlayerVIew.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.GONE);
        releasePlayer();
        intiPlayer(mediaCenterLogoUrl);
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void intiPlayer(String url) {
        try {
//            exoPlayer = ExoPlayerFactory.newSimpleInstance(TabbedActivity.tabbedActivity, trackSelector);
            exoPlayer = new ExoPlayer.Builder(_context).build();
            MediaItem mediaSource = MediaItem.fromUri(url);
            idExoPlayerVIew.setPlayer(exoPlayer);
            idExoPlayerVIew.setShowNextButton(false);
            idExoPlayerVIew.setShowPreviousButton(false);
            idExoPlayerVIew.hideController();
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
            exoPlayer.setVolume(0f);
            exoPlayer.setMediaItem(mediaSource);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
            idExoPlayerVIew.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    Objects.requireNonNull(idExoPlayerVIew.getPlayer()).play();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    Objects.requireNonNull(idExoPlayerVIew.getPlayer()).pause();
                }
            });
        } catch (Exception e) {
            Log.e("MainAcvtivity", " exoplayer error " + e.toString());
        }
    }

    public void notificationStatus() {
        WebService.getInstance().getNotificationStatus(new RestCallBack<NotificationStatus>() {
            @Override
            public void onResponse(NotificationStatus notificationStatus) {
                if (notificationStatus != null) {
                    if (isGuestUser) {
                        if (getActivity() != null) {
                            ((TabbedActivity) getActivity())
                                    .setGuestTabDot(notificationStatus, getContext());
                        }
                        guestHomeDotUpdate(notificationStatus);
                    } else {

                        if (getActivity() != null) {
                            ((TabbedActivity) getActivity())
                                    .setResidentTabDot(notificationStatus, getActivity());
                        }
                        residentHomeDotUpdate(notificationStatus);
                    }

                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    public void updateNotificationStatus(String id) {
        WebService.getInstance().setNotificationStatus(id, new RestEmptyCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                notificationStatus();
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    public void updateNotificationOneTime() {
        WebService.getInstance().updateNotificationOneTime(true, new RestEmptyCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {

            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void residentHomeDotUpdate(NotificationStatus notificationStatus) {
        Utilities.showInvisible(_context, tabDotAccesspoint, notificationStatus.getAccessPoints());
        Utilities.showInvisible(_context, tabDotCoupons, notificationStatus.getCoupons());
        Utilities.showInvisible(_context, tabDotDigitalKey, notificationStatus.getDigitalKey());
        Utilities.showInvisible(_context, tabDotRentalTool, notificationStatus.getMainRequests() | notificationStatus.getAmenities());
        Utilities.showInvisible(_context, tabDotHealth, notificationStatus.getHealthVideo());
        Utilities.showInvisible(_context, tabDotVoiceMail, notificationStatus.getVoiceMail());
        Utilities.showInvisible(_context, tabDotBillBoard, notificationStatus.getBulletinBoard());
        Utilities.showInvisible(_context, tabDotChat, notificationStatus.getChat());
        Utilities.showInvisible(_context, tabDotChat2, notificationStatus.getChat());
    }

    public void callAdvertisements() {
        WebService.getInstance().getAdvertisements(new RestCallBack<List<CouponsAdvertisement>>() {
            @Override
            public void onResponse(List<CouponsAdvertisement> response) {
                if (response.size() > 0) {
                    imgTicker.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(WSException wse) {
                imgTicker.setVisibility(View.GONE);
            }
        });
    }


    public boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    private void showDialogToEnableBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabbedActivity.tabbedActivity);
        builder.setTitle("Enable Bluetooth");
        builder.setMessage("Please enable Bluetooth.");
        builder.setPositiveButton("Turn On", (dialog, which) -> {
            dialog.cancel();
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.enable();
            new Handler().postDelayed(() -> unlockNearByBrivoDoor(), 2000);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void unlockNearByBrivoDoor() {
        if (!isBluetoothEnabled()) {
            showDialogToEnableBluetooth();
        } else {
            tvMagicDoor.setBackgroundColor(getResources().getColor(R.color.event_color_04));
            CancellationSignal signal = new CancellationSignal();
            waitForSlowConnection(signal);
            try {
                BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(signal, new IOnCommunicateWithAccessPointListener() {
                    @Override
                    public void onResult(@NonNull BrivoResult result) {
                        signal.cancel();
                        if (result.getCommunicationState() == AccessPointCommunicationState.SUCCESS) {
                            onUnlockSuccess();
                        } else if (result.getCommunicationState() == AccessPointCommunicationState.FAILED) {
                            onUnlockFailed(result.getError());
                        } else if (result.getCommunicationState() == AccessPointCommunicationState.SHOULD_CONTINUE) {
                            result.getShouldContinueListener().onShouldContinue(true);
                        }
                    }
                });
            } catch (BrivoSDKInitializationException e) {
                e.printStackTrace();
                signal.cancel();
                onUnlockFailed(null);
            }
        }
    }

    private void onUnlockSuccess() {
        tvMagicDoor.setBackgroundColor(getResources().getColor(R.color.event_color_03));
        resetItem();
    }

    private void onUnlockFailed(BrivoError error) {
        String errorCode = "";

        if (error != null) {
            switch (error.getCode()) {
                case BrivoBLEErrorCodes.BLE_DISABLED_ON_DEVICE:
                    errorCode = getString(R.string.brivo_sample_please_enable_ble_to_continue);
                    break;
                case BrivoBLEErrorCodes.BLE_FAILED_TRANSMISSION:
                case BrivoBLEErrorCodes.BLE_ACCESS_DENIED:
                case BrivoBLEErrorCodes.BLE_CONNECTION_MANAGER_FAILED_TO_INITIALIZE:
                case BrivoBLEErrorCodes.BLE_UNKNOWN_ERROR:
                    errorCode = getString(R.string.brivo_sample_failed_to_unlock_door);
                    break;
                case BrivoBLEErrorCodes.BLE_AUTHENTICATION_TIMED_OUT:
                    errorCode = getString(R.string.brivo_sample_failed_to_unlock_door_timeout);
                    break;
                case BrivoBLEErrorCodes.BLE_LOCATION_DISABLED_ON_DEVICE:
                case BrivoBLEErrorCodes.BLE_LOCATION_PERMISSION_NOT_GRANTED:
                    errorCode = getString(R.string.brivo_sample_location_permission_denied);
                    break;
                default:
                    Toast.makeText(_context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            errorCode = "Unable to find nearby door, Please try again later.";
        }
        tvMagicDoor.setBackgroundColor(getResources().getColor(R.color.event_color_02));
        Toast.makeText(_context, errorCode, Toast.LENGTH_SHORT).show();
        resetItem();
    }

    private void resetItem() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvMagicDoor.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        }, 4000);
    }

    private void waitForSlowConnection(final CancellationSignal cancellationSignal) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancellationSignal.cancel();
                onUnlockFailed(null);
            }
        }, 30000);
    }
}
