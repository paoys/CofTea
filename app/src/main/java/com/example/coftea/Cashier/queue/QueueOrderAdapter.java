package com.example.coftea.Cashier.queue;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.example.coftea.utilities.PHPCurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class QueueOrderAdapter extends RecyclerView.Adapter<QueueOrderAdapter.QueueOrderViewHolder> {
    private ArrayList<QueueOrder> queueOrderList;
    private QueueViewModel queueViewModel;

    public QueueOrderAdapter(QueueViewModel queueViewModel) {
        this.queueOrderList = new ArrayList<>();
        this.queueViewModel = queueViewModel;
    }

    public void UpdateList(ArrayList<QueueOrder> queueOrders) {
        this.queueOrderList = queueOrders;
        this.notifyDataSetChanged();
    }

    @NonNull
    public QueueOrderAdapter.QueueOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_order_list_item, parent, false);
        return new QueueOrderAdapter.QueueOrderViewHolder(view);
    }

    public void onBindViewHolder(@NonNull QueueOrderAdapter.QueueOrderViewHolder holder, int position) {

        PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();
        QueueOrder queueOrder = queueOrderList.get(position);

        String price = phpCurrencyFormatter.formatAsPHP(queueOrder.getTotalPayment());
        holder.tvCustomerName.setText(queueOrder.getCustomerName());
        holder.tvCustomerPhone.setText(queueOrder.getCustomerPhone());
        holder.tvQueueOrderAmount.setText(price);

        holder.btnQueueOrderDone.setOnClickListener(view -> queueViewModel.setQueueOrderToDone(queueOrder));
        holder.btnQueueOrderCancel.setOnClickListener(view -> queueViewModel.setQueueOrderToCancel(queueOrder));

    }

    @Override
    public int getItemCount() {
        return queueOrderList.size();
    }

    static class QueueOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName;
        TextView tvCustomerPhone;
        TextView tvQueueOrderAmount;
        Button btnQueueOrderDone;
        Button btnQueueOrderCancel;

        QueueOrderViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvQueueOrderCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvQueueOrderCustomerPhone);
            tvQueueOrderAmount = itemView.findViewById(R.id.tvQueueOrderAmount);
            btnQueueOrderDone = itemView.findViewById(R.id.btnQueueOrderDone);
            btnQueueOrderCancel = itemView.findViewById(R.id.btnQueueOrderCancel);
        }
    }
}
