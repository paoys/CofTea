package com.example.coftea.Paymongo;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaymongoPaymentList extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... voids) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.paymongo.com/v1/payments?limit=50")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("authorization", "Basic c2tfdGVzdF91R1FVczRYRjRYczdSeFpuUXhpTHVkMm46")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return "Response not successful or body is null";
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("CATCH", e.getMessage());
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
