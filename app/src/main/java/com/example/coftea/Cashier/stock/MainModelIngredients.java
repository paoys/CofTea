package com.example.coftea.Cashier.stock;

public class MainModelIngredients {
    String id, name, qty, turl, measurement, category;

    public MainModelIngredients(String id, String name, String qty, String turl, String measurement) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.turl = turl;
        this.measurement = measurement;
        this.category = category;
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

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }
}
