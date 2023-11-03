package com.example.coftea.Customer.advance_order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.coftea.databinding.FragmentAdvanceOrderBinding;
import com.example.coftea.databinding.FragmentProductsBinding;
import com.example.coftea.databinding.FragmentQueueBinding;

public class AdvanceOrderFragment extends Fragment {

    private FragmentAdvanceOrderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdvanceOrderViewModel advanceOrderViewModel =
                new ViewModelProvider(this).get(AdvanceOrderViewModel.class);

        binding = FragmentAdvanceOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAdvanceOrder;
        advanceOrderViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
