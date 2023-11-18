package com.example.coftea.Cashier.queue;

public class QueueOrder {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getReceiptID() {
        return receiptID;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    private String customerName;
    private String customerPhone;
    private String receiptID;
    private double totalPayment;

    public QueueOrder(){}
}
