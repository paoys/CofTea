package com.example.coftea.Cashier.settings.promos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.coftea.R;
import com.example.coftea.repository.RealtimeDB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class AddPromoActivity extends AppCompatActivity {

    private EditText editTextAnnouncement;
    private ImageView promoImageView;
    private Uri imageUri;
    private RealtimeDB<Promo> realtimeDB;

    private static final int IMAGE_PICK_REQUEST_CODE = 1000;
    private static final int PERMISSION_REQUEST_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promo);

        // Initialize views and RealtimeDB instance
        editTextAnnouncement = findViewById(R.id.editTextAnnouncement);
        promoImageView = findViewById(R.id.promoImageView);
        Button addImageButton = findViewById(R.id.addImageButton);
        Button addPromoButton = findViewById(R.id.addPromoButton);

        realtimeDB = new RealtimeDB<>("promos");

        // Handle click events
        addImageButton.setOnClickListener(v -> pickImageFromGallery());
        addPromoButton.setOnClickListener(v -> savePromo());
    }

    private void pickImageFromGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE);
        }
    }

    private void savePromo() {
        String announcement = editTextAnnouncement.getText().toString().trim();

        if (!announcement.isEmpty() && imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("promo_images");
            StorageReference imageRef = storageRef.child(Objects.requireNonNull(imageUri.getLastPathSegment()));

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Create a Promo object with a key
                            String key = realtimeDB.getDatabaseReference().push().getKey();
                            Promo promo = new Promo(key, announcement, imageUrl);

                            // Save to Firebase Realtime Database
                            realtimeDB.addObject(promo);

                            // Inform the user
                            Toast.makeText(AddPromoActivity.this, "Promo added successfully", Toast.LENGTH_SHORT).show();

                            // Clear the fields after saving
                            editTextAnnouncement.setText("");
                            promoImageView.setImageResource(R.drawable.ic_menu_gallery);
                            imageUri = null;
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddPromoActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please enter announcement and select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            promoImageView.setImageURI(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
