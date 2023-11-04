package com.example.coftea.Cashier.order;

import java.util.Date;
import java.util.List;

public class ReceiptEntry {
    private List<Cart> products;
    private double totalPayment;
    private String customerName;
    private String customerPhone;
    private Date date;


    public ReceiptEntry(List<Cart> products, double totalPayment, String customerName, String customerPhone, Date date) {
        this.products = products;
        this.totalPayment = totalPayment;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.date = date;
    }

    public List<Cart> getProducts() {
        return products;
    }

    public void setProducts(List<Cart> products) {
        this.products = products;
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
