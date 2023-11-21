package com.example.coftea.Customer.checkout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.Cashier.order.QueueEntry;
import com.example.coftea.Cashier.order.ReceiptEntry;
import com.example.coftea.Order.OrderDatabase;
import com.example.coftea.Order.OrderViewModel;
import com.example.coftea.OrderItemList.CartItemListViewModel;
import com.example.coftea.Paymongo.PaymongoCheckout;
import com.example.coftea.Paymongo.PaymongoCheckoutListener;
import com.example.coftea.Paymongo.PaymongoCheckoutSession;
import com.example.coftea.Paymongo.PaymongoCheckoutSessionListener;
import com.example.coftea.Paymongo.PaymongoPayload;
import com.example.coftea.Paymongo.PaymongoCheckoutResponse;
import com.example.coftea.R;
import com.example.coftea.data.Order;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.databinding.FragmentCheckoutBinding;
import com.example.coftea.repository.RealtimeDB;
import com.example.coftea.utilities.PHPCurrencyFormatter;
import com.example.coftea.utilities.UserProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Date;

public class CheckoutFragment extends Fragment implements PaymongoCheckoutListener, PaymongoCheckoutSessionListener {
    private final UserProvider userProvider = UserProvider.getInstance();
    private final PHPCurrencyFormatter formatter = PHPCurrencyFormatter.getInstance();
    private OrderViewModel orderViewModel;
    private RealtimeDB orderRealtimeDB, cartItemListRealtimeDB;
    private PaymongoPayload paymongoPayload;
    private CartItemListViewModel cartItemListViewModel;
    private FragmentCheckoutBinding binding;
    private TextView tvTotalAmountDue, tvCheckoutStatus, tvCheckoutDescription;
    private Button btnPayWithGCash;
    CheckoutViewModel checkoutViewModel;
    OrderDatabase orderDatabase;
    private Order order;
    String userName, userMobileNo;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        init();
        listen();

