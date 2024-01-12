package net.invictusmanagement.invictuslifestyle.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.models.AuthenticationGuestResult;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.UUID;

public class GuestLoginActivity extends BaseActivity {

    private EditText _emailView;
    private EditText _activationCodeView;
    private View _progressView;
    private Button _activateButton;
    private TextView _tvGuestUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guest_login);

        setToolBar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        _tvGuestUser = findViewById(R.id.tvGuestUser);
        _tvGuestUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestLoginActivity.this, GuestSignupActivity.class);
                /*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
                startActivity(intent);
                /*finish();*/
            }
        });

        _emailView = findViewById(R.id.email);
        _activationCodeView = findViewById(R.id.activationCode);
        _progressView = findViewById(R.id.activation_progress);

        _activateButton = findViewById(R.id.activate_button);
        _activateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        _activateButton.setEnabled(Utilities.checkPlayServices(this));

        /*if (Debug.isDebuggerConnected()) {
            _emailView.setText("hblakeslee@gmail.com");
            _activationCodeView.setText("31d3f1a6-95f0-4d9c-b120-0933ee6c1fb2");
        }*/
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Utilities.hideKeyboard(GuestLoginActivity.this);
            }
        });
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_about:
                Utilities.showAboutDialog(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }*/

    private void attemptLogin() {

        _emailView.setError(null);
        _activationCodeView.setError(null);

        String email = _emailView.getText().toString().trim();
        String activationCode = _activationCodeView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            _emailView.setError(getString(R.string.error_field_required));
            focusView = _emailView;
            cancel = true;
        } else if (!Utilities.isValidEmail(email)) {
            _emailView.setError(getString(R.string.error_invalid_email));
            focusView = _emailView;
            cancel = true;
        } else if (TextUtils.isEmpty(activationCode)) {
            _activationCodeView.setError(getString(R.string.error_field_required));
            focusView = _activationCodeView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            new AsyncTask<String, Void, AuthenticationGuestResult>() {

                private String _activationCode;

                @Override
                protected void onPreExecute() {

                    Utilities.hideKeyboard(GuestLoginActivity.this);
                    showProgress(true);
                }

                @Override
                protected AuthenticationGuestResult doInBackground(String... args) {
                    _activationCode = args[1];
                    try {
                        return MobileDataProvider.getInstance().authenticateGuest(args[0], _activationCode);
                    } catch (Exception ex) {
                        Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(AuthenticationGuestResult result) {
                    if (result != null && !TextUtils.isEmpty(result.authenticationCookie)) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GuestLoginActivity.this);
                        sharedPreferences.edit().putString("activationCode", result.activationCode).apply();
                        sharedPreferences.edit().putLong("userId", result.userId).apply();
                        sharedPreferences.edit().putBoolean("isGuestUser", true).apply();
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                        sharedPreferences.edit().putString("authenticationCookie", result.authenticationCookie).apply();
                        MobileDataProvider.getInstance().setAuthenticationCookie(result.authenticationCookie);
                        Intent intent = new Intent(GuestLoginActivity.this, TabbedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        showProgress(false);
                        if (MobileDataProvider.guestLoginErrorMessage.startsWith("301")) {

                            String currentString = MobileDataProvider.guestLoginErrorMessage;
                            String[] separated = currentString.split("301");

                            new AlertDialog.Builder(GuestLoginActivity.this)
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
            }.execute(email, activationCode);
        }
    }

    private boolean isActivationCodeValid(String activationCode) {
        try {
            UUID id = UUID.fromString(activationCode);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }

    private void showProgress(final boolean show) {
        Utilities.showHide(this, _activateButton, !show);
        Utilities.showHide(this, _tvGuestUser, !show);
        Utilities.showHide(this, _progressView, show);
    }
}

