package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.Insurance;
import net.invictusmanagement.invictuslifestyle.models.InsuranceBasicInfo;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.TimeZone;

public class RentalInsuranceActivity extends AppCompatActivity implements IRefreshableFragment {

    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private InsuranceBasicInfo insuranceBasicInfo;
    private EditText edName, edEmail, edPhoneNumber, edAddress, edContentValue;
    private Button btnSubmit;
    private boolean isFormatted = true;
    private boolean isUnFormatted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_insurance);

        initControls();
    }

    private void initControls() {
        _context = RentalInsuranceActivity.this;
        toolBar();
        initView();
        refresh();
    }

    private void initView() {
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPhoneNumber = findViewById(R.id.edPhoneNumber);
        edAddress = findViewById(R.id.edAddress);
        edContentValue = findViewById(R.id.edContentValue);
        btnSubmit = findViewById(R.id.btnSubmit);

        edPhoneNumber.addTextChangedListener(new TextWatcher() {
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
                        edPhoneNumber.setText(s.toString().replaceAll("[^0-9]", ""));
                        edPhoneNumber.setSelection(edPhoneNumber.getText().length());
                    }

                }

                if (filteredString == 10) {
                    if (!isFormatted) {
                        isUnFormatted = false;
                        isFormatted = true;
                        edPhoneNumber.setText(Utilities.formatPhone(edPhoneNumber.getText().toString().trim()));
                        edPhoneNumber.setSelection(edPhoneNumber.getText().length());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.addHaptic(v);
                checkValidation();
            }
        });

        _swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void checkValidation() {
        boolean cancel = false;
        View focusView = null;

        edName.setError(null);
        edEmail.setError(null);
        edPhoneNumber.setError(null);
        edAddress.setError(null);
        edContentValue.setError(null);


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
        } else if (TextUtils.isEmpty(edPhoneNumber.getText().toString())) {
            edPhoneNumber.setError(getString(R.string.error_field_required));
            focusView = edPhoneNumber;
            cancel = true;
        } else if (edPhoneNumber.getText().toString().trim().length() != 14) {
            edPhoneNumber.setError(getString(R.string.error_invalid_mobile));
            focusView = edPhoneNumber;
            cancel = true;
        } else if (TextUtils.isEmpty(edAddress.getText().toString())) {
            edAddress.setError(getString(R.string.error_field_required));
            focusView = edAddress;
            cancel = true;
        } else if (TextUtils.isEmpty(edContentValue.getText().toString())) {
            edContentValue.setError(getString(R.string.error_field_required));
            focusView = edContentValue;
            cancel = true;
        }

        if (!cancel) {
            TimeZone timezone = TimeZone.getDefault();
            Insurance insurance = new Insurance();
            insurance.name = edName.getText().toString().trim();
            insurance.email = edEmail.getText().toString().trim();
            insurance.phoneNumber = edPhoneNumber.getText().toString().trim();
            insurance.address = edAddress.getText().toString().trim();
            insurance.contentValue = edContentValue.getText().toString().trim();
            submitInsurance(insurance);

        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
    }


    private void submitInsurance(Insurance insurance) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {

                Utilities.hideKeyboard(RentalInsuranceActivity.this);
                ProgressDialog.showProgress(_context);
            }

            @Override
            protected Boolean doInBackground(Void... args) {

                try {
                    MobileDataProvider.getInstance().submitInsurance(insurance);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    /*refresh();*/
                    ProgressDialog.dismissProgress();
                    Toast.makeText(_context, "Rental Insurance Submitted Successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    ProgressDialog.dismissProgress();
                    Toast.makeText(_context, "Rental Insurance Failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }

    public void refresh() {
        if (_swipeRefreshLayout == null)
            return;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                _swipeRefreshLayout.setRefreshing(true);
                ProgressDialog.showProgress(_context);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    // Refresh logic goes here.
                    insuranceBasicInfo = MobileDataProvider.getInstance().getInsuranceBasicInfo();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean success) {
                _swipeRefreshLayout.setRefreshing(false);
                ProgressDialog.dismissProgress();
                if (success) {
                    edName.setText(insuranceBasicInfo.name);
                    edEmail.setText(insuranceBasicInfo.email);
                    /*String number  = insuranceBasicInfo.phoneNumber.replaceAll("[^0-9]", "");*/
                    edPhoneNumber.setText(Utilities.formatPhone(insuranceBasicInfo.phoneNumber));
                    edAddress.setText(insuranceBasicInfo.address);
                    edContentValue.setText("");
                } else {
                    // Toast.makeText(getActivity(), "Unable to refresh content. Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();
    }
}