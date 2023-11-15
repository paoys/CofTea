package com.example.coftea.Paymongo;

public class PaymongoResponse {
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

    public PaymongoResponse(String id, String checkoutUrl, Double amount, String name, String phone) {
        this.id = id;
        this.checkoutUrl = checkoutUrl;
        this.amount = amount;
        this.name = name;
        this.phone = phone;
    }

    public PaymongoResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String id;
    private String checkoutUrl;
    private Double amount;
    private String name;
    private String phone;
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

}
