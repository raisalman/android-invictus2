package net.invictusmanagement.invictuslifestyle.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import net.invictusmanagement.invictuslifestyle.customviews.CustomizedExceptionHandler;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (!BuildConfig.DEBUG) {
            MetricsManager.register(getApplication());
        }*/
        // Sets the default uncaught exception handler. This handler is invoked
        // in case any Thread dies due to an unhandled exception.
        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler(
                "/mnt/sdcard/", this));

//        String nullString = null;
//        System.out.println(nullString.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        /*CrashManager.register(this, Utilities.createCrashManagerListerner(this));*/
    }
}
