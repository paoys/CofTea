package com.example.coftea.Cashier.queue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.R;
import com.example.coftea.data.OrderItem;
import com.example.coftea.data.Product;
import com.example.coftea.utilities.PHPCurrencyFormatter;

import java.util.ArrayList;

public class QueueOrderToDoneAdapter extends RecyclerView.Adapter<QueueOrderToDoneAdapter.QueueOrderItemViewHolder> {
    private ArrayList<CartItem> queueOrderItemList;
    PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();

    public QueueOrderToDoneAdapter(){
        this.queueOrderItemList = new ArrayList<>();
    }

    public void UpdateList(ArrayList<CartItem> queueOrderItemList) {
        this.queueOrderItemList = queueOrderItemList;
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public QueueOrderToDoneAdapter.QueueOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_fragment_queue_order_to_done_item, parent, false);
        return new QueueOrderToDoneAdapter.QueueOrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueOrderToDoneAdapter.QueueOrderItemViewHolder holder, int position) {

        CartItem queueOrderItem = queueOrderItemList.get(position);

        String price = phpCurrencyFormatter.formatAsPHP(queueOrderItem.getPrice());
        String totalPrice = phpCurrencyFormatter.formatAsPHP(queueOrderItem.getTotalPrice());

        holder.tvQueueItemName.setText(queueOrderItem.getName());
        holder.tvQueueItemPrice.setText(price);
        holder.tvQueueItemTotalPrice.setText(totalPrice);
        holder.tvQueueItemQuantity.setText(String.valueOf(queueOrderItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return queueOrderItemList.size();
    }

    static class QueueOrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvQueueItemName, tvQueueItemPrice, tvQueueItemQuantity, tvQueueItemTotalPrice;

        QueueOrderItemViewHolder(View itemView) {
            super(itemView);
            tvQueueItemName = itemView.findViewById(R.id.tvOrderQueueItemName);
            tvQueueItemPrice = itemView.findViewById(R.id.tvOrderQueueItemPrice);
            tvQueueItemQuantity = itemView.findViewById(R.id.tvOrderQueueItemQuantity);
            tvQueueItemTotalPrice = itemView.findViewById(R.id.tvOrderQueueItemTotalPrice);
        }
    }
}
