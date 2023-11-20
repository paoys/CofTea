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

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.Order.OrderDialogViewModel;
import com.example.coftea.R;
import com.example.coftea.databinding.FragmentOrderItemListBinding;
import com.example.coftea.utilities.PHPCurrencyFormatter;

import java.util.ArrayList;

public class CartItemListDialogFragment extends DialogFragment {
    PHPCurrencyFormatter formatter = PHPCurrencyFormatter.getInstance();
    private CartItemListViewModel cartItemListViewModel;
    private OrderDialogViewModel orderDialogViewModel;
    private CartItemListItemAdapter cartItemListItemAdapter;
    private @NonNull FragmentOrderItemListBinding binding;
    private Button btnOrderItemListCheckout;
    private Button btnOrderItemListClose;
    private TextView tvOrderItemListTotalPrice;
    private ArrayList<CartItem> _cartItemList = new ArrayList<>();

    public CartItemListDialogFragment(CartItemListViewModel cartItemListViewModel, OrderDialogViewModel orderDialogViewModel) {
        this.cartItemListViewModel = cartItemListViewModel;
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

        cartItemListItemAdapter = new CartItemListItemAdapter(_cartItemList, orderDialogViewModel, cartItemListViewModel);
        rvCustomerOrderItemList.setAdapter(cartItemListItemAdapter);

        cartItemListViewModel.cartItems.observe(getViewLifecycleOwner(), cartItems -> {
            if(cartItems == null) return;
            if(cartItems.size() == 0){
                dismiss();
                return;
            }
            _cartItemList = cartItems;
            cartItemListItemAdapter.UpdateList(_cartItemList);
            Double total = 0.0d;
            for (CartItem item : cartItems){
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
