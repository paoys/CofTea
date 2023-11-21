package com.example.coftea.OrderItemList;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class CartItemListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<CartItem>> _cartItems = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<CartItem>> cartItems;

    public RealtimeDB<CartItem> getRealtimeDB() {
        return realtimeDB;
    }
    private final RealtimeDB<CartItem> realtimeDB;

    public CartItemListViewModel(String id){
        cartItems = _cartItems;
        realtimeDB = new RealtimeDB<>("order/"+id+"/items");
        listenUpdate();
    }

    private void listenUpdate(){

        realtimeDB.get().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_cartItems.getValue() == null) {
                    return;
                }

                CartItem item = snapshot.getValue(CartItem.class);
                item.setKey(snapshot.getKey());
                ArrayList<CartItem> list = new ArrayList<>(_cartItems.getValue());
                list.add(item);
                _cartItems.setValue(list);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_cartItems.getValue() == null) {
                    return;
                }
                CartItem item = snapshot.getValue(CartItem.class);
                item.setKey(snapshot.getKey());

                ArrayList<CartItem> list = new ArrayList<>(_cartItems.getValue());
                for (int i = 0; i < list.size(); i++) {
                    CartItem existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(item.getId())) {
                        list.set(i, item);
                        _cartItems.setValue(list);
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (_cartItems.getValue() == null) {
                    return;
                }
                CartItem item = snapshot.getValue(CartItem.class);
                item.setId(item.getId());

                ArrayList<CartItem> list = new ArrayList<>(_cartItems.getValue());
                for (int i = 0; i < list.size(); i++) {
                    CartItem existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(item.getId())) {
                        list.remove(i);
                        _cartItems.setValue(list);
                        break;
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
