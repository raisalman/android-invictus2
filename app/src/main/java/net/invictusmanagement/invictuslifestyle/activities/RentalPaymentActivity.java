package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.PaymentRequest;
import net.invictusmanagement.invictuslifestyle.models.RentalPayment;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class RentalPaymentActivity extends AppCompatActivity implements IRefreshableFragment {

    //    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private RentalPayment rentalPayment;
    private TextView txtRentAmount, txtPaidAmount, txtRemark;
    private Button btnPayNow, btnSetUpAutoPay;
    private BottomSheetDialog bottomSheetDialog;
    private Button btnPaymentHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_payment);

        initControls();
    }

    private void initControls() {
        _context = RentalPaymentActivity.this;
        toolBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initView() {
        txtPaidAmount = findViewById(R.id.txtPaidAmount);
        txtRentAmount = findViewById(R.id.txtRentAmount);
        txtRemark = findViewById(R.id.txtRemark);
        btnPayNow = findViewById(R.id.btnPayNow);
        btnSetUpAutoPay = findViewById(R.id.btnSetupAutoPay);
        btnPaymentHistory = findViewById(R.id.btnPaymentHistory);

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.addHaptic(v);
                showDialogToPay();
            }
        });

        btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_context, PaymentHistoryActivity.class));
            }
        });

        btnSetUpAutoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.addHaptic(view);
                if (rentalPayment != null) {
                    if (rentalPayment.rentAutoPay == null) {
                        startAutoSetupScreen();
                    } else if (rentalPayment.rentAutoPay.isActive) {
                        showDialogToCancelAutoPay();
                    } else {
                        startAutoSetupScreen();
                    }
                }
            }
        });
    }

    private void startAutoSetupScreen() {
        Intent intent = new Intent(RentalPaymentActivity.this,
                RentalSetAutoPayActivity.class);
        intent.putExtra("EXTRA_TOTAL_AMOUNT", rentalPayment.totalPaid);
        intent.putExtra("EXTRA_CURRENT_AMOUNT", rentalPayment.currentMonthRent);

        startActivity(intent);
    }

    private void showDialogToCancelAutoPay() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RentalPaymentActivity.this);
        alertDialog.setTitle("Cancel AutoPay");
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Are you sure you want to cancel AutoPay?");
        alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Continue with delete operation
                callCancelAutoPayAPI();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Continue with delete operation
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showDialogToPay() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_pay_now);

        AppCompatButton btnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);
        AppCompatButton btnPayNow = bottomSheetDialog.findViewById(R.id.btnPay);

        EditText edtAmount = bottomSheetDialog.findViewById(R.id.edAmount);

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.addHaptic(view);
                if (edtAmount.getText().toString().length() == 0) {
                    Toast.makeText(RentalPaymentActivity.this,
                            "Please enter amount", Toast.LENGTH_SHORT).show();
                } else {
                    callPaymentRequestAPI(edtAmount.getText().toString());
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void callPaymentRequestAPI(String amount) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                Utilities.hideKeyboard(RentalPaymentActivity.this);
                ProgressDialog.showProgress(_context);
            }

            @Override
            protected String doInBackground(Void... args) {

                try {
                    PaymentRequest model = new PaymentRequest();
                    model.amount = Float.parseFloat(amount);
                    model.payfor = "Rent";
                    return MobileDataProvider.getInstance().postPaymentRequest(model);
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                ProgressDialog.dismissProgress();
                if (result == null) {
                    Toast.makeText(_context, "Payment Request Failed", Toast.LENGTH_LONG).show();
                } else {
                    openWebViewWithURL(result, "paynow");
                }
            }
        }.execute();

    }

    private void openWebViewWithURL(String result, String paynow) {
        bottomSheetDialog.dismiss();
        Intent intent = new Intent(RentalPaymentActivity.this, WebViewActivity.class);
        intent.putExtra("EXTRA_URL", result);
        intent.putExtra("EXTRA_FROM", paynow);
        startActivity(intent);
    }

    public void refresh() {
//        if (_swipeRefreshLayout == null)
//            return;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
//                _swipeRefreshLayout.setRefreshing(true);
                ProgressDialog.showProgress(_context);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    // Refresh logic goes here.
                    rentalPayment = MobileDataProvider.getInstance().getPaymentRequest();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean success) {
//                _swipeRefreshLayout.setRefreshing(false);
                ProgressDialog.dismissProgress();
                if (success) {
                    setPaymentData();
                } else {
                    Toast.makeText(_context, "Unable to load content. Please try again later.",
                            Toast.LENGTH_LONG).show();
                }

            }
        }.execute();
    }

    private void setPaymentData() {
        txtPaidAmount.setText(String.format("$%s", rentalPayment.totalPaid));
        txtRentAmount.setText(String.format("$%s", rentalPayment.currentMonthRent));
        txtRemark.setVisibility(View.GONE);
        if (rentalPayment.rentAutoPay == null) {
            btnSetUpAutoPay.setText(getString(R.string.setup_autopay));
        } else {
            if (rentalPayment.rentAutoPay.isActive) {
                btnSetUpAutoPay.setText(getString(R.string.cancel_autopay));
                txtRemark.setVisibility(View.VISIBLE);
                Calendar calendar = Calendar.getInstance();
                if (calendar.get(Calendar.DAY_OF_MONTH) > Integer.parseInt(rentalPayment.rentAutoPay.autoPayDay)) {
                    calendar.add(Calendar.MONTH, 1);
                }
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(rentalPayment.rentAutoPay.autoPayDay));
                Date dt = calendar.getTime();
                String formattedDate = DateFormat.getDateInstance().format(dt);
                txtRemark.setText("You have scheduled a payment of $" + rentalPayment.rentAutoPay.amount
                        + "\nthat will occurs on " + formattedDate);
            } else if (!rentalPayment.rentAutoPay.deleted) {
                txtRemark.setVisibility(View.VISIBLE);
                btnSetUpAutoPay.setText(getString(R.string.setup_autopay));
                txtRemark.setText(String.format("Remarks:\n%s", rentalPayment.rentAutoPay.remarks));
            } else {
                btnSetUpAutoPay.setText(getString(R.string.setup_autopay));
            }
        }
    }

    private void callCancelAutoPayAPI() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                Utilities.hideKeyboard(RentalPaymentActivity.this);
                ProgressDialog.showProgress(_context);
            }

            @Override
            protected Boolean doInBackground(Void... args) {

                try {
                    MobileDataProvider.getInstance().cancelAutoPayment();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                ProgressDialog.dismissProgress();
                if (!result) {
                    Toast.makeText(_context, "Cancel Payment Request Failed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(_context, "Cancel Payment Successfully", Toast.LENGTH_LONG).show();
                    refresh();
                }
            }
        }.execute();
    }
}