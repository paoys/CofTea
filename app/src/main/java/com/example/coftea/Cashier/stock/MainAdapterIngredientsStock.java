package com.example.coftea.Cashier.stock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coftea.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapterIngredientsStock extends FirebaseRecyclerAdapter<MainModelIngredients, MainAdapterIngredientsStock.myViewHolder> {

    private MainModelIngredients originalModel;
    private int originalQuantity; // Variable to store the original quantity

    public MainAdapterIngredientsStock(@NonNull FirebaseRecyclerOptions<MainModelIngredients> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MainModelIngredients model) {
        holder.name.setText(model.getName());
        holder.id.setText(model.getId());
        holder.qty.setText(model.getQty());
        holder.measurement.setText(model.getMeasurement());

        Glide.with(holder.img.getContext())
                .load(model.getTurl())
                .placeholder(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true, 1200)
                        .create();

                View view = dialogPlus.getHolderView();
                EditText name = view.findViewById(R.id.nametxt);
                EditText id = view.findViewById(R.id.id);
                EditText qty = view.findViewById(R.id.qty);
                EditText turl = view.findViewById(R.id.img1);
                EditText measurement = view.findViewById(R.id.measurement);

                Button btnUpdate = view.findViewById(R.id.btnUpdate);

                // Store the original quantity before making changes
                originalQuantity = Integer.parseInt(model.getQty());

                name.setText(model.getName());
                id.setText(model.getId());
                qty.setText(model.getQty());
                turl.setText(model.getTurl());
                measurement.setText(model.getMeasurement());

                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name.getText().toString());
                        map.put("id", id.getText().toString());

                        double newQty = originalQuantity + Double.parseDouble(qty.getText().toString());
                        // Convert the double to a string with proper precision (if needed)
                        String formattedQty = String.valueOf(newQty); // Convert double to string


                        map.put("qty", formattedQty);
                        map.put("turl", turl.getText().toString());
                        map.put("measurement", measurement.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Ingredients")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.name.getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.name.getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });
            }
        });


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted Data can't be undone.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Ingredients")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(holder.name.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {


        CircleImageView img;
        TextView id, name, qty, measurement;

        Button btnEdit, btnDelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = (CircleImageView) itemView.findViewById(R.id.img1);
            name = (TextView) itemView.findViewById(R.id.nametxt);
            id = (TextView) itemView.findViewById(R.id.id);
            qty = (TextView) itemView.findViewById(R.id.qty);
            measurement = (TextView) itemView.findViewById(R.id.measurement);

            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
        }
    }
}