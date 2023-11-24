package com.example.coftea.Cashier.stock;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coftea.data.ProductIngredient;
import com.example.coftea.databinding.AddProductIngredientDialogFragmentBinding;

import java.util.ArrayList;
import java.util.UUID;


public class AddProductIngredientDialogFragment extends DialogFragment {

    AddProductIngredientDialogFragmentBinding binding;
    EditText etIngredientAmount;
    Button btnAddIngredientConfirm, btnAddIngredientCancel;
    Spinner sIngredientSelection;

    private ArrayList<MainModelIngredients> ingredients;

    public AddProductIngredientDialogFragment(){
        this.ingredients = new ArrayList<>();
    }

    public void SetIngredientList(ArrayList<MainModelIngredients> ingredients, Context context){
        this.ingredients = ingredients;
    }
    private MainModelIngredients selectedIngredient;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = AddProductIngredientDialogFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnAddIngredientConfirm = binding.btnIngredientToAddConfirm;
        btnAddIngredientCancel = binding.btnIngredientToAddCancel;
        etIngredientAmount = binding.etIngredientToAddAmount;
        sIngredientSelection = binding.sIngredientToAddSelection;

        ArrayAdapter<MainModelIngredients> adapter = new ArrayAdapter<MainModelIngredients>(getActivity(), android.R.layout.simple_spinner_item, ingredients) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(ingredients.get(position).getName());
                return view;
            }
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setText(ingredients.get(position).getName());
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sIngredientSelection.setAdapter(adapter);
        sIngredientSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedIngredient = ingredients.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedIngredient = null;
            }
        });

        btnAddIngredientConfirm.setOnClickListener(view -> AddIngredient());
        btnAddIngredientCancel.setOnClickListener(view -> dismiss());

        return root;
    }

    private void AddIngredient(){
        if(etIngredientAmount.getText().toString().isEmpty()){
            etIngredientAmount.setError("Invalid Amount");
            return;
        }
        if(selectedIngredient == null){
            Toast.makeText(getContext(), "No Ingredient Selected", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            int amount = Integer.parseInt(etIngredientAmount.getText().toString());
            ProductIngredient newProductIngredient = new ProductIngredient(UUID.randomUUID().toString(), selectedIngredient.getId(), selectedIngredient.getName(), Double.parseDouble(String.valueOf(amount)));
            newProductIngredient.setIngredientKey(selectedIngredient.getKey());
            Log.e("Added Ingredient", String.valueOf(newProductIngredient.getIngredientKey()));
            if(callback != null){
                callback.onProductIngredientAdded(newProductIngredient);
                dismiss();
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(), "Adding Ingredient Failed, please try again", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    private AddProductIngredientListener callback;
    public void setIngredientSelectionListener(AddProductIngredientListener listener) {
        this.callback = listener;
    }
}
