package com.example.coftea.OrderItemList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Customer.advance_order.AdvanceOrderViewModel;
import com.example.coftea.Customer.advance_order.CustomerAdvancedOrderAdapter;
import com.example.coftea.Customer.products.CustomerProductAdapter;
import com.example.coftea.OrderItem.OrderItemDatabase;
import com.example.coftea.OrderItem.OrderItemDialogFragment;
import com.example.coftea.OrderItem.OrderItemDialogViewModel;
import com.example.coftea.data.OrderItem;
import com.example.coftea.data.Product;
import com.example.coftea.databinding.FragmentOrderItemListBinding;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.example.coftea.utilities.UserProvider;

import java.util.ArrayList;

public class OrderItemListDialogFragment extends DialogFragment {
    PHPCurrencyFormatter formatter = PHPCurrencyFormatter.getInstance();
    private OrderItemListViewModel orderItemListViewModel;
    private OrderItemDialogViewModel orderItemDialogViewModel;
    private OrderItemListItemAdapter orderItemListItemAdapter;
    private @NonNull FragmentOrderItemListBinding binding;

    private Button btnOrderItemListCheckout;
    private Button btnOrderItemListClose;
    private TextView tvOrderItemListTotalPrice;
    private ArrayList<OrderItem> _orderItemList = new ArrayList<>();

    public OrderItemListDialogFragment(OrderItemListViewModel orderItemListViewModel, OrderItemDialogViewModel orderItemDialogViewModel) {

        this.orderItemListViewModel = orderItemListViewModel;
        this.orderItemDialogViewModel = orderItemDialogViewModel;
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

        orderItemListItemAdapter = new OrderItemListItemAdapter(_orderItemList, orderItemDialogViewModel, orderItemListViewModel);
        rvCustomerOrderItemList.setAdapter(orderItemListItemAdapter);

        orderItemListViewModel.orderItems.observe(getViewLifecycleOwner(), orderItems -> {
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

        return root;

    }
}
