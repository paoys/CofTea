package com.example.coftea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import com.example.coftea.Database.CheckInternet;
import com.example.coftea.Database.CustomDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class SetNewPassword extends AppCompatActivity {

    TextInputLayout newPassword, rePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        newPassword = findViewById(R.id.newPassword);
        rePassword = findViewById(R.id.rePassword);

        // Set an InputFilter to block spaces in the password field
        TextInputEditText newPasswordEditText = (TextInputEditText) newPassword.getEditText();
        TextInputEditText rePasswordEditText = (TextInputEditText) rePassword.getEditText();

        InputFilter noSpaceFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && source.toString().contains(" ")) {
                    return "";
                }
                return null;
            }
        };

        newPasswordEditText.setFilters(new InputFilter[] { noSpaceFilter });
        rePasswordEditText.setFilters(new InputFilter[] { noSpaceFilter });


    }

    public void btn_setNewPass(View view) {

        //Check internet connection
        CheckInternet internetChecker = new CheckInternet(this);
        if (!internetChecker.isNetworkAvailable()) {
            CustomDialog.showNoInternetDialog(this);
        }

        //Validate password
        if (!validatePassword() | !validateConfirmPassword()) {
            return;
        }

        //Get data from fields
        String newPass = newPassword.getEditText().getText().toString().trim();
        String mobileNo = getIntent().getStringExtra("phoneNo");

        String hashedPassword = hashPassword(newPass);

        //Update data in firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(mobileNo).child("hashedPassword").setValue(hashedPassword);

        startActivity(new Intent(getApplicationContext(), ForgotPasswordSuccessMessage.class));
        finish();
    }

    private boolean validatePassword() {
        String val = newPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            newPassword.setError("Field cannot be empty.");
            return false;
        } else {
            newPassword.setError(null);
            newPassword.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String val = rePassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            rePassword.setError("Field cannot be empty.");
            return false;
        } else {
            rePassword.setError(null);
            rePassword.setErrorEnabled(false);
            return true;
        }
    }

    private String hashPassword(String password) {
        BCrypt.Hasher hasher = BCrypt.withDefaults();
        return hasher.hashToString(12, password.toCharArray());
    }

}