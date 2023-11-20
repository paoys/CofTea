package com.example.coftea.OrderItemList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.Order.OrderDialogViewModel;
import com.example.coftea.R;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.example.coftea.utilities.UserProvider;

import java.util.ArrayList;

public class CartItemListItemAdapter extends RecyclerView.Adapter<CartItemListItemAdapter.OrderItemListItemViewHolder> {
    private ArrayList<CartItem> cartItemList;
    private UserProvider userProvider = UserProvider.getInstance();
    private OrderDialogViewModel orderDialogViewModel;
    private CartItemListViewModel cartItemListViewModel;

    public CartItemListItemAdapter(ArrayList<CartItem> cartItemList, OrderDialogViewModel orderDialogViewModel, CartItemListViewModel cartItemListViewModel) {
        this.cartItemList = cartItemList;
        this.orderDialogViewModel = orderDialogViewModel;
        this.cartItemListViewModel = cartItemListViewModel;
    }

    public void UpdateList(ArrayList<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        this.notifyDataSetChanged();
    }

    @NonNull
    public CartItemListItemAdapter.OrderItemListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_list_item, parent, false);
        return new CartItemListItemAdapter.OrderItemListItemViewHolder(view);
    }


    public void onBindViewHolder(@NonNull CartItemListItemAdapter.OrderItemListItemViewHolder holder, int position) {

        PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();
        CartItem cartItem = cartItemList.get(position);

        String price = phpCurrencyFormatter.formatAsPHP(cartItem.getPrice());
        String totalPrice = phpCurrencyFormatter.formatAsPHP(cartItem.getTotalPrice());
        holder.tvOrderItemName.setText(cartItem.getName());
        holder.tvOrderItemPrice.setText(price);
        holder.tvOrderItemTotalPrice.setText(totalPrice);
        holder.tvOrderItemQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.btnRemoveOrderItem.setOnClickListener(view -> {
            String userMobileNo = userProvider.getUser().second;
            orderDialogViewModel.removeOrderItem(userMobileNo+"/items/"+ cartItem.getKey());
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
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
