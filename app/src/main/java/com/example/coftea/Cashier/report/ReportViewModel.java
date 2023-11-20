package com.example.coftea.Cashier.report;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.Cashier.order.ReceiptEntry;
import com.example.coftea.Cashier.queue.QueueOrder;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.data.Product;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;

import java.util.ArrayList;

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
    public ReportViewModel(){
        labels = _labels;
        barArraylist = _barArraylist;
        realtimeDB = new RealtimeDB<>("cashier/receipts");
        receiptBDRef = realtimeDB.get().getRef();
        receiptEntryList = new ArrayList<>();
        setChildEventListener();
    }

    public void getDailyReport(OrderStatus orderStatus, Long from, Long to){
        receiptEntryList.clone();
        Query filterQueueDB = receiptBDRef.orderByChild("status").equalTo(orderStatus.toString());

        filterQueueDB.removeEventListener(childEventListener);
        filterQueueDB.addChildEventListener(childEventListener);
    }
    public void getWeeklyReport(OrderStatus orderStatus, Long from, Long to){
        receiptEntryList.clone();
        Query filterQueueDB = receiptBDRef.orderByChild("status").equalTo(orderStatus.toString());
        filterQueueDB.removeEventListener(childEventListener);
        filterQueueDB.addChildEventListener(childEventListener);
    }

    public void getMonthlyReport(OrderStatus orderStatus, Long from, Long to){
        receiptEntryList.clone();
        Query filterQueueDB = receiptBDRef.orderByChild("status").equalTo(orderStatus.toString());
        filterQueueDB.removeEventListener(childEventListener);
        filterQueueDB.addChildEventListener(childEventListener);
    }
    public void getQuarterlyReport(OrderStatus orderStatus, Long from, Long to){
        receiptEntryList.clone();
        Query filterQueueDB = receiptBDRef.orderByChild("status").equalTo(orderStatus.toString());
        filterQueueDB.removeEventListener(childEventListener);
        filterQueueDB.addChildEventListener(childEventListener);
    }
    public void getYearlyReport(OrderStatus orderStatus, Long from, Long to){
        receiptEntryList.clone();
        Query filterQueueDB = receiptBDRef.orderByChild("status").equalTo(orderStatus.toString());
        filterQueueDB.removeEventListener(childEventListener);
        filterQueueDB.addChildEventListener(childEventListener);
    }
    private ArrayList<ReceiptEntry> receiptEntryList;
    private void setChildEventListener() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ReceiptEntry item = snapshot.getValue(ReceiptEntry.class);
                item.setId(snapshot.getKey());
                ArrayList<ReceiptEntry> list = new ArrayList<>(receiptEntryList);
                list.add(item);
                receiptEntryList = list;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ReceiptEntry newItem = snapshot.getValue(ReceiptEntry.class);
                String itemId = snapshot.getKey();

                ArrayList<ReceiptEntry> list = new ArrayList<>(receiptEntryList);

                for (int i = 0; i < list.size(); i++) {
                    ReceiptEntry existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.set(i, newItem);
                        return;
                    }
                }
                receiptEntryList = list;
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                String itemId = snapshot.getKey();

                ArrayList<ReceiptEntry> list = new ArrayList<>(receiptEntryList);

                for (int i = 0; i < list.size(); i++) {
                    ReceiptEntry existingItem = list.get(i);
                    if (existingItem != null && existingItem.getId() != null && existingItem.getId().equals(itemId)) {
                        list.remove(i);
                        return;
                    }
                }
                receiptEntryList = list;
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
