package net.invictusmanagement.invictuslifestyle.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

import net.invictusmanagement.invictuslifestyle.BuildConfig;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utilities {

    public static final String _baseUrl = BuildConfig._baseUrl;
    public static final boolean IS_RELEASE = false;

    public static final int OPERATOR_INVICTUS = 1;
    public static final int OPERATOR_OPENPATH = 2;
    public static final int OPERATOR_BRIVO = 3;
    public static final int OPERATOR_PDK = 4;

    public static final String TAG = "InvictusMobile";
    /*private static CrashManagerListener _crashManagerListener;*/
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String NO_INTERNET = "No network available.";
    public static final String NO_WIFI = "Network Error. Unable to connect to Invictus. Please try again";

    public static final String EXTRA_BUSINESS_TYPE_JSON = "net.invictusmanagement.invictusmobile.business.type";


    public static String getFileNameFromUrl(URL url) {
        String urlString = url.getFile();
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }

    public static void sendEmail(Activity context, String emailAddress, String subject, String body) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);

            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            ResolveInfo best = null;
            for (int i = 0; i < matches.size(); i++) {
                if (matches.get(i).activityInfo.packageName.endsWith(".gm") ||
                        matches.get(i).activityInfo.name.toLowerCase(Locale.getDefault()).contains("gmail")
                ) {
                    best = matches.get(i);
                }
            }

            if (best != null) emailIntent.setClassName(
                    best.activityInfo.packageName, best.activityInfo.name);
            context.startActivity(emailIntent);
        } catch (Exception e) {
            Log.d(TAG, "sendEmail: " + (e.getLocalizedMessage()));
        }
    }

    public static File compressedFile(File file) {
        return file;
    }

    public interface onDateTimePickerChangedListener {
        void dateTimeChanged(Calendar date);
    }

    public static void showLongToast(Context _context, String message) {
        Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
    }

    public void showShortToast(Context _context, String message) {
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
    }

}
