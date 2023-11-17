package com.example.coftea.OrderItemList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Order.OrderDialogViewModel;
import com.example.coftea.R;
import com.example.coftea.data.OrderItem;
import com.example.coftea.databinding.FragmentOrderItemListBinding;
import com.example.coftea.utilities.PHPCurrencyFormatter;

import java.util.ArrayList;

public class OrderItemListDialogFragment extends DialogFragment {
    PHPCurrencyFormatter formatter = PHPCurrencyFormatter.getInstance();
    private OrderItemListViewModel  orderItemListViewModel;
    private OrderDialogViewModel orderDialogViewModel;
    private OrderItemListItemAdapter orderItemListItemAdapter;
    private @NonNull FragmentOrderItemListBinding binding;

    private Button btnOrderItemListCheckout;
    private Button btnOrderItemListClose;
    private TextView tvOrderItemListTotalPrice;
    private ArrayList<OrderItem> _orderItemList = new ArrayList<>();

    public OrderItemListDialogFragment(com.example.coftea.OrderItemList.OrderItemListViewModel orderItemListViewModel, OrderDialogViewModel orderDialogViewModel) {
        this.orderItemListViewModel = orderItemListViewModel;
        this.orderDialogViewModel = orderDialogViewModel;
    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentOrderItemListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnOrderItemListCheckout = binding.btnOrderItemListCheckOut;
        btnOrderItemListClose = binding.btnOrderItemListClose;
        tvOrderItemListTotalPrice = binding.tvOrderItemListTotalPrice;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        root.setLayoutParams(params);


        RecyclerView rvCustomerOrderItemList = binding.rvCustomerOrderItemList;

        rvCustomerOrderItemList.setHasFixedSize(true);
        rvCustomerOrderItemList.setLayoutManager(new LinearLayoutManager(getContext()));

        orderItemListItemAdapter = new OrderItemListItemAdapter(_orderItemList, orderDialogViewModel, orderItemListViewModel);
        rvCustomerOrderItemList.setAdapter(orderItemListItemAdapter);

        orderItemListViewModel.orderItems.observe(getViewLifecycleOwner(), orderItems -> {
            if(orderItems == null) return;
            if(orderItems.size() == 0){
                dismiss();
                return;
            }
            _orderItemList = orderItems;
            orderItemListItemAdapter.UpdateList(_orderItemList);
            Double total = 0.0d;
            for (OrderItem item : orderItems){
                total += item.getTotalPrice();
            }
            String totalPrice = formatter.formatAsPHP(total);
            tvOrderItemListTotalPrice.setText(totalPrice);
        });

        btnOrderItemListClose.setOnClickListener(view -> {
            dismiss();
        });

        btnOrderItemListCheckout.setOnClickListener(view -> {
            navigateToFragment(R.id.navigation_checkout_customer);
        });

        return root;

    }

    private void navigateToFragment(int fragmentId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_customer_activity);
        navController.navigate(fragmentId);
        dismiss();
    }
}
