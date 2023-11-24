package com.example.coftea.Cashier.settings.promos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.coftea.R;
import com.example.coftea.repository.RealtimeDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PromosActivity extends AppCompatActivity {

    private RecyclerView rvPromos;
    private List<Promo> promoList;
    private PromosAdapter promosAdapter;
    private RealtimeDB<Promo> realtimeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promos);

        // Find the Toolbar and set it as the support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Promo"); // Set the title in the app bar

        // Initialize RealtimeDB instance
        realtimeDB = new RealtimeDB<>("promos");
        DatabaseReference databaseReference = realtimeDB.getDatabaseReference(); // Get DatabaseReference

        // Initialize RecyclerView and its adapter
        rvPromos = findViewById(R.id.rvPromos);
        promoList = new ArrayList<>();
        promosAdapter = new PromosAdapter(promoList, databaseReference);

        // Set layout manager and adapter to RecyclerView
        rvPromos.setLayoutManager(new LinearLayoutManager(this));
        rvPromos.setAdapter(promosAdapter);

        // Fetch promos from Firebase Realtime Database
        fetchPromosFromDatabase();

        // Find the FloatingActionButton
        FloatingActionButton fabAddPromo = findViewById(R.id.fabAddPromo);

        // Set OnClickListener for the FloatingActionButton
        fabAddPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch AddPromoActivity when FloatingActionButton is clicked
                startActivity(new Intent(PromosActivity.this, AddPromoActivity.class));
            }
        });
    }

    private void fetchPromosFromDatabase() {
        realtimeDB.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                promoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Promo promo = snapshot.getValue(Promo.class);
                    if (promo != null) {
                        promo.setKey(snapshot.getKey());
                        promoList.add(promo);
                    }
                }
                promosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
