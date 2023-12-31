package com.example.coftea.Order;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.data.Order;
import com.example.coftea.repository.RealtimeDB;

public class OrderViewModel extends ViewModel {
    private MutableLiveData<Order> _order = new MutableLiveData<>();
    public LiveData<Order> order;

    private MutableLiveData<OrderResult> _orderResult = new MutableLiveData<>();
    public LiveData<OrderResult> orderResult;

    public RealtimeDB<Order> getRealtimeDB() {
        return realtimeDB;
    }

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
                    Object test = dataSnapshot.child("isOrderPlaced").getValue();
                    Log.e("====", String.valueOf(test));
                    Log.e("====", String.valueOf(order));
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
