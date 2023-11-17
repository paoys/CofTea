package com.example.coftea.Paymongo;

public class PaymongoCheckoutResponse {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PaymongoCheckoutResponse(String id, String checkoutUrl, Double amount, String name, String phone) {
        this.id = id;
        this.checkoutUrl = checkoutUrl;
        this.amount = amount;
        this.name = name;
        this.phone = phone;
    }

    public PaymongoCheckoutResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String id;
    private String checkoutUrl;
    private Double amount;
    private String name;
    private String phone;
    private String errorMessage;

    public Double getFee() {
        return fee;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public boolean isCheckoutSessionPaid(){
        return (fee != null && netAmount != null && paymentID != null);
    }
    private Double fee;
    private Double netAmount;
    private String paymentID;

    public PaymongoCheckoutResponse(String paymentID, Double amount, Double fee, Double netAmount) {
        this.paymentID = paymentID;
        this.amount = amount;
        this.fee = fee;
        this.netAmount = netAmount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
