package com.example.coftea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassword extends AppCompatActivity {

    TextInputLayout phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        phoneNo = findViewById(R.id.phoneNo);

        // Add a TextWatcher to the mobileNo field
        phoneNo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();
                if (!currentText.isEmpty() && currentText.charAt(0) != '9') {
                    // Display a message if the number doesn't start with '9'
                    phoneNo.setError("Mobile Number must start with 9");
                } else {
                    phoneNo.setError(null);  // Clear the error when the condition is met
                    phoneNo.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Call the OTP screen and pass the formatted mobile number for verification
    public void next(View view) {
        // Validate mobile number
        if (!validateFields()) {
            return;
        }

        String userEnteredMobileNo = phoneNo.getEditText().getText().toString().trim();

        // Check whether user exists or not in the database
        Query checkUser = FirebaseDatabase.getInstance().getReference("users").orderByChild("mobileNo").equalTo(userEnteredMobileNo);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneNo.setError(null);
                    phoneNo.setErrorEnabled(false);

                    // Call OTP to verify the formatted phone number
                    Intent intent = new Intent(getApplicationContext(), VerifyPhoneNo.class);
                    intent.putExtra("phoneNo", userEnteredMobileNo);
                    intent.putExtra("whatToDo", "updateData");
                    startActivity(intent);
                    finish();
                } else {
                    phoneNo.setError("No such user exists!");
                    phoneNo.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    // Validate fields and return true if valid
    private boolean validateFields() {
        String val = phoneNo.getEditText().getText().toString();

        if (val.isEmpty()) {
            phoneNo.setError("Field cannot be empty.");
            return false;
        } else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
            return true;
        }
    }

    public void btn_back(View view) {
        finish();
    }
}
