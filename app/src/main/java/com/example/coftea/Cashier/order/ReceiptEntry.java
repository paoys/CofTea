package com.example.coftea.Cashier.order;

import com.example.coftea.data.OrderStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ReceiptEntry implements Serializable {
    private List<CartItem> cartItems;
    private double totalPayment;
    private String customerName;
    private String customerPhone;
    private Date date;

    public Long getCreatedAt() {
        return createdAt;
    }

    private Long createdAt;
    private OrderStatus status;

    public ReceiptEntry(List<CartItem> cartItems, double totalPayment, String customerName, String customerPhone, Date date, OrderStatus status) {
        this.cartItems = cartItems;
        this.totalPayment = totalPayment;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.date = date;
        this.createdAt = date.getTime();
        this.status = status;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
