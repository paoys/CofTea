package com.example.coftea.Cashier.queue;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.Cashier.order.QueueEntry;
import com.example.coftea.data.OrderActionStatus;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.data.ProductIngredient;
import com.example.coftea.repository.RealtimeDB;
import com.example.coftea.utilities.SMSSender;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueueViewModel extends ViewModel {

    private MutableLiveData<ArrayList<QueueEntry>> _queueOrderList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<ArrayList<QueueEntry>> queueOrderList;
    private MutableLiveData<QueueEntry> _queueOrderToProcess = new MutableLiveData<>();
    public LiveData<QueueEntry> queueOrderToProcess;
    private MutableLiveData<QueueEntry> _queueOrderToCancel = new MutableLiveData<>();
    public LiveData<QueueEntry> queueOrderToCancel;
    public final RealtimeDB<QueueEntry> queueRealtimeDB, receiptsRealtimeDB, orderRealtimeDB;
    private OrderStatus orderStatus;
    private ChildEventListener childEventListener;
    private Context context;
    public QueueViewModel(OrderStatus orderStatus, Context context) {
        queueOrderList = _queueOrderList;
        queueOrderToProcess = _queueOrderToProcess;
        queueOrderToCancel = _queueOrderToCancel;
        cartItemList = _cartItemList;
        queueViewModelProcessResult = _queueViewModelProcessResult;
        this.orderStatus = orderStatus;
        queueRealtimeDB = new RealtimeDB<>("cashier/queue");
        receiptsRealtimeDB = new RealtimeDB<>("cashier/receipts");
        orderRealtimeDB = new RealtimeDB<>("order");
        this.context = context;
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
                if(item.getStatus() != orderStatus) return;
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


    public void setQueueOrderToProcess(QueueEntry queueEntry){
        getQueueOrderItemList(queueEntry.getReceiptID());
        _queueOrderToProcess.setValue(queueEntry);
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
                    Log.e("Order Item", String.valueOf(item.getId()));
                    Log.e("Order Item", String.valueOf(item.getIngredientPath()));
                    cartItems.add(item);
                }
                _cartItemList.setValue(cartItems);
            }
        });
    }

    private MutableLiveData<QueueViewModelProcessResult> _queueViewModelProcessResult = new MutableLiveData<>();
    public LiveData<QueueViewModelProcessResult> queueViewModelProcessResult;

    public void readyQueueOrder(ArrayList<CartItem> cartItems){
        if(_queueOrderToProcess.getValue() == null) return;
        QueueEntry entry = _queueOrderToProcess.getValue();
        Boolean isOnline = entry.getOnlineOrder();

        if(isOnline != null && isOnline){
            Log.e("READY_QUEUE_ORDER", "ONLINE");
            processReadyOnlineQueueOrder(entry, cartItems);
        }
        else{
            Log.e("READY_QUEUE_ORDER", "WALK IN");
            processReadyQueueOrder(entry, cartItems);
        }
    }

    private void processReadyQueueOrder(QueueEntry entry, ArrayList<CartItem> cartItems){
        String queueID = entry.getId();
        String receiptID = entry.getReceiptID();
        RealtimeDB realtimeDB = new RealtimeDB("cashier");
        DatabaseReference cashierDBRef = realtimeDB.getDatabaseReference();
        cashierDBRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.child("queue").child(queueID).child("status").setValue(OrderStatus.READY);
                currentData.child("receipts").child(receiptID).child("status").setValue(OrderStatus.READY);
                return Transaction.success(currentData);
            }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.e("DATABASE ERROR",String.valueOf(error));
                QueueViewModelProcessResult result;
                if (committed && error == null) {
                    SMSSender.sendSMS(context, entry.getCustomerPhone(), generateSMSOrderReady(entry.getCustomerName()));
                    result = new QueueViewModelProcessResult(entry);
                    processIngredients(cartItems);
                    _queueViewModelProcessResult.setValue(result);
                } else {
                    result = new QueueViewModelProcessResult("Process Online Payment Failed: "+String.valueOf(error.getCode()));
                    _queueViewModelProcessResult.setValue(result);
                }
