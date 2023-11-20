package com.example.coftea.Order;


import android.util.Log;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderDatabase {

    private final RealtimeDB<CartItem> orderDB;
    private final DatabaseReference orderItemsDBRef;
    private final DatabaseReference orderDBRef;
    public OrderDatabase(String user){
        orderDB = new RealtimeDB("order/"+user);
        orderDBRef = orderDB.getDatabaseReference();
        orderItemsDBRef = orderDB.getDatabaseReference().child("items");
    }

    public Task<Boolean> AddOrderItemToCart(CartItem cartItem) {

        String keyToUse = orderItemsDBRef.push().getKey();
        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        try {
//            CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), cartItem.getQuantity(), cartItem.getTotalPrice());
            cartItem.setKey(keyToUse);
            orderItemsDBRef.child(keyToUse).setValue(cartItem)
            .addOnSuccessListener(unused -> taskCompletionSource.setResult(true))
            .addOnFailureListener(e -> taskCompletionSource.setResult(false))
            .addOnCanceledListener(() -> taskCompletionSource.setResult(false));
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
            taskCompletionSource.setResult(false);
        }
        return taskCompletionSource.getTask();
    }

    public Task<Boolean> UpdateOrderItemToCart(String id, CartItem cartItem) {

        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        try {
            Map<String, Object> itemToPost = new HashMap<>();
            itemToPost.put("quantity", cartItem.getQuantity());
            itemToPost.put("totalPrice", cartItem.getTotalPrice());

            orderItemsDBRef.child(id).setValue(itemToPost)
                    .addOnSuccessListener(unused -> taskCompletionSource.setResult(true))
                    .addOnFailureListener(e -> taskCompletionSource.setResult(false))
                    .addOnCanceledListener(() -> taskCompletionSource.setResult(false));
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
            taskCompletionSource.setResult(false);
        }

        return taskCompletionSource.getTask();
    }

    public Task<Boolean> UpdateOrderCheckoutSessionID(String sessionId){
        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        try {
            orderDBRef.child("checkoutSessionID").setValue(sessionId)
                .addOnSuccessListener(unused -> taskCompletionSource.setResult(true))
                .addOnFailureListener(e -> taskCompletionSource.setResult(false))
                .addOnCanceledListener(() -> taskCompletionSource.setResult(false));
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
            taskCompletionSource.setResult(false);
        }
        return taskCompletionSource.getTask();

    }

    public DatabaseReference getOrderDBRef(){
        return this.orderDBRef;
    }
}
