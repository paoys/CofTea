package com.example.coftea.Cashier.order;


public class CartItem {
    private String id;
    private String name;
    private Double price;

    public Double getTotalPrice() {
        return totalPrice;
    }

    private Double totalPrice;
    private int quantity;
    private String imageUrl;

    public CartItem() {
        // Default constructor required for Firebase
    }

    public CartItem(String id, String name, Double price, int quantity, Double totalPrice) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

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

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getProductName() {
        return 0;
    }
}