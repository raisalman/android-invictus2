package net.invictusmanagement.invictuslifestyle.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRegistrationIntentService extends IntentService {

    public ChatRegistrationIntentService() {
        super(Utilities.TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            registrationId(task.getResult());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void registrationId(String firebaseToken) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String activationCode = sharedPreferences.getString("activationCode", null);
            if (!TextUtils.isEmpty(activationCode)) {
//                if (!sharedPreferences.getString("firebaseToken", "").equals(firebaseToken)) {
                NotificationHub hub = new NotificationHub(BuildConfig.VideoHubName,
                        BuildConfig.HubListenConnectionString, this);
                Log.d(Utilities.TAG, "Notification Hubs Registration refreshing with firebase token: " + firebaseToken);
//                Toast.makeText(this, "Notification Hubs Registration refreshing with firebase token: " + firebaseToken, Toast.LENGTH_LONG).show();
                Log.d(Utilities.TAG, "Activation Code: " + activationCode);
                registerHub(hub, firebaseToken, activationCode);
//                }
            }
        } catch (Exception ex) {
            Log.e(Utilities.TAG, "Failed to complete push notification registration", ex);
        }
    }

    private void registerHub(NotificationHub hub, String firebaseToken, String activationCode) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String template = "{ \"data\": { \"id\": \"$(id)\"," +
                " \"caller\": \"$(caller)\", \"room\": \"$(room)\"," +
                " \"accessPointName\": \"$(accessPointName)\"," +
                " \"alert\": \"$(alert)\"," +
                " \"validUntilDateUtc\": \"$(validUntilDateUtc)\" } }";
        executor.execute(() -> {
            try {
                hub.unregister();
                String registrationId = hub.registerTemplate(firebaseToken,
                        "VideoChatTemplate", template,
                        activationCode).getRegistrationId();
                //Background work here
                handler.post(() -> {
                    Log.d(Utilities.TAG,
                            "New Notification Hubs Registration Successful: " + registrationId);
//                    Toast.makeText(this, "New Notification Hubs Registration Successful: " + registrationId, Toast.LENGTH_LONG).show();

                    sharedPreferences.edit().putString("firebaseToken", firebaseToken).apply();

                });
            } catch (Exception e) {
                Log.e(Utilities.TAG, "Exception >> " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

}
