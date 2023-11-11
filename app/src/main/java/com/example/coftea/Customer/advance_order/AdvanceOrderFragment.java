package com.example.coftea.Customer.advance_order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coftea.OrderItem.OrderItemDialogFragment;
import com.example.coftea.OrderItem.OrderItemDialogViewModel;
import com.example.coftea.data.Product;
import com.example.coftea.databinding.FragmentAdvanceOrderBinding;
import com.example.coftea.utilities.UserProvider;

import java.util.ArrayList;

public class AdvanceOrderFragment extends Fragment {
    private final UserProvider userProvider = UserProvider.getInstance();

    private FragmentAdvanceOrderBinding binding;
    private CustomerAdvancedOrderAdapter customerAdvancedOrderAdapter;
    private AdvanceOrderViewModel advanceOrderViewModel;
    private OrderItemDialogViewModel orderItemDialogViewModel;
    private ArrayList<Product> _products = new ArrayList<>();
    OrderItemDialogFragment orderToCartDialogFragment;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAdvanceOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        advanceOrderViewModel = new ViewModelProvider(this).get(AdvanceOrderViewModel.class);
        orderItemDialogViewModel = new ViewModelProvider(this).get(OrderItemDialogViewModel.class);

        RecyclerView rvCustomerProductList = binding.rvAdvanceOrderList;

        rvCustomerProductList.setHasFixedSize(true);
        rvCustomerProductList.setLayoutManager(new LinearLayoutManager(getContext()));

        customerAdvancedOrderAdapter = new CustomerAdvancedOrderAdapter(_products, advanceOrderViewModel, orderItemDialogViewModel);
        rvCustomerProductList.setAdapter(customerAdvancedOrderAdapter);
        advanceOrderViewModel.productList.observe(getViewLifecycleOwner(), products -> {
            _products = products;
            customerAdvancedOrderAdapter.UpdateList(_products);
        });

        orderToCartDialogFragment = new OrderItemDialogFragment(orderItemDialogViewModel);

        orderItemDialogViewModel.orderItem.observe(getViewLifecycleOwner(), orderItem -> {
            if (orderItem == null) return;
            Log.e("TEST========11",orderItem.getId());
            OrderItemDialogFragment existingFragment = (OrderItemDialogFragment) getParentFragmentManager().findFragmentByTag("ItemToOrderFragment");

            if (existingFragment == null){
                orderToCartDialogFragment.show(getParentFragmentManager(), "ItemToOrderFragment");
                Log.e("TEST========22",orderItem.getId());
            }
            else{
                existingFragment.getDialog().show();
                Log.e("TEST========33",orderItem.getId());
            }

        });

        return root;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
