package com.example.coftea.Cashier.settings.voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VouchersAdapter extends RecyclerView.Adapter<VouchersAdapter.VoucherViewHolder> {

    private List<Voucher> voucherList;

    public VouchersAdapter(List<Voucher> voucherList) {
        this.voucherList = voucherList;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.bindVoucher(voucher);
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        private TextView codeTextView;
        private TextView discountTextView;
        private TextView expirationTextView;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            codeTextView = itemView.findViewById(R.id.codeTextView);
            discountTextView = itemView.findViewById(R.id.discountTextView);
            expirationTextView = itemView.findViewById(R.id.expirationTextView);
        }

        public void bindVoucher(Voucher voucher) {
            codeTextView.setText("Code: " + voucher.getCode());
            discountTextView.setText("Discount: " + voucher.getDiscount() + "%");

            // Format the expiration timestamp into a readable date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String expirationDate = dateFormat.format(new Date(voucher.getExpirationTimestamp()));

            expirationTextView.setText("Expiration: " + expirationDate);
        }
    }
}
