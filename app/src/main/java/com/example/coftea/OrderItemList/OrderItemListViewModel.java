package com.example.coftea.OrderItemList;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.OrderItem.OrderItemResult;
import com.example.coftea.data.OrderItem;
import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;
import com.example.coftea.utilities.UserProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderItemListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<OrderItem>> _orderItems = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<OrderItem>> orderItems;
    private final RealtimeDB<OrderItemFirebase> realtimeDB;

    public OrderItemListViewModel(String id){
        orderItems = _orderItems;
        realtimeDB = new RealtimeDB<>("order_item/"+id);
        listenUpdate();
    }

    private void listenUpdate(){

        realtimeDB.get().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_orderItems.getValue() == null) {
                    return;
                }

                OrderItemFirebase newItem = snapshot.getValue(OrderItemFirebase.class);
                DatabaseReference ref = realtimeDB.getDB().getReference(newItem.getProduct());
                String itemId = snapshot.getKey();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);

                        Integer qty = newItem.getQuantity();

                        OrderItem item = new OrderItem(itemId, product, qty);
                        ArrayList<OrderItem> list = new ArrayList<>(_orderItems.getValue());
                        list.add(item);
                        _orderItems.setValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_orderItems.getValue() == null) {
                    return;
                }
                OrderItemFirebase newItem = snapshot.getValue(OrderItemFirebase.class);
                String itemId = snapshot.getKey();

                Product product = snapshot.getValue(Product.class);
                Integer qty = Integer.parseInt(newItem.getQuantity().toString());

                OrderItem item = new OrderItem(itemId, product, qty);
                ArrayList<OrderItem> list = new ArrayList<>(_orderItems.getValue());
                for (int i = 0; i < list.size(); i++) {
                    OrderItem existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.set(i, item);
                        _orderItems.setValue(list);
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (_orderItems.getValue() == null) {
                    return;
                }
                String itemId = snapshot.getKey();

                ArrayList<OrderItem> list = new ArrayList<>(_orderItems.getValue());
                for (int i = 0; i < list.size(); i++) {
                    OrderItem existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.remove(i);
                        _orderItems.setValue(list);
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
