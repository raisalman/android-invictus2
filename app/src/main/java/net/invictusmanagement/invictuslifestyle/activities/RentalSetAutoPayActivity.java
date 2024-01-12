package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.models.AutoPaymentRequest;
import net.invictusmanagement.invictuslifestyle.models.AutoPaymentResponse;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.Date;

public class RentalSetAutoPayActivity extends AppCompatActivity {

    private Context _context;
    private Spinner spnDay;
    private EditText etAmount;
    private CheckBox chkIsFullPay;
    private Button btnSetUpAutoPay;
    private AutoPaymentResponse paymentResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_auto_payment);

        initControls();
    }

    private void initControls() {
        _context = RentalSetAutoPayActivity.this;
        toolBar();
        initView();
    }

    private void initView() {
        etAmount = findViewById(R.id.edAmount);
        chkIsFullPay = findViewById(R.id.chkIsFullPay);
        btnSetUpAutoPay = findViewById(R.id.btnSetupAutoPay);
        spnDay = findViewById(R.id.spnDay);

        if (getIntent().getExtras() != null) {
            etAmount.setText(String.valueOf(getIntent()
                    .getExtras().getFloat("EXTRA_CURRENT_AMOUNT")));
        }

        final String[] str = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, str);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        spnDay.setAdapter(spinnerArrayAdapter);

//        ArrayAdapter<String> yourSpinnerAdapter = new ArrayAdapter<String>(this,
//                R.layout.spinner_item, str) {
//
//            @Override
//            public View getDropDownView(int position, View convertView,
//                                        ViewGroup parent) {
//                convertView = super.getView(position, convertView,
//                        parent);
//
//                convertView.setVisibility(View.VISIBLE);
//                ViewGroup.LayoutParams p = convertView.getLayoutParams();
//                p.height = 100; // set the height
//                convertView.setLayoutParams(p);
//
//                convertView.setMinimumHeight(30);
//
//                return convertView;
//            }
//        };

//        spnDay.setAdapter(yourSpinnerAdapter);

        btnSetUpAutoPay.setOnClickListener(v -> {
            Utilities.addHaptic(v);
            if (etAmount.getText().toString().length() == 0) {
                Toast.makeText(RentalSetAutoPayActivity.this,
                        "Please enter amount", Toast.LENGTH_SHORT).show();
            } else {
                callSetAutoPayAPI();
            }
        });

        chkIsFullPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    etAmount.setEnabled(false);
                    etAmount.setText(String.valueOf(getIntent()
                            .getExtras().getFloat("EXTRA_CURRENT_AMOUNT")));
                } else {
                    etAmount.setEnabled(true);
                }
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

    private void callSetAutoPayAPI() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                Utilities.hideKeyboard(RentalSetAutoPayActivity.this);
                ProgressDialog.showProgress(_context);
            }

            @Override
            protected String doInBackground(Void... args) {
                try {
                    AutoPaymentRequest model = new AutoPaymentRequest();
                    model.amount = Float.parseFloat(etAmount.getText().toString());
                    model.isWholeRentAmount = chkIsFullPay.isChecked();
                    model.autoPayDay = spnDay.getSelectedItem().toString();
                    return MobileDataProvider.getInstance().setAutoPayment(model);
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {

                    // https://portaldev.invictusmanagement.net/payment/cardinfo?paymentRequestId=6
                    //{"applicationUserId":917,"amount":20.0,"isWholeRentAmount":false,
                    // "autoPayDay":1,"token":"","isActive":false,"remarks":"Auto Pay Created By User",
                    // "redirectUrl":"https://portaldev.invictusmanagement.net/payment/getAutopayToken?tokenRequestId=8",
                    // "id":8,"createdUtc":"2021-10-25T12:49:03.7866667","deleted":false}

                    paymentResponse = new GsonBuilder()
                            .registerTypeAdapter(Date.class,
                                    new MobileDataProvider.DateDeserializer())
                            .create().fromJson(result, new TypeToken<AutoPaymentResponse>() {
                            }.getType());
                    ProgressDialog.dismissProgress();
//                    Toast.makeText(_context, "Rental Insurance Submitted Successfully", Toast.LENGTH_LONG).show();
                    openWebViewWithURL(paymentResponse.redirectUrl, "autopay");
                } else {
                    ProgressDialog.dismissProgress();
                    Toast.makeText(_context, "Setting Auto Payment Failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void openWebViewWithURL(String result, String paynow) {
        Intent intent = new Intent(RentalSetAutoPayActivity.this, WebViewActivity.class);
        intent.putExtra("EXTRA_URL", result);
        intent.putExtra("EXTRA_FROM", paynow);
        startActivity(intent);
    }


}