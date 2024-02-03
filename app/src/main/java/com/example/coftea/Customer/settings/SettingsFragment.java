package com.example.coftea.Customer.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coftea.LogIn;
import com.example.coftea.databinding.FragmentSettingsCustomerBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    private FragmentSettingsCustomerBinding binding;
    private TextView nameTextView;
    private TextView mobileTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsCustomerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nameTextView = binding.name;
        mobileTextView = binding.number;
        Button logoutButton = binding.logoutButton;

        logoutButton.setOnClickListener(v -> logoutUser());

        // Fetching intent data
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String mobileNo = intent.getStringExtra("mobileNo");

            // Setting the received data to TextViews
            nameTextView.setText(name);
            mobileTextView.setText(mobileNo);
        }

        return root;
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
