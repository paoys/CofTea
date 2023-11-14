package com.example.coftea.Customer.checkout;

import android.os.Bundle;

import com.example.coftea.Paymongo.PaymongoCheckout;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;

import com.example.coftea.databinding.CheckoutBinding;

public class Checkout extends AppCompatActivity {

    private CheckoutBinding binding;

    TextView tvTotalAmountDue;
    Button btnPayWithGCash;

    PaymongoCheckout paymongoCheckout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }



    private void init(){
        tvTotalAmountDue = binding.tvTotalAmountDue;
        btnPayWithGCash = binding.btnPayWithGCash;


        btnPayWithGCash.setOnClickListener(view -> {
        });
    }
}