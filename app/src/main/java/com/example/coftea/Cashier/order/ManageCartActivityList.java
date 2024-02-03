package com.example.coftea.Cashier.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Cashier.settings.voucher.Voucher;
import com.example.coftea.R;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.repository.RealtimeDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageCartActivityList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ManageCartAdapter productAdapter;
    private List<CartItem> cartItemList;
    private DatabaseReference cartDBRef;
    private Button checkoutButton;
    private Button voucherButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private boolean paymentValidated = false;
    private int queueItemNumber = 0; // Initialize the queue item number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_cart_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItemList = new ArrayList<>();
        productAdapter = new ManageCartAdapter(this, cartItemList);
        recyclerView.setAdapter(productAdapter);

        cartDBRef = database.getReference("cashier/cart");

        // Retrieve and display products
        retrieveProducts();

        voucherButton = findViewById(R.id.voucherButton);
        voucherButton.setOnClickListener(view -> showVoucherPopup());

        checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(view -> {
            if (paymentValidated) {
                // If payment is validated, show "Enter Name and Phone" dialog
                showNamePhoneInputDialog();
            } else {
                // If payment is not validated, show "Payment Result" dialog
                double totalPrice = calculateTotalPrice(); // Calculate the total price
                showPaymentResultDialog();
            }
        });
    }

    private void showVoucherPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.voucher_popup, null);
        dialogBuilder.setView(dialogView);

        EditText editTextVoucherCode = dialogView.findViewById(R.id.editTextVoucherCode);
        Button buttonApplyVoucher = dialogView.findViewById(R.id.buttonApplyVoucher);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(view -> alertDialog.dismiss()); // Dismiss the dialog on cancel button click

        buttonApplyVoucher.setOnClickListener(view -> {
            String enteredVoucherCode = editTextVoucherCode.getText().toString().trim();
            if (!enteredVoucherCode.isEmpty()) {
                alertDialog.dismiss();

                RealtimeDB<Voucher> voucherRealtimeDB = new RealtimeDB<>("vouchers");
                DatabaseReference voucherDBRef = voucherRealtimeDB.getDatabaseReference();

                voucherDBRef.orderByChild("code").equalTo(enteredVoucherCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Voucher code exists in the database
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Voucher voucher = snapshot.getValue(Voucher.class);
                                if (voucher != null) {
                                    long currentTimeStamp = System.currentTimeMillis() / 1000; // Current timestamp in seconds

                                    if (voucher.getExpirationTimestamp() < currentTimeStamp) {
                                        // Voucher is expired
                                        Toast.makeText(getApplicationContext(), "The voucher is expired and not available anymore.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Voucher is valid and not expired
                                        double discount = voucher.getDiscount();
                                        double totalPrice = calculateTotalPrice(); // Calculate the total price

                                        double discountedTotalPrice = totalPrice - (totalPrice * (discount / 100));

                                        // Display a message indicating that the voucher is successfully applied
                                        Toast.makeText(getApplicationContext(), "The voucher is successfully applied!", Toast.LENGTH_SHORT).show();

                                        // Call the method to handle applying voucher and updating payment details
                                        applyVoucherAndGetTotalPrice(totalPrice, enteredVoucherCode);
                                        //showPaymentResultDialog();
                                    }
                                }
                            }
                        } else {
                            // Voucher code does not exist in the database
                            Toast.makeText(getApplicationContext(), "Invalid voucher code. Please enter a valid code.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
            } else {
                // Show an error message if the voucher code is empty
                Toast.makeText(this, "Please enter a voucher code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to calculate the total price of items in the cart
    private double calculateTotalPrice() {
        double totalPrice = 0.0;

        for (CartItem product : cartItemList) {
            double itemPrice = Double.parseDouble(product.getPrice().toString());
            int itemQuantity = product.getQuantity();
            double itemTotal = itemPrice * itemQuantity;
            totalPrice += itemTotal; // Accumulate the total price
        }

        return totalPrice;
    }

    private void retrieveProducts() {
        cartDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem product = snapshot.getValue(CartItem.class);
                    cartItemList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }

    private void showPaymentResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment Result");

        StringBuilder message = new StringBuilder();
        double totalPrice = 0.0; // Initialize the total price

        if (!cartItemList.isEmpty()) {
            message.append("Payment is valid. Your order has been placed.\n\n");
            message.append("Items in the cart:\n\n");

            // Iterate through cart items and show details
            for (CartItem product : cartItemList) {
                message.append("Product: ").append(product.getName()).append("\n");
                message.append("Price: ").append(product.getPrice()).append("\n");
                message.append("Quantity: ").append(product.getQuantity()).append("\n");

                // Calculate item total price
                double itemPrice = Double.parseDouble(product.getPrice().toString());
                int itemQuantity = product.getQuantity();
                double itemTotal = itemPrice * itemQuantity;
                message.append("Item Total: ").append(itemTotal).append("\n\n");
            }

            // Add buttons to the dialog
            builder.setPositiveButton("Continue", (dialog, which) -> {
                paymentValidated = true;
                dialog.dismiss();
                showNamePhoneInputDialog(); // Automatically open the "Enter Phone and Name" dialog
            });

            builder.setNeutralButton("Edit Order", (dialog, which) -> {
                // Handle editing the order here
                dialog.dismiss();
            });
        } else {
            message.append("Payment is invalid. Please check your payment information.");
        }

        // Set message to the dialog and show
        builder.setMessage(message.toString());
        builder.create().show();
    }

    private void showPaymentResultDialog(double totalPrice, double discountedTotalPrice, boolean voucherApplied) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment Result");

        StringBuilder message = new StringBuilder();

        if (!cartItemList.isEmpty()) {
            if (voucherApplied) {
                message.append("Payment is valid with applied voucher. Your order has been placed.\n\n");
            } else {
                message.append("Payment is valid. Your order has been placed.\n\n");
            }
            message.append("Items in the cart:\n\n");

            for (CartItem product : cartItemList) {
                message.append("Product: ").append(product.getName()).append("\n");
                message.append("Price: ").append(product.getPrice()).append("\n");
                message.append("Quantity: ").append(product.getQuantity()).append("\n");

                double itemPrice = Double.parseDouble(product.getPrice().toString());
                int itemQuantity = product.getQuantity();
                double itemTotal = itemPrice * itemQuantity;
                message.append("Item Total: ").append(itemTotal).append("\n\n");
            }

            message.append("Total Price (Before Discount): ").append(totalPrice).append("\n");

            if (voucherApplied) {
                message.append("Total Price (After Discount): ").append(discountedTotalPrice).append("\n");
            }

            builder.setPositiveButton("Continue", (dialog, which) -> {
                paymentValidated = true;
                dialog.dismiss();
                showNamePhoneInputDialog();
            });

            /*builder.setNeutralButton("Edit Order", (dialog, which) -> {
                // Handle editing the order here
                dialog.dismiss();
            });*/
        } else {
            message.append("Payment is invalid. Please check your payment information.");
        }

        builder.setMessage(message.toString());
        builder.create().show();
    }

    private void applyVoucherAndGetTotalPrice(double totalPrice, String enteredVoucherCode) {
        RealtimeDB<Voucher> voucherRealtimeDB = new RealtimeDB<>("vouchers");
        DatabaseReference voucherDBRef = voucherRealtimeDB.getDatabaseReference();

        if (!enteredVoucherCode.isEmpty()) {
            voucherDBRef.orderByChild("code").equalTo(enteredVoucherCode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Voucher voucher = snapshot.getValue(Voucher.class);
                            if (voucher != null) {
                                long currentTimeStamp = System.currentTimeMillis() / 1000;

                                if (voucher.getExpirationTimestamp() < currentTimeStamp) {
                                    Toast.makeText(getApplicationContext(), "The voucher is expired and not available anymore.", Toast.LENGTH_SHORT).show();
                                } else {
                                    double discount = voucher.getDiscount();
                                    double discountAmount = totalPrice * (discount / 100);
                                    double discountedTotalPrice = totalPrice - discountAmount;

                                    Toast.makeText(getApplicationContext(), "The voucher is successfully applied!", Toast.LENGTH_SHORT).show();

                                    // Update the total price in the Firebase database
                                    cartDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                CartItem cartItem = snapshot.getValue(CartItem.class);
                                                if (cartItem != null) {
                                                    double itemPrice = Double.parseDouble(cartItem.getPrice().toString());
                                                    int itemQuantity = cartItem.getQuantity();
                                                    double itemTotal = itemPrice * itemQuantity;

                                                    // Calculate the updated item total based on the discounted price
                                                    double updatedItemTotal = (itemTotal / totalPrice) * discountedTotalPrice;

                                                    // Update the total price in the Firebase database for this item
                                                    snapshot.getRef().child("totalPrice").setValue(updatedItemTotal);
                                                }
                                            }
                                            // Show the payment result dialog with the updated total price
                                            showPaymentResultDialog(totalPrice, discountedTotalPrice, true);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle onCancelled
                                        }
                                    });
                                    return;
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid voucher code. Please enter a valid code.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });
        } else {
            Toast.makeText(this, "Please enter a voucher code", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNamePhoneInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Customer Information");

        // Inflate a custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.enter_info, null);
        builder.setView(dialogView);

        // Get references to the name and phone number EditText fields
        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        final EditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);

        builder.setPositiveButton("Add to Queue", (dialog, which) -> {

            String name = nameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String id = phoneEditText.getText().toString();

            double totalPayment = 0.0;
            Date date = new Date();

            for (CartItem product : cartItemList) {
                double itemPrice = Double.parseDouble(product.getPrice().toString());
                int itemQuantity = product.getQuantity();
                double itemTotal = itemPrice * itemQuantity;
                totalPayment += itemTotal;
            }


            // Validate that name and phone number are not empty
            if (name.isEmpty() || phone.isEmpty()) {
                // Show an error message if fields are empty
                showErrorMessage("Name and phone number are required.");
            } else {
                int queueNumber = generateQueueNumber();
                ReceiptEntry receiptEntry = new ReceiptEntry(cartItemList, totalPayment, name, phone, date, OrderStatus.PENDING);
                QueueEntry queueEntry = new QueueEntry(totalPayment, name, phone, date, OrderStatus.PENDING, queueNumber);
                saveTransaction(receiptEntry, queueEntry);
//                    addToQueue(name, phone, totalPayment);
//                    saveReceipt(cartItemList, name, phone, totalPayment, date);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private int generateQueueNumber() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        int storedQueueNumber = getStoredQueueNumber();
        String storedDate = getStoredDate();

        if (storedDate != null && storedDate.equals(formattedDate)) {
            storedQueueNumber++;
            saveDateAndQueueNumber(formattedDate, storedQueueNumber);
            return storedQueueNumber;
        } else {
            saveDateAndQueueNumber(formattedDate, 1);
            return 1;
        }
    }

    private void saveDateAndQueueNumber(String date, int queueNumber) {
        SharedPreferences preferences = getSharedPreferences("QueuePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("storedDate", date);
        editor.putInt("queueNumber", queueNumber);
        editor.apply();
    }

    private String getStoredDate() {
        SharedPreferences preferences = getSharedPreferences("QueuePrefs", MODE_PRIVATE);
        return preferences.getString("storedDate", null);
    }

    private int getStoredQueueNumber() {
        SharedPreferences preferences = getSharedPreferences("QueuePrefs", MODE_PRIVATE);
        return preferences.getInt("queueNumber", 0);
    }


    private void removeItemsFromCart() {
        // Remove items from the "cart" reference
        cartDBRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Items removed from the cart successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the failure to remove items from the cart
                    }
                });
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void saveTransaction(ReceiptEntry receiptEntry, QueueEntry queueEntry){
        DatabaseReference cashierDBRef = database.getReference("cashier");

        DatabaseReference receiptsDBRef = cashierDBRef.child("receipts");
        DatabaseReference queueDBRef = cashierDBRef.child("queue");
        DatabaseReference cartDBRef = cashierDBRef.child("cart");

        String receiptID = receiptsDBRef.push().getKey();
        String queueID = queueDBRef.push().getKey();

        queueEntry.setReceiptID(receiptID);

        receiptEntry.setId(receiptID);

        receiptsDBRef.child(receiptID).setValue(receiptEntry);
        queueDBRef.child(queueID).setValue(queueEntry);
        cartDBRef.setValue(null);
        Toast.makeText(getApplicationContext(), "Order has been placed!", Toast.LENGTH_SHORT).show();
    }
}