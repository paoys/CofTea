package com.example.coftea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import com.example.coftea.Cashier.CashierMain;
import com.example.coftea.Customer.CustomerDashboard;
import com.example.coftea.utilities.UserProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class LogIn extends AppCompatActivity {

    Button callSignUp, btn_signIn;
    TextInputLayout mobileNo, password;

    UserProvider userProvider = UserProvider.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        callSignUp = findViewById(R.id.btn_signUp);
        btn_signIn = findViewById(R.id.btn_signIn);
        mobileNo = findViewById(R.id.mobileNo);
        password = findViewById(R.id.password);

        // Add a TextWatcher to the mobileNo field
        mobileNo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();
                if (currentText.isEmpty() || (currentText.length() > 0 && currentText.charAt(0) != '9')) {
                    mobileNo.setError("Mobile Number must start with 9");
                } else {
                    mobileNo.setError(null);
                    mobileNo.setErrorEnabled(false);
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

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(view);
            }
        });
    }

    private Boolean validateMobileNo() {
        String val = mobileNo.getEditText().getText().toString();

        if (val.isEmpty()) {
            mobileNo.setError("Field cannot be empty.");
            return false;
        } else {
            mobileNo.setError(null);
            mobileNo.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();

        if(val.isEmpty()) {
            password.setError("Field cannot be empty.");
            return false;
        } else {
            // Check if the password contains spaces
            if (val.contains(" ")) {
                password.setError("Password cannot contain spaces.");
                return false;
            }

            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        if (!validateMobileNo() || !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        FirebaseApp.initializeApp(getApplicationContext());
        final String userEnteredMobileNo = mobileNo.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("mobileNo").equalTo(userEnteredMobileNo);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mobileNo.setError(null);
                    mobileNo.setErrorEnabled(false);

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String hashedPasswordFromDB = dataSnapshot.child("hashedPassword").getValue(String.class);

                        if (hashedPasswordFromDB != null && BCrypt.verifyer().verify(userEnteredPassword.toCharArray(), hashedPasswordFromDB).verified) {
                            // Passwords match, proceed with login
                            mobileNo.setError(null);
                            mobileNo.setErrorEnabled(false);

                            // Check if the user is the cashier
                            if (userEnteredMobileNo.equals("9475774920")) {
                                // User is the cashier, pass the specific mobile number to CashierOrders activity
                                String nameFromDB = dataSnapshot.child("name").getValue(String.class);
                                Intent intent = new Intent(getApplicationContext(), CashierMain.class);
                                intent.putExtra("name", nameFromDB);
                                intent.putExtra("mobileNo", "9475774920"); // Pass the specific mobile number for the cashier
                                userProvider.setUser("Cashier","9475774920");
                                startActivity(intent);
                            } else {
                                // User is a customer, pass the customer's mobile number
                                String nameFromDB = dataSnapshot.child("name").getValue(String.class);
                                String mobileNoFromDB = dataSnapshot.child("mobileNo").getValue(String.class);
                                Intent intent = new Intent(getApplicationContext(), CustomerDashboard.class);
                                intent.putExtra("name", nameFromDB); // Pass the customer's name
                                intent.putExtra("mobileNo", mobileNoFromDB); // Pass the customer's mobile number
                                userProvider.setUser(nameFromDB,mobileNoFromDB);
                                startActivity(intent);
                            }

                            return;
                        }
                    }

                    // If the password does not match any user in the loop
                    password.setError("Wrong Password.");
                    password.requestFocus();
                } else {
                    mobileNo.setError("You are not registered.");
                    password.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    public void btn_forgotPassword(View view) {
        startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
    }
}
