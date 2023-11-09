package com.example.coftea.data;

public class Product {
    private String id;
    private String name;
    private String price;
    private String imageUrl;
    public Product() {
        // Default constructor required for Firebase
    }

    public Product(String id, String name, String price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
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
        return Double.parseDouble(price);
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getImageUrl() {
        return imageUrl;
    }


}
