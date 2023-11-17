package com.example.coftea.Paymongo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class PaymongoCheckoutSession extends AsyncTask<String, Void, String> {
    private final PaymongoCheckoutSessionListener callback;

    public PaymongoCheckoutSession(PaymongoCheckoutSessionListener callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... ids) {
        if(ids.length != 1) {
            return "Invalid ID";
        }
        String checkoutID = ids[0];
        OkHttpClient client = new OkHttpClient();

        String apiUrl = "https://api.paymongo.com/v1/checkout_sessions/"+checkoutID;

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("accept", "application/json")
                .header("authorization", "Basic c2tfdGVzdF91R1FVczRYRjRYczdSeFpuUXhpTHVkMm46")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            Log.e("PaymongoCheckoutRaw",result);
            JSONObject responseObject = new JSONObject(result);
            JSONObject dataObject = responseObject.getJSONObject("data");

            JSONObject attributesObject = dataObject.getJSONObject("attributes");

            JSONArray payments = attributesObject.getJSONArray("payments");
            if (payments.length() != 0){
                JSONObject paymentObject = payments.getJSONObject(0);
                String paymentId = paymentObject.getString("id");
                JSONObject paymentAttributes = paymentObject.getJSONObject("attributes");
                double _amount = paymentAttributes.getDouble("amount");
                double _fee = paymentAttributes.getDouble("fee");
                double _net_amount = paymentAttributes.getDouble("net_amount");
                double amount = addDecimalPoint(_amount);
                double fee = addDecimalPoint(_fee);
                double net_amount = addDecimalPoint(_net_amount);
                PaymongoCheckoutResponse successRes = new PaymongoCheckoutResponse(paymentId, amount, fee, net_amount);
                Log.e("ResponsePaymentID", successRes.getPaymentID());
                if (callback != null) {
                    callback.onPaymongoCheckoutSessionComplete(successRes);
                }
                return;
            }
            PaymongoCheckoutResponse successRes = new PaymongoCheckoutResponse("Please proceed to payment");
            if (callback != null) {
                callback.onPaymongoCheckoutSessionComplete(successRes);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("CheckoutSessionError",e.getMessage());
            PaymongoCheckoutResponse successRes = new PaymongoCheckoutResponse(e.toString());
            if (callback != null) {
                callback.onPaymongoCheckoutSessionComplete(successRes);
            }
        }
    }

    private double addDecimalPoint(double amount) {
        String amountString = String.valueOf(amount).replace(".0","");
        StringBuilder formattedAmount = new StringBuilder(amountString);
        formattedAmount.insert(amountString.length() - 2, '.');
        return Double.parseDouble(formattedAmount.toString());
    }
}
