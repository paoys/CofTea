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
import com.example.coftea.repository.RealtimeDB;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.CustomerProductViewHolder> {
    private ArrayList<Product> productList;
    private RealtimeDB<String> realtimeDBIngredients; // RealtimeDB instance for ingredients

    public CustomerProductAdapter(ArrayList<Product> productList) {
        this.productList = productList;
        this.realtimeDBIngredients = new RealtimeDB<>("products_ingredients"); // Path to ingredients in Firebase
    }

    public void UpdateList(ArrayList<Product> productList) {
        this.productList = productList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_product_list_item, parent, false);
        return new CustomerProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerProductViewHolder holder, int position) {
        PHPCurrencyFormatter phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();
        Product product = productList.get(position);

        String price = phpCurrencyFormatter.formatAsPHP(product.getPrice());

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(price);

        // Fetch product ingredients from Firebase Realtime Database
        String productId = product.getId(); // Assuming product has an ID
        realtimeDBIngredients.getDatabaseReference().child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> ingredients = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Long amountLong = snapshot.child("amount").getValue(Long.class);
                        String name = snapshot.child("name").getValue(String.class);

                        if (name != null && !name.isEmpty()) {
                            String amount = (amountLong != null) ? String.valueOf(amountLong) : "";
                            if (!amount.isEmpty()) {
                                String ingredient = name + " - " + amount;
                                ingredients.add(ingredient);
                            }
                        }
                    }

                    if (!ingredients.isEmpty()) {
                        String formattedIngredients = formatProductDescription(ingredients);
                        holder.tvProductDesc.setText(formattedIngredients);
                    } else {
                        holder.tvProductDesc.setText("No ingredients available");
                    }
                } else {
                    holder.tvProductDesc.setText("No ingredients available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.tvProductDesc.setText("Failed to fetch ingredients");
            }
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

    static class CustomerProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvProductPrice;
        ImageView ivProductImage;
        TextView tvProductDesc;

        CustomerProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvCustomerProductName);
            tvProductPrice = itemView.findViewById(R.id.tvCustomerProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivCustomerProductImage);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
        }
    }

    private String formatProductDescription(List<String> ingredients) {
        StringBuilder formattedDescription = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            String ingredient = ingredients.get(i);
            formattedDescription.append(ingredient);
            if (i != ingredients.size() - 1) {
                formattedDescription.append(", ");
            }
        }
        return formattedDescription.toString();
    }
}

