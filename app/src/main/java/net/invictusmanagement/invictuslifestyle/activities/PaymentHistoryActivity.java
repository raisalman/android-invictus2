package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.PaymentHistoryAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.PaymentTransactionResponse;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.List;

public class PaymentHistoryActivity extends AppCompatActivity implements IRefreshableFragment {

    private Context _context;
    private RecyclerView rvPaymentHistory;
    private TextView tvNoDataFound;
    private PaymentHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        initControls();
    }

    private void initControls() {
        _context = PaymentHistoryActivity.this;
        toolBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initView() {
        rvPaymentHistory = findViewById(R.id.rvPaymentHistory);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
    }


    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Payment History");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void refresh() {
        ProgressDialog.showProgress(_context);
        WebService.getInstance().getPaymentHistory(
                new RestCallBack<List<PaymentTransactionResponse>>() {
                    @Override
                    public void onResponse(List<PaymentTransactionResponse> response) {
                        ProgressDialog.dismissProgress();
                        setPaymentData(response);
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        ProgressDialog.dismissProgress();
                    }
                });
    }

    private void setPaymentData(List<PaymentTransactionResponse> paymentData) {
        adapter = new PaymentHistoryAdapter(_context, paymentData);

        rvPaymentHistory.setHasFixedSize(true);
        rvPaymentHistory.setLayoutManager(new LinearLayoutManager(this));
        rvPaymentHistory.addItemDecoration(new DividerItemDecoration(rvPaymentHistory.getContext(),
                DividerItemDecoration.VERTICAL));
        rvPaymentHistory.setAdapter(adapter);

        if (paymentData.size() > 0) {
            tvNoDataFound.setVisibility(View.GONE);
            rvPaymentHistory.setVisibility(View.VISIBLE);
        } else {
            tvNoDataFound.setVisibility(View.VISIBLE);
            rvPaymentHistory.setVisibility(View.GONE);
        }
    }
}