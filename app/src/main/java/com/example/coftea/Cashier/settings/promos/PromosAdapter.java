package com.example.coftea.Cashier.settings.promos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class PromosAdapter extends RecyclerView.Adapter<PromosAdapter.PromoViewHolder> {

    private List<Promo> promoList;
    private DatabaseReference databaseReference; // Firebase database reference

    public PromosAdapter(List<Promo> promoList, DatabaseReference databaseReference) {
        this.promoList = promoList;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        Promo promo = promoList.get(position);

        // Bind promo data to views in the item layout
        holder.textViewAnnouncement.setText(promo.getAnnouncement());

        // Get image URL from the Promo object
        String imageUrl = promo.getImageUrl();

        // Set the image using the URL retrieved from Firebase
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_menu_gallery) // Placeholder image while loading
                    .into(holder.imageViewPromo);
        } else {
            // Handle the case where the image URL is empty or null
            // You may choose to show a placeholder or hide the ImageView
            holder.imageViewPromo.setImageResource(R.drawable.ic_menu_gallery); // Set a placeholder
        }

        // Set OnClickListener for delete button (if available in item layout)
        MaterialButton deleteButton = holder.itemView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(view -> {
            // Display a confirmation dialog before deleting the promo
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Delete Promo")
                    .setMessage("Are you sure you want to delete this promo?")
                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                        // Remove promo from the list
                        promoList.remove(position);
                        notifyItemRemoved(position);

                        // Remove corresponding data from Firebase Database
                        if (promo.getKey() != null) {
                            databaseReference.child(promo.getKey()).removeValue();
                        }

                        // Show a toast message indicating successful deletion
                        Toast.makeText(view.getContext(), "Promo deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAnnouncement;
        ImageView imageViewPromo; // Assuming the image is loaded into an ImageView

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAnnouncement = itemView.findViewById(R.id.textViewAnnouncement);
            imageViewPromo = itemView.findViewById(R.id.imageViewPromo); // Initialize ImageView
        }
    }
}
