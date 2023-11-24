package com.example.coftea.Cashier.stock;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.R;
import com.example.coftea.data.ProductIngredient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.IngredientViewHolder> {

    private ArrayList<ProductIngredient> productIngredients;
    private IngredientsViewModel ingredientsViewModel;
    public AddIngredientAdapter(){
        this.productIngredients = new ArrayList<>();
    }
    public AddIngredientAdapter(IngredientsViewModel ingredientsViewModel){
        this.productIngredients = new ArrayList<>();
        this.ingredientsViewModel = ingredientsViewModel;
    }
    public void UpdateList(ArrayList<ProductIngredient> list){
        this.productIngredients = list;
        notifyDataSetChanged();
        Log.d("Adapter", "List size: " + productIngredients.size());
    }
    @NonNull
    @Override
    public AddIngredientAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_add_product_ingredient, parent, false);
        return new AddIngredientAdapter.IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddIngredientAdapter.IngredientViewHolder holder, int position) {

        ProductIngredient productIngredient = productIngredients.get(position);
        holder.btnIngredientRemove.setEnabled(true);
        holder.tvIngredientAmount.setText(String.valueOf(productIngredient.getAmount()));
        holder.tvIngredientName.setText(String.valueOf(productIngredient.getName()));
        holder.btnIngredientRemove.setOnClickListener(view -> {
            ingredientsViewModel.setProductIngredientToRemove(productIngredient);
            holder.btnIngredientRemove.setEnabled(false);
        });
    }

    @Override
    public int getItemCount() {
        return productIngredients.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName, tvIngredientAmount;
        Button btnIngredientRemove;

        IngredientViewHolder(View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvIngredientAmount = itemView.findViewById(R.id.tvIngredientAmount);
            btnIngredientRemove = itemView.findViewById(R.id.btnIngredientRemove);
        }
    }
}
