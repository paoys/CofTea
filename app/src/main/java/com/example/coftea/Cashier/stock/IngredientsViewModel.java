package com.example.coftea.Cashier.stock;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.Cashier.order.ReceiptEntry;
import com.example.coftea.data.ProductIngredient;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class IngredientsViewModel extends ViewModel {

    private RealtimeDB ingredientsRealtimeDB;
    private MutableLiveData<ArrayList<MainModelIngredients>> _ingredients = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<MainModelIngredients>> ingredients;
    private DatabaseReference ingredientsDBRef;
    private ChildEventListener childEventListener;
    public IngredientsViewModel(){
        ingredientsRealtimeDB = new RealtimeDB<>("Ingredients");
        ingredientsDBRef = ingredientsRealtimeDB.getDatabaseReference();
        ingredients = _ingredients;
        productIngredientToRemove = _productIngredientToRemove;
        setChildListener();
        listen();
    }

    private MutableLiveData<ProductIngredient> _productIngredientToRemove = new MutableLiveData<>();
    public LiveData<ProductIngredient> productIngredientToRemove;

    public void setProductIngredientToRemove(ProductIngredient productIngredient){
        _productIngredientToRemove.setValue(productIngredient);
    }

    public void clearProductIngredientToRemove(){
        _productIngredientToRemove.setValue(null);
    }
    private void listen(){
        ingredientsDBRef.removeEventListener(childEventListener);
        ingredientsDBRef.addChildEventListener(childEventListener);
    }

    private void setChildListener(){
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (_ingredients.getValue() == null) {
                    return;
                }
                MainModelIngredients ingredients = snapshot.getValue(MainModelIngredients.class);
                Log.e("TESTTEST", String.valueOf(ingredients.getId()));
                ArrayList<MainModelIngredients> ingredientsList = _ingredients.getValue();
                if (ingredientsList != null) {
                    ingredientsList.add(ingredients);
                    _ingredients.setValue(ingredientsList);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

}
