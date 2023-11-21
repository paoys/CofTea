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
    private Double netAmount;
    private Double fee;

    public boolean isCheckoutSessionPaid(){
        return (netAmount != null && fee != null && amount != null);
    }
    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getOrderPlaced() {
        return orderPlaced;
    }

    private Boolean orderPlaced;
    private Double amount;
}
