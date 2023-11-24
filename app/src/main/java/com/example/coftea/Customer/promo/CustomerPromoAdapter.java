package com.example.coftea.Customer.promo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.R;
import com.squareup.picasso.Picasso; // Import Picasso for image loading

import java.util.List;

public class CustomerPromoAdapter extends RecyclerView.Adapter<CustomerPromoAdapter.PromoViewHolder> {

    private List<Promo> promoList;

    public CustomerPromoAdapter(List<Promo> promoList) {
        this.promoList = promoList;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_announcement, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        Promo promo = promoList.get(position);
        holder.bind(promo);
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAnnouncement;
        ImageView imageViewPromo;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find and assign views here
            textViewAnnouncement = itemView.findViewById(R.id.textViewAnnouncement);
            imageViewPromo = itemView.findViewById(R.id.imageViewPromo);
        }

        public void bind(Promo promo) {
            // Bind data to views here
            textViewAnnouncement.setText(promo.getAnnouncement());

            // Load image using Picasso into imageViewPromo
            if (promo.getImageUrl() != null && !promo.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(promo.getImageUrl())
                        //.placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                        //.error(R.drawable.error_image) // Image to display in case of error
                        .into(imageViewPromo);
            } else {
                // Handle the case where the image URL is empty or null
                // You may choose to show a placeholder or hide the ImageView
                //imageViewPromo.setImageResource(R.drawable.placeholder_image); // Set a placeholder
            }
        }
    }
}