        return root;
    }

    private void init(){

        userName = userProvider.getUser().first;
        userMobileNo = userProvider.getUser().second;

        orderDatabase = new OrderDatabase(userMobileNo);

        cartItemListViewModel = new CartItemListViewModel(userMobileNo);
        cartItemListRealtimeDB = cartItemListViewModel.getRealtimeDB();
        orderViewModel = new OrderViewModel(userMobileNo);
        orderRealtimeDB = orderViewModel.getRealtimeDB();

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        tvTotalAmountDue = binding.tvTotalAmountDue;
        tvCheckoutStatus = binding.tvCheckoutStatus;
        tvCheckoutDescription = binding.tvCheckoutDescription;
        btnPayWithGCash = binding.btnPayWithGCash;
        btnPayWithGCash.setEnabled(false);
        btnPayWithGCash.setOnClickListener(view -> {
            startCheckout();
        });
    }

    private void listen(){

        orderViewModel.orderResult.observe(getViewLifecycleOwner(), order -> {
            if(order == null) return;
            if(order.error != null){
                Toast.makeText(getContext(), order.error, Toast.LENGTH_SHORT).show();
                return;
            }
            if(order.success != null){
                if(!order.success.hasCheckoutSession())
                    btnPayWithGCash.setEnabled(true);
                else
                    this.order = order.success;
                getCartItem();
            }
        });
    }
    private void getCartItem(){
        cartItemListRealtimeDB.getDatabaseReference().get()
                .addOnSuccessListener(dataSnapshot -> {
                    ArrayList<CartItem> list = new ArrayList<>();
                    Double total = 0.0d;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        CartItem item = snapshot.getValue(CartItem.class);
                        item.setKey(snapshot.getKey());
                        total += item.getTotalPrice();
                        list.add(item);
                    }

                    String totalPrice = formatter.formatAsPHP(total);
                    tvTotalAmountDue.setText(totalPrice);
                    cartItems = list;
                    paymongoPayload = new PaymongoPayload(userMobileNo+userName, userName, userMobileNo, total);
                    proceedWithPaymentChecking();
                });
    }
    private void proceedWithPaymentChecking(){
        if(order == null || cartItems == null || paymongoPayload == null) return;
        Log.e("TEST====1", String.valueOf(order));
        Log.e("TEST====1", String.valueOf(order.getAmount()));
        Log.e("TEST====1", String.valueOf(order.getOrderPlaced()));
        Log.e("TEST====1", String.valueOf(order.getCheckoutSessionID()));
        if(order.getOrderPlaced() != null && order.getOrderPlaced()) {
            tvCheckoutDescription.setText(R.string.customer_message_payment_received);
            return;
        }
        checkPayment(order.getCheckoutSessionID());
    }
    private void startCheckout(){
        if(order != null){
            Log.e("Checkout Exists", order.getCheckoutSessionID());
            gotoCheckoutSessionUrl(this.order.getCheckoutSessionUrl());
            return;
        }
        if(paymongoPayload == null){
            Toast.makeText(getContext(), "Invalid Checkout Payload, Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        new PaymongoCheckout(this).execute(paymongoPayload);
    }

    private void checkPayment(String sessionID) {
        Log.e("CheckingPayment", String.valueOf(order.hasCheckoutSession()));

        PaymongoCheckoutSession paymongoCheckoutSession = new PaymongoCheckoutSession(this);
        paymongoCheckoutSession.execute(sessionID);
    }
    @Override
    public void onPaymongoCheckoutComplete(PaymongoCheckoutResponse result) {

        try {
            DatabaseReference checkoutSessionID = orderDatabase.getOrderDBRef();
            checkoutSessionID.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    currentData.child("checkoutSessionID").setValue(result.getId());
                    currentData.child("checkoutSessionUrl").setValue(result.getCheckoutUrl());
                    return Transaction.success(currentData);
                }
                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    if (committed && error == null) {
                        processCheckout(result);
                    } else {
                        Toast.makeText(getContext(), "Updating Order Failed, Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Updating Order Error, Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    ArrayList<CartItem> cartItems;
    @Override
    public void onPaymongoCheckoutSessionComplete(PaymongoCheckoutResponse result) {
        Log.e("SessionResponse", String.valueOf(result.getPaymentID()));
        if(!result.isCheckoutSessionPaid() && !order.isCheckoutSessionPaid()){
            btnPayWithGCash.setEnabled(true);
        }
        else {
            btnPayWithGCash.setEnabled(false);
        }

        Date date = new Date();
        ReceiptEntry receiptEntry = new ReceiptEntry(cartItems, paymongoPayload.getAmount(), userName, userMobileNo, date, OrderStatus.PENDING);
        QueueEntry queueEntry = new QueueEntry(paymongoPayload.getAmount(), userName, userMobileNo, date, OrderStatus.PENDING);
        queueEntry.setOnlineOrder(true);

        try {
            DatabaseReference checkoutSessionID = orderDatabase.getOrderDBRef();
            checkoutSessionID.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    currentData.child("paymentID").setValue(result.getPaymentID());
                    currentData.child("fee").setValue(result.getFee());
                    currentData.child("netAmount").setValue(result.getNetAmount());
                    currentData.child("amount").setValue(result.getAmount());
                    currentData.child("orderPlaced").setValue(true);
                    return Transaction.success(currentData);
                }
                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    if (committed && error == null) {
                        saveTransaction(receiptEntry, queueEntry);
                    } else {
                        Toast.makeText(getContext(), "Updating Order Failed, Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Updating Order Error, Please try again.", Toast.LENGTH_SHORT).show();
        }
    }


    private void processCheckout(PaymongoCheckoutResponse result){
        try{
            gotoCheckoutSessionUrl(result.getCheckoutUrl());
        }
        catch (Exception e){
            Log.e("ERROR", e.toString());
        }
    }

    private void gotoCheckoutSessionUrl(String checkoutUrl){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl));
        startActivity(intent);
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private void saveTransaction(ReceiptEntry receiptEntry, QueueEntry queueEntry){
        DatabaseReference cashierDBRef = database.getReference("cashier");

        DatabaseReference receiptsDBRef = cashierDBRef.child("receipts");
        DatabaseReference queueDBRef = cashierDBRef.child("queue");

        String receiptID = receiptsDBRef.push().getKey();
        String queueID = queueDBRef.push().getKey();

        queueEntry.setReceiptID(receiptID);
        receiptEntry.setId(receiptID);

        receiptsDBRef.child(receiptID).setValue(receiptEntry);
        queueDBRef.child(queueID).setValue(queueEntry);
        tvCheckoutDescription.setText(R.string.customer_message_payment_received);
        Toast.makeText(getContext(), "Order Placed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
