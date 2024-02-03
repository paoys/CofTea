package com.example.coftea.Cashier.settings.voucher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coftea.R;
import com.example.coftea.repository.RealtimeDB;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddVoucher extends AppCompatActivity {

    private EditText discountEditText, customCodeEditText, expirationEditText;
    private TextView voucherCodeTextView;
    private RealtimeDB<Map<String, Object>> realtimeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voucher);

        discountEditText = findViewById(R.id.discountEditText);
        customCodeEditText = findViewById(R.id.customCodeEditText);
        expirationEditText = findViewById(R.id.expirationEditText);
        voucherCodeTextView = findViewById(R.id.voucherCodeTextView);
        Button generateVoucherButton = findViewById(R.id.generateVoucherButton);

        realtimeDB = new RealtimeDB<>("vouchers");

        generateVoucherButton.setOnClickListener(v -> generateAndStoreVoucher());
    }

    private void generateAndStoreVoucher() {
        String discountStr = discountEditText.getText().toString();
        String customFormat = customCodeEditText.getText().toString();
        String expirationStr = expirationEditText.getText().toString();

        if (TextUtils.isEmpty(discountStr) || TextUtils.isEmpty(customFormat) || TextUtils.isEmpty(expirationStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double discount = 0.0;
        int expirationDays = 7; // Default expiration duration

        try {
            discount = Double.parseDouble(discountStr);
            expirationDays = Integer.parseInt(expirationStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        String voucherCode = generateCustomCode(customFormat);
        String discountText = "Discount: " + discount + "%";
        String result = voucherCode + "\n" + discountText;
        voucherCodeTextView.setText(result);

        // Calculate expiration timestamp based on user-input duration
        long expirationTimestamp = System.currentTimeMillis() + (expirationDays * 24 * 60 * 60 * 1000); // Expiration in milliseconds

        // Store voucher data in Firebase Realtime Database
        Map<String, Object> voucherData = new HashMap<>();
        voucherData.put("code", voucherCode);
        voucherData.put("discount", discount);
        voucherData.put("expirationTimestamp", expirationTimestamp); // Add customizable expiration timestamp
        voucherData.put("devicesRedeemed", new HashMap<String, Boolean>());

        realtimeDB.addObject(voucherData);

        Toast.makeText(this, "Voucher code generated successfully", Toast.LENGTH_SHORT).show();

        // Clear EditText fields after successful generation
        discountEditText.getText().clear();
        customCodeEditText.getText().clear();
        expirationEditText.getText().clear();
    }

    private String generateCustomCode(String customFormat) {
        StringBuilder voucherCode = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < customFormat.length(); i++) {
            char currentChar = customFormat.charAt(i);

            if (currentChar == '#') {
                int index = random.nextInt(36);
                char randomChar = (index < 10) ? (char) ('0' + index) : (char) ('A' + index - 10);
                voucherCode.append(randomChar);
            } else {
                voucherCode.append(currentChar);
            }
        }

        return voucherCode.toString();
    }
}
