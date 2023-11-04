package com.example.coftea.Cashier.order;

import java.util.ArrayList;
import java.util.List;

public class QueueEntry {
    private List<Cart> products;
    private String id;
    private double totalPayment;
    private String customerName;
    private String customerPhone;

    public QueueEntry() {
        // Default constructor required for Firebase
    }

    public QueueEntry(double totalPayment, String customerName, String customerPhone) {
        this.products = new ArrayList<>(); // Initialize the products list
        this.totalPayment = totalPayment;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.id = null;
    }

    public List<Cart> getProducts() {
        return products;
    }

    public void setProducts(List<Cart> products) {
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
}
