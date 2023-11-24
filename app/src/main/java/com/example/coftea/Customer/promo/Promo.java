package com.example.coftea.Customer.promo;

public class Promo {
    private String announcement;
    private String imageUrl;

    // Required default constructor (for Firebase serialization)
    public Promo() {
        // Default constructor required by Firebase
    }

    public Promo(String announcement, String imageUrl) {
        this.announcement = announcement;
        this.imageUrl = imageUrl;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