//                clearQueueOrderToProcess();
            }
        });
    }
    private void processReadyOnlineQueueOrder(QueueEntry entry, ArrayList<CartItem> cartItems){
        String queueID = entry.getId();
        String receiptID = entry.getReceiptID();

        RealtimeDB realtimeDB = new RealtimeDB("cashier");
        DatabaseReference cashierDBRef = realtimeDB.getDatabaseReference();
        DatabaseReference orderDBRef = orderRealtimeDB.getDatabaseReference();

        cashierDBRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.child("queue").child(queueID).child("status").setValue(OrderStatus.READY);
                currentData.child("receipts").child(receiptID).child("status").setValue(OrderStatus.READY);
                return Transaction.success(currentData);
            }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.e("DATABASE ERROR", String.valueOf(error));
                QueueViewModelProcessResult result;
                if (committed && error == null) {
                    orderDBRef.child(entry.getCustomerPhone()).child("status").setValue(OrderStatus.READY);
                    SMSSender.sendSMS(context, entry.getCustomerPhone(), generateSMSOrderOnlineReady(entry.getCustomerName()));
                    processIngredients(cartItems);
                    result = new QueueViewModelProcessResult(entry);
                    _queueViewModelProcessResult.setValue(result);
                } else {
                    result = new QueueViewModelProcessResult("Process Online Payment Failed: " + String.valueOf(error.getCode()));
                    _queueViewModelProcessResult.setValue(result);
                }
