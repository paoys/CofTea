package com.example.coftea.Customer.advance_order;

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


import com.example.coftea.LocationListener.MyLocationListener;
import com.example.coftea.Order.OrderDialogFragment;
import com.example.coftea.Order.OrderDialogViewModel;
import com.example.coftea.OrderItemList.OrderItemListDialogFragment;
import com.example.coftea.OrderItemList.OrderItemListViewModel;
import com.example.coftea.OrderItemList.OrderItemListViewModelFactory;
import com.example.coftea.data.Product;
import com.example.coftea.databinding.FragmentAdvanceOrderBinding;
import com.example.coftea.utilities.UserProvider;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
public class AdvanceOrderFragment extends Fragment {
    private final UserProvider userProvider = UserProvider.getInstance();
    private ImageButton ibCartButton;
    private TextView tvOrderItemCount;
    private FragmentAdvanceOrderBinding binding;
    private CustomerAdvancedOrderAdapter customerAdvancedOrderAdapter;
    private AdvanceOrderViewModel advanceOrderViewModel;
    private OrderDialogViewModel orderDialogViewModel;
    private OrderItemListViewModel  orderItemListViewModel;
    private ArrayList<Product> _products = new ArrayList<>();
    private OrderDialogFragment orderToCartDialogFragment;
    private OrderItemListDialogFragment orderItemListDialogFragment;

    MyLocationListener myLocationListener;

    private void startLocationListener() {
        myLocationListener = new MyLocationListener(requireContext());

        if (myLocationListener.canGetLocation()) {
            startListen();
        } else {
            // Show alert to prompt the user to enable GPS
            myLocationListener.showSettingsAlert();
        }
    }

    private void stopLocationListener() {
        if (myLocationListener != null) {
            myLocationListener.stopListener();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdvanceOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init();

        return root;
    }

    private void init(){

        String userName = userProvider.getUser().first;
        String userMobileNo = userProvider.getUser().second;

        ibCartButton = binding.ibCartButton;
        tvOrderItemCount = binding.tvOrderItemCount;

        RecyclerView rvCustomerProductList = binding.rvAdvanceOrderList;
        rvCustomerProductList.setHasFixedSize(true);
        rvCustomerProductList.setLayoutManager(new LinearLayoutManager(getContext()));

        ibCartButton.setEnabled(false);
        ibCartButton.setVisibility(View.GONE);
        tvOrderItemCount.setVisibility(View.GONE);

        advanceOrderViewModel = new ViewModelProvider(this).get(AdvanceOrderViewModel.class);
        orderDialogViewModel = new ViewModelProvider(this).get(OrderDialogViewModel.class);
        orderItemListViewModel = new ViewModelProvider(this, new OrderItemListViewModelFactory(userMobileNo)).get(OrderItemListViewModel.class);

        customerAdvancedOrderAdapter = new CustomerAdvancedOrderAdapter(_products, advanceOrderViewModel, orderDialogViewModel);
        orderToCartDialogFragment = new OrderDialogFragment(orderDialogViewModel);
        rvCustomerProductList.setAdapter(customerAdvancedOrderAdapter);

        orderItemListViewModel = new OrderItemListViewModel(userMobileNo);
        orderItemListDialogFragment = new OrderItemListDialogFragment(orderItemListViewModel, orderDialogViewModel);

        ibCartButton.setOnClickListener(view -> {
            OrderItemListDialogFragment existingFragment = (OrderItemListDialogFragment) getParentFragmentManager().findFragmentByTag("OrderItemListFragment");

            if (existingFragment == null)
                orderItemListDialogFragment.show(getParentFragmentManager(), "OrderItemListFragment");
            else
                existingFragment.getDialog().show();
        });
    }

    private void startListen(){
        ibCartButton.setVisibility(View.VISIBLE);
        tvOrderItemCount.setVisibility(View.VISIBLE);

        advanceOrderViewModel.productList.observe(getViewLifecycleOwner(), products -> {
            _products = products;
            customerAdvancedOrderAdapter.UpdateList(_products);
        });

        orderDialogViewModel.orderItem.observe(getViewLifecycleOwner(), orderItem -> {
            if (orderItem == null) return;
            OrderDialogFragment existingFragment = (OrderDialogFragment) getParentFragmentManager().findFragmentByTag("ItemToOrderFragment");

            if (existingFragment == null)
                orderToCartDialogFragment.show(getParentFragmentManager(), "ItemToOrderFragment");
            else
                existingFragment.getDialog().show();
        });

//        OrderItemDialogPlus orderItemDialogPlus = new OrderItemDialogPlus(getViewLifecycleOwner(), getContext(), orderItemDialogViewModel);

        orderItemListViewModel.orderItems.observe(getViewLifecycleOwner(), orderItems -> {
            ibCartButton.setEnabled(orderItems.size() != 0);
            tvOrderItemCount.setText(String.valueOf(orderItems.size()));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        startLocationListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
