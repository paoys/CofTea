package com.example.coftea.Customer.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coftea.LogIn;
import com.example.coftea.databinding.FragmentSettingsCustomerBinding;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {

    private FragmentSettingsCustomerBinding binding;
    private TextView nameTextView;
    private TextView mobileTextView;
    private RealtimeDB<String> realtimeDB;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        realtimeDB = new RealtimeDB<>("users");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsCustomerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nameTextView = binding.textSettingsName;
        mobileTextView = binding.textSettingsMobile;

        Button logoutButton = binding.logoutButton;

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userMobileNo = currentUser.getPhoneNumber(); // Retrieve the user's mobile number from Firebase Authentication

            // Fetch the user's account information from RealtimeDB based on mobile number
            realtimeDB.getDatabaseReference().orderByChild("mobileNo").equalTo(userMobileNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String mobileNo = snapshot.child("mobileNo").getValue(String.class);

                        Log.d("SettingsFragment", "Name: " + name);
                        Log.d("SettingsFragment", "Mobile: " + mobileNo);

                        if (name != null && !name.isEmpty()) {
                            nameTextView.setText(name);
                        } else {
                            nameTextView.setText("Name not available");
                        }

                        if (mobileNo != null && !mobileNo.isEmpty()) {
                            mobileTextView.setText(mobileNo);
                        } else {
                            mobileTextView.setText("Mobile number not available");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that may occur
                }
            });
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        return root;
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
