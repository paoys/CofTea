package com.example.coftea.Customer.advance_order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.coftea.OrderItem.OrderItemDialogFragment;
import com.example.coftea.OrderItem.OrderItemDialogPlus;
import com.example.coftea.OrderItem.OrderItemDialogViewModel;
import com.example.coftea.OrderItemList.OrderItemListDialogFragment;
import com.example.coftea.OrderItemList.OrderItemListViewModel;
import com.example.coftea.OrderItemList.OrderItemListViewModelFactory;
import com.example.coftea.data.Product;
import com.example.coftea.databinding.FragmentAdvanceOrderBinding;
import com.example.coftea.utilities.UserProvider;

import java.util.ArrayList;

public class AdvanceOrderFragment extends Fragment {
    private final UserProvider userProvider = UserProvider.getInstance();

    private ImageButton ibCartButton;
    private TextView tvOrderItemCount;

    private FragmentAdvanceOrderBinding binding;
    private CustomerAdvancedOrderAdapter customerAdvancedOrderAdapter;
    private AdvanceOrderViewModel advanceOrderViewModel;
    private OrderItemDialogViewModel orderItemDialogViewModel;
    private OrderItemListViewModel orderItemListViewModel;
    private ArrayList<Product> _products = new ArrayList<>();
    OrderItemDialogFragment orderToCartDialogFragment;
    OrderItemListDialogFragment orderItemListDialogFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        String userName = userProvider.getUser().first;
        String userMobileNo = userProvider.getUser().second;

        binding = FragmentAdvanceOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ibCartButton = binding.ibCartButton;
        tvOrderItemCount = binding.tvOrderItemCount;

        ibCartButton.setEnabled(false);

        advanceOrderViewModel = new ViewModelProvider(this).get(AdvanceOrderViewModel.class);
        orderItemDialogViewModel = new ViewModelProvider(this).get(OrderItemDialogViewModel.class);
        orderItemListViewModel = new ViewModelProvider(this, new OrderItemListViewModelFactory(userMobileNo)).get(OrderItemListViewModel.class);

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
            OrderItemDialogFragment existingFragment = (OrderItemDialogFragment) getParentFragmentManager().findFragmentByTag("ItemToOrderFragment");

            if (existingFragment == null)
                orderToCartDialogFragment.show(getParentFragmentManager(), "ItemToOrderFragment");
            else
                existingFragment.getDialog().show();
        });

//        OrderItemDialogPlus orderItemDialogPlus = new OrderItemDialogPlus(getViewLifecycleOwner(), getContext(), orderItemDialogViewModel);

        orderItemListViewModel = new OrderItemListViewModel(userMobileNo);
        orderItemListViewModel.orderItems.observe(getViewLifecycleOwner(), orderItems -> {
            ibCartButton.setEnabled(orderItems.size() != 0);
            tvOrderItemCount.setText(String.valueOf(orderItems.size()));
        });

        orderItemListDialogFragment = new OrderItemListDialogFragment(orderItemListViewModel, orderItemDialogViewModel);

        ibCartButton.setOnClickListener(view -> {
            OrderItemListDialogFragment existingFragment = (OrderItemListDialogFragment) getParentFragmentManager().findFragmentByTag("OrderItemListFragment");

            if (existingFragment == null)
                orderItemListDialogFragment.show(getParentFragmentManager(), "OrderItemListFragment");
            else
                existingFragment.getDialog().show();
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
