package com.example.coftea.Order;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.OrderItemList.CartItemResult;
import com.example.coftea.R;
import com.example.coftea.databinding.FragmentOrderItemBinding;
import com.example.coftea.utilities.InputFieldFilter;
import com.example.coftea.utilities.UserProvider;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.google.android.gms.tasks.Tasks;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class OrderDialogFragment extends DialogFragment {

    private OrderDialogViewModel orderDialogViewModel;
    UserProvider userProvider = UserProvider.getInstance();
    FragmentOrderItemBinding binding;
    PHPCurrencyFormatter phpCurrencyFormatter;
    Button btnClose, btnAddOrder;
    TextView tvProductName, tvProductPrice, tvProductTotalPrice;
    ImageView ivProductImage;
    EditText etProductQuantity;
    ImageButton btnAddQuantity, btnMinusQuantity;
    OrderDatabase orderDatabase;

    public OrderDialogFragment(OrderDialogViewModel orderDialogViewModel) {
        this.orderDialogViewModel = orderDialogViewModel;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentOrderItemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        root.setLayoutParams(params);

        String name = userProvider.getUser().second;

        orderDatabase = new OrderDatabase(name);

        InputFieldFilter inputFieldFilter = InputFieldFilter.getInstance();
        phpCurrencyFormatter = PHPCurrencyFormatter.getInstance();

        btnClose = binding.btnOrderItemClose;
        btnAddOrder = binding.btnOrderItemAddOrder;

        tvProductName = binding.tvOrderItemName;
        tvProductPrice = binding.tvOrderItemPrice;
        tvProductTotalPrice = binding.tvOrderItemTotalPrice;
        ivProductImage = binding.ivOrderItemImage;
        etProductQuantity = binding.etOrderItemQuantity;

        btnAddQuantity = binding.btnOrderItemAdd;
        btnMinusQuantity = binding.btnOrderItemMinus;

        btnClose.setOnClickListener(view -> {
            orderDialogViewModel.clearOrderItem();
            Objects.requireNonNull(getDialog()).hide();
        });

        btnAddOrder.setOnClickListener(view -> {
            orderDialogViewModel.setOrderItemLoading();
            new Thread(this::onAddOrderItem).start();
        });

        btnAddQuantity.setOnClickListener(view -> orderDialogViewModel.addOrderItemQuantity());
        btnMinusQuantity.setOnClickListener(view -> orderDialogViewModel.minusOrderItemQuantity());

        InputFilter inputFilter = inputFieldFilter.createNumberFilter(etProductQuantity);
        etProductQuantity.setFilters(new InputFilter[]{inputFilter});

        listenOrderItem();

        return root;
    }

    private void listenOrderItem(){
        this.orderDialogViewModel.orderItem.observe(getViewLifecycleOwner(), this::updateOrderItem);
        this.orderDialogViewModel.orderItemResult.observe(getViewLifecycleOwner(), this::onAddOrderItemResult);
    }
    private void updateOrderItem(CartItem cartItem){
        if(cartItem == null) return;
        String price = phpCurrencyFormatter.formatAsPHP(cartItem.getPrice());
        String totalPrice = phpCurrencyFormatter.formatAsPHP(cartItem.getTotalPrice());
        tvProductName.setText(cartItem.getName());
        tvProductPrice.setText(price);
        tvProductTotalPrice.setText(totalPrice);
        etProductQuantity.setText(String.valueOf(cartItem.getQuantity()));
        if ( cartItem.getImageUrl() != null && !cartItem.getImageUrl().isEmpty()) {
            Picasso.get().load( cartItem.getImageUrl()).placeholder(R.drawable.placeholder).into(ivProductImage);
        }
    }

    private void onAddOrderItem(){

        CartItem cartItem = orderDialogViewModel.orderItem.getValue();
        CartItemResult result;

        try {
            boolean addOrderItemResult = Tasks.await(orderDatabase.AddOrderItemToCart(cartItem));

            if(addOrderItemResult)
                result = new CartItemResult(cartItem);
            else
                result = new CartItemResult("ADD TO CART FAILED!");

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            result = new CartItemResult("ADD TO CART FAILED!");
        }
        orderDialogViewModel.postOrderItemResult(result);
    }

    private void onAddOrderItemResult(CartItemResult result){
        if(result == null){
            setActionState(true);
            return;
        }
        else
            setActionState(!result.loading);

        if(result.error != null){
            Toast.makeText(getContext(), result.error, Toast.LENGTH_LONG).show();
        }
        if(result.success != null){
            Toast.makeText(getContext(), "Order Added to Cart!", Toast.LENGTH_LONG).show();
            orderDialogViewModel.clearOrderItem();
            Objects.requireNonNull(getDialog()).hide();
        }
    }

    private void setActionState(boolean isEnabled){
        btnAddQuantity.setEnabled(isEnabled);
        btnMinusQuantity.setEnabled(isEnabled);
        btnClose.setEnabled(isEnabled);
        btnAddOrder.setEnabled(isEnabled);
    }

    @Override
    public void onDestroy() {
        this.orderDialogViewModel.clearOrderItem();
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if(hidden) {
            this.orderDialogViewModel.clearOrderItem();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDetach() {
        this.orderDialogViewModel.clearOrderItem();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        this.orderDialogViewModel.clearOrderItem();
        super.onDestroyView();
    }
}

