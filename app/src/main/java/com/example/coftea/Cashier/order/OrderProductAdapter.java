package com.example.coftea.Cashier.order;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ProductViewHolder> {
    private List<ModelOrderProduct> productList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference cartRef;

    public OrderProductAdapter(List<ModelOrderProduct> productList) {
        this.productList = productList;
        this.cartRef = database.getReference("cashier/cart");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productadapter, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ModelOrderProduct product = productList.get(position);
        holder.productNameTextView.setText(product.getName());
        holder.productIdTextView.setText(product.getId());
        holder.productPriceEditText.setText(String.valueOf(product.getPrice()));

        // Load and display the image using Picasso
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get().load(product.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.productImageView);
        }

        // Set an OnClickListener for the Add to Cart button
        holder.addToCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.productImageView.getContext())
                        .setContentHolder(new ViewHolder(R.layout.addtocart_popup))
                        .setExpanded(true, 1200)
                        .create();

                View view = dialogPlus.getHolderView();
                EditText productIDEditText = view.findViewById(R.id.productIDEditText);
                EditText productNameEditText = view.findViewById(R.id.productNameEditText);
                EditText productQuantity = view.findViewById(R.id.productQuantityEditText);
                Button btnAddCart = view.findViewById(R.id.btnAddCart);
                Spinner priceSpinner = view.findViewById(R.id.priceSpinner); // Initialize the priceSpinner

                // Initialize the priceSpinner with price options
                ArrayAdapter<CharSequence> priceAdapter = ArrayAdapter.createFromResource(
                        view.getContext(),
                        R.array.price_options, // Reference the price options array
                        android.R.layout.simple_spinner_item
                );
                priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                priceSpinner.setAdapter(priceAdapter);

                // Initialize the EditText fields with model values
                productIDEditText.setText(product.getId());
                productNameEditText.setText(product.getName());

                dialogPlus.show();
                btnAddCart.setOnClickListener(v1 -> {
                    // Get the quantity entered by the user
                    int quantity = Integer.parseInt(productQuantity.getText().toString());

                    Double totalPrice = product.getPrice() * quantity;

                    // Create a Cart object with the selected product's details and quantity
                    CartItem cartItem = new CartItem(product.getId(), product.getId(), product.getName(), product.getPrice(), quantity, totalPrice, product.getIngredientsPath());
                    cartItem.setImageUrl(product.getImageUrl()); // Set the image URL

                    // Save the cart item to the "cart" database with a unique key
                    saveCartItemToCartDatabase(cartItem);

                    // Close the dialog after adding to the cart
                    dialogPlus.dismiss();
                });
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
        ImageView productImageView;
        Button addToCartBTN;

        ProductViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productIdTextView = itemView.findViewById(R.id.productIdTextView);
            productPriceEditText = itemView.findViewById(R.id.productPriceEditText);
            productImageView = itemView.findViewById(R.id.productImageView);
            addToCartBTN = itemView.findViewById(R.id.addToCartBTN); // Initialize the Add to Cart button
        }
    }

    // Function to save a Cart item to the "cart" database with a unique key
    private void saveCartItemToCartDatabase(CartItem cartItem) {

        // Check if an item with the same product ID already exists in the cart
        Query query = cartRef.orderByChild("id").equalTo(cartItem.getId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Item with the same product ID already exists in the cart
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the existing item's key and details
                        String existingKey = snapshot.getKey();
                        CartItem existingCartItemItem = snapshot.getValue(CartItem.class);

                        // Update the quantity of the existing item in the cart
                        if (existingCartItemItem != null) {
                            int updatedQuantity = existingCartItemItem.getQuantity() + cartItem.getQuantity();
                            Double totalPrice = updatedQuantity * existingCartItemItem.getPrice();
                            cartItem.setQuantity(updatedQuantity);
                            cartItem.setTotalPrice(totalPrice);
                        }

                        // Set the image URL of the updated cart item
                        cartItem.setImageUrl(cartItem.getImageUrl());
                        cartItem.setKey(existingKey);
                        // Update the existing cart item with the new quantity
                        updateCartItemInCartDatabase(existingKey, cartItem);
                        return;
                    }
                } else {
                    // Item with this product ID does not exist in the cart, add it as a new item
                    addCartItemToCartDatabase(cartItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database error
                Log.e("CartDatabase", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Function to add a new Cart item to the "cart" database with a specific key (product ID)
    private void addCartItemToCartDatabase(CartItem cartItem) {
        // Use the product ID as the key in the cart database
        String productId = cartItem.getId();

        // Create a reference to the specific item with the product ID as the key
        DatabaseReference specificCartItemRef = cartRef.child(productId);
        String cartItemKey = specificCartItemRef.push().getKey();
        cartItem.setKey(cartItemKey);
        specificCartItemRef.setValue(cartItem)
                .addOnSuccessListener(aVoid -> {
                    // New item added to the cart successfully
                    Log.d("CartDatabase", "New item added to cart successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to add the item to the cart
                    Log.e("CartDatabase", "Failed to add item to cart: " + e.getMessage());
                });
    }

    // Function to update an existing Cart item in the "cart" database
    private void updateCartItemInCartDatabase(String cartItemKey, CartItem updatedCartItemItem) {

        // Reference to the specific item to be updated
        DatabaseReference itemRef = cartRef.child(cartItemKey);

        // Update the item with the new details
        itemRef.setValue(updatedCartItemItem)
                .addOnSuccessListener(aVoid -> {
                    // Item updated successfully
                    Log.d("CartDatabase", "Item updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to update the item
                    Log.e("CartDatabase", "Failed to update item: " + e.getMessage());
                });
    }
}
