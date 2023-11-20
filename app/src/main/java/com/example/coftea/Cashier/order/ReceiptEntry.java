package com.example.coftea.Cashier.order;

import com.example.coftea.data.OrderStatus;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReceiptEntry implements Serializable {
    private List<CartItem> cartItems;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private double totalPayment;
    private String customerName;
    private String customerPhone;
    private Date date;

    public Long getCreatedAt() {
        return createdAt;
    }

    private Long createdAt;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    private OrderStatus status;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    private String paymentId;

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
