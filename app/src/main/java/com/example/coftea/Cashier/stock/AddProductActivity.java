package com.example.coftea.Cashier.stock;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Cashier.order.ModelOrderProduct;
import com.example.coftea.Order.OrderDialogFragment;
import com.example.coftea.R;
import com.example.coftea.data.ProductIngredient;
import com.example.coftea.databinding.ActivityAddProductBinding;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddProductActivity extends AppCompatActivity implements AddProductIngredientListener{

    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productIDEditText;
    private Button addButton;
    private Button btnProduct;

    private DatabaseReference productsRef;
    private ImageView productImageView;
    private Button addImageButton, addIngredient;
    private RecyclerView rvIngredientList;
    private Uri imageUri; // Store the selected image URI

    // Add a variable to track the ID counter
    private int idCounter = 1;

    private ActivityAddProductBinding binding;

    ProductIngredientViewModel productIngredientViewModel;
    IngredientsViewModel ingredientsViewModel;
    AddIngredientAdapter addIngredientAdapter;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        productIngredients = new ArrayList<>();

        FirebaseApp.initializeApp(this);

        btnProduct = binding.SeeProduct;
        productImageView = binding.productImageView;
        addImageButton = binding.addImageButton;
        addIngredient = binding.btnAddIngredient;
        addIngredient.setEnabled(false);
        rvIngredientList = binding.rvProductIngredients;

        ingredientsViewModel = new IngredientsViewModel();
        addIngredientAdapter = new AddIngredientAdapter(ingredientsViewModel);

        rvIngredientList.setHasFixedSize(true);
        rvIngredientList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvIngredientList.setAdapter(addIngredientAdapter);

        addProductIngredientDialogFragment = new AddProductIngredientDialogFragment();
        addProductIngredientDialogFragment.setIngredientSelectionListener(this);

        addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        btnProduct.setOnClickListener(v -> {
            // Create an Intent to navigate to the SecondActivity
            Intent intent = new Intent(AddProductActivity.this, ManageProductActivityList.class);
            startActivity(intent);
        });

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");

        productNameEditText = binding.productNameEditText;
        productPriceEditText = binding.productPriceEditText;
        productIDEditText = binding.productIDEditText;
        addButton = binding.addButton;

        addButton.setOnClickListener(v -> {
            // Retrieve the product information from the input fields
            String name = productNameEditText.getText().toString();
            String price = productPriceEditText.getText().toString();

            // Check if the ID exists and proceed accordingly
            checkExistingId(name, price);
        });

        addIngredient.setOnClickListener(v -> {
            if (ingredients == null) return;
            AddProductIngredientDialogFragment existingFragment = (AddProductIngredientDialogFragment) getSupportFragmentManager().findFragmentByTag("AddProductIngredientDialog");

            if (existingFragment == null){
                addProductIngredientDialogFragment.show(getSupportFragmentManager(), "AddProductIngredientDialog");
                addProductIngredientDialogFragment.SetIngredientList(ingredients, getApplicationContext());
            }
            else
                existingFragment.getDialog().show();
        });

        listen();
    }
    private AddProductIngredientDialogFragment addProductIngredientDialogFragment;
    private void listen(){
        ingredientsViewModel.ingredients.observe(this, ingredients -> {
            this.ingredients = ingredients;
            if(ingredients == null) return;
            if(ingredients.size() == 0) return;
            addIngredient.setEnabled(true);
        });

        ingredientsViewModel.productIngredientToRemove.observe(this, productIngredient -> {
            Log.e("IngredientToRemove", String.valueOf(productIngredient));
            if(productIngredient == null) return;
            try {
                int indexToRemove = -1;
                for (int i = 0; i < productIngredients.size(); i++) {
                    if (productIngredients.get(i).getId() == productIngredient.getId()) {
                        indexToRemove = i;
                        break;
                    }
                }
                if (indexToRemove != -1) {
                    productIngredients.remove(indexToRemove);
                    addIngredientAdapter.UpdateList(productIngredients);
                }
            }
            catch (Exception e){

            }
        });
    }

    private ArrayList<MainModelIngredients> ingredients = new ArrayList<>();

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

        boolean hasIngredients = productIngredients.size() != 0;

        if(hasIngredients){
            try {
                DatabaseReference productIngredientsRef = database.getReference("products_ingredients").child(id);
                productIngredientsRef.setValue(productIngredients).addOnSuccessListener(unused -> {
                    // Create a Product object with the image URL
                    ModelOrderProduct product = new ModelOrderProduct(price, name, id, imageUrl, "products_ingredients/"+id);
                    // Store the product data in Firebase Realtime Database with the given ID
                    Log.e("Added w/ Ingredients", String.valueOf(product.getIngredientsPath()));
                    productsRef.child(id).setValue(product);

                    // Clear the input fields and reset the image
                    productNameEditText.setText("");
                    productIDEditText.setText("");
                    productPriceEditText.setText("");
                    productImageView.setImageResource(R.drawable.ic_menu_gallery); // Reset image to default
                });
            }
            catch (Exception e){

            }
        }
        else{
            ModelOrderProduct product = new ModelOrderProduct(price, name, id, imageUrl, null);
            // Create a Product object with the image URL
            Log.e("Added w/o Ingredients", String.valueOf(product.getIngredientsPath()));
            // Store the product data in Firebase Realtime Database with the given ID
            productsRef.child(id).setValue(product);

            // Clear the input fields and reset the image
            productNameEditText.setText("");
            productIDEditText.setText("");
            productPriceEditText.setText("");
            productImageView.setImageResource(R.drawable.ic_menu_gallery); // Reset image to default
        }

    }

    private ArrayList<ProductIngredient> productIngredients;
    @Override
    public void onProductIngredientAdded(ProductIngredient productIngredient) {
        try {
            int indexOfMatch = -1;
            for (int i = 0; i < productIngredients.size(); i++) {
                if (Objects.equals(productIngredients.get(i).getIngredientID(), productIngredient.getIngredientID())) {
                    indexOfMatch = i;
                    break;
                }
            }
            if (indexOfMatch != -1)
                productIngredients.set(indexOfMatch,productIngredient);
            else
                productIngredients.add(productIngredient);

            addIngredientAdapter.UpdateList(productIngredients);
        }
        catch (Exception e){

        }
    }
}