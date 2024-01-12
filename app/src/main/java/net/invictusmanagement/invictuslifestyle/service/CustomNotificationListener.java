//package net.invictusmanagement.invictuslifestyle.service;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.google.firebase.messaging.RemoteMessage;
//import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener;
//
//import net.invictusmanagement.invictuslifestyle.utils.Utilities;
//
//import java.util.Map;
//
//public class CustomNotificationListener implements NotificationListener {
//
//    private static final String TAG = "CustomNotification";
//
//    @Override
//    public void onPushNotificationReceived(Context context, RemoteMessage message) {
//        /* The following notification properties are available. */
//        Log.d(Utilities.TAG, "Push notification received.");
//        RemoteMessage.Notification notification = message.getNotification();
//        String title = notification.getTitle();
//        String body = notification.getBody();
//        Map<String, String> data = message.getData();
//
//        if (message != null) {
//            Log.d(TAG, "Message Notification Title: " + title);
//            Log.d(TAG, "Message Notification Body: " + message);
//        }
//
//        if (data != null) {
//            for (Map.Entry<String, String> entry : data.entrySet()) {
//                Log.d(TAG, "key, " + entry.getKey() + " value " + entry.getValue());
//            }
//        }
//    }
//}