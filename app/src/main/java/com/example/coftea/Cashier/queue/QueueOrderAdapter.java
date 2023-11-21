package com.example.coftea.Cashier.queue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Cashier.order.QueueEntry;
import com.example.coftea.R;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.utilities.PHPCurrencyFormatter;

import java.util.ArrayList;

public class QueueOrderAdapter extends RecyclerView.Adapter<QueueOrderAdapter.QueueOrderItemViewHolder> {
    private ArrayList<QueueEntry> queueEntryList;
    private QueueViewModel queueViewModel;

    public QueueOrderAdapter(QueueViewModel queueViewModel) {
        this.queueEntryList = new ArrayList<>();
        this.queueViewModel = queueViewModel;
    }

    public void UpdateList(ArrayList<QueueEntry> queueEntries) {
        this.queueEntryList = queueEntries;
        this.notifyDataSetChanged();
    }

    @NonNull
    public QueueOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_order_list_item, parent, false);
        return new QueueOrderItemViewHolder(view);
    }

    public void onBindViewHolder(@NonNull QueueOrderItemViewHolder holder, int position) {

        PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();
        QueueEntry queueEntry = queueEntryList.get(position);

        String price = phpCurrencyFormatter.formatAsPHP(queueEntry.getTotalPayment());
        holder.tvCustomerName.setText(queueEntry.getCustomerName());
        holder.tvCustomerPhone.setText(queueEntry.getCustomerPhone());
        holder.tvQueueOrderAmount.setText(price);

        OrderStatus status = queueEntry.getStatus();

        if(status == OrderStatus.DONE ||  status == OrderStatus.CANCELLED){
            holder.btnQueueOrderProcess.setVisibility(View.GONE);
            holder.btnQueueOrderCancel.setVisibility(View.GONE);
            return;
        }
        else{
            holder.btnQueueOrderProcess.setVisibility(View.VISIBLE);
            holder.btnQueueOrderCancel.setVisibility(View.VISIBLE);
        }

        holder.btnQueueOrderProcess.setOnClickListener(view -> queueViewModel.setQueueOrderToProcess(queueEntry));
        holder.btnQueueOrderCancel.setOnClickListener(view -> queueViewModel.setQueueOrderToCancel(queueEntry));

    }

    @Override
    public int getItemCount() {
        return queueEntryList.size();
    }

    static class QueueOrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerPhone, tvQueueOrderAmount;
        Button btnQueueOrderProcess, btnQueueOrderCancel;

        QueueOrderItemViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvQueueOrderCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvQueueOrderCustomerPhone);
            tvQueueOrderAmount = itemView.findViewById(R.id.tvQueueOrderAmount);
            btnQueueOrderProcess = itemView.findViewById(R.id.btnQueueOrderProcess);
            btnQueueOrderCancel = itemView.findViewById(R.id.btnQueueOrderCancel);
        }
    }
}
