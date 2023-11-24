package com.example.coftea.Cashier.stock;

public class MainModelIngredients {
    private String id;
    private String name;
    private String category;
    private String measurement;
    private String qty;
    private String turl; // Image URL

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;

    public MainModelIngredients() {
        // Default constructor required for Firebase
    }

    public MainModelIngredients(String id, String name, String category, String measurement, String qty, String turl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.measurement = measurement;
        this.qty = qty;
        this.turl = turl;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTurl() {
        return turl;
    }

    public void setTurl(String turl) {
        this.turl = turl;
    }
}
