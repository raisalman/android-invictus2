package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.models.PaymentTransactionResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {

    private Context _context;
    private List<PaymentTransactionResponse> _dataSource;

    public PaymentHistoryAdapter(Context context,
                                 List<PaymentTransactionResponse> transactionResponseList) {
        _context = context;
        _dataSource = transactionResponseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);
        holder.tvTransactionAmount.setText("$" + holder.item.getTransactionAmount());
        holder.tvTransactionId.setText("#" + holder.item.getTransactionId());
        holder.tvPaymentType.setText("via " + holder.item.getPaymentType());
        if (holder.item.isSuccess()) {
            holder.tvStatus.setTextColor(_context.getResources().getColor(R.color.color_success));
            holder.tvStatus.setText("Success");
        } else {
            holder.tvStatus.setTextColor(_context.getResources().getColor(R.color.color_red));
            holder.tvStatus.setText("Failed");
        }
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        holder.tvPaymentDate.setText(formatter.format(holder.item.getTransactionDate()));
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvTransactionId;
        private TextView tvStatus;
        private TextView tvPaymentType;
        private TextView tvTransactionAmount;
        private TextView tvPaymentDate;
        private PaymentTransactionResponse item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvTransactionId = view.findViewById(R.id.tvTransactionId);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvPaymentType = view.findViewById(R.id.tvPaymentType);
            tvTransactionAmount = view.findViewById(R.id.tvTransactionAmount);
            tvPaymentDate = view.findViewById(R.id.tvPaymentDate);
        }
    }
}
