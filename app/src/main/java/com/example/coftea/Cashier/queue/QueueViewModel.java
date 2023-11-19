package com.example.coftea.Cashier.queue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class QueueViewModel extends ViewModel {

    private MutableLiveData<ArrayList<QueueOrder>> _queueOrderList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<QueueOrder>> queueOrderList;
    private MutableLiveData<QueueOrder> _queueOrderToDone = new MutableLiveData<>();
    public LiveData<QueueOrder> queueOrderToDone;
    private MutableLiveData<QueueOrder> _queueOrderToCancel = new MutableLiveData<>();
    public LiveData<QueueOrder> queueOrderToCancel;
    private final RealtimeDB<QueueOrder> realtimeDB;
    private final OrderStatus orderStatus;
    public QueueViewModel(OrderStatus orderStatus) {
        queueOrderList = _queueOrderList;
        queueOrderToDone = _queueOrderToDone;
        queueOrderToCancel = _queueOrderToCancel;
        cartItemList = _cartItemList;
        this.orderStatus = orderStatus;
        realtimeDB = new RealtimeDB<>("cashier/queue");
        listenUpdate();
    }

    private void listenUpdate(){
        DatabaseReference queueDBRef = realtimeDB.get().getRef();
        Log.e("Status", orderStatus.toString());
        Query filterQueueDB = queueDBRef.orderByChild("status").equalTo(orderStatus.toString());
        filterQueueDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_queueOrderList.getValue() == null) {
                    return;
                }

                QueueOrder item = snapshot.getValue(QueueOrder.class);
                item.setId(snapshot.getKey());
                ArrayList<QueueOrder> list = new ArrayList<>(_queueOrderList.getValue());
                list.add(item);
                _queueOrderList.setValue(list);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_queueOrderList.getValue() == null) {
                    return;
                }

                QueueOrder newItem = snapshot.getValue(QueueOrder.class);
                String itemId = snapshot.getKey();

                ArrayList<QueueOrder> list = new ArrayList<>(_queueOrderList.getValue());

                for (int i = 0; i < list.size(); i++) {
                    QueueOrder existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.set(i, newItem);
                        _queueOrderList.setValue(list);
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (_queueOrderList.getValue() == null) {
                    return;
                }

                String itemId = snapshot.getKey();

                ArrayList<QueueOrder> list = new ArrayList<>(_queueOrderList.getValue());

                for (int i = 0; i < list.size(); i++) {
                    QueueOrder existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.remove(i);
                        _queueOrderList.setValue(list);
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

    public void setQueueOrderToDone(QueueOrder queueOrder){
        getQueueOrderItemList(queueOrder.getReceiptID());
        _queueOrderToDone.postValue(queueOrder);
    }
    public void clearQueueOrderToDone(){
        _cartItemList.setValue(null);
        _queueOrderToDone.setValue(null);
    }
    public void setQueueOrderToCancel(QueueOrder queueOrder){
        _queueOrderToCancel.postValue(queueOrder);
    }
    public void clearQueueOrderToCancel(){
        _cartItemList.setValue(null);
        _queueOrderToCancel.setValue(null);
    }

    private MutableLiveData<ArrayList<CartItem>> _cartItemList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<CartItem>> cartItemList ;
    public void getQueueOrderItemList(String receiptID){
        RealtimeDB realtimeDB = new RealtimeDB<>("cashier/receipts/"+receiptID);

        DatabaseReference cartItemsDBRef = realtimeDB.getDatabaseReference().child("cartItems");
        ArrayList<CartItem> cartItems = new ArrayList<>();
        cartItemsDBRef.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isComplete()){
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    CartItem item = dataSnapshot.getValue(CartItem.class);
                    cartItems.add(item);
                }
                _cartItemList.setValue(cartItems);
            }
        });
    }
}

