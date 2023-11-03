package com.example.coftea.Cashier.stock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.coftea.R;

public class AddIngredients extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
    }

    public void btn_back(View view) {
        finish(); // This will finish the current activity and return to the previous activity.
    }
}