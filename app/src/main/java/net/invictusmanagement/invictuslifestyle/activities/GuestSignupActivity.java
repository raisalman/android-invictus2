package net.invictusmanagement.invictuslifestyle.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.models.AuthenticationGuestResult;
import net.invictusmanagement.invictuslifestyle.models.CreateGuestUser;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuestSignupActivity extends AppCompatActivity {

    private EditText edName, edEmail, edPhoneNo, edPassword, edConfirmPassword;
    private Button btnSingUp;
    private String wholeString = null;
    private boolean isFormatted = true;
    private boolean isUnFormatted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_signup);

        setToolbar();
        initViews();
        onClickListeners();
    }

    private void onClickListeners() {
        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

    }

    private void checkValidation() {
        boolean cancel = false;
        View focusView = null;

        edName.setError(null);
        edEmail.setError(null);
        edPhoneNo.setError(null);
        edPassword.setError(null);
        edConfirmPassword.setError(null);


        if (TextUtils.isEmpty(edName.getText().toString())) {
            edName.setError(getString(R.string.error_field_required));
            focusView = edName;
            cancel = true;
        } else if (TextUtils.isEmpty(edEmail.getText().toString())) {
            edEmail.setError(getString(R.string.error_field_required));
            focusView = edEmail;
            cancel = true;
        } else if (!Utilities.isValidEmail(edEmail.getText().toString())) {
            edEmail.setError(getString(R.string.error_invalid_email));
            focusView = edEmail;
            cancel = true;
        } else if (TextUtils.isEmpty(edPhoneNo.getText().toString())) {
            edPhoneNo.setError(getString(R.string.error_field_required));
            focusView = edPhoneNo;
            cancel = true;
        } else if (edPhoneNo.getText().toString().trim().length() != 14) {
            edPhoneNo.setError(getString(R.string.error_invalid_mobile));
            focusView = edPhoneNo;
            cancel = true;
        } else if (TextUtils.isEmpty(edPassword.getText().toString())) {
            edPassword.setError(getString(R.string.error_field_required));
            focusView = edPassword;
            cancel = true;
        } else if (TextUtils.isEmpty(edConfirmPassword.getText().toString())) {
            edConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = edConfirmPassword;
            cancel = true;
        } else if (!edPassword.getText().toString().trim().equals(edConfirmPassword.getText().toString().trim())) {
            edPassword.setError(getString(R.string.error_password_field));
            edConfirmPassword.setError(getString(R.string.error_password_field));
            focusView = edPassword;
            cancel = true;
        } else if (edPassword.getText().toString().trim().length() < 6) {
            edPassword.setError("Passwords must be at least 6 characters.");
            edConfirmPassword.setError("Passwords must be at least 6 characters.");
            focusView = edPassword;
            cancel = true;
        } else if (!isValidPassword(edPassword.getText().toString())) {
            AlertDialog("Password must have at least 1 uppercase, lowercase, special character and number.");
            cancel = true;
        }

        if (!cancel) {
            TimeZone timezone = TimeZone.getDefault();
            CreateGuestUser createGuestUser = new CreateGuestUser();
            createGuestUser.name = edName.getText().toString().trim();
            createGuestUser.email = edEmail.getText().toString().trim();
            createGuestUser.password = edPassword.getText().toString().trim();
            createGuestUser.phoneNumber = edPhoneNo.getText().toString().trim();
            createGuestUser.timeZoneOffset = timezone.getOffset(Calendar.ZONE_OFFSET) / 1000 / 60;
            createGuestUser(createGuestUser);

        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
    }

    private void createGuestUser(CreateGuestUser createGuestUser) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {

                Utilities.hideKeyboard(GuestSignupActivity.this);
                ProgressDialog.showProgress(GuestSignupActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {

                try {
                    MobileDataProvider.getInstance().createGuestUser(createGuestUser, GuestSignupActivity.this);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result != null) {
                    if (result) {
                        loginAndRedirect();
                    }
                } else if (wholeString != null) {
                    ProgressDialog.dismissProgress();
                    if (wholeString.contains("301")) {
                        AlertDialog(wholeString.split("301")[1]);
                    } else if (wholeString.contains("302")) {
                        AlertDialog(wholeString.split("302")[1]);
                    } else {
                        AlertDialog("Failed to create guest account, please try again.");
                    }
                } else {
                    ProgressDialog.dismissProgress();
                    AlertDialog("Failed to create guest account, please try again.");
                }
            }
        }.execute();
    }

    private void AlertDialogWithFinish(String _activationCode, AuthenticationGuestResult result) {
        new AlertDialog.Builder(GuestSignupActivity.this)
                .setCancelable(false)
                .setMessage("Guest user created successfully")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GuestSignupActivity.this);
                        sharedPreferences.edit().putString("activationCode", result.activationCode).apply();
                        sharedPreferences.edit().putLong("userId", result.userId).apply();
                        sharedPreferences.edit().putBoolean("isGuestUser", true).apply();
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                        sharedPreferences.edit().putString("authenticationCookie", result.authenticationCookie).apply();
                        MobileDataProvider.getInstance().setAuthenticationCookie(result.authenticationCookie);
                        Intent intent = new Intent(GuestSignupActivity.this, TabbedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finishAffinity();
                    }
                }).create().show();
    }

    private void AlertDialog(String s) {
        new AlertDialog.Builder(GuestSignupActivity.this)
                .setCancelable(false)
                .setMessage(s)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).create().show();
    }

    public void showDialogActivity(String string) {
        wholeString = string;
    }

    private void initViews() {

        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPhoneNo = findViewById(R.id.edPhoneNo);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        btnSingUp = findViewById(R.id.signup_button);

        edPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int filteredString = s.toString().replaceAll("[^0-9]", "").length();
                if (filteredString < 10) {
                    if (!isUnFormatted) {
                        isFormatted = false;
                        isUnFormatted = true;
                        edPhoneNo.setText(s.toString().replaceAll("[^0-9]", ""));
                        edPhoneNo.setSelection(edPhoneNo.getText().length());
                    }

                }

                if (filteredString == 10) {
                    if (!isFormatted) {
                        isUnFormatted = false;
                        isFormatted = true;
                        edPhoneNo.setText(Utilities.formatPhone(edPhoneNo.getText().toString().trim()));
                        edPhoneNo.setSelection(edPhoneNo.getText().length());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Utilities.hideKeyboard(GuestSignupActivity.this);
            }
        });
    }

    public boolean isValidPassword(final String password) {
                /*302 Error in Registration.
                Passwords must be at least 6 characters.,
                Passwords must have at least one non alphanumeric character.,
                Passwords must have at least one digit ('0'-'9').,
                Passwords must have at least one uppercase ('A'-'Z').*/
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private void loginAndRedirect() {

        ProgressDialog.showProgress(this);
        String email = edEmail.getText().toString().trim();
        String activationCode = edPassword.getText().toString().trim();

        new AsyncTask<String, Void, AuthenticationGuestResult>() {

            private String _activationCode;

            @Override
            protected void onPreExecute() {

              /*  Utilities.hideKeyboard(GuestLoginActivity.this);
                showProgress(true);*/
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
                ProgressDialog.dismissProgress();
                if (result != null && !TextUtils.isEmpty(result.authenticationCookie)) {
                    ProgressDialog.dismissProgress();
                    AlertDialogWithFinish(_activationCode, result);
                } else {
                    ProgressDialog.dismissProgress();
                    Toast.makeText(GuestSignupActivity.this, "Please try to login.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(email, activationCode);
    }
}