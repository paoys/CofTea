package com.example.coftea.OrderItemList;

import com.example.coftea.data.OrderItem;

public class OrderItemResult {
    public boolean loading = true;
    public OrderItem success;
    public String error;

    public OrderItemResult() {}
    public OrderItemResult(OrderItem item) {
        this.loading = false;
        this.success = item;
    }
    public OrderItemResult(String error) {
        this.loading = false;
        this.error = error;
    }
}
