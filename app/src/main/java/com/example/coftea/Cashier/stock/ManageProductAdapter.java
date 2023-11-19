package com.example.coftea.Cashier.stock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ProductViewHolder> {



    private List<ModelManageProduct> productList;

    public ManageProductAdapter(List<ModelManageProduct> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartadapter, parent, false);
        return new ProductViewHolder(view, productList);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ModelManageProduct product = productList.get(position);
        holder.productNameTextView.setText(product.getName());
        holder.productIdTextView.setText(product.getId());
        holder.productPriceEditText.setText(String.valueOf(product.getPrice()));

        // Load and display the image using Picasso
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get().load(product.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.productImageView);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productIdTextView;
        TextView productPriceEditText;
        ImageView productImageView;
        Button editBTN, deleteBTN;
        List<ModelManageProduct> productList;

        ProductViewHolder(View itemView, List<ModelManageProduct> productList) {
            super(itemView);
            this.productList = productList;

            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productIdTextView = itemView.findViewById(R.id.productIdTextView);
            productPriceEditText = itemView.findViewById(R.id.productPriceEditText);
            productImageView = itemView.findViewById(R.id.productImageView);

            // Set up click listeners for the edit and delete buttons
            editBTN = itemView.findViewById(R.id.BTNEdit);
            deleteBTN = itemView.findViewById(R.id.BTNDelete);

            editBTN.setOnClickListener(view -> {
                // Handle the edit button click here
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Get the selected item from the list
                    ModelManageProduct product = productList.get(position);

                    // Create a dialog for editing the product
                    showEditProductDialog(itemView.getContext(), product);
                }
            });

            deleteBTN.setOnClickListener(view -> {
                // Show a confirmation dialog
                showDeleteConfirmationDialog(getAdapterPosition());
            });
        }

        // Method to show a confirmation dialog before deletion
        private void showDeleteConfirmationDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setMessage("Are you sure you want to delete this item?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // User confirmed the deletion, call the delete method
                ModelManageProduct product = productList.get(position);
                deleteProductFromDatabase(product.getId());
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                // User canceled the deletion, do nothing
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        // Edit a product in the database
        private static void editProductInDatabase(String productId, String newName, Double newPrice) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference productsRef = database.getReference("products");

            // Find the specific product using its unique key (productId)
            DatabaseReference productToUpdateRef = productsRef.child(productId);

            // Update the product details
            productToUpdateRef.child("name").setValue(newName);
            productToUpdateRef.child("price").setValue(newPrice)
                    .addOnSuccessListener(aVoid -> {
                        // Product details updated successfully
                        Log.d("Database", "Product details updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update product details
                        Log.e("Database", "Failed to update product details: " + e.getMessage());
                    });
        }

        // Delete a product from the database
        private static void deleteProductFromDatabase(String productId) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference productsRef = database.getReference("products");

            // Find the specific product using its unique key (productId)
            DatabaseReference productToDeleteRef = productsRef.child(productId);

            // Remove the product from the database
            productToDeleteRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Product deleted successfully
                        Log.d("Database", "Product deleted successfully");
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to delete the product
                        Log.e("Database", "Failed to delete product: " + e.getMessage());
                    });
        }

        // Method to show the edit product dialog
        private void showEditProductDialog(Context context, final ModelManageProduct product) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_product_dialog, null);

            // Initialize the dialog views
            final EditText productNameEditText = dialogView.findViewById(R.id.editProductNameEditText);
            final EditText productPriceEditText = dialogView.findViewById(R.id.editProductPriceEditText);

            // Set the existing product name and price in the dialog
            productNameEditText.setText(product.getName());
            productPriceEditText.setText(String.valueOf(product.getPrice()));

            builder.setView(dialogView)
                    .setTitle("Edit Product")
                    .setPositiveButton("Save", (dialog, which) -> {
                        try {
                            String newProductName = productNameEditText.getText().toString();
                            String _newProductPrice = productPriceEditText.getText().toString();
                            Double newProductPrice = Double.parseDouble(_newProductPrice);
                            editProductInDatabase(product.getId(), newProductName, newProductPrice);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        // Call the edit method with the obtained values
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
