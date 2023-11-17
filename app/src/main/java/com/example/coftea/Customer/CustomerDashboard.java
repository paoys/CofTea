package com.example.coftea.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.coftea.R;
import com.example.coftea.databinding.ActivityCustomerDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerDashboard extends AppCompatActivity {

    private ActivityCustomerDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view using the data binding layout
        binding = ActivityCustomerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the ActionBar for this activity
        setSupportActionBar(binding.toolbar);

        // Initialize BottomNavigationView
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Set up top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_products_customer, R.id.navigation_advance_order_customer, R.id.navigation_checkout_customer, R.id.navigation_settings_customer)
                .build();

        // Initialize NavController using the NavHostFragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_customer_activity);

        // Set up ActionBar with NavController and AppBarConfiguration
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(binding.navView, navController);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
//            String param1 = deepLink.getQueryParameter("param1");
//            String param2 = deepLink.getQueryParameter("param2");
        }
    }

}
