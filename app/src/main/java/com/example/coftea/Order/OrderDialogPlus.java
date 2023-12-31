package com.example.coftea.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.OrderItemList.CartItemResult;
import com.example.coftea.databinding.CustomerAddToCartBinding;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.example.coftea.utilities.UserProvider;
import com.google.android.gms.tasks.Tasks;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.concurrent.ExecutionException;

public class OrderDialogPlus {
    UserProvider userProvider = UserProvider.getInstance();
    private PHPCurrencyFormatter formatter = PHPCurrencyFormatter.getInstance();
    private DialogPlus dialogPlus;
    private OrderDialogViewModel orderDialogViewModel;
    private CustomerAddToCartBinding binding;


    private TextView tvCustomerProductID;
    private TextView tvCustomerProductName;
    private TextView tvCustomerProductPrice;
    private TextView tvCustomerProductPriceLabel;
    private TextView tvCustomerOrderItemTotalPrice;
    private TextView tvCustomerOrderItemQuantity;
    private TextView tvCustomerOrderItemQuantityLabel;
    private ImageButton ibCustomerOrderItemAddQuantity;
    private ImageButton ibCustomerOrderItemMinusQuantity;
    private Button btnCustomerOrderItemAddToCart;
    private Button btnCustomerOrderItemCancel;

    private LifecycleOwner lifecycleOwner;
    private OrderDatabase orderDatabase;

    private Context context;
    public OrderDialogPlus(LifecycleOwner lifecycleOwner, Context context, OrderDialogViewModel orderDialogViewModel) {
        this.context = context;
        binding = CustomerAddToCartBinding.inflate(LayoutInflater.from(context));
        DialogPlusBuilder dialogBuilder = DialogPlus.newDialog(context);
        dialogBuilder.setContentHolder(new ViewHolder(binding.getRoot()));
        dialogBuilder.setExpanded(true, 1200);
        dialogPlus = dialogBuilder.create();

//        View view = dialogPlus.getHolderView();
        this.lifecycleOwner = lifecycleOwner;
        this.orderDialogViewModel = orderDialogViewModel;

        init();
        listenOrderItem();
    }

    private void init(){
        tvCustomerProductID = binding.tvCustomerProductID;
        tvCustomerProductName = binding.tvCustomerProductName;
        tvCustomerProductPrice = binding.tvCustomerProductPrice;
        tvCustomerOrderItemTotalPrice = binding.tvCustomerOrderItemTotalPrice;
        tvCustomerProductPriceLabel = binding.tvCustomerProductPriceLabel;

        tvCustomerOrderItemQuantity = binding.tvCustomerOrderItemQuantity;
        tvCustomerOrderItemQuantityLabel = binding.tvCustomerOrderItemQuantityLabel;

        ibCustomerOrderItemAddQuantity = binding.ibCustomerOrderItemAddQuantity;
        ibCustomerOrderItemMinusQuantity = binding.ibCustomerOrderItemMinusQuantity;

        btnCustomerOrderItemAddToCart = binding.btnCustomerOrderItemAddToCart;
        btnCustomerOrderItemCancel = binding.btnCustomerOrderItemCancel;

        ibCustomerOrderItemAddQuantity.setOnClickListener(view -> orderDialogViewModel.addOrderItemQuantity());
        ibCustomerOrderItemMinusQuantity.setOnClickListener(view -> orderDialogViewModel.minusOrderItemQuantity());

        btnCustomerOrderItemAddToCart.setOnClickListener(view -> {
            orderDialogViewModel.setOrderItemLoading();
            new Thread(this::onAddOrderItem).start();
        });

        btnCustomerOrderItemCancel.setOnClickListener(view -> {
            orderDialogViewModel.clearOrderItem();
            cleanUp();
        });

        String name = userProvider.getUser().second;
        orderDatabase = new OrderDatabase(name);
    }

    private void listenOrderItem(){
        orderDialogViewModel.orderItem.observe(lifecycleOwner, this::updateView);
        orderDialogViewModel.orderItemResult.observe(lifecycleOwner, this::onAddOrderItemResult);
    }

    private void updateView(CartItem cartItem){
        if(cartItem == null){
            cleanUp();
            return;
        }
        if(!dialogPlus.isShowing())
            dialogPlus.show();

        tvCustomerProductID.setText(cartItem.getId().toString());
        tvCustomerProductName.setText(cartItem.getName());

        String productPrice = formatter.formatAsPHP(cartItem.getPrice());
        tvCustomerProductPrice.setText(productPrice);
        tvCustomerProductPriceLabel.setText(productPrice);

        tvCustomerOrderItemQuantity.setText(String.valueOf(cartItem.getQuantity()));

        String orderTotalPrice = formatter.formatAsPHP(cartItem.getTotalPrice());
        tvCustomerOrderItemTotalPrice.setText(orderTotalPrice);

        tvCustomerOrderItemQuantityLabel.setText(String.valueOf(cartItem.getQuantity()));

    }

    private void onAddOrderItem(){
        setActionState(false);
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
            orderDialogViewModel.clearOrderItem();
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
            Toast.makeText(context, result.error, Toast.LENGTH_LONG).show();
        }
        if(result.success != null){
            Toast.makeText(context, "Order Added to Cart!", Toast.LENGTH_LONG).show();
            orderDialogViewModel.clearOrderItem();
            cleanUp();
        }
    }

    private void setActionState(boolean isEnabled){
        btnCustomerOrderItemAddToCart.setEnabled(isEnabled);
        ibCustomerOrderItemMinusQuantity.setEnabled(isEnabled);
        ibCustomerOrderItemAddQuantity.setEnabled(isEnabled);
    }

    public void cleanUp(){
        if(dialogPlus == null) return;
        dialogPlus.dismiss();
    }
}
