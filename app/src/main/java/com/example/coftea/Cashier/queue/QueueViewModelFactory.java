package com.example.coftea.Cashier.queue;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.coftea.data.OrderStatus;


public class QueueViewModelFactory implements ViewModelProvider.Factory {

    private final OrderStatus orderStatus;
    private final Context context;


    public QueueViewModelFactory(OrderStatus orderStatus, Context context) {
        this.orderStatus = orderStatus;
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QueueViewModel.class)) {
            // Pass the argument to the ViewModel constructor
            return (T) new QueueViewModel(orderStatus, context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
