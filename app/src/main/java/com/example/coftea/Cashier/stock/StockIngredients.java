package com.example.coftea.Cashier.stock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.coftea.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class StockIngredients extends AppCompatActivity {

    RecyclerView recyclerView;
    MainAdapterIngredientsStock mainAdapter;
    FloatingActionButton floatingActionButton;
    Spinner spinnerCategory;

    private boolean isSearchVisible = false;
    private EditText editTextSearch;
    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_ingredients);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spinnerCategory = findViewById(R.id.category);

        // Create an ArrayAdapter for the category spinner
        String[] categoryOptions = {"All", "Material", "Ingredients"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryOptions);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        FirebaseRecyclerOptions<MainModelIngredients> options = new FirebaseRecyclerOptions.Builder<MainModelIngredients>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Ingredients"), MainModelIngredients.class)
                .build();

        mainAdapter = new MainAdapterIngredientsStock(options);
        recyclerView.setAdapter(mainAdapter);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddIngredients.class));
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected category
                String selectedCategory = spinnerCategory.getSelectedItem().toString();
                filterItemsByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nothing selected if needed
            }
        });
    }

    private void filterItemsByCategory(String selectedCategory) {
        Query query;
        if ("All".equals(selectedCategory)) {
            query = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        } else {
            query = FirebaseDatabase.getInstance().getReference().child("Ingredients").orderByChild("category").equalTo(selectedCategory);
        }

        FirebaseRecyclerOptions<MainModelIngredients> filteredOptions = new FirebaseRecyclerOptions.Builder<MainModelIngredients>()
                .setQuery(query, MainModelIngredients.class)
                .build();

        mainAdapter.updateOptions(filteredOptions);
    }



    public void toggleSearch() {
        // Toggle the visibility of the search EditText and hide/show related views
        isSearchVisible = !isSearchVisible;
        editTextSearch.setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);
        searchIcon.setVisibility(isSearchVisible ? View.GONE : View.VISIBLE);
        findViewById(R.id.category).setVisibility(isSearchVisible ? View.GONE : View.VISIBLE);

        // Show the "X" button when the search is visible
        findViewById(R.id.exitSearch).setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);

        // Handle any other UI changes or actions when search is toggled
    }

    public void exitSearch(View view) {
        // Exit the search toggle
        isSearchVisible = false;
        editTextSearch.setVisibility(View.GONE);

        // Show the search icon and category spinner
        searchIcon.setVisibility(View.VISIBLE);
        findViewById(R.id.category).setVisibility(View.VISIBLE);

        // Hide the "X" button
        findViewById(R.id.exitSearch).setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }
}
