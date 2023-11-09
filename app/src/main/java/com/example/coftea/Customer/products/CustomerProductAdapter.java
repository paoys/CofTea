package com.example.coftea.Customer.products;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.example.coftea.data.Product;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.CustomerProductViewHolder>  {
    private ArrayList<Product> productList;

    public CustomerProductAdapter(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public void UpdateList(ArrayList<Product> productList) {
        this.productList = productList;
        this.notifyDataSetChanged();
    }

    @NonNull
    public CustomerProductAdapter.CustomerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_product_list_item, parent, false);
        return new CustomerProductAdapter.CustomerProductViewHolder(view, productList);
    }


    public void onBindViewHolder(@NonNull CustomerProductAdapter.CustomerProductViewHolder holder, int position) {

        PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();
        Product product = productList.get(position);

        String price = phpCurrencyFormatter.formatAsPHP(product.getPrice());

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(price);

        // Load and display the image using Picasso
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get().load(product.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.ivProductImage);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class CustomerProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvProductPrice;
        ImageView ivProductImage;
        List<Product> productList;

        CustomerProductViewHolder(View itemView, List<Product> productList) {
            super(itemView);
            this.productList = productList;
            tvProductName = itemView.findViewById(R.id.tvCustomerProductName);
            tvProductPrice = itemView.findViewById(R.id.tvCustomerProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivCustomerProductImage);
        }
    }
}
