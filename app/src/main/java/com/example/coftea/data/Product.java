package com.example.coftea.data;

public class Product {
    private String id;
    private String name;
    private Double price;
    private String imageUrl;

    public String getIngredientPath() {
        return ingredientPath;
    }

    public void setIngredientPath(String ingredientPath) {
        this.ingredientPath = ingredientPath;
    }

    private String ingredientPath;
    public Product() {
        // Default constructor required for Firebase
    }

    public Product(String id, String name, Double price, String imageUrl, String ingredientPath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.ingredientPath = ingredientPath;
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
    public String getImageUrl() {
        return imageUrl;
    }


}
