package com.example.coftea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    TextInputLayout name, mobileNo, password;
    Button btn_SignUp, btn_SignIn;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.mobileNo);
        password = findViewById(R.id.password);
        btn_SignUp = findViewById(R.id.btn_signUp);
        btn_SignIn = findViewById(R.id.btn_signIn);

        //Save data in Firebase on button click
        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rootNode = FirebaseDatabase.getInstance("https://coftea-c90ae-default-rtdb.asia-southeast1.firebasedatabase.app");
                reference = rootNode.getReference("users");

                if (name.equals("") || mobileNo.equals("") || password.equals("")) {
                    Toast.makeText(SignUp.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    //Get all the values
                    String regName = name.getEditText().getText().toString();
                    String regMobileNo = mobileNo.getEditText().getText().toString();
                    String regPassword = password.getEditText().getText().toString();

                    UserHelperClass helperClass = new UserHelperClass(regName, regMobileNo, regPassword);

                    reference.child(regMobileNo).setValue(helperClass);
                    Toast.makeText(SignUp.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });
    }
}