package com.example.coftea.Cashier.settings.voucher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.coftea.R;
import com.example.coftea.repository.RealtimeDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity {

    private RecyclerView rvVouchers;
    private List<Voucher> voucherList;
    private VouchersAdapter vouchersAdapter;
    private RealtimeDB<Voucher> realtimeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        // Find the Toolbar and set it as the support action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vouchers"); // Set the title in the app bar

        // Initialize RealtimeDB instance
        realtimeDB = new RealtimeDB<>("vouchers");
        DatabaseReference databaseReference = realtimeDB.getDatabaseReference(); // Get DatabaseReference

        // Initialize RecyclerView and its adapter
        rvVouchers = findViewById(R.id.rvVouchers);
        voucherList = new ArrayList<>();
        vouchersAdapter = new VouchersAdapter(voucherList);

        // Set layout manager and adapter to RecyclerView
        rvVouchers.setLayoutManager(new LinearLayoutManager(this));
        rvVouchers.setAdapter(vouchersAdapter);

        // Fetch vouchers from Firebase Realtime Database
        fetchVouchersFromDatabase(databaseReference);

        // Find FloatingActionButton and set OnClickListener
        FloatingActionButton fabAddVoucher = findViewById(R.id.fabAddPromo);
        fabAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch AddVoucherActivity when FloatingActionButton is clicked
                startActivity(new Intent(VoucherActivity.this, AddVoucher.class));
            }
        });
    }

    private void fetchVouchersFromDatabase(DatabaseReference databaseReference) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                voucherList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Voucher voucher = snapshot.getValue(Voucher.class);
                    if (voucher != null) {
                        voucher.setKey(snapshot.getKey());
                        voucherList.add(voucher);
                    }
                }
                vouchersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
