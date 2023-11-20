package com.example.coftea.OrderItemList;

import com.example.coftea.Cashier.order.CartItem;

public class CartItemResult {
    public boolean loading = true;
    public CartItem success;
    public String error;

    public CartItemResult() {}
    public CartItemResult(CartItem item) {
        this.loading = false;
        this.success = item;
    }
    public CartItemResult(String error) {
        this.loading = false;
        this.error = error;
    }
}
