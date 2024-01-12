package net.invictusmanagement.invictuslifestyle.activities;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;

import net.invictusmanagement.invictuslifestyle.MyApplication;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.models.AuthenticationResult;
import net.invictusmanagement.invictuslifestyle.models.Login;
import net.invictusmanagement.invictuslifestyle.models.User;
import net.invictusmanagement.invictuslifestyle.utils.BrivoSampleConstants;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.ResponseBody;

public class VerifyOTPActivity extends BaseActivity {

    private EditText etOTP;
    private TextView tvTimer;
    private LinearLayout llTimer;
    private TextView tvResend;
    private Button btnSubmit;
    private long userId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_otp);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Verify One Time Password");
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getIntent().getExtras() != null) {
            userId = getIntent().getExtras().getLong("ID");
            email = getIntent().getExtras().getString("EMAIL");
        }

        etOTP = findViewById(R.id.otp);
        tvTimer = findViewById(R.id.tvTimer);
        tvResend = findViewById(R.id.btnResend);
        llTimer = findViewById(R.id.llTimer);
        btnSubmit = findViewById(R.id.btnSubmit);

        startTimer();
        setRegisters();

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPItoResendOTP();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etOTP.setError(null);
                if (etOTP.getText().toString().length() == 0) {
                    etOTP.setError("Required this field");
                    etOTP.requestFocus();
                } else {
                    callAPItoVerifyOtp();
                }
            }
        });
    }

    private void setRegisters() {
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null);
        Task<Void> task = SmsRetriever.getClient(this).startSmsUserConsent(null);
    }

    private void callAPItoVerifyOtp() {
        Login payload = new Login();
        payload.email = email;
        payload.applicationUserId = userId;
        payload.isLoginWithOTP = true;
        payload.otp = etOTP.getText().toString();
        TimeZone timezone = TimeZone.getDefault();
        payload.timeZoneOffset = timezone.getOffset(Calendar.ZONE_OFFSET) / 1000 / 60;
        payload.deviceId = Utilities.getDeviceID(VerifyOTPActivity.this);


        new AsyncTask<Login, Void, AuthenticationResult>() {

            private String _activationCode;

            @Override
            protected void onPreExecute() {
                Utilities.hideKeyboard(VerifyOTPActivity.this);
                ProgressDialog.showProgress(VerifyOTPActivity.this);
            }

            @Override
            protected AuthenticationResult doInBackground(Login... args) {
                try {
                    return MobileDataProvider.getInstance().verifyOTP(args[0]);
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(AuthenticationResult result) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(VerifyOTPActivity.this);
                if (result != null && !TextUtils.isEmpty(result.getAuthenticationCookie())) {

                    sharedPreferences.edit().putString("email", email).apply();
                    sharedPreferences.edit().putString("activationCode", result.getActivationCode()).apply();
                    sharedPreferences.edit().putLong("userId", result.getId()).apply();
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                    sharedPreferences.edit().putString("userRole", result.getRoleName()).apply();
                    sharedPreferences.edit().putString("authenticationCookie", result.getAuthenticationCookie()).apply();
                    MobileDataProvider.getInstance().setAuthenticationCookie(result.getAuthenticationCookie());

                    if (result.getRoleName().equals(getString(R.string.role_leasing_officer)) ||
                            result.getRoleName().equals(getString(R.string.role_property_manager)) ||
                            result.getRoleName().equals(getString(R.string.role_resident)) ||
                            result.getRoleName().equals(getString(R.string.role_vendor)) ||
                            result.getRoleName().equals(getString(R.string.role_facility))) {
                        sharedPreferences.edit().putBoolean("isGuestUser", false).apply();
                        getUserData();
                    } else {
                        sharedPreferences.edit().putBoolean("isGuestUser", true).apply();
                        Intent intent = new Intent(VerifyOTPActivity.this, TabbedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity();
                    }
                } else {
                    ProgressDialog.dismissProgress();
                    if (MobileDataProvider.loginErrorMessage.startsWith("301")) {

                        String currentString = MobileDataProvider.loginErrorMessage;
                        String[] separated = currentString.split("301");

                        new AlertDialog.Builder(VerifyOTPActivity.this)
                                .setCancelable(false)
                                .setMessage(separated[1].trim())
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        //dismiss dialog
                                    }
                                }).create().show();

                    } else {
                        new AlertDialog.Builder(VerifyOTPActivity.this)
                                .setCancelable(false)
                                .setMessage("Something went wrong, Please try again later.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        //dismiss dialog
                                    }
                                }).create().show();
                    }
                }
            }
        }.execute(payload);
    }

    private void getUserData() {
        ProgressDialog.showProgress(VerifyOTPActivity.this);
        WebService.getInstance().getUserData(new RestCallBack<User>() {
            @Override
            public void onResponse(User user) {
                ProgressDialog.dismissProgress();
                if (user != null) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(VerifyOTPActivity.this);
                    sharedPreferences.edit()
                            .putBoolean("enableBrivoIntegration", user.isEnableBrivoIntegration()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enablePDKIntegration", user.isEnablePDKIntegration()).apply();

                    if (user.isEnableBrivoIntegration()) {
                        BrivoSampleConstants.CLIENT_ID = user.getBrivoDoorAccessClientId();
                        BrivoSampleConstants.CLIENT_SECRET = user.getBrivoDoorAccessClientSecret();
                        ((MyApplication) getApplication()).initializeBrivoSDK();
                    }
                    sharedPreferences.edit()
                            .putString("peekId", user.getEenUserName()).apply();
                    sharedPreferences.edit()
                            .putString("peekPassword", user.getEenPassword()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enableEENIntegration", user.isEnableEENIntegration()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enableAVAIntegration", user.enableAVAIntegration).apply();
                    sharedPreferences.edit()
                            .putString("avaUserName", user.avaUserName).apply();
                    sharedPreferences.edit()
                            .putString("avaPassword", user.avaPassword).apply();
                    sharedPreferences.edit()
                            .putString("avaServerName", user.avaServerName).apply();
                    sharedPreferences.edit()
                            .putBoolean("isHapticOn", user.isHapticOn()).apply();
                    sharedPreferences.edit()
                            .putBoolean("isPushSilent", user.isPushSilent()).apply();
                    sharedPreferences.edit()
                            .putBoolean("enableOpenPathIntegration", user.isEnableOpenPathIntegration()).apply();
                    sharedPreferences.edit()
//                            .putBoolean("allowMaintenanceRequest", false).apply();
                            .putBoolean("allowMaintenanceRequest", user.isAllowMaintenanceRequest()).apply();
                    sharedPreferences.edit()
//                            .putBoolean("allowAmenitiesBooking", false).apply();
                            .putBoolean("allowAmenitiesBooking", user.isAllowAmenitiesBooking()).apply();
                    sharedPreferences.edit()
//                            .putBoolean("allowBulletinBoard", false).apply();
                            .putBoolean("allowBulletinBoard", user.isAllowBulletinBoard()).apply();
                    sharedPreferences.edit()
//                            .putBoolean("allowInsuranceRequest", false).apply();
                            .putBoolean("allowInsuranceRequest", user.isAllowInsuranceRequest()).apply();
                    sharedPreferences.edit()
//                            .putBoolean("allowGeneralChat", false).apply();
                            .putBoolean("allowGeneralChat", user.isAllowGeneralChat()).apply();
                    //enableRentPayment
                    sharedPreferences.edit()
//                            .putBoolean("enableRentPayment", false).apply();
                            .putBoolean("enableRentPayment", user.isEnableRentPayment()).apply();
                    //hasExtraIntegration
                    sharedPreferences.edit()
//                            .putBoolean("hasExtraIntegration", false).apply();
                            .putBoolean("hasExtraIntegration", user.isHasExtraIntegration()).apply();

                    Intent intent = new Intent(VerifyOTPActivity.this, TabbedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
            }
        });
    }


    private void callAPItoResendOTP() {
        Login payload = new Login();
        payload.email = email;
        payload.applicationUserId = userId;
        TimeZone timezone = TimeZone.getDefault();
        payload.timeZoneOffset = timezone.getOffset(Calendar.ZONE_OFFSET) / 1000 / 60;
        payload.deviceId = Utilities.getDeviceID(VerifyOTPActivity.this);
        WebService.getInstance().resendOTP(payload, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                if (response != null) {
                    startTimer();
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void startTimer() {

        tvResend.setVisibility(View.GONE);
        llTimer.setVisibility(View.VISIBLE);
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setText("00:" + millisUntilFinished / 1000);
                // logic to set the EditText could go here
            }

            public void onFinish() {
                tvResend.setVisibility(View.VISIBLE);
                llTimer.setVisibility(View.GONE);
            }
        }.start();
    }

    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code
    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get consent intent
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            // Handle the exception ...
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Time out occurred, handle the error.
                        break;
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SMS_CONSENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Get SMS message content
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                // Extract one-time code from the message and complete verification
                // `sms` contains the entire text of the SMS message, so you will need
                // to parse the string.
                String number = message.replaceAll("[^0-9]", "");
                etOTP.setText(number);
            }
        }
    }
}

