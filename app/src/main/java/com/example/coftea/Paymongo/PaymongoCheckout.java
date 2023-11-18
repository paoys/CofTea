package com.example.coftea.Paymongo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymongoCheckout extends AsyncTask<PaymongoPayload, Void, String> {

    private final PaymongoCheckoutListener callback;

    public PaymongoCheckout(PaymongoCheckoutListener callback) {
        this.callback = callback;
    }
    @Override
    protected String doInBackground(PaymongoPayload... payloads) {
        try {
            if(payloads.length != 1) {
                return "Invalid Payload";
            }

            PaymongoPayload payload = payloads[0];
            OkHttpClient client = new OkHttpClient();

            String phoneNumber = payload.getMobileNumber();
            String name = payload.getUserName();
            double amount = payload.getAmount();
            String successUrl = "https://coftea-web.vercel.app";
            String cancelUrl = "https://coftea-web.vercel.app";
            String referenceNumber = payload.getMobileNumber();
            String parsedAmount = String.format("%.2f", amount).replace(".", "");
            String jsonBody = "{\"data\":{\"attributes\":{\"billing\":{\"name\":\""+name+"\",\"email\":\"coftea@email.com\",\"phone\":\""+phoneNumber+"\"},\"send_email_receipt\":false,\"show_description\":true,\"show_line_items\":true,\"line_items\":[{\"currency\":\"PHP\",\"amount\":"+parsedAmount+",\"quantity\":1,\"name\":\"Sample\",\"description\":\"Sample\"}],\"payment_method_types\":[\"gcash\"],\"success_url\":\""+successUrl+"\",\"reference_number\":\""+referenceNumber+"\",\"cancel_url\":\""+cancelUrl+"\",\"description\":\"The Testing Payment\"}}}";

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonBody);

            Request request = new Request.Builder()
                    .url("https://api.paymongo.com/v1/checkout_sessions")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("authorization", "Basic c2tfdGVzdF91R1FVczRYRjRYczdSeFpuUXhpTHVkMm46")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return "Response not successful or body is null";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CATCH", e.getMessage());
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            Log.e("PaymongoCheckoutRaw",result);
            JSONObject responseObject = new JSONObject(result);
            JSONObject dataObject = responseObject.getJSONObject("data");

            String id = dataObject.getString("id");
            JSONObject attributesObject = dataObject.getJSONObject("attributes");

            JSONObject billingObject = attributesObject.getJSONObject("billing");
            String name = billingObject.getString("name");
            String phone = billingObject.getString("phone");
            String checkoutURL = attributesObject.getString("checkout_url");
            JSONObject paymentIntent = attributesObject.getJSONObject("payment_intent");
            JSONObject paymentIntentAttributes = paymentIntent.getJSONObject("attributes");
            Double amount = paymentIntentAttributes.getDouble("amount");

            PaymongoCheckoutResponse successRes = new PaymongoCheckoutResponse(id, checkoutURL, amount, name, phone);
            if (callback != null) {
                callback.onPaymongoCheckoutComplete(successRes);
            }
        } catch (JSONException e) {
            if (callback != null) {
                PaymongoCheckoutResponse errorRes = new PaymongoCheckoutResponse(e.toString());
                callback.onPaymongoCheckoutComplete(errorRes);
            }
        }
    }
}
