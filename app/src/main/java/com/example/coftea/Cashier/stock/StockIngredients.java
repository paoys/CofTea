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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class StockIngredients extends AppCompatActivity {

    private boolean isSearchVisible = false;
    private EditText editTextSearch;
    private ImageView searchIcon;
    RecyclerView recyclerView;
    Spinner spinnerCategory;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_ingredients);

        // Initialize your UI elements
        editTextSearch = findViewById(R.id.editTextSearch);
        searchIcon = findViewById(R.id.searchIcon);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinnerCategory = findViewById(R.id.category);

        // Create an ArrayAdapter for the category spinner
        String[] categoryOptions = {"All", "Materials", "Ingredients"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryOptions);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set an onClickListener for the search icon
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSearch();
            }
        });

        /*FirebaseRecyclerOptions<MainModelIngredients> options = new FirebaseRecyclerOptions.Builder<MainModelIngredients>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("ingredients"), MainModelIngredients.class)
                .build();*/

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddIngredients.class));
            }
        });

        /*spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/

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



    public void btn_back(View view) {
        finish(); // This will finish the current activity and return to the previous activity.
    }
}
