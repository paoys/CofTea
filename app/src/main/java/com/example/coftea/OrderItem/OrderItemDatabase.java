package com.example.coftea.OrderItem;


import android.util.Log;

import com.example.coftea.data.OrderItem;
import com.example.coftea.repository.RealtimeDB;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class OrderItemDatabase {
    private final RealtimeDB<OrderItem> orderItemDB;
    private final DatabaseReference orderItemDBRef;
    public OrderItemDatabase(String user){
        orderItemDB = new RealtimeDB("order_item/"+user);
        orderItemDBRef = orderItemDB.getDatabaseReference();
    }

    public Task<Boolean> AddOrderItemToCart(OrderItem orderItem) {

        String keyToUse = orderItemDBRef.push().getKey();
        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        try {
            Map<String, Object> itemToPost = new HashMap<>();
            itemToPost.put("id", orderItem.getId());
            itemToPost.put("quantity", orderItem.getQuantity());
            itemToPost.put("totalPrice", orderItem.getTotalPrice());
            itemToPost.put("product", "products/"+orderItem.getProduct().getId());

            orderItemDBRef.child(keyToUse).setValue(itemToPost)
            .addOnSuccessListener(unused -> taskCompletionSource.setResult(true))
            .addOnFailureListener(e -> taskCompletionSource.setResult(false))
            .addOnCanceledListener(() -> taskCompletionSource.setResult(false));
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
            taskCompletionSource.setResult(false);
        }
        return taskCompletionSource.getTask();
    }

    public Task<Boolean> UpdateOrderItemToCart(String id,OrderItem orderItem) {

        final TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        Map<String, Object> itemToPost = new HashMap<>();
        itemToPost.put("quantity", orderItem.getQuantity());
        itemToPost.put("totalPrice", orderItem.getTotalPrice());

        orderItemDBRef.child(id).setValue(itemToPost)
                .addOnSuccessListener(unused -> taskCompletionSource.setResult(true))
                .addOnFailureListener(e -> taskCompletionSource.setResult(false))
                .addOnCanceledListener(() -> taskCompletionSource.setResult(false));

        return taskCompletionSource.getTask();
    }
}
