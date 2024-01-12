package net.invictusmanagement.invictuslifestyle.activities;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.invictusmanagement.invictuslifestyle.enum_utils.AccessPointOperator;
import net.invictusmanagement.invictuslifestyle.enum_utils.DeviceType;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.models.OpenAccessPoint;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.widgets.NewWidgetActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Button crashButton = new Button(this);
//        crashButton.setText("Test Crash");
//        crashButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                throw new RuntimeException("Test Crash"); // Force a crash
//            }
//        });
        NotificationManager notify_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notify_manager.cancel(1);
        TabbedActivity.isFirstTime = true;
        TabbedActivity.isOpenPathInit = false;
        HomeFragment.isOpenPathInit = false;
        HomeFragment.isSurveyAvailable = false;
        TabbedActivity.isHubConnected = false;
        TabbedActivity.isMobileHubConnected = false;
        TabbedActivity.isFirstTimeNotificationDot = true;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String authenticationCookie = sharedPreferences.getString("authenticationCookie", null);
        sharedPreferences.edit().putBoolean("isWalkThroughEnable", true).apply();
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);
        if (isFirstTime) {
            sharedPreferences.edit().putBoolean("isRWTHome", true).apply();
            sharedPreferences.edit().putBoolean("isGWTHome", true).apply();
            sharedPreferences.edit().putBoolean("isRWTAccessPoint", true).apply();
            sharedPreferences.edit().putBoolean("isRWTCoupons", true).apply();
            sharedPreferences.edit().putBoolean("isGWTCoupons", true).apply();
            sharedPreferences.edit().putBoolean("isRWTDigitalKey", true).apply();
            sharedPreferences.edit().putBoolean("isGWTDigitalKey", true).apply();
            sharedPreferences.edit().putBoolean("isRWTNotifictions", true).apply();
            sharedPreferences.edit().putBoolean("isGWTNotifictions", true).apply();
            sharedPreferences.edit().putBoolean("isRWTRenterTools", true).apply();
            sharedPreferences.edit().putBoolean("isRWTHealthAndWellness", true).apply();
            sharedPreferences.edit().putBoolean("isGWTHealthAndWellness", true).apply();
            sharedPreferences.edit().putBoolean("isRWTVoiceMail", true).apply();
            sharedPreferences.edit().putBoolean("isRWTBulletinBoard", true).apply();
            sharedPreferences.edit().putBoolean("isRWTAmenities", true).apply();
            sharedPreferences.edit().putBoolean("isRWTTopic", true).apply();
        }

        if (authenticationCookie == null) {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
            Intent intent = new Intent(this, NewWidgetActivity.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(this)
                    .getAppWidgetIds(new ComponentName(this, NewWidgetActivity.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            this.sendBroadcast(intent);

            startActivity(new Intent(this, LoginActivity.class));


        } else {
            MobileDataProvider.getInstance().setAuthenticationCookie(authenticationCookie);
            boolean isFromNotification = false;
            boolean isQuickKey = false;
            boolean isAccessPoint = false;
            if (getIntent().getExtras() != null) {
                isFromNotification = getIntent().getExtras().getBoolean("isFromNotification");
                isQuickKey = getIntent().getExtras().getBoolean("isQuickKey");
                isAccessPoint = getIntent().getExtras().getBoolean("isAccessPoint");
            }
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
            Intent tabbedIntent = new Intent(this, TabbedActivity.class);
            tabbedIntent.putExtra("isFromNotification", isFromNotification);
            tabbedIntent.putExtra("isQuickKey", isQuickKey);
            tabbedIntent.putExtra("isAccessPoint", isAccessPoint);
            startActivity(tabbedIntent);

        }
        finish();
    }
}
