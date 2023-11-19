package com.example.coftea.Cashier.stock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coftea.Cashier.order.ModelOrderProduct;
import com.example.coftea.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productIDEditText;
    private Button addButton;
    private Button SeeProduct;

    private DatabaseReference productsRef;
    private ImageView productImageView;
    private Button addImageButton;
    private Uri imageUri; // Store the selected image URI

    // Add a variable to track the ID counter
    private int idCounter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        FirebaseApp.initializeApp(this);

        SeeProduct = findViewById(R.id.SeeProduct);
        productImageView = findViewById(R.id.productImageView);
        addImageButton = findViewById(R.id.addImageButton);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        SeeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the SecondActivity
                Intent intent = new Intent(AddProductActivity.this, ManageProductActivityList.class);
                startActivity(intent);
            }
        });

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");

        productNameEditText = findViewById(R.id.productNameEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        productIDEditText = findViewById(R.id.productIDEditText);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the product information from the input fields
                String name = productNameEditText.getText().toString();
                String price = productPriceEditText.getText().toString();

                // Check if the ID exists and proceed accordingly
                checkExistingId(name, price);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            productImageView.setImageURI(imageUri);
        }
    }

    // In your `checkExistingId` method, update it as follows:
    private void checkExistingId(final String name, final String price) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("products");

        // Query to check if an item with the same ID already exists
        productsRef.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> existingIds = new ArrayList<>();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String existingId = itemSnapshot.child("id").getValue(String.class);
                    existingIds.add(existingId);
                }

                // Find a new unique ID that is not in the database
                while (existingIds.contains(String.valueOf(idCounter))) {
                    idCounter++;
                }

                // Update the EditText with the new ID
                productIDEditText.setText(String.valueOf(idCounter));

                // Proceed with the insertion using the new ID
                final String autoId = String.valueOf(idCounter);
                final String[] imageUrl = {""};
                Double _price = Double.parseDouble(price);
                // Upload the image if an image URI is available
                if (imageUri != null) {
                    // Upload the image to Firebase Storage
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("product_images/" + UUID.randomUUID().toString());

                    storageRef.putFile(imageUri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            storageRef.getDownloadUrl().addOnCompleteListener(uriTask -> {
                                if (uriTask.isSuccessful()) {
                                    imageUrl[0] = uriTask.getResult().toString();
                                }
                                insertProduct(name, _price, autoId, imageUrl[0]);
                            });
                        } else {
                            // Handle the error during image upload
                            // You may want to show an error message to the user
                        }
                    });
                } else {
                    insertProduct(name, _price, autoId, imageUrl[0]);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddProductActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void insertProduct(String name, Double price, String id, String imageUrl) {
        // Create a Product object with the image URL
        ModelOrderProduct product = new ModelOrderProduct(price, name, id, imageUrl);

        // Store the product data in Firebase Realtime Database with the given ID
        productsRef.child(id).setValue(product);

        // Clear the input fields and reset the image
        productNameEditText.setText("");
        productIDEditText.setText("");
        productPriceEditText.setText("");
        productImageView.setImageResource(R.drawable.ic_menu_gallery); // Reset image to default
    }
}