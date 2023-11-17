package com.example.coftea.Order;


import android.util.Log;

import com.example.coftea.data.Order;
import com.example.coftea.data.OrderItem;
import com.example.coftea.repository.RealtimeDB;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderDatabase {

    private final RealtimeDB<OrderItem> orderDB;
    private final DatabaseReference orderItemsDBRef;
    private final DatabaseReference orderDBRef;
    public OrderDatabase(String user){
        orderDB = new RealtimeDB("order/"+user);
        orderDBRef = orderDB.getDatabaseReference();
        orderItemsDBRef = orderDB.getDatabaseReference().child("items");
    }

    public Task<Boolean> AddOrderItemToCart(OrderItem orderItem) {

        String keyToUse = orderItemsDBRef.push().getKey();
        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        try {
            Map<String, Object> itemToPost = new HashMap<>();
            itemToPost.put("id", orderItem.getId());
            itemToPost.put("quantity", orderItem.getQuantity());
            itemToPost.put("totalPrice", orderItem.getTotalPrice());
            itemToPost.put("product", "products/"+orderItem.getProduct().getId());

            orderItemsDBRef.child(keyToUse).setValue(itemToPost)
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

    public Task<Boolean> UpdateOrderItemToCart(String id,OrderItem orderItem) {

        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        try {
            Map<String, Object> itemToPost = new HashMap<>();
            itemToPost.put("quantity", orderItem.getQuantity());
            itemToPost.put("totalPrice", orderItem.getTotalPrice());

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
