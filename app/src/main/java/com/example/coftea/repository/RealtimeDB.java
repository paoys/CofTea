package com.example.coftea.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RealtimeDB<T> {
    private DatabaseReference databaseReference;
    public RealtimeDB(String path){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(path);
    }
    public void addObject(T value){
        databaseReference.push().setValue(value);
    }
    public Query get() {
        return databaseReference.orderByKey();
    }

    public DatabaseReference getDatabaseReference(){
        return databaseReference;
    }
}
