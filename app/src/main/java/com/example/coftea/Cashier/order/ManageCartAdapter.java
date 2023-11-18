package com.example.coftea.Cashier.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ManageCartAdapter extends RecyclerView.Adapter<ManageCartAdapter.ProductViewHolder> {
    private List<CartItem> productList;
    private Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference cartRef;
    public ManageCartAdapter(Context context, List<CartItem> productList) {
        this.context = context;
        this.productList = productList;
        this.cartRef = database.getReference("cashier/cart");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productincart, parent, false);
        return new ProductViewHolder(view, productList);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CartItem product = productList.get(position);
        holder.productNameTextView.setText(product.getName());
        holder.productIdTextView.setText(product.getId());
        holder.productPriceEditText.setText(product.getPrice());
        holder.productQuantityTextView.setText("" + product.getQuantity());

        // Load and display the image using Picasso
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get().load(product.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.productImageView);
        }

        // Set up click listeners for the edit and delete buttons
        holder.editBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(product);
            }
        });

        holder.deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productIdTextView;
        TextView productPriceEditText;
        TextView productQuantityTextView;
        ImageView productImageView;
        Button editBTN, deleteBTN;
        List<CartItem> productList;

        ProductViewHolder(View itemView, List<CartItem> productList) {
            super(itemView);
            this.productList = productList;

            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productIdTextView = itemView.findViewById(R.id.productIdTextView);
            productPriceEditText = itemView.findViewById(R.id.productPriceEditText);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            editBTN = itemView.findViewById(R.id.BTNEdit);
            deleteBTN = itemView.findViewById(R.id.BTNDelete);
        }
    }

    private void showEditDialog(CartItem product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Item");
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.edit_cart_dialog, null);
        builder.setView(viewInflated);

        final EditText editName = viewInflated.findViewById(R.id.editProductNameEditText);
        final EditText editPrice = viewInflated.findViewById(R.id.editProductPriceEditText);
        final EditText editQuantity = viewInflated.findViewById(R.id.productQuantityEditText);
        final EditText editID = viewInflated.findViewById(R.id.editIDEdittext);

        editQuantity.setText(String.valueOf(product.getQuantity()));
        editPrice.setText(product.getPrice());
        editID.setText(product.getId());
        editName.setText(product.getName());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the updated values from the EditText fields
                String newName = editName.getText().toString();
                String newPrice = editPrice.getText().toString();
                String newQuantity = editQuantity.getText().toString();
                String newID = editID.getText().toString();

                // Update the product object with the updated values
                product.setName(newName);
                product.setPrice(newPrice);
                product.setQuantity(Integer.parseInt(newQuantity));
                product.setId(newID);

                // Update the item in the database
                updateItemInDatabase(product.getId(), product);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private void showDeleteConfirmationDialog(CartItem product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this item from the cart?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItemFromDatabase(product.getId());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void updateItemInDatabase(String itemId, CartItem updatedProduct) {

        DatabaseReference itemRef = cartRef.child(itemId);
        itemRef.setValue(updatedProduct)
                .addOnSuccessListener(aVoid -> {
                    // Item updated successfully
                    Log.d("CartDatabase", "Item updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to update the item
                    Log.e("CartDatabase", "Failed to update item: " + e.getMessage());
                });
    }


    private void deleteItemFromDatabase(String itemId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cartRef = database.getReference("cart");

        DatabaseReference itemRef = cartRef.child(itemId);
        itemRef.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("CartDatabase", "Item deleted successfully"))
                .addOnFailureListener(e -> Log.e("CartDatabase", "Failed to delete item: " + e.getMessage()));
    }
}
