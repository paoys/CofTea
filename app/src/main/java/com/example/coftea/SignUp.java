package com.example.coftea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class SignUp extends AppCompatActivity {

    TextInputLayout name, phoneNo, password;
    Button btn_SignUp, btn_SignIn;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name);
        phoneNo = findViewById(R.id.phoneNo);
        password = findViewById(R.id.password);
        btn_SignUp = findViewById(R.id.btn_signUp);
        btn_SignIn = findViewById(R.id.btn_signIn);

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

        // Set an InputFilter to block spaces in the password field
        TextInputEditText passwordEditText = (TextInputEditText) password.getEditText();
        passwordEditText.setFilters(new InputFilter[] { new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && source.toString().contains(" ")) {
                    return "";
                }
                return null;
            }
        }});

        // Initialize the Firebase reference
        reference = FirebaseDatabase.getInstance().getReference("users");

        // Save data in Firebase on button click
        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get all the values as strings
                String regName = name.getEditText().getText().toString();
                String mobileNo = phoneNo.getEditText().getText().toString();
                String regPassword = password.getEditText().getText().toString();

                if (TextUtils.isEmpty(regName) || TextUtils.isEmpty(mobileNo) || TextUtils.isEmpty(regPassword)) {
                    Toast.makeText(SignUp.this, "Please enter all the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the mobile number is already registered
                    reference.child(mobileNo).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(SignUp.this, "Mobile number already registered.", Toast.LENGTH_SHORT).show();
                            } else {
                                // If the mobile number is not registered, proceed with registration
                                String hashedPassword = hashPassword(regPassword);
                                Intent intent = new Intent(getApplicationContext(), VerifyPhoneNo.class);
                                intent.putExtra("phoneNo", mobileNo);
                                intent.putExtra("regName", regName);
                                intent.putExtra("regPassword", hashedPassword);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error if needed
                        }
                    });
                }
            }
        });

        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String hashPassword(String password) {
        BCrypt.Hasher hasher = BCrypt.withDefaults();
        return hasher.hashToString(12, password.toCharArray());
    }

}
