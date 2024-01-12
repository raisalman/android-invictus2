package net.invictusmanagement.invictuslifestyle.service;

import static net.invictusmanagement.invictuslifestyle.service.FirebaseService.NOTIFICATION_CHANNEL_ID;
import static net.invictusmanagement.invictuslifestyle.service.FirebaseService.NOTIFICATION_CHANNEL_NAME;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.ChatActivity;

import java.util.Objects;

public class NotificationSoundService extends Service {

    private MediaPlayer mMediaPlayer;
    public static final String ACTION_START_PLAYBACK = "start_playback";
    public static final String ACTION_STOP_PLAYBACK = "stop_playback";
    public static final String EXTRA_SOUND_URI = "soundUri";
    public final static String EXTRA_ACCESS_POINT_ID = "net.invictusmanagement.invictusmobile.Access.Point.Id";
    public final static String EXTRA_CALLER_NAME = "net.invictusmanagement.invictusmobile.Caller.Name";
    public final static String EXTRA_ACCESSPOINT_NAME = "net.invictusmanagement.invictusmobile.AccessPoint.Name";
    public final static String EXTRA_ROOM_ID = "net.invictusmanagement.invictusmobile.Room.Id";
    public final static String EXTRA_ALERT = "net.invictusmanagement.invictusmobile.Alert";
    private Vibrator mvibrator;

    private String CHANNEL_ID = "VoipChannel";
    private String CHANNEL_NAME = "Voip Channel";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null || intent.getAction() == null) {
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        switch (action) {
            case ACTION_START_PLAYBACK:
                startSound();
                showNotification(intent);
//                try {
//                    Intent notificationIntent = new Intent(this, ChatActivity.class);
//                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    notificationIntent.putExtra(ChatActivity.EXTRA_ACCESS_POINT_ID, intent.getExtras().getString(EXTRA_ACCESS_POINT_ID));
//                    notificationIntent.putExtra(ChatActivity.EXTRA_CALLER_NAME, intent.getExtras().getString(EXTRA_CALLER_NAME));
//                    notificationIntent.putExtra(ChatActivity.EXTRA_ACCESSPOINT_NAME, intent.getExtras().getString(EXTRA_ACCESSPOINT_NAME));
//                    notificationIntent.putExtra(ChatActivity.EXTRA_ROOM_ID, intent.getExtras().getString(EXTRA_ROOM_ID));
//                    PendingIntent contentIntent = PendingIntent.getActivity(this,
//                            0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                    Uri soundUri = Uri.parse(sharedPreferences.getString("chat_ringtone", String.valueOf(Settings.System.DEFAULT_RINGTONE_URI)));
//
//                    createChannel(intent);
//                    NotificationCompat.Builder notificationBuilder;
//
//                    notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                            .setContentTitle(intent.getExtras().getString(EXTRA_CALLER_NAME))
//                            .setContentText(intent.getExtras().getString(EXTRA_ALERT))
//                            .setSmallIcon(R.mipmap.ic_launcher_original)
//                            .setPriority(NotificationCompat.PRIORITY_HIGH)
////                            .setCategory(NotificationCompat.CATEGORY_CALL)
//                            .setAutoCancel(true)
//                            .setSound(null)
//                            .setFullScreenIntent(contentIntent, true);
//
//                    Notification incomingCallNotification;
//                    incomingCallNotification = notificationBuilder.build();
//                    startForeground(120, incomingCallNotification);
//                } catch (Exception e0) {
//                    e0.printStackTrace();
//                }
                break;
            case ACTION_STOP_PLAYBACK:
                stopSound();
                break;
        }

        return START_NOT_STICKY;
    }

    private void showNotification(Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel;

        try {
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription(intent.getExtras().getString(EXTRA_ALERT));
            mChannel.setSound(null, null);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle(intent.getExtras().getString(EXTRA_CALLER_NAME));
            builder.setContentText(intent.getExtras().getString(EXTRA_ALERT));
            builder.setSmallIcon(R.mipmap.ic_launcher_original);
            builder.setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setVibrate(new long[]{0, 500, 1000})
                    .setDefaults(Notification.DEFAULT_LIGHTS);
            builder.setSound(null);
//            mNotificationManager.createNotificationChannel(notificationChannel);

            Intent notificationIntent = new Intent(this, ChatActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.putExtra(ChatActivity.EXTRA_ACCESS_POINT_ID, intent.getExtras().getString(EXTRA_ACCESS_POINT_ID));
            notificationIntent.putExtra(ChatActivity.EXTRA_CALLER_NAME, intent.getExtras().getString(EXTRA_CALLER_NAME));
            notificationIntent.putExtra(ChatActivity.EXTRA_ACCESSPOINT_NAME, intent.getExtras().getString(EXTRA_ACCESSPOINT_NAME));
            notificationIntent.putExtra(ChatActivity.EXTRA_ROOM_ID, intent.getExtras().getString(EXTRA_ROOM_ID));
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            builder.setContentIntent(contentIntent);
            Notification incomingCallNotification = builder.build();
            int NOTIFICATION_ID = 123;
            startForeground(NOTIFICATION_ID, incomingCallNotification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createChannel(Intent intent) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(intent.getExtras().getString(EXTRA_ALERT));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Uri soundUri = Uri.parse(sharedPreferences.getString("chat_ringtone", String.valueOf(Settings.System.DEFAULT_RINGTONE_URI)));
//        channel.setSound(null,
//                new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                        .setLegacyStreamType(AudioManager.STREAM_RING)
//                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());
        Objects.requireNonNull(getApplicationContext().getSystemService(NotificationManager.class)).createNotificationChannel(channel);
    }

    private void startSound() {
        Uri soundUri;
        AudioManager audioManager;
        boolean status = false;
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            soundUri = Uri.parse(sharedPreferences.getString("chat_ringtone", String.valueOf(Settings.System.DEFAULT_RINGTONE_URI)));

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    status = true;
                }
            }

            // play sound
            if (status) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();

                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();

                    mMediaPlayer.setAudioAttributes(audioAttributes);
                    mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
                }

                mMediaPlayer.setDataSource(this, soundUri);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepareAsync();
            } else {
                mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Start without a delay
                // Each element then alternates between vibrate, sleep, vibrate, sleep...
                long[] pattern = {0, 250, 200, 250, 150, 150, 75,
                        150, 75, 150};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                mvibrator.vibrate(pattern, -1);
            }
        } catch (Exception e) {
            stopSound();
        }
    }

    private void stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        try {
            if (mvibrator != null) {
                if (mvibrator.hasVibrator()) {
                    mvibrator.cancel();
                }
                mvibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cleanup();
    }

    private void cleanup() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();// release your media player here audioManager.abandonAudioFocus(afChangeListener);
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        try {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                }
                mMediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}