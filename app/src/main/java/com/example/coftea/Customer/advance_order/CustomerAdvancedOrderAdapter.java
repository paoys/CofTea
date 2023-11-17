package com.example.coftea.Customer.advance_order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Order.OrderDialogViewModel;
import com.example.coftea.R;
import com.example.coftea.data.Product;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdvancedOrderAdapter extends RecyclerView.Adapter<CustomerAdvancedOrderAdapter.CustomerOrderViewHolder> {
    private ArrayList<Product> productList;
    private AdvanceOrderViewModel advanceOrderViewModel;
    private OrderDialogViewModel orderDialogViewModel;

    public CustomerAdvancedOrderAdapter(ArrayList<Product> productList, AdvanceOrderViewModel advanceOrderViewModel, OrderDialogViewModel orderDialogViewModel) {
        this.productList = productList;
        this.advanceOrderViewModel = advanceOrderViewModel;
        this.orderDialogViewModel = orderDialogViewModel;
    }

    public void UpdateList(ArrayList<Product> productList) {
        this.productList = productList;
        this.notifyDataSetChanged();
    }

    @NonNull
    public CustomerAdvancedOrderAdapter.CustomerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_product_list_order_item, parent, false);
        return new CustomerAdvancedOrderAdapter.CustomerOrderViewHolder(view, productList);
    }


    public void onBindViewHolder(@NonNull CustomerAdvancedOrderAdapter.CustomerOrderViewHolder holder, int position) {

        PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();
        Product product = productList.get(position);

        String price = phpCurrencyFormatter.formatAsPHP(product.getPrice());

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(price);
        holder.btnProductAddToCart.setOnClickListener(view -> {
            orderDialogViewModel.setOrderItem(product);
        });

        // Load and display the image using Picasso
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get().load(product.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.ivProductImage);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class CustomerOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvProductPrice;
        Button btnProductAddToCart;
        ImageView ivProductImage;
        List<Product> productList;

        CustomerOrderViewHolder(View itemView, List<Product> productList) {
            super(itemView);
            this.productList = productList;
            tvProductName = itemView.findViewById(R.id.tvCustomerProductName);
            tvProductPrice = itemView.findViewById(R.id.tvCustomerProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivCustomerProductImage);
            btnProductAddToCart = itemView.findViewById(R.id.btnCustomerProductAddToCart);
        }
    }
}
