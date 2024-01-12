package net.invictusmanagement.invictuslifestyle.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.invictusmanagement.invictuslifestyle.MyApplication;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.enum_utils.AccessPointOperator;
import net.invictusmanagement.invictuslifestyle.enum_utils.DeviceType;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.OpenAccessPoint;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

public class NewWidgetActivity extends AppWidgetProvider {

    public static final String gate1Unlock = "gate1Unlock";
    public static final String gate2Unlock = "gate2Unlock";
    public static final String gate3Unlock = "gate3Unlock";
    public static final String gate4Unlock = "gate4Unlock";
    public static final String gate5Unlock = "gate5Unlock";


    public static final String EXTRA_WIDGET_ACCESSPOINT_1 = "accessPoint1";
    public static final String EXTRA_WIDGET_ACCESSPOINT_2 = "accessPoint2";
    public static final String EXTRA_WIDGET_ACCESSPOINT_3 = "accessPoint3";
    public static final String EXTRA_WIDGET_ACCESSPOINT_4 = "accessPoint4";
    public static final String EXTRA_WIDGET_ACCESSPOINT_5 = "accessPoint5";


    public static List<AccessPoint> accessPoints = new ArrayList<>();
    public static AccessPoint accessPoint1 = new AccessPoint();
    public static AccessPoint accessPoint2 = new AccessPoint();
    public static AccessPoint accessPoint3 = new AccessPoint();
    public static AccessPoint accessPoint4 = new AccessPoint();
    public static AccessPoint accessPoint5 = new AccessPoint();
    public static SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.context);

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; i++) {
            int currentWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_new_widget);


            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            boolean isGuestUser = sharedPreferences.getBoolean("isGuestUser", false);
            views.setTextViewText(R.id.tvWarning, "Please log in first.");

            if (isGuestUser) {
                views.setTextViewText(R.id.tvWarning, "This feature is only for resident.");
                views.setViewVisibility(R.id.llNoGates, View.VISIBLE);
                views.setViewVisibility(R.id.llGates, View.GONE);
                appWidgetManager.updateAppWidget(currentWidgetId, views);
            } else {
                if (isLoggedIn) {
                    views.setViewVisibility(R.id.llGates, View.VISIBLE);
                    views.setViewVisibility(R.id.llNoGates, View.GONE);
                    allAccessPoints(views, currentWidgetId, context, appWidgetManager);
                } else {
                    views.setViewVisibility(R.id.llNoGates, View.VISIBLE);
                    views.setViewVisibility(R.id.llGates, View.GONE);
                    appWidgetManager.updateAppWidget(currentWidgetId, views);
                }
            }
            /*Toast.makeText(context, "widget added", Toast.LENGTH_SHORT).show();*/

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }


    public void onReceive(Context context, Intent intent) {

        if (gate1Unlock.equals(intent.getAction())) {
            MobileDataProvider.getInstance().setAuthenticationCookie(sharedPreferences.getString("authenticationCookie", null));
            accessPoint1 = new Gson().fromJson(sharedPreferences.getString(EXTRA_WIDGET_ACCESSPOINT_1, ""), new TypeToken<AccessPoint>() {
            }.getType());


            if (accessPoint1.getOperator() == Utilities.OPERATOR_OPENPATH) {
                try {
                    TabbedActivity.tabbedActivity.openPathEntryOpen(accessPoint1.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                unlockNormalDoor(accessPoint1, context);
            }

            Toast.makeText(context, accessPoint1.getName() + " Opening", Toast.LENGTH_SHORT).show();
        } else if (gate2Unlock.equals(intent.getAction())) {
            MobileDataProvider.getInstance().setAuthenticationCookie(sharedPreferences.getString("authenticationCookie", null));
            accessPoint2 = new Gson().fromJson(sharedPreferences.getString(EXTRA_WIDGET_ACCESSPOINT_2, ""), new TypeToken<AccessPoint>() {
            }.getType());

            if (accessPoint2.getOperator() == Utilities.OPERATOR_OPENPATH) {
                try {
                    TabbedActivity.tabbedActivity.openPathEntryOpen(accessPoint2.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                unlockNormalDoor(accessPoint2, context);
            }

            Toast.makeText(context, accessPoint2.getName() + " Opening", Toast.LENGTH_SHORT).show();
        } else if (gate3Unlock.equals(intent.getAction())) {
            MobileDataProvider.getInstance().setAuthenticationCookie(sharedPreferences.getString("authenticationCookie", null));
            accessPoint3 = new Gson().fromJson(sharedPreferences.getString(EXTRA_WIDGET_ACCESSPOINT_3, ""), new TypeToken<AccessPoint>() {
            }.getType());

            if (accessPoint3.getOperator() == Utilities.OPERATOR_OPENPATH) {
                try {
                    TabbedActivity.tabbedActivity.openPathEntryOpen(accessPoint3.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                unlockNormalDoor(accessPoint3, context);
            }

            Toast.makeText(context, accessPoint3.getName() + " Opening", Toast.LENGTH_SHORT).show();
        } else if (gate4Unlock.equals(intent.getAction())) {
            MobileDataProvider.getInstance().setAuthenticationCookie(sharedPreferences.getString("authenticationCookie", null));
            accessPoint4 = new Gson().fromJson(sharedPreferences.getString(EXTRA_WIDGET_ACCESSPOINT_4, ""), new TypeToken<AccessPoint>() {
            }.getType());

            if (accessPoint4.getOperator() == Utilities.OPERATOR_OPENPATH) {
                try {
                    TabbedActivity.tabbedActivity.openPathEntryOpen(accessPoint4.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                unlockNormalDoor(accessPoint4, context);
            }

            Toast.makeText(context, accessPoint4.getName() + " Opening", Toast.LENGTH_SHORT).show();
        } else if (gate5Unlock.equals(intent.getAction())) {
            MobileDataProvider.getInstance().setAuthenticationCookie(sharedPreferences.getString("authenticationCookie", null));
            accessPoint5 = new Gson().fromJson(sharedPreferences.getString(EXTRA_WIDGET_ACCESSPOINT_5, ""), new TypeToken<AccessPoint>() {
            }.getType());

            if (accessPoint5.getOperator() == Utilities.OPERATOR_OPENPATH) {
                try {
                    TabbedActivity.tabbedActivity.openPathEntryOpen(accessPoint5.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                unlockNormalDoor(accessPoint5, context);
            }

            Toast.makeText(context, accessPoint5.getName() + " Opening", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), NewWidgetActivity.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    public void allAccessPoints(RemoteViews views,
                                int currentWidgetId, Context context,
                                AppWidgetManager appWidgetManager) {
        boolean isGuestUser = sharedPreferences.getBoolean("isGuestUser", false);
        WebService.getInstance().getAccessPoints(isGuestUser,
                new RestCallBack<List<AccessPoint>>() {
                    @Override
                    public void onResponse(List<AccessPoint> response) {
                        accessPoints = response;
                        setWidgetData(context, views, currentWidgetId,
                                appWidgetManager);
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        views.setTextViewText(R.id.tvWarning, "Please restart app.");
                        views.setViewVisibility(R.id.llNoGates, View.VISIBLE);
                        views.setViewVisibility(R.id.llGates, View.GONE);
                        Toast.makeText(context,
                                context.getString(R.string.error_fetching_access_point),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void setWidgetData(Context context, RemoteViews views,
                               int currentWidgetId, AppWidgetManager appWidgetManager) {
        views.setViewVisibility(R.id.llNoGates, View.GONE);
        views.setViewVisibility(R.id.llGates, View.VISIBLE);
        List<AccessPoint> accessPointsNew = new ArrayList<>();
        List<AccessPoint> accessPointsFav = new ArrayList<>();


        for (int i = 0; i < accessPoints.size(); i++) {
            if (accessPoints.get(i).getFavorite() != null)
                if (accessPoints.get(i).getFavorite()) {
                    accessPointsFav.add(accessPoints.get(i));
                } else {
                    accessPointsNew.add(accessPoints.get(i));
                }
        }

        accessPointsFav.sort((o1, o2) -> {
            if (o1.getDisplayOrder() < o2.getDisplayOrder()) {// less than
                return -1;
            } else {
                return 0;
            }
        });


        accessPointsNew.sort((o1, o2) -> {
            if (o1.getDisplayOrder() < o2.getDisplayOrder()) {// less than
                return -1;
            } else {
                return 0;
            }
        });

        if (accessPointsFav.size() == 0 && accessPointsNew.size() == 0) {
            views.setTextViewText(R.id.tvWarning,
                    context.getString(R.string.error_no_access_points_available));
            views.setViewVisibility(R.id.llNoGates, View.VISIBLE);
            views.setViewVisibility(R.id.llGates, View.GONE);
        } else {

            if (accessPointsFav.size() == 0) {
                accessPointsFav = accessPointsNew;
            }
            views.setViewVisibility(R.id.llView, View.GONE);
            views.setViewVisibility(R.id.glGate1, View.GONE);
            views.setViewVisibility(R.id.glGate2, View.GONE);
            views.setViewVisibility(R.id.glGate3, View.GONE);
            views.setViewVisibility(R.id.glGate4, View.GONE);
            views.setViewVisibility(R.id.glGate5, View.GONE);
            views.setViewVisibility(R.id.llView, View.GONE);

            for (int i = 0; i < accessPointsFav.size(); i++) {
                if (i == 0) {
                    views.setViewVisibility(R.id.glGate1, View.VISIBLE);

                    accessPoint1 = accessPointsFav.get(i);
                    NewWidgetActivity.sharedPreferences.edit()
                            .putString(EXTRA_WIDGET_ACCESSPOINT_1,
                                    new Gson().toJson(accessPoint1)).apply();

                    views.setTextViewText(R.id.tvGate1Name, accessPoint1.getName());

                    views.setOnClickPendingIntent(R.id.tvGate1open,
                            getPendingSelfIntent(context, gate1Unlock));
                } else if (i == 1) {
                    views.setViewVisibility(R.id.glGate2, View.VISIBLE);

                    accessPoint2 = accessPointsFav.get(i);
                    NewWidgetActivity.sharedPreferences.edit()
                            .putString(EXTRA_WIDGET_ACCESSPOINT_2,
                                    new Gson().toJson(accessPoint2)).apply();

                    views.setTextViewText(R.id.tvGate2Name, accessPoint2.getName());

                    views.setOnClickPendingIntent(R.id.tvGate2open,
                            getPendingSelfIntent(context, gate2Unlock));
                } else if (i == 2) {
                    views.setViewVisibility(R.id.glGate3, View.VISIBLE);

                    accessPoint3 = accessPointsFav.get(i);
                    NewWidgetActivity.sharedPreferences.edit()
                            .putString(EXTRA_WIDGET_ACCESSPOINT_3,
                                    new Gson().toJson(accessPoint3)).apply();

                    views.setTextViewText(R.id.tvGate3Name, accessPoint3.getName());

                    views.setOnClickPendingIntent(R.id.tvGate3open,
                            getPendingSelfIntent(context, gate3Unlock));
                } else if (i == 3) {
                    views.setViewVisibility(R.id.glGate4, View.VISIBLE);

                    accessPoint4 = accessPointsFav.get(i);
                    NewWidgetActivity.sharedPreferences.edit()
                            .putString(EXTRA_WIDGET_ACCESSPOINT_4,
                                    new Gson().toJson(accessPoint4)).apply();

                    views.setTextViewText(R.id.tvGate4Name, accessPoint4.getName());

                    views.setOnClickPendingIntent(R.id.tvGate4open,
                            getPendingSelfIntent(context, gate4Unlock));
                } else if (i == 4) {
                    views.setViewVisibility(R.id.glGate5, View.VISIBLE);

                    accessPoint5 = accessPointsFav.get(i);
                    NewWidgetActivity.sharedPreferences.edit()
                            .putString(EXTRA_WIDGET_ACCESSPOINT_5,
                                    new Gson().toJson(accessPoint5)).apply();

                    views.setTextViewText(R.id.tvGate5Name, accessPoint5.getName());

                    views.setOnClickPendingIntent(R.id.tvGate5open,
                            getPendingSelfIntent(context, gate5Unlock));
                }
            }
        }
        appWidgetManager.updateAppWidget(currentWidgetId, views);

    }

    public void unlockNormalDoor(AccessPoint item, Context context) {
        new AsyncTask<AccessPoint, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(AccessPoint... params) {
                try {
                    OpenAccessPoint model = new OpenAccessPoint();
                    model.id = params[0].id;
                    model.isSilent = true;
                    model.isVideoAccess = false;
                    model.entryName = item.getName();
                    model.deviceType = DeviceType.Mobile.value();
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
                if (success)
                    Toast.makeText(context, item.getName() + " was successfully opened.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Opening the " + item.getName() + " access point failed.  Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute(item);
    }
}