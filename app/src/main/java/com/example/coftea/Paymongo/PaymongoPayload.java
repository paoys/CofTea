package com.example.coftea.Paymongo;

public class PaymongoPayload {

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymongoPayload(String reference_id, String userName, String mobileNumber, Double amount) {
        this.reference_id = reference_id;
        this.userName = userName;
        this.mobileNumber = mobileNumber;
        this.amount = amount;
    }

    private String reference_id;
    private String userName;
    private String mobileNumber;
    private Double amount;
}
