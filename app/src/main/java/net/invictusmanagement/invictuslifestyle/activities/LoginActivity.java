package net.invictusmanagement.invictuslifestyle.activities;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.MyApplication;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.enum_utils.NewDeviceRequestStatus;
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

public class LoginActivity extends BaseActivity {

    private EditText _emailView;
    private TextView deviceId;
    private EditText _activationCodeView;
    private View _progressView;
    private Button _activateButton;
    private Button btnLoginOTP;
    private TextView _tvGuestUser;
    private TextInputLayout _inputActivationCode;

    private LinearLayout llPasswordLogin, llLoginOTP;
    private ImageView imgArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("FromLogout", false)) {
                if (getIntent().getExtras().getBoolean("showMessage", false)) {
                    showLogoutDialog();
                }
            }
        }

        _tvGuestUser = findViewById(R.id.tvGuestUser);
        _tvGuestUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, GuestSignupActivity.class);
                startActivity(intent);

            }
        });

        _emailView = findViewById(R.id.email);
        deviceId=findViewById(R.id.deviceId);
        _activationCodeView = findViewById(R.id.activationCode);
        _progressView = findViewById(R.id.activation_progress);
        _inputActivationCode = findViewById(R.id.input_activation_code);
        _activateButton = findViewById(R.id.activate_button);
        btnLoginOTP = findViewById(R.id.btnLoginOTP);
        llLoginOTP = findViewById(R.id.llLoginOTP);
        llPasswordLogin = findViewById(R.id.llPasswordLogin);
        imgArrow = findViewById(R.id.imgArrow);
        llLoginOTP.setVisibility(View.GONE);
        llPasswordLogin.setVisibility(View.GONE);
        _activateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });
        _activateButton.setEnabled(Utilities.checkPlayServices(this));

        btnLoginOTP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(true);
            }
        });

        imgArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(_emailView.getText().toString())) {
//                    _emailView.setError(getString(R.string.error_field_required));
//                    _emailView.requestFocus();
                    showErrorDialog(getString(R.string.error_field_required));
                } else if (!Utilities.isValidEmail(_emailView.getText())) {
//                    _emailView.setError(getString(R.string.error_invalid_email));
//                    _emailView.requestFocus();
                    showErrorDialog(getString(R.string.error_invalid_email));
                } else {
                    callAPIForCheckUserDeviceLogin();
                }
            }
        });

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Toast.makeText(this,android_id,Toast.LENGTH_SHORT).show();
        deviceId.setText(android_id);
        _emailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imgArrow.performClick();
                }
                return false;
            }
        });

        if (BuildConfig.DEBUG) {
            //Resident
//            _emailView.setText("krishandroid@gmail.com");
//            _activationCodeView.setText("E76404F2-77DD-4722-9D66-88C5505FBB71");

//            _emailView.setText("taurusdev567@gmail.com");
//            _activationCodeView.setText("02baafc8-8a8a-4797-aa85-5cb3233be89f");

//            _emailView.setText("robert.gomez@brivo.com");
//            _activationCodeView.setText("2EBBB0E1-D0DA-4B38-B436-5E76795AE3B8");

//            _emailView.setText("krishlive@gmail.com");
//            _activationCodeView.setText("4E994682-ADE9-4132-8E6C-A4515B7A5260");

//            _emailView.setText("jaybhanderi866@gmail.com");
//            _activationCodeView.setText("E0CCFFC5-42B1-44F2-8F1B-70689F2279EA");

//            _emailView.setText("pdktestvendor@gmail.com");
//            _activationCodeView.setText("F9159374-C538-4E23-8B57-66A017E2E22D");

            //Prospect
//            _emailView.setText("krishprospect@gmail.com");
//            _activationCodeView.setText("EE0BB525-ECFD-4655-9204-801C1543ADAF");

            //Guest
//            _emailView.setText("taurusdev567@gmail.com");
//            _activationCodeView.setText("7BC85564-B772-4A77-8666-E9406C3D507F");

            //Live
//            _emailView.setText("krishlive@gmail.com");
//            _activationCodeView.setText("4a1f91af-f5f2-48ec-967b-a0f728e445af");

//            _emailView.setText("krishandroid@gmail.com");
//            Emulator Local
//            _activationCodeView.setText("e6f631d2-5836-4960-9b80-684d4c2737bc");

//            Emulator Live Android 11
//            _activationCodeView.setText("f8f9032d-ae5a-4cfb-8630-2d1f9b902da0");

//            Android 13
//            _activationCodeView.setText("752422eb-e24b-4f06-880b-c846bf2b0357");

//            Samsung Android 13
//            _activationCodeView.setText("23d4e6e7-883d-4383-8483-82a9be85a5fd");


//            _activationCodeView.setText("8B9BC456-3980-4B83-800E-4EBAC6025152");
//            _activationCodeView.setText("EBFC822A-FABB-47B9-AE38-191B68C8D93B");
            //            Samsung Live
//            _activationCodeView.setText("35a32bf2-f1ba-469a-8aba-e01f5abbf095");


//            _activationCodeView.setText("4642A285-E49F-4E1B-AFEB-C2DDECF77182");
//            4642A285-E49F-4E1B-AFEB-C2DDECF77182 - Pritesh's phone

//            _emailView.setText("angelyac213@gmail.com");
//            _activationCodeView.setText("7CED0493-3A42-4B5A-86AF-ACAEDCE5731A");

            //Guest _ Live
//            _emailView.setText("jaybhanderi866@gmail.com");
//            _activationCodeView.setText("jGc3tz$$o");

            //Leasing Officer/Agent
//            _emailView.setText("jaybhanderi86@gmail.com");
//            _activationCodeView.setText("4F829E67-3488-4138-A4E5-B94FEA959363");

            //Property Manager
//            _emailView.setText("pintu4291@mailinator.com");
//            _activationCodeView.setText("175B1EAB-4824-4017-931C-9DBA478AF542");

            //Vendor
//            _emailView.setText("testm@gmail.com");
//            _activationCodeView.setText("F963FB54-210D-4FB8-8FC1-E77721283065");

            //Vendor -Live
//            _emailView.setText("testvendor@gmail.com");
//            _activationCodeView.setText("3C39E274-F6F5-4D75-87F2-A8C15D7521E6");

            //Facility Manager
//            _emailView.setText("pritesh.kadival@tatvasoft.com");
//            _activationCodeView.setText("47CCD424-7B4F-4461-83A3-7B6A04533EC0");

            //facility manager live
//            _emailView.setText("ftlive@gmail.com");
//            _activationCodeView.setText("255644FB-4030-4AFD-A8C6-DD1167997B77");

//            _emailView.setText("rob.correa12@gmail.com");
//            _activationCodeView.setText("57736A52-3439-4DF6-A28F-46785E86EC9A");

            //9229EA99-2B47-467B-A5CD-2458614FA230
//            _emailView.setText("krishlive@gmail.com");
//            _activationCodeView.setText("9229EA99-2B47-467B-A5CD-2458614FA230");
        }
    }

    private void showErrorDialog(String string) {
        new AlertDialog.Builder((Context) this)
                .setCancelable(true)
                .setMessage(string)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void callAPIForCheckUserDeviceLogin() {
        ProgressDialog.showProgress(this);
        Login login = new Login();
        login.deviceId = Utilities.getDeviceID(this);
        login.email = _emailView.getText().toString();
        login.deviceName = Utilities.getDeviceName();
        WebService.getInstance().checkUserDeviceLogin(login, new RestCallBack<Integer>() {
            @Override
            public void onResponse(Integer response) {
                ProgressDialog.dismissProgress();
                llPasswordLogin.setVisibility(View.VISIBLE);
                imgArrow.setVisibility(View.GONE);
                if (response == NewDeviceRequestStatus.Activated.value()) {
                    llLoginOTP.setVisibility(View.VISIBLE);
                } else {
                    showAlertDialogForNewDevice();
                    llLoginOTP.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                String currentString = wse.getServerMessage();
                if (currentString.startsWith("301")) {
                    String[] separated = currentString.split("301");

                    new AlertDialog.Builder(LoginActivity.this)
                            .setCancelable(false)
                            .setMessage(separated[1].trim())
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //dismiss dialog
                                }
                            }).create().show();
                } else {
                    _emailView.setError(getString(R.string.error_invalid_email));
                    _emailView.requestFocus();
                }
            }
        });
    }

    private void showAlertDialogForNewDevice() {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("New Device")
                .setMessage("New Phones require a new access code for login from admin. \nCheck your registered email for verification of your new phone, \nThank you")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage("You have been logged into another device")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Utilities.showAboutDialog(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptLogin(boolean isWithOTP) {

        _emailView.setError(null);
        _activationCodeView.setError(null);

        String email = _emailView.getText().toString();
        String activationCode = _activationCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!isWithOTP) {
            if (TextUtils.isEmpty(email)) {
                _emailView.setError(getString(R.string.error_field_required));
                focusView = _emailView;
                cancel = true;
            } else if (!Utilities.isValidEmail(_emailView.getText())) {
                _emailView.setError(getString(R.string.error_invalid_email));
                focusView = _emailView;
                cancel = true;
            } else if (TextUtils.isEmpty(activationCode)) {
                _activationCodeView.setError(getString(R.string.error_field_required));
                focusView = _activationCodeView;
                cancel = true;

            }
        } else {
            if (TextUtils.isEmpty(email)) {
                _emailView.setError(getString(R.string.error_field_required));
                focusView = _emailView;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Login payload = new Login();
            payload.email = email;
            payload.activationCode = activationCode;
            TimeZone timezone = TimeZone.getDefault();
            payload.timeZoneOffset = timezone.getOffset(Calendar.ZONE_OFFSET) / 1000 / 60;
            payload.deviceId = Utilities.getDeviceID(LoginActivity.this);
            payload.isLoginWithOTP = isWithOTP;

            new AsyncTask<Login, Void, AuthenticationResult>() {

                private String _activationCode;

                @Override
                protected void onPreExecute() {

                    Utilities.hideKeyboard(LoginActivity.this);
//                    showProgress(true);
                    ProgressDialog.showProgress(LoginActivity.this);
                }

                @Override
                protected AuthenticationResult doInBackground(Login... args) {
                    _activationCode = args[0].activationCode;
                    try {
                        return MobileDataProvider.getInstance().authenticate(args[0]);
                    } catch (Exception ex) {
                        Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(AuthenticationResult result) {
                    if (payload.isLoginWithOTP) {
                        ProgressDialog.dismissProgress();
                        Intent intent = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                        intent.putExtra("ID", result.getId());
                        intent.putExtra("EMAIL", email);
                        startActivity(intent);
                    } else {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        if (result != null && !TextUtils.isEmpty(result.getAuthenticationCookie())) {

                            sharedPreferences.edit().putString("email", email).apply();
                            sharedPreferences.edit().putString("activationCode", _activationCode).apply();
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
                                Intent intent = new Intent(LoginActivity.this, TabbedActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finishAffinity();
                            }
                        } else {
//                            showProgress(false);
                            ProgressDialog.dismissProgress();
                            if (MobileDataProvider.loginErrorMessage.startsWith("301")) {

                                String currentString = MobileDataProvider.loginErrorMessage;
                                String[] separated = currentString.split("301");

                                new AlertDialog.Builder(LoginActivity.this)
                                        .setCancelable(false)
                                        .setMessage(separated[1].trim())
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                //dismiss dialog
                                            }
                                        }).create().show();

                            } else {
                                _emailView.setError(getString(R.string.error_invalid_login));
                                _emailView.requestFocus();
                            }
                        }
                    }
                }
            }.execute(payload);

        }
    }

    private void getUserData() {
//        showProgress(true);
        ProgressDialog.showProgress(LoginActivity.this);
        WebService.getInstance().getUserData(new RestCallBack<User>() {
            @Override
            public void onResponse(User user) {
//                showProgress(false);
                ProgressDialog.dismissProgress();
                if (user != null) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
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
                }
                Intent intent = new Intent(LoginActivity.this, TabbedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
            }
        });
    }
}

