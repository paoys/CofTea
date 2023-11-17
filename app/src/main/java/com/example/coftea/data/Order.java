package com.example.coftea.data;

public class Order {
    public String getReferenceID() {
        return referenceID;
    }

    public String getCheckoutSessionID() {
        return checkoutSessionID;
    }

    public String getDatePaid() {
        return datePaid;
    }
    public Order(){}
    public Order(String referenceID, String checkoutSessionID) {
        this.referenceID = referenceID;
        this.checkoutSessionID = checkoutSessionID;
    }
    public Order(String checkoutSessionID) {
        this.checkoutSessionID = checkoutSessionID;
    }
    private String referenceID;
    private String checkoutSessionID;

    public String getCheckoutSessionUrl() {
        return checkoutSessionUrl;
    }

    private String checkoutSessionUrl;

    public void setDatePaid(String date_paid) {
        this.datePaid = date_paid;
    }
    public boolean hasCheckoutSession(){
        return checkoutSessionID != null;
    }
    private String datePaid;
}
