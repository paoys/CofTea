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

public class PaymongoCheckout extends AsyncTask<Void, Void, String> {

    private final PaymongoCheckoutListener callback;

    public PaymongoCheckout(PaymongoCheckoutListener callback) {
        this.callback = callback;
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"data\":{\"attributes\":{\"billing\":{\"name\":\"Sample User\",\"email\":\"coftea@email.com\",\"phone\":\"09991231234\"},\"send_email_receipt\":false,\"show_description\":true,\"show_line_items\":true,\"line_items\":[{\"currency\":\"PHP\",\"amount\":3000,\"quantity\":1,\"name\":\"Sample\",\"description\":\"Sample\"}],\"payment_method_types\":[\"gcash\"],\"success_url\":\"https://coftea.com\",\"cancel_url\":\"https://coftea.com\",\"description\":\"The Testing Payment\"}}}");
            Request request = new Request.Builder()
                    .url("https://api.paymongo.com/v1/checkout_sessions")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("authorization", "Basic c2tfdGVzdF82bkdpVjhQcmVvNGEzVDdZZ1B2R3VSMlU6")
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
            JSONObject attributesObject = dataObject.getJSONObject("attributes");
            String checkoutURL = attributesObject.getString("checkout_url");
            if (callback != null) {
                callback.onPaymongoCheckoutComplete(checkoutURL);
            }
        } catch (JSONException e) {
            if (callback != null) {
                callback.onPaymongoCheckoutComplete(e.toString());
            }
        }
        // Handle the response here
    }
}
