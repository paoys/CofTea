package com.example.coftea.Cashier.order;

import java.util.ArrayList;
import java.util.List;

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

    public QueueEntry(double totalPayment, String customerName, String customerPhone) {
        this.products = new ArrayList<>();
        this.totalPayment = totalPayment;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.id = null;
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
