package com.example.coftea.Cashier.stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageProductActivityList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ManageProductAdapter productAdapters;
    private List<ModelManageProduct> productList;
    private DatabaseReference productsRef;
    private FloatingActionButton floatingActionButton;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_ingredients);

        // Initialize RecyclerView and SearchView
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();

        // Initialize the productList
        productList = new ArrayList<>();
        productAdapters = new ManageProductAdapter(productList);
        recyclerView.setAdapter(productAdapters);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");

        // Set up SearchView listener for filtering products
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Create a query to search products by name
                Query query;
                if (newText.isEmpty()) {
                    query = FirebaseDatabase.getInstance().getReference().child("products");
                } else {
                    query = FirebaseDatabase.getInstance().getReference().child("products")
                            .orderByChild("name").startAt(newText).endAt(newText + "\uf8ff");
                }

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelManageProduct product = snapshot.getValue(ModelManageProduct.class);
                            productList.add(product);
                        }
                        productAdapters.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle potential errors here
                    }
                });

                return true;
            }
        });

        // Retrieve and display products
        retrieveProducts();

        // Set up FloatingActionButton to add new products
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddProductActivity.class));
            }
        });
    }

    // Method to retrieve products from the database
    private void retrieveProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelManageProduct product = snapshot.getValue(ModelManageProduct.class);
                    productList.add(product);
                }
                productAdapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }
}
