package com.example.coftea.Order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.OrderItemList.OrderItemResult;
import com.example.coftea.data.OrderItem;
import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;

import java.util.Date;

public class OrderDialogViewModel extends ViewModel {
    private MutableLiveData<OrderItem> _orderItem = new MutableLiveData<>();
    public LiveData<OrderItem> orderItem;

    private MutableLiveData<OrderItemResult> _orderItemResult = new MutableLiveData<>();
    public LiveData<OrderItemResult> orderItemResult;

    public OrderDialogViewModel(){
        orderItem = _orderItem;
        orderItemResult = _orderItemResult;
    }
    public void setOrderItem(Product product) {
        String tempID = String.valueOf(new Date().getTime());
        OrderItem orderItem = new OrderItem(tempID, product,1);
        _orderItem.setValue(orderItem);
    }

    public void setOrderItem(Product product, Integer quantity) {
        String tempID = String.valueOf(new Date().getTime());
        OrderItem orderItem = new OrderItem(tempID, product,quantity);
        _orderItem.setValue(orderItem);
    }

    public void setOrderItem(OrderItem orderItem) {
        _orderItem.setValue(orderItem);
    }

    public void addOrderItemQuantity() {
        OrderItem item = _orderItem.getValue();
        if(item == null) return;
        item.addQuantity();
        _orderItem.postValue(item);
    }
    public boolean minusOrderItemQuantity() {
        OrderItem item = _orderItem.getValue();
        if(item == null) return false;
        boolean isValid = item.minusQuantity();
        _orderItem.postValue(item);
        return isValid;
    }
    public void clearOrderItem() {
        _orderItem.postValue(null);
        _orderItemResult.postValue(null);
    }

    public void setOrderItemLoading(){
        _orderItemResult.setValue(new OrderItemResult());
    }
    public void postOrderItemResult(OrderItemResult result){
        _orderItemResult.postValue(result);
    }

    public void removeOrderItem(String pathToOrder){
        RealtimeDB realtimeDB = new RealtimeDB<>("order/"+pathToOrder);
        realtimeDB.getDatabaseReference().removeValue();
    }
}
