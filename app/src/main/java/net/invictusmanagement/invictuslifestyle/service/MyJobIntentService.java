package net.invictusmanagement.invictuslifestyle.service;

import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_ACCESSPOINT_NAME;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_ACCESS_POINT_ID;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_ALERT;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_CALLER_NAME;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_ROOM_ID;
import static net.invictusmanagement.invictuslifestyle.service.NotificationSoundService.EXTRA_SOUND_URI;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import net.invictusmanagement.invictuslifestyle.activities.ChatActivity;

public class MyJobIntentService extends JobIntentService {
    final Handler mHandler = new Handler();
    private static final String TAG = "MyJobIntentService";
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 2;

    public static final String EXTRA_SOUND_URI = "soundUri";

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, MyJobIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showToast("Job Execution Started");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent1) {
        /**
         * Write code here.. Perform Long operation here such as Download/Upload of file, Sync Some data
         * The system or framework is already holding a wake lock for us at this point
         */

        Intent intent = new Intent(getApplicationContext(), NotificationSoundService.class);
        intent.setAction(NotificationSoundService.ACTION_START_PLAYBACK);
        intent.putExtra(EXTRA_SOUND_URI, intent.getExtras().getString(EXTRA_SOUND_URI));
        intent.putExtra(ChatActivity.EXTRA_ACCESS_POINT_ID, intent.getExtras().getString(EXTRA_ACCESS_POINT_ID));
        intent.putExtra(ChatActivity.EXTRA_CALLER_NAME, intent.getExtras().getString(EXTRA_CALLER_NAME));
        intent.putExtra(ChatActivity.EXTRA_ACCESSPOINT_NAME, intent.getExtras().getString(EXTRA_ACCESSPOINT_NAME));
        intent.putExtra(ChatActivity.EXTRA_ROOM_ID, intent.getExtras().getString(EXTRA_ROOM_ID));
        getApplicationContext().startForegroundService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showToast("Job Execution Finished");
    }

    // Helper for showing tests
    void showToast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyJobIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}