package com.example.coftea.Paymongo;

public interface PaymongoPaymentListListener {
    void onPaymongoCheckoutSessionComplete(PaymongoCheckoutResponse result);
}