//                clearQueueOrderToProcess();
            }
        });
    }



    public void finishQueueOrder(){
        if(_queueOrderToProcess.getValue() == null) return;
        QueueEntry entry = _queueOrderToProcess.getValue();
        Boolean isOnline = entry.getOnlineOrder();

        if(isOnline != null && isOnline){
            Log.e("FINISH_QUEUE_ORDER", "ONLINE");
            processFinishOnlineQueueOrder(entry);
        }
        else{
            Log.e("FINISH_QUEUE_ORDER", "WALK IN");
            processFinishQueueOrder(entry);
        }
    }
    private void processFinishQueueOrder(QueueEntry entry){
        String queueID = entry.getId();
        String receiptID = entry.getReceiptID();
        RealtimeDB realtimeDB = new RealtimeDB("cashier");
        DatabaseReference cashierDBRef = realtimeDB.getDatabaseReference();
        cashierDBRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.child("queue").child(queueID).child("status").setValue(OrderStatus.DONE);
                currentData.child("receipts").child(receiptID).child("status").setValue(OrderStatus.DONE);
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.e("DATABASE ERROR",String.valueOf(error));
                QueueViewModelProcessResult result;
                if (committed && error == null) {
                    SMSSender.sendSMS(context, entry.getCustomerPhone(), generateThankYouMessage(entry.getCustomerName()));
                    result = new QueueViewModelProcessResult(entry);
                    _queueViewModelProcessResult.setValue(result);
                } else {
                    result = new QueueViewModelProcessResult("Finish Transaction Failed: "+String.valueOf(error.getCode()));
                    _queueViewModelProcessResult.setValue(result);
                }
//                clearQueueOrderToProcess();
            }
        });
    }
    private void processFinishOnlineQueueOrder(QueueEntry entry){
        String queueID = entry.getId();
        String receiptID = entry.getReceiptID();

        RealtimeDB realtimeDB = new RealtimeDB("cashier");
        DatabaseReference cashierDBRef = realtimeDB.getDatabaseReference();
        DatabaseReference orderDBRef = orderRealtimeDB.getDatabaseReference();

        cashierDBRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.child("queue").child(queueID).child("status").setValue(OrderStatus.DONE);
                currentData.child("receipts").child(receiptID).child("status").setValue(OrderStatus.DONE);
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.e("DATABASE ERROR OL", String.valueOf(error));
                QueueViewModelProcessResult result;
                if (committed && error == null) {
                    orderDBRef.child(entry.getCustomerPhone()).setValue(null);
                    SMSSender.sendSMS(context, entry.getCustomerPhone(), generateThankYouMessage(entry.getCustomerName()));
                    result = new QueueViewModelProcessResult(entry);
                    _queueViewModelProcessResult.setValue(result);
                } else {
                    result = new QueueViewModelProcessResult("Finish Transaction Failed: " + String.valueOf(error.getCode()));
                    _queueViewModelProcessResult.setValue(result);
                }
//                clearQueueOrderToProcess();
            }
        });
    }
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processIngredients(ArrayList<CartItem> cartItems){

        ArrayList<CartItem> _cartItems = cartItems.stream()
                .filter(item -> item.getIngredientPath() != null)
                .collect(Collectors.toCollection(ArrayList::new));


        if (_cartItems.size() == 0) return;

        ArrayList<ProductIngredient> productIngredients = new ArrayList<>();

        final int totalPaths = _cartItems.size();
        final AtomicInteger pathsProcessed = new AtomicInteger(0);

        for (CartItem item : _cartItems) {
            DatabaseReference reference = database.getReference(item.getIngredientPath());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        // Assuming the children are of type "ProductIngredient"
                        ProductIngredient ingredient = childSnapshot.getValue(ProductIngredient.class);
                        ingredient.setProductID(item.getId());
                        Log.e("Ingredient::A", String.valueOf(ingredient.getIngredientKey()));
                        if (ingredient != null) {
                            productIngredients.add(ingredient);
                        }
                    }

                    int currentProcessed = pathsProcessed.incrementAndGet();

                    // Check if all paths have been processed
                    if (currentProcessed == totalPaths) {
                        // Call processAll when all paths are processed
                        processAll(productIngredients, _cartItems);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processAll(ArrayList<ProductIngredient> ingredients, ArrayList<CartItem> cartItems){

        DatabaseReference ingredientsRef = database.getReference("Ingredients");

        ArrayList<ProductIngredient> distinctList = aggregateIngredients(ingredients);
        for (ProductIngredient item : distinctList) {

            ArrayList<CartItem> _cartItems = cartItems.stream()
                    .filter(cartItem -> item.getProductID().equals(cartItem.getId()))
                    .collect(Collectors.toCollection(ArrayList::new));

            int sumOfQuantities = _cartItems.stream().mapToInt(CartItem::getQuantity).sum();

            Log.e("ProIngredient", String.valueOf(item.getName()));
            Log.e("ProIngredient", String.valueOf(item.getAmount()));
            Log.e("ProIngredient", String.valueOf(sumOfQuantities));
            DatabaseReference ingredientQtyRef = ingredientsRef.child(item.getIngredientKey()).child("qty");

            ingredientQtyRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    // If the node does not exist, return success to abort the transaction
                    if (mutableData.getValue() == null) {
                        return Transaction.success(mutableData);
                    }

                    // Decrease the quantity by 1 (you can modify this based on your needs)
                    String currentQty = (String) mutableData.getValue();
                    Double _currentQty = Double.parseDouble(currentQty);
                    String newQty = String.valueOf(_currentQty - (item.getAmount() * sumOfQuantities));

                    mutableData.setValue(newQty);

                    // Return success to commit the transaction
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@NonNull DatabaseError databaseError, boolean committed, @NonNull DataSnapshot dataSnapshot) {
                    if (committed) {
                        // Transaction completed successfully
                    } else {
                        // Transaction failed
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static ArrayList<ProductIngredient> aggregateIngredients(ArrayList<ProductIngredient> ingredients) {

        ArrayList<ProductIngredient> _ingredients = new ArrayList<>();
        int index = 0;

        for(ProductIngredient ingredient : ingredients){

            String ingredientID = ingredient.getIngredientKey();
            boolean ingredientExists = false;
            for (ProductIngredient existingIngredient : _ingredients) {
                if (existingIngredient.getIngredientKey().equals(ingredientID)) {
                    // Ingredient already exists, update its amount
                    ProductIngredient _existingIngredient = existingIngredient;
                    _existingIngredient.setAmount(existingIngredient.getAmount() + ingredient.getAmount());
                    _ingredients.set(index, _existingIngredient);
                    ingredientExists = true;
                    break;
                }
            }

            // If the ingredient doesn't exist, add it to _ingredients
            if (!ingredientExists) {
                _ingredients.add(ingredient);
            }
            index++;
        }

        return _ingredients;
    }

    public void cancelQueueOrder(){
        if(_queueOrderToCancel.getValue() == null) return;
        QueueEntry entry = _queueOrderToCancel.getValue();
        Boolean isOnline = entry.getOnlineOrder();

        if(isOnline != null && isOnline){
            Log.e("CANCEl_QUEUE_ORDER", "ONLINE");
            processCancelQueueOrder(entry);
        }
        else{
            Log.e("CANCEl_QUEUE_ORDER", "WALK IN");
            processCancelOnlineQueueOrder(entry);
        }
    }

    private void processCancelQueueOrder(QueueEntry entry){
        String queueID = entry.getId();
        String receiptID = entry.getReceiptID();
        RealtimeDB realtimeDB = new RealtimeDB("cashier");
        DatabaseReference cashierDBRef = realtimeDB.getDatabaseReference();
        cashierDBRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.child("queue").child(queueID).child("status").setValue(OrderStatus.CANCELLED);
                currentData.child("receipts").child(receiptID).child("status").setValue(OrderStatus.CANCELLED);
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.e("DATABASE ERROR",String.valueOf(error));
                QueueViewModelProcessResult result;
                if (committed && error == null) {
                    result = new QueueViewModelProcessResult(entry);
                    SMSSender.sendSMS(context, entry.getCustomerPhone(), generateSMSOrderCancelled(entry.getCustomerName()));
                    _queueViewModelProcessResult.setValue(result);
                    clearQueueOrderToCancel();
                } else {
                    result = new QueueViewModelProcessResult("Cancel Transaction Failed: "+String.valueOf(error.getCode()));
                    _queueViewModelProcessResult.setValue(result);
                }
            }
        });
    }
    private void processCancelOnlineQueueOrder(QueueEntry entry){
        String queueID = entry.getId();
        String receiptID = entry.getReceiptID();

        RealtimeDB realtimeDB = new RealtimeDB("cashier");
        DatabaseReference cashierDBRef = realtimeDB.getDatabaseReference();
        DatabaseReference orderDBRef = orderRealtimeDB.getDatabaseReference();

        cashierDBRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.child("queue").child(queueID).child("status").setValue(OrderStatus.CANCELLED);
                currentData.child("receipts").child(receiptID).child("status").setValue(OrderStatus.CANCELLED);
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.e("DATABASE ERROR OL", String.valueOf(error));
                QueueViewModelProcessResult result;
                if (committed && error == null) {
                    orderDBRef.child(entry.getCustomerPhone()).setValue(null);
                    SMSSender.sendSMS(context, entry.getCustomerPhone(), generateSMSOrderCancelled(entry.getCustomerName()));
                    result = new QueueViewModelProcessResult(entry);
                    _queueViewModelProcessResult.setValue(result);
                    clearQueueOrderToCancel();
                } else {
                    result = new QueueViewModelProcessResult("Cancel Transaction Failed: " + String.valueOf(error.getCode()));
                    _queueViewModelProcessResult.setValue(result);
                }
            }
        });
    }


    public String generateThankYouMessage(String customerName) {
        return "This is Coftea:\n" +
                "Thank you, " + customerName + ", for choosing Coftea! " +
                "We appreciate your order and look forward to serving you again soon.";
    }

    public String generateSMSOrderCancelled(String customerName){
        return "This is Coftea:\n" +
                "We regret to inform you that your order has been cancelled, " + customerName + ". " +
                "Please contact us for further assistance.";
    }

    public String generateSMSOrderReady(String customerName){
        return "This is Coftea:\n" +
                "Good news, " + customerName + "! Your order is now ready.";
    }
    public String generateSMSOrderOnlineReady(String customerName){
        return "This is Coftea:\n" +
                "Good news, " + customerName + "! Your order is now ready for pickup. " +
                "Please visit us at your earliest convenience to enjoy your order.";
    }
}

