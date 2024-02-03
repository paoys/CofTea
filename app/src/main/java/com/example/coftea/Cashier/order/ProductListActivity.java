package com.example.coftea.Cashier.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.databinding.ActivityManageProductListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderProductAdapter productAdapter;
    private List<ModelOrderProduct> productList;
    private DatabaseReference productsRef;
    private SearchView searchView;
    private Button btnViewCart;

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
        productAdapter = new OrderProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");

        // Initialize Views
        searchView = binding.serchView;
        btnViewCart = binding.btnViewCart;

        // SearchView Functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return true;
            }
        });

        // Button click listener to view the cart
        btnViewCart.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ManageCartActivityList.class)));

        // Retrieve and display products initially
        retrieveProducts();
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
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }

    private void searchProducts(String searchText) {
        Query query;
        if (searchText.isEmpty()) {
            query = FirebaseDatabase.getInstance().getReference().child("products");
        } else {
            query = FirebaseDatabase.getInstance().getReference().child("products")
                    .orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelOrderProduct product = snapshot.getValue(ModelOrderProduct.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }
}
