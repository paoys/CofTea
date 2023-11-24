package com.example.coftea.Customer.promo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PromoFragment extends Fragment {

    private RecyclerView rvPromos;
    private List<Promo> promoList;
    private CustomerPromoAdapter promosAdapter;
    private RealtimeDB<Promo> realtimeDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_promo, container, false);

        // Add the line below to remove the back button from the ActionBar
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Initialize RealtimeDB instance
        realtimeDB = new RealtimeDB<>("promos");
        DatabaseReference databaseReference = realtimeDB.getDatabaseReference();

        // Initialize RecyclerView and its adapter
        rvPromos = rootView.findViewById(R.id.rvPromos);
        promoList = new ArrayList<>();
        promosAdapter = new CustomerPromoAdapter(promoList);

        // Set layout manager and adapter to RecyclerView
        rvPromos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPromos.setAdapter(promosAdapter);

        // Fetch promos from Firebase Realtime Database
        fetchPromosFromDatabase();

        return rootView;
    }

    private void fetchPromosFromDatabase() {
        realtimeDB.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                promoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Promo promo = snapshot.getValue(Promo.class);
                    if (promo != null) {
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
