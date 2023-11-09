package com.example.coftea.Customer.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.data.Product;
import com.example.coftea.databinding.FragmentProductsBinding;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {
    private CustomerProductAdapter productAdapter;
    private FragmentProductsBinding binding;
    private ArrayList<Product> _products = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProductsViewModel productsViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);

        binding = FragmentProductsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView rvCustomerProductList = binding.rvCustomerProductList;

        rvCustomerProductList.setHasFixedSize(true);
        rvCustomerProductList.setLayoutManager(new LinearLayoutManager(getContext()));

        productAdapter = new CustomerProductAdapter(_products);
        rvCustomerProductList.setAdapter(productAdapter);

        productsViewModel.productList.observe(getViewLifecycleOwner(), products -> {
            _products = products;
            productAdapter.UpdateList(_products);
        });

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
