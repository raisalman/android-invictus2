package net.invictusmanagement.invictuslifestyle.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendMessage(this);
        return Service.START_STICKY;

    }

    public static void sendMessage(Context context) {
        /*Toast.makeText(context, " Service Started", Toast.LENGTH_LONG).show();*/
        Log.d("TAGS", "MainActivity.send()");
        Intent sendableIntent = new Intent("notificationReceived");
        sendableIntent.putExtra("result", "risult");
        LocalBroadcastManager.getInstance(context).sendBroadcast(sendableIntent);
        Log.d("TAGS", "sended_data");
    }

    @Override
    public void onCreate() {
        /*Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();*/

    }

    @Override
    public void onDestroy() {
        /*Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();*/

    }
}