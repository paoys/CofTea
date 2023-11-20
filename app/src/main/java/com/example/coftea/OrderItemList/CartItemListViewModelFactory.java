package com.example.coftea.OrderItemList;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CartItemListViewModelFactory implements ViewModelProvider.Factory {
    private final String user_id;

    public CartItemListViewModelFactory(String user_id) {
        this.user_id= user_id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartItemListViewModel.class)) {
            // Pass the argument to the ViewModel constructor
            return (T) new CartItemListViewModel(user_id);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
