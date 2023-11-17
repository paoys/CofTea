package com.example.coftea.OrderItemList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Order.OrderDialogViewModel;
import com.example.coftea.R;
import com.example.coftea.data.OrderItem;
import com.example.coftea.data.Product;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.example.coftea.utilities.UserProvider;

import java.util.ArrayList;

public class OrderItemListItemAdapter extends RecyclerView.Adapter<OrderItemListItemAdapter.OrderItemListItemViewHolder> {
    private ArrayList<OrderItem> orderItemList;
    private UserProvider userProvider = UserProvider.getInstance();
    private OrderDialogViewModel orderDialogViewModel;
    private OrderItemListViewModel  orderItemListViewModel;

    public OrderItemListItemAdapter(ArrayList<OrderItem> orderItemList, OrderDialogViewModel orderDialogViewModel, OrderItemListViewModel orderItemListViewModel) {
        this.orderItemList = orderItemList;
        this.orderDialogViewModel = orderDialogViewModel;
        this.orderItemListViewModel = orderItemListViewModel;
    }

    public void UpdateList(ArrayList<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
        this.notifyDataSetChanged();
    }

    @NonNull
    public OrderItemListItemAdapter.OrderItemListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_list_item, parent, false);
        return new OrderItemListItemAdapter.OrderItemListItemViewHolder(view);
    }


    public void onBindViewHolder(@NonNull OrderItemListItemAdapter.OrderItemListItemViewHolder holder, int position) {

        PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();
        OrderItem orderItem = orderItemList.get(position);
        Product product = orderItem.getProduct();
        if(product != null) {
            String price = phpCurrencyFormatter.formatAsPHP(product.getPrice());
            String totalPrice = phpCurrencyFormatter.formatAsPHP(orderItem.getTotalPrice());
            holder.tvOrderItemName.setText(product.getName());
            holder.tvOrderItemPrice.setText(price);
            holder.tvOrderItemTotalPrice.setText(totalPrice);
            holder.tvOrderItemQuantity.setText(String.valueOf(orderItem.getQuantity()));
        }
        else{
            holder.tvOrderItemName.setText("NOT FOUND!");
            holder.tvOrderItemPrice.setText("---");
        }

        holder.btnRemoveOrderItem.setOnClickListener(view -> {
            String userMobileNo = userProvider.getUser().second;
            orderDialogViewModel.removeOrderItem(userMobileNo+"/items/"+orderItem.getId());
        });

//        holder.btnRemoveOrderItem.setOnClickListener(view -> {
//            orderItemDialogViewModel.setOrderItem(orderItem);
//        });


    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    static class OrderItemListItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderItemName;
        TextView tvOrderItemPrice;
        TextView tvOrderItemTotalPrice;
        TextView tvOrderItemQuantity;
        Button btnRemoveOrderItem;

        OrderItemListItemViewHolder(View itemView) {
            super(itemView);
            tvOrderItemName = itemView.findViewById(R.id.tvOrderItemName);
            tvOrderItemPrice = itemView.findViewById(R.id.tvOrderItemPrice);
            btnRemoveOrderItem = itemView.findViewById(R.id.btnRemoveOrderItem);
            tvOrderItemTotalPrice = itemView.findViewById(R.id.tvOrderItemTotalPrice);
            tvOrderItemQuantity = itemView.findViewById(R.id.tvOrderItemQuantity);
        }
    }
}
