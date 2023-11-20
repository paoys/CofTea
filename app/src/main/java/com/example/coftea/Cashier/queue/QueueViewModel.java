package com.example.coftea.Cashier.queue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.Cashier.order.QueueEntry;
import com.example.coftea.data.OrderActionStatus;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class QueueViewModel extends ViewModel {

    private MutableLiveData<ArrayList<QueueEntry>> _queueOrderList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<QueueEntry>> queueOrderList;
    private MutableLiveData<QueueEntry> _queueOrderToProcess = new MutableLiveData<>();
    public LiveData<QueueEntry> queueOrderToDone;
    private MutableLiveData<QueueEntry> _queueOrderToCancel = new MutableLiveData<>();
    public LiveData<QueueEntry> queueOrderToCancel;
    private final RealtimeDB<QueueEntry> queueRealtimeDB, receiptsRealtimeDB;
    private OrderStatus orderStatus;
    private ChildEventListener childEventListener;
    public QueueViewModel(OrderStatus orderStatus) {
        queueOrderList = _queueOrderList;
        queueOrderToDone = _queueOrderToProcess;
        queueOrderToCancel = _queueOrderToCancel;
        cartItemList = _cartItemList;
        this.orderStatus = orderStatus;
        queueRealtimeDB = new RealtimeDB<>("cashier/queue");
        receiptsRealtimeDB = new RealtimeDB<>("cashier/receipts");
        setChildEventListener();
        listenUpdate();
    }

    private void listenUpdate(){
        DatabaseReference queueDBRef = queueRealtimeDB.get().getRef();
        Query filterQueueDB = queueDBRef.orderByChild("status").equalTo(orderStatus.toString());
        filterQueueDB.removeEventListener(childEventListener);
        filterQueueDB.addChildEventListener(childEventListener);
    }
    public void changeOrderStatusFilter(OrderStatus status){
        _queueOrderList.setValue(new ArrayList<>());
        this.orderStatus = status;
        listenUpdate();
    }
    private void setChildEventListener(){
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_queueOrderList.getValue() == null) {
                    return;
                }

                QueueEntry item = snapshot.getValue(QueueEntry.class);
                item.setId(snapshot.getKey());
                ArrayList<QueueEntry> list = new ArrayList<>(_queueOrderList.getValue());
                list.add(item);
                _queueOrderList.setValue(list);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_queueOrderList.getValue() == null) {
                    return;
                }

                QueueEntry newItem = snapshot.getValue(QueueEntry.class);
                String itemId = snapshot.getKey();

                ArrayList<QueueEntry> list = new ArrayList<>(_queueOrderList.getValue());

                for (int i = 0; i < list.size(); i++) {
                    QueueEntry existingItem = list.get(i);
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

                ArrayList<QueueEntry> list = new ArrayList<>(_queueOrderList.getValue());

                for (int i = 0; i < list.size(); i++) {
                    QueueEntry existingItem = list.get(i);
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
        };
    }
    public void processQueueOrder(OrderActionStatus actionStatus){
        String orderID = _queueOrderToProcess.getValue().getOrderID();
        switch (actionStatus){
            case TO_DONE:
               Log.e("====1","TEST");
               break;
            case TO_READY:
                Log.e("====2","TEST");
                break;
            case TO_CANCEL:
                Log.e("====3","TEST");
                break;
            default:
                break;
        }
    }

    public void updateQueueOrderStatus(String orderID, String queueID, boolean isOnline){
//        receiptsRealtimeDB.getDatabaseReference().
    }

    public void setQueueOrderToProcess(QueueEntry queueEntry){
        getQueueOrderItemList(queueEntry.getReceiptID());
        _queueOrderToProcess.postValue(queueEntry);
    }
    public void clearQueueOrderToProcess(){
        _cartItemList.setValue(null);
        _queueOrderToProcess.setValue(null);
    }
    public void setQueueOrderToCancel(QueueEntry queueEntry){
        _queueOrderToCancel.postValue(queueEntry);
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

