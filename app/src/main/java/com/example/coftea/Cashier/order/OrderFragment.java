package com.example.coftea.Cashier.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coftea.databinding.FragmentOrderBinding;

public class OrderFragment extends Fragment {
    private FragmentOrderBinding binding;
    Button btn_order;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrderViewModel orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btn_order = binding.btnOrder; // Initialize the button

        btn_order.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), ProductListActivity.class); // Use requireActivity() to get the parent activity
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
