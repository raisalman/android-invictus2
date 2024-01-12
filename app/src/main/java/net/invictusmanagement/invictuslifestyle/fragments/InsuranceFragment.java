package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.Insurance;
import net.invictusmanagement.invictuslifestyle.models.InsuranceBasicInfo;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.TimeZone;

public class InsuranceFragment extends Fragment implements IRefreshableFragment {
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private InsuranceBasicInfo insuranceBasicInfo;
    private EditText edName, edEmail, edPhoneNumber, edAddress, edContentValue;
    private Button btnSubmit;

    public InsuranceFragment() {
    }

    @SuppressWarnings("unused")
    public static InsuranceFragment newInstance() {

        return new InsuranceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_insurance, container, false);
        if (view instanceof SwipeRefreshLayout) {

            edName = view.findViewById(R.id.edName);
            edEmail = view.findViewById(R.id.edEmail);
            edPhoneNumber = view.findViewById(R.id.edPhoneNumber);
            edAddress = view.findViewById(R.id.edAddress);
            edContentValue = view.findViewById(R.id.edContentValue);
            btnSubmit = view.findViewById(R.id.btnSubmit);

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkValidation();
                }
            });

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
            refresh();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    public void refresh() {
        if (_swipeRefreshLayout == null)
            return;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                _swipeRefreshLayout.setRefreshing(true);
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
                    edPhoneNumber.setText(insuranceBasicInfo.phoneNumber);
                    edAddress.setText(insuranceBasicInfo.address);
                    edContentValue.setText("");
                } else {
                    // Toast.makeText(getActivity(), "Unable to refresh content. Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();
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
        } else if (edPhoneNumber.getText().toString().trim().length() != 10) {
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

                Utilities.hideKeyboard(getActivity());
                ProgressDialog.showProgress(getActivity());
            }

            @Override
            protected Boolean doInBackground(Void... args) {

                try {
                    MobileDataProvider.getInstance().submitInsurance(insurance);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    refresh();
                    Toast.makeText(TabbedActivity.tabbedActivity, "Rental Insurance Submitted Successfully", Toast.LENGTH_LONG).show();
                } else {
                    ProgressDialog.dismissProgress();
                    Toast.makeText(TabbedActivity.tabbedActivity, "Rental Insurance Failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }
}
