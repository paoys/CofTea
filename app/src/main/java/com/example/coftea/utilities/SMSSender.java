package com.example.coftea.utilities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SMSSender {

    public static void sendSMS(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE);

            smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, null);

            Toast.makeText(context, "Customer Notified!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SMS Error", String.valueOf(e));
            Log.e("SMS Error", String.valueOf(e.getMessage()));
            Toast.makeText(context, "Failed to send SMS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
