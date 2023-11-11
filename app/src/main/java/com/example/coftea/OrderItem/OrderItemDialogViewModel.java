package com.example.coftea.OrderItem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.data.OrderItem;
import com.example.coftea.data.Product;

import java.util.Date;

public class OrderItemDialogViewModel extends ViewModel {
    private MutableLiveData<OrderItem> _orderItem = new MutableLiveData<>();
    public LiveData<OrderItem> orderItem;

    private MutableLiveData<OrderItemResult> _orderItemResult = new MutableLiveData<>();
    public LiveData<OrderItemResult> orderItemResult;

    public OrderItemDialogViewModel(){
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
        _orderItem.setValue(item);
    }
    public boolean minusOrderItemQuantity() {
        OrderItem item = _orderItem.getValue();
        if(item == null) return false;
        boolean isValid = item.minusQuantity();
        _orderItem.setValue(item);
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

}
