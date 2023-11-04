package com.example.coftea.Cashier.stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coftea.R;
import com.example.coftea.databinding.FragmentStockBinding;

public class StockFragment extends Fragment {
    private FragmentStockBinding binding;
    Button btn_ingredients_stock, btn_products;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StockViewModel stockViewModel = new ViewModelProvider(this).get(StockViewModel.class);

        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btn_ingredients_stock = root.findViewById(R.id.btn_ingredients_stock); // Initialize the button
        btn_products = root.findViewById(R.id.btn_products);

        btn_ingredients_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), StockIngredients.class); // Use requireActivity() to get the parent activity
                startActivity(intent);
            }
        });

        btn_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ManageProductActivityList.class); // Use requireActivity() to get the parent activity
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
