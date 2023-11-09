package com.example.coftea.Customer.products;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Objects;

public class ProductsViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Product>> _productList;
    public final LiveData<ArrayList<Product>> productList;
    private final RealtimeDB<Product> realtimeDB;
    public ProductsViewModel() {
        _productList = new MutableLiveData<>(new ArrayList<>());
        productList = _productList;
        realtimeDB = new RealtimeDB<>("products");
        listenUpdate();
    }

    private void listenUpdate(){
        realtimeDB.get().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_productList.getValue() == null) {
                    return;
                }

                Product item = snapshot.getValue(Product.class);
                ArrayList<Product> list = new ArrayList<>(_productList.getValue());
                list.add(item);
                _productList.setValue(list);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_productList.getValue() == null) {
                    return;
                }

                Product newItem = snapshot.getValue(Product.class);
                String itemId = snapshot.getKey();

                ArrayList<Product> list = new ArrayList<>(_productList.getValue());

                for (int i = 0; i < list.size(); i++) {
                    Product existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.set(i, newItem);
                        _productList.setValue(list);
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (_productList.getValue() == null) {
                    return;
                }

                String itemId = snapshot.getKey();

                ArrayList<Product> list = new ArrayList<>(_productList.getValue());

                for (int i = 0; i < list.size(); i++) {
                    Product existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.remove(i);
                        _productList.setValue(list);
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("MOVED", String.valueOf(snapshot.exists()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}
