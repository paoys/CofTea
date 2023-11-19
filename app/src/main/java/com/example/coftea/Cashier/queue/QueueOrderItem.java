package com.example.coftea.Cashier.queue;

import java.io.Serializable;

public class QueueOrderItem implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public QueueOrderItem() {}
    public QueueOrderItem(String id, String name, Double price, int quantity, Double totalPrice) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    private String id;
    private String name;
    private Double price;
    private int quantity;
    private Double totalPrice;
}
