package com.example.coftea.Order;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.data.Order;
import com.example.coftea.data.OrderItem;
import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderViewModel extends ViewModel {
    private MutableLiveData<Order> _order = new MutableLiveData<>();
    public LiveData<Order> order;

    private MutableLiveData<OrderResult> _orderResult = new MutableLiveData<>();
    public LiveData<OrderResult> orderResult;

    private final RealtimeDB<Order> realtimeDB;

    public OrderViewModel(String id){
        order = _order;
        orderResult = _orderResult;
        realtimeDB = new RealtimeDB<>("order/"+id);
        listenUpdate();
    }

    private void listenUpdate(){
        realtimeDB.getDatabaseReference().get()
                .addOnSuccessListener(dataSnapshot -> {
                    Order order = dataSnapshot.getValue(Order.class);
                    OrderResult orderResult = new OrderResult(order);
                    _orderResult.setValue(orderResult);
                })
                .addOnFailureListener(e -> {
                    OrderResult orderResult = new OrderResult("Get Order Info Failed");
                    _orderResult.setValue(orderResult);
                })
                .addOnCanceledListener(() -> {
                    OrderResult orderResult = new OrderResult("Get Order Info Cancelled");
                    _orderResult.setValue(orderResult);
                });
    }
}
