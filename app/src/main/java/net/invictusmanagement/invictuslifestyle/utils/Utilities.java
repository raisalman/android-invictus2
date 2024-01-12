package net.invictusmanagement.invictuslifestyle.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.BaseActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.customviews.SlideView;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Utilities {

    public static final String _baseUrl = BuildConfig._baseUrl;
    public static final String _baseUrlEEN = BuildConfig._baseUrlEEN;
    public static final String _baseUrlBrivoSmartHome = BuildConfig._baseUrlBrivoSmartHome;
    public static final boolean IS_RELEASE = false;


    public static final String TAG = "InvictusMobile";
    /*private static CrashManagerListener _crashManagerListener;*/
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String NO_INTERNET = "No network available.";
    public static final String NO_WIFI = "Network Error. Unable to connect to Invictus. Please try again";

    public static final String EXTRA_BUSINESS_TYPE_JSON = "net.invictusmanagement.invictusmobile.business.type";

    public static final int OPERATOR_INVICTUS = 1;
    public static final int OPERATOR_OPENPATH = 2;
    public static final int OPERATOR_BRIVO = 3;
    public static final int OPERATOR_PDK = 4;

    public static final String FRAGMENT_HOME = "Home";
    public static final String FRAGMENT_ACCESS_POINTS = "Access Points";
    public static final String FRAGMENT_BRIVO_DEVICES = "Smart Home";
    public static final String FRAGMENT_PROMOTIONS = "Coupons";
    public static final String FRAGMENT_DIGITAL_KEYS = "Digital Keys";
    public static final String FRAGMENT_SERVICE_KEYS = "Service Keys";
    public static final String FRAGMENT_NOTIFICATIONS = "Notifications";
    public static final String FRAGMENT_RENTAL_TOOL = "Renter Tool";
    public static final String FRAGMENT_HEALTH = "Health & Wellness";
    public static final String FRAGMENT_VOICE_MAIL = "Voice Mail";
    public static final String FRAGMENT_BILLBOARD = "Bulletin Board";
    public static final String FRAGMENT_CHAT = "General Chat";
    public static final String FRAGMENT_COMMUNITY_NOTIFICATIONS = "Community Notifications";

    public static String formatPhone(String phone) {
        if (phone != null) {
            if (phone.length() == 10) {
                return "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6);
            }
        }
        return phone;
    }

    public static void showDatePicker(final Context context, Calendar initialDate, final onDateTimePickerChangedListener listener) {
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                listener.dateTimeChanged(date);
            }
        }, initialDate.get(Calendar.YEAR), initialDate.get(Calendar.MONTH), initialDate.get(Calendar.DATE)).show();
    }

    public static void showDatePickerWithMinDate(final Context context, Calendar initialDate, final onDateTimePickerChangedListener listener) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, month, dayOfMonth);
                listener.dateTimeChanged(date);
            }
        }, initialDate.get(Calendar.YEAR), initialDate.get(Calendar.MONTH), initialDate.get(Calendar.DATE));
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    public static void showDatePickerWithMaxDate(final Context context, Calendar initialDate,
                                                 final onDateTimePickerChangedListener listener) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, month, dayOfMonth);
                listener.dateTimeChanged(date);
            }
        }, initialDate.get(Calendar.YEAR), initialDate.get(Calendar.MONTH), initialDate.get(Calendar.DATE));
        initialDate.set(Calendar.YEAR, 1);
        datePickerDialog.getDatePicker().setMaxDate(initialDate.getTimeInMillis());
        datePickerDialog.show();
    }

    public static void showDatePickerWithMinMaxDate(int advanceBookingDays, final Context context, FragmentManager supportFragmentManager, Calendar datePicker, final onDateTimePickerChangedListener listener) {

        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar date = Calendar.getInstance();
                        date.set(year, monthOfYear, dayOfMonth);// Inital day selection
                        listener.dateTimeChanged(date);
                    }
                },
                datePicker.get(Calendar.YEAR),
                datePicker.get(Calendar.MONTH),
                datePicker.get(Calendar.DAY_OF_MONTH)


        );
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, advanceBookingDays);
        dpd.setMinDate(Calendar.getInstance());
        if (advanceBookingDays != -1) {
            dpd.setMaxDate(gc);
        }

        dpd.show(supportFragmentManager, "DatePickerDialog");
    }

    public static void showTimePicker(final Context context, Calendar initialTime, final onDateTimePickerChangedListener listener) {
        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar date = Calendar.getInstance();
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                listener.dateTimeChanged(date);
            }
        }, initialTime.get(Calendar.HOUR_OF_DAY), initialTime.get(Calendar.MINUTE), false).show();
    }

    public static void showTimePickerTo(Boolean isSameday, FragmentManager supportFragmentManager, final Context context, Calendar initialTime, final onDateTimePickerChangedListener listener) {
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                        Calendar date = Calendar.getInstance();
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        listener.dateTimeChanged(date);
                    }
                },
                initialTime.get(Calendar.HOUR_OF_DAY),
                initialTime.get(Calendar.MINUTE),
                false
        );
        tpd.setMinTime(isSameday ? initialTime.get(Calendar.HOUR_OF_DAY) : 0, isSameday ? initialTime.get(Calendar.MINUTE) : 0, 0); // MIN: hours, minute, secconds
        tpd.show(supportFragmentManager, "TimePickerDialog");
    }

    public static void showTimePickerTo(Boolean isSameday, FragmentManager supportFragmentManager, final Context context, Calendar initialTime, Calendar setTime, final onDateTimePickerChangedListener listener) {
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                        Calendar date = Calendar.getInstance();
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        listener.dateTimeChanged(date);
                    }
                },
                setTime.get(Calendar.HOUR_OF_DAY),
                setTime.get(Calendar.MINUTE),
                true
        );
        tpd.setMinTime(isSameday ? initialTime.get(Calendar.HOUR_OF_DAY) : 0, isSameday ? initialTime.get(Calendar.MINUTE) : 0, 0); // MIN: hours, minute, secconds
        tpd.show(supportFragmentManager, "TimePickerDialog");
    }

    public static void showTimePickerFrom(FragmentManager supportFragmentManager, final Context context, Calendar initialTime, final onDateTimePickerChangedListener listener) {
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                        Calendar date = Calendar.getInstance();
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        listener.dateTimeChanged(date);
                    }
                },
                initialTime.get(Calendar.HOUR_OF_DAY),
                initialTime.get(Calendar.MINUTE),
                false
        );
        tpd.setMinTime(initialTime.get(Calendar.HOUR_OF_DAY), initialTime.get(Calendar.MINUTE), 0); // MIN: hours, minute, secconds
        tpd.show(supportFragmentManager, "TimePickerDialog");
    }

    public static void showTimePickerFromWithMaxHour(FragmentManager supportFragmentManager, Calendar initialTime, Calendar _fromDateTime, Calendar setTime, String maxHour, String maxMin, final onDateTimePickerChangedListener listener) {
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                        Calendar date = Calendar.getInstance();
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        listener.dateTimeChanged(date);
                    }
                },
                setTime.get(Calendar.HOUR_OF_DAY),
                setTime.get(Calendar.MINUTE),
                true
        );
        tpd.setMinTime(new Timepoint(initialTime.get(Calendar.HOUR_OF_DAY), initialTime.get(Calendar.MINUTE), 0)); // MIN: hours, minute, secconds
        if (Integer.parseInt(maxHour) != 0) {
            int hour = _fromDateTime.get(Calendar.HOUR_OF_DAY) + Integer.parseInt(maxHour);
            int min = _fromDateTime.get(Calendar.MINUTE) + Integer.parseInt(maxMin);
            if (min >= 60) {
                if (hour >= 24) {
                    hour = 22;
                    min = 59;
                }
                tpd.setMaxTime(new Timepoint(hour + 1, min, 0)); // MAX: hours, minute, secconds
            } else {
                if (hour >= 24) {
                    hour = 23;
                    min = 59;
                }
                tpd.setMaxTime(new Timepoint(hour, min, 0)); // MAX: hours, minute, secconds
            }
        } else {
            if (Integer.parseInt(maxMin) > 20) {
                int hour = _fromDateTime.get(Calendar.HOUR_OF_DAY) + Integer.parseInt(maxHour);
                int min = _fromDateTime.get(Calendar.MINUTE) + Integer.parseInt(maxMin);
                if (min >= 60) {
                    if (hour >= 24) {
                        hour = 22;
                        min = 59;
                    }
                    tpd.setMaxTime(new Timepoint(hour + 1, min, 0)); // MAX: hours, minute, secconds
                } else {
                    if (hour >= 24) {
                        hour = 23;
                        min = 59;
                    }
                    tpd.setMaxTime(new Timepoint(hour, min, 0)); // MAX: hours, minute, secconds
                }
            } else {
                tpd.setMaxTime(new Timepoint(23, 59, 0));
            }

        }

        tpd.show(supportFragmentManager, "TimePickerDialog");
    }

    public static void showHide(final Context context, final View view, final Boolean show) {

        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        view.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public static void showInvisible(final Context context, final View view, final Boolean show) {

        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        view.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    public static void showDiscardChangesDialog(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Discard changes?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                NavUtils.navigateUpFromSameTask(context);
            }
        });
        builder.show();
    }

    public static void showAboutDialog(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_about, null);
        builder.setView(view)
                .setTitle("About Invictus Lifestyle");
        try {
            ((TextView) view.findViewById(R.id.version)).setText("Version: " + context.getApplication().getPackageManager().getPackageInfo(context.getApplication().getPackageName(), 0).versionName);
        } catch (Exception ex) {
            Log.e(Utilities.TAG, Log.getStackTraceString(ex));
        }
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

 /*   public static void progressDialog(final Activity activity, boolean show) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.progress_dialog);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        if (show) dialog.show();
        else dialog.dismiss();
    }*/

    public final static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /*public static void saveException(Throwable exception, Context context) {
        ExceptionHandler.saveException(exception, Thread.currentThread(), createCrashManagerListerner(context));
    }*/

    /*public static CrashManagerListener createCrashManagerListerner(final Context context) {
        if (_crashManagerListener == null) {
            _crashManagerListener = new CrashManagerListener() {
                @Override
                public String getUserID() {
                    long userId = PreferenceManager.getDefaultSharedPreferences(context).getLong("userId", 0);
                    return userId > 0 ? String.valueOf(userId) : super.getUserID();
                }

                @Override
                public boolean shouldAutoUploadCrashes() {
                    return true;
                }
            };
        }
        return _crashManagerListener;
    }*/

    public static void converDateWithFormatter(String date, TextView textView, String from, String to) {
        String strCurrentDate = date;
        SimpleDateFormat format = new SimpleDateFormat(from);
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat(to);
        String datee = format.format(newDate);
        textView.setText(datee);
    }

    public final static boolean checkPlayServices(final BaseActivity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable((Context) activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((Activity) activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(Utilities.TAG, "This device is not supported by Google Play Services.");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText((Context) activity, "This device is not supported by Google Play Services.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return false;
        }
        return true;
    }

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

    public static void addHaptic(SlideView slideView) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(slideView.getContext());
        if (preferences.getBoolean("isHapticOn", false)) {
            slideView.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
            );
        }
    }

    public static void addHaptic(TextView slideView) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(slideView.getContext());
        if (preferences.getBoolean("isHapticOn", false)) {
            slideView.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
            );
        }
    }

    public static void addHaptic(Button slideView) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(slideView.getContext());
        if (preferences.getBoolean("isHapticOn", false)) {
            slideView.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
            );
        }
    }

    public static void addHaptic(LinearLayout slideView) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(slideView.getContext());
        if (preferences.getBoolean("isHapticOn", false)) {
            slideView.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
            );
        }
    }

    public static void addHaptic(View slideView) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(slideView.getContext());
        if (preferences.getBoolean("isHapticOn", false)) {
            slideView.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
            );
        }
    }

    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }
}
