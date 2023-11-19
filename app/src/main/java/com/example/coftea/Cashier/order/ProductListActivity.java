package com.example.coftea.Cashier.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.example.coftea.databinding.ActivityManageProductListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderProductAdapter productAdapters;
    private List<ModelOrderProduct> productList;
    private DatabaseReference productsRef;
    Button btnViewCart;

    private ActivityManageProductListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageProductListBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        recyclerView = binding.rvCashierProductList;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        productAdapters = new OrderProductAdapter(productList);
        recyclerView.setAdapter(productAdapters);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");

        // Retrieve and display products
        retrieveProducts();

        btnViewCart = binding.btnViewCart;
        btnViewCart.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ManageCartActivitiyList.class)));

    }

    private void retrieveProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelOrderProduct product = snapshot.getValue(ModelOrderProduct.class);
                    productList.add(product);
                }
                productAdapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
