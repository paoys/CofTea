package com.example.coftea.Order;

import com.example.coftea.data.Order;

public class OrderResult {
    public boolean loading = true;
    public Order success;
    public String error;

    public OrderResult(Order item) {
        this.loading = false;
        this.success = item;
    }
    public OrderResult(String error) {
        this.loading = false;
        this.error = error;
    }
}
