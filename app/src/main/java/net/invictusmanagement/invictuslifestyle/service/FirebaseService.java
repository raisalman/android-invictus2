package net.invictusmanagement.invictuslifestyle.service;

import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_ACCESSPOINT_NAME;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_ACCESS_POINT_ID;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_ALERT;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_CALLER_NAME;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_ROOM_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.microsoft.windowsazure.messaging.NotificationHub;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.ChatActivity;
import net.invictusmanagement.invictuslifestyle.activities.MainActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseService extends FirebaseMessagingService {
    public static String TAG = "FirebaseService";

    public static final String NOTIFICATION_CHANNEL_ID = "12345";
    public static final String NOTIFICATION_CHANNEL_NAME = "Notification Channel";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Notification Hubs Demo Channel";

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private String FCM_token;

    private String CHANNEL_ID = "VoipChannel";
    private String CHANNEL_NAME = "Voip Channel";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FCM_token = token;
        startService(new Intent((Context) this, ChatRegistrationIntentService.class));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String activationCode = sharedPreferences.getString("activationCode", null);
        if (!TextUtils.isEmpty(activationCode)) {
            try {
                registrationId(sharedPreferences, activationCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(Utilities.TAG, "Push notification received.");
        JSONObject json = null;
        boolean startActivity = false;
        String validUntilDateUtc = "";

        Log.d(Utilities.TAG, "Data >> " + remoteMessage.getData() + "");
        //{body=You have new key for guest access from 'Krish Android'., title=New Key Is Generated , notificationType=1}

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                json = new JSONObject(remoteMessage.getData());
                validUntilDateUtc = json.getString("validUntilDateUtc");
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        wakeUpLock();
        if (!TextUtils.isEmpty(validUntilDateUtc)) {
            try {
                startActivity = true;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                ParsePosition parsePosition = new ParsePosition(0);
                Date validUntilDateLocal = formatter.parse(validUntilDateUtc, parsePosition);
                startActivity = new Date().before(validUntilDateLocal);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
//            int notificationType = remoteMessage.getNotification().get
            if (title != null & body != null) {
                showNotification(title, body, ctx);
                //Api call when notification received
                NotificationService.sendMessage(ctx);
            }
        }

        try {
            if (startActivity && json != null) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (!sharedPreferences.getBoolean("isBackground", true)) {
                    Log.d(Utilities.TAG, "Starting chat activity.");
                    Intent intent = new Intent(ctx, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent.putExtra(ChatActivity.EXTRA_ACCESS_POINT_ID, json.getString("id"));
                    intent.putExtra(ChatActivity.EXTRA_CALLER_NAME, json.getString("caller"));
                    intent.putExtra(ChatActivity.EXTRA_ACCESSPOINT_NAME, json.getString("accessPointName"));
                    intent.putExtra(ChatActivity.EXTRA_ROOM_ID, json.getString("room"));
                    ctx.startActivity(intent);
                } else {
                    showCallingNotification(json, ctx);
                }
//                showCallingNotification(json, ctx);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showCallingNotification(JSONObject json, Context context) {
        try {

            Intent notificationIntent = new Intent(this, ChatActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.putExtra(EXTRA_ALERT, json.getString("alert"));
            notificationIntent.putExtra(EXTRA_ACCESS_POINT_ID, json.getString("id"));
            notificationIntent.putExtra(EXTRA_CALLER_NAME, json.getString("caller"));
            notificationIntent.putExtra(EXTRA_ACCESSPOINT_NAME, json.getString("accessPointName"));
            notificationIntent.putExtra(EXTRA_ROOM_ID, json.getString("room"));
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Uri soundUri = Uri.parse(sharedPreferences.getString("chat_ringtone", String.valueOf(Settings.System.DEFAULT_RINGTONE_URI)));

            NotificationManager notificationManager = createChannel(json.getString("alert"));
            NotificationCompat.Builder notificationBuilder;

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(json.getString("caller"))
                    .setContentText(json.getString("alert"))
                    .setSmallIcon(R.mipmap.ic_launcher_original)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setColor(getColor(R.color.blue_6E97D3))
                    .setColorized(true)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setVibrate(new long[]{0, 250, 200, 250, 150, 150, 75,
                            150, 75, 150})
                    .setAutoCancel(false)
                    .setSound(soundUri)
                    .setFullScreenIntent(contentIntent, true);

            Notification notification = notificationBuilder.build();
            notification.flags = Notification.FLAG_INSISTENT;
            notificationManager.notify(1234, notification);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//
//            } else {
//                //start foreground service for the sound and notification
//                Intent intent = new Intent(getApplicationContext(), NotificationSoundService.class);
//                intent.setAction(NotificationSoundService.ACTION_START_PLAYBACK);
//                intent.putExtra(EXTRA_ALERT, json.getString("alert"));
//                intent.putExtra(EXTRA_ACCESS_POINT_ID, json.getString("id"));
//                intent.putExtra(EXTRA_CALLER_NAME, json.getString("caller"));
//                intent.putExtra(EXTRA_ACCESSPOINT_NAME, json.getString("accessPointName"));
//                intent.putExtra(EXTRA_ROOM_ID, json.getString("room"));
//                ContextCompat.startForegroundService(getApplicationContext(), intent);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public NotificationManager createChannel(String intent) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(intent);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Uri soundUri = Uri.parse(sharedPreferences.getString("chat_ringtone", String.valueOf(Settings.System.DEFAULT_RINGTONE_URI)));
        channel.setSound(soundUri,
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setLegacyStreamType(AudioManager.STREAM_RING)
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setFlags(Notification.FLAG_INSISTENT)
                        .build());
        channel.enableVibration(true);
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        return notificationManager;
    }

    public static void showNotification(String title, String body, Context context) {
        NotificationCompat.Builder notificationBuilder;

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("isFromNotification", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationChannelId = context.getPackageName();

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(notificationChannelId, notificationChannelId, importance);
        mChannel.setDescription("Channel Description");
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.setShowBadge(false);
        notificationManager.createNotificationChannel(mChannel);
        notificationBuilder = new NotificationCompat.Builder(context, notificationChannelId);

        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher_original)
                .setContentTitle(title)
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(body)
                )
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setChannelId(notificationChannelId);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("isPushSilent", false)) {
            notificationBuilder.setSound(null);
            notificationBuilder.setSilent(true);
        }

        if (TabbedActivity.isVisible || ChatActivity.isVisible) {
            notificationManager.notify(new SecureRandom().nextInt(), notificationBuilder.build());
        }

        if (TabbedActivity.isVisible) {
            TabbedActivity.showNotificationDialog(title, body);
        }
    }

    public static void createChannelAndHandleNotifications(Context context) {
        ctx = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            channel.setShowBadge(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Register Hub with fcm token and activation code for Message/Voice Hub
     */
    private void registrationId(SharedPreferences sharedPreferences, String activationCode) {
        String regID;
        String resultString;

        regID = sharedPreferences.getString("registrationID", null);
        if (regID == null) {
            NotificationHub hub = new NotificationHub(BuildConfig.HubName,
                    BuildConfig.MsgHubListenConnectionString, this);
            registerHub(hub, activationCode, sharedPreferences, FCM_token);
        } else if (!(sharedPreferences.getString("FCMtoken", "")).equals(FCM_token)) {
            // Check to see if the token has been compromised and needs refreshing.
            NotificationHub hub = new NotificationHub(BuildConfig.HubName,
                    BuildConfig.MsgHubListenConnectionString, this);
            /*regID = hub.register(FCM_token,activationCode).getRegistrationId();*/
            registerHub(hub, activationCode, sharedPreferences, FCM_token);
        } else {
            resultString = "Previously Registered Successfully - RegId : " + regID;
            Log.d(TAG, resultString + " >> FCM" + FCM_token);
        }
    }

    /**
     * Registering hub in background else will throw main thread exception
     */
    public void registerHub(NotificationHub hub, String activationCode,
                            SharedPreferences sharedPreferences, String FCM_token) {
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

    private void wakeUpLock() {
        PowerManager powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "InvictusLifeStyle::WakeLock");
        //acquire will turn on the display
        wakeLock.acquire();
        //release will release the lock from CPU, in case of that, screen will go back to sleep mode in defined time bt device settings
        wakeLock.release();
    }
}