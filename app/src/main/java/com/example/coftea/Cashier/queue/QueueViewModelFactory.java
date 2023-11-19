package com.example.coftea.Cashier.queue;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.coftea.data.OrderStatus;


public class QueueViewModelFactory implements ViewModelProvider.Factory {

    private final OrderStatus orderStatus;

    public QueueViewModelFactory(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QueueViewModel.class)) {
            // Pass the argument to the ViewModel constructor
            return (T) new QueueViewModel(orderStatus);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
