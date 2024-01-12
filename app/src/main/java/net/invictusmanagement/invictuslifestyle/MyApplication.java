package net.invictusmanagement.invictuslifestyle;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.brivo.sdk.BrivoLog;
import com.brivo.sdk.BrivoSDK;
import com.brivo.sdk.BrivoSDKInitializationException;
import com.brivo.sdk.interfaces.IOnShouldContinueListener;
import com.brivo.sdk.model.BrivoConfiguration;
import com.brivo.sdk.model.BrivoError;
import com.brivo.sdk.onair.model.BrivoSelectedAccessPoint;

import net.invictusmanagement.invictuslifestyle.utils.BrivoSampleConstants;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;
import net.invictusmanagement.invictuslifestyle.webservice.WebServiceBrivoSmarthHome;
import net.invictusmanagement.invictuslifestyle.webservice.WebServiceEEN;

public class MyApplication extends Application implements LifecycleObserver {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //Parse SDK stuff goes here
        context = this;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        // init webservice
        WebService.init(this);
        WebServiceEEN.init(this);
        WebServiceBrivoSmarthHome.init(this);
    }

    public void initializeBrivoSDK() {
        try {
            BrivoSDK.getInstance().init(getApplicationContext(), new BrivoConfiguration(
                    BrivoSampleConstants.CLIENT_ID,
                    BrivoSampleConstants.CLIENT_SECRET,
                    true, false));
        } catch (BrivoSDKInitializationException e) {
            e.printStackTrace();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.d(Utilities.TAG, "App is in background");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("isBackground", true).apply();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        Log.d(Utilities.TAG, "App is in foreground");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("isBackground", false).apply();
    }
}
