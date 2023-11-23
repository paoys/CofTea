package com.example.coftea.Cashier.report;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.Cashier.order.ReceiptEntry;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ReportViewModel extends ViewModel {
    private String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private String[] weeks = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private String[] quarters = new String[]{"1st Quarter", "2nd Quarter", "3rd Quarter", "4th Quarter"};
    private MutableLiveData<String[]> _labels = new MutableLiveData<>(new String[]{});
    public LiveData<String[]> labels;
    private MutableLiveData<ArrayList> _barArraylist = new MutableLiveData<>(new ArrayList());
    public LiveData<ArrayList> barArraylist;
    private final RealtimeDB<ReceiptEntry> realtimeDB;
    private final DatabaseReference receiptBDRef;
    private ChildEventListener childEventListener;
    private RealtimeDB<Product> productRealtimeDB;
    private MutableLiveData<ArrayList<Product>> _productList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<Product>> productList;
    public ReportViewModel(){
        labels = _labels;
        barArraylist = _barArraylist;
        realtimeDB = new RealtimeDB<>("cashier/receipts");
        productRealtimeDB = new RealtimeDB<>("products");
        receiptBDRef = realtimeDB.get().getRef();
        receiptEntryList = _receiptEntryList;
        productList = _productList;
        setChildEventListener();
        getProductList();
    }

    public void getProductList(){
        DatabaseReference productDBRef = productRealtimeDB.getDatabaseReference();
        productDBRef.get().addOnSuccessListener(dataSnapshot -> {
            if(!dataSnapshot.exists()) return;
            ArrayList<Product> products = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                Product product = snapshot.getValue(Product.class);
                products.add(product);
            }
            _productList.setValue(products);
        });
    }
    private Long dateFrom, dateTo;
    public void getFilteredReport(OrderStatus orderStatus, Long from, Long to){
        _receiptEntryList.setValue(new ArrayList<>());
        Query filterQueueDB = receiptBDRef.orderByChild("status").equalTo(orderStatus.toString());
        filterQueueDB.removeEventListener(childEventListener);
        this.dateFrom = from;
        this.dateTo = to;
        filterQueueDB.addChildEventListener(childEventListener);
    }

    private MutableLiveData<ArrayList<ReceiptEntry>> _receiptEntryList = new MutableLiveData<>(new ArrayList());
    public LiveData<ArrayList<ReceiptEntry>> receiptEntryList;
    private void setChildEventListener() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_receiptEntryList.getValue() == null) {
                    return;
                }
                ReceiptEntry item = snapshot.getValue(ReceiptEntry.class);
                if (item.getCreatedAt() == null) return;
                if (item.getCreatedAt() >= dateFrom && item.getCreatedAt() <= dateTo) {
                    item.setId(snapshot.getKey());
                    ArrayList<ReceiptEntry> list = new ArrayList<>(_receiptEntryList.getValue());
                    list.add(item);
                    _receiptEntryList.setValue(list);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (_receiptEntryList.getValue() == null) {
                    return;
                }

                ReceiptEntry newItem = snapshot.getValue(ReceiptEntry.class);
                String itemId = snapshot.getKey();
                newItem.setId(itemId);

                ArrayList<ReceiptEntry> list = new ArrayList<>(_receiptEntryList.getValue());

                if (newItem.getCreatedAt() >= dateFrom && newItem.getCreatedAt() <= dateTo) {
                    for (int i = 0; i < list.size(); i++) {
                        ReceiptEntry existingItem = list.get(i);
                        if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                            list.set(i, newItem);
                            return;
                        }
                    }
                }

                _receiptEntryList.setValue(list);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (_receiptEntryList.getValue() == null) {
                    return;
                }
                String itemId = snapshot.getKey();
                ArrayList<ReceiptEntry> list = new ArrayList<>(_receiptEntryList.getValue());

                for (int i = 0; i < list.size(); i++) {
                    ReceiptEntry existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.remove(i);
                        return;
                    }
                }
                _receiptEntryList.setValue(list);
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
}
