package com.example.coftea.Cashier.settings.voucher;

import java.util.Map;

public class Voucher {
    private String key;
    private String code;
    private double discount;
    private long expirationTimestamp;
    private Map<String, Boolean> devicesRedeemed;

    // Constructors, getters, and setters

    public Voucher() {
        // Default constructor required for Firebase
    }

    public Voucher(String code, double discount, long expirationTimestamp, Map<String, Boolean> devicesRedeemed) {
        this.code = code;
        this.discount = discount;
        this.expirationTimestamp = expirationTimestamp;
        this.devicesRedeemed = devicesRedeemed;
    }

    // Getters and setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    public Map<String, Boolean> getDevicesRedeemed() {
        return devicesRedeemed;
    }

    public void setDevicesRedeemed(Map<String, Boolean> devicesRedeemed) {
        this.devicesRedeemed = devicesRedeemed;
    }
}
