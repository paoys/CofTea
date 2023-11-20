package com.example.coftea.Cashier.order;

import com.example.coftea.data.OrderStatus;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QueueEntry {
    private List<CartItem> products;
    private String id;
    private double totalPayment;
    private String customerName;
    private String customerPhone;
    public String getReceiptID() {
        return receiptID;
    }
    private String receiptID;
    public QueueEntry() {
        // Default constructor required for Firebase
    }
    public Date getDate() {
        return date;
    }
    private Date date;
    public Long getCreatedAt() {
        return createdAt;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    private OrderStatus status;
    private Long createdAt;
    public String getOrderID() {
        return orderID;
    }
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Boolean getOnlineOrder() {
        return onlineOrder;
    }

    public void setOnlineOrder(Boolean onlineOrder) {
        this.onlineOrder = onlineOrder;
    }

    private Boolean onlineOrder;
    private String orderID;
    public QueueEntry(double totalPayment, String customerName, String customerPhone, Date date, OrderStatus status) {
        this.products = new ArrayList<>();
        this.totalPayment = totalPayment;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.id = null;
        this.date = date;
        this.createdAt = date.getTime();
        this.status = status;
        this.onlineOrder = false;
    }

    public List<CartItem> getProducts() {
        return products;
    }

    public void setProducts(List<CartItem> products) {
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public void setReceiptID(String receiptID){
        this.receiptID = receiptID;
    }
}
