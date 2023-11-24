package com.example.coftea.Order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.OrderItemList.CartItemResult;
import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;

public class OrderDialogViewModel extends ViewModel {
    private MutableLiveData<CartItem> _orderItem = new MutableLiveData<>();
    public LiveData<CartItem> orderItem;

    private MutableLiveData<CartItemResult> _orderItemResult = new MutableLiveData<>();
    public LiveData<CartItemResult> orderItemResult;

    public OrderDialogViewModel(){
        orderItem = _orderItem;
        orderItemResult = _orderItemResult;
    }
    public void setOrderItem(Product product) {
        CartItem cartItem = new CartItem(product.getId(), product.getId(), product.getName(), product.getPrice(), 1, product.getPrice(), product.getIngredientPath());
        cartItem.setImageUrl(product.getImageUrl());
        _orderItem.setValue(cartItem);
    }

    public void setOrderItem(Product product, Integer quantity) {
        CartItem cartItem = new CartItem(product.getId(), product.getId(), product.getName(), product.getPrice(), quantity, product.getPrice(), product.getIngredientPath());
        cartItem.setImageUrl(product.getImageUrl());
        _orderItem.setValue(cartItem);
    }

    public void setOrderItem(CartItem cartItem) {
        _orderItem.setValue(cartItem);
    }

    public void addOrderItemQuantity() {
        CartItem item = _orderItem.getValue();
        if(item == null) return;
        if(item.getQuantity() >= 99) return;
        int newQty = item.getQuantity()+1;
        Double totalPrice = item.getPrice() * newQty;
        item.setQuantity(newQty);
        item.setTotalPrice(totalPrice);
        _orderItem.postValue(item);
    }
    public boolean minusOrderItemQuantity() {
        CartItem item = _orderItem.getValue();
        if(item == null) return false;
        if(item.getQuantity() <= 1) return false;
        int newQty = item.getQuantity()-1;
        Double totalPrice = item.getPrice() * newQty;
        item.setQuantity(newQty);
        item.setTotalPrice(totalPrice);
        _orderItem.postValue(item);
        return true;
    }
    public void clearOrderItem() {
        _orderItem.postValue(null);
        _orderItemResult.postValue(null);
    }

    public void setOrderItemLoading(){
        _orderItemResult.setValue(new CartItemResult());
    }
    public void postOrderItemResult(CartItemResult result){
        _orderItemResult.postValue(result);
    }

    public void removeOrderItem(String pathToOrder){
        RealtimeDB realtimeDB = new RealtimeDB<>("order/"+pathToOrder);
        realtimeDB.getDatabaseReference().removeValue();
    }
}
