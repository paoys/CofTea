package com.example.coftea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    Button callSignUp, btn_signIn;
    TextInputLayout mobileNo, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        callSignUp = findViewById(R.id.btn_signUp);
        btn_signIn = findViewById(R.id.btn_signIn);
        mobileNo = findViewById(R.id.mobileNo);
        password = findViewById(R.id.password);

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
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
            mobileNo.setError("Field cannot be empty");
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
            password.setError("Field cannot be empty");
            return false;
        } else {
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
        final String userEnteredMobileNo = mobileNo.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://coftea-c90ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");

        Query checkUser = reference.orderByChild("mobileNo").equalTo(userEnteredMobileNo);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) { // Changed 'if (dataSnapshot.exists())' to 'if (snapshot.exists())'
                    mobileNo.setError(null);
                    mobileNo.setErrorEnabled(false);

                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next(); // Added this line to get the first matching user

                    String passwordFromDB = dataSnapshot.child("password").getValue(String.class);

                    if (passwordFromDB.equals(userEnteredPassword)) {
                        mobileNo.setError(null);
                        mobileNo.setErrorEnabled(false);

                        String nameFromDB = dataSnapshot.child("name").getValue(String.class);
                        String mobileNoFromDB = dataSnapshot.child("mobileNo").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);

                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("mobileNo", mobileNoFromDB);

                        startActivity(intent);
                    } else {
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                } else {
                    mobileNo.setError("You are not registered");
                    password.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
