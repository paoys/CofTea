package com.example.coftea.OrderItemList;

public class OrderItemFirebase {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQuantity() {
        String str_value = quantity.toString().replaceAll(".0","");
        return Integer.parseInt(str_value);
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private String id;
    private String product;
    private Double quantity;
    private Double totalPrice;

    public OrderItemFirebase(){}


}
