package com.example.coftea.data;

public class OrderItem {
    private String id;
    private Product product;
    private Integer quantity;
    private Double totalPrice;

    public OrderItem(String id, Product product, Integer quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
    }

    public String getId() {
        return id;
    }
    public Product getProduct() { return product; }
    public Integer getQuantity() { return quantity; }
    public void addQuantity() {
        this.quantity = this.quantity+1;
        this.totalPrice = product.getPrice() * this.quantity;
    }
    public boolean minusQuantity() {
        this.quantity = this.quantity-1;
        if(this.quantity <= 0) {
            this.quantity = 1;
            return false;
        }
        this.totalPrice = product.getPrice() * this.quantity;
        return true;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
    }
    public Double getTotalPrice() { return totalPrice; }


}

