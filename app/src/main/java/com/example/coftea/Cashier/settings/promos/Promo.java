package com.example.coftea.Cashier.settings.promos;

public class Promo {
    private String key; // Unique key for Firebase
    private String announcement;
    private String imageUrl;

    public Promo() {
        // Default constructor required for calls to DataSnapshot.getValue(Promo.class)
    }

    public Promo(String key, String announcement, String imageUrl) {
        this.key = key;
        this.announcement = announcement;
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Add setter method for key if needed
    public void setKey(String key) {
        this.key = key;
    }
}
