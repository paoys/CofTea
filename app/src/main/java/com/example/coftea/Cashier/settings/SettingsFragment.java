package com.example.coftea.Cashier.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coftea.Cashier.settings.inventory_report.InventoryReport;
import com.example.coftea.Cashier.settings.voucher.VoucherActivity;
import com.example.coftea.LogIn;
import com.example.coftea.databinding.FragmentSettingsBinding;
import com.example.coftea.Cashier.settings.promos.PromosActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    Button btnLogout, btnPromo, btnInventoryReport, btnVoucher;
    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnLogout = binding.btnLogout;
        btnPromo = binding.btnPromos;
        btnInventoryReport = binding.btnInventoryReport;
        btnVoucher = binding.btnVoucher;

        btnPromo.setOnClickListener(view -> navigateToPromoActivity());

        btnInventoryReport.setOnClickListener(view -> navigateToInventoryReport());

        btnVoucher.setOnClickListener(view -> navigateToVoucher());

        btnLogout.setOnClickListener(v -> logoutUser());


        // Fetching intent data
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String mobileNo = intent.getStringExtra("mobileNo");

            // Setting received name and mobile number to ViewModel
            settingsViewModel.setName(name);
            settingsViewModel.setNumber(mobileNo);

            // Setting the received data to TextViews
            TextView nameTextView = binding.name;
            TextView mobileNoTextView = binding.number;

            nameTextView.setText(name);
            mobileNoTextView.setText(mobileNo);
        }

        return root;
    }

    private void navigateToInventoryReport() {
        Intent intent = new Intent(requireActivity(), InventoryReport.class);
        startActivity(intent);
    }

    private void navigateToPromoActivity() {
        Intent intent = new Intent(requireActivity(), PromosActivity.class);
        startActivity(intent);
    }

    private void navigateToVoucher() {
        Intent intent = new Intent(requireActivity(), VoucherActivity.class);
        startActivity(intent);
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
