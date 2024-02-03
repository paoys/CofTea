package com.example.coftea.Cashier.queue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.Cashier.order.QueueEntry;
import com.example.coftea.R;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QueueOrderAdapter extends RecyclerView.Adapter<QueueOrderAdapter.QueueOrderItemViewHolder> {
    private ArrayList<QueueEntry> queueEntryList;
    private QueueViewModel queueViewModel;
    PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();

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


        QueueEntry queueEntry = queueEntryList.get(position);
        holder.tvQueueTitle.setText(R.string.queue_number);
        holder.tvQueueNumber.setText(String.valueOf(queueEntry.getQueueNumber()));
        String price = phpCurrencyFormatter.formatAsPHP(queueEntry.getTotalPayment());
        holder.tvCustomerName.setText(queueEntry.getCustomerName());
        holder.tvCustomerPhone.setText(queueEntry.getCustomerPhone());
        holder.tvQueueOrderAmount.setText(price);
        holder.llCartItemContainer.removeAllViews();
        loadCartItemsFromFirebase(holder.llCartItemContainer, queueEntry.getReceiptID());

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
        TextView tvCustomerName, tvCustomerPhone, tvQueueOrderAmount, tvQueueNumber, tvQueueTitle;
        Button btnQueueOrderProcess, btnQueueOrderCancel;
        LinearLayout llCartItemContainer;
        private Context context;

        QueueOrderItemViewHolder(View itemView) {
            super(itemView);
            tvQueueTitle = itemView.findViewById(R.id.tvQueueNumber);
            tvQueueNumber = itemView.findViewById(R.id.tvQueueNumberValue);
            tvCustomerName = itemView.findViewById(R.id.tvQueueOrderCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvQueueOrderCustomerPhone);
            tvQueueOrderAmount = itemView.findViewById(R.id.tvQueueOrderAmount);
            btnQueueOrderProcess = itemView.findViewById(R.id.btnQueueOrderProcess);
            btnQueueOrderCancel = itemView.findViewById(R.id.btnQueueOrderCancel);
            llCartItemContainer = itemView.findViewById(R.id.llCartItemContainer);
            context = itemView.getContext();
        }
    }

    private void loadCartItemsFromFirebase(LinearLayout llCartItemContainer, String receiptID) {

        DatabaseReference receiptRef = queueViewModel.receiptsRealtimeDB.getDatabaseReference().child(receiptID).child("cartItems").getRef();
        receiptRef.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                CartItem cartItem = snapshot.getValue(CartItem.class);
                if (cartItem != null) {
                    // Create a view for each cart item and add it to llCartItemContainer
                    View itemView = createCartItemView(llCartItemContainer.getContext(), cartItem);
                    llCartItemContainer.addView(itemView);
                }
            }
        });
    }

    private View createCartItemView(Context context, CartItem cartItem) {
        // Create a view for each cart item (customize based on your layout)
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.dialog_fragment_queue_order_to_process_item, null);

        TextView tvQueueItemName, tvQueueItemPrice, tvQueueItemQuantity, tvQueueItemTotalPrice;

        tvQueueItemName = itemView.findViewById(R.id.tvOrderQueueItemName);
        tvQueueItemPrice = itemView.findViewById(R.id.tvOrderQueueItemPrice);
        tvQueueItemQuantity = itemView.findViewById(R.id.tvOrderQueueItemQuantity);
        tvQueueItemTotalPrice = itemView.findViewById(R.id.tvOrderQueueItemTotalPrice);

        // Populate the itemView with cartItem data (assuming you have TextViews in your layout)
        tvQueueItemPrice.setText(phpCurrencyFormatter.formatAsPHP(cartItem.getPrice()));
        tvQueueItemName.setText(cartItem.getName());
        tvQueueItemTotalPrice.setText(phpCurrencyFormatter.formatAsPHP(cartItem.getTotalPrice()));
        tvQueueItemQuantity.setText("* "+ cartItem.getQuantity());
        return itemView;
    }
}
