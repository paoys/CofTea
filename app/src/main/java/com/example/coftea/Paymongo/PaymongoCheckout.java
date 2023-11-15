package com.example.coftea.Paymongo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.Buffer;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymongoCheckout extends AsyncTask<Void, Void, String> {

    private final PaymongoCheckoutListener callback;

    public PaymongoCheckout(PaymongoCheckoutListener callback) {
        this.callback = callback;
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            OkHttpClient client = new OkHttpClient();

            String phoneNumber = "09991231234";
            String name = "Sample User";
            double amount = 20;
            String successUrl = "https://test-deeplinking-test.vercel.app";
            String cancelUrl = "https://test-deeplinking-test.vercel.app";
            String parsedAmount = String.format("%.2f", amount).replace(".", "");
            String jsonBody = "{\"data\":{\"attributes\":{\"billing\":{\"name\":\""+name+"\",\"email\":\"coftea@email.com\",\"phone\":\""+phoneNumber+"\"},\"send_email_receipt\":false,\"show_description\":true,\"show_line_items\":true,\"line_items\":[{\"currency\":\"PHP\",\"amount\":"+parsedAmount+",\"quantity\":1,\"name\":\"Sample\",\"description\":\"Sample\"}],\"payment_method_types\":[\"gcash\"],\"success_url\":\""+successUrl+"\",\"cancel_url\":\""+cancelUrl+"\",\"description\":\"The Testing Payment\"}}}";
            String _jsonBody = "{\"data\":{\"attributes\":{\"billing\":{\"name\":\"Sample User\",\"email\":\"coftea@email.com\",\"phone\":\"09991231234\"},\"send_email_receipt\":false,\"show_description\":true,\"show_line_items\":true,\"line_items\":[{\"currency\":\"PHP\",\"amount\":3000,\"quantity\":1,\"name\":\"Sample\",\"description\":\"Sample\"}],\"payment_method_types\":[\"gcash\"],\"success_url\":\"https://coftea.com\",\"cancel_url\":\"https://coftea.com\",\"description\":\"The Testing Payment\"}}}";

            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody _body = RequestBody.create(mediaType, "{\"data\":{\"attributes\":{\"billing\":{\"name\":\"Sample User\",\"email\":\"coftea@email.com\",\"phone\":\"09991231234\"},\"send_email_receipt\":false,\"show_description\":true,\"show_line_items\":true,\"line_items\":[{\"currency\":\"PHP\",\"amount\":3000,\"quantity\":1,\"name\":\"Sample\",\"description\":\"Sample\"}],\"payment_method_types\":[\"gcash\"],\"success_url\":\"https://coftea.com\",\"cancel_url\":\"https://coftea.com\",\"description\":\"The Testing Payment\"}}}");
            RequestBody _body = RequestBody.create(mediaType, _jsonBody);
            RequestBody body = RequestBody.create(mediaType, jsonBody);

            Log.e("With Var", body.toString());
            Log.e("Static", _body.toString());
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
            Log.e("RAW",result);
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

            PaymongoResponse successRes = new PaymongoResponse(id, checkoutURL, amount, name, phone);
            if (callback != null) {
                callback.onPaymongoCheckoutComplete(successRes);
            }
        } catch (JSONException e) {
            Log.e("RAW2",e.getMessage());
            if (callback != null) {
                PaymongoResponse errorRes = new PaymongoResponse(e.toString());
                callback.onPaymongoCheckoutComplete(errorRes);
            }
        }
        // Handle the response here
    }
}
