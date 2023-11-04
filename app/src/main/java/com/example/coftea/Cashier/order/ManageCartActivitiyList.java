package com.example.coftea.Cashier.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManageCartActivitiyList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ManageCartAdapter productAdapter;
    private List<Cart> productList;
    private DatabaseReference productsRef;
    private Button checkoutButton;

    private boolean paymentValidated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_cart_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        productAdapter = new ManageCartAdapter(this, productList);
        recyclerView.setAdapter(productAdapter);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("cart");

        // Retrieve and display products
        retrieveProducts();

        checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentValidated) {
                    // If payment is validated, show "Enter Name and Phone" dialog
                    showNamePhoneInputDialog();
                } else {
                    // If payment is not validated, show "Payment Result" dialog
                    showPaymentResultDialog();
                }
            }
        });
    }

    private void retrieveProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart product = snapshot.getValue(Cart.class);
                    productList.add(product);
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

        if (!productList.isEmpty()) {
            message.append("Payment is valid. Your order has been placed.\n\n");
            message.append("Items in the cart:\n\n");

            for (Cart product : productList) {
                message.append("Product: ").append(product.getName()).append("\n");
                message.append("Price: ").append(product.getPrice()).append("\n");
                message.append("Quantity: ").append(product.getQuantity()).append("\n");

                double itemPrice = Double.parseDouble(product.getPrice());
                int itemQuantity = product.getQuantity();
                double itemTotal = itemPrice * itemQuantity;
                totalPrice += itemTotal; // Accumulate the total price

                message.append("Item Total: ").append(itemTotal).append("\n\n");
            }

            // Display the total price at the end
            message.append("Total Price: ").append(totalPrice).append("\n");

            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    paymentValidated = true;
                    dialog.dismiss();
                    showNamePhoneInputDialog(); // Automatically open the "Enter Phone and Name" dialog
                }
            });

            builder.setNeutralButton("Edit Order", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle editing the order here.
                    // You can implement the desired functionality.
                    dialog.dismiss();
                }
            });
        } else {
            message.append("Payment is invalid. Please check your payment information.");
        }

        builder.setMessage(message.toString());
        builder.create().show();
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

        builder.setPositiveButton("Add to Queue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String id = phoneEditText.getText().toString();
                int queueItemNumber = 0;

                double totalPayment = 0.0;
                Date date = new Date();

                for (Cart product : productList) {
                    double itemPrice = Double.parseDouble(product.getPrice());
                    int itemQuantity = product.getQuantity();
                    double itemTotal = itemPrice * itemQuantity;
                    totalPayment += itemTotal;
                }


                // Validate that name and phone number are not empty
                if (name.isEmpty() || phone.isEmpty()) {
                    // Show an error message if fields are empty
                    showErrorMessage("Name and phone number are required.");
                } else {

                    addToQueue(name, phone, totalPayment);
                    saveReceipt(productList, name, phone, totalPayment, date);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private int queueItemNumber = 0; // Initialize the queue item number

    private void addToQueue(String name, String phone, double totalPayment) {
        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference queueRef = database.getReference("queue");

        // Increment the queue item number
        queueItemNumber++;

        // Create a receipt entry
        QueueEntry queueEntry = new QueueEntry(totalPayment, name, phone);

        // Set the receipt entry in the "queue" database using the same key as the queueItemNumber
        queueRef.child(String.valueOf(queueItemNumber)).setValue(queueEntry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeItemsFromCart();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the failure to save the receipt
                    }
                });
    }


    private void saveReceipt(List<Cart> products, String name, String phone, double totalPayment, Date date) {
        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference receiptsRef = database.getReference("receipts");

        // Create a receipt entry
        ReceiptEntry receiptEntry = new ReceiptEntry(products, totalPayment, name, phone, date);

        // Set the receipt entry in the "receipts" database using the unique key
        String receiptItemId = receiptsRef.push().getKey();
        receiptsRef.child(receiptItemId).setValue(receiptEntry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Receipt saved successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the failure to save the receipt
                    }
                });
    }

    private void removeItemsFromCart() {
        // Remove items from the "cart" reference
        productsRef.removeValue()
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
}