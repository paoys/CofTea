package com.example.coftea.Cashier.stock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.data.ProductIngredient;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.DatabaseReference;

public class ProductIngredientViewModel extends ViewModel {

    private RealtimeDB realtimeDB;
    public DatabaseReference ingredientsDBRef;

    public ProductIngredientViewModel(String orderID){
        realtimeDB = new RealtimeDB("products_ingredients"+orderID);
        ingredientsDBRef = realtimeDB.getDatabaseReference();
    }

}
